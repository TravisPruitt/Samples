package com.disney.xband.ac.lib.auth;

import com.disney.xband.ac.lib.IAuthRequestWrapper;
import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.ac.lib.XbSecureToken;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/28/12
 * Time: 11:43 AM
 */
/**
 * The local authentication provider first tries to use Basic HTTP authentication. If it fails back to X.509 client
 * authentication. The TLS handshake itself is handled by the web container. The authentication provider just makes
 * sure the HTTP request contains a client certificate.
 */
public class LocalAuthService extends BaseAuthService {
    protected String tokenServiceUrl;

    @Override
    public void init(final HashMap<String, String> props) {
        super.init(props);

        this.tokenServiceUrl = this.getTokenServiceUrlForXbrc(props);
    }

    @Override
    public XbSecureToken authenticate(IAuthRequestWrapper request) {
        XbSecureToken token = this.authenticateHttpBasic(request);

        if(token == null) {
            token = this.authenticateX509(request);
        }

        if(token == null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Local authentication failed for request to " + request.getRequestURL().toString());
            }
        }

        return token;
    }

    private XbSecureToken authenticateX509(IAuthRequestWrapper request) {
        final X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if((certs == null) || (certs.length == 0)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(
                            "X.509 authentication failed for request to ( " +
                                    request.getRequestURL().toString() +
                                    "). No client certificate found. Make sure client requests are made to the Tomcat TLS port that requires client auth."
                    );
                }

            return null;
        }

        final XbSecureToken token = new XbSecureToken();
        token.setLogonName(certs[0].getSubjectDN().getName());
        token.setDisplayName(certs[0].getSubjectDN().getName());
        final List<String> roles = new ArrayList<String>();
        roles.add(this.props.get(PkConstants.NAME_PROP_AC_SERVICE_ACCT_ROLE));
        token.setRoles(roles);
        token.setAuthType(XbSecureToken.AuthType.X509_CERT);
        token.setAuthTime(System.currentTimeMillis());
        token.setAuthenticated(true);
        UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("X.509 authentication succeeded for request to (" + request.getRequestURL().toString() + ") on behalf of user " + token.getLogonName());
        }

        return token;
    }

    protected XbSecureToken authenticateHttpBasic(IAuthRequestWrapper request) {
        final String basicAuthPasswd = this.props.get(PkConstants.NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD);
        String auth = request.getHeader("Authorization");

        if (XbUtils.isEmpty(auth) || (auth.length() < 6)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }

        auth = auth.substring(6);

        if (XbUtils.isEmpty(auth)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }

        try {
            final XbSecureToken token = XbSecureToken.decode(auth);

            if (token == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("HTTP Basic: failed to deserialize xConnect token");
                }

                return null;
            }

            if(XbUtils.isEmpty(basicAuthPasswd)) {
                token.setAuthenticated(true);
                return token;
            }

            if (XbUtils.isEmpty(token.getAuthData()) || !basicAuthPasswd.equals(token.getAuthData())) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + ") due to password mismatch");
                }

                return null;
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication succeeded for request to (" + request.getRequestURL().toString() + ") on behalf of user " + token.getLogonName());
            }

            token.setAuthenticated(true);
            UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
            return token;
        }
        catch (Exception e) {
            try {
                final String creds = new String(Base64.decodeBase64(auth));
                final int ind = creds.indexOf(":");
                final String role = creds.substring(0, ind);
                final String pwd = creds.substring(ind + 1);

                if(basicAuthPasswd.equals(pwd)) {
                    final XbSecureToken token = new XbSecureToken();
                    token.setLogonName(role);
                    token.setDisplayName(role);
                    final List<String> roles = new ArrayList<String>();
                    roles.add(role);
                    token.setRoles(roles);
                    token.setAuthType(XbSecureToken.AuthType.HTTP_BASIC);
                    token.setAuthTime(System.currentTimeMillis());
                    token.setAuthenticated(true);

                    return token;
                }

                if(!XbUtils.isEmpty(role)) {
                    final XbSecureToken token = this.getToken(role, pwd);

                    if(token != null) {
                        UserContext.instance.set(new UserContext(token.getDisplayName(), token.getInitSid(), null, token, request.getClientAddress()));
                        return token;
                    }
                }
            }
            catch(Exception e2) {
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP Basic authentication failed for request to (" + request.getRequestURL().toString() + "). Authorization header: " + auth);
            }

            return null;
        }
    }

    /**
     * Authenticate against remote token service.
     *
     * @param user User
     * @param pass Password
     * @return Secure Token or null if authentication failed.
     */
    protected XbSecureToken getToken(String user, String pass) {
        final String url = this.tokenServiceUrl;
        final String rToken = LocalAuthService.sendGet(url, user, pass);

        if(rToken != null) {
            try {
                final JAXBContext context = JAXBContext.newInstance(XbSecureToken.class);
                final Unmarshaller m = context.createUnmarshaller();
                return (XbSecureToken) m.unmarshal(new StringReader(rToken));
            }
            catch (Exception ignore) {
            }
        }

        return null;
    }

    protected static String getTokenServiceUrlForXbrc(final HashMap<String, String> props) {
        final String proto = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE)) ? "https://" : "http://";

        final String server;

        if(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null) {
            server = "localhost";
            props.put(PkConstants.NAME_PROP_AC_LOGON_APPNAME, "UI");
            props.put(PkConstants.NAME_PROP_AC_LOGON_PORT, "8090");
        }
        else {
            server = props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER);
        }

        int port = 8080;

        if(server != null) {
            if(props.get(PkConstants.NAME_PROP_AC_LOGON_PORT) != null) {
                port = Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_PORT));
            }
        }
        else {
            if(props.get(PkConstants.NAME_PROP_AC_LOGON_PORT) != null) {
                port = Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_PORT));
            }
            else {
                port = 8090;
            }
        }

        final String app = props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME) != null ?
                props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME) : "UI";

        return proto + server + ":" + port + "/" + app + "/login/gettoken?appclass=" + AuditEvent.AppClass.XBRC.toString();
    }

    private static String sendGet(String sURL, String user, String pass)
    {

        InputStreamReader is = null;
        BufferedReader br = null;

        try
        {
            final URL url = new URL(sURL);

            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            httpCon.setConnectTimeout(5000);
            httpCon.setReadTimeout(5000);
            httpCon.setRequestProperty("Accept", "application/xml");
            httpCon.setRequestProperty("Authorization", XbConnection.getAuthorizationString(user, pass));
            int n = httpCon.getResponseCode();

            if (n != 200) {
                return null;
            }

            is = new InputStreamReader(httpCon.getInputStream());
            br = new BufferedReader(is);
            final StringBuilder sb = new StringBuilder();

            while(true)
            {
                final String sLine = br.readLine();
                if (sLine==null)
                {
                    return sb.toString();
                }
                sb.append(sLine);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }
}
