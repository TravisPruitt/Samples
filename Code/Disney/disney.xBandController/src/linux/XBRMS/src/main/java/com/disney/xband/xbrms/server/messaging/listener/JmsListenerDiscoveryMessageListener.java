package com.disney.xband.xbrms.server.messaging.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.server.messaging.JmsMessageListener;
import com.disney.xband.xbrms.server.messaging.PublishEventType;

public class JmsListenerDiscoveryMessageListener extends JmsMessageListener
{
	private static Logger logger = Logger.getLogger(JmsListenerDiscoveryMessageListener.class);
	
	public static JmsListenerDiscoveryMessageListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void onMessage(Message message)
	{
		if (!(message instanceof TextMessage))
			return;
		
//		// validate the message
//		if (!isValidJmsListenerMessage(message, this))
//			return;
//		
//		// cross-park messages are ignored
//		if (isCrossParkMessage(message, this))
//			return;
		
		String data = null;
    	try
		{
    		// unpack the message
			data = ((TextMessage)message).getText();
			
			logger.trace("Received JMS DISCOVERY message from jms listener: " + data);
			
			//TODO add processing code once jms listener starts sending DISCOVERY messages
			
			// add to the health item cache
//			JmsListenerDto jmsListener = new JmsListenerDto();
//			XbrcDiscoveryConsumer.getInstance().addHealthItem(jmsListener);
		}
		catch (JMSException e)
		{
			logger.error("Failed to process " + PublishEventType.JMS_LISTENER_DISCOVERY.getJmsMessageType() + " jms message.", e);
		}
    	catch (Exception e)
    	{
    		logger.error("Failed to add jms listener to the health item cache.", e);
    	}
	}
	
	private JmsListenerDiscoveryMessageListener(){}
	
	private static class SingletonHolder
	{
		private static final JmsListenerDiscoveryMessageListener INSTANCE = new JmsListenerDiscoveryMessageListener();
	}
}
