package com.disney.xband.xbms.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.UUID;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import progress.message.jclient.ConnectionStateChangeListener;
import progress.message.jclient.Constants;
import progress.message.jimpl.Connection;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;
import com.disney.xband.xbms.dao.DAOFactory;

public class MessagePublisher extends TimerTask implements ExceptionListener, ConnectionStateChangeListener
{
	private static Logger logger = Logger.getLogger(MessagePublisher.class);

	private Connection connection = null;

	private Session session = null;
	private MessageProducer xbmsXbandMessageProducer = null;
	private MessageProducer idmsXbandMessageProducer = null;
	private MessageProducer xbmsXbandRequestMessageProducer = null;
	private MessageProducer idmsXbandRequestMessageProducer = null;
	
	private int messageBatchId;
	
	public MessagePublisher(int messageBatchId)
	{
		this.messageBatchId = messageBatchId;
	}

	private void createConnection(
			String connectionFactoryJndiName, String brokerDomain,
			String brokerUrl, String user, String password)
	{
		progress.message.jclient.ConnectionFactory factory = null;
				
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sonicsw.jndi.mfcontext.MFContextFactory");
		env.put("com.sonicsw.jndi.mfcontext.domain", brokerDomain);
		env.put(Context.PROVIDER_URL, brokerUrl);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try
		{
			InitialContext context = new InitialContext(env);

			// create the connection factory
			factory = (progress.message.jclient.ConnectionFactory) context
					.lookup(connectionFactoryJndiName);
			
			factory.setSocketConnectTimeout(5000);
		}
		catch (NamingException e)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Cannot connect to JMS Broker - " + brokerUrl
							+ " using Domain - " + brokerDomain
							+ " and User - " + user, e));
			this.connection = null;
		}

		try
		{	
			// now use the factory to create our connection
			if(factory != null) {
			   connection = (progress.message.jimpl.Connection) factory.createConnection(user, password);
      }
		}
		catch (javax.jms.JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Cannot connect to JMS Broker - " + brokerUrl
					+ " using Domain - " + brokerDomain
					+ " and User - " + user, ex));
			
			connection = null;
			return;
		}
	    
		this.connection.setConnectionStateChangeListener(this);
		this.connection.setPingInterval(5000);
		
		try
		{
			this.connection.setExceptionListener(this);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Service: DiscoveryService set exception listener: ", ex));
			
			this.connection = null;
			return;
		}

		try
		{
			// This session is not transacted, and it uses automatic message
			// acknowledgment
			this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Could not create session: ", ex));
			
			this.connection = null;
			this.session = null;
			return;
		}

		try
		{
			// create a pub/sub on our topic
			 Topic xbandTopic = this.session
					.createTopic("XBMS.XBAND");

			// create a publisher for that topic
			this.xbmsXbandMessageProducer = this.session.createProducer(xbandTopic);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Could not create topic for topic XBMS.XBAND", ex));
			
			this.connection = null;
			this.session = null;
			this.xbmsXbandMessageProducer = null;
			return;
		}
		
		try
		{
			// create a pub/sub on our topic
			 Topic xbandTopic = this.session
					.createTopic("XBMS.IDMS.XBAND");

			// create a publisher for that topic
			this.idmsXbandMessageProducer = this.session.createProducer(xbandTopic);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Could not create topic for topic XBMS.IDMS.XBAND", ex));
			
			this.connection = null;
			this.session = null;
			this.idmsXbandMessageProducer = null;
			return;
		}
		
		try
		{
			// create a pub/sub on our topic
			 Topic xbandRequestTopic = this.session
					.createTopic("XBMS.XBANDREQUEST");

			// create a publisher for that topic
			this.xbmsXbandRequestMessageProducer = this.session.createProducer(xbandRequestTopic);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Could not create topic for topic XBMS.XBAND", ex));
			
			this.connection = null;
			this.session = null;
			this.xbmsXbandRequestMessageProducer = null;
			return;
		}
		
		try
		{
			// create a pub/sub on our topic
			 Topic xbandRequestTopic = this.session
					.createTopic("XBMS.IDMS.XBANDREQUEST");

			// create a publisher for that topic
			this.idmsXbandRequestMessageProducer = this.session.createProducer(xbandRequestTopic);
		}
		catch(JMSException ex)
		{
			logger.error(ExceptionFormatter.formatMessage(
					"Could not create topic for topic XBMS.IDMS.XBAND", ex));
			
			this.connection = null;
			this.session = null;
			this.idmsXbandRequestMessageProducer = null;
			return;
		}
	}

	@Override
	public void run()
	{
		try
		{
			if (this.connection == null)
			{
				// now use the factory to create our connection
				createConnection(
	            		ConfigurationSettings.INSTANCE.getConnectionFactoryJndiName(),
						ConfigurationSettings.INSTANCE.getJmsBrokerDomain(),
						ConfigurationSettings.INSTANCE.getJmsBrokerUrl(),
						ConfigurationSettings.INSTANCE.getJmsUser(),
						ConfigurationSettings.INSTANCE.getJmsPassword());
			}
			else
			{
				try
				{
					XbandRequest xbandRequest = DAOFactory.getDAOFactory().getMessageDAO().GetNextXbandRequestMessage(this.messageBatchId);
	
					if(xbandRequest != null)
					{
						BusinessEvent businessEvent = new BusinessEvent();
						
						SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
								"yyyy-MMM-dd'T'HH:mm:ss'Z'");
						dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

						String timestamp = dateFormatGmt.format(new Date());

						businessEvent.setLocation("XBMS.XBANDREQUEST");

						businessEvent.setEventType("BOOK");

						businessEvent.setSubType("");
						businessEvent.setReferenceId(xbandRequest.getXbandRequestId());
						businessEvent.setGuestIdentifier("");
						businessEvent.setTimeStamp(timestamp);
						businessEvent.setCorrelationId(UUID.randomUUID().toString());

						//Serialize
						String messageText = XmlUtil.convertToXml(businessEvent, BusinessEvent.class);
						
						TextMessage msg = this.session.createTextMessage();
						msg.setText(messageText);
						this.xbmsXbandRequestMessageProducer.send(msg);
						
						DAOFactory.getDAOFactory().getMessageDAO().XbandRequestMessageSent(xbandRequest.getXbandRequestId());
					}
					else
					{
	
						Xband xband = DAOFactory.getDAOFactory().getMessageDAO().GetNextXbandMessage(this.messageBatchId);
						if(xband != null)
						{
							BusinessEvent businessEvent = new BusinessEvent();
							
							SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
									"yyyy-MMM-dd'T'HH:mm:ss'Z'");
							dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

							String timestamp = dateFormatGmt.format(new Date());

							businessEvent.setLocation("XBMS.XBAND");

							businessEvent.setEventType("BOOK");

							businessEvent.setSubType("");
							businessEvent.setReferenceId(xband.getXbandId());
							businessEvent.setGuestIdentifier("");
							businessEvent.setTimeStamp(timestamp);
							businessEvent.setCorrelationId(UUID.randomUUID().toString());

							//Serialize
							String messageText = XmlUtil.convertToXml(businessEvent, BusinessEvent.class);
							
							javax.jms.TextMessage msg = this.session.createTextMessage();
							msg.setText(messageText);
							
							boolean sent = false;
							
							if(xband.getBandRole() != null)
							{
								if(xband.getBandRole().compareToIgnoreCase("Puck") == 0  ||
								   xband.getBandRole().compareToIgnoreCase("Cast Member") == 0)
								{
									this.idmsXbandMessageProducer.send(msg);
									sent = true;
								}
							}
						
							if(!sent)
							{
								this.xbmsXbandMessageProducer.send(msg);
							}

							DAOFactory.getDAOFactory().getMessageDAO().XbandMessageSent(xband.getXbandId());
						}
						else
						{
							JMSAgent.INSTANCE.FinishBatch(this.messageBatchId);
							DAOFactory.getDAOFactory().getMessageDAO().Finish(messageBatchId);
						}
					}

				}
				catch (JMSException e)
				{
					logger.error("Failed to publish message.", e);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Unexpected exception.", ex));
		}
	}

	@Override
	public void onException(JMSException ex)
	{
		logger.error(ExceptionFormatter.format(
				"Unexpected exception.", ex));
	}

	@Override
	public void connectionStateChanged(int state)
	{
		if(state == Constants.ACTIVE)
		{
			logger.error("Discovery connection state changed to ACTIVE.");
		}
		else if(state == Constants.RECONNECTING)
		{
			logger.error("Discovery connection state changed to RECONNECTING.");
		}
		else if(state == Constants.FAILED)
		{
			logger.error("Discovery connection state changed to FAILED.");
		}
		else if(state == Constants.CLOSED)
		{
			logger.error("Discovery connection state changed to CLOSED.");
		}
	}

}
