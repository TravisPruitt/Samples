package com.disney.xband.jmslistener.xi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.gxp.BookingResponse;
import com.disney.xband.jms.lib.entity.gxp.Entertainment;
import com.disney.xband.jms.lib.entity.gxp.ReturnWindow;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;
import com.disney.xband.jmslistener.configuration.XiListenerConfiguration;

public class BookingEventListener extends Listener
{
	private static Logger bookingLogger = Logger.getLogger(BookingEventListener.class);

	private static String BUSINESS_EVENT_CREATE = "{call gxp.usp_BusinessEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String ENTITILEMENT_LOCATION = "GXP.XPASS";

	public BookingEventListener(XiService service)
	{
		super(service);
	}
	
	private XiListenerConfiguration getXiListenerConfiguration()
	{
		return ((XiService) this.service).getXiListenerConfiguration();
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
			
			if(event.getLocation().compareToIgnoreCase(ENTITILEMENT_LOCATION) == 0)
			{
	
				long guestId = ((XiService) this.service).getGxpLinkIdCache().get(event.getGuestIdentifier());
		
				// Get the return window on the booked entitlement
				Entertainment entertainment = null;
				ReturnWindow returnWindow = null;
						
				BookingResponse response = getBookingResponse(event.getReferenceId());
				
				if (response != null) 
				{
					returnWindow = response.getReturnWindow();
					if(response.getEntertainments() != null)
					{
						if(response.getEntertainments().size() > 0)
						{
							entertainment = response.getEntertainments().get(0);
						}
					}
				}
				
				dbConnection = this.service.getConnectionPool().createConnection();
				statement = dbConnection.prepareCall(BUSINESS_EVENT_CREATE);
	
				if(returnWindow == null)
				{
					statement.setNull("@StartTime",java.sql.Types.VARCHAR);
					statement.setNull("@EndTime",java.sql.Types.VARCHAR);
				}
				else
				{
					statement.setString("@StartTime", returnWindow.getStartTime());
					statement.setString("@EndTime", returnWindow.getEndTime());
				}
				
				if (entertainment == null)
				{
					statement.setNull("@LocationID",java.sql.Types.BIGINT);
					statement.setNull("@EntertainmentID",java.sql.Types.BIGINT);
				}
				else
				{
					statement.setLong("@LocationID", entertainment.getLocationId());
					statement.setLong("@EntertainmentID", entertainment.getEntertainmentId());
				}
	
				
				statement.setString("@Location", event.getLocation());
				statement.setString("@BusinessEventType", event.getEventType());
				statement.setString("@BusinessEventSubType", event.getSubType());
				statement.setString("@ReferenceID", event.getReferenceId());
				statement.setLong("@GuestId", guestId);
				statement.setNull("@RawMessage",java.sql.Types.VARCHAR);
				statement.setString("@Timestamp", event.getTimeStamp());
				statement.setString("@CorrelationID", event.getCorrelationId());
				statement.registerOutParameter("@BusinessEventId",
						java.sql.Types.INTEGER);
		
				long startTime = System.currentTimeMillis();
				
				statement.execute();
	
				StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
						System.currentTimeMillis()-startTime);
			}
		} 
    	catch (JMSException ex) 
    	{
			bookingLogger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		} 
    	catch (JAXBException ex) 
		{
			bookingLogger.error(ExceptionFormatter.format(
					"Problem deserializing message - "
					,ex));
		} 
    	catch (SQLException ex) 
    	{
			bookingLogger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		} 
    	catch (UnsupportedEncodingException ex) 
		{
			bookingLogger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		}
    	catch (IOException ex) 
		{
			bookingLogger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
	}

	private BookingResponse getBookingResponse(String referernceId)
			throws JAXBException, IOException {
		BookingResponse response = null;
		InputStreamReader reader = null;

		long startMsec = System.currentTimeMillis();
		
		try 
		{
			URL url = new URL(this.getXiListenerConfiguration().getGxpCallbackRootUrl() + referernceId);

			// Call GXP to get the return window for the booking.
			URLConnection connection = url.openConnection();

			// Get the response
			reader = new InputStreamReader(
					connection.getInputStream());
			
			BufferedReader rd = new BufferedReader(reader);

			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			rd.close();

			ObjectMapper mapper = new ObjectMapper(); 
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
			response = mapper.readValue(sb.toString(),BookingResponse.class);
			
		} 
		catch (IOException ex) 
		{
			bookingLogger.error(ExceptionFormatter.format(
							"Problem communicating with GXP  -",ex));
		}
		finally
		{
			StatusInfo.INSTANCE.getListenerStatus().getPerfGxpCallbackMsec().processValue(System.currentTimeMillis()-startMsec);

			if(reader != null)
			{
				reader.close();
			}
		}

		return response;
	}

}
