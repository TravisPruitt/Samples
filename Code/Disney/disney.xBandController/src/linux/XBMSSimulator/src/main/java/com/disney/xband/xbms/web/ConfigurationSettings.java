package com.disney.xband.xbms.web;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.NGEPropertiesDecoder;

public class ConfigurationSettings
{
	public static ConfigurationSettings INSTANCE = new ConfigurationSettings();
	
	private static Logger logger = Logger.getLogger(ConfigurationSettings.class);

	private static Properties properties = new Properties();
	
	private static String DATABASE_DRIVER_KEY = "xbms.sim.databasedriver";
	private static String DATABASE_URL_KEY = "xbms.sim.dbserver.url";
	private static String DATABASE_USER_KEY = "xbms.sim.dbserver.uid";
	private static String DATABASE_PASSWORD_KEY = "xbms.sim.dbserver.pwd";
	
	private static String JMS_CONNECTION_FACTORY_JNDI_NAME_KEY = "nge.eventserver.idms.connectionfactory.jndi.name";
	private static String JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
	private static String JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static String JMS_BROKER_USER_KEY = "xbms.sim.jms.uid";
	private static String JMS_BROKER_PWD_KEY = "xbms.sim.jms.pwd";
	private static String JMS_RETRY_PERIOD_KEY = "xbms.sim.jms.retryperiod";
	private static String JMS_MESSAGE_INTERVAL_KEY = "xbms.sim.jms.messageInterval";

	private static String CONFIG_MAX_POOLSIZE = "20";
	private static String CONFIG_MAX_STATEMENTS = "400";
	private static String CONFIG_MAX_STATEMENTS_PER_CONNECTION = "50";
	private static String CONFIG_JMS_RETRY_PERIOD_MSECS = "10000";
	private static String CONFIG_JMS_MESSAGE_INTERVAL_MSECS = "500";
	
	private ConfigurationSettings()
	{
	}

	static
	{
		// first, read the properties file to get the database parameters
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
				
		// get property settings
		// is there is a system property to identify where the environemtn.properites is
		String sPropFile = System.getProperty("environment.properties");
		if (sPropFile != null)
		{
			logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
			decoder.setPropertiesPath(sPropFile);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sJasyptPropFile != null)
		{
			logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
			decoder.setJasyptPropertiesPath(sJasyptPropFile);
		}

		try
		{
			decoder.initialize();
			properties = decoder.read();
			
			boolean missingPropertyFound = false;

			if (properties.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
				missingPropertyFound = true;
			}

			if (properties.getProperty(JMS_BROKER_URL_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_URL_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
				missingPropertyFound = true;
			}

			if (properties.getProperty(JMS_BROKER_DOMAIN_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_DOMAIN_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(JMS_BROKER_USER_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_USER_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(JMS_BROKER_PWD_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}

			// must have some properties
			if (properties.getProperty(DATABASE_DRIVER_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}

			if(properties.getProperty(DATABASE_URL_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}
			
			if(properties.getProperty(DATABASE_USER_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}
			
			if(properties.getProperty(DATABASE_PASSWORD_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}
			
			if(missingPropertyFound)
			{
				System.exit(1);
			}		
		}
		catch (Exception ex)
		{
			logger.fatal("!! Could not read the properties file [" + 
					decoder.getPropertiesPath() + "]: "
						 + ex.getLocalizedMessage());
		}
	}

	public String getDatabaseDriver()
	{
		return properties.getProperty(DATABASE_DRIVER_KEY);
	}

	public String getDatabaseURL()
	{
		return properties.getProperty(DATABASE_URL_KEY);
	}
	
	public String getDatabaseUser() 
	{
		return properties.getProperty(DATABASE_USER_KEY);
	}
	
	@XmlTransient
	public String getDatabasePassword() 
	{
		return properties.getProperty(DATABASE_PASSWORD_KEY);
	}
	
	public String getMaxPoolSize()
	{
		return CONFIG_MAX_POOLSIZE;
	}
	
	public String getMaxStatements()
	{
		return CONFIG_MAX_STATEMENTS;
	}
	
	public String getMaxStatementsPerConnection()
	{
		return CONFIG_MAX_STATEMENTS_PER_CONNECTION;
	}
	@XmlElement
	public String getConnectionFactoryJndiName()
	{
		return properties.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
	}
	
	@XmlElement
	public String getJmsBrokerUrl()
	{
		return properties.getProperty(JMS_BROKER_URL_KEY);
	}

	@XmlElement
	public String getJmsBrokerDomain()
	{
		return properties.getProperty(JMS_BROKER_DOMAIN_KEY);
	}

	@XmlElement
	public String getJmsUser()
	{
		return properties.getProperty(JMS_BROKER_USER_KEY);
	}

	@XmlTransient
	public String getJmsPassword()
	{
		return properties.getProperty(JMS_BROKER_PWD_KEY);
	}
	
	@XmlElement
	public int getJmsRetryPeriod() 
	{
		if (properties.getProperty(JMS_RETRY_PERIOD_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(JMS_RETRY_PERIOD_KEY));
		}

		return Integer.parseInt(CONFIG_JMS_RETRY_PERIOD_MSECS);
	}

	@XmlElement
	public int getJmsMessageInterval() 
	{
		if (properties.getProperty(JMS_MESSAGE_INTERVAL_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(JMS_MESSAGE_INTERVAL_KEY));
		}

		return Integer.parseInt(CONFIG_JMS_MESSAGE_INTERVAL_MSECS);
	}
}
