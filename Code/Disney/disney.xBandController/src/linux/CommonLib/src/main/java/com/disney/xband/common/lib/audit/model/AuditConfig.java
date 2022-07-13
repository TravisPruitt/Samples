package com.disney.xband.common.lib.audit.model;

import java.sql.Connection;
import java.util.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.common.lib.audit.IAuditConnectionPool;
import com.disney.xband.common.lib.audit.PkAuditConstants;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:07 PM
 */
@XmlRootElement(name = "AuditConfig")
@PersistName("AuditConfig")
public class AuditConfig extends Configuration
{
    transient private static Logger logger = Logger.getLogger(AuditConfig.class);
    
    /*
     * The transient properties are not persisted to the database
     */
    transient private String collectorUser;
    transient private String collectorPass;
    // Database connections poll
    transient private IAuditConnectionPool connectionPool;
    // Local file used by the AuditFileImpl and AuditXmlFileImpl providers to cache events
    transient private String fileStorePath;
    // A comma or space separated list of IAudit interface implementations.
    transient private String implStack;
    // Maximum number of events passed over network between event providers and event collectors in one operation.
    transient private int batchSizeMax;
    // See AuditEvent.AppClass (XBRC, XBRCUI, XBRMS, XBRMSUI, XI, IDMS, JSMLISTENER, GREETER_APP, UNKNOWN)
    transient private String appClass;
    // Host name (DIP). If VIP or DIP is not defined "host = vHost".
    transient private String host;
    // Host name (VIP). If VIP or DIP is not defined "host = vHost".
    transient private String vHost;
    // Comma-separated list of interceptors
    transient private List<AuditInterceptorConfig> interceptors;
    // Templates for processing the "description" field
    transient private Map<String, String> descTemplate;

    /*
     * Persisted properties
     */
    // Does this configuration override the global one?
    @PersistName("isOverride")
	@MetaData(
        name = "isOverride",
        description = "Does this configuration override the global one?",
        defaultValue = "false",
        updatable = true
    )
    private boolean isOverride;

    // Is Audit enabled?
    @PersistName("isEnabled")
	@MetaData(
        name = "isEnabled",
        description = "Is Audit enabled?",
        defaultValue = "true",
        updatable = true)
    private boolean isEnabled;

    // If isOverride=true, this attribute contains a space-separated list of managed systems (DIPS), on which
    // audit is enabled.
    @PersistName("enabledDips")
	@MetaData(
        name = "enabledDips",
        description = "If isOverride=true, a space-separated list of managed systems (DIPS), on which audit is enabled",
        defaultValue = "",
        updatable = true
    )
    private String enabledDips;

    // Events collector connection settings used by the AuditNetImpl provider to "push" events to an event collector.
    @PersistName("collectorUrl")
	@MetaData(
        name = "collectorUrl",
        description = "Events collector connection URL used by the AuditNetImpl provider to 'push' events to an event collector.",
        defaultValue="",
        updatable = true
    )
    private String collectorUrl;

    // Maximum number of records to keep in the events cache. On reaching this number 1/3 of the records is
    // automatically deleted to prevent running out of disk space. This setting does not apply to the
    // enterprise (global) database.
    @PersistName("keepInCacheEventsMax")
	@MetaData(
        name = "keepInCacheEventsMax",
        description = "Maximum number of records to keep in the events cache.",
        defaultValue = PkAuditConstants.DEFAULT_KEEP_IN_CACHE_EVENTS_MAX + "",
        updatable = true
    )
    private long keepInCacheEventsMax;

    // Maximum number of days to keep audit events in the enterprise (global) database.
    @PersistName("keepInGlobalDbDaysMax")
	@MetaData(
        name = "keepInGlobalDbDaysMax",
        description = "Maximum number of days to keep audit events in the enterprise (global) database.",
        defaultValue = PkAuditConstants.DEFAULT_KEEP_IN_GLOBAL_DB_DAYS_MAX + "",
        updatable = true
    )
    private int keepInGlobalDbDaysMax;

    // How often event collectors must pull events from event providers.
    @PersistName("pullIntervalSecs")
	@MetaData(
        name = "pullIntervalSecs",
        description = "How often event collectors must pull events from event providers.",
        defaultValue = PkAuditConstants.DEFAULT_PULL_INTERVAL_SECS + "",
        updatable = true
    )
    private long pullIntervalSecs;

    // See AuditEvent.Type (AUDIT_FAILURE, AUDIT_SUCCESS, FATAL, ERROR, WARN, INFO)
    @PersistName("level")
	@MetaData(
        name = "level",
        description = "Audit Level: AUDIT_FAILURE, AUDIT_SUCCESS, FATAL, ERROR, WARN, INFO",
        defaultValue = "INFO",
        updatable = true
    )
    private AuditEvent.Type level;

    // Application instance ID, e.g. venue ID for xBRC, park ID for xBRMS, etc.
    @PersistName("appId")
	@MetaData(
        name = "appId",
        description = "Application instance ID, e.g. venue ID for xBRC, park ID for xBRMS, etc.",
        defaultValue = "",
        updatable = false
    )
    private String appId;

    public boolean populateAuditConfigFromDb(final Connection conn) {

        try {
            // read property/value pairs from the Config table
            com.disney.xband.common.lib.Config.getInstance().read(conn, this);
        }
        catch(Exception e) {
            logger.error("!! Error processing audit configuration table in database: " + e.getMessage());
            return false;
        }

        return true;
    }

	@Override
	protected void initHook(Connection conn)
	{
	}

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getvHost() {
        return vHost;
    }

    public void setvHost(String vHost) {
        this.vHost = vHost;
    }

    public String getImplStack() {
        return implStack;
    }

    public void setImplStack(String implStack) {
        this.implStack = implStack;
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public void setCollectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
    }

    public String getCollectorUser() {
        return collectorUser;
    }

    public void setCollectorUser(String collectorUser) {
        this.collectorUser = collectorUser;
    }

    public String getCollectorPass() {
        return collectorPass;
    }

    public void setCollectorPass(String collectorPass) {
        this.collectorPass = collectorPass;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getFileStorePath() {
        return fileStorePath;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }

    public AuditEvent.Type getLevel() {
        return level;
    }

    public void setLevel(AuditEvent.Type level) {
        this.level = level;
    }

    @XmlTransient
    public IAuditConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(IAuditConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public void setOverride(boolean override) {
        isOverride = override;
    }

    public int getBatchSizeMax() {
        return batchSizeMax;
    }

    public void setBatchSizeMax(int batchSizeMax) {
        this.batchSizeMax = batchSizeMax;
    }

    public long getKeepInCacheEventsMax() {
        return keepInCacheEventsMax;
    }

    public void setKeepInCacheEventsMax(long keepInCacheEventsMax) {
        this.keepInCacheEventsMax = keepInCacheEventsMax;
    }

    public int getKeepInGlobalDbDaysMax() {
        return keepInGlobalDbDaysMax;
    }

    public void setKeepInGlobalDbDaysMax(int keepInGlobalDbDaysMax) {
        this.keepInGlobalDbDaysMax = keepInGlobalDbDaysMax;
    }

    public long getPullIntervalSecs() {
        return pullIntervalSecs;
    }

    public void setPullIntervalSecs(long pullIntervalSecs) {
        this.pullIntervalSecs = pullIntervalSecs;
    }

    public String getEnabledDips() {
        return enabledDips;
    }

    public void setEnabledDips(String enabledDips) {
        this.enabledDips = enabledDips;
    }

    @XmlTransient
    public List<AuditInterceptorConfig> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<AuditInterceptorConfig> interceptors) {
        this.interceptors = interceptors;
    }

    @XmlTransient
    public Map<String, String> getDescTemplate() {
        return descTemplate;
    }

    public void setDescTemplate(Map<String, String> descTemplate) {
        this.descTemplate = descTemplate;
    }

    public static Map<String, String> createDescriptionTemplatesFromProps(final Properties props) {
        final Map<String, String> res = new HashMap<String, String>();

        if (props != null) {
            for(int i = 0; i < PkAuditConstants.DEFAULT_AUDIT_DESC_TEMPLS.length; ++i) {
                final String[] s = PkAuditConstants.DEFAULT_AUDIT_DESC_TEMPLS[i].split(":");
                res.put(s[0].trim(), s[1].trim());
            }

            for (int i = 0; i < 20; ++i) {
                final String p = (String) props.get(PkAuditConstants.PROP_NAME_AUDIT_DESC_TEMPL_BASE + "." + i);

                if (p != null) {
                    final String[] s = p.split(":");

                    if((s != null) && (s[0] != null) && (s[1] != null)) {
                        res.put(s[0].trim(), s[1].trim());
                    }
                }
            }
        }

        return res;
    }

    public static List<AuditInterceptorConfig> createAuditInterceptorConfigsFromProps(final Properties props, final boolean isXbrc) {
        final ArrayList<AuditInterceptorConfig> res = new ArrayList<AuditInterceptorConfig>();

        if (props != null) {
            for (int i = 0; i < 20; ++i) {
                final String interceptorClass = (String) props.get(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_CLASS_BASE + "." + i);
                final String interceptorParams = (String) props.get(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_PARAMS_BASE + "." + i);

                if ((interceptorClass != null) && (interceptorClass.trim().length() > 0)) {
                    res.add(new AuditInterceptorConfig(interceptorClass, interceptorParams));
                }
            }

            if(res.size() == 0) {
                res.add(
                    new AuditInterceptorConfig("com.disney.xband.common.lib.audit.interceptors.SimpleFilter",
                    isXbrc ? PkAuditConstants.DEFAULT_SIMPLE_FILTER_PARAMS_XBRC : PkAuditConstants.DEFAULT_SIMPLE_FILTER_PARAMS)
                );
            }
        }

        return res;
    }
}
