package com.disney.xband.xbrms.server.messaging.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.server.messaging.JmsMessageListener;
import com.disney.xband.xbrms.server.messaging.PublishEventType;

public class XbrmsSampleListener extends JmsMessageListener
{
	private static Logger logger = Logger.getLogger(XbrmsSampleListener.class);
	
	public static XbrmsSampleListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void onMessage(Message message)
	{
		if (!(message instanceof TextMessage))
			return;
		
		// validate the message
		if (!isValidXbrmsMessage(message, this))
			return;
		
		// cross-park messages are ignored
		if (isCrossParkMessage(message, this))
			return;
		
		String data = null;
    	try
		{
    		// unpack the message
			data = ((TextMessage)message).getText();
			
			//TODO implement
			
			if (logger.isTraceEnabled())
				logger.trace("Processed JMS message: " + data);
		}
		catch (JMSException e)
		{
			logger.error("Failed to process " + PublishEventType.XBRMS_CONFIGURATION_UPDATE.getJmsMessageType() + " jms message.", e);
		}
	}
	
	private XbrmsSampleListener(){}
	
	private static class SingletonHolder
	{
		private static final XbrmsSampleListener INSTANCE = new XbrmsSampleListener();
	}
}
