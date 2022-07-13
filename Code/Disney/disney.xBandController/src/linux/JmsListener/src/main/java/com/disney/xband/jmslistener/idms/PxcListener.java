package com.disney.xband.jmslistener.idms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestName;
import com.disney.xband.jms.lib.pxc.Combination;
import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.data.GuestDataIdentifier;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.xbms.BandAssociationNotification;
import com.disney.xband.jms.lib.entity.common.GuestIdentifier;
import com.disney.xband.jms.lib.entity.xbms.Owner;
import com.disney.xband.jms.lib.pxc.CallbackMessage;
import com.disney.xband.jmslistener.MessageQueue;
import com.disney.xband.jmslistener.StatusInfo;

/*
 Event Definition:

 https://wiki.nge.wdig.com/display/NGE/Change+Event-+Publish+Changes+-+Association

 */

public class PxcListener extends IdmsListener
{
	private static Logger logger = Logger.getLogger(PxcListener.class);

	private static final String CORRELATION_ID_KEY = "CORRELATION_ID";
	private static final String SOR_NAME_KEY = "SOR_NAME";
	private static final String ORCHESTRATION_ID_KEY = "orchestrationId";
    private static final String LOCATION_VAL_COMBINATION = "COMBINATION";

    private Session session;

	private MessageQueue messageQueue;

	public PxcListener(IdmsService service, MessageQueue messageQueue, Session session)
	{
		super(service, null);
		this.messageQueue = messageQueue;
        this.session = session;
	}

	@Override
    //public void processMessage(SavedMessage message)
	public void processMessage(TextMessage message)
	{
		StatusInfo.INSTANCE.getListenerStatus()
				.incrementPxcMessagesSinceStart();

		String messageText;
		String correlationId = "";
		String orchestrationId = "";

        long startTime = System.currentTimeMillis();

		try
		{
			messageText = message.getText();

			if (message.propertyExists(CORRELATION_ID_KEY))
			{
				correlationId = message.getStringProperty(CORRELATION_ID_KEY);
			}

			if (message.propertyExists(ORCHESTRATION_ID_KEY))
			{
				orchestrationId = message
						.getStringProperty(ORCHESTRATION_ID_KEY);
			}

            if (logger.isTraceEnabled()) {
            	msgLogger.trace("Processing Message: " + messageText);
            }

			ByteArrayInputStream bais;
			bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

			BusinessEvent event = XmlUtil.convertToPojo(bais,
					BusinessEvent.class);

			String payload = event.getPayload();

			payload = payload.replaceAll("<!\\[CDATA\\[", "");
			payload = payload.replaceAll("\\]\\]>", "");

            ObjectMapper mapper = new ObjectMapper();

            if((event.getLocation() != null) && (event.getLocation().indexOf(LOCATION_VAL_COMBINATION) >= 0)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Combination notification received");
                }

                mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                final Combination notificationCmb = mapper.readValue(
                        payload, Combination.class);

                try {
                    PreprocessNotification(notificationCmb);
                }
                catch(Exception ex)
                {
                    AcknowledgeMessage(correlationId, orchestrationId, "FAILURE");
                    throw ex;
                }

                mapper = new ObjectMapper();
            }

			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			BandAssociationNotification notification = mapper.readValue(
					payload, BandAssociationNotification.class);

			try
			{
				ProcessNotification(notification);

				AcknowledgeMessage(correlationId, orchestrationId, "SUCCESS");
			}
			catch(Exception ex)
			{
				AcknowledgeMessage(correlationId, orchestrationId, "FAILURE");
				throw ex;
			}

            if (logger.isTraceEnabled()) {
                logger.trace("Finished processing PxC message");
            }
		}
		catch (JMSException ex)
		{
			logger.error(ExceptionFormatter
					.format("Issue with jms message", ex));
		}
		catch (UnsupportedEncodingException ex)
		{
			logger.error(ExceptionFormatter.format(
					"Message could not be encoded UTF-8", ex));
		}
		catch (JAXBException ex)
		{
			logger.error(ExceptionFormatter.format(
					"Message of type could not be deserialized", ex));
		}
		catch (IOException ex)
		{
			logger.error(ExceptionFormatter.format("Error processing request.",
					ex));
		}
		catch (DAOException ex)
		{
            throw new RuntimeException(ExceptionFormatter.format("Data access exception: ", ex));
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Unexpected Error.", ex));
		}
        finally {
            StatusInfo.INSTANCE.getListenerStatus().getPerfPxcCallbackMsec().processValue(System.currentTimeMillis() - startTime);
        }
	}
	
	private void AcknowledgeMessage(String correlationId, String orchestrationId, String status) 
			throws JMSException, JsonGenerationException, JsonMappingException, IOException
	{
		
		// Acknowledge message received.
		TextMessage acknowledgementMessage = session.createTextMessage();

		acknowledgementMessage.setStringProperty(CORRELATION_ID_KEY,
				correlationId);
		acknowledgementMessage.setStringProperty(SOR_NAME_KEY, "IDMS");
		acknowledgementMessage.setStringProperty(ORCHESTRATION_ID_KEY,
				orchestrationId);

		CallbackMessage callbackMessage = new CallbackMessage();
		callbackMessage.setSor("IDMS");
		callbackMessage.setStatus(status);
		callbackMessage.setOrchestrationId(orchestrationId);
		
		ObjectMapper mapper = new ObjectMapper();

		String messageText = mapper
				.writeValueAsString(callbackMessage);
		acknowledgementMessage.setText(messageText);

		this.messageQueue.add(acknowledgementMessage);
		
	}

    private void PreprocessNotification(Combination notification)
            throws IOException, JMSException, DAOException {
        final List<GuestIdentifier> previousGuestIdentifiers = notification.getPreviousGuestIdentifiers();
        final List<GuestIdentifier> newGuestIdentifiers =
                notification.getOwner() == null ? null : notification.getOwner().getGuestIdentifiers();
        final List<GuestIdentifier> savedGuestIdentifiers = new ArrayList<GuestIdentifier>();

        if ((previousGuestIdentifiers == null) || (newGuestIdentifiers == null)) {
            logger.warn("Assertion failed: (previousGuestIdentifiers != null) && (newGuestIdentifiers != null)");
            return;
        }

        final GuestData prevGuestData = GetGuest(previousGuestIdentifiers);

        if (prevGuestData == null) {
            logger.warn("Assertion failed: prevGuestData != null");
            return;
        }

        final GuestData newGuestData = GetGuest(newGuestIdentifiers, prevGuestData);

        if (newGuestData == null) {
            if(logger.isInfoEnabled()) {
                logger.info("New and old guest sections reference the same entry");
            }

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Previous guest found by " + prevGuestData.getFoundBy() + "=" + prevGuestData.getFoundByValue());
            logger.debug("New guest found by " + newGuestData.getFoundBy() + "=" + newGuestData.getFoundByValue());
        }

        String xid = null;
        String xbmsLinkId = null;

        for (GuestDataIdentifier newGuestIdentifier : newGuestData.getIdentifierList()) {
            if (newGuestIdentifier.getType().compareToIgnoreCase("xid") == 0)
            {
                xid = newGuestIdentifier.getValue();
            }
            else {
                if (newGuestIdentifier.getType().compareToIgnoreCase("xbms-link-id") == 0) {
                    xbmsLinkId = newGuestIdentifier.getValue();
                }
            }
        }

        if ((xid == null) && (xbmsLinkId == null)) {
            logger.error("Assertion failed: (xid != null) || (xbms-link-id != null)");
            return;
        }

        for (GuestIdentifier previousGuestIdentifier : previousGuestIdentifiers) {
            if (
                (previousGuestIdentifier.getType().compareToIgnoreCase("xbandid") == 0) ||
                (previousGuestIdentifier.getType().compareToIgnoreCase("xband-id") == 0)
            ) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Reassigning xband " + previousGuestIdentifier.getValue() + " to guest " + newGuestData.getGuestId());
                }

                // Reassigning xband to a new guest
                ReassignBand(newGuestData.getGuestId(), previousGuestIdentifier.getValue());
            }
        }

        // Delete all identifiers from the previous guest
        for(com.disney.xband.idms.lib.data.GuestDataIdentifier previousGuestIdentifier : prevGuestData.getIdentifierList()) {
            // Save the identifier we are about to delete
            if(
                !previousGuestIdentifier.getType().equalsIgnoreCase("xid") &&
                ((IdmsService) this.service).getGuestLocators().contains(previousGuestIdentifier.getType())
            ) {
                // Save guest identifier
                final GuestIdentifier tmp = new GuestIdentifier();
                tmp.setType(previousGuestIdentifier.getType());
                tmp.setValue(previousGuestIdentifier.getValue());
                savedGuestIdentifiers.add(tmp);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Deleting " + previousGuestIdentifier.getType() + " from guest " + prevGuestData.getGuestId());
            }

            // Deleting the identifier from the previous guest
            DeleteGuestIdentifier("guestId", String.valueOf(prevGuestData.getGuestId()),
                    previousGuestIdentifier.getType(),
                    previousGuestIdentifier.getValue());
        }

        // Add all saved identifiers to the new guest
        for(GuestIdentifier savedIdentifier : savedGuestIdentifiers) {
            try {
                if(xid != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adding " + savedIdentifier.getType() + " to guest xid=" + xid);
                    }

                    CreateGuestIdentifier("xid", xid, savedIdentifier.getType(), savedIdentifier.getValue());
                }
                else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Adding " + savedIdentifier.getType() + " to guest xbms-link-id=" + xbmsLinkId);
                    }

                    CreateGuestIdentifier("xbms-link-id", xbmsLinkId, savedIdentifier.getType(), savedIdentifier.getValue());
                }
            }
            catch(Exception e) {
                if(!(e instanceof SQLException) || (e.toString().indexOf("duplicate key") < 0)) {
                    throw new DAOException(e);
                }
            }
        }

        // Now update new guest with data from the previous guest
        final Guest updateGuest = new Guest();

        updateGuest.setGuestId(newGuestData.getGuestId());
        updateGuest.setIDMSTypeName(prevGuestData.getIDMSTypeName() != null ? prevGuestData.getIDMSTypeName() : newGuestData.getIDMSTypeName());
        updateGuest.setDateOfBirth(prevGuestData.getDateOfBirth() != null ? prevGuestData.getDateOfBirth() : newGuestData.getDateOfBirth());
        final GuestName name = new GuestName();
        name.setFirstName(prevGuestData.getFirstName() != null ? prevGuestData.getFirstName() : newGuestData.getFirstName());
        name.setLastName(prevGuestData.getLastName() != null ? prevGuestData.getLastName() : newGuestData.getLastName());
        name.setMiddleName(prevGuestData.getMiddleName() != null ? prevGuestData.getMiddleName() : newGuestData.getMiddleName());
        name.setTitle(prevGuestData.getTitle() != null ? prevGuestData.getTitle() : newGuestData.getTitle());
        name.setSuffix(prevGuestData.getSuffix() != null ? prevGuestData.getSuffix() : newGuestData.getSuffix());
        updateGuest.setName(name);
        updateGuest.setEmailAddress(prevGuestData.getEmailAddress() != null ? prevGuestData.getEmailAddress() : newGuestData.getEmailAddress());
        updateGuest.setParentEmail(prevGuestData.getParentEmail() != null ? prevGuestData.getParentEmail() : newGuestData.getParentEmail());
        updateGuest.setCountryCode(prevGuestData.getCountryCode() != null ? prevGuestData.getCountryCode() : newGuestData.getCountryCode());
        updateGuest.setLanguageCode(prevGuestData.getLanguageCode() != null ? prevGuestData.getLanguageCode() : newGuestData.getLanguageCode());
        updateGuest.setGender(prevGuestData.getGender() != null ? prevGuestData.getGender() : newGuestData.getGender());
        updateGuest.setUserName(prevGuestData.getUserName() != null ? prevGuestData.getUserName() : newGuestData.getUserName());
        updateGuest.setVisitCount(prevGuestData.getVisitCount());
        updateGuest.setAvatar(prevGuestData.getAvatar() != null ? prevGuestData.getAvatar() : newGuestData.getAvatar());
        updateGuest.setStatus("Active");

        if (logger.isDebugEnabled()) {
            logger.debug("Updating properties of guest " + newGuestData.getGuestId());
        }

        daoFactory.getGuestDAO().UpdateGuest(updateGuest);
    }

    private void
    ProcessNotification(BandAssociationNotification notification)
    throws IOException, JMSException, DAOException
	{
		Owner owner = notification.getOwner();

		if (owner != null)
		{
			if (owner.getGuestIdentifiers() != null)
			{
				GuestData guestData = GetGuest(owner
						.getGuestIdentifiers());

				if (guestData != null)
				{
                    if(logger.isDebugEnabled()) {
                        logger.debug("Guest found by " + guestData.getFoundBy() + "=" + guestData.getFoundByValue());
                    }

					//Ensure IDMS matches all the OneView identifiers.
					for (GuestIdentifier sourceGuestIdentifier : owner
							.getGuestIdentifiers())
					{
						if (((IdmsService) this.service).getGuestLocators()
								.contains(sourceGuestIdentifier.getType()) ||
								(sourceGuestIdentifier.getType().compareToIgnoreCase("xbandid") == 0) ||
                                (sourceGuestIdentifier.getType().compareToIgnoreCase("xband-id") == 0))
						{

							boolean guestIdentifierExists = false;
							for (GuestDataIdentifier destinationGuestIdentifier : guestData.getIdentifierList())
							{
								if (sourceGuestIdentifier.getType()
										.compareToIgnoreCase(destinationGuestIdentifier.getType()) == 0 &&
									sourceGuestIdentifier.getValue()
										.compareToIgnoreCase(destinationGuestIdentifier.getValue()) == 0)
								{
									guestIdentifierExists = true;
									break;
								}
							}

							if (!guestIdentifierExists)
							{
								if((sourceGuestIdentifier.getType().compareToIgnoreCase("xbandid") == 0) ||
                                    sourceGuestIdentifier.getType().compareToIgnoreCase("xband-id") == 0)
								{
                                    if(logger.isDebugEnabled()) {
                                        logger.debug(
                                            "Reassigning xband " + sourceGuestIdentifier.getValue() +
                                            " for guest " + guestData.getGuestId()
                                        );
                                    }

									ReassignBand(guestData.getGuestId(),
											sourceGuestIdentifier.getValue());
								}
								else
								{
                                    if(logger.isDebugEnabled()) {
                                        logger.debug(
                                            "Creating/updating guest id " + sourceGuestIdentifier.getType() +
                                            " with value " + sourceGuestIdentifier.getValue()
                                        );
                                    }

                                    try {
									    CreateGuestIdentifier("guestId", String.valueOf(guestData.getGuestId()),
										    sourceGuestIdentifier.getType(),
											sourceGuestIdentifier.getValue());
                                    }
                                    catch(Exception e) {
                                        if(!(e instanceof SQLException) || (e.toString().indexOf("duplicate key") < 0)) {
                                            throw new DAOException(e);
                                        }
                                    }
								}
							}
						}
					}
					
					//Ensure IDMS doesn't have any extra identifiers
					for (GuestDataIdentifier destinationGuestIdentifier : guestData.getIdentifierList())
					{
						//Ignore xbandids as they are a special case in IDMS.
						if(
                           (destinationGuestIdentifier.getType().compareToIgnoreCase("xbandid") != 0) &&
                           (destinationGuestIdentifier.getType().compareToIgnoreCase("xband-id") != 0)
                        )
						{
							boolean guestIdentifierExists = false;
							for (GuestIdentifier sourceGuestIdentifier : owner
									.getGuestIdentifiers())
							{
								if (sourceGuestIdentifier.getType()
										.compareToIgnoreCase(destinationGuestIdentifier.getType()) == 0 &&
									sourceGuestIdentifier.getValue()
										.compareToIgnoreCase(destinationGuestIdentifier.getValue()) == 0)
								{
									guestIdentifierExists = true;
									break;
								}
							}
							
							if(!guestIdentifierExists)
							{
                                if(logger.isDebugEnabled()) {
                                    logger.debug(
                                        "Guest id " + destinationGuestIdentifier.getType() +
                                        " is not present in PxC message for guest " + guestData.getGuestId()
                                    );
                                }

                                /*
                                // A temporary workaround for "swid" being removed problem
								DeleteGuestIdentifier("guestId", String.valueOf(guestData.getGuestId()),
										destinationGuestIdentifier.getType(), 
										destinationGuestIdentifier.getValue());
							    */
							}
						}
						else
						{
							//TODO: Unassign band? Not sure what to do here.
                            if(logger.isDebugEnabled()) {
                                logger.debug("Unassign band? Not sure what to do");
                            }
						}
					}
				}
				else
				{
                    if(logger.isDebugEnabled()) {
                        logger.debug("Creating a new guest on receipt of a PxC message.");
                    }

                    CreateGuest(owner.getGuestIdentifiers());
				}
			}
		}
	}

    private boolean isSameGuest(GuestData g1, GuestData g2) {
        if(g1 == null) {
            if(g2 == null) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if(g2 == null) {
                return false;
            }
            else {
                final Map<String, String> m1 = new HashMap<String, String>(16);
                final Map<String, String> m2 = new HashMap<String, String>(16);

                for (GuestDataIdentifier gi : g1.getIdentifierList()) {
                    m1.put(gi.getType() + "-!-" + gi.getValue(), "");
                }

                for (GuestDataIdentifier gi : g2.getIdentifierList()) {
                    m2.put(gi.getType() + "-!-" + gi.getValue(), "");
                }

                if(m1.size() != m2.size()) {
                    return false;
                }

                for(String key : m1.keySet()) {
                    if(m2.get(key) == null) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private GuestData GetGuest(List<GuestIdentifier> guestIdentifiers) throws DAOException
    {
        return GetGuest(guestIdentifiers, null);
    }

	private GuestData GetGuest(List<GuestIdentifier> guestIdentifiers, GuestData skipGuest) throws DAOException
	{
		
		//First check for xid, there should only be one.
		for (GuestIdentifier g : guestIdentifiers)
		{
			if (g.getType().compareToIgnoreCase("xid") == 0)
			{
				GuestData guestData = GetGuestData(g.getType(),
						g.getValue()); 
				
				if(guestData != null)
				{
                    if((skipGuest != null) && isSameGuest(guestData, skipGuest)) {
                        continue;
                    }

                    guestData.setFoundBy(g.getType());
                    guestData.setFoundByValue(g.getValue());
					return guestData;
				}
			}
		}
		
		//Next check for xbms-link-id, there can be more than one.
		for (GuestIdentifier g : guestIdentifiers)
		{
			if (g.getType().compareToIgnoreCase("xbms-link-id") == 0)
			{
				GuestData guestData = GetGuestData(g.getType(),
						g.getValue()); 
				
				if(guestData != null)
				{
                    if((skipGuest != null) && isSameGuest(guestData, skipGuest)) {
                        continue;
                    }

                    guestData.setFoundBy(g.getType());
                    guestData.setFoundByValue(g.getValue());
					return guestData;
				}
			}
		}
		
		//Go through each guest Identifier and try and find the guest.
		//There are not matches we should create.
		for (GuestIdentifier g : guestIdentifiers)
		{
			if (((IdmsService) this.service).getGuestLocators()
					.contains(g.getType()))
			{
				GuestData guestData = GetGuestData(g.getType(),
						g.getValue()); 
				
				if(guestData != null)
				{
                    if((skipGuest != null) && isSameGuest(guestData, skipGuest)) {
                        continue;
                    }

                    guestData.setFoundBy(g.getType());
                    guestData.setFoundByValue(g.getValue());
					return guestData;
				}
			}
		}

		return null;
	}
}
