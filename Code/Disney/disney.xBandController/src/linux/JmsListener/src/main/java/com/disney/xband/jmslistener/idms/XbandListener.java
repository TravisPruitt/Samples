package com.disney.xband.jmslistener.idms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.model.XbandAssociation;
import com.disney.xband.idms.lib.model.oneview.GuestData;
import com.disney.xband.idms.lib.model.oneview.GuestDataEntitlement;
import com.disney.xband.idms.lib.model.oneview.GuestDataGuest;
import com.disney.xband.idms.lib.model.oneview.GuestDataGuestIdentifier;
import com.disney.xband.idms.lib.model.oneview.GuestDataXband;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.xbms.BandAssociationNotification;
import com.disney.xband.jms.lib.entity.xbms.Entitlement;
import com.disney.xband.jms.lib.entity.xbms.Entitlements;
import com.disney.xband.jms.lib.entity.common.GuestIdentifier;
import com.disney.xband.jms.lib.entity.common.Link;
import com.disney.xband.jms.lib.entity.xbms.LinkCollection;
import com.disney.xband.jms.lib.entity.xbms.NotificationXband;
import com.disney.xband.jms.lib.entity.xbms.Owner;
import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.jms.lib.entity.xbms.Xbands;
import com.disney.xband.jmslistener.MessageQueue;
import com.disney.xband.jmslistener.RetryMessageQueue;
import com.disney.xband.jmslistener.StatusInfo;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class XbandListener extends IdmsListener
{
	// singleton
	private static Logger xbandLogger = Logger.getLogger(XbandListener.class);

    private Session session;

	/*
	 * <businessEvent> <guestIdentifier></guestIdentifier>
	 * <eventType>BOOK</eventType> <subType></subType>
	 * <location>XBMS.ORDER</location> <timestamp>March 28, 2011 9:39:01 PM
	 * UTC</timestamp>
	 * <referenceId>EE692F11-8982-47EB-9717-24EFB9C15D35</referenceId>
	 * <payload><![CDATA[]]></payload>
	 * <correlationId>21ffefd0-c279-46d8-b978-fe268868b509</correlationId>
	 * </businessEvent>
	 */
	
	private MessageQueue notificationMessageQueue;

	public XbandListener(IdmsService service, RetryMessageQueue messageQueue, MessageQueue notificationQueue, Session session)
	{
		super(service, messageQueue);
		this.notificationMessageQueue = notificationQueue;
        this.session = session;
	}

	@Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus()
				.incrementXbmsMessagesSinceStart();

		String messageText;

		try
		{
			messageText = message.getText();

            if (xbandLogger.isTraceEnabled()) {
            	msgLogger.trace("Processing Message: " + messageText);
            }

			ByteArrayInputStream bais;
			bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

			BusinessEvent event = XmlUtil.convertToPojo(bais,
					BusinessEvent.class);

			ProcessXband(event);

            if (xbandLogger.isTraceEnabled()) {
                xbandLogger.trace("Finished processing XBAND message");
            }
		}
		catch (JMSException ex)
		{
			xbandLogger.error(ExceptionFormatter.format(
					"Issue with jms message", ex));
		}
		catch (UnsupportedEncodingException ex)
		{
			xbandLogger.error(ExceptionFormatter.format(
					"Message could not be encoded UTF-8", ex));
		}
		catch (JAXBException ex)
		{
			xbandLogger.error(ExceptionFormatter.format(
					"Message of type could not be deserialized", ex));
		}
		catch (IOException ex)
		{
			xbandLogger.error(ExceptionFormatter.format(
					"Error processing request.", ex));
		}
		catch (ParseException ex)
		{
			xbandLogger.error(ExceptionFormatter.format("Error parsing JSON.",
					ex));
		}
		catch (DAOException ex)
		{
            throw new RuntimeException(ExceptionFormatter.format("Data access exception: ", ex));
		}
	}

	private void ProcessXband(BusinessEvent event) throws IOException,
			JMSException, ParseException, JAXBException, DAOException
	{
		long startTime = System.currentTimeMillis();

		URL url = new URL(this.getIdmsListenerConfiguration()
				.getXbmsRootUrl() + "xband/" + event.getReferenceId());

		// Call xBMS and get the details
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/json;charset=utf-8");

		// Get the response
		String response = ReadResponse(connection);

        if (xbandLogger.isTraceEnabled()) {
        	msgLogger.trace("XBAND details: " + response);
        }

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 

		Xband xband = mapper.readValue(response, Xband.class);

		StatusInfo.INSTANCE.getListenerStatus().getPerfXbandCallbackMsec()
				.processValue(System.currentTimeMillis() - startTime);

        //updateGuestNames(xband.getGuestIdType(), String.valueOf(xband.getGuestId()));

        // Create xband/guest association
		XbandAssociation xbandAssociation = new XbandAssociation();

        xbandAssociation.setAssignmentDateTime(parseDatetime(xband.getAssignmentDateTime()));
		xbandAssociation.setExternalNumber(xband.getExternalNumber());
		xbandAssociation.setLongRangeTag(String.valueOf(xband.getPublicId()));
		if (xband.getPrintedName() != null)
		{
			xbandAssociation.setPrintedName(xband.getPrintedName());
		}
		else
		{
			xbandAssociation.setPrintedName("PrintedName "
					+ String.valueOf(xband.getPublicId()));

		}
		
		//xbandAssociation.setProductId(xband.getProductId());

		xbandAssociation.setPublicId(String.valueOf(xband.getPublicId()));
		xbandAssociation.setSecureId(String.valueOf(xband.getSecureId()));
		xbandAssociation.setShortRangeTag(xband.getShortRangeTag());
		xbandAssociation.setUID(xband.getShortRangeTag());
		xbandAssociation.setXbmsId(xband.getXbandId());
		xbandAssociation.setPrimaryState(xband.getState());
		xbandAssociation.setSecondaryState(xband.getSecondaryState());
		xbandAssociation.setGuestIdType(xband.getGuestIdType());
		xbandAssociation.setGuestIdValue(xband.getGuestId());
		xbandAssociation.setXbandOwnerId(xband.getXbandOwnerId());
		if(xband.getXbandRequest() != null)
		{
			xbandAssociation.setXbandRequestId(xband.getXbandRequest().replace("/xband-requests/", ""));
		}

		if (xband.getBandRole() != null)
		{
			xbandAssociation.setBandType(xband.getBandRole());
		}

    	startTime = System.currentTimeMillis();
    	
		daoFactory.getXBandDAO().Create(xbandAssociation);

        if(xbandLogger.isDebugEnabled()) {
            xbandLogger.debug("XBand Association created.\n" + xbandAssociation.toString());
        }
		
		StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().processValue(
				System.currentTimeMillis()-startTime);			

		//Check if queue name has be included in the enviroment.properties file
		if(!ConfigurationProperties.INSTANCE.getIdmsListenerConfiguration().getNotificationQueue().startsWith("#"))
		{
			if(this.notificationMessageQueue != null)
			{
				GuestData guestData = GetGuest(xband.getGuestIdType(),
						xband.getGuestId());
				
				// Check if guest has a gxp link id
				for (GuestDataGuestIdentifier identifier : guestData.getGuest().getGuestIdentifiers())
				{
					if (identifier.getType().compareToIgnoreCase("gxp-link-id") == 0)
					{
						PublishBandAssociationNotification(xband, guestData);
					}
				}
			}
		}
	}

    /*
    private static boolean updateGuestNames(String guestIdType, String guestId) {
        try {
            com.disney.xband.idms.lib.data.GuestData guestInfo = daoFactory.getGuestDAO().GetGuest(guestIdType, guestId);

            if ((guestInfo != null) && (guestInfo.getLastName() != null) && (guestInfo.getLastName().startsWith("UNKNOWN:"))) {

                if (!ConfigurationProperties.INSTANCE.getAssemblyRootUrl().startsWith("#")) {
                    // Update name from SF/OV.
                    // @TODO: Get XbandRequest details from xBRMS to get guest names
                    GuestProfile guestProfile = XbandRequestListener.GetGuestProfile(guestIdType, guestId);

                    if(
                        guestInfo.getFirstName().compareToIgnoreCase(guestProfile.getFirstName()) != 0 ||
                        guestInfo.getLastName().compareToIgnoreCase(guestProfile.getLastName()) != 0
                     ) {
                        Guest updateGuest = new Guest();
                        GuestName guestName = new GuestName();

                        guestName.setLastName(guestProfile.getFirstName());
                        guestName.setFirstName(guestProfile.getLastName());
                        updateGuest.setName(guestName);
                        updateGuest.setGuestId(guestInfo.getGuestId());
                        updateGuest.setStatus(guestInfo.getStatus());

                        long startTime = System.currentTimeMillis();

                        daoFactory.getGuestDAO().UpdateGuest(updateGuest);

                        StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().processValue(
                            System.currentTimeMillis()-startTime);

                        return true;
                    }
                }
            }
        }
        catch (IOException e) {
            xbandLogger.error("updateGuestNames() call failed: ", e);
        }
        catch (DAOException e) {
            xbandLogger.error("updateGuestNames() call failed: ", e);
        }

        return false;
    }
    */

	private void PublishBandAssociationNotification(
			Xband xband, GuestData guestData) throws JMSException, JsonGenerationException,
			JsonMappingException, IOException, ParseException, JAXBException
	{

		GuestDataGuest guest = guestData.getGuest();

		String referenceId = "";

		for (GuestDataGuestIdentifier g : guest.getGuestIdentifiers())
		{
			GuestIdentifier guestIdentifier = new GuestIdentifier();

			guestIdentifier.setType(g.getType());
			guestIdentifier.setValue(g.getValue());

			if (g.getType().compareTo("xid") == 0)
			{
				referenceId = g.getValue();
			}
		}

		BusinessEvent businessEvent = new BusinessEvent();

		if (referenceId.isEmpty())
		{
			referenceId = UUID.randomUUID().toString();
		}

		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MMM-dd'T'HH:mm:ss'Z'");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

		String timestamp = dateFormatGmt.format(new Date());

		businessEvent.setLocation(this.getIdmsListenerConfiguration()
				.getNotificationQueue());

		businessEvent.setEventType("OWNERSHIP");

		businessEvent.setSubType("");
		businessEvent.setReferenceId(referenceId);
		businessEvent.setGuestIdentifier("");
		businessEvent.setTimeStamp(timestamp);
		businessEvent.setCorrelationId(UUID.randomUUID().toString());
		businessEvent.setPayload("");
		
		Owner owner = CreateOwner(guestData,xband);

		List<GuestIdentifier> guestIdentifiers = new ArrayList<GuestIdentifier>();
		List<GuestIdentifier> resultingGuestIdentifiers = new ArrayList<GuestIdentifier>();

		for (GuestDataGuestIdentifier g : guest.getGuestIdentifiers())
		{
			GuestIdentifier guestIdentifier = new GuestIdentifier();

			guestIdentifier.setType(g.getType());
			guestIdentifier.setValue(g.getValue());

			guestIdentifiers.add(guestIdentifier);
			resultingGuestIdentifiers.add(guestIdentifier);
		}
		
		GuestIdentifier guestIdentifier = new GuestIdentifier();

		guestIdentifier.setType("xbandid");
		guestIdentifier.setValue(xband.getXbandId());
		resultingGuestIdentifiers.add(guestIdentifier);

		owner.setGuestIdentifiers(guestIdentifiers);

		BandAssociationNotification b = new BandAssociationNotification();

		// Time in GMT
		b.setTimestamp(timestamp);
		b.setType("ownership");

		b.setChangeNotificationTrigger("xBand");
		b.setChangeNotificationTriggerId(String.valueOf(xband.getXbandId()));
		b.setOwner(owner);
		b.setResultingGuestIdentifiers(resultingGuestIdentifiers);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 

		String payloadText = mapper.writeValueAsString(b);

		//Serialize
		String messageText = XmlUtil.convertToXml(businessEvent, BusinessEvent.class);
		
		//Replace payload
		messageText = messageText.replaceAll("<payLoad></payLoad>", "<payLoad><![CDATA[" + payloadText + "]]></payLoad>");
		
		TextMessage textMessage = session.createTextMessage();

		textMessage.setText(messageText);

		this.notificationMessageQueue.add(textMessage);
	}

	private Owner CreateOwner(GuestData guestData, Xband xband)
	{

		LinkCollection linkCollection = new LinkCollection();
		Link self = new com.disney.xband.jms.lib.entity.common.Link();

		self.setHref(guestData.getLinks().getSelf().getHref());

		linkCollection.setSelf(self);

		Entitlements entitlements = new Entitlements();

		List<Entitlement> celebrations = new ArrayList<Entitlement>();

		for (GuestDataEntitlement e : guestData.getEntitlements())
		{
			if (e.getType().compareToIgnoreCase("celebration") == 0)
			{
				Entitlement celebration = new Entitlement();
				celebration.setId(e.getID());
				celebration.setEndTime(e.getEndDateTime());
				celebration.setStartTime(e.getStartDateTime());

				linkCollection = new LinkCollection();
				self = new Link();
				self.setHref(e.getLinks().getSelf().getHref());

				linkCollection.setSelf(self);

				celebration.setLinks(linkCollection);

				celebrations.add(celebration);
			}
		}

		Xbands xbands = new Xbands();

		self = new Link();
		for (GuestDataGuestIdentifier identifier : guestData.getGuest()
				.getGuestIdentifiers())
		{
			if (identifier.getType().compareToIgnoreCase("xid") == 0)
			{
				self.setHref("guest" + identifier.getValue()
						+ "/managed-xbands");
			}
		}

		xbands.setSelf(self);

		List<NotificationXband> entries = new ArrayList<NotificationXband>();

		for (GuestDataXband guestDataXband : guestData.xBands/* guestData.getXBands() */)
		{
			NotificationXband entry = new NotificationXband();

			entry.setPrimaryStatus(guestDataXband.getPrimaryStatus());
			entry.setSecondaryStatus(guestDataXband.getSecondaryStatus());
			entry.setPublicXBandId(guestDataXband.getPublicXBandId());
			entry.setShortRangepublicId(guestDataXband.getShortRangePublicId());
			entry.setLongRangepublicId(guestDataXband.getLongRangePublicId());
			entry.setSecureXBandId(guestDataXband.getSecureId());
			entry.setExternalNumber(guestDataXband.getExternalNumber());
			entry.setShortRangeId(guestDataXband.getShortRangeTag());
			entry.setLongRangeId(guestDataXband.getLongRangeTag());

			entries.add(entry);

		}

		//Add new band.
		NotificationXband entry = new NotificationXband();

		entry.setPrimaryStatus(xband.getState());
		entry.setSecondaryStatus(xband.getSecondaryState());
		entry.setPublicXBandId(xband.getXbandId());
		entry.setShortRangepublicId(String.valueOf(xband.getPublicId()));
		entry.setLongRangepublicId(String.valueOf(xband.getPublicId()));
		entry.setSecureXBandId(String.valueOf(xband.getSecureId()));
		entry.setExternalNumber(xband.getExternalNumber());
		entry.setShortRangeId(xband.getShortRangeTag());
		entry.setLongRangeId(String.valueOf(xband.getPublicId()));

		entries.add(entry);

		xbands.setEntries(entries);

		Owner owner = new Owner();

		owner.setLinks(linkCollection);
		owner.setEntitlements(entitlements);
		owner.setXbands(xbands);

		return owner;
	}

    private Date parseDatetime(String s) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT);
        dateFormatGmt.setTimeZone(TimeZone.getDefault());

        try {
            return dateFormatGmt.parse(s);
        }
        catch (ParseException e) {
            xbandLogger.error("Failed to parse xband's assignment date. Expecting: " + DATE_FORMAT + ". Got: " + s);
            return new Date();
        }
    }

/*	private long CreateXband(Xband xband) throws IOException, DAOException
	{
		XBandPut newBand = new XBandPut();

		newBand.setBandId(xband.getExternalNumber());
		newBand.setLongRangeTag(String.valueOf(xband.getPublicId()));
		if (xband.getPrintedName() != null)
		{
			newBand.setPrintedName(xband.getPrintedName());
		}
		else
		{
			newBand.setPrintedName("PrintedName "
					+ String.valueOf(xband.getPublicId()));

		}

		newBand.setPublicId(String.valueOf(xband.getPublicId()));
		newBand.setSecureId(String.valueOf(xband.getSecureId()));
		newBand.setShortRangeTag(xband.getShortRangeTag());
		newBand.setUID(xband.getShortRangeTag());
		newBand.setXbmsId(xband.getXbandId());

		if (xband.getBandRole() != null)
		{
			newBand.setBandType(xband.getBandRole());
		}

		long xbandId = daoFactory.getXBandDAO().Create(newBand);

		return xbandId;
	}

	private boolean AssignXband(String xbandId, GuestData guestData)
			throws IOException, NumberFormatException, DAOException
	{
		boolean assigned = false;
		
		if(guestData.getGuest()
				.getGuestIdentifiers().size() > 0)
		{
			GuestDataGuestIdentifier identifier = guestData.getGuest()
				.getGuestIdentifiers().get(0);

			assigned = daoFactory.getXBandDAO().AssignXbandToGuest(identifier.getType(), 
					identifier.getValue(),Long.parseLong(xbandId));
		}
		
		if(!assigned)
		{
			xbandLogger.error("xid for guest: " + guestData.getLinks().getSelf()  + " not found.");
		}

		return assigned;

	}


	private BandData GetXband(String publicId)
			throws IOException, DAOException
	{
		return daoFactory.getXBandDAO().GetBand(BandLookupType.PUBLICID, publicId);
		
	}*/
}
