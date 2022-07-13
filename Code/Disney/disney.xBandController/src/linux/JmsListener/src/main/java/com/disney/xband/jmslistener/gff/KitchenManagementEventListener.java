package com.disney.xband.jmslistener.gff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.jms.lib.entity.gff.BusinessEvent;
import com.disney.xband.jms.lib.entity.gff.OrderEvent;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.StatusInfo;

class KitchenManagementEventListener extends Listener
{
	private static Logger logger = Logger.getLogger(KitchenManagementEventListener.class);

	private static String ORDER_EVENT_CREATE = "{call gff.usp_OrderEvent_Create (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String GUEST_ORDER_MAP_CREATE = "{call gff.usp_GuestOrderMap_Create(?,?,?)}";
	private static String KITCHEN_INFO_CREATE = "{call gff.usp_KitchenInfo_Create(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	public KitchenManagementEventListener(JmsService service)
	{
		super(service);
	}

	@Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus()
				.incrementGffMessagesSinceStart();

		java.sql.Connection dbConnection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		long startTime = 0;
		int businessEventId = -1;
		OrderEvent orderEvent = null;

		try
		{
			String messageText = message.getText();

            if(msgLogger.isTraceEnabled()) {
			    msgLogger.trace("Processing Message: " + messageText);
            }

			ByteArrayInputStream bais = new ByteArrayInputStream(
					messageText.getBytes("UTF-8"));

			BusinessEvent event = (BusinessEvent) XmlUtil.convertToPojo(bais,
					BusinessEvent.class);

			String payload = event.getPayLoad();

			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");
			payload = payload.replaceAll("<\\?xml version=\"1.0\"\\?>", "");
			payload = payload.replaceAll(
					"<\\?xml version=\"1.0\" encoding=\"utf-16\"\\?>", "");

			bais = new ByteArrayInputStream(payload.getBytes("UTF-8"));

			orderEvent = (OrderEvent) XmlUtil.convertToPojo(bais,
					OrderEvent.class);

			dbConnection = this.service.getConnectionPool().createConnection();
			statement = dbConnection.prepareCall(ORDER_EVENT_CREATE);
			statement.setString("@Location", event.getLocation());
			statement.setString("@BusinessEventType", orderEvent.getEvent());
			statement.setString("@BusinessEventSubType", event.getSubType());
			String sReferenceId = event.getReferenceId();
			if (sReferenceId == null)
			{
				sReferenceId = "";
			}
			statement.setString("@ReferenceID", sReferenceId);
			// statement.setLong("@GuestId", guestId);
			// statement.setString("@GuestIdentifier",
			// event.getGuestIdentifier());
			// String s = formatXMLDateAsString(event.getTimeStamp());
			statement.setString("@Timestamp", event.getTimeStamp());
			statement.setString("@CorrelationID", event.getCorrelationId());
			String facId = event.getFacilityId();
			if (facId == null)
			{
				facId = orderEvent.getFacilityId();
			}
			statement.setString("@FacilityName", facId);

			// xml blob
			statement.setString("@RawMessage", messageText);

			// now the Table Specific stuff
			statement.setString("@Event", orderEvent.getEvent());
			statement.setInt("@EventType", orderEvent.getEventTypeId());
			statement.setString("@Source", orderEvent.getSource());
			statement.setString("@SourceType", orderEvent.getSourceType());
			statement.setString("@Terminal", orderEvent.getTerminal());
			statement.setString("@OrderNumber", orderEvent.getOrderNumber());
			statement.setInt("@PartySize", orderEvent.getPartySize());
			statement.setString("@UserName", orderEvent.getUser());
			statement.setBigDecimal("@OrderAmount", orderEvent.getOrderAmount());

			startTime = System.currentTimeMillis();

			rs = statement.executeQuery();

			StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec()
					.processValue(System.currentTimeMillis() - startTime);

			if (rs.next())
			{
				businessEventId = rs.getInt(1);
				logger.debug("Created event: " + orderEvent.getEvent()
						+ "with id:" + businessEventId);
			}

		}
		catch (JMSException ex)
		{
			logger.error(ExceptionFormatter.format("Issue with jms message - ",
					ex));
		}
		catch (JAXBException ex)
		{
			logger.error(ExceptionFormatter.format(
					"Problem deserializing message - ", ex));
		}
		catch (IOException ex)
		{
			logger.error(ExceptionFormatter.format("IO Error -", ex));
		}
		catch (SQLException ex)
		{
			logger.error(ExceptionFormatter.format(
					"Event could not be persisted - ", ex));
		}
		finally
		{
			this.service.getConnectionPool().close(rs);
			this.service.getConnectionPool().close(statement);
			this.service.getConnectionPool().release(dbConnection);
		}

		try
		{
			// intentionally omit the kitchen info
			OrderEvent.KitchenInfo kitchenInfo = orderEvent.getKitchenInfo();
			if (kitchenInfo != null)
			{
				/*
				 * int businessEventID private String itemId; private String
				 * itemNumber; private BigInteger cookTime; private String
				 * courseNumber; private String courseName; private String
				 * viewId; private String viewType; private String modifier1Id;
				 * private String modifier2Id; private String modifier3Id;
				 * private String itemTag; private String parentItemNumber;
				 * private String sosTag; private String state; private
				 * XMLGregorianCalendar orderStartTime;
				 */
				dbConnection = this.service.getConnectionPool().createConnection();
				statement = dbConnection.prepareCall(KITCHEN_INFO_CREATE);
				statement.setString("@OrderNumber", orderEvent.getOrderNumber());
				statement.setInt("@KitchenInfoId", businessEventId);
				statement.setString("@ItemId", kitchenInfo.getItemId());
				statement.setString("@ItemNumber", kitchenInfo.getItemNumber());
				BigInteger ct = kitchenInfo.getCookTime();
				int iCookTime = 0;
				if (ct != null)
				{
					iCookTime = Integer.parseInt(ct.toString());
				}
				statement.setInt("@CookTime", iCookTime);
				statement.setString("@CourseNumber",
						kitchenInfo.getCourseNumber());
				statement.setString("@CourseName", kitchenInfo.getCourseName());
				statement.setString("@ViewId", kitchenInfo.getViewId());
				statement.setString("@ViewType", kitchenInfo.getViewType());
				statement.setString("@Modifier1Id",
						kitchenInfo.getModifier1Id());
				statement.setString("@Modifier2Id",
						kitchenInfo.getModifier2Id());
				statement.setString("@Modifier3Id",
						kitchenInfo.getModifier3Id());
				statement.setString("@ItemTag", kitchenInfo.getItemTag());
				statement.setString("@ParentItemNumber",
						kitchenInfo.getParentItemNumber());
				statement.setString("@SosTag", kitchenInfo.getSosTag());
				statement.setString("@State", kitchenInfo.getState());
				XMLGregorianCalendar xmlGregCal = kitchenInfo
						.getOrderStartTime();
				statement.setDate("@OrderStartTime", new java.sql.Date(
						xmlGregCal.toGregorianCalendar().getTimeInMillis()));
				startTime = System.currentTimeMillis();
				statement.execute();
				StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec()
						.processValue(System.currentTimeMillis() - startTime);
			}

			// if there are guests listed -- associate them each to the
			OrderEvent.Guests guests = orderEvent.getGuests();
			if (guests != null)
			{
				List<OrderEvent.Guests.Guest> guestList = guests.getGuest();
				if (!guestList.isEmpty())
				{
                    if(statement != null) {
                        try {
					        statement.close();
                        }
                        catch(Exception ignore) {
                        }
                    }

					statement = dbConnection
							.prepareCall(GUEST_ORDER_MAP_CREATE);
					for (OrderEvent.Guests.Guest g : guestList)
					{
						long guestId = ((GffService) this.service)
								.getXbandIdCache().get(g.getxBandId());
						statement.setString("@OrderNumber",
								orderEvent.getOrderNumber());
						statement.setLong("@GuestId", guestId);
						statement.setInt("@BusinessEventId", businessEventId);

						startTime = System.currentTimeMillis();

						statement.execute();

						StatusInfo.INSTANCE
								.getListenerStatus()
								.getPerfDatabaseMsec()
								.processValue(
										System.currentTimeMillis() - startTime);
					}
				}
			}
		}
		catch (SQLException ex)
		{
			logger.error(ExceptionFormatter.format(
					"Event could not be persisted - ", ex));
		}
		finally
		{
			this.service.getConnectionPool().close(rs);
			this.service.getConnectionPool().close(statement);
			this.service.getConnectionPool().release(dbConnection);
		}
	}
}
