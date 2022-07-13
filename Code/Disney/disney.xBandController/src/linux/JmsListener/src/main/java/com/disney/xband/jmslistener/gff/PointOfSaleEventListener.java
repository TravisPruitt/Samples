package com.disney.xband.jmslistener.gff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.gff.BusinessEvent;
import com.disney.xband.jms.lib.entity.gff.TapEvent;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;

public class PointOfSaleEventListener extends Listener 
{
	private static Logger posLogger = Logger.getLogger(PointOfSaleEventListener.class);

    private static String TAP_EVENT_CREATE = "{call gff.usp_TapEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

    public PointOfSaleEventListener(JmsService service)
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

			JAXBContext jc = JAXBContext.newInstance(BusinessEvent.class);
			Unmarshaller u = jc.createUnmarshaller();
			BusinessEvent event = (BusinessEvent) u.unmarshal(bais);

			String payload = event.getPayLoad();

			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\"\\?>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-16\"\\?>", "");

			bais = new ByteArrayInputStream(payload.getBytes("UTF-8"));

			TapEvent tapEvent = (TapEvent) XmlUtil.convertToPojo(bais, TapEvent.class);

            long guestId = ((GffService) this.service).getXbandIdCache().get(tapEvent.getXBandId());

            dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection.prepareCall(TAP_EVENT_CREATE);

           	statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", tapEvent.getEvent());
			statement.setString("@BusinessEventSubType", event.getSubType());
            String sReferenceId = event.getReferenceId();
            if(sReferenceId == null) {
                sReferenceId = "";
            }
			statement.setString("@ReferenceID", sReferenceId);
			statement.setLong("@GuestId", guestId);
			statement.setString("@GuestIdentifier", "");
            //String s = formatXMLDateAsString(event.getTimeStamp());
            statement.setString("@Timestamp",event.getTimeStamp());
			statement.setString("@CorrelationID", event.getCorrelationId());
			statement.setString("@XBandID", tapEvent.getXBandId());

            String facId = event.getFacilityId();
            if(facId == null){
                facId = tapEvent.getFacilityId();
            }
            statement.setString("@FacilityName", facId);

            // xml blob
            statement.setString("@RawMessage", messageText);

            // now the Tap Specific stuff
            statement.setString("@Event", tapEvent.getEvent());
            statement.setString("@EventType", tapEvent.getEventTypeId().toString());
			statement.setString("@Source", tapEvent.getSource());
            statement.setString("@SourceType", tapEvent.getSourceType());
            statement.setString("@Terminal", tapEvent.getTerminal());
            statement.setString("@OrderNumber", tapEvent.getOrderNumber());
            statement.setString("@Lane", tapEvent.getLane());
            statement.setString("@IsBlueLaned", Boolean.toString(tapEvent.isBlueLaned()));
            statement.setString("@XPassId", tapEvent.getXPassId());
            BigInteger ps = tapEvent.getPartySize();
            int iPartySize=0;
            if(ps != null ) {
               iPartySize = Integer.parseInt( tapEvent.getPartySize().toString());
            }
            statement.setInt("@PartySize", iPartySize );
            
            long startTime = System.currentTimeMillis();
            
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);

		}
    	catch (JMSException ex) 
    	{
			posLogger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		} 
    	catch (JAXBException ex) 
		{
			posLogger.error(ExceptionFormatter.format(
					"Problem deserializing message - "
					,ex));
		} 
    	catch (IOException ex) 
    	{
			posLogger.error(ExceptionFormatter.format(
					"IO Error -",
					ex));
		} 
    	catch (SQLException ex) 
    	{
			posLogger.error(ExceptionFormatter.format(
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
