package com.disney.xband.xbrms.client;

import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.common.lib.audit.AuditFactory;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.xbrms.client.rest.RestCall;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 3:44 PM
 */
public class XbrmsUiServletContextListener implements ServletContextListener {
    private static Logger logger = Logger.getLogger(XbrmsUiServletContextListener.class);

    public static boolean isAuditInitialized;
    public static boolean serversNotified;

    private Thread auditInitializer;
    private boolean auditRun;

    @Override
	public void contextDestroyed(ServletContextEvent context) {
        this.auditRun = false;
        auditInitializer.interrupt();

        try {
            auditInitializer.join();
        }
        catch(InterruptedException e) {
        }
    }

    @Override
	public void contextInitialized(ServletContextEvent context) {
//        try {Thread.sleep(10000);} catch(Exception e) {};

        try
        {
            if(ConfigProperties.getInstance() == null) {
                ConfigProperties.init(false);
            }
        }
        catch (Exception e)
        {
            logger.fatal("!! This must never happen: Failed to initialize xBRMS properties class. !!");
            System.exit(1);
        }

		// read initial configuration options from the environment properties file
		try
		{
			ConfigProperties.getInstance().readConfigurationOptionsFromEnvironmentProperties();
		}
		catch (IOException e)
		{
			logger.fatal("!! Failed to initialize xBRMS. Failed to read the properties file !!");
			System.exit(1);
		}

        try {
            XbUtils.loadProperties(ConfigProperties.getInstance(), "xbrc", logger);
            XbConnection.init(XbUtils.propsToHashMap(ConfigProperties.getInstance()));
        }
        catch (Exception e) {
            logger.fatal("!! Failed to initialize XbConnection instance.");
            System.exit(1);
        }

        ConfigProperties.getInstance().initXbrmsServers();

        this.auditInitializer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setServerRoles();
                    initializeAudit(ConfigProperties.getInstance());
                }
                catch (Exception e) {
                }

                auditRun = true;

                while(auditRun) {
                    try {
                        Thread.sleep(60000);
                        setServerRoles();
                        initializeAudit(ConfigProperties.getInstance());
                    }
                    catch (Exception e) {
                    }
                }
            }
        });

        this.auditInitializer.start();
    }

    public static void initializeAudit(final Properties props) {
        if(XbrmsUiServletContextListener.isAuditInitialized ) {
            return;
        }

        AuditConfig config;
        String implClass;

        try {
            final XbrmsDto global = ConfigProperties.getInstance().getGlobalXbrmsServer();

            if((global == null) || XbrmsUtils.isEmpty(global.getUrl())) {
                logger.error("The audit subsystem will not be configured until global xBRMS is set.");
                return;
            }

            config = XbrmsUtils.getRestCaller(global.getUrl()).getAuditConfig();
            config.setEnabled(true);
            implClass = "com.disney.xband.xbrms.common.audit.providers.AuditNetImpl";
            final String collectorUrl = XbrmsUtils.normalizeXbrmsServiceUrl(ConfigProperties.getInstance().getGlobalXbrmsServer().getUrl() + "/audit/push");
            config.setCollectorUrl(collectorUrl);
            config.setImplStack(implClass);
            config.setAppClass(AuditEvent.AppClass.XBRMSUI.toString());
            config.setAppId("xBRMS UI");

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
            Auditor.getInstance().setEventsProvider(auditFactory.getAuditControl().getEventsProvider(implClass));
            Auditor.getInstance().setConfig(config);
            Auditor.getInstance().setAuditFactory(auditFactory);

            XbrmsUiServletContextListener.isAuditInitialized = true;
        }
        catch (Exception e) {
            logger.error("Audit subsystem configuration failed. The global xBRMS server is likely not running. Will try again after a while. " + e.getMessage());
        }
    }

    public static void setServerRoles() {
        if(XbrmsUiServletContextListener.serversNotified) {
            return;
        }

        boolean notifiedOk = true;
        final XbrmsDto global = ConfigProperties.getInstance().getGlobalXbrmsServer();
        final Map <String, XbrmsDto> map = ConfigProperties.getInstance().getXbrmsServers();

        if(global == null) {
            return;
        }

        final List<String> parks = new ArrayList<String>();

        if(map != null) {
            for(XbrmsDto m : map.values()) {
                if(!m.isGlobal()) {
                    parks.add(m.getUrl());
                }
            }
        }

        try {
            ((RestCall) XbrmsUtils.getRestCaller(global.getUrl())).getConnection().setConnectTimeout(3000);
            final XbrmsConfigDto conf = XbrmsUtils.getRestCaller(global.getUrl()).getXbrmsConfig();

            if(conf == null) {
                throw new RuntimeException();
            }

            conf.setGlobalServer(true);
            conf.setParksUrlList(parks);
            XbrmsUtils.getRestCaller(global.getUrl()).setXbrmsConfig(conf);
        }
        catch (Exception e) {
            final String warn = "Failed to notify global xBRMS about its role via URL " + global.getUrl();
            logger.warn(warn);
            notifiedOk = false;
        }
        finally {
            ((RestCall) XbrmsUtils.getRestCaller(global.getUrl())).getConnection().setConnectTimeout(RestCall.DEFAULT_CONNECTION_TO);
        }

        if(parks != null) {
            for(String park : parks) {
                try {
                    ((RestCall) XbrmsUtils.getRestCaller(park)).getConnection().setConnectTimeout(3000);
                    final XbrmsConfigDto conf = XbrmsUtils.getRestCaller(park).getXbrmsConfig();

                    if(conf == null) {
                        throw new RuntimeException();
                    }

                    conf.setGlobalServer(false);
                    conf.setParksUrlList(parks);
                    XbrmsUtils.getRestCaller(park).setXbrmsConfig(conf);
                }
                catch (Exception e) {
                    final String warn = "Failed to notify park xBRMS about its role via URL " + park;
                    logger.warn(warn);
                    notifiedOk = false;
                }
                finally {
                    ((RestCall) XbrmsUtils.getRestCaller(park)).getConnection().setConnectTimeout(RestCall.DEFAULT_CONNECTION_TO);
                }
            }
        }

        if(notifiedOk) {
            XbrmsUiServletContextListener.serversNotified = true;
        }
    }
}
