package com.disney.xband.ac.lib;

import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.security.InputValidator;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/19/12
 * Time: 2:38 PM
 */
public class XbUtils {
    private static final String DEV_PROPS_PATH_PREFIX = "/usr/share/disney.xband";
    private static final String DEV_PROPS_FILE = "config.properties";

    private static final ThreadLocal<String> appPathComp = new ThreadLocal<String> () {
        @Override protected String initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<String> ipPathComp = new ThreadLocal<String> () {
        @Override protected String initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<String> ipPortComp = new ThreadLocal<String> () {
        @Override protected String initialValue() {
            return null;
        }
    };

    public static String getAppPathComp() {
        return  XbUtils.appPathComp.get();
    }

    public static String getIpPathComp() {
        return  XbUtils.ipPathComp.get();
    }

    public static String getPortPathComp() {
        return  XbUtils.ipPortComp.get();
    }

    public static void setAuthServerUrlComps(final String url) {
        assert(url != null);

        XbUtils.appPathComp.set(XbUtils.getAppIdFromUrl(url));
        XbUtils.ipPathComp.set(XbUtils.getIpFromUrl(url));
        XbUtils.ipPortComp.set(XbUtils.getPortFromUrl(url));
    }

    public static boolean isEmpty(String str) {
        if((str == null) || str.isEmpty()) {
            return true;
        }

        return false;
    }

    public static HashMap<String, String> propsToHashMap(final Properties props) {
        final HashMap<String, String> prop = new HashMap<String, String>();

        for(String name : props.stringPropertyNames()) {
            prop.put(name, (String) props.get(name));
        }

        return prop;
    }

    public static void loadProperties(Properties props, String appId, Logger logger) {
        final HashMap<String, String> nProps = XbUtils.propsToHashMap(props);

        XbUtils.loadProperties(nProps, appId, logger);

        for(String key : nProps.keySet()) {
            props.put(key, nProps.get(key));
        }
    }

    public static void loadProperties(HashMap<String, String> props, String appId, Logger logger) {
        boolean propsLocationRedefined = false;
        String propsFileName = props.containsKey(PkConstants.NAME_PROP_AC_PROP_FILE_PATH) ?
                InputValidator.validateFilePath(props.get(PkConstants.NAME_PROP_AC_PROP_FILE_PATH)) : null;

        if(propsFileName != null) {
            propsLocationRedefined = true;
            FileInputStream is = null;

            try {
                final Properties props2 = new Properties();
                is = new FileInputStream(InputValidator.validateFilePath(propsFileName));
                props2.load(is);
                final Enumeration ep = props2.propertyNames();

                while(ep.hasMoreElements()) {
                    final String pName = (String) ep.nextElement();
                    props.put(pName, props2.getProperty(pName));
                }
            }
            catch(Exception e) {
                logger.fatal("Failed to read the properties file [" + propsFileName + "]: " + e.toString());
                throw new RuntimeException();
            }
            finally {
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            }
        }

        if(!propsLocationRedefined) {
            final NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
            final String sPropFile = System.getProperty("environment.properties");

            if (sPropFile != null)
            {
                logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
                decoder.setPropertiesPath(sPropFile);
            }

            final String sJasyptPropFile = System.getProperty("jasypt.properties");
            if (sJasyptPropFile != null)
            {
                logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
                decoder.setJasyptPropertiesPath(sJasyptPropFile);
            }
                                    
            try
            {
            	decoder.initialize();
                Properties prop = decoder.read();
                for (Map.Entry<Object, Object> entry : prop.entrySet())
                {
                    props.put((String)entry.getKey(), (String)entry.getValue());
                }
            }
            catch (Exception e)
            {
                logger.fatal("Failed to read the properties file [" + decoder.getPropertiesPath() + "]: " + e.toString());
                throw new RuntimeException();
            }

            // Try to override the loaded properties
            propsFileName = XbUtils.DEV_PROPS_PATH_PREFIX + "/" + appId + "/" + XbUtils.DEV_PROPS_FILE;
            FileInputStream is = null;

            try {
                final Properties props2 = new Properties();
                is = new FileInputStream(InputValidator.validateFilePath(propsFileName));
                props2.load(is);
                final Enumeration ep = props2.propertyNames();

                while(ep.hasMoreElements()) {
                    final String pName = (String) ep.nextElement();
                    props.put(pName, props2.getProperty(pName));
                }

                logger.info("Production properties have been overridden by dev\'s configuration file [" + propsFileName + "]");
            }
            catch(Exception e) {
                logger.info("Production properties have not been overridden by dev\'s configuration file [" + propsFileName + "]");
            }
            finally {
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            }
        }

        XbUtils.populatePropDefaults(props);
    }

    public static void populatePropDefaults(HashMap<String, String> props) {

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOGON_PATH)) {
            props.put(PkConstants.NAME_PROP_AC_LOGON_PATH, PkConstants.DEFAULT_AC_LOGON_PATH);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_HELLO_PATH)) {
            props.put(PkConstants.NAME_PROP_AC_HELLO_PATH, PkConstants.DEFAULT_AC_HELLO_PATH);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOGON_APPNAME)) {
            props.put(PkConstants.NAME_PROP_AC_LOGON_APPNAME, PkConstants.DEFAULT_AC_LOGON_APPNAME);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOGOUT_PATH)) {
            props.put(PkConstants.NAME_PROP_AC_LOGOUT_PATH, PkConstants.DEFAULT_AC_LOGOUT_PATH);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE)) {
            props.put(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE, PkConstants.DEFAULT_AC_LOGON_IS_SECURE);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOGON_PORT)) {
            if(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE).toLowerCase().equals("true")) {
                props.put(PkConstants.NAME_PROP_AC_LOGON_PORT, PkConstants.DEFAULT_AC_LOGON_SECURE_PORT);
            }
            else {
                props.put(PkConstants.NAME_PROP_AC_LOGON_PORT, PkConstants.DEFAULT_AC_LOGON_PORT);
            }
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_AUTH_ATTEMPTS_MAX)) {
            props.put(PkConstants.NAME_PROP_AC_AUTH_ATTEMPTS_MAX, PkConstants.DEFAULT_AC_AUTH_ATTEMPTS_MAX);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_AUTH_SERVICE_CLASS)) {
            props.put(PkConstants.NAME_PROP_AC_AUTH_SERVICE_CLASS, PkConstants.DEFAULT_AC_AUTH_SERVICE_CLASS);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_LOCAL_AUTH_SERVICE_CLASS)) {
            props.put(PkConstants.NAME_PROP_AC_LOCAL_AUTH_SERVICE_CLASS, PkConstants.DEFAULT_AC_LOCAL_AUTH_SERVICE_CLASS);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_REAUTH_SECS)) {
            props.put(PkConstants.NAME_PROP_AC_REAUTH_SECS, PkConstants.DEFAULT_AC_REAUTH_SECS);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_DISABLE_FILTER)) {
            props.put(PkConstants.NAME_PROP_AC_DISABLE_FILTER, PkConstants.DEFAULT_AC_DISABLE_FILTER);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_KS_ENABLE_AUTHORIZATION)) {
            props.put(PkConstants.NAME_PROP_AC_KS_ENABLE_AUTHORIZATION, PkConstants.DEFAULT_AC_KS_ENABLE_AUTHORIZATION);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_KS_REAUTH_SECS)) {
            props.put(PkConstants.NAME_PROP_AC_KS_REAUTH_SECS, PkConstants.DEFAULT_AC_KS_REAUTH_SECS);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_KS_USE_CLIENT_CERT)) {
            props.put(PkConstants.NAME_PROP_AC_KS_USE_CLIENT_CERT, PkConstants.DEFAULT_AC_KS_USE_CLIENT_CERT);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_ENABLE_TOKEN_DELEGATION)) {
            props.put(PkConstants.NAME_PROP_AC_ENABLE_TOKEN_DELEGATION, PkConstants.DEFAULT_AC_ENABLE_TOKEN_DELEGATION);
        }

        Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_AUTH_ATTEMPTS_MAX));
        Long.parseLong(props.get(PkConstants.NAME_PROP_AC_REAUTH_SECS));
        Long.parseLong(props.get(PkConstants.NAME_PROP_AC_KS_REAUTH_SECS));

        final String ks = props.get(PkConstants.NAME_PROP_AC_KEYSTORE_PATH);
        final String ts = props.get(PkConstants.NAME_PROP_AC_TRUSTSTORE_PATH);
        final String ksp = props.get(PkConstants.NAME_PROP_AC_KEYSTORE_PASSWD);
        final String tsp = props.get(PkConstants.NAME_PROP_AC_TRUSTSTORE_PASSWD);

        if(XbUtils.isEmpty(ks)) {
            props.put(PkConstants.NAME_PROP_AC_KEYSTORE_PATH, PkConstants.DEFAULT_AC_KEYSTORE_PATH);
        }

        if(XbUtils.isEmpty(ts)) {
            props.put(PkConstants.NAME_PROP_AC_TRUSTSTORE_PATH, PkConstants.DEFAULT_AC_TRUSTSTORE_PATH);
        }

        if(XbUtils.isEmpty(ksp)) {
            props.put(PkConstants.NAME_PROP_AC_KEYSTORE_PASSWD, PkConstants.DEFAULT_AC_KEYSTORE_PASSWD);
        }

        if(XbUtils.isEmpty(tsp)) {
            props.put(PkConstants.NAME_PROP_AC_TRUSTSTORE_PASSWD, PkConstants.DEFAULT_AC_TRUSTSTORE_PASSWD);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_SERVICE_ACCT_ROLE)) {
            props.put(PkConstants.NAME_PROP_AC_SERVICE_ACCT_ROLE, PkConstants.DEFAULT_AC_SERVICE_ACCT_ROLE);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD)) {
            props.put(PkConstants.NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD, PkConstants.DEFAULT_AC_HTTP_BASIC_AUTH_PASSWD);
        }

        if(!props.containsKey(PkConstants.NAME_PROP_AC_TRUSTED_HOSTS_PATTERN)) {
            props.put(PkConstants.NAME_PROP_AC_TRUSTED_HOSTS_PATTERN, PkConstants.DEFAULT_AC_TRUSTED_HOSTS_PATTERN);
        }

        // Audit properties

        if(!props.containsKey(PkConstants.NAME_PROP_AUDIT_BATCH_SIZE_MAX)) {
            props.put(PkConstants.NAME_PROP_AUDIT_BATCH_SIZE_MAX, PkConstants.DEFAULT_AUDIT_BATCH_SIZE_MAX);
        }
    }

    public static String
    createLogonServiceReauthUrl(HashMap<String, String> props, String returnUrl, String sessionId, String nonce) {
        return XbUtils.createLogonServiceUrl(
            "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE)),
            props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER),
            Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT)),
            (
                props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                    XbUtils.getAppPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME)
            ) + "/" + props.get(PkConstants.NAME_PROP_AC_LOGON_PATH),
            returnUrl,
            sessionId,
            nonce
        );
    }

    public static String
    createLogonServiceUrl(HashMap<String, String> props, String returnUrl, String nonce, String method) {
        if(!"GET".equalsIgnoreCase(method)) {
            returnUrl = XbUtils.getAppBaseUrl(returnUrl);
        }

        return XbUtils.createLogonServiceUrl(
            "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE)),
            props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER),
            Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT)),
            (
                props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                        XbUtils.getAppPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME)
            ) + "/" + props.get(PkConstants.NAME_PROP_AC_LOGON_PATH),
            returnUrl,
            null,
            nonce
        );
    }

    public static String
    createLogoutServiceUrl(HashMap<String, String> props) {
        return XbUtils.createLogoutUrl(props, null);
    }

    public static String
    createLogoutServiceUrl(HashMap<String, String> props, String back) {
        return XbUtils.createLogoutUrl(props, back);
    }

    public static String
    createHelloServiceUrl(HashMap<String, String> props) {
        final String server = props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER);
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT));

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath(
            "/" +
            (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ? XbUtils.getAppPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME)) +
            "/" +
            props.get(PkConstants.NAME_PROP_AC_HELLO_PATH)
        );

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String
    getAuthServiceUrl(HashMap<String, String> props) {
        final String server = props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER);
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT));

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath(
            "/" +
            (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ? XbUtils.getAppPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME))
        );

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String
    createLogonServiceUrl(boolean isSecure, String server, int port, String servicePath, String returnUrl, String sessionId, String nonce) {
        assert(!XbUtils.isEmpty(server) && !XbUtils.isEmpty(returnUrl));

        try {
            final URIBuilder ub = new URIBuilder();
            ub.setScheme(isSecure ? "https" : "http");
            ub.setHost(server);
            ub.setPort(port);
            ub.setPath("/" + servicePath);

            ub.setParameter(PkConstants.NAME_PARAM_AC_RETURN_URL, returnUrl);

            if (sessionId != null) {
                ub.setParameter("jsessionid", sessionId);
            }

            if (nonce != null) {
                ub.setParameter(PkConstants.NAME_PARAM_AC_NONCE, nonce);
            }

            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean
    isReturnUrlPresent(String url) {
        if((url != null) && (url.indexOf(PkConstants.NAME_PARAM_AC_RETURN_URL) >= 0)) {
            return true;
        }

        return false;
    }

    public static String createGUID() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    public static String
    createReturnUrl(String logonServiceUrl, String reqId, String error) {
        assert(!XbUtils.isEmpty(reqId) || !XbUtils.isEmpty(error));

        try {
            final URIBuilder ub = new URIBuilder(logonServiceUrl);
            final String returnUrl = XbUtils.getParam(ub, PkConstants.NAME_PARAM_AC_RETURN_URL);
            final URIBuilder rub = new URIBuilder(URLDecoder.decode(returnUrl, "UTF-8"));

            if(XbUtils.isEmpty(reqId)) {
                rub.setParameter(PkConstants.NAME_PARAM_AC_ERROR, error);
            }
            else {
                final String nonce = XbUtils.getParam(ub, PkConstants.NAME_PARAM_AC_NONCE);

                if(nonce != null) {
                    rub.setParameter(PkConstants.NAME_PARAM_AC_REQUEST_ID, XbUtils.xorId(reqId, nonce));
                }
                else {
                    rub.setParameter(PkConstants.NAME_PARAM_AC_REQUEST_ID, reqId);
                }
            }

            final String retStr = rub.build().toURL().toString();

            return retStr;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String
    createReturnUrl(String logonServiceUrl) {
        try {
            final URIBuilder ub = new URIBuilder(logonServiceUrl);
            final String returnUrl = XbUtils.getParam(ub, PkConstants.NAME_PARAM_AC_RETURN_URL);
            final URIBuilder rub = new URIBuilder(URLDecoder.decode(returnUrl, "UTF-8"));
            final String retStr = rub.build().toURL().toString();

            return retStr;
        }
        catch(Exception e) {
            return null;
        }
    }

    public static boolean isNoncePresent(String url) {
        try {
            final URIBuilder ub = new URIBuilder(url);

            if(XbUtils.getParam(ub, PkConstants.NAME_PARAM_AC_NONCE) != null) {
                return true;
            }
        }
        catch(Exception e) {
        }

        return false;
    }

    public static String getAppIdFromUrl(String url) {
        int pos = -1;
        pos = url.indexOf("//");

        if(pos > 0) {
            url = url.substring(pos + 2);
            pos = url.indexOf('/');

            if(pos > 0) {
                url = url.substring(pos + 1);
                pos = url.indexOf('/');

                if(pos > 0) {
                    return url.substring(0, pos);
                }
            }
        }

        return "";
    }

    public static String getIpFromUrl(String url) {
        int pos = -1;
        pos = url.indexOf("//");

        if(pos > 0) {
            url = url.substring(pos + 2);
            pos = url.indexOf('/');
            final int pos2 = url.indexOf(":");

            if(pos2 < 0) {
                if(pos < 0) {
                    return url.substring(0);
                }
                else {
                    return url.substring(0, pos);
                }
            }
            else {
                return url.substring(0, pos2);
            }
        }

        return "";
    }

    public static String getIpPortFromUrl(String url) {
        int pos = -1;
        pos = url.indexOf("//");

        if(pos > 0) {
            url = url.substring(pos + 2);
            pos = url.indexOf('/');

            if(pos < 0) {
                return url.substring(0);
            }
            else {
                return url.substring(0, pos);
            }
        }

        return "";
    }

    public static String getPortFromUrl(String url) {
        url = XbUtils.getIpPortFromUrl(url);
        final int ind = url.indexOf(":");

        if(ind < 0) {
            return null;
        }
        else {
            return url.substring(ind + 1);
        }
    }

    public static String getAppBaseUrl(String url) {
        int pos = -1;
        pos = url.indexOf("//");

        try {
            if (pos > 0) {
                pos += 2;
                pos += url.substring(pos).indexOf('/');

                if (pos > 0) {
                    ++pos;
                    pos += url.substring(pos).indexOf('/');

                    if (pos > 0) {
                        return url.substring(0, pos);
                    }
                }
            }
        }
        catch (Exception ignore) {
        }

        return url.substring(0, url.lastIndexOf("/"));
    }

    public static String xorId(String reqId, String nonce) {
        if(nonce == null) {
            return reqId;
        }

        assert(!XbUtils.isEmpty(nonce));

        final byte[] s1 = reqId.getBytes();
        final byte[] s2 = nonce.getBytes();

        assert(s1.length == s2.length);

        byte[] s3 = new byte[s1.length];

        for(int i = 0; i < s1.length; ++i) {
            s3[i] = (byte) (s1[i] ^ s2[i]);
        }

        String res = Base64.encodeBase64String(s3);

        return res;
    }

    public static String dexorId(String reqId, String nonce) {
        if(nonce == null) {
            return reqId;
        }

        assert(!XbUtils.isEmpty(reqId));

        try {
            reqId = URLDecoder.decode(reqId, "UTF-8");
        }
        catch(Exception e) {
            return null;
        }

        final byte[] s1 = Base64.decodeBase64(reqId);
        final byte[] s2 = nonce.getBytes();

        assert(s1.length == s2.length);

        byte[] s3 = new byte[s1.length];

        for(int i = 0; i < s1.length; ++i) {
            s3[i] = (byte) (s1[i] ^ s2[i]);
        }

        return new String(s3);
    }

    public static String getTokenFromUrl(String url) {
        assert(url != null);

        String token = null;

        try {
            final URIBuilder ub = new URIBuilder(url);
            token = XbUtils.getParam(ub, PkConstants.NAME_PARAM_AC_REQUEST_ID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    public static String getParam(URIBuilder ub, String name) {

        for(int i = 0; i < ub.getQueryParams().size(); ++i) {
            if(name.equals(ub.getQueryParams().get(i).getName())) {
                return ub.getQueryParams().get(i).getValue();
            }
        }

        return null;
    }

    public static String dumpProperties(HashMap<String, String> props) {
        if(props == null) {
            return "";
        }

        final StringBuilder res = new StringBuilder();
        final Iterator<String> keys = props.keySet().iterator();

        while(keys.hasNext()) {
            final String key = keys.next();
            res.append(key);
            res.append(":");

            final String lkey = key.toLowerCase();

            if((lkey.indexOf("pwd") >= 0) || (lkey.indexOf("pass") >= 0) || (lkey.indexOf("secret") >= 0)) {
                res.append("***");
            }
            else {
                res.append(props.get(key));
            }
            res.append("\n");
        }

        return res.toString();
    }

    /**
     * This method parses authorization header, which must be base64 encoded.
     * Format: <tokenRef> | <[<directory>\] <userName>>:<password>
     *
     * @param headerBase64 Authorization header
     * @return Array of 4 Strings: tokenRef, directory, userName, password.
     * @throws Exception If parsing fails.
     */
    public static String[] parseAuthorizationHeader(final String headerBase64) throws Exception {
        final String[] res = new String[4];

        for(int i = 0; i < 4; ++i) {
            res[i] = "";
        }

        if(!XbUtils.isEmpty(headerBase64)) {
            if(!headerBase64.toLowerCase().startsWith("basic")) {
                throw new IllegalArgumentException("Wrong format of HTTP Basic authorization header");
            }

            final String header = new String(Base64.decodeBase64(headerBase64.substring(6).trim()));
            String[] comps = header.split(":");

            switch(comps.length) {
                case 1:
                    if(header.indexOf("\\") < 0) {
                        res[0] = comps[0];
                        break;
                    }
                    else {
                        final String tmp = comps[0];
                        comps = new String[2];
                        comps[0] = tmp;
                        comps[1] = "";
                    }

                case 2:
                    final String[] subcomps = comps[0].split("\\\\");

                    switch(subcomps.length) {
                        case 1:
                            if(comps[0].endsWith("\\")) {
                                res[1] = subcomps[0];
                            }
                            else {
                                res[2] = subcomps[0];
                            }

                            res[3] = comps[1];
                            break;

                        case 2:
                            res[1] = subcomps[0];
                            res[2] = subcomps[1];
                            res[3] = comps[1];
                            break;

                        default:
                            break;
                    }
                    break;

                default:
                    break;
            }
        }

        return res;
    }

    public static String
    createGetTokenUrl(final HashMap<String, String> props, final String appId, final boolean enableTokenDelegation) {
        final String server = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) : XbUtils.getIpPathComp();
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt((props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation ?
                props.get(PkConstants.NAME_PROP_AC_LOGON_PORT) : XbUtils.getPortPathComp());
        final String app = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME) : appId;

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath("/" + app + "/login/gettokens2s.action");
        ub.setParameter("appclass", AuditEvent.AppClass.XBRMS.toString());

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String
    createValidateTokenUrl(final HashMap<String, String> props, final String appId, final boolean enableTokenDelegation) {
        final String server = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) : XbUtils.getIpPathComp();
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt((props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation ?
                props.get(PkConstants.NAME_PROP_AC_LOGON_PORT) : XbUtils.getPortPathComp());
        final String app = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME) : appId;

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath("/" + app + "/login/validatetokens2s.action");

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String
    createInvalidateTokenUrl(final HashMap<String, String> props, final String appId, final boolean enableTokenDelegation) {
        final String server = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) : XbUtils.getIpPathComp();
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt((props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation ?
                props.get(PkConstants.NAME_PROP_AC_LOGON_PORT) : XbUtils.getPortPathComp());
        final String app = (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) != null) && enableTokenDelegation?
                props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME) : appId;

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath("/" + app + "/login/invalidatetokens2s.action");

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String
    createLogoutUrl(HashMap<String, String> props, String back) {
        final String server = props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getIpPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER);
        final boolean isSecure = "true".equalsIgnoreCase(props.get(PkConstants.NAME_PROP_AC_LOGON_IS_SECURE));
        final int port = Integer.parseInt(props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ?
                XbUtils.getPortPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_PORT));

        final URIBuilder ub = new URIBuilder();
        ub.setScheme(isSecure ? "https" : "http");
        ub.setHost(server);
        ub.setPort(port);
        ub.setPath(
            "/" +
            (props.get(PkConstants.NAME_PROP_AC_LOGON_SERVER) == null ? XbUtils.getAppPathComp() : props.get(PkConstants.NAME_PROP_AC_LOGON_APPNAME)) +
            "/" + props.get(PkConstants.NAME_PROP_AC_LOGOUT_PATH)
        );

        if(!XbUtils.isEmpty(back)) {
            ub.setParameter(PkConstants.NAME_PARAM_AC_RETURN_URL, back);
        }

        try {
            final String ret = ub.build().toURL().toString();
            return ret;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object objectFromXmlString(final String s, final Class clazz) throws Exception {
        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Unmarshaller m = context.createUnmarshaller();
        return m.unmarshal(new StringReader(s));
    }

    public static String objectToXmlString(final Object o, final Class clazz) throws Exception {
        final StringWriter writer = new StringWriter();
        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Marshaller m = context.createMarshaller();
        m.marshal(o, writer);
        return writer.toString();
    }

    /**
	 * Invoke action asynchronously.
	 *
	 * @param targetClass Target class
	 * @param methodName Target method name
	 * @param paramTypes Argument types, method accepts
	 * @param params Arguments
	 * @param timeout Timeout in milisecs
	 * @param target Target object, null for static methods
	 * @return Object(s) type(s) defined by the target method
	 * @throws java.lang.InterruptedException in the case of timeout or exception type
	 * originally thrown by the target method.
	 */
	public static Object tryAction(
        Class targetClass,
		String methodName,
		Class[] paramTypes,
		Object[] params,
		int timeout,
		Object target
	) throws Throwable {

		Method method;

		try {
			method = targetClass.getDeclaredMethod(methodName, paramTypes);
		}
		catch(NoSuchMethodException e) {
			//This should never happen
			throw new RuntimeException(
				"NoSuchMethodException was caught in tryAction()"
			);
		}

		InnerHelper helper = new InnerHelper(method, params, target);
		Thread p = new Thread(helper);

		p.start();

		try {
			p.join(timeout);
		}
		catch(InterruptedException e) {
			p.interrupt();
			p = null;
			throw e;
		}

		if(p.isAlive()) {
			p.interrupt();
			p = null;
			throw new InterruptedException();
		}

		if(helper.isSuccess() == false) {
			try {
				helper.getException();
			}
			catch(Throwable e) {
				throw e;
			}
		}

		return helper.getResults();
	}

    final private static class InnerHelper implements Runnable {
        private final Method mMethod;
        private final Object mTarget;
        private final Object[] mParams;
        private Object mResults;
        private Throwable mException = null;

        InnerHelper(Method method, Object[] params, Object target) {
            mMethod = method;
            mTarget = target;
            mParams = params;
        }

        Object getResults() {
            return mResults;
        }

        boolean isSuccess() {
            if (mException == null) {
                return true;
            }
            else {
                return false;
            }
        }

        void getException() throws Throwable {
            throw mException;
        }

        public void run() {
            try {
                mResults = mMethod.invoke(mTarget, mParams);
            }
            catch (IllegalAccessException e) {
                //This should never happen
                throw new RuntimeException("IllegalAccessException was caught in tryAction()");
            }
            catch (InvocationTargetException e) {
                mException = e.getTargetException();
            }
        }
    }

    /*
    public static void main(String[] args) throws Exception {
        String url = "https://www.cnn.com/app/redirect.action?fdfdfd=888";
        String ip = "myip";
        System.out.println(XbUtils.replaceHostIp(url, ip));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(XbUtils.getAppBaseUrl("http://www.cnn.com:8080/app/redirect.action?fdfdfd=888"));
    }

    public static void main(String[] args) throws Exception {
        String returnUrl = "https://localhost:8090/UI/attractionview";
        String url = XbUtils.createLogonServiceUrl(true, "xbrms.disney.com", 8080, "XBRMS", returnUrl, "ses12345", "FATDNGFLGNSEIRGNVLDFGMoo");

        System.out.println(url);

        String url2 = XbUtils.createReturnUrl(url, "FATDNGFLGNSEIRGNVLDFGMMV", null);

        System.out.println(url2);

        url2 = XbUtils.createReturnUrl(url, null, "Error description");

        System.out.println(url2);
        System.out.println(XbUtils.createGUID());

        URIBuilder ub = new URIBuilder("http://localhost:8090/AC/login.action?aaa=bbb");
        System.out.println("URL: " + ub.build().toURL().toString());

        String reqId = "8D9F502825959EC06EA0139B4B588DD5";
        String nonce = "cndgwudncvbr84hdvs93fbvukfufndge";
        String res = XbUtils.xorId(reqId, nonce);

        res = URLEncoder.encode(res, "UTF-8");
        System.out.println(res);

        res = XbUtils.dexorId(res, nonce);

        System.out.println(res);
    }

    public static void main(String[] args) throws Exception {
        String s = URLEncoder.encode("user:password", "UTF-8");
        String res[] = XbUtils.parseAuthorizationHeader(s);

        for(String r : res) {
            System.out.println(r);
        }

        System.out.println("--------------");

        s = URLEncoder.encode("directory\\user:password", "UTF-8");
        res = XbUtils.parseAuthorizationHeader(s);

        for(String r : res) {
            System.out.println(r);
        }

        System.out.println("--------------");

        s = URLEncoder.encode("\\user:password", "UTF-8");
        res = XbUtils.parseAuthorizationHeader(s);

        for(String r : res) {
            System.out.println(r);
        }

        System.out.println("--------------");

        s = URLEncoder.encode("directory\\user:", "UTF-8");
        res = XbUtils.parseAuthorizationHeader(s);

        for(String r : res) {
            System.out.println(r);
        }

        System.out.println("--------------");

        s = URLEncoder.encode("directory\\:password", "UTF-8");
        res = XbUtils.parseAuthorizationHeader(s);

        for(String r : res) {
            System.out.println(r);
        }
    }
    */
}
