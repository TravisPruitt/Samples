package com.disney.xband.xbrms.server.messaging.listener;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.health.DiscoveryInfo;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.server.SystemHealthConsumer;
import com.disney.xband.xbrms.server.messaging.JmsMessageListener;
import com.disney.xband.xbrms.server.messaging.PublishEventType;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;

public class XbrcDiscoveryMessageListener extends JmsMessageListener
{
	private static Logger logger = Logger.getLogger(XbrcDiscoveryMessageListener.class);
	
	public static XbrcDiscoveryMessageListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void onMessage(Message message)
	{
		if (message == null)
			return;
		
		if (!(message instanceof TextMessage))
			return;
		
		// validate the message
		if (!isValidXbrcMessage(message, this))
			return;
		
		// cross-park messages are ignored
		if (isCrossParkMessage(message, this))
			return;
		
		String data = null;
    	try
		{
    		// unpack the message
			data = ((TextMessage)message).getText();
			
			logger.trace("Received JMS DISCOVERY message from xbrc: " + data);
			
			// process
			try {
				DiscoveryInfo info = deserializeDiscoveryInfo(data);
				
				if (info == null){
					logger.error("Failed to deserialize xbrc DISCOVERY message: [" + data + "]");
					return;
				}
				
				// previously deactivated health item may not be activated via discovery, only manually
				XbrcDto xbrc = new XbrcDto(info);
				SystemHealthConsumer.getInstance().addHealthItem(xbrc);
				
			} catch (JsonParseException e) {
	            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ProcessMessage, "Failed to deserialize XBRC broadcast message.", e);
				logger.error("Failed to deserialize XBRC broadcast: [" + data + "]", e);
			} catch (JsonMappingException e) {
	            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ProcessMessage, "Failed to map XBRC broadcast message.", e);
				logger.error("Failed to map XBRC broadcast: [" + data + "]", e);
			} catch (IOException e) {
	            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ProcessMessage, "I/O error occurred when deserializing XBRC broadcast message.", e);
				logger.error("IO exception receiving XBRC broadcast", e);
			} catch (Exception e) {
	            ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ProcessMessage, "SQL exception occurred when persisting XBRC broadcast message.", e);
				logger.error("SQL exception updating/inserting XBRC broadcast", e);
			}
			
			if (logger.isTraceEnabled())
				logger.trace("Processed JMS message: " + data);
		}
		catch (JMSException e)
		{
			logger.error("Failed to process " + PublishEventType.XBRMS_CONFIGURATION_UPDATE.getJmsMessageType() + " jms message.", e);
		}
	}

	private DiscoveryInfo deserializeDiscoveryInfo(String str) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		DiscoveryInfo result = om.readValue(str, DiscoveryInfo.class);
		return result;
	}
	
	private XbrcDiscoveryMessageListener(){}
	
	private static class SingletonHolder
	{
		private static final XbrcDiscoveryMessageListener INSTANCE = new XbrcDiscoveryMessageListener();
	}
}
