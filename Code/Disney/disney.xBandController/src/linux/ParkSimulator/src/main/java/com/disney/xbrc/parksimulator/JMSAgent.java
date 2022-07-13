package com.disney.xbrc.parksimulator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.jms.JMSException;

import com.disney.xbrc.parksimulator.entity.Message;


public class JMSAgent 
{
	private javax.jms.Connection connJMS = null;

	private javax.jms.Session sessJMS = null;
	private javax.jms.MessageProducer pubJMS = null;
	private Thread sendThread = null;
	private volatile boolean run = true;
	
	private String sJMSBroker;
	private String sJMSTopic;
	private String sJMSUser;
	private String sJMSPassword;
	
	// metrics
	private Integer cMessages = 0;
	private long lTimeTotal = 0;
    
	// list of pending messages to send
	private LinkedList<Message> liMessages = new LinkedList<Message>();
	
	public JMSAgent(String sJMSBroker, String sJMSTopic, String sJMSUser, String sJMSPassword)
	{
		this.sJMSBroker = sJMSBroker;
		this.sJMSTopic = sJMSTopic;
		this.sJMSUser = sJMSUser;
		this.sJMSPassword = sJMSPassword;
	}
	
	public void initialize() 
	{
		openJMSPublishingConnection();
		
		sendThread = new Thread(){ public void run() { runSendThread(); } };
		sendThread.start();
	}
	
	public void terminate() 
	{
		// wait for outgoing messages to be sent before shutting down
		waitForMessagesToSend();
		
		// set the semaphore to stop the send thread
		run = false;
		
		// wait for it to die
		try
		{
			sendThread.join(1000);
		}
		catch (InterruptedException e1)
		{
		}
		sendThread = null;
		
		// shutdown
		try
		{
			if (pubJMS!=null)
				pubJMS.close();
			
			if (sessJMS!=null)
				sessJMS.close();
			
			if (connJMS!=null)
				connJMS.close();
		}
		catch (JMSException e)
		{
		}
	}

	public void waitForMessagesToSend()
	{
		// wait for message count to go to zero
		int cMessages = getMessageCount();
		if (cMessages>0)
		{
			do
			{
				try
				{
					Thread.sleep(cMessages * 4);
				}
				catch (InterruptedException e)
				{
				}
				cMessages = getMessageCount();
			}
			while(cMessages>0);
		}
	}
	
	public double getAverageMsecPerSend()
	{
		synchronized(cMessages)
		{
			return (double) lTimeTotal / cMessages;
		}
	}
	
	public void clearStats()
	{
		synchronized(cMessages)
		{
			cMessages = 0;
			lTimeTotal = 0;
		}
	}

	public int getMessageCount()
	{
		int cMessages;
		synchronized(liMessages)
		{
			cMessages = liMessages.size();
		}
		return cMessages;
	}
	
	/*
	 * The send thread 
	 */
	private void runSendThread() 
	{
		if (pubJMS==null)
			openJMSPublishingConnection();
		
		// if we don't have one, JMS is down - just return "success" knowing we'll try later
		if (pubJMS==null)
			return;
		
		String facilityType = "AttractionModel";
		
		// send out queued messages
		while(true)
		{
			// time to quit?
			if (!run)
				break;
			
			// get one
			Message msg = null;
			synchronized(liMessages)
			{
				try
				{
					if (liMessages.size()==0)
						liMessages.wait(100);
				}
				catch (InterruptedException e)
				{
				}
				
				// if list is empty, got nothing to do
				if (liMessages.size()==0)
					continue;
				
				msg = liMessages.removeFirst();
			}

			long lStart = new Date().getTime();
			
			String sFacility = msg.getFacility();
			String sMessageType = msg.getType();
			String sMessagePayload = msg.getPayload();
			
			// now send this out
			try
			{
				javax.jms.TextMessage jmsg = sessJMS.createTextMessage();					
				jmsg.setText(sMessagePayload);
				
				jmsg.setStringProperty("xbrc_facility", sFacility);
				jmsg.setStringProperty("xbrc_facility_type", facilityType);
				jmsg.setStringProperty("xbrc_message_type", sMessageType);
				
				pubJMS.send(jmsg);
                sessJMS.commit();
			}
			catch(Exception e)
			{
				System.err.println("Error sending to JMS: " + e);
			}
			
			// update stats
			long lDuration = (new Date().getTime() - lStart);
			
			synchronized(cMessages)
			{
				cMessages++;
				lTimeTotal += lDuration;
			}
		}
		
	}
	
	
	private void openJMSConnection()
	{
		// Create a connection.
        try
        {
            javax.jms.ConnectionFactory factory;
            
            // create the connection factory
            factory = new progress.message.jclient.ConnectionFactory (sJMSBroker);
            
            // now use the factory to create our connection
            connJMS = factory.createConnection (sJMSUser, sJMSPassword);
            
            // create a session
            sessJMS = connJMS.createSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
        }
        catch (javax.jms.JMSException jmse)
        {
        	System.err.println("!! Warning: Cannot connect to JMS Broker - " + sJMSBroker); 
        }
		
	}
	
	private void openJMSPublishingConnection()
	{
		// open the connection if necessary
		if (connJMS==null || sessJMS==null)
		{
			openJMSConnection();
			if (connJMS==null || sessJMS==null)
				return;
		}
			
        try
        {
            // create a pub/sub on our topic
            javax.jms.Topic topic = sessJMS.createTopic (sJMSTopic);
            
            // create a publisher for that topic
            pubJMS = sessJMS.createProducer(topic);            
            
            // start everything going
            connJMS.start();
            
        }
        catch (javax.jms.JMSException jmse)
        {
            System.err.println("!! Warning: Cannot create publish/subscribe topic: " + sJMSTopic);  
        }

	}
		
	public void publishMessage(Message msg)
	{
		synchronized(liMessages)
		{
			liMessages.add(msg);
			liMessages.notify();
		}
	}
	
}
