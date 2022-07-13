package com.disney.xband.jmslistener.gff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.gff.BusinessEvent;
import com.disney.xband.jms.lib.entity.gff.RestaurantEvent;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.StatusInfo;

public class RestaurantEventListener extends Listener 
{
	private static Logger logger = Logger.getLogger(RestaurantEventListener.class);

    private static String RESTAURANT_EVENT_CREATE = "{call gff.usp_RestaurantEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

    public RestaurantEventListener(JmsService service)
    {
    	super(service);
    }
    
    @Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus().incrementGffMessagesSinceStart();

		java.sql.Connection dbConnection = null;
    	CallableStatement statement = null;
    	
    	try
		{
			String messageText = message.getText();

			ByteArrayInputStream bais = new ByteArrayInputStream(
					messageText.getBytes("UTF-8"));

			BusinessEvent event = (BusinessEvent) XmlUtil.convertToPojo(bais, BusinessEvent.class);

			String payload = event.getPayLoad();

			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\"\\?>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-16\"\\?>", "");

			bais = new ByteArrayInputStream(payload.getBytes("UTF-8"));

			RestaurantEvent restaurantEvent = (RestaurantEvent) XmlUtil.convertToPojo(bais, RestaurantEvent.class);

            dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection.prepareCall(RESTAURANT_EVENT_CREATE);

            statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", restaurantEvent.getEvent());
			statement.setString("@BusinessEventSubType", event.getSubType());
            String sReferenceId = event.getReferenceId();
            if(sReferenceId == null) 
            {
                sReferenceId = "";
            }
			statement.setString("@ReferenceID", sReferenceId);
			//statement.setLong("@GuestId", guestId);
			//statement.setString("@GuestIdentifier", event.getGuestIdentifier());
            //String s = formatXMLDateAsString(event.getTimeStamp());
            statement.setString("@Timestamp",event.getTimeStamp());

			statement.setString("@CorrelationID", event.getCorrelationId());
			statement.setString("@FacilityName", restaurantEvent.getFacilityId());

            // xml blob
            statement.setString("@RawMessage", messageText);

            // now the Table Specific stuff
            statement.setString("@Event", restaurantEvent.getEvent());
            statement.setString("@Source", restaurantEvent.getSource());
            statement.setString("@OpeningTime", restaurantEvent.getOpeningTime());
            statement.setString("@ClosingTime", restaurantEvent.getClosingTime());
            //statement.setString("@ServiceURI", restaurantEvent.getServiceURI());
            RestaurantEvent.TableOccupancy tocc = restaurantEvent.getTableOccupancy();
            RestaurantEvent.SeatOccupancy socc = restaurantEvent.getSeatOccupancy();

            if(tocc != null) 
            {
                // available occupied dirty closed
                statement.setInt("@TableOccupancyAvailable", tocc.getAvailable());
                statement.setInt("@TableOccupancyOccupied", tocc.getOccupied());
                statement.setInt("@TableOccupancyDirty", tocc.getDirty());
                statement.setInt("@TableOccupancyClosed", tocc.getClosed());
            }
            else 
            {
                statement.setInt("@TableOccupancyAvailable", -1);
                statement.setInt("@TableOccupancyOccupied", -1);
                statement.setInt("@TableOccupancyDirty", -1);
                statement.setInt("@TableOccupancyClosed", -1);
            }

            if(socc != null) 
            {
                statement.setInt("@SeatOccupancyTotalSeats", socc.getOccupied());
                statement.setInt("@SeatOccupancyOccupied", socc.getTotalSeats());
            }
            else 
            {
                statement.setInt("@SeatOccupancyTotalSeats", -1);
                statement.setInt("@SeatOccupancyOccupied", -1);
            }

            long startTime = System.currentTimeMillis();
			
            statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);

		}
    	catch (JMSException ex) 
    	{
			logger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		} 
    	catch (JAXBException ex) 
		{
			logger.error(ExceptionFormatter.format(
					"Problem deserializing message - "
					,ex));
		} 
    	catch (IOException ex) 
    	{
			logger.error(ExceptionFormatter.format(
					"IO Error -",
					ex));
		} 
    	catch (SQLException ex) 
    	{
			logger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		} 
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
	}
	
	

}
