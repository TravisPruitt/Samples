package com.disney.xband.xbrc.ui;

import com.disney.xband.ac.lib.PkConstants;
import com.disney.xband.common.lib.audit.AuditFactory;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAuditConnectionPool;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/22/13
 * Time: 12:31 PM
 */
public class AuditInit {
    private static Logger logger = Logger.getLogger(AuditInit.class);

    public static void initializeAudit(final Properties props, final String venue) {
        final AuditConfig currentConfig = Auditor.getInstance().getConfig();
        final AuditConfig newConfig = new AuditConfig();

        Connection conn = null;

        try {
            conn = UIConnectionPool.getInstance().getConnection();
            newConfig.populateAuditConfigFromDb(conn);
        }
        catch (Exception e) {
            logger.error("Failed to populate AuditConfig from a database");
            return;
        }
        finally {
            if(conn != null) {
                try {
                    UIConnectionPool.getInstance().getInstance().releaseConnection(conn);
                }
                catch(Exception ignore) {
                }
            }
        }

        if(currentConfig.getImplStack().indexOf("AuditDbImpl") < 0) {
            AuditInit.initializeAudit(props, newConfig);
        }
        else {
            // This attribute only makes sense for systems, which manage others, like xBRMS.
            currentConfig.setEnabledDips("");
            newConfig.setEnabledDips("");

            // In case the application ID has changed
            newConfig.setAppId(venue);

            if(!newConfig.isEqual(currentConfig)) {
                AuditInit.initializeAudit(props, currentConfig);
            }
        }

        Auditor.getInstance().getEventsProvider().cleanup(false);
    }

    private static void initializeAudit(final Properties props, final AuditConfig config) {
        config.setImplStack("com.disney.xband.common.lib.audit.providers.AuditDbImpl");
        config.setConnectionPool(new IAuditConnectionPool() {
            @Override
            public Connection getConnection() throws Exception {
                return UIConnectionPool.getInstance().getConnection();
            }

            @Override
            public void releaseConnection(Connection conn) {
                UIConnectionPool.getInstance().getInstance().releaseConnection(conn);
            }
        });

        config.setAppClass(AuditEvent.AppClass.UI.toString());

        if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_ENABLED) != null) {
            config.setEnabled(Boolean.parseBoolean((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_ENABLED)));
        }

        if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_KEEP_IN_CACHE_EVENTS_MAX) != null) {
            config.setKeepInCacheEventsMax(Integer.parseInt((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_KEEP_IN_CACHE_EVENTS_MAX)));
        }

        if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_KEEP_IN_GLOBAL_DB_DAYS_MAX) != null) {
            config.setKeepInGlobalDbDaysMax(Integer.parseInt((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_KEEP_IN_GLOBAL_DB_DAYS_MAX)));
        }

        if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_PULL_INTERVAL_SECS) != null) {
            config.setPullIntervalSecs(Integer.parseInt((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_PULL_INTERVAL_SECS)));
        }

        if(props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_LEVEL) != null) {
            config.setLevel(AuditEvent.Type.valueOf((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_LEVEL)));
        }

        config.setBatchSizeMax(Integer.parseInt((String) props.get(com.disney.xband.ac.lib.PkConstants.NAME_PROP_AUDIT_BATCH_SIZE_MAX)));
        config.setInterceptors(AuditConfig.createAuditInterceptorConfigsFromProps(props, false));
        config.setDescTemplate(AuditConfig.createDescriptionTemplatesFromProps(props));

        final AuditFactory auditFactory = new AuditFactory(config);
        Auditor.getInstance().setAuditor(auditFactory.getAudit());
        Auditor.getInstance().setEventsProvider(auditFactory.getAuditControl().getEventsProvider("AuditDbImpl"));
        Auditor.getInstance().setConfig(config);
        Auditor.getInstance().setAuditFactory(auditFactory);
    }
}
