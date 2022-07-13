package com.disney.xband.jmslistener.xi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import javax.jms.*;

import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.jms.lib.entity.xbrc.*;
import com.disney.xband.jms.lib.entity.xbrc.parkentry.*;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.StatusInfo;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;

public class XbrcListener extends Listener
{
	private static Logger xbrcLogger = Logger.getLogger(XbrcListener.class);

	private String ENTRY_MESSAGE_TYPE = "ENTRY";
	private String INQUEUE_MESSAGE_TYPE = "INQUEUE";
	private String MERGE_MESSAGE_TYPE = "MERGE";
	private String LOAD_MESSAGE_TYPE = "LOAD";
	private String EXIT_MESSAGE_TYPE = "EXIT";
	private String ABANDON_MESSAGE_TYPE = "ABANDON";
	private String DISCOVERY_MESSAGE_TYPE = "DISCOVERY";
	private String METRICS_MESSAGE_TYPE = "METRICS";
	private String READER_EVENT_MESSAGE_TYPE = "READEREVENT";
	private String IN_VEHICLE_MESSAGE_TYPE = "INVEHICLE";
	
	//These were added for the Park Entry Messages
	private String TAPPED_MESSAGE_TYPE = "TAPPED";
	private String BLUELANE_MESSAGE_TYPE = "BLUELANE";
	private String ABANDONED_MESSAGE_TYPE = "ABANDONED";
	private String HAS_ENTERED_MESSAGE_TYPE = "HASENTERED";
	
	//More Message Types to be added
	
	private String XBRC_FACILITY_PROPERTY = "xbrc_facility";
	private String XBRC_MESSAGE_TYPE_PROPERTY = "xbrc_message_type";
	private String XBRC_FACILITY_TYPE_PROPERTY = "xbrc_facility_type";
	
    public XbrcListener(JmsService service)
    {
    	super(service);
    }
    
    @Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus().incrementXbrcMessagesSinceStart();

		String messageType = "";
    	String facility = "";
    	String facilityTypeName = "";
    	
    	try
        {
        	//Cast as text message, all messages
        	//from the xBRC are text messages.

        	//Grab the message properties.
        	if(message.propertyExists(XBRC_FACILITY_PROPERTY))
        	{
         		facility = message.getStringProperty(XBRC_FACILITY_PROPERTY);
        	}
        	
        	if(message.propertyExists(XBRC_MESSAGE_TYPE_PROPERTY))
        	{
        		messageType = message.getStringProperty(XBRC_MESSAGE_TYPE_PROPERTY).toUpperCase();
        	}
        	
        	if(message.propertyExists(XBRC_FACILITY_TYPE_PROPERTY))
        	{
        		facilityTypeName = message.getStringProperty(XBRC_FACILITY_TYPE_PROPERTY);
        	}
        	msgLogger.trace("Message: " + message.getText());
        	
	        // Check the message type.
			if(messageType.equals(ENTRY_MESSAGE_TYPE) || 
			   messageType.equals(INQUEUE_MESSAGE_TYPE) || 
			   messageType.equals(MERGE_MESSAGE_TYPE))
            {
			     saveMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(LOAD_MESSAGE_TYPE))
	        {
	        	saveLoadMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(EXIT_MESSAGE_TYPE))
	        {
	        	saveExitMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(ABANDON_MESSAGE_TYPE))
	        {
	        	saveAbandonMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(METRICS_MESSAGE_TYPE))
	        {
	        	saveMetricsMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(READER_EVENT_MESSAGE_TYPE))
	        {
	        	saveReaderEventMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.equals(DISCOVERY_MESSAGE_TYPE))
	        {
	            xbrcLogger.debug("Discovery Message: \n" + message.getText());
	        	
	        }
	        else if(messageType.equals(IN_VEHICLE_MESSAGE_TYPE))
	        {
	        	saveInVehicleMessage(messageType, facility, facilityTypeName, message);
	        	
	        }
	        else if(messageType.equals(TAPPED_MESSAGE_TYPE))
	        {
	        	saveTappedMessage(messageType, facility, facilityTypeName, message);
	        }
			//These are all storing the same data and all need to just save an ParkEntryEvent
	        else if(messageType.equals(BLUELANE_MESSAGE_TYPE) ||
	        		messageType.equals(HAS_ENTERED_MESSAGE_TYPE) ||
	        		messageType.equals(ABANDONED_MESSAGE_TYPE))
	        {
	        	saveParkEntryMessage(messageType, facility, facilityTypeName, message);
	        }
	        else if(messageType.isEmpty())
	        {
	            xbrcLogger.debug("No Message Type found. \nMessage Text:\n" + message.getText());
	        }
	        else 
	        {
	            xbrcLogger.debug("Message Type not currently being processed by listener: " + messageType);
	        }
        }
        catch(ClassNotFoundException cnfe)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("JDBC Driver type could not be found",cnfe));
        }
        catch (JAXBException je)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("Message of type " + messageType 
	        				+ " could not be deserialized",je));
        }
        catch(SQLException se)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("Message of type " + messageType  +
	        				" could not be persisted",se) );
        }
        catch (JMSException jms)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("Issue with jms message",jms));
        }
        catch (UnsupportedEncodingException u)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("Message could not be encoded UTF-8",u));
        }
        catch (Exception ex)
        {
	        xbrcLogger.error(
	        		ExceptionFormatter.format("Unexpected exception",ex));
        }
	}
    
    private void saveMessage(String messageType, 
    		String facility,
    		String facilityTypeName,
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
    	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
	    	String messageText = textMessage.getText();
	    	
			ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));
					
			EventMessagePayload em = XmlUtil.convertToPojo(bais, MessagePayload.class, EventMessagePayload.class);
	
			dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection.prepareCall("{call rdr.usp_Event_Create(?,?,?,?,?,?,?,?,?,?)}");
			
	    	if(em.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",em.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
			statement.setBoolean("@xPass",em.getMessage().getxPass() != null ? em.getMessage().getxPass() : false);
			statement.setString("@FacilityName",facility);
			statement.setString("@FacilityTypeName",facilityTypeName);
			statement.setString("@EventTypeName",messageType);
			statement.setString("@ReaderLocation",em.getMessage().getReaderLocation());
			statement.setString("@Timestamp",em.getMessage().getTimestamp());
			statement.setString("@BandType", em.getMessage().getBandtype());
			statement.setString("@RawMessage", messageText);
			statement.registerOutParameter("@EventId", java.sql.Types.INTEGER);
			 
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }
    
    private void saveLoadMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;
    	
    	try
		{
    		String messageText = textMessage.getText();

    		ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

	    	LoadMessagePayload loadMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, LoadMessagePayload.class);
	
			dbConnection = this.service.getConnectionPool().createConnection();
	        statement = dbConnection.prepareCall("{call rdr.usp_LoadEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?)}");
	        
	    	if(loadMessage.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",loadMessage.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
	        statement.setBoolean("@xPass",loadMessage.getMessage().getxPass() != null ? loadMessage.getMessage().getxPass() : false);
	        statement.setString("@FacilityName",facility);
	        statement.setString("@FacilityTypeName",facilityTypeName);
	        statement.setString("@EventTypeName",messageType);
	        statement.setString("@ReaderLocation",loadMessage.getMessage().getReaderLocation());
	        statement.setString("@Timestamp",loadMessage.getMessage().getTimestamp());
	        statement.setInt("@WaitTime",loadMessage.getMessage().getWaitTime());
	        statement.setInt("@MergeTime",loadMessage.getMessage().getMergeTime());
	        statement.setString("@CarID",loadMessage.getMessage().getCarId());
			statement.setString("@BandType", loadMessage.getMessage().getBandtype());
			statement.setString("@RawMessage", messageText);
	         
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }
    	
    private void saveExitMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
    		String messageText = textMessage.getText();
    		
	     	ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

	     	ExitMessagePayload exitMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, ExitMessagePayload.class);
	    	
			dbConnection = this.service.getConnectionPool().createConnection();

	    	statement = dbConnection.prepareCall("{call rdr.usp_ExitEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	    	
	    	if(exitMessage.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",exitMessage.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
	        statement.setBoolean("@xPass",exitMessage.getMessage().getxPass() != null ? exitMessage.getMessage().getxPass() : false);
	        statement.setString("@FacilityName",facility);
	        statement.setString("@FacilityTypeName",facilityTypeName);
	        statement.setString("@EventTypeName",messageType);
	        statement.setString("@ReaderLocation",exitMessage.getMessage().getReaderLocation());
	        statement.setString("@Timestamp",exitMessage.getMessage().getTimestamp());
	        statement.setInt("@WaitTime",exitMessage.getMessage().getStatistics().getWaitTime());
	        statement.setInt("@MergeTime",exitMessage.getMessage().getStatistics().getMergeTime());
	        statement.setInt("@TotalTime",exitMessage.getMessage().getStatistics().getTotalTime());
	        statement.setString("@CarID",exitMessage.getMessage().getCarId());
			statement.setString("@BandType", exitMessage.getMessage().getBandtype());
			statement.setString("@RawMessage", messageText);
	                
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }    
        
    private void saveAbandonMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
    		String messageText = textMessage.getText();

	    	ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

	    	AbandonMessagePayload abandonMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, AbandonMessagePayload.class);
	    	
			dbConnection = this.service.getConnectionPool().createConnection();

	    	statement = dbConnection.prepareCall("{call rdr.usp_AbandonEvent_Create(?,?,?,?,?,?,?,?,?,?)}");
	    	if(abandonMessage.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",abandonMessage.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
	        statement.setBoolean("@xPass",abandonMessage.getMessage().getxPass() != null ? abandonMessage.getMessage().getxPass() : false);
	        statement.setString("@FacilityName",facility);
	        statement.setString("@FacilityTypeName",facilityTypeName);
	        statement.setString("@EventTypeName",messageType);
	        statement.setString("@ReaderLocation",abandonMessage.getMessage().getReaderLocation());
	        statement.setString("@Timestamp",abandonMessage.getMessage().getTimestamp());
	        statement.setString("@LastTransmit",abandonMessage.getMessage().getLastXmit());
			statement.setString("@BandType", abandonMessage.getMessage().getBandtype());
			statement.setString("@RawMessage", messageText);
	         
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
   }
    
    private void saveReaderEventMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
    		String messageText = textMessage.getText();

	     	ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

	     	ReaderEventMessagePayload readerEventMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, ReaderEventMessagePayload.class);
	    	
			dbConnection = this.service.getConnectionPool().createConnection();

	    	statement = dbConnection.prepareCall("{call rdr.usp_ReaderEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	    	if(readerEventMessage.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",readerEventMessage.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
	        statement.setBoolean("@xPass",readerEventMessage.getMessage().getxPass() != null ? readerEventMessage.getMessage().getxPass() : false);
	        statement.setString("@FacilityName",facility);
	        statement.setString("@FacilityTypeName",facilityTypeName);
	        statement.setString("@EventTypeName",messageType);
	        statement.setString("@ReaderLocation",readerEventMessage.getMessage().getReaderLocation());
	        statement.setString("@Timestamp",readerEventMessage.getMessage().getTimestamp());
	        statement.setString("@ReaderLocationID",readerEventMessage.getMessage().getReaderLocationId());
	        statement.setString("@ReaderName",readerEventMessage.getMessage().getReaderName());
	        statement.setString("@ReaderID",readerEventMessage.getMessage().getReaderId());
	        // See bug 6752. IsWearingPrimaryBand is no longer sent in the READEREVENT
	        statement.setBoolean("@IsWearingPrimaryBand",false);
			statement.setString("@BandType", readerEventMessage.getMessage().getBandtype());
			statement.setInt("@Confidence", readerEventMessage.getMessage().getConfidence());
			statement.setString("@RawMessage", messageText);
	                     
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }    
     
    private void saveInVehicleMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, IOException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
    		String messageText = textMessage.getText();

	     	ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

	     	InVechicleMessagePayload inVehicleMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, InVechicleMessagePayload.class);
	    	
			dbConnection = this.service.getConnectionPool().createConnection();

	    	statement = dbConnection.prepareCall("{call rdr.usp_InVehicleEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	    	if(inVehicleMessage.getMessage().getGuestId() != null)
	    	{
	    		statement.setLong("@GuestID",inVehicleMessage.getMessage().getGuestId());
	    	}
	    	else
	    	{
	    		statement.setLong("@GuestID",0);
	    	}
	        statement.setBoolean("@xPass",inVehicleMessage.getMessage().getxPass() != null ? inVehicleMessage.getMessage().getxPass() : false);
	        statement.setString("@FacilityName",facility);
	        statement.setString("@FacilityTypeName",facilityTypeName);
	        statement.setString("@EventTypeName",messageType);
	        statement.setString("@ReaderLocation",inVehicleMessage.getMessage().getReaderLocation());
	        statement.setString("@Timestamp",inVehicleMessage.getMessage().getTimestamp());
	        /* BUGBUG - Remove this in favor of line below when DB supports Vehicle instead of VehicleId */ statement.setString("@VehicleId",inVehicleMessage.getMessage().getVehicle());
	        /* BUGBUG - Need to update DB schema to support statement.setString("@Vehicle",inVehicleMessage.getMessage().getVehicle()); */
	        /* BUGBUG - Need to update DB schema to support statement.setString("@Car",inVehicleMessage.getMessage().getCar()); */
	        /* BUGBUG - Need to update DB schema to support setString("@Row",inVehicleMessage.getMessage().getRow()); */
	        /* BUGBUG - Need to update DB schema to support setString("@Seat",inVehicleMessage.getMessage().getSeat()); */
	        statement.setString("@AttractionId",inVehicleMessage.getMessage().getAttractionId());
	        statement.setString("@LocationId",inVehicleMessage.getMessage().getLocationId());
	        statement.setString("@Confidence",inVehicleMessage.getMessage().getConfidence());
	        statement.setString("@Sequence",inVehicleMessage.getMessage().getSequence());
			statement.setString("@BandType", inVehicleMessage.getMessage().getBandtype());
	        statement.setString("@RawMessage", messageText);
	                     
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }    

    private void saveMetricsMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, UnsupportedEncodingException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(textMessage.getText().getBytes("UTF-8"));

			MetricsMessagePayload metricsMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, MetricsMessagePayload.class);
	
	     	dbConnection = this.service.getConnectionPool().createConnection();
		    statement = dbConnection.prepareCall("{call rdr.usp_Metric_Create(?,?,?,?,?,?,?,?,?,?)}");
		    statement.setString("@FacilityName",facility);
		    statement.setString("@FacilityTypeName",facilityTypeName);
		    statement.setString("@StartTime",metricsMessage.getMessage().getStartTime());
		    statement.setString("@EndTime",metricsMessage.getMessage().getEndTime());
		    statement.setString("@MetricTypeName","StandBy");
		    statement.setInt("@Guests",metricsMessage.getMessage().getStandBy().getGuests());
		    statement.setInt("@Abandonments",metricsMessage.getMessage().getStandBy().getAbandonments());
		    statement.setInt("@WaitTime",metricsMessage.getMessage().getStandBy().getWaitTime());
		    statement.setInt("@MergeTime",metricsMessage.getMessage().getStandBy().getMergeTime());
		    statement.setInt("@TotalTime",metricsMessage.getMessage().getStandBy().getTotalTime());
		     
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		    
		    statement.close();
		    
		    statement = dbConnection.prepareCall("{call rdr.usp_Metric_Create(?,?,?,?,?,?,?,?,?,?)}");
		    statement.setString("@FacilityName",facility);
		    statement.setString("@FacilityTypeName",facilityTypeName);
		    statement.setString("@StartTime",metricsMessage.getMessage().getStartTime());
		    statement.setString("@EndTime",metricsMessage.getMessage().getEndTime());
		    statement.setString("@MetricTypeName","xPass");
		    statement.setInt("@Guests",metricsMessage.getMessage().getxPass().getGuests());
		    statement.setInt("@Abandonments",metricsMessage.getMessage().getxPass().getAbandonments());
		    statement.setInt("@WaitTime",metricsMessage.getMessage().getxPass().getWaitTime());
		    statement.setInt("@MergeTime",metricsMessage.getMessage().getxPass().getMergeTime());
		    statement.setInt("@TotalTime",metricsMessage.getMessage().getxPass().getTotalTime());
		    
			startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }
    
    private void saveTappedMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, UnsupportedEncodingException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;
    	
    	try
    	{
    		ByteArrayInputStream bais = new ByteArrayInputStream(textMessage.getText().getBytes("UTF-8"));
    		
    		TappedMessagePayload tappedMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, TappedMessagePayload.class);
    		
    		dbConnection = this.service.getConnectionPool().createConnection();
    		//THIS IS NEEDING TO BE FILLED IN WITH THE PARAMS FROM AMAR
    		statement = dbConnection.prepareCall("{call rdr.usp_ParkEntryTappedEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?)}");
    		//Venue
			statement.setString("@FacilityName",facility);
			//XBRC model = PE in this case
			statement.setString("@FacilityTypeName",facilityTypeName);
			//Tapped
			statement.setString("@EventTypeName",messageType);
			statement.setString("@Timestamp",tappedMessage.getMessage().getTimestamp());
			statement.setString("@PublicId",tappedMessage.getMessage().getPidDecimal());
			statement.setString("@ReaderLocation",tappedMessage.getMessage().getReaderLocation().get(0));
			statement.setString("@ReaderSection",tappedMessage.getMessage().getLocationId().get(0));
			
			//statement.setBoolean("@xPass",tappedMessage.getMessage().getXpass());
			
			statement.setString("@readerdeviceid",tappedMessage.getMessage().getReaderDeviceId().get(0));
			statement.setString("@ReaderName",tappedMessage.getMessage().getReaderName().get(0));
			statement.setString("@Reason",tappedMessage.getMessage().getReason());
			statement.setString("@XbrcReferenceNo",tappedMessage.getMessage().getXbrcReferenceNo().get(0));
			statement.setString("@Sequence",tappedMessage.getMessage().getSequence());
			
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }
    
    private void saveParkEntryMessage(String messageType, 
    		String facility, 
    		String facilityTypeName, 
    		TextMessage textMessage)
    throws JAXBException, SQLException, JMSException, 
	ClassNotFoundException, UnsupportedEncodingException
    {
    	java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;
    	
    	try
    	{
    		ByteArrayInputStream bais = new ByteArrayInputStream(textMessage.getText().getBytes("UTF-8"));
    		
    		ParkEntryMessagePayload parkEntryMessage = XmlUtil.convertToPojo(bais, MessagePayload.class, ParkEntryMessagePayload.class);
    		
    		dbConnection = this.service.getConnectionPool().createConnection();
    		
    		
    		statement = dbConnection.prepareCall("{call rdr.usp_ParkEntryEvent_Create(?,?,?,?,?,?,?,?,?,?)}");
    		
    		//Venue
			statement.setString("@FacilityName",facility);
			//XBRC model = PE in this case
			statement.setString("@FacilityTypeName",facilityTypeName);
			//BLUELANE OR HASENTERED OR ABANDONED
			statement.setString("@EventTypeName",messageType);
			statement.setString("@Timestamp",parkEntryMessage.getMessage().getTimestamp());
			statement.setString("@PublicId",parkEntryMessage.getMessage().getPidDecimal().get(0));
			//Not being used any more?
			//statement.setBoolean("@xPass",bluelaneMessage.getMessage().getXpass());
			statement.setString("@Reason",parkEntryMessage.getMessage().getReason());
			statement.setString("@XbrcReferenceNo", parkEntryMessage.getMessage().getXbrcReferenceNumber().get(0));
			statement.setString("@Sequence", parkEntryMessage.getMessage().getSequence());
			if (parkEntryMessage.getMessage().getEntErrorCode() == null)
				statement.setObject("@EntErrorCode", null);
			else
				statement.setInt("@EntErrorCode", parkEntryMessage.getMessage().getEntErrorCode());
			statement.setString("@EntErrorDescription", parkEntryMessage.getMessage().getEntErrorDescription());
    		//statement.setInt("@ParkEntryEventId", parkEntryMessage.);
 
			long startTime = System.currentTimeMillis();
		
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
				System.currentTimeMillis()-startTime);
    	}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
    }
}
