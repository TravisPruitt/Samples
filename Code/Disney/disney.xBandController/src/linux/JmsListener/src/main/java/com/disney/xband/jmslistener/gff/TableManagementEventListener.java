package com.disney.xband.jmslistener.gff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.gff.BusinessEvent;
import com.disney.xband.jms.lib.entity.gff.TableEvent;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;

public class TableManagementEventListener extends Listener 
{
	private static Logger logger = Logger.getLogger(TableManagementEventListener.class);

    private static String TABLE_EVENT_CREATE = "{call gff.usp_TableEvent_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static String TABLE_GUEST_MAP_CREATE = "{call gff.usp_TableGuestOrderMap_Create(?,?,?)}";

    public TableManagementEventListener(JmsService service)
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
        ResultSet rs = null;

    	try
		{
			TextMessage textMessage = (TextMessage) message;
			String messageText = textMessage.getText();

			ByteArrayInputStream bais = new ByteArrayInputStream(
					messageText.getBytes("UTF-8"));

			BusinessEvent event = (BusinessEvent) XmlUtil.convertToPojo(bais, BusinessEvent.class);

			String payload = event.getPayLoad();

			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\"\\?>", "");
            payload = payload.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-16\"\\?>", "");

			bais = new ByteArrayInputStream(payload.getBytes("UTF-8"));

			TableEvent tableEvent = (TableEvent) XmlUtil.convertToPojo(bais, TableEvent.class);

            String sXBandId = tableEvent.getXBandId();
            long guestId;

            if(sXBandId == null) 
            {
                guestId = -1;
            }
            else
                guestId = ((GffService) this.service).getXbandIdCache().get(tableEvent.getXBandId());

            dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection
					.prepareCall(TABLE_EVENT_CREATE);

			statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", tableEvent.getEvent());
			statement.setString("@BusinessEventSubType", event.getSubType());
            String sReferenceId = event.getReferenceId();
            if(sReferenceId == null) 
            {
                sReferenceId = "";
            }
			
            statement.setString("@ReferenceID", sReferenceId);
            //String s = formatXMLDateAsString(event.getTimeStamp());
            statement.setString("@Timestamp",event.getTimeStamp());
            statement.setString("@CorrelationID", null);
			statement.setLong("@GuestId", guestId);
			//statement.setString("@GuestIdentifier", "");

            String facId = event.getFacilityId();
            if(facId == null)
            {
                facId = tableEvent.getFacilityId();
            }
            statement.setString("@FacilityName", facId);
            statement.setString("@RawMessage", messageText);

            // now the Table Specific stuff
            statement.setString("@Event", tableEvent.getEvent());
            statement.setString("@EventType", tableEvent.getEventTypeId().toString());
			statement.setString("@Source", tableEvent.getSource());
            statement.setString("@Terminal", tableEvent.getTerminal());
            statement.setString("@UserName", tableEvent.getUser());
            statement.setString("@SourceTableName", tableEvent.getTableName());
            statement.setString("@SourceTableId", (tableEvent.getTableId()).toString());

            long startTime = System.currentTimeMillis();
            
            statement.execute();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
					System.currentTimeMillis()-startTime);
			
			rs = statement.executeQuery();

            int businessEventId=-1;
            int tableId = -1;
            if(rs.next()) 
            {
                businessEventId = rs.getInt(1);
                tableId = rs.getInt(2);
                logger.debug("Created event: " + tableEvent.getEvent() + "with id:" + businessEventId);
            }

            // if there are ordernumbers -- we should be able to map them already via GUEST->Order map
            List<TableEvent.OrderNumbers> orderNumbersList = tableEvent.getOrderNumbers();
            if(orderNumbersList != null) 
            {
                if((orderNumbersList.size() != 0)
                 && (tableEvent.getXBandId() != null))
                {
                    statement.close();
                    statement = dbConnection.prepareCall(TABLE_GUEST_MAP_CREATE);
                    for(TableEvent.OrderNumbers onum : orderNumbersList) 
                    {
                        statement.setString("@OrderNumber", onum.getOrderNumber());
                        statement.setInt("@TableID", tableId);
                        statement.setInt("@BusinessEventID", businessEventId);
                        
                        startTime = System.currentTimeMillis();
                        statement.execute();
            			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().processValue(
            					System.currentTimeMillis()-startTime);
                        
                    }
                }
            }

            // intentionally discard ChangedDetails at this point
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
    		this.service.getConnectionPool().close(rs);
    		this.service.getConnectionPool().close(statement);
    		this.service.getConnectionPool().release(dbConnection);
    	}
	}

}
