package com.disney.xband.jmslistener.configuration;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.jmslistener.JmsService;
import org.apache.log4j.Logger;

public class XiListenerConfiguration  implements JmsServiceConfiguration, ConnectionPoolConfiguration
{
	private static Logger logger = Logger.getLogger(XiListenerConfiguration.class);

	private Properties properties;
    private String[] acceptedEventTypes;
	
	private static String XI_LISTENER_DATABASE_SERVICE_KEY = "nge.dbserver.xilistener.service";
	private static String XI_LISTENER_DATABASE_USER_KEY = "nge.dbserver.xilistener.uid";
	private static String XI_LISTENER_DATABASE_PWD_KEY = "nge.dbserver.xilistener.pwd";
	private static String XI_LISTENER_JMS_RETRY_PERIOD_KEY = 
			"nge.xconnect.xilistener.jms.retryperiod";

	private static String XI_LISTENER_XBRC_TOPIC_KEY = 
			"nge.xconnect.xilistener.xbrc.topic";
	private static String XI_LISTENER_XPASS_TOPIC_KEY = 
			"nge.xconnect.xilistener.xpass.topic";
	private static String XI_LISTENER_BLUE_LANE_TOPIC_KEY = 
			"nge.xconnect.xilistener.bluelane.topic";
	private static String XI_LISTENER_XPASS_REDEEM_TOPIC_KEY = 
			"nge.xconnect.xilistener.xpassredeem.topic";

	private static String XI_LISTENER_JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
	private static String XI_LISTENER_JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static String XI_LISTENER_JMS_BROKER_USER_KEY = "nge.eventserver.xilistener.uid";
	private static String XI_LISTENER_JMS_BROKER_PWD_KEY = "nge.eventserver.xilistener.pwd";
	private static String XI_LISTENER_SHARED_CONFIGURATIONS_KEY = 
			"nge.xconnect.xilistener.sharedconfigcount";
	private static String XI_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY = "nge.xconnect.xilistener.shared.subscription.group";

	private static String XI_LISTENER_GXP_CALLBACK_ROOTURL_KEY = 
			"nge.xconnect.xilistener.gxpcallback.rooturl";

	private static String XI_LISTENER_IDMS_ROOTURL_KEY = "nge.xconnect.xilistener.idms.rooturl";

    private static String XI_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY ="nge.xconnect.xilistener.notraffictolerancesecs";

	private static String XI_LISTENER_CONFIG_SHARED_CONFIGURATIONS = "10";
	private static String XI_LISTENER_SHARED_SUBSCRIPTION_GROUP = "[[XiListenerGroup]]";

	private static int XI_LISTENER_JMS_RETRY_PERIOD = 10000;

    private static String XI_LISTENER_ACCEPTED_EVENT_TYPES = "nge.xconnect.xilistener.redemption.acceptedeventtypes";

	public XiListenerConfiguration()
	{
	}

	@Override
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}
	
	@Override
	@XmlElement
	public String getDatabaseDriver()
	{
		return "net.sourceforge.jtds.jdbc.Driver";
	}

	@Override
	@XmlElement
	public String getDatabaseService()
	{
		return properties.getProperty(XI_LISTENER_DATABASE_SERVICE_KEY);
	}

	@Override
	@XmlElement
	public String getDatabaseUser()
	{
		return properties.getProperty(XI_LISTENER_DATABASE_USER_KEY);
	}

	@Override
	@XmlTransient
	public String getDatabasePassword()
	{
		return properties.getProperty(XI_LISTENER_DATABASE_PWD_KEY);
	}

	@Override
	@XmlElement
	public int getMaxPoolSize()
	{
		return this.getSharedConfigurations();
	}

	@Override
	@XmlElement
	public String getMaxStatements()
	{
		return String.valueOf(this.getSharedConfigurations() * 5);
	}

	@Override
	@XmlElement
	public String getMaxStatementsPerConnection()
	{
		return String.valueOf(this.getSharedConfigurations() * 5);
	}

	@Override
	@XmlElement
	public String getJmsBrokerUrl()
	{
		return properties.getProperty(XI_LISTENER_JMS_BROKER_URL_KEY);
	}

	@Override
	@XmlElement
	public String getJmsBrokerDomain()
	{
		return properties.getProperty(XI_LISTENER_JMS_BROKER_DOMAIN_KEY);
	}

	@Override
	@XmlElement
	public String getJmsUser()
	{
		return properties.getProperty(XI_LISTENER_JMS_BROKER_USER_KEY);
	}

	@Override
	@XmlTransient
	public String getJmsPassword()
	{
		return properties.getProperty(XI_LISTENER_JMS_BROKER_PWD_KEY);
	}

	@Override
	@XmlElement
	public int getSharedConfigurations()
	{
		if (properties.getProperty(XI_LISTENER_SHARED_CONFIGURATIONS_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(XI_LISTENER_SHARED_CONFIGURATIONS_KEY));
		}

		return Integer.parseInt(XI_LISTENER_CONFIG_SHARED_CONFIGURATIONS);
	}

	@XmlElement
	@Override
	public String getSharedConfigurationGroup()
	{
		if (properties.getProperty(XI_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY ) != null)
		{
			return properties
					.getProperty(XI_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY);
		}
		
		return XI_LISTENER_SHARED_SUBSCRIPTION_GROUP;
	}

	@Override
	@XmlElement
	public int getJmsRetryPeriod()
	{
		if (properties.getProperty(XI_LISTENER_JMS_RETRY_PERIOD_KEY) != null)
		{
			return Integer.parseInt(properties.getProperty(XI_LISTENER_JMS_RETRY_PERIOD_KEY));
		}

		return XI_LISTENER_JMS_RETRY_PERIOD;
	}
	
    @XmlElement
    @Override
    public long getNoTrafficToleranceSecs()
    {
        try {
            return Long.parseLong(properties.getProperty(XI_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY)) * 1000;
        }
        catch (Exception e) {
            return JmsService.DEFAULT_NO_TRAFFIC_TOLERANCE_SECS * 1000;
        }
    }

    public String[] getAcceptedEventTypes() {
        if(this.acceptedEventTypes == null) {
            final String types = properties.getProperty(XI_LISTENER_ACCEPTED_EVENT_TYPES);

            if (types == null) {
                this.acceptedEventTypes = new String[] {"XPASS.CHANGE.PROPERTIES"};
            }
            else {
                this.acceptedEventTypes = types.split(",");

                if((acceptedEventTypes == null) || (acceptedEventTypes.length == 0)) {
                    this.acceptedEventTypes = new String[] {"XPASS.CHANGE.PROPERTIES"};
                }
            }

            for(int i = 0; i < this.acceptedEventTypes.length; ++i) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Registering event type with the redemption event listener: " + this.acceptedEventTypes);
                }

                this.acceptedEventTypes[i] = "<eventType>" + this.acceptedEventTypes;
            }
        }

        return this.acceptedEventTypes;
    }

	@Override
	public boolean hasPropertiesMissing()
	{
		boolean missingPropertyFound = false;

		if (properties.getProperty(XI_LISTENER_JMS_BROKER_URL_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_JMS_BROKER_URL_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(XI_LISTENER_JMS_BROKER_DOMAIN_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_JMS_BROKER_DOMAIN_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(XI_LISTENER_JMS_BROKER_USER_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_JMS_BROKER_USER_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(XI_LISTENER_JMS_BROKER_PWD_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_JMS_BROKER_PWD_KEY);
			missingPropertyFound = true;
		}
		
		if (properties.getProperty(XI_LISTENER_GXP_CALLBACK_ROOTURL_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_GXP_CALLBACK_ROOTURL_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(XI_LISTENER_IDMS_ROOTURL_KEY) == null)
		{
			logger.fatal("!! Missing property: " + XI_LISTENER_IDMS_ROOTURL_KEY);
			missingPropertyFound = true;
		}

		return missingPropertyFound;
	}
	
	@XmlElement
	public String getGxpCallbackRootUrl()
	{
		return properties.getProperty(XI_LISTENER_GXP_CALLBACK_ROOTURL_KEY);
	}

	@XmlElement
	public String getIdmsRootUrl()
	{
		return properties.getProperty(XI_LISTENER_IDMS_ROOTURL_KEY);
	}

	@XmlElement
	public String getXbrcTopic()
	{
		return properties.getProperty(XI_LISTENER_XBRC_TOPIC_KEY);
	}
	
	@XmlElement
	public String getXpassTopic()
	{
		return properties.getProperty(XI_LISTENER_XPASS_TOPIC_KEY);
	}

	@XmlElement
	public String getBlueLaneTopic()
	{
		return properties.getProperty(XI_LISTENER_BLUE_LANE_TOPIC_KEY);
	}

	@XmlElement
	public String getXpassRedeemTopic()
	{
		return properties.getProperty(XI_LISTENER_XPASS_REDEEM_TOPIC_KEY);
	}
}
