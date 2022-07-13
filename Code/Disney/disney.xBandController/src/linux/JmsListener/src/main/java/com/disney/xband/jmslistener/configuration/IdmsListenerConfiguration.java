package com.disney.xband.jmslistener.configuration;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.jmslistener.JmsService;
import org.apache.log4j.Logger;

@XmlRootElement(name = "idmslistener")
public class IdmsListenerConfiguration implements JmsServiceConfiguration
{
	private static Logger logger = Logger
			.getLogger(IdmsListenerConfiguration.class);

	private static String IDMS_LISTENER_JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
	private static String IDMS_LISTENER_JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static String IDMS_LISTENER_JMS_BROKER_USER_KEY = "nge.eventserver.idmslistener.uid";
	private static String IDMS_LISTENER_JMS_BROKER_PWD_KEY = "nge.eventserver.idmslistener.pwd";
	private static String IDMS_LISTENER_SHARED_CONFIGURATIONS_KEY = "nge.xconnect.idmslistener.sharedconfigcount";
	private static String IDMS_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY = "nge.xconnect.idmslistener.shared.subscription.group";

	private static String IDMS_LISTENER_JMS_RETRY_PERIOD_KEY = "nge.xconnect.idmslistener.jms.retryperiod";

	private static String IDMS_LISTENER_XBMS_XBANDREQUEST_TOPIC_KEY = "nge.xconnect.idmslistener.xbms.xbandrequest.topic";
	private static String IDMS_LISTENER_XBMS_XBAND_TOPIC_KEY = "nge.xconnect.idmslistener.xbms.xband.topic";
	private static String IDMS_LISTENER_IDMS_XBANDREQUEST_TOPIC_KEY = "nge.xconnect.idmslistener.idms.xbandrequest.topic";
	private static String IDMS_LISTENER_IDMS_XBAND_TOPIC_KEY = "nge.xconnect.idmslistener.idms.xband.topic";
	private static String IDMS_LISTENER_NOTIFICATION_QUEUE_KEY = "nge.xconnect.idmslistener.notification.queue";
	private static String IDMS_LISTENER_PXC_RECEIVE_QUEUE_KEY = "nge.xconnect.idmslistener.pxc.receivequeue";
	private static String IDMS_LISTENER_PXC_CALLBACK_QUEUE_KEY = "nge.xconnect.idmslistener.pxc.callbackqueue";

	private static String IDMS_LISTENER_XBMS_ROOTURL_KEY = "nge.xconnect.idmslistener.xbms.rooturl";

    private static String IDMS_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY ="nge.xconnect.idmslistener.notraffictolerancesecs";

	private static String IDMS_LISTENER_CONFIG_SHARED_CONFIGURATIONS = "10";
    private static String IDMS_LISTENER_SHARED_SUBSCRIPTION_GROUP = "[[IDMSListenerGroup]]";
	private static int IDMS_LISTENER_JMS_RETRY_PERIOD = 10000;

	private Properties properties;

	public IdmsListenerConfiguration()
	{
	}

	@Override
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}
	

	@Override
	public boolean hasPropertiesMissing()
	{
		boolean missingPropertyFound = false;

		if (properties.getProperty(IDMS_LISTENER_JMS_BROKER_URL_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_JMS_BROKER_URL_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_JMS_BROKER_DOMAIN_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_JMS_BROKER_DOMAIN_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_JMS_BROKER_USER_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_JMS_BROKER_USER_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_JMS_BROKER_PWD_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_JMS_BROKER_PWD_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_SHARED_CONFIGURATIONS_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_SHARED_CONFIGURATIONS_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_XBMS_ROOTURL_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_XBMS_ROOTURL_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_XBMS_XBANDREQUEST_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_XBMS_XBANDREQUEST_TOPIC_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_XBMS_XBAND_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_XBMS_XBAND_TOPIC_KEY);
			missingPropertyFound = true;
		}
		if (properties.getProperty(IDMS_LISTENER_IDMS_XBANDREQUEST_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_IDMS_XBANDREQUEST_TOPIC_KEY);
			missingPropertyFound = true;
		}
		if (properties.getProperty(IDMS_LISTENER_IDMS_XBAND_TOPIC_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_IDMS_XBAND_TOPIC_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_PXC_RECEIVE_QUEUE_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_PXC_RECEIVE_QUEUE_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_PXC_CALLBACK_QUEUE_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_PXC_CALLBACK_QUEUE_KEY);
			missingPropertyFound = true;
		}

		if (properties.getProperty(IDMS_LISTENER_NOTIFICATION_QUEUE_KEY) == null)
		{
			logger.fatal("!! Missing property: "
					+ IDMS_LISTENER_NOTIFICATION_QUEUE_KEY);
			missingPropertyFound = true;
		}

		return missingPropertyFound;
	}

	@XmlElement
	@Override
	public String getJmsBrokerUrl()
	{
		return properties.getProperty(IDMS_LISTENER_JMS_BROKER_URL_KEY);
	}

	@XmlElement
	@Override
	public String getJmsBrokerDomain()
	{
		return properties.getProperty(IDMS_LISTENER_JMS_BROKER_DOMAIN_KEY);
	}

	@XmlElement
	@Override
	public String getJmsUser()
	{
		return properties.getProperty(IDMS_LISTENER_JMS_BROKER_USER_KEY);
	}

	@XmlTransient
	@Override
	public String getJmsPassword()
	{
		return properties.getProperty(IDMS_LISTENER_JMS_BROKER_PWD_KEY);
	}

	@XmlElement
	public String getXbmsXbandRequestTopic()
	{
		return properties
				.getProperty(IDMS_LISTENER_XBMS_XBANDREQUEST_TOPIC_KEY);
	}

	@XmlElement
	public String getXbmsXbandTopic()
	{
		return properties.getProperty(IDMS_LISTENER_XBMS_XBAND_TOPIC_KEY);
	}

	@XmlElement
	public String getIdmsXbandRequestTopic()
	{
		return properties
				.getProperty(IDMS_LISTENER_IDMS_XBANDREQUEST_TOPIC_KEY);
	}

	@XmlElement
	public String getIdmsXbandTopic()
	{
		return properties.getProperty(IDMS_LISTENER_IDMS_XBAND_TOPIC_KEY);
	}

	@XmlElement
	public String getPxcReceiveQueue()
	{
		return properties.getProperty(IDMS_LISTENER_PXC_RECEIVE_QUEUE_KEY);
	}

	@XmlElement
	public String getPxcCallbackQueue()
	{
		return properties
				.getProperty(IDMS_LISTENER_PXC_CALLBACK_QUEUE_KEY);
	}

	@XmlElement
	public String getNotificationQueue()
	{
		return properties.getProperty(IDMS_LISTENER_NOTIFICATION_QUEUE_KEY);
	}

	@XmlElement
	public String getXbmsRootUrl()
	{
		return properties.getProperty(IDMS_LISTENER_XBMS_ROOTURL_KEY);
	}

	@XmlElement
	@Override
	public int getSharedConfigurations()
	{
		if (properties.getProperty(IDMS_LISTENER_SHARED_CONFIGURATIONS_KEY) != null)
		{
			return Integer.parseInt(properties
					.getProperty(IDMS_LISTENER_SHARED_CONFIGURATIONS_KEY));
		}

		return Integer.parseInt(IDMS_LISTENER_CONFIG_SHARED_CONFIGURATIONS);
	}

	@XmlElement
	@Override
	public String getSharedConfigurationGroup()
	{
		if (properties.getProperty(IDMS_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY ) != null)
		{
			return properties
					.getProperty(IDMS_LISTENER_SHARED_SUBSCRIPTION_GROUP_KEY);
		}
		
		return IDMS_LISTENER_SHARED_SUBSCRIPTION_GROUP;
	}

	@XmlElement
	@Override
	public int getJmsRetryPeriod()
	{
		if (properties.getProperty(IDMS_LISTENER_JMS_RETRY_PERIOD_KEY) != null)
		{
			return Integer.parseInt(properties
					.getProperty(IDMS_LISTENER_JMS_RETRY_PERIOD_KEY));
		}

		return IDMS_LISTENER_JMS_RETRY_PERIOD;
	}

    @XmlElement
    @Override
    public long getNoTrafficToleranceSecs()
    {
        try {
            return Long.parseLong(properties.getProperty(IDMS_LISTENER_NO_TRAFFIC_TOLERANCE_SECS_KEY)) * 1000;
        }
        catch (Exception e) {
            return JmsService.DEFAULT_NO_TRAFFIC_TOLERANCE_SECS * 1000;
        }
    }

	/*
	 * All the methods below are here for jaxb's benefit and they are meant to
	 * be private.
	 */
	@SuppressWarnings("unused")
	private void setJmsBrokerUrl(String jmsBroker)
	{
	}

	@SuppressWarnings("unused")
	private void setJmsBrokerDomain(String jmsBroker)
	{
	}

	@SuppressWarnings("unused")
	private void setJmsUser(String jmsUser)
	{
	}

	@SuppressWarnings("unused")
	private void setJmsPassword(String jmsPassword)
	{
	}

	@SuppressWarnings("unused")
	private void setXbmsXbandRequestTopic(String xbmsXbandRequestTopic)
	{
	}

	@SuppressWarnings("unused")
	private void setXbmsXbandTopic(String xbmsXbandTopic)
	{
	}

	@SuppressWarnings("unused")
	private void setIdmsXbandRequestTopic(String idmsXbandRequestTopic)
	{
	}

	@SuppressWarnings("unused")
	private void setIdmsXbandTopic(String idmsXbandTopic)
	{
	}

	@SuppressWarnings("unused")
	private void setPxcReceiveQueue(String x)
	{
	}

	@SuppressWarnings("unused")
	private void setPxcCallbackQueue(String x)
	{
	}

	@SuppressWarnings("unused")
	private void setNotificationQueue(String notificationQueue)
	{
	}

	@SuppressWarnings("unused")
	private void setIdmsRootUrl(String idmsRootUrl)
	{
	}

	@SuppressWarnings("unused")
	private void setXbmsRootUrl(String xbmsRootUrl)
	{
	}

	@SuppressWarnings("unused")
	private void setJmsRetryPeriod(int jmsRetryPeriod)
	{
	}

	@SuppressWarnings("unused")
	private void setSharedConfigurations(int sharedConfigurations)
	{
	}
	
	@SuppressWarnings("unused")
	private void setSharedConfigurationGroup(String s)
	{
	}
}
