package com.disney.xband.xbrms.common;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 4:30 PM
 */
public interface PkConstants {
    // url format: jdbc:jtds:sqlserver://10.10.10.10:1433/XBRMS
    String PROP_DB_URL = "nge.xconnect.xbrms.dbserver.url";
    String PROP_DB_USER = "nge.xconnect.xbrms.dbserver.uid";
    String PROP_DB_PASS = "nge.xconnect.xbrms.dbserver.pwd";
    String PROP_READER_URL_TEMPLATE = "nge.xconnect.xbrms.reader_url_template";
    int DEFAULT_SQL_SERVER_PORT = 1433;
    int READER_PRONOUNCED_DEAD_AFTER_MIN = 2;
    long READER_IDENTIFICATION_SEQUENCE_TIMEOUT = 30000;
    String XBRMS_KEY = "127.0.0.1:0"; // Key for XBRMS
    String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
    long STATUS_REFRESH_MS = 60000;
    String PROP_NAME_XBRMS_SERVER_URL = "nge.xconnect.xbrms.server.url";
    String PROP_NAME_XBRMS_SERVER_URI = "nge.xconnect.xbrms.server.uri";
    String PROP_NAME_XBRMS_SERVER_DESC = "nge.xconnect.xbrms.server.desc";
    String PROP_NAME_XBRMS_SERVER_LOGO = "nge.xconnect.xbrms.server.logo";
    String PROP_NAME_XBRMS_UI_HOST_ALIAS = "nge.xconnect.xbrms.ui.host.alias";
    String PROP_NAME_XBRMS_UI_HOST = "nge.xconnect.xbrms.ui.host";
    String PROP_NAME_XBRMS_SERVER_UI_NS = "nge.xconnect.xbrms.server.ui.ns";
    String XBRMS_SERVER_APP_NAME = "XBRMS";
    String XBRMS_UI_APP_NAME = "XBRMSUI";
    String XBRMS_UI_HOME_ACTION_NAME = "home.action";
    int XBRMS_SERVERS_MAX = 10;
    String IP_PATTERN =
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    String PROP_NAME_LOCAL_SETTINGS_FILE = "nge.xconnect.xbrms.ui.settings.file";
    String DEFAULT_LOCAL_SETTINGS_FILE = "/usr/share/xbrms/parks.xml";
}
