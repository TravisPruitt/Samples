package com.disney.xband.xbrms.server.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.ConfigProperties;

public abstract class JmsMessageListener implements MessageListener
{
	protected static Logger logger = Logger.getLogger(JmsMessageListener.class);
	
	protected static final String XBRMS_MESSAGE_TYPE = "xbrms_message_type";
	protected static final String XBRC_MESSAGE_TYPE = "xbrc_message_type";
	protected static final String PARK_ID = "xconnect_parkid";
	
	/**
	 * All JMS messages xbrms produces and consumes must have the 'xbrms_message_type'
	 * property set.
	 * This method returns <CODE>true</CODE> if they do, <CODE>false</CODE> otherwise.
	 * 
	 * @param message the jms message being scrutinized
	 * @param listener the jms message listener that intercepted the jms message
	 * @return
	 */
	public static boolean isValidXbrmsMessage(Message message, MessageListener listener)
	{
		if (message == null)
			return false;
		
		// make sure xbrms_message_type is specified and supported
		String messageType = "unknown";
		try
		{
			messageType = message.getStringProperty(XBRMS_MESSAGE_TYPE);
			
			if (PublishEventType.valueOf(messageType) != PublishEventType.XBRMS_CONFIGURATION_UPDATE)
			{
				logger.warn("Jms message ignored. Jms message listener " + listener.getClass().getName() 
						+ " doesn't support messages of type " + messageType 
						+ " and shouldn't have been registered with the JMSAgent to receive these.");
				return false;
			}
			
			return true;
		}
		catch (JMSException e)
		{
			logger.trace("Ignoring jms message with missing xbrms_message_type.");
			return false;
		}
	}
	
	/**
	 * All xbrc JMS messages xbrms consumes must have the 'xbrc_message_type'
	 * property set.
	 * 
	 * This method returns <CODE>true</CODE> if they do, <CODE>false</CODE> otherwise.
	 * 
	 * @param message the jms message being scrutinized
	 * @param listener the jms message listener that intercepted the jms message
	 * @return
	 */
	public static boolean isValidXbrcMessage(Message message, MessageListener listener)
	{
		if (message == null)
			return false;
		
		// make sure xbrms_message_type is specified and supported
		String messageType = "unknown";
		try
		{
			messageType = message.getStringProperty(XBRC_MESSAGE_TYPE);
			
			PublishEventType eventType = PublishEventType.getByMessageType(messageType);
			
			if (eventType == null)
				return false;
			
			if (eventType != PublishEventType.XBRC_DISCOVERY)
			{
				logger.warn("Jms message ignored. Jms message listener " + listener.getClass().getName() 
						+ " doesn't support messages of type " + messageType 
						+ " and shouldn't have been registered with the JMSAgent to receive these.");
				return false;
			}
			
			return true;
		}
		catch (JMSException e)
		{
			logger.trace("Ignoring jms message with missing xbrms_message_type.");
			return false;
		}
	}
	
	/**
	 * All JMS messages xbrms consumes must come only from within the same park this
	 * xbrms application is responsible for. Cross park messages are ignored. Xbrms
	 * uses the value of the xbrms_park_id or xbrc_park_id property determine that.
	 * 
	 * This method returns <CODE>true</CODE> if park id information is not available
	 * or if the value of park id provided is different from this xbrms's park id, 
	 * <CODE>false</CODE> otherwise.
	 * 
	 * @param message the jms message being scrutinized
	 * @param listener the jms message listener that intercepted the jms message
	 * @return
	 */
	public static boolean isCrossParkMessage(Message message, MessageListener listener)
	{
		String parkId = null;
		try
		{
			parkId = message.getStringProperty(PARK_ID);
		}
		catch (JMSException e){}

        if(ConfigProperties.getInstance() == null) {
            logger.error("!!! ConfigProperties instance is null. This must never happen. !!!");
            return true;
        }
		
		if ((parkId == null) || !parkId.trim().equals(ConfigProperties.getInstance().getProperty("nge.xconnect.parkid")))
		{
			logger.warn("Cross park jms message detected. Ignoring. Expected " + PARK_ID + " value: " + 
					ConfigProperties.getInstance().getProperty("nge.xconnect.parkid")  + " but received: " + parkId);
			return true;
		}
		
		return false;
	}
}
