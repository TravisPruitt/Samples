package com.disney.xband.xbrc.Controller;

import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.health.DiscoveryInfo;
import com.disney.xband.xbrc.lib.entity.HAMessage;
import com.disney.xband.xbrc.lib.model.IXBRCConnector;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.model.XBRCMessage;
import com.disney.xband.xbrc.lib.net.NetInterface;

public class JMSAgent implements IXBRCConnector, MessageListener, ExceptionListener
{
	// How often to cleanup the Messages table.
	private static final int MESSAGE_CLEANUP_INTERVAL_MSEC = 10000;

	// singleton pattern
	public static JMSAgent INSTANCE = new JMSAgent();

	private static Logger logger = Logger.getLogger(JMSAgent.class);
	private static Logger plogger = Logger.getLogger("performance." + JMSAgent.class.toString());

    //private javax.jms.Connection connJMS = null;
    private javax.jms.Connection connJMS = null;

	private javax.jms.Session sessJMS = null;
	private javax.jms.MessageProducer pubJMS = null;
	private javax.jms.MessageConsumer consumerHA = null;
	private Thread sendThread = null;
	
	private boolean run = true;
	private boolean closeHandles = false;
	private volatile Date forceDiscoverySendTime = null;
	
	// JMS message time
	private Date dtLastJMSSend = new Date();

	private Date dtLastJMSAttempt = null;
	// to control when to send discovery message
	private Date dtLastDiscoveryMessage = new Date();

	private int cJMSMessage = 0;
	private int cMetricsJMSMessage = 0;
	
	private Date dtLastMessageCleanup = new Date();
	
	private boolean processConfigChange = true;
	private String jmsPublishingError = null;
	
	// queue for incoming HA messages
	private List<HAMessage> listHA = null;

	public void initialize()
	{
		sendThread = new Thread()
		{
			public void run()
			{
				runSendThread();
			}
		};
		sendThread.start();
	}

	public void Terminate()
	{
		run = false;

		if (sendThread == null)
			return;

		synchronized (sendThread)
		{
			// wake up our send thread to let it exit
			sendThread.notify();
		}

		sendThread = null;
	}
	
	public String getStatusMessage()
	{
		if (!ConfigOptions.INSTANCE.getESBInfo().isEnablejms())
			return null;
		
		if (jmsPublishingError != null)
			return jmsPublishingError;

		return null;
	}

	public HAMessage[] getHAMessages()
	{
		// JMS connection gets established or fixed in the JMS thread
		if (consumerHA==null)
			return null;
		
		synchronized(listHA)
		{
			HAMessage[] aHA = listHA.toArray(new HAMessage[0]);
			listHA.clear();
			return aHA;
		}
	}
	
	private boolean isBrokerEnabled()
	{
		String sBroker = getBrokerUrl();
		return ConfigOptions.INSTANCE.getESBInfo().isEnablejms() &&
			sBroker != null && !sBroker.equals("") && !sBroker.startsWith("#");
	}
	
	public String getBrokerUrl()
	{
		return XBRCController.getInstance().getProperties().getProperty("nge.eventserver.mgmtBrokerUrl");
	}
	
	/*
	 * Listener initialization for HA 
	 */
	private void initializeHAListener()
	{
		// open the JMS connection (in case not already open)
        openJMSConnection();
        if (sessJMS==null)
        	return;
        
		// create the message queue
        if (listHA==null)
        	listHA = new ArrayList<HAMessage>();
		
        try
        {
	        // create a consumer
        	logger.trace("Creating JMS listener topic: " + ConfigOptions.INSTANCE.getESBInfo().getJMSTopic());
	        Topic topic = sessJMS.createTopic(ConfigOptions.INSTANCE.getESBInfo().getJMSTopic()); 

	        String sSelector = "xbrc_facility='" + ConfigOptions.INSTANCE.getControllerInfo().getVenue() + "'";
	        consumerHA = sessJMS.createConsumer(topic, sSelector, true);
	        consumerHA.setMessageListener(this);
        }
        catch(JMSException ex)
        {
        	logger.error(ExceptionFormatter.format("Failed to create HA listener", ex));
        	// clean up anything that's open
        	closeJMSHandles();
        }
	}

	/*
	 * The send thread
	 */
	private void runSendThread()
	{
		// run indefinitely
		while (true)
		{

			try
			{
				// if the "closeHandles" flag is set or we're exiting shut everything down
				if (closeHandles || !run)
				{
					closeJMSHandles();
					closeHandles = false;
				}
				
				// time to quit?
				if (!run)
					break;			
				
				// any changes to configuration? If so, reread
				if (processConfigChange)
				{				
					processConfigChange();
					processConfigChange = false;
				}
				
				Date dtNow = new Date();
				
				if ((dtNow.getTime() - dtLastMessageCleanup.getTime()) >= MESSAGE_CLEANUP_INTERVAL_MSEC)
				{
					if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
						plogger.trace("Start deleting old messages.");

				    // delete any old stuff
					deleteOldMessages();

					if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
						plogger.trace("End deleting old messages.");
				}
				
				if (isBrokerEnabled())
				{			
					// establish HA connection if necessary
					if (consumerHA==null && ConfigOptions.INSTANCE.getControllerInfo().isHaEnabled())
					{
						initializeHAListener();
					}			

					// is it time to send?
					if ((dtNow.getTime() - dtLastJMSSend.getTime()) >= ConfigOptions.INSTANCE.getESBInfo().getJmsSendIntervalMs())
					{
						if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
							plogger.trace("Start sending queued messages to JMS.");

						// send any queued data
						sendQueuedMessagesViaJMS();
						dtLastJMSSend = dtNow;

						if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
							plogger.trace("End sending queued messages to JMS.");
					}			

					if ((forceDiscoverySendTime != null && 
							dtNow.getTime() >= forceDiscoverySendTime.getTime()) ||
						((dtNow.getTime() - dtLastDiscoveryMessage.getTime()) > 
							ConfigOptions.INSTANCE.getESBInfo().getJmsDiscoveryTimeSec() * 1000))
					{
						if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
							plogger.trace("Start sending DISCOVERY message");
						
						forceDiscoverySendTime = null;
						sendDiscoveryJMSMessage();
						dtLastDiscoveryMessage = dtNow;
						
						if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
							plogger.trace("End sending DISCOVERY message");
					}
				}
				
				// do the wait before the end of the loop
				try
				{
					synchronized (sendThread)
					{
						sendThread.wait(100);
					}
				}
				catch (InterruptedException e)
				{
				}
			}
			catch (Exception e)
			{
				logger.error(ExceptionFormatter.format("Exception in JMS thread", e));				
				jmsPublishingError = "Exception in JMS thread";
			}
		}
	}

	void processConfigChange()
	{
		// close down all the connections in case the ESB parameters are reset
		closeJMSHandles();
	}

	private void openJMSConnection()
	{	
		// Create a connection.
		String sBroker = "";
		try
		{
			// Don't try to re-open if already opened
			if (connJMS != null && sessJMS != null)
				return;
			
			if (dtLastJMSAttempt != null)
			{
				Date dtNow = new Date();
				long msdiff = dtNow.getTime() - dtLastJMSAttempt.getTime();

				// if tried too recently don't retry yet
				if (msdiff < ConfigOptions.INSTANCE.getControllerInfo().getJMSRetryPeriod())
					return;
			}
			
			dtLastJMSAttempt = new Date();
			
			// grab environment settings
			sBroker = getBrokerUrl();
			
			// if broker starts with #, don't do any JMS
			if (sBroker == null || sBroker.startsWith("#"))
				return;
			
			String connectionFactoryJndiName = 
					XBRCController.getInstance().getProperties().getProperty("nge.eventserver.xbrc.connectionfactory.jndi.name");
			String sBrokerDomain = 
					XBRCController.getInstance().getProperties().getProperty("nge.eventserver.brokerDomain");
			String sUser = XBRCController.getInstance().getProperties().getProperty("nge.eventserver.xbrc.uid");
			String sPassword = XBRCController.getInstance().getProperties().getProperty("nge.eventserver.xbrc.pwd");

			// if broker starts with #, don't do any JMS
			if (sBroker.startsWith("#"))
				return;
			
			// create the connection
			logger.trace("Creating JMS connection factory connection");
			connJMS = createConnection(
            		connectionFactoryJndiName,
            		sBrokerDomain,
            		sBroker, 
            		sUser, 
            		sPassword,
            		this);
			
			logger.trace("Creating JMS session");


			// create a session
			// TODO: Review whether to specify true or false for the "transacted" parm
			sessJMS = connJMS.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
			
			logger.trace("JMS connnection and session established");
			
		}
		catch (Exception e)
		{
			logger.error("Cannot connect to JMS Broker - "
					+ sBroker
					+ " will retry in "
					+ ConfigOptions.INSTANCE.getControllerInfo()
							.getJMSRetryPeriod() + " milliseconds", e);
			
			jmsPublishingError = "Cannot connect to the JMS Broker at " + sBroker;
			
			// clean up anything that's open
			closeJMSHandles();
		}

	}

	private void openJMSPublishingConnection()
	{
		synchronized(this)
		{	
			openJMSConnection();
	
			if (connJMS == null || sessJMS == null)
				return;
	
			String sTopic = ConfigOptions.INSTANCE.getESBInfo().getJMSTopic();
			try
			{
				logger.trace ("Creating JMS publishing topic");
				
				// create a pub/sub on our topic
				javax.jms.Topic topic = sessJMS.createTopic(sTopic);
	
				// create a publisher for that topic
				pubJMS = sessJMS.createProducer(topic);
	
				logger.trace("Created JMS producer for topic: " + sTopic);
	
				// start everything going
				connJMS.start();
	
				dtLastJMSAttempt = null;
	
			}
			catch (javax.jms.JMSException jmse)
			{
				logger.error("Cannot create publish/subscribe topic: "
						+ sTopic);
				
				jmsPublishingError = "Cannot create JMS publish/subscribe topic: " + sTopic + ": " + jmse.getLocalizedMessage();
				
				// clean up anything that's open
	        	closeJMSHandles();
			}
			finally
			{
				dtLastJMSAttempt = new Date();
			}
		}
	}

	public javax.jms.Connection createConnection( String connectionFactoryJndiName, 
										String brokerDomain,
										String brokerUrl, 
										String user, 
										String password, 
										ExceptionListener el) throws JMSException, NamingException
	{
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sonicsw.jndi.mfcontext.MFContextFactory");
		env.put("com.sonicsw.jndi.mfcontext.domain", brokerDomain);
		env.put(Context.PROVIDER_URL, brokerUrl);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);

		InitialContext context = new InitialContext(env);

		// create the connection factory
		javax.jms.ConnectionFactory factory = 
				(javax.jms.ConnectionFactory) context.lookup(connectionFactoryJndiName);

		// now use the factory to create our connection
		javax.jms.Connection connection = factory.createConnection(user, password);
		
		if(el != null)
		{
			connection.setExceptionListener(el);
		}
		
		return connection;

	}
	private boolean writePayloadToDatabase(String sType, Date timestamp,
			String sPayload, boolean sendToJMS, boolean sendToHttp, Connection conn)
	{
		PreparedStatement stmt = null;

		try
		{
			String sSQL = "INSERT INTO Messages(MessageType, Timestamp, Payload, SendToHttp, SendToJMS) VALUES(?,?,?,?,?)";
			stmt = conn.prepareStatement(sSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, sType);
			stmt.setLong(2, timestamp.getTime());
			stmt.setString(3, sPayload);
            stmt.setBoolean(4, sendToHttp);
            stmt.setBoolean(5, sendToJMS);
			stmt.execute();
			
			ResultSet rs = stmt.getGeneratedKeys();
			long id = -1;
			
			if (rs.next())
			{
				id = rs.getLong(1);
			}
			
			rs.close();
			
			if (id == -1)
			{
				logger.warn("Failed to retrieve newly generated Messages id.");
			}
			else
			{
				// Check for Status table getting out of sync. 
				if (ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToJMS() > id)
				{
					logger.warn("The Status.LastMessageIdToJMS is " + ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToJMS() + 
								" which is larger than newly inserted Messages id of " + id + ". Resetting to " + (id -1) );
					ConfigOptions.INSTANCE.getStoredStatus().setLastMessageIdToJMS(id -1);					
				}
				if (ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToPostStream() > id)
				{
					logger.warn("The Status.LastMessageIdToPostStream is " + ConfigOptions.INSTANCE.getStoredStatus().getLastMessageIdToPostStream() + 
							" which is larger than newly inserted Messages id of " + id + ". Resetting to " + (id -1) );
					ConfigOptions.INSTANCE.getStoredStatus().setLastMessageIdToPostStream(id - 1);
				}
			}

			return true;
		}
		catch (SQLException e)
		{
			logger.error("!! SQL error when opening local database: " + e.getLocalizedMessage());
			return false;
		}
		finally
		{

			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	public static String preparePlaceHolders(int length) 
	{
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < length;) 
	    {
	        builder.append("?");
	        if (++i < length) 
	        {
	            builder.append(",");
	        }
	    }
	    return builder.toString();
	}

	public static void setValues(PreparedStatement preparedStatement, Collection<String> values, int startIndex) throws SQLException 
	{
		for (String value : values) 
		{
	        preparedStatement.setObject(startIndex++, value);
	    }
	}

	private void sendQueuedMessagesViaJMS()
	{
		if (pubJMS == null)
			openJMSPublishingConnection();

		// if we don't have one, JMS is down - just return "success" knowing
		// we'll try later
		if (pubJMS == null)
			return;	

		String facilityType = ConfigOptions.INSTANCE.getControllerInfo()
				.getModelName();

		// read events from the database
		long lReadFromId = ConfigOptions.INSTANCE.getStoredStatus()
				.getLastMessageIdToJMS();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();
			Date dtStart = new Date();
			if (lReadFromId == -1)
			{
				// haven't sent anything
				String sSQL = "SELECT * from Messages WHERE SendToJMS=1 limit 1000";
				stmt = conn.createStatement();
				stmt.execute(sSQL);
				rs = stmt.getResultSet();
			}
			else
			{
				String sSQL = "SELECT * from Messages WHERE Id>? AND SendToJMS=1 limit 1000";
				pstmt = conn.prepareStatement(sSQL);
				pstmt.setLong(1, lReadFromId);
				pstmt.execute();
				rs = pstmt.getResultSet();
			}

			Date dtNow = new Date();
			long lusecRead = dtNow.getTime() - dtStart.getTime();
			dtStart = dtNow;
			int cSentMessages = 0;
			while (rs.next())
			{
				long id = rs.getLong("id");
				String sMessageType = rs.getString("MessageType");
				String sMessagePayload = rs.getString("Payload");
				
				// escape name in case there are any weird characters in it
				String venue = ConfigOptions.INSTANCE.getControllerInfo().getVenue();
				venue = StringEscapeUtils.escapeXml(venue);

				// wrap in an envelope
				StringBuilder sEnv = new StringBuilder();
				sEnv.append("<venue name=\""
						+ venue
						+ "\" " + "time=\"" + formatTime(new Date().getTime())
						+ "\">\n");
				sEnv.append(sMessagePayload);
				sEnv.append("</venue>");

				// now send this out
				try
				{
					javax.jms.TextMessage msg = sessJMS.createTextMessage();
					msg.setText(sEnv.toString());
					// Do not use dots in JMS selector (property) names.
					msg.setStringProperty("xbrc_facility",
							ConfigOptions.INSTANCE.getControllerInfo()
									.getVenue());
					msg.setStringProperty("xbrc_facility_type", facilityType);
					msg.setStringProperty("xbrc_message_type", sMessageType);
					pubJMS.send(msg);
					sessJMS.commit();

					cJMSMessage++;

					if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
						logger.info("** Sending JMS message #: " + cJMSMessage);
					if (sMessageType.compareTo("METRICS") == 0)
					{
						cMetricsJMSMessage++;
						if (ConfigOptions.INSTANCE.getControllerInfo().getVerbose())
							logger.info("** Sending JMS Metrics message #: "
									+ cMetricsJMSMessage);
					}

					// update status
					ConfigOptions.INSTANCE.getStoredStatus()
							.setLastMessageIdToJMS(id);					
				}
				catch (JMSException e)
				{
					logger.error("!! Error: communicating with JMS Broker: " + e.getLocalizedMessage());
					logger.error("          Will retry later.");
					
					jmsPublishingError = "Failed to send message to the JMS broker: " + e.getLocalizedMessage();

					// set the flag to close the handles
					closeHandles = true;

					return;
				}
				cSentMessages++;
			}
			if (cSentMessages > 0)
			{
				dtNow = new Date();
				long msecSend = dtNow.getTime() - dtStart.getTime();
				logger.trace("Processed " + cSentMessages
						+ " message(s). Read msec: " + lusecRead
						+ " send msec: " + msecSend);

				double msecPerMessage = (double) msecSend / cSentMessages;
				Processor.INSTANCE.getStatusObject().getPerfUpstreamMsec()
						.processValue(msecPerMessage);
			}
			
			// clear all errors
			jmsPublishingError = null;
		}
		catch (Exception e1)
		{
			logger.error("!! Error reading messages from database: "
					+ e1.getLocalizedMessage());
			jmsPublishingError = "Error reading messages from database: "
					+ e1.getLocalizedMessage();
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception e)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	private void deleteOldMessages()
	{
		Connection conn = null;
		PreparedStatement pstmt = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();

			long lTimeOld = new Date().getTime()
					- ConfigOptions.INSTANCE.getControllerInfo()
							.getMessageStoragePeriod() * 1000;
			pstmt = conn
					.prepareStatement("DELETE FROM Messages WHERE Timestamp<?");
			pstmt.setLong(1, lTimeOld);
			pstmt.executeUpdate();
		}
		catch (Exception ex)
		{
			logger.error("!! Error deleting old messages from the database: "
					+ ex.getLocalizedMessage());
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception e)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);

            // We will always indicate that we tried to clean things up, even in an error condition.
            // We don't want to hammer on this function if there is some configuration issue.
            dtLastMessageCleanup = new Date();
		}
	}

	private void sendDiscoveryJMSMessage()
	{
		try
		{
			if (pubJMS == null)
			{
				logger.trace("sendDiscoveryJMSMessage: JMS disabled. Not sending discovery messages.");
				return;
			}
	
			String ourIp = getOwnIp();
			
			DiscoveryInfo discoveryInfo = new DiscoveryInfo(	ourIp, 
																ConfigOptions.INSTANCE.getControllerInfo().getPort(),
																NetInterface.getHostname(), 
																ConfigOptions.INSTANCE.getControllerInfo().getVenue(), 
																ConfigOptions.INSTANCE.getControllerInfo().getModel(), 
																ConfigOptions.INSTANCE.getControllerInfo().getVenue(), 
																Processor.INSTANCE.getHaStatus(),
																DiscoveryInfo.getSerialversionuid(), 
																ConfigOptions.INSTANCE.getESBInfo().getJmsDiscoveryTimeSec(),
																DateUtils.format(ConfigOptions.INSTANCE.getConfigurationChangedDate().getTime()));
	
			try
			{
				String facilityType = ConfigOptions.INSTANCE.getControllerInfo().getModelName();
				
				ObjectMapper om = new ObjectMapper();
				String text;
				text = om.writeValueAsString(discoveryInfo);
				javax.jms.TextMessage msg = sessJMS.createTextMessage();
				msg.setText(text);
				msg.setStringProperty("xbrc_type", "DISCOVERY");			// leave here for historical reasons
				msg.setStringProperty("xbrc_message_type", "DISCOVERY");
				msg.setStringProperty("xbrc_facility", ConfigOptions.INSTANCE.getControllerInfo().getVenue());
				msg.setStringProperty("xbrc_facility_type", facilityType);
				msg.setStringProperty("xconnect_parkid", XBRCController.getInstance().getProperties().getProperty("nge.xconnect.parkid"));				
				
				// TODO: review whether to uncomment this
				// msg.setJMSExpiration(ConfigOptions.INSTANCE.getESBInfo().getJmsDiscoveryTimeSec() * 1000);
				
				pubJMS.send(msg);
				sessJMS.commit();
	
				logger.trace("Sent JMS discovery message. " + text);
			}
			catch (Exception e)
			{
				logger.error("Failed to publish discovery message. Closing JMS, will retry later.", e);
	
				// set the flag to close the handles
				closeHandles = true;
			}
		}
		catch(Exception e)
		{
        	logger.error(ExceptionFormatter.format("Error setting up DISCOVERY message", e));
		}
	}

	private String getOwnIp()
	{
		String sOwnIp = "";
		try
		{
			Collection<String> iplist = NetInterface
					.getOwnIpAddress(ConfigOptions.INSTANCE.getControllerInfo()
							.getDiscoveryNetPrefix());
			
			// if got nothing, try without prefix
			if (iplist.size()==0)
				iplist = NetInterface.getOwnIpAddress(null);
			
			if (iplist.size() > 0)
				sOwnIp = iplist.iterator().next();
		}
		catch (SocketException e)
		{
			logger.error("Failed to get our own IP address",e);
		}
		return sOwnIp;
	}

	@Override
	public void publishMessage(XBRCMessage msg, boolean sendToJMS, boolean sendToHttp)
	{
		Connection conn = null;

		try
		{
			conn = Controller.getInstance().getPooledConnection();
			logger.trace("Writing JMS message to database");
			writePayloadToDatabase(msg.getType(), msg.getTimestamp(),
					msg.getPayload(), sendToJMS, sendToHttp, conn);
			logger.trace("Written");
		}
		catch (Exception ex)
		{
			logger.error("Error publishing message: "
					+ ex.getLocalizedMessage());
		}
		finally
		{
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void sendMessage(Object client, XBRCMessage msg)
	{
	}
	
	private static String formatTime(long lTime)
	{
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}

	public void setProcessConfigChange(boolean processConfigChange)
	{
		this.processConfigChange = processConfigChange;
	}
	
	public void forceSendingHAChangeDiscoveryMessage()
	{
		// Send the discovery any time within next 6 seconds. We use random time
		// to guard against the two xBRC pairs sending Discovery messages at the 
		// same time to each other possibly causing a flip-flop between master to slave.
		forceDiscoverySendTime = new Date(new Date().getTime() 
				+ (long)(Math.random() * 6000));
	}

	@Override
	public void onMessage(Message message)
	{
    	TextMessage textMessage = null;
    	
    	try
        {
        	// Cast as text message, all messages
        	// from the xBRC are text messages.
        	textMessage = (TextMessage) message;

        	// Grab the message properties.
        	if(textMessage.propertyExists("xbrc_facility"))
        	{
         		String facility = textMessage.getStringProperty("xbrc_facility");
         		if (!facility.equals(ConfigOptions.INSTANCE.getControllerInfo().getVenue()))
         		{
         			logger.error("Received HA message for incorrect facility");
         			return;
         		}
        	}
        	
        	// enqueue the message
			String messageType = "";
	    	if(textMessage.propertyExists("xbrc_message_type"))
	    	{
	    		messageType = textMessage.getStringProperty("xbrc_message_type").toUpperCase(); 
	    	}
	    	
	    	logger.trace("Received JMS message of type: " + messageType);
	    	
	    	if (messageType.isEmpty())
	    	{
	    		logger.error("Empty message type in HA message");
	    		return;
	    	}
	
	    	String messageText = textMessage.getText();
	    	
        	HAMessage ha = new HAMessage();
        	ha.setMessageType(messageType);
        	ha.setMessageText(messageText);
        	
        	synchronized(listHA)
        	{
        		listHA.add(ha);
        	}
        	
        }
    	catch(Exception ex)
    	{
    		logger.error(ExceptionFormatter.format("Error while processing HA message", ex));
    	}
        	
		
	}

	@Override
	public void onException(JMSException ex)
	{
		logger.error(ExceptionFormatter.format("JMS error: " + ex.getMessage(), ex));

		// set the flag to tell the main loop to shut thigns down and retry later
		closeHandles = true;
	}
	
	private void closeJMSHandles()
	{
		logger.warn("Closing JMS connection");
		
		// Close everything down and try again later
		if (consumerHA != null)
			try
			{
				consumerHA.close();
			}
			catch(Exception e)
			{
			}
			finally
			{
				consumerHA = null;
			}
		
		if (pubJMS != null)
			try
			{
				pubJMS.close();
			}
			catch(Exception e)
			{
			}
			finally
			{
				pubJMS = null;
			}
		
		if (sessJMS != null)
			try
			{
				sessJMS.close();
			}
			catch(Exception e)
			{
			}
			finally
			{
				sessJMS = null;
			}
		
		if (connJMS != null)
			try
			{
				connJMS.close();
			}
			catch(Exception e)
			{
			}
			finally
			{
				connJMS = null;
			}
	}
}
