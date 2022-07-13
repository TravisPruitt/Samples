package com.disney.xband.xbrms.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.disney.xband.common.lib.audit.AuditFactory;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.IAuditConnectionPool;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache;
import com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCacheConfig;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.SchedulerItemParameterMetadata;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.server.messaging.JMSAgent;
import com.disney.xband.xbrms.server.messaging.publisher.XbrmsConfigurationUpdatePublisher;
import com.disney.xband.xbrms.server.messaging.publisher.XbrmsStatusRefreshPublisher;
import com.disney.xband.xbrms.server.messaging.subscriber.XbrmsConfigurationUpdateSubscriber;
import com.disney.xband.xbrms.server.messaging.subscriber.XbrmsStatusRefreshSubscriber;
import com.disney.xband.xbrms.server.model.MetaDao;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;
import com.disney.xband.xbrms.server.scheduler.XbrmsSchedulerHelper;
import com.disney.xband.xbrms.server.scheduler.XbrmsSchedulerSerializer;

public class XbrmsServletContextListener implements ServletContextListener
{
	private static Logger logger = Logger.getLogger(XbrmsServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent context) {
		SystemHealthConsumer.getInstance().stopStatusThread();
		XbrmsStatusBo.getInstance().stopTimer();

		// stop listening/sending jms messages
		JMSAgent.getInstance().stop();

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements())
		{
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				System.out.println("Failed to deregister sql driver.");
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
        //try { Thread.sleep(10000); } catch(Exception e) { }

		logger.info("Initializing xBRMS ...");

        try
        {
            ConfigProperties.init(true);
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
			logger.fatal("!! Failed to start xBRMS. Failed to read the properties file !!");
			System.exit(1);
		}

        boolean isDatabaseConnectionProblem = false;

		// initialize database connection pool
		try
		{
			SSConnectionPool.getInstance().initialize();
		}
		catch (IllegalArgumentException e)
		{
			logger.fatal("!! Failed to start xBRMS. " + e.getLocalizedMessage() + ".!!");
			System.exit(1);
		}
		catch(ClassNotFoundException e) {
			logger.fatal("!! Failed to start xBRMS. Could not find the sql driver class. Failed to initialize connection pool.!!", e);
			System.exit(1);
		}
		catch(SQLException e)
		{
            isDatabaseConnectionProblem = true;

            String reason = SSConnectionPool.handleSqlException(e, "Attempting to initialize database connection pool");

            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ConnectToXbrmsDb, reason, e);
            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Red, reason);
		}

		// read the rest of configuration options from the database
		try
		{
			ConfigProperties.getInstance().readConfigurationOptionsFromDatabase();
		}
		catch (ClassNotFoundException e)
		{
			logger.fatal("!! Failed to start xBRMS. JDBC driver not found. !!", e);
			System.exit(1);
		}
		catch(SQLException e)
		{
            isDatabaseConnectionProblem = true;

            String reason = SSConnectionPool.handleSqlException(e, "Trying to read configuration properties from the database with URL: "
																	+ ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_URL)
																	+ ", user: " + ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_USER)
																	+ " on startup");

            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ConnectToXbrmsDb, reason, e);
            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Red, reason);

		}
		catch (IllegalAccessException e)
		{
			logger.fatal("!! Failed to start xBRMS. Failed to read configuration properties from the database. !!", e);
			System.exit(1);
		}

        XbrmsServletContextListener.initializeAudit(ConfigProperties.getInstance());

		// initialize xbrms status
		try
		{
			XbrmsStatusBo.getInstance().initialize(logger, context.getServletContext(), true);
		}
		catch (Exception e)
		{
            if(isDatabaseConnectionProblem) {
                logger.error("Failed to start xBRMS. Could not initialize xbrms status object.");
            }
            else {
			    logger.fatal("!! Failed to start xBRMS. Could not initialize xbrms status object. !!", e);
			    System.exit(1);
            }
		}
		
		// try to become master
		boolean master = false;
		try
		{
			master = MetaDao.amIMaster(XbrmsConfigBo.getInstance().getDto().getMasterPronouncedDeadAfter_sec());
			if (master)
				logger.info("This xBRMS became the master at startup.");
			else 
				logger.info("This xBRMS is did not become the master at startup.");
		}
		catch (Exception e)
		{
			logger.error("Detected errors while trying to become the master.");
		}
		
		// connect to jms broker to send and receive jms messages
		try
		{
			boolean readyToRNR = JMSAgent.getInstance().initialize();
			if (master && readyToRNR)
				JMSAgent.getInstance().start();

			if (!JMSAgent.getInstance().isInitializedCorrectly())
			{
				logger.info("JMS Agent has not been started. xBRMS will start, but not all xBRMS functionality will be available !!");
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to start xBRMS. The JMSAgent failed to connect to the broker." + e.getLocalizedMessage() + ".!!", e);
		}

		// start discovery consumer
		SystemHealthConsumer.getInstance().startStatusThread();

		// initialize the publisher/subscriber mechanism
		//TODO annotate publishers and subscribers and use reflection to initialize all of this
		try
		{
			// TODO sample, remove or enable as needed
			XbrmsConfigurationUpdatePublisher.getInstance().register(
					new XbrmsConfigurationUpdateSubscriber(XbrmsConfigurationUpdatePublisher.PUBLISH_EVENT_TYPE)
			);
			XbrmsStatusRefreshPublisher.getInstance().register(
					new XbrmsStatusRefreshSubscriber(XbrmsStatusRefreshPublisher.PUBLISH_EVENT_TYPE)
			);
		}
		catch (Exception e)
		{
			logger.error("Failed to register some or all subscribers.");
		}

        ConfigProperties.getInstance().initXbrmsServers();
        
        initializeScheduler();

		logger.info("XBRMS has been started.");
	}

    public static void initializeAudit(final Properties props) {
        //try {Thread.sleep(10000);}catch(Exception e) {}
        final AuditConfig currentConfig = Auditor.getInstance().getConfig();
        final AuditConfig newConfig = new AuditConfig();

        Connection conn = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            try {
                if(!newConfig.populateAuditConfigFromDb(conn)) {
                    return;
                }
            }
            catch(Exception e) {
                logger.error("Failed to initialize audit subsystem");
                return;
            }
        }
        catch(Exception e) {
            logger.error("Failed to database connection for audit subsystem: " + e.getMessage());
            return;
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }

        if(currentConfig.getImplStack().indexOf("AuditSqlServerDbImpl") < 0) {
            XbrmsServletContextListener.initializeAudit(props, newConfig);
        }
        else {
            // In case the application ID has changed
            newConfig.setAppId(XbrmsConfigBo.getInstance().getDto().getId());

            if(!newConfig.isEqual(currentConfig)) {
                XbrmsServletContextListener.initializeAudit(props, currentConfig);
            }
        }
    }

    private static void initializeAudit(final Properties props, final AuditConfig config) 
    {
        config.setImplStack("com.disney.xband.common.lib.audit.providers.AuditSqlServerDbImpl");

        config.setConnectionPool(new IAuditConnectionPool() {
            @Override
            public Connection getConnection() throws Exception {
                return SSConnectionPool.getInstance().getConnection();
            }

            @Override
            public void releaseConnection(Connection conn) {
                SSConnectionPool.getInstance().releaseConnection(conn);
            }
        });

        config.setAppClass(AuditEvent.AppClass.XBRMS.toString());

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
        Auditor.getInstance().setEventsProvider(auditFactory.getAuditControl().getEventsProvider("AuditSqlServerDbImpl"));
        Auditor.getInstance().setConfig(config);
        Auditor.getInstance().setAuditFactory(auditFactory);

        final LogonCacheConfig lcc = new LogonCacheConfig("com.disney.xband.common.lib.audit.interceptors.pwcache.LogonCache", props);
        final LogonCache lc = new LogonCache();
        lc.init(lcc, config);

        auditFactory.getAuditControl().addInterceptor(lc, lcc);

    }
    
    private void initializeScheduler()
	{
		try 
		{
			XconnectScheduler.getInstance().initialize(ConfigProperties.getInstance(), new XbrmsSchedulerSerializer());
		} 
		catch (SchedulerException e) 
		{
			logger.error("Failed to initialize the scheduler. No scheduled task will run.", e);
			return;
		}
		
		try 
		{
			XbrmsSchedulerHelper.registerClasses(XconnectScheduler.getInstance());
		} 
		catch (Exception e) 
		{
			logger.error("Failed to register scheduler tasks metadata. Some scheduled task may not run.", e);
		}
		
		try 
		{
			XconnectScheduler.getInstance().loadItems();
		} 
		catch (Exception e) 
		{
			logger.error("Failed to load scheduled items from the xBRMS database.", e);
		}
		
		try 
		{
			XconnectScheduler.getInstance().start();
		} 
		catch (Exception e) 
		{
			logger.error("Failed to start the scheduler.", e);
		}
	}
}
