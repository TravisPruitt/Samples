package com.disney.xband.jmslistener.xi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.gxp.BlueLane;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;

public class BlueLaneEventListener extends Listener 
{
	private static Logger bluelaneLogger = Logger.getLogger(BlueLaneEventListener.class);

	private static String BLUE_LANE_EVENT_CREATE = "{call gxp.usp_BlueLaneEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?)}";

	public BlueLaneEventListener(JmsService service)
	{
		super(service);
	}

	@Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus().incrementGxpMessagesSinceStart();

		java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;

    	try
		{
			String messageText = message.getText();
			
			ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));
			
			BusinessEvent event = XmlUtil.convertToPojo(bais, BusinessEvent.class);
	
			String payload = event.getPayload();
	
			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");
	
			bais = new ByteArrayInputStream(payload.getBytes("UTF-8"));
	
			BlueLane blueLane = (BlueLane)  XmlUtil.convertToPojo(bais, BlueLane.class);
	
			dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection
					.prepareCall(BLUE_LANE_EVENT_CREATE);
	
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss z yyyy");
			java.util.Date date = sdf.parse(blueLane.getTapTime());
			java.sql.Timestamp tapTime = new java.sql.Timestamp(date.getTime());
	
			long guestId = ((XiService) this.service).getSecureIdCache().get(event.getGuestIdentifier());
	
			statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", event.getEventType());
			statement.setString("@BusinessEventSubType", event.getSubType());
			statement.setString("@ReferenceID", event.getReferenceId());
			statement.setLong("@GuestId", guestId);
			statement.setString("@Timestamp", event.getTimeStamp());
			statement.setString("@CorrelationID", event.getCorrelationId());
			statement.setString("@xBandID", blueLane.getXbandId());
			statement.setString("@EntertainmentID",
					blueLane.getGxpEntertainmentId());
			statement.setString("@ReasonCode", blueLane.getReason());
			statement.setTimestamp("@TapTime", tapTime);
			statement.setString("@FacilityName", blueLane.getFacilityId());
	
			long startTime = System.currentTimeMillis();

			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);

		}
    	catch (JMSException ex) 
    	{
			bluelaneLogger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		} 
    	catch (JAXBException ex) 
		{
			bluelaneLogger.error(ExceptionFormatter.format(
					"Problem deserializing message - "
					,ex));
		} 
    	catch (IOException ex) 
    	{
			bluelaneLogger.error(ExceptionFormatter.format(
					"IO Error -",
					ex));
		} 
    	catch (SQLException ex) 
    	{
			bluelaneLogger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		} 
    	catch (ParseException ex) 
		{
			bluelaneLogger.error(ExceptionFormatter.format(
					"Tap Time could not be parsed -",
					ex));
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
	}


}
