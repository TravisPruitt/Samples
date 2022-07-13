package com.disney.xband.xbrms.server.messaging;

import java.lang.reflect.Method;

import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.server.messaging.listener.IdmsDiscoveryMessageListener;
import com.disney.xband.xbrms.server.messaging.listener.JmsListenerDiscoveryMessageListener;
import com.disney.xband.xbrms.server.messaging.listener.XbrcDiscoveryMessageListener;
import com.disney.xband.xbrms.server.messaging.listener.XbrmsSampleListener;

public enum PublishEventType
{
	// events that don't trigger jms message
	XBRMS_CONFIGURATION_UPDATE(null, null, null),
	XBRMS_CONFIGURATION_REFRESH(null, null, null),
	
	// events which listen to jms messages
	XBRC_DISCOVERY(
			"xbrc_message_type='DISCOVERY'",
			"DISCOVERY", 
			XbrcDiscoveryMessageListener.class),
	IDMS_DISCOVERY(
			"idms_message_type='DISCOVERY'",
			"DISCOVERY",
			IdmsDiscoveryMessageListener.class),
	JMS_LISTENER_DISCOVERY(
			"jmslistener_message_type='DISCOVERY'",
			"DISCOVERY",
			JmsListenerDiscoveryMessageListener.class);
	
	private String jmsMessageSelector;
	private String jmsMessageType;
	private Class<? extends MessageListener> jmsMessageListenerClass;
	private String parkId;
	
	/**
	 * @param jmsMessageSelector may be null for event types that don't trigger jms messages
	 * @param jmsMessageType may be null for event types that don't trigger jms messages
	 */
	PublishEventType(String jmsMessageSelector, String jmsMessageType, Class<? extends MessageListener> jmsMessageListenerClass)
	{
		this.jmsMessageSelector = jmsMessageSelector;
		this.jmsMessageType = jmsMessageType;
		this.jmsMessageListenerClass = jmsMessageListenerClass;
	}
	
	public static PublishEventType getByMessageType(String messageType)
	{
		if (messageType == null)
			return null;
		
		for (PublishEventType eventType : PublishEventType.values())
		{
			if (messageType.equalsIgnoreCase(eventType.jmsMessageType))
				return eventType;
		}
		
		return null;
	}
	
	public JmsMessageListener getJmsMessageListener(Logger logger)
	{
		if (jmsMessageListenerClass == null)
		{
			logger.warn("Concrete listener not specified for jms messages of type " + jmsMessageType
					+ " Defaulting to using " + JMSAgent.class.getName());
			
			return null;
		}
			
		Method method = null;
		try
		{
			method = jmsMessageListenerClass.getMethod("getInstance", new Class[0]);
		}
		catch (Exception e)
		{
			logger.warn("Declaration of the concrete listener " + jmsMessageListenerClass.getName()
					+ " is missing the required getInstance() method. Defaulting to using " + JMSAgent.class.getName()
					+ " as a listener for jms messages of type " + jmsMessageType);
			
			return null;
		}
			
		JmsMessageListener listener = null;
		try
		{
			listener = (JmsMessageListener) method.invoke(null, (Object[])null);
		}
		catch (Exception e)
		{
			logger.warn("Failed to instantiate a listener of type " + jmsMessageListenerClass.getName()
					+ " Defaulting to using " + JMSAgent.class.getName()
					+ " as a listener for jms messages of type " + jmsMessageType);
			
			return null;
		}
		
		return listener;
	}
	
	/**
	 * Used to limit the type of jms message delivered to the jms message consumer using a message selector. 
	 * Only messages with properties matching the message selector expression are delivered. 
	 * A value of null or an empty string indicates that there is no message selector for the message consumer.
	 */
	public String getJmsMessageSelector()
	{
		if (parkId != null)
		{
			if (jmsMessageSelector != null)
				return jmsMessageSelector + " AND xconnect_parkid='" + parkId + "'";
			
			return "xconnect_parkid='" + parkId + "'";
		}
					
		return jmsMessageSelector;
	}
	
	public void setMessageSelector(String jmsMessageSelector)
	{
		this.jmsMessageSelector = jmsMessageSelector;
	}
	
	public String getJmsMessageType()
	{
		return jmsMessageType;
	}

	public Class<? extends MessageListener> getJmsMessageListenerClass()
	{
		return jmsMessageListenerClass;
	}

	public String getParkId() {
		return parkId;
	}

	public void setParkId(String parkId) {
		this.parkId = parkId;
	}
}
