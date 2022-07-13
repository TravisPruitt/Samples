package com.disney.xband.jmslistener.xi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import com.disney.xband.jmslistener.configuration.XiListenerConfiguration;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.gxp.RedemptionEvent;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;

public class RedemptionEventListener extends Listener
{
	private static Logger redemptionLogger = Logger.getLogger(RedemptionEventListener.class);	

	private static String REDEMPTION_EVENT_CREATE = "{call gxp.usp_RedemptionEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private String[] acceptedEventTypes;


	public RedemptionEventListener(JmsService service)
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

			msgLogger.trace("Message: " + messageText);

            if(!isAcceptedEventType(messageText)) {
                if(redemptionLogger.isDebugEnabled()) {
                    redemptionLogger.debug("Ignoring unsupported redemption event");
                }

                return;
            }
			
			ByteArrayInputStream bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));
			
			BusinessEvent event = XmlUtil.convertToPojo(bais, BusinessEvent.class);

			String payload = event.getPayload();
			
			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");

                        //
			// BUGBUG - There are various message formats on this topic. We are assuming only one format and will encounter
			// an exception when the format of other messages occurs. We need to switch on message type and process those
			// needed, and ignore the rest.
			//

			ObjectMapper mapper = new ObjectMapper(); 
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
			RedemptionEvent redemptionEvent = mapper.readValue(payload, RedemptionEvent.class);

			dbConnection = this.service.getConnectionPool().createConnection();
	
			long guestId = ((XiService) this.service).getSecureIdCache().get(event.getGuestIdentifier());
	
			statement = dbConnection.prepareCall(REDEMPTION_EVENT_CREATE);
			statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", event.getEventType());
			statement.setString("@BusinessEventSubType", event.getSubType());
			statement.setString("@ReferenceID", event.getReferenceId());
			statement.setLong("@GuestID", guestId);
			statement.setString("@Timestamp", event.getTimeStamp());
			statement.setString("@CorrelationID", event.getCorrelationId());

			statement.setString("@FacilityName", String.valueOf(redemptionEvent.getEntertainmentId()));
			statement.setString("@AppointmentReason", redemptionEvent.getApntmtReason());
			statement.setString("@AppointmentStatus", redemptionEvent.getApntmtStatus());
			statement.setLong("@AppointmentID", redemptionEvent.getApntmtId());
	
			if (redemptionEvent.getCacheXpassApntmtId() != null) 
			{
				statement.setLong("@CacheXpassAppointmentID",
						redemptionEvent.getCacheXpassApntmtId());
			} 
			else 
			{
				statement.setLong("@CacheXpassAppointmentID", 0);
			}
	
			statement.setString("@TapDate", DateUtils.format(redemptionEvent.getTapDate(), null,null));
	
			long startTime = System.currentTimeMillis();
			
			statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
		}
    	catch (JMSException ex) 
		{
			redemptionLogger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		} 
    	catch (JAXBException ex) 
    	{
			redemptionLogger.error(ExceptionFormatter.format(
					"Problem parsing JSON payload of redemption message  -",
					ex));
		} 
    	catch (SQLException ex) 
    	{
			redemptionLogger.error(ExceptionFormatter.format(
					"Event could not be persisted - ",
					ex));
		} 
    	catch (IOException ex) 
		{
			redemptionLogger.error(ExceptionFormatter.format(
					"IO Error -",
					ex));
		}
    	finally
    	{
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
	}

    private boolean isAcceptedEventType(String msg) {
        if(msg == null) {
            return false;
        }

        if(this.acceptedEventTypes == null) {
            this.acceptedEventTypes = ((XiListenerConfiguration) this.service.getConfiguration()).getAcceptedEventTypes();
        }

        if(this.acceptedEventTypes != null) {
            for(String type : this.acceptedEventTypes) {
                if(msg.indexOf(type) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
