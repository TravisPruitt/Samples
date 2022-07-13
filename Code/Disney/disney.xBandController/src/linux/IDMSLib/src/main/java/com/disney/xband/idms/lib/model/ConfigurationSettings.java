package com.disney.xband.idms.lib.model;

import java.util.Properties;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.NGEPropertiesDecoder;

public class ConfigurationSettings
{
	public static ConfigurationSettings INSTANCE = new ConfigurationSettings();
	
	private static Logger logger = Logger.getLogger(ConfigurationSettings.class);

	private static Properties prop = new Properties();
	
	private static String CONFIG_MAX_POOLSIZE = "20";
	private static String CONFIG_MAX_STATEMENTS = "400";
	private static String CONFIG_MAX_STATEMENTS_PER_CONNECTION = "50";
	
	private boolean restartIDMS;
	
	private boolean CONFIG_READONLY = false;
	
	private static boolean CONFIG_CACHE_ENABLED = false;
	private static String CONFIG_CACHE_MAX_GUESTS = "1000000";
	private static String CONFIG_CACHE_MAX_BANDS = "3000000";

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
			prop = decoder.read();
			
			// must have some properties
			if (prop.getProperty("nge.xconnect.idms.databasedriver") == null
				|| prop.getProperty("nge.xconnect.idms.dbserver.url") == null
				|| prop.getProperty("nge.xconnect.idms.dbserver.uid") == null
				|| prop.getProperty("nge.xconnect.idms.dbserver.pwd") == null)
			{
				logger.fatal("!! Error: environment.properties must contain, at least, settings for " +
						"nge.xconnect.idms.databasedriver, nge.xconnect.idms.dbserver.url, nge.xconnect.idms.dbserver.uid and nge.xconnect.idms.dbserver.pwd");
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
		return prop.getProperty("nge.xconnect.idms.databasedriver");
	}

	public String getDatabaseURL()
	{
		return prop.getProperty("nge.xconnect.idms.dbserver.url");
	}
	
	public String getDatabaseUser() 
	{
		return prop.getProperty("nge.xconnect.idms.dbserver.uid");
	}
	
	@XmlTransient
	public String getDatabasePassword() 
	{
		return prop.getProperty("nge.xconnect.idms.dbserver.pwd");
	}
	
	public String getMaxPoolSize()
	{
		if (prop.getProperty("nge.xconnect.idms.c3p0.maxPoolSize") != null)
		{
			return prop.getProperty("nge.xconnect.idms.c3p0.maxPoolSize");
		}

		return CONFIG_MAX_POOLSIZE;
	}
	
	public String getMaxStatements()
	{
		if (prop.getProperty("nge.xconnect.idms.c3p0.maxStatements") != null)
		{
			return prop.getProperty("nge.xconnect.idms.c3p0.maxStatements");
		}

		return CONFIG_MAX_STATEMENTS;
	}
	
	public String getMaxStatementsPerConnection()
	{
		if (prop.getProperty("nge.xconnect.idms.c3p0.maxStatementsPerConnection") != null)
		{
			return prop.getProperty("nge.xconnect.idms.c3p0.maxStatementsPerConnection");
		}

		return CONFIG_MAX_STATEMENTS_PER_CONNECTION;
	}

	public boolean isRestartIDMS() 
	{
		return this.restartIDMS;
	}
	
	public void setRestartIDMS(boolean restartIDMS) 
	{
		this.restartIDMS = restartIDMS;
	}
	
	public boolean isReadOnly()
	{
		String readOnly = prop.getProperty("nge.xconnect.idms.readonly");
		
		if (readOnly != null)
		{
			return Boolean.valueOf(readOnly);
		}
		return CONFIG_READONLY;
	}
	
	public boolean isCacheEnabled()
	{
		String cacheEnabled = prop.getProperty("nge.xconnect.idms.cache.enable");
		if (cacheEnabled != null)
		{
			return Boolean.valueOf(cacheEnabled);
		}
		return CONFIG_CACHE_ENABLED;
	}

	public String getCacheMaxGuests()
	{
		if (prop.getProperty("nge.xconnect.idms.cache.maxGuests") != null)
		{
			return prop.getProperty("nge.xconnect.idms.cache.maxGuests");
		}
		return CONFIG_CACHE_MAX_GUESTS;
	}	
	
	public String getCacheMaxBands()
	{
		if (prop.getProperty("nge.xconnect.idms.cache.maxBands") != null)
		{
			return prop.getProperty("nge.xconnect.idms.cache.maxBands");
		}
		return CONFIG_CACHE_MAX_BANDS;
	}
	
	public String getCacheResetTime()
	{
		return prop.getProperty("nge.xconnect.idms.cache.resetTime");
	}
}
