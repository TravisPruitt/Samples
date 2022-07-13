package com.disney.xband.xbrms.server.messaging.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.model.IdmsDto;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.server.SystemHealthConsumer;
import com.disney.xband.xbrms.server.messaging.JmsMessageListener;
import com.disney.xband.xbrms.server.messaging.PublishEventType;

public class IdmsDiscoveryMessageListener extends JmsMessageListener
{
	private static Logger logger = Logger.getLogger(IdmsDiscoveryMessageListener.class);
	
	public static IdmsDiscoveryMessageListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void onMessage(Message message)
	{
		if (!(message instanceof TextMessage))
			return;
		
//		// validate the message
//		if (!isValidIdmsMessage(message, this))
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
			
			logger.trace("Received JMS DISCOVERY message from idms: " + data);
			
			//TODO add processing code once idms starts sending DISCOVERY messages
			
			// add to the health item cache
//			IdmsDto idms = new IdmsDto();
//			XbrcDiscoveryConsumer.getInstance().addHealthItem(idms);
		}
		catch (JMSException e)
		{
			logger.error("Failed to process " + PublishEventType.IDMS_DISCOVERY.getJmsMessageType() + " jms message.", e);
		}
    	catch (Exception e)
    	{
    		logger.error("Failed to add idms to the health item cache.", e);
    	}
	}
	
	private IdmsDiscoveryMessageListener(){}
	
	private static class SingletonHolder
	{
		private static final IdmsDiscoveryMessageListener INSTANCE = new IdmsDiscoveryMessageListener();
	}
}
