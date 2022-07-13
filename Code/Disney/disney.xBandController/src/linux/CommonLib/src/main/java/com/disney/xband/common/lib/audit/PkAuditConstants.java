package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.audit.model.DbField;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/31/13
 * Time: 11:11 AM
 */
public interface PkAuditConstants {
    // Properties
    String PROP_NAME_AUDIT_INTERCEPTOR_CLASS_BASE = "com.disney.xband.common.lib.audit.interceptor.class";
    String PROP_NAME_AUDIT_INTERCEPTOR_PARAMS_BASE = "com.disney.xband.common.lib.audit.interceptor.params";
    //String NAME_PROP_PW_HASH_VALID_DAYS = "nge.xconnect.ac.pwcache.valid.days";
    String PROP_NAME_AUDIT_DESC_TEMPL_BASE = "com.disney.xband.common.lib.audit.desc.template";

    // Defaults
    long DEFAULT_KEEP_IN_CACHE_EVENTS_MAX = 10000;
    int DEFAULT_KEEP_IN_GLOBAL_DB_DAYS_MAX = 14;
    long DEFAULT_PULL_INTERVAL_SECS = 180;

    String DEFAULT_SIMPLE_FILTER_PARAMS_XBRC =
        "AUDIT_SUCCESS:ACCESS::," +
        "AUDIT_FAILURE:ACCESS::120," +
        "AUDIT_SUCCESS:ACCESS:xconnect-service:," +
        "AUDIT_SUCCESS:READ:xconnect-service:," +
        "AUDIT_SUCCESS:WRITE:xconnect-service:";

    String DEFAULT_SIMPLE_FILTER_PARAMS =
        "AUDIT_SUCCESS:ACCESS::," +
        "AUDIT_FAILURE:ACCESS::120," +
        "AUDIT_SUCCESS:ACCESS:xconnect-service:";

    String[] DEFAULT_AUDIT_DESC_TEMPLS = new String[] {
        "AUDIT_SUCCESS/ACCESS: User " + DbField.UID.getTemplateVar() + " got access to end-point " + DbField.RID.getTemplateVar(),
        "AUDIT_FAILURE/ACCESS: User " + DbField.UID.getTemplateVar() + " failed to get access to end-point " + DbField.RID.getTemplateVar(),
        "AUDIT_SUCCESS/LOGIN: User " + DbField.UID.getTemplateVar() + " was successfully authenticated by xAG on host " + DbField.HOST.getTemplateVar(),
        "AUDIT_FAILURE/LOGIN: User " + DbField.UID.getTemplateVar() + " failed to authenticate to xAG on host " + DbField.HOST.getTemplateVar(),
        "AUDIT_SUCCESS/LOGOUT: User " + DbField.UID.getTemplateVar() + " successfully logged out from xConnect on host " + DbField.HOST.getTemplateVar(),
        "AUDIT_SUCCESS/WRITE: User " + DbField.UID.getTemplateVar() + " modified the following " + DbField.APPCLASS.getTemplateVar() + " property on host " + DbField.HOST.getTemplateVar() + ": " + DbField.RDATA.getTemplateVar(),
        "AUDIT_FAILURE/WRITE: User " + DbField.UID.getTemplateVar() + " failed to modify an " + DbField.APPCLASS.getTemplateVar() + " property on host " + DbField.HOST.getTemplateVar(),
        "ERROR/STATUS: Initial status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " is %message.",
        "WARN/STATUS: Initial status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " is %message.",
        "INFO/STATUS: Initial status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " is %message",
        "ERROR/STATUS_CHANGE: Status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " changed from %message.",
        "WARN/STATUS_CHANGE: Status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " changed from %message.",
        "INFO/STATUS_CHANGE: Status of " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " changed from %message",
        "ERROR/: " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " reported an error: %message",
        "WARN/: " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " issued a warning: %message",
        "INFO/: " + DbField.APPCLASS.getTemplateVar() + " on host " + DbField.HOST.getTemplateVar() + " said: %message",
    };

    //int DEFAULT_PW_HASH_VALID_DAYS = 14;
}
