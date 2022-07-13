package com.disney.xband.xbrms.server.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.disney.xband.jms.lib.connection.ConnectionManager;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;

/**
 * Single threaded, but thread safe. 
 */
public class JMSAgent extends JmsMessageListener implements ExceptionListener
{
	private static final String DISCOVERY_JMS_BROKER_DOMAIN_KEY = "nge.eventserver.brokerDomain";
	private static final String JMS_CONNECTION_FACTORY_JNDI_NAME_KEY = "nge.eventserver.xbrc.connectionfactory.jndi.name";
	private static final String DISCOVERY_JMS_BROKER_URL_KEY = "nge.eventserver.mgmtBrokerUrl";
	private static final String DISCOVERY_JMS_BROKER_USER_KEY = "nge.eventserver.xbrc.uid";
	private static final String DISCOVERY_JMS_BROKER_PWD_KEY = "nge.eventserver.xbrc.pwd";
	
	public static JMSAgent INSTANCE = new JMSAgent();

	private static Logger logger = Logger.getLogger(JMSAgent.class);

    private Connection connection = null;
    // single-threaded context for producing and consuming messages
	private Session session = null;
	private MessageProducer publisher = null;
	
	private ConcurrentMap<PublishEventType, MessageConsumer> consumers;
	
	public static JMSAgent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public void start() throws JMSException, NamingException
	{
		synchronized(this)
		{
			// create connection, session, and producer
			connect();
			
			// start the connection
			openConnection();
		}
	}
	
	public void stop()
	{
		synchronized(this)
		{
			closeConnection();
		}
	}
	
	public synchronized boolean publishMessage(PublishEventType messageType)
	{
		return publishMessage(messageType, null, null);
	}
	
	public synchronized boolean publishMessage(PublishEventType messageType, String payload)
	{
		return publishMessage(messageType, null, payload);
	}
	
	public synchronized boolean publishMessage(PublishEventType messageType, Map<String, String> properties)
	{
		return publishMessage(messageType, properties, null);
	}
	
	public synchronized boolean publishMessage(PublishEventType messageType, Map<String, String> properties, String payload)
	{
		if (messageType == null)
		{
			logger.trace("Not sending jms message. Message type missing.");
			return false;
		}
		
		if (publisher == null || session == null)
		{
			logger.trace("JMS disabled. Not sending " + messageType.getJmsMessageType() + " messages.");
			
			return false;
		}
		
		try
		{
			// create jms xbrms message
			TextMessage msg = session.createTextMessage();
			
			// set properties common to all xbrms messages
			msg.setStringProperty(XBRMS_MESSAGE_TYPE, messageType.getJmsMessageType());
			msg.setStringProperty(PARK_ID, ConfigProperties.getInstance().getProperty("nge.xconnect.parkid"));
			
			// set message specific properties
			if (properties != null)
			{
				for (String property: properties.keySet())
				{
					if (properties.get(property) == null)
						continue;
					
					if (properties.get(property).trim().isEmpty())
						continue;
					
					msg.setStringProperty(property, properties.get(property));
				}
			}
			
			// set message attributes
			msg.setJMSExpiration(XbrmsConfigBo.getInstance().getDto().getJmsMessageExpiration_sec() * 1000);
			
			// serialize and add the payload
			String messageText = "";
			if (payload != null)
			msg.setText(payload);
			
			// send
			publisher.send(msg);
			session.commit();

			logger.trace("Sent " + messageType.getJmsMessageType() + " JMS message: " + messageText);
		}
		catch (Exception e)
		{
			logger.error("Failed to publish " + messageType.getJmsMessageType() + " message.", e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * JMSAgent acts as a generic listener. It should be replaced with a smarter listener per consumer.
	 * Use the this.registerMessageListener() method to register more appropriate listeners.
	 */
	@Override
	public void onMessage(Message message)
	{
		if (message == null || !(message instanceof TextMessage))
			return;
		
    	try
		{
			logger.warn("We don't have a message listener registered for this type of jms message: \n" 
								+ ((TextMessage) message).getText());
		}
		catch (JMSException e)
		{
			logger.error("", e);
		}
	}
	
	@Override
	public void onException(JMSException exception)
	{

	    logger.error("Got a JMS exception. Trying to restart jms broker - "
			+ ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY)
			+ " using Domain - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY)
			+ " and User - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_USER_KEY), exception);

	    stop();      
	}
	
	public synchronized boolean initialize() throws IllegalArgumentException
	{
		if (logger.isInfoEnabled())
			 logger.info("Initializing JMS agent...");
		
		/*
		 * Verify that we have all the necessary connection info
		 */
		String topicName = XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic();
		if (topicName.trim().length() == 0 || topicName.trim().startsWith("#"))
		{
			logger.warn("JMS agent disabled. Not processing messages.");
			return false;
		}
		
		String connectionFactoryName = ConfigProperties.getInstance().getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
		if (connectionFactoryName == null || connectionFactoryName.trim().length() == 0)
			throw new IllegalArgumentException("Can't instantiate JMSAgent. Missing environment property: " + JMS_CONNECTION_FACTORY_JNDI_NAME_KEY);
		
		String brokerDomain = ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY);
		if (brokerDomain == null || brokerDomain.trim().length() == 0)
			throw new IllegalArgumentException("Can't instantiate JMSAgent. Missing environment property: " + DISCOVERY_JMS_BROKER_DOMAIN_KEY);
		
		String brokerUrl = ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY);
		if (brokerUrl == null || brokerUrl.trim().length() == 0)
			throw new IllegalArgumentException("Can't instantiate JMSAgent. Missing environment property: " + DISCOVERY_JMS_BROKER_URL_KEY);
		
		String brokerUser = ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_USER_KEY);
		if (brokerUser == null || brokerUser.trim().length() == 0)
			throw new IllegalArgumentException("Can't instantiate JMSAgent. Missing environment property: " + DISCOVERY_JMS_BROKER_USER_KEY);
		
		String brokerPass = ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_PWD_KEY);
		if (brokerPass == null || brokerPass.trim().length() == 0)
			throw new IllegalArgumentException("Can't instantiate JMSAgent. Missing environment property: " + DISCOVERY_JMS_BROKER_PWD_KEY);
	
		return true;
	}
	
	public boolean isEnabled()
	{
		String jmsTopic = XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic();
		if (jmsTopic == null || jmsTopic.trim().isEmpty() || jmsTopic.trim().startsWith("#"))
			return false;
		return true;
	}
	
	public boolean isInitializedCorrectly()
	{
		// discovery mechanism requires connection and session
		if (connection == null || session == null)
			return false;
		
		// high availability mechanism also requires a publisher
		//TODO enable this back once XBRMS starts sending jms messages of any type
//		if (publisher == null)
//			return false;
		
		return true;
	}
	
	private void connect() throws JMSException, NamingException 
	{
		try
		{
			if (!isEnabled())
				return;
			
			logger.info("Opening JMS connection");
			
			connection = ConnectionManager.createConnection(
	        		ConfigProperties.getInstance().getProperty(JMS_CONNECTION_FACTORY_JNDI_NAME_KEY),
					ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY),
					ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY),
					ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_USER_KEY),
					ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_PWD_KEY),
					this);
			
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Topic topic = session.createTopic(XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic());
			
			publisher = session.createProducer(topic);
			
			String parkid = ConfigProperties.getInstance().getProperty("nge.xconnect.parkid");
			if (parkid == null || parkid.isEmpty() || parkid.equals("#"))
			{
				logger.error("Ignoring all DISCOVERY messages because the nge.xconnect.parkid environment.properties entry is not set.");
				ProblemsReportBo.getInstance().setLastError(ProblemAreaType.JmsMessaging, "Ignoring all DISCOVERY messages because the nge.xconnect.parkid environment.properties entry is not set.");
				return;
			}
			
			/*
			 * Register listeners/consumers
			 */
			for (PublishEventType eventType : PublishEventType.values())
			{
				if (eventType.getJmsMessageListenerClass() == null)
					continue;
				
				eventType.setParkId(parkid);
				
				/*
				 * Only listen to messages from the topic filtered by the specified message selector.
				 * Messages from self are ignored.
				 */
				consumers.put(eventType, session.createConsumer(topic, eventType.getJmsMessageSelector(), true));
				
				JmsMessageListener listener = eventType.getJmsMessageListener(logger);
				
				if (listener == null)
				{
					/*
					 *  In case we don't know of a more appropriate listener/consumer for messages
					 *  of this eventType, this will be used as a generic listener.
					 */
					consumers.get(eventType).setMessageListener(this);
					
					continue;
				}
				
				consumers.get(eventType).setMessageListener(listener);
			}
					
			logger.info("Connected to topic: " + XbrmsConfigBo.getInstance().getDto().getJmsXbrcDiscoveryTopic()
					+ " on broker: " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY));
		}
		catch (javax.jms.JMSException e)
		{
			logger.error("Cannot connect to JMS Broker - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY)
							+ " using Domain - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY)
							+ " and User - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_USER_KEY), e);
			
			throw e;
		}
		catch (NamingException e)
		{
			logger.error("Cannot connect to JMS Broker - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_URL_KEY)
							+ " using Domain - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_DOMAIN_KEY)
							+ " and User - " + ConfigProperties.getInstance().getProperty(DISCOVERY_JMS_BROKER_USER_KEY), e);			
			throw e;
		}
	}
	
	private void openConnection() throws JMSException
	{
		// connection must be started before it can consume messages
		if (connection != null)
		{
			logger.info("Starting JMS connection");
			connection.start();
		}
	}
	
	private void closeConnection()
	{
		logger.info("Closing all JMS connections");
		
        try
        {
            if (publisher != null) {
                publisher.close();
            }
        }
        catch (Exception e)
        {
        }

        publisher = null;

        try
        {
            if (session != null) {
                session.close();
            }
        }
        catch (Exception e)
        {
            logger.info("Failed to close JMS session.", e);
        }

        session = null;

		try
		{
			// Closing a connection also closes its sessions and their message producers and message consumers.
			if (connection != null) {
				connection.close();
            }
		}
		catch (Exception e)
		{
			logger.info("Failed to close JMS connection. This can cause resources not to be released by the JMS provider", e);
		}

        connection = null;
	}
	
	private JMSAgent() {
		consumers = new ConcurrentHashMap<PublishEventType, MessageConsumer>();
	}
	
	private static class SingletonHolder
	{
		private static final JMSAgent INSTANCE = new JMSAgent();
	}
}
