package com.disney.xband.jmslistener.configuration;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.jmslistener.JmsService;
import org.apache.log4j.Logger;

public class GffListenerConfiguration implements JmsServiceConfiguration, ConnectionPoolConfiguration
{
	private static Logger logger = Logger.getLogger(GffListenerConfiguration.class);

	private Properties properties;
	
	private static String GFF_LISTENER_DATABASE_SERVICE_KEY = "nge.dbserver.gfflistener.service";
	private static String GFF_LISTENER_DATABASE_USER_KEY = "nge.dbserver.gfflistener.uid";
	private static String GFF_LISTENER_DATABASE_PWD_KEY = "nge.dbserver.gfflistener.pwd";
	
	private static String GFF_LISTENER_JMS_RETRY_PERIOD_KEY = 
			"nge.xconnect.gfflistener.jms.retryperiod";

	private static String GFF_LISTENER_POINTOFSALE_TOPIC_KEY = "nge.xconnect.gfflistener.pointofsale.topic";
    private static String GFF_LISTENER_TABLEMANAGEMENT_TOPIC_KEY = "nge.xconnect.gfflistener.tablemanagement.topic";
    private static String GFF_LISTENER_KITCHENMANAGEMENT_TOPIC_KEY = "nge.xconnect.gfflistener.kitchenmanagement.topic";
    private static String GFF_LISTENER_XPASSVENUE_TOPIC_KEY ="nge.xconnect.gfflistener.xpassvenue.topic";

    private static String GFF_LISTENER_JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
    private static String GFF_LISTENER_JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static String GFF_LISTENER_JMS_BROKER_USER_KEY = "nge.eventserver.gfflistener.uid";
	private static String GFF_LISTENER_JMS_BROKER_PWD_KEY = "nge.eventserver.gfflistener.pwd";
	private static String GFF_LISTENER_SHARED_CONFIGURATIONS_KEY = 
			"nge.xconnect.gfflistener.sharedconfigcount";
	private static String GFF_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY = "nge.xconnect.gfflistener.shared.subscription.group";
	
	private static String GFF_LISTENER_IDMS_ROOTURL_KEY = "nge.xconnect.gfflistener.idms.rooturl";

    private static String GFF_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY ="nge.xconnect.gfflistener.notraffictolerancesecs";

	private static String GFF_LISTENER_CONFIG_SHARED_CONFIGURATIONS = "10";
    private static String GFF_LISTENER_SHARED_SUBSCRIPTION_GROUP = "[[GFFListenerGroup]]";

	private static int GFF_LISTENER_JMS_RETRY_PERIOD = 10000;

	public GffListenerConfiguration()
	{
	}
	
	@Override
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}
	
	@XmlElement
	@Override
	public String getDatabaseDriver()
	{
		return "net.sourceforge.jtds.jdbc.Driver";
	}

	@XmlElement
	@Override
	public String getDatabaseService()
	{
		return properties.getProperty(GFF_LISTENER_DATABASE_SERVICE_KEY);
	}

	@XmlElement
	@Override
	public String getDatabaseUser()
	{
		return properties.getProperty(GFF_LISTENER_DATABASE_USER_KEY);
	}

	@XmlTransient
	@Override
	public String getDatabasePassword()
	{
		return properties.getProperty(GFF_LISTENER_DATABASE_PWD_KEY);
	}

	@XmlElement
	@Override
	public int getMaxPoolSize()
	{
		return this.getSharedConfigurations();
	}

	@XmlElement
	@Override
	public String getMaxStatements()
	{
		return String.valueOf(this.getSharedConfigurations() * 5);
	}

	@XmlElement
	@Override
	public String getMaxStatementsPerConnection()
	{
		return String.valueOf(this.getSharedConfigurations() * 5);
	}

	@XmlElement
	@Override
	public String getJmsBrokerUrl()
	{
		return properties.getProperty(GFF_LISTENER_JMS_BROKER_URL_KEY);
	}

	@XmlElement
	@Override
	public String getJmsBrokerDomain()
	{
		return properties.getProperty(GFF_LISTENER_JMS_BROKER_DOMAIN_KEY);
	}

	@XmlElement
	@Override
	public String getJmsUser()
	{
		return properties.getProperty(GFF_LISTENER_JMS_BROKER_USER_KEY);
	}

	@XmlTransient
	@Override
	public String getJmsPassword()
	{
		return properties.getProperty(GFF_LISTENER_JMS_BROKER_PWD_KEY);
	}


	@XmlElement
	@Override
	public int getSharedConfigurations()
	{
		if (properties.getProperty(GFF_LISTENER_SHARED_CONFIGURATIONS_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(GFF_LISTENER_SHARED_CONFIGURATIONS_KEY));
		}

		return Integer.parseInt(GFF_LISTENER_CONFIG_SHARED_CONFIGURATIONS);
	}

	@XmlElement
	@Override
	public String getSharedConfigurationGroup()
	{
		if (properties.getProperty(GFF_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY ) != null)
		{
			return properties
					.getProperty(GFF_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY);
		}
		
		return GFF_LISTENER_SHARED_SUBSCRIPTION_GROUP;
	}

	@XmlElement
	@Override
	public int getJmsRetryPeriod()
	{
		if (properties.getProperty(GFF_LISTENER_JMS_RETRY_PERIOD_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(GFF_LISTENER_JMS_RETRY_PERIOD_KEY));
		}

		return GFF_LISTENER_JMS_RETRY_PERIOD;
	}

	@XmlElement
	public String getIdmsRootUrl()
	{
		return properties.getProperty(GFF_LISTENER_IDMS_ROOTURL_KEY);
	}

	@XmlElement
	public String getPointOfSaleTopic()
	{
		return properties.getProperty(GFF_LISTENER_POINTOFSALE_TOPIC_KEY);
	}
	
	@XmlElement
	public String getTableManagementTopic()
	{
		return properties.getProperty(GFF_LISTENER_TABLEMANAGEMENT_TOPIC_KEY);
	}

	@XmlElement
	public String getKitchenManagementTopic()
	{
		return properties.getProperty(GFF_LISTENER_KITCHENMANAGEMENT_TOPIC_KEY);
	}

	@XmlElement
	public String getXpassVenueTopic()
	{
		return properties.getProperty(GFF_LISTENER_XPASSVENUE_TOPIC_KEY);
	}

    @XmlElement
    @Override
    public long getNoTrafficToleranceSecs()
    {
        try {
            return Long.parseLong(properties.getProperty(GFF_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY)) * 1000;
        }
        catch (Exception e) {
            return JmsService.DEFAULT_NO_TRAFFIC_TOLERANCE_SECS * 1000;
        }
    }

	@Override
	public boolean hasPropertiesMissing()
	{
		boolean missingPropertyFound = false;
		
		if (properties.getProperty(GFF_LISTENER_DATABASE_SERVICE_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_DATABASE_SERVICE_KEY);
			missingPropertyFound = true;
		}
		if (properties.getProperty(GFF_LISTENER_DATABASE_USER_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_DATABASE_USER_KEY);
			missingPropertyFound = true;
		}
		if (properties.getProperty(GFF_LISTENER_DATABASE_PWD_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_DATABASE_PWD_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(GFF_LISTENER_JMS_BROKER_URL_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_JMS_BROKER_URL_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_JMS_BROKER_DOMAIN_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_JMS_BROKER_DOMAIN_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_JMS_BROKER_USER_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_JMS_BROKER_USER_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_JMS_BROKER_PWD_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_JMS_BROKER_PWD_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_IDMS_ROOTURL_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_IDMS_ROOTURL_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_POINTOFSALE_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_POINTOFSALE_TOPIC_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_TABLEMANAGEMENT_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_TABLEMANAGEMENT_TOPIC_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_KITCHENMANAGEMENT_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_KITCHENMANAGEMENT_TOPIC_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(GFF_LISTENER_XPASSVENUE_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: " + GFF_LISTENER_XPASSVENUE_TOPIC_KEY);
			missingPropertyFound = true;
		}

	    return missingPropertyFound;
	}

	
	@SuppressWarnings("unused")
	private void setSharedConfigurationGroup(String s)
	{
	}
}
