package com.disney.xband.ac.lib;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/18/12
 * Time: 5:53 PM
 */
public interface PkConstants {
    // Http request attribute names
    public static final String NAME_ATTR_REQ_AC_DISABLED = "ReqAttrNameAcDisabled";

    // Http session attribute names
    public static final String NAME_ATTR_AC_SECURE_TOKEN = "AttrName.AcSecureToken";
    public static final String NAME_ATTR_AC_SECURE_TOKEN_SRV = "AttrName.AcSecureTokenSrv";
    public static final String NAME_ATTR_AC_AUTH_RETRIES = "AttrName.AcAuthRetries";
    public static final String NAME_ATTR_AC_SAVED_REQUEST = "AttrName.AcSavedRequest";
    public static final String NAME_ATTR_AC_SAVED_NONCE = "AttrName.AcSavedNonce";
    public static final String NAME_ATTR_AC_RETURN_URL = "AttrName.AcReturnUrl";

    // Http Servlet container attribute names
    public static final String NAME_ATTR_AC_MANAGER = "AttrName.AcManager";
    public static final String NAME_ATTR_AC_PROPS = "AttrName.AcProps";
    public static final String NAME_ATTR_AC_AUTH_SERVICE = "AttrName.AcAuthService";

    // Property names
    public static final String NAME_PROP_AC_LOGON_SERVER = "nge.xconnect.ac.logonServer";
    public static final String NAME_PROP_AC_LOGON_PORT = "nge.xconnect.ac.logonPort";
    public static final String NAME_PROP_AC_LOGON_PATH = "nge.xconnect.ac.logonPath";
    public static final String NAME_PROP_AC_LOGOUT_PATH = "nge.xconnect.ac.logoutPath";
    public static final String NAME_PROP_AC_HELLO_PATH = "nge.xconnect.ac.helloPath";
    public static final String NAME_PROP_AC_LOGON_APPNAME = "nge.xconnect.ac.logonAppName";
    public static final String NAME_PROP_AC_LOGON_IS_SECURE = "nge.xconnect.ac.logonUseHttps";
    public static final String NAME_PROP_AC_ROLES_MAP_FILE = "nge.xconnect.ac.rolesMapFile";
    public static final String NAME_PROP_AC_USERS_FILE = "nge.xconnect.ac.usersFile";
    public static final String NAME_PROP_AC_PROP_FILE_PATH = "nge.xconnect.ac.propsFilePath";
    public static final String NAME_PROP_AC_USERS_PATH = "nge.xconnect.ac.usersFilePath";
    public static final String NAME_PROP_AC_AUTH_ATTEMPTS_MAX = "nge.xconnect.ac.authAttemptsMax";
    public static final String NAME_PROP_AC_AUTH_SERVICE_CLASS = "nge.xconnect.ac.authServiceClass";
    public static final String NAME_PROP_AC_LOCAL_AUTH_SERVICE_CLASS = "nge.xconnect.ac.localAuthServiceClass";
    public static final String NAME_PROP_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS = "nge.xconnect.ac.xbrcLocalAuthServiceClass";
    public static final String NAME_PROP_AC_DISABLE_XBRC_AC = "nge.xconnect.ac.disableXbrcAc";
    public static final String NAME_PROP_AC_REAUTH_SECS = "nge.xconnect.ac.reauthSecs";
    public static final String NAME_PROP_AC_DISABLE_FILTER = "nge.xconnect.ac.disableAcFilter";
    public static final String NAME_PROP_AC_TRUSTSTORE_PATH = "nge.xconnect.ac.truststorePath";
    public static final String NAME_PROP_AC_TRUSTSTORE_PASSWD = "nge.xconnect.ac.truststorePasswd";
    public static final String NAME_PROP_AC_TRUSTED_HOSTS_PATTERN = "nge.xconnect.ac.trustedHostsPattern";
    public static final String NAME_PROP_AC_KEYSTORE_PATH = "nge.xconnect.ac.keystorePath";
    public static final String NAME_PROP_AC_KEYSTORE_PASSWD = "nge.xconnect.ac.keystorePasswd";
    public static final String NAME_PROP_AC_HOST_CERT_MAP = "nge.xconnect.ac.hostCertMap";
    public static final String NAME_PROP_AC_SERVICE_ACCT_ROLE = "nge.xconnect.ac.serviceAcctRole";
    public static final String NAME_PROP_AC_HTTP_BASIC_AUTH_PASSWD = "nge.xconnect.ac.httpBasicAuthPasswd";
    public static final String NAME_PROP_AC_KS_DIRS = "nge.xconnect.ac.ksDirs";
    public static final String NAME_PROP_AC_KS_APP_ID = "nge.xconnect.ac.ksApplicationId";
    public static final String NAME_PROP_AC_KS_PRI_URL = "nge.xconnect.ac.ksPrimaryAuthURL";
    public static final String NAME_PROP_AC_KS_SEC_URL = "nge.xconnect.ac.ksSecondaryAuthURL";
    public static final String NAME_PROP_AC_KS_REAUTH_SECS = "nge.xconnect.ac.ksReauthSecs";
    public static final String NAME_PROP_AC_KS_ENABLE_AUTHORIZATION = "nge.xconnect.ac.ksEnableAuthorization";
    public static final String NAME_PROP_AC_KS_USE_CLIENT_CERT = "nge.xconnect.ac.ksUseClientCert";
//    public static final String NAME_PROP_AC_KS_CONNECTION_TO_MS = "nge.xconnect.ac.ksConnectionTimeoutMs";
    public static final String NAME_PROP_AC_HOME_URL = "nge.xconnect.ac.homeUrl";
    public static final String NAME_PROP_AC_ENABLE_TOKEN_DELEGATION = "nge.xconnect.ac.enableTokenDelegation";

    public static final String NAME_PROP_AUDIT_ENABLED = "nge.xconnect.audit.enabled";
    public static final String NAME_PROP_AUDIT_KEEP_IN_CACHE_EVENTS_MAX = "nge.xconnect.audit.keepInCacheEventsMax";
    public static final String NAME_PROP_AUDIT_KEEP_IN_GLOBAL_DB_DAYS_MAX = "nge.xconnect.audit.keepInGlobalDbDaysMax";
    public static final String NAME_PROP_AUDIT_PULL_INTERVAL_SECS = "nge.xconnect.audit.pullIntervalSecs";
    public static final String NAME_PROP_AUDIT_LEVEL = "nge.xconnect.audit.level";
    public static final String NAME_PROP_AUDIT_BATCH_SIZE_MAX = "nge.xconnect.audit.batchSizeMax";

    // Property defaults
    public static final String DEFAULT_AC_LOGON_PORT = "8080";
    public static final String DEFAULT_AC_LOGON_SECURE_PORT = "8443";
    public static final String DEFAULT_AC_LOGON_PATH = "login/login.action";
    public static final String DEFAULT_AC_HELLO_PATH = "login/hello.action";
    public static final String DEFAULT_AC_LOGOUT_PATH = "login/logout.action";
    public static final String DEFAULT_AC_LOGON_APPNAME = "IDMS";
    public static final String DEFAULT_AC_LOGON_IS_SECURE = "false";
    public static final String DEFAULT_AC_ROLES_MAP_FILE = "/WEB-INF/classes/ac-model.xml";
    public static final String DEFAULT_AC_USERS_FILE = "/WEB-INF/classes/xb-users.xml";
    public static final String DEFAULT_AC_PROP_FILE_PATH = "/etc/nge/config/";
    public static final String DEFAULT_AC_AUTH_ATTEMPTS_MAX = "3";
    public static final String DEFAULT_AC_AUTH_SERVICE_CLASS = "com.disney.xband.ac.lib.auth.InternalAuthService";
    public static final String DEFAULT_AC_LOCAL_AUTH_SERVICE_CLASS = "com.disney.xband.ac.lib.auth.LocalAuthService";
    public static final String DEFAULT_AC_XBRC_LOCAL_AUTH_SERVICE_CLASS = "com.disney.xband.ac.lib.auth.XbrcAuthService";
    public static final String DEFAULT_AC_REAUTH_SECS = "600"; // "6";
    public static final String DEFAULT_AC_DISABLE_FILTER = "false";
    public static final String DEFAULT_AC_KS_REAUTH_SECS = "0";
    public static final String DEFAULT_AC_KS_ENABLE_AUTHORIZATION = "false";
//    public static final int DEFAULT_AC_KS_CONNECTION_TO_MS = 15000;
    public static final String DEFAULT_AC_KEYSTORE_PATH = DEFAULT_AC_PROP_FILE_PATH + "keystore.jks";
    public static final String DEFAULT_AC_TRUSTSTORE_PATH = DEFAULT_AC_PROP_FILE_PATH + "truststore.jks";
    public static final String DEFAULT_AC_KEYSTORE_PASSWD = "changeit";
    public static final String DEFAULT_AC_TRUSTSTORE_PASSWD = DEFAULT_AC_KEYSTORE_PASSWD;
    public static final String DEFAULT_AC_TRUSTED_HOSTS_PATTERN = "*";
    public static final String DEFAULT_AC_SERVICE_ACCT_ROLE = "xconnect-service";
    public static final String DEFAULT_AC_HTTP_BASIC_AUTH_PASSWD = "";
    public static final String DEFAULT_AC_KS_USE_CLIENT_CERT = "false";
    public static final String DEFAULT_AC_ENABLE_TOKEN_DELEGATION = "false";

    public static final String DEFAULT_AUDIT_BATCH_SIZE_MAX = "1000";

    // URL parameter names
    public static final String NAME_PARAM_AC_RETURN_URL = "AcReturnUrl";
    public static final String NAME_PARAM_AC_HOME_URL = "AcHomeUrl";
    public static final String NAME_PARAM_AC_REQUEST_ID = "AcReqId";
    public static final String NAME_PARAM_AC_TOKEN = "AcToken";
    public static final String NAME_PARAM_AC_ERROR = "AcError";
    public static final String NAME_PARAM_AC_NONCE = "AcNonce";
}
