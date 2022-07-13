package com.disney.xband.ac.lib.client;

import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 7/18/12
 * Time: 12:17 PM
 */
public class XbConnection {

    private static final boolean DEBUG_TLS = false;

    private static Logger logger;
    private static final int CONNECTION_TO = 10000;
    private static String serviceRoleName;
    private static String basicAuthPasswd;
    private static boolean isUseClientCert;

    public synchronized static void init(HashMap<String, String> props) throws Exception {
        //assert(props != null);

        if(XbConnection.logger != null) {
            return;
        }

        if(props == null) {
            return;
        }

        XbConnection.logger = Logger.getLogger(XbConnection.class);
        XbConnection.serviceRoleName = props.get(PkConstants.NAME_PROP_AC_SERVICE_ACCT_ROLE);
        XbConnection.basicAuthPasswd = props.get(PkConstants.NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD);
        XbConnection.isUseClientCert = Boolean.valueOf(props.get(PkConstants.NAME_PROP_AC_KS_USE_CLIENT_CERT));

        // Host-Cert map indicates what client certificates are used for what hosts
        final HashMap<String, String> aliasMap = XbConnection.createHostCertMap(props.get(PkConstants.NAME_PROP_AC_HOST_CERT_MAP));
        final boolean isSecure = props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE).toLowerCase().equals("true") ? true : false;

        if(XbConnection.isUseClientCert || isSecure) {
            /*
            System.setProperty("javax.net.ssl.keyStore", XbConnection.keystorePath);
            System.setProperty("javax.net.ssl.keyStorePassword", XbConnection.keystorePasswd);
            System.setProperty("javax.net.ssl.trustStore", XbConnection.truststorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", XbConnection.truststorePasswd);
            */

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(
                    new X509KeyManager[] {
                            new XbX509KeyManager(
                                    props.get(PkConstants.NAME_PROP_AC_KEYSTORE_PATH),
                                    props.get(PkConstants.NAME_PROP_AC_KEYSTORE_PASSWD), aliasMap
                            )
                    },
                    new X509TrustManager[] {
                            new XbX509TrustManager(
                                    props.get(PkConstants.NAME_PROP_AC_TRUSTSTORE_PATH),
                                    props.get(PkConstants.NAME_PROP_AC_TRUSTSTORE_PASSWD)
                            )
                    },
                    null
            );

            HttpsURLConnection.setDefaultSSLSocketFactory(new MySocketFactory(context.getSocketFactory()));
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new XbHostnameVerifier(
                            props.get(PkConstants.NAME_PROP_AC_TRUSTED_HOSTS_PATTERN),
                            HttpsURLConnection.getDefaultHostnameVerifier()
                    )
            );
        }
    }

    /**
     * Open an HTTP connection on behalf of a service account.
     *
     * @param url Destination
     * @return  HTTP connection object
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static HttpURLConnection getConnection(URL url) throws IOException, IllegalArgumentException {
        assert(url != null);

        if (url.getProtocol().equals("https")) {
            return getHttpsConnection(url, null);
        }
        else {
            return getHttpConnection(url, null);
        }
    }

    /**
     * Open an HTTP connection on behalf of a previously authenticated user.
     *
     * @param url Destination
     * @param token xConnect secure token obtained from an authenticated HttpServletRequest object
     * @return  HTTP connection object
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static HttpURLConnection getImpersonatedConnection(URL url, XbSecureToken token) throws IOException, IllegalArgumentException {
        assert(url != null);

        if (url.getProtocol().equals("https")) {
            return getHttpsConnection(url, token);
        }
        else {
            return getHttpConnection(url, token);
        }
    }

    public static String getAuthorizationString(String user, String pass) {
        return "Basic " + Base64.encodeBase64String((user + ":" + pass).getBytes());
    }

    public static String getAuthorizationString() {
        //XbSecureToken token = (XbSecureToken) request.getSession().getAttribute(PkConstants.NAME_ATTR_AC_SECURE_TOKEN);
        XbSecureToken token = null;
        final UserContext uc = (UserContext) UserContext.instance.get();

        if(uc != null) {
            token = (XbSecureToken) uc.getToken();
        }

        if (token == null) {
            token = new XbSecureToken();
            token.setLogonName(serviceRoleName);
            token.setDisplayName(serviceRoleName);
            final List<String> roles = new ArrayList<String>();
            roles.add(serviceRoleName);
            token.setRoles(roles);
            token.setAuthType(XbSecureToken.AuthType.HTTP_BASIC);
            token.setAuthTime(System.currentTimeMillis());
        }

        token.setAuthData(XbConnection.basicAuthPasswd);
        // System.out.println("Authorization: Basic " + XbSecureToken.encode(token));
        return "Basic " + XbSecureToken.encode(token);
    }

    private static HttpURLConnection getHttpConnection(URL url, XbSecureToken token) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        XbConnection.prepareConnection(conn, token);

        return conn;
    }

    private static HttpURLConnection getHttpsConnection(URL url, XbSecureToken token) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        XbConnection.prepareConnection(conn, token);

        return conn;
    }

    private static void prepareConnection(HttpURLConnection conn, XbSecureToken token) {
        conn.setConnectTimeout(XbConnection.CONNECTION_TO);

        if (token == null) {
            token = new XbSecureToken();
            token.setLogonName(serviceRoleName);
            token.setDisplayName(serviceRoleName);
            final List<String> roles = new ArrayList<String>();
            roles.add(serviceRoleName);
            token.setRoles(roles);
            token.setAuthType(XbSecureToken.AuthType.HTTP_BASIC);
            token.setAuthTime(System.currentTimeMillis());
        }

        token.setAuthData(XbConnection.basicAuthPasswd);
        // System.out.println("Authorization: Basic " + XbSecureToken.encode(token));
        conn.setRequestProperty("Authorization", "Basic " + XbSecureToken.encode(token));
    }

    private static HashMap<String, String> createHostCertMap(String hostCertMap) {
        final HashMap<String, String> aliasMap = new HashMap<String, String>(8);

        if(!XbUtils.isEmpty(hostCertMap)) {
            final StringTokenizer st = new StringTokenizer(hostCertMap, ", ", false);

            while(st.hasMoreTokens()) {
                final String pair = st.nextToken();
                final int ind = pair.indexOf(":");

                if(ind > 0) {
                    aliasMap.put(pair.substring(0, ind).toLowerCase(), pair.substring(ind + 1));
                }
            }
        }

        return aliasMap;
    }

    private static class XbHostnameVerifier implements HostnameVerifier {
        private final String trustedHostsPattern;
        private HostnameVerifier defaultVerifier;

        public XbHostnameVerifier(String trustedHostsPattern, HostnameVerifier defaultVerifier) {
            this.defaultVerifier = defaultVerifier;
            this.trustedHostsPattern = (trustedHostsPattern == null ? "" : trustedHostsPattern.trim()).toLowerCase();
        }

        public boolean verify(String hostname, SSLSession session) {
            boolean res = false;

            if (!XbUtils.isEmpty(hostname)) {

                if (XbUtils.isEmpty(this.trustedHostsPattern) || !this.trustedHostsPattern.startsWith("*")) {
                    res = this.defaultVerifier.verify(hostname, session);
                }
                else {
                    final String suffix = this.trustedHostsPattern.substring(1);

                    if (hostname.endsWith(suffix.toLowerCase())) {
                       res = true;
                    }
                }
            }

            if(!res) {
                XbConnection.logger.warn("TLS host name verification failed for host " + hostname);
            }

            return res;
        }
    }

    private static class XbX509KeyManager implements X509KeyManager {
        private X509KeyManager sunKeyManager;
        private HashMap<String, String> aliasForHost;

        XbX509KeyManager(String keystorePath, String keystorePasswd, HashMap<String, String> aliasForHost) throws Exception {
            this.aliasForHost = aliasForHost;

            final KeyStore ks = KeyStore.getInstance("JKS");
            final char[] pw =  keystorePasswd == null ? new char[] {} : keystorePasswd.toCharArray();

            FileInputStream fis = null;

            try {
                final String validatedPath = InputValidator.validateFilePath(keystorePath); // validatedPath: Path validation
                fis = new FileInputStream(validatedPath);
                ks.load(fis, pw);
            }
            finally {
                if(fis != null) {
                    try {
                        fis.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            }

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            kmf.init(ks, pw);
            final KeyManager kms[] = kmf.getKeyManagers();

            for (int i = 0; i < kms.length; i++) {
                if (kms[i] instanceof X509KeyManager) {
                    this.sunKeyManager = (X509KeyManager) kms[i];
                }
            }

            if(this.sunKeyManager == null) {
                throw new Exception("Failed to load client certificates from file " + keystorePath);
            }
        }

        @Override
        public String[] getClientAliases(String s, Principal[] principals) {
            return this.sunKeyManager.getClientAliases(s, principals);
        }

        @Override
        public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
            final SocketAddress socketAddress = socket.getRemoteSocketAddress();
            final String hostName = ((InetSocketAddress) socketAddress).getHostName().toLowerCase();

            String alias = null;
            if (this.aliasForHost.containsKey(hostName)) {
                return this.aliasForHost.get(hostName);
            }
            else {
                if (this.aliasForHost.containsKey("*")) {
                    return this.aliasForHost.get("*");
                }
                else {
                    return this.sunKeyManager.chooseClientAlias(strings, principals, socket);
                }
            }
        }

        @Override
        public String[] getServerAliases(String s, Principal[] principals) {
            return this.sunKeyManager.getServerAliases(s, principals);
        }

        @Override
        public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
            return this.sunKeyManager.chooseServerAlias(s, principals, socket);
        }

        @Override
        public X509Certificate[] getCertificateChain(String s) {
            final X509Certificate[] chain = this.sunKeyManager.getCertificateChain(s);
            return chain;
        }

        @Override
        public PrivateKey getPrivateKey(String s) {
            final PrivateKey key = this.sunKeyManager.getPrivateKey(s);
            return key;
        }
    }

    /**
     }
     * This class is currently not used.
     */
    private static class XbX509TrustManager implements X509TrustManager {

        private X509TrustManager sunTrustManager;
        private List<X509Certificate[]> trustedCerts;

        XbX509TrustManager(String truststorePath, String truststorePasswd) throws Exception {
            final KeyStore ks = KeyStore.getInstance("JKS");

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(InputValidator.validateFilePath(truststorePath));
                ks.load(fis, truststorePasswd == null ? new char[] {} : truststorePasswd.toCharArray());
            }
            finally {
                if(fis != null) {
                    try {
                        fis.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            }

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(ks);
            final TrustManager tms[] = tmf.getTrustManagers();

            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    this.sunTrustManager = (X509TrustManager) tms[i];
                }
            }

            if(this.sunTrustManager == null) {
                throw new Exception("Failed to load trusted certificates from file " + truststorePath);
            }

            final Enumeration<String> aliases = ks.aliases();
            this.trustedCerts = new ArrayList<X509Certificate[]>(8);

            while(aliases.hasMoreElements()) {
                final Certificate[] certs = new Certificate[] {ks.getCertificate(aliases.nextElement())};
                //final Certificate[] certs = ks.getCertificateChain(aliases.nextElement());
                final X509Certificate[] x509Certs = new X509Certificate[certs.length];

                for(int i = 0; i < certs.length; ++i) {
                    x509Certs[i] = (X509Certificate) certs[i];
                }

                this.trustedCerts.add(x509Certs);
            }
        }

        public void
        checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
            this.sunTrustManager.checkClientTrusted(chain, authType);
        }

        public void
        checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
            try {
                this.sunTrustManager.checkServerTrusted(chain, authType);
            }
            catch (CertificateException e) {
                if(!isChainTrusted(chain)) {
                    throw e;
                }
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            final Iterator<X509Certificate[]> it = this.trustedCerts.iterator();
            final X509Certificate[] certs = new X509Certificate[this.trustedCerts.size()];
            int i = 0;

            while(it.hasNext()) {
                certs[i++] = it.next()[0];
            }

            return certs;
        }

        private boolean isChainTrusted(X509Certificate[] chain) {
            for(int i = 0; i < chain.length; ++i) {
                final Iterator<X509Certificate[]> it = this.trustedCerts.iterator();

                if(new String(chain[i].getSignature()).equals(new String(it.next()[0].getSignature()))) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class MySocketFactory extends SSLSocketFactory {
        private SSLSocketFactory sf;

        public MySocketFactory(SSLSocketFactory sf) {
            this.sf = sf;
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
            return this.sf.createSocket(s, i);
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
            return this.sf.createSocket(s, i, inetAddress, i1);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return this.sf.createSocket(inetAddress, i);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
            return this.sf.createSocket(inetAddress, i, inetAddress1, i1);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return this.sf.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return this.sf.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
            if(XbConnection.DEBUG_TLS == true) {
                return new DebugSocket((SSLSocket) this.sf.createSocket(socket, s, i, b));
            }
            else {
                return this.sf.createSocket(socket, s, i, b);
            }
        }
    }
}
