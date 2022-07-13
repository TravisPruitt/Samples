package com.disney.xband.jmslistener.configuration;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.NGEPropertiesDecoder;

@XmlRootElement(name="configuration")
@XmlType(propOrder={"cacheExpirationMinutes","cacheSize","connectionFactoryJndiName","discoveryJmsBrokerDomain",
		"discoveryJmsBrokerUrl","discoveryJmsTopic","discoveryJmsUser","discoveryRetryPeriod","pingInterval","socketTimeout","httpPort",
		"httpsPort","SSLClientKeystore","SSLServerKeystore","useSSL", "messageProcessingInterval", 
		"retryAttempts", "retryPeriodMsecs", "assemblyRootUrl",
		"gffListenerConfiguration","idmsListenerConfiguration",
		"xiListenerConfiguration"})
public class ConfigurationProperties
{
	private static Logger logger = Logger.getLogger(ConfigurationProperties.class);

	public static final ConfigurationProperties INSTANCE = new ConfigurationProperties();
	
	private static Properties properties;
	
	private static String CACHE_EXPIRATION_MINUTE_KEY = "nge.xconnect.jmslistener.cacheExpriationMinutes";
	private static String CACHE_SIZE_KEY = "nge.xconnect.jmslistener.cacheSize";
	private static String USE_SSL_KEY = "nge.xconnect.jmslistener.usessl";
	private static String HTTP_PORT_KEY = "nge.xconnect.jmslistener.httpPort";
	private static String HTTPS_PORT_KEY = "nge.xconnect.jmslistener.httpsPort";

	private static String JMS_CONNECTION_FACTORY_JNDI_NAME_KEY = "nge.eventserver.idms.connectionfactory.jndi.name";
	private static String DISCOVERY_JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
	private static String DISCOVERY_JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static String DISCOVERY_JMS_BROKER_USER_KEY = "nge.eventserver.jmslistener.uid";
	private static String DISCOVERY_JMS_BROKER_PWD_KEY = "nge.eventserver.jmslistener.pwd";
	private static String DISCOVERY_JMS_BROKER_TOPIC_KEY = "nge.xconnect.jmslistener.discovery.topic";
	private static String DISCOVERY_RETRY_PERIOD_KEY = "nge.xconnect.jmslistener.discovery.retryperiod";
	
	private static String SSL_SERVER_KEYSTORE_KEY = "nge.xconnect.jmslisetner.ssl.keystore.server";
	private static String SSL_SERVER_KEYSTORE_PASSWORD_KEY = "nge.xconnect.jmslistener.ssl.keystore.client.pwd";
	private static String SSL_CLIENT_KEYSTORE_KEY = "nge.xconnect.jmslistener.ssl.keystore.client";
	private static String SSL_CLIENT_KEYSTORE_PASSWORD_KEY = "nge.xconnect.jmslistener.ssl.keystore.server.pwd";
	
	private static String RETRY_ATTEMPTS_KEY = "nge.xconnect.jmslistener.retry.attempts";
	private static String RETRY_PERIOD_MSECS_KEY = "nge.xconnect.jmslistener.retry.msec";
	private static String PROCESSING_INTERVAL_MSECS_KEY = "nge.xconnect.jmslistener.processing.interval";
	private static String PING_INTERVAL_KEY = "nge.xconnect.jmslistener.ping.interval";
	private static String SOCKET_TIMEOUT_KEY = "nge.xconnect.jmslistener.sokcet.timeout";
	
	private static String ASSEMBLY_ROOT_URL_KEY = "nge.appserver.ngeapi.assemblyRootURL";

	private static String CONFIG_HTTP_PORT = "8081";
	private static String CONFIG_HTTPS_PORT = "8009";
	private static String CONFIG_CACHE_EXPIRATION_MINUTES = "60"; //1 Hour
	private static String CONFIG_CACHE_SIZE = "10000"; 
	private static String CONFIG_RETRY_ATTEMPTS = "3";
	private static String CONFIG_RETRY_PERIOD_MSECS = "10000";
	private static String CONFIG_PROCESSING_INTERVAL_MSECS = "500";
	private static String CONFIG_DISCOVERY_RETRY_PERIOD_MSECS = "60000";
	private static String CONFIG_PING_INTERVAL_SECS = "5";
	private static String CONFIG_SOCKET_TIMEOUT_MSECS = "5000";
	private static String CONFIG_ASSEMBLY_ROOT_URL = "#";
	
	private static IdmsListenerConfiguration idmsListenerConfiguration;
	private static GffListenerConfiguration gffListenerConfiguration;
	private static XiListenerConfiguration xiListenerConfiguration;
	
	

	static
	{
		// first, read the properties file to get the database parameters
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
				
		// get property settings
		// is there is a system property to identify where the environment.properites is
		String sPropFile = System.getProperty("environment.properties");
		if (sPropFile != null)
		{
			logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
			decoder.setPropertiesPath(sPropFile);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sJasyptPropFile != null)
		{
			logger.info("The jasypt.properties java argument is set. Using the " + sJasyptPropFile + " properties file.");
			decoder.setJasyptPropertiesPath(sJasyptPropFile);
		}

		try
		{
			decoder.initialize();
			properties = decoder.read();
			
			boolean missingPropertyFound = false;
			
			//properties.getProperty("nge.xconnect.xi.dbserver.password");
			
			if (properties.getProperty(DISCOVERY_JMS_BROKER_URL_KEY) == null)
			{
				logger.fatal("!! Missing property: " + DISCOVERY_JMS_BROKER_URL_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY) == null)
			{
				logger.fatal("!! Missing property: " + JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
				missingPropertyFound = true;
			}

			if (properties.getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY) == null)
			{
				logger.fatal("!! Missing property: " + DISCOVERY_JMS_BROKER_DOMAIN_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(DISCOVERY_JMS_BROKER_USER_KEY) == null)
			{
				logger.fatal("!! Missing property: " + DISCOVERY_JMS_BROKER_USER_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(DISCOVERY_JMS_BROKER_PWD_KEY) == null)
			{
				logger.fatal("!! Missing property: " + DISCOVERY_JMS_BROKER_PWD_KEY);
				missingPropertyFound = true;
			}
			
			if (properties.getProperty(DISCOVERY_JMS_BROKER_TOPIC_KEY) == null)
			{
				logger.fatal("!! Missing property: " + DISCOVERY_JMS_BROKER_TOPIC_KEY);
				missingPropertyFound = true;
			}
			

			idmsListenerConfiguration = new IdmsListenerConfiguration();
			gffListenerConfiguration = new GffListenerConfiguration();
			xiListenerConfiguration = new  XiListenerConfiguration();

			idmsListenerConfiguration.setProperties(properties);
			gffListenerConfiguration.setProperties(properties);
			xiListenerConfiguration.setProperties(properties);
			
			if(idmsListenerConfiguration.hasPropertiesMissing() ||
			   gffListenerConfiguration.hasPropertiesMissing() ||
			   xiListenerConfiguration.hasPropertiesMissing()	||
			   missingPropertyFound)
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
	
	private ConfigurationProperties()
	{
		
	}
	
	@XmlElement
	public IdmsListenerConfiguration getIdmsListenerConfiguration()
	{
		return idmsListenerConfiguration;
	}
	
	@XmlElement
	public GffListenerConfiguration getGffListenerConfiguration()
	{
		return gffListenerConfiguration;
	}

	@XmlElement
	public XiListenerConfiguration getXiListenerConfiguration()
	{
		return xiListenerConfiguration;
	}

	@XmlElement
	public String getConnectionFactoryJndiName()
	{
		return properties.getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
	}

	@XmlElement
	public String getDiscoveryJmsBrokerUrl()
	{
		return properties.getProperty(DISCOVERY_JMS_BROKER_URL_KEY);
	}

	@XmlElement
	public String getDiscoveryJmsBrokerDomain()
	{
		return properties.getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY);
	}

	@XmlElement
	public String getDiscoveryJmsUser()
	{
		return properties.getProperty(DISCOVERY_JMS_BROKER_USER_KEY);
	}

	@XmlTransient
	public String getDiscoveryJmsPassword()
	{
		return properties.getProperty(DISCOVERY_JMS_BROKER_PWD_KEY);
	}
	
	@XmlElement
	public String getDiscoveryJmsTopic()
	{
		return properties.getProperty(DISCOVERY_JMS_BROKER_TOPIC_KEY);
	}
	
	@XmlElement
	public int getDiscoveryRetryPeriod() 
	{
		if (properties.getProperty(DISCOVERY_RETRY_PERIOD_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(DISCOVERY_RETRY_PERIOD_KEY));
		}

		return Integer.parseInt(CONFIG_DISCOVERY_RETRY_PERIOD_MSECS);
	}

	@XmlElement
	public boolean getUseSSL() 
	{
		if (properties.getProperty(USE_SSL_KEY) != null)
		{
			return Boolean.parseBoolean(properties.getProperty(USE_SSL_KEY));
		}

		return false;
	}

	@XmlElement
	public int getHttpPort()
	{
		if (properties.getProperty(HTTP_PORT_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(HTTP_PORT_KEY));
		}

		return Integer.parseInt(CONFIG_HTTP_PORT);
	}

	@XmlElement
	public int getHttpsPort() 
	{
		if (properties.getProperty(HTTPS_PORT_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(HTTPS_PORT_KEY));
		}

		return Integer.parseInt(CONFIG_HTTPS_PORT);
	}


	@XmlElement
	public int getCacheExpirationMinutes() 
	{
		if (properties.getProperty(CACHE_EXPIRATION_MINUTE_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(CACHE_EXPIRATION_MINUTE_KEY));
		}

		return Integer.parseInt(CONFIG_CACHE_EXPIRATION_MINUTES);
	}

	@XmlElement
	public int getCacheSize() 
	{
		if (properties.getProperty(CACHE_SIZE_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(CACHE_SIZE_KEY));
		}

		return Integer.parseInt(CONFIG_CACHE_SIZE);
	}

	@XmlElement
	public String getSSLServerKeystore() 
	{
		return properties.getProperty(SSL_SERVER_KEYSTORE_KEY);
	}

	@XmlTransient
	public String getSSLServerKeystorePassword() 
	{
		return properties.getProperty(SSL_SERVER_KEYSTORE_PASSWORD_KEY);
	}

	@XmlElement
	public String getSSLClientKeystore() 
	{
		return properties.getProperty(SSL_CLIENT_KEYSTORE_KEY);
	}

	@XmlTransient
	public String getSSLClientKeystorePassword() 
	{
		return properties.getProperty(SSL_CLIENT_KEYSTORE_PASSWORD_KEY);
	}


	@XmlElement
	public int getRetryAttempts() 
	{
		if (properties.getProperty(RETRY_ATTEMPTS_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(RETRY_ATTEMPTS_KEY));
		}

		return Integer.parseInt(CONFIG_RETRY_ATTEMPTS);
	}

	@XmlElement
	public int getRetryPeriodMsecs() 
	{
		if (properties.getProperty(RETRY_PERIOD_MSECS_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(RETRY_PERIOD_MSECS_KEY));
		}

		return Integer.parseInt(CONFIG_RETRY_PERIOD_MSECS);
	}

	@XmlElement
	public int getMessageProcessingInterval() 
	{
		if (properties.getProperty(PROCESSING_INTERVAL_MSECS_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(PROCESSING_INTERVAL_MSECS_KEY));
		}

		return Integer.parseInt(CONFIG_PROCESSING_INTERVAL_MSECS);
	}

	@XmlElement
	public int getPingInterval() 
	{
		if (properties.getProperty(PING_INTERVAL_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(PING_INTERVAL_KEY));
		}

		return Integer.parseInt(CONFIG_PING_INTERVAL_SECS);
	}

	@XmlElement
	public int getSocketTimeout() 
	{
		if (properties.getProperty(SOCKET_TIMEOUT_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(SOCKET_TIMEOUT_KEY));
		}

		return Integer.parseInt(CONFIG_SOCKET_TIMEOUT_MSECS);
	}

	@XmlElement
	public String getAssemblyRootUrl() 
	{
		if (properties.getProperty(ASSEMBLY_ROOT_URL_KEY) != null)
		{
			if(!properties.getProperty(ASSEMBLY_ROOT_URL_KEY).endsWith("/"))
			{
				return properties.getProperty(ASSEMBLY_ROOT_URL_KEY) + "/";
			}
			
			return properties.getProperty(ASSEMBLY_ROOT_URL_KEY);
		}

		return CONFIG_ASSEMBLY_ROOT_URL;
	}

	/*
	 * All the methods below are here for jaxb's benefit and they are meant to be private.
	 */
	@SuppressWarnings("unused")
	private void setIdmsListenerConfiguration(IdmsListenerConfiguration configuration)
	{
	}
	
	@SuppressWarnings("unused")
	private void setGffListenerConfiguration(GffListenerConfiguration configuration)
	{
	}

	@SuppressWarnings("unused")
	private void setXiListenerConfiguration(XiListenerConfiguration configuration)
	{
	}
	
	@SuppressWarnings("unused")
	private void setCacheExpirationMinutes(int i) 
	{
	}
	
	@SuppressWarnings("unused")
	private void setCacheSize(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryJmsBrokerUrl(String s)
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryJmsBrokerDomain(String s)
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryJmsUser(String s)
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryJmsPassword(String s)
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryJmsTopic(String s)
	{
	}

	@SuppressWarnings("unused")
	private void setDiscoveryRetryPeriod(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setUseSSL(boolean b) 
	{
	}

	@SuppressWarnings("unused")
	private void setHttpPort(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setHttpsPort(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setSSLServerKeystore(String s) 
	{
	}

	@SuppressWarnings("unused")
	private void setSSLClientKeystore(String s) 
	{
	}

	@SuppressWarnings("unused")
	private void setConnectionFactoryJndiName(String s)
	{
	}
	
	@SuppressWarnings("unused")
	private void setRetryAttempts(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setRetryPeriodMsecs(int i) 
	{
	}
	
	@SuppressWarnings("unused")
	private void setMessageProcessingInterval(int i) 
	{
	}
	
	@SuppressWarnings("unused")
	private void setPingInterval(int i) 
	{
	}
	
	@SuppressWarnings("unused")
	private void setSocketTimeout(int i) 
	{
	}

	@SuppressWarnings("unused")
	private void setAssemblyRootUrl(String s)
	{
	}
}
