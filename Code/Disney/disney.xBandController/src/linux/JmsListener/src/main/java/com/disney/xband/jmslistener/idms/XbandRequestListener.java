package com.disney.xband.jmslistener.idms;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.disney.xband.jmslistener.SavedMessage;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestName;
import com.disney.xband.idms.lib.model.ReservationGuest;
import com.disney.xband.jms.lib.entity.assembly.GuestProfile;
import com.disney.xband.jms.lib.entity.common.BusinessEvent;
import com.disney.xband.jms.lib.entity.xbms.CustomizationSelection;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;
import com.disney.xband.jmslistener.RetryMessageQueue;
import com.disney.xband.jmslistener.StatusInfo;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class XbandRequestListener extends IdmsListener
{
	private static Logger xbandRequestLogger = Logger.getLogger(XbandRequestListener.class);
	
	/*
	 * <businessEvent> <guestIdentifier></guestIdentifier>
	 * <eventType>BOOK</eventType> <subType></subType>
	 * <location>XBMS.XBANDREQUEST</location> <timestamp>March 28, 2011 9:39:01
	 * PM UTC</timestamp>
	 * <referenceId>EE692F11-8982-47EB-9717-24EFB9C15D35</referenceId>
	 * <payload><![CDATA[]]></payload>
	 * <correlationId>21ffefd0-c279-46d8-b978-fe268868b509</correlationId>
	 * </businessEvent>
	 */

	public XbandRequestListener(IdmsService service, RetryMessageQueue messageQueue)
	{
		super(service, messageQueue);
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

            if (xbandRequestLogger.isTraceEnabled()) {
            	msgLogger.trace("Processing Message: " + messageText);
            }

			ByteArrayInputStream bais;
			bais = new ByteArrayInputStream(messageText.getBytes("UTF-8"));

			BusinessEvent event = XmlUtil.convertToPojo(bais,
					BusinessEvent.class);

			ProcessXbandRequest(event);

            if (xbandRequestLogger.isTraceEnabled()) {
                xbandRequestLogger.trace("Finished processing XBANDREQUEST message");
            }
		}
		catch (JMSException ex)
		{
			xbandRequestLogger.error(ExceptionFormatter.format(
					"Issue with jms message", ex));
		}
		catch (UnsupportedEncodingException ex)
		{
			xbandRequestLogger.error(ExceptionFormatter.format(
					"Message could not be encoded UTF-8", ex));
		}
		catch (JAXBException ex)
		{
			xbandRequestLogger.error(ExceptionFormatter.format(
					"Message of type could not be deserialized", ex));
		}
		catch (IOException ex)
		{
			xbandRequestLogger.error(ExceptionFormatter.format(
					"Error processing request.", ex));
		}
		catch (ParseException ex)
		{
			xbandRequestLogger.error(ExceptionFormatter.format(
					"Error parsing JSON.", ex));
		}
		catch (DAOException ex)
		{
            throw new RuntimeException(ExceptionFormatter.format("Data access exception: ", ex));
		}
	}

	private void ProcessXbandRequest(BusinessEvent event) throws IOException,
			ParseException, DAOException
	{
		XbandRequest xbandRequest = GetXbandRequest(event.getReferenceId());

		// Create a guest for each guest found
		for (CustomizationSelection customizationSelection : xbandRequest
				.getCustomizationSelections())
		{
	    	long startTime = System.currentTimeMillis();

	    	//See if guest exists.
			com.disney.xband.idms.lib.data.GuestData guestData =  
					daoFactory.getGuestDAO().GetGuest( customizationSelection.getGuestIdType(), 
							String.valueOf(customizationSelection.getGuestId()));
			
			StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSQueryMsec().processValue(
    				System.currentTimeMillis()-startTime);
			
			if(guestData == null)
			{
				//Create guest if it they don't exist.
				ReservationGuest reservationGuest = new ReservationGuest();
				
				reservationGuest.setAcquisitionStartDate(xbandRequest.getAcquisitionStartDate());
				reservationGuest.setAcquisitionType(xbandRequest.getAcquisitionIdType());
				reservationGuest.setAcquisitionUpdateDate(xbandRequest.getAcquisitionUpdateDate());
				reservationGuest.setFirstName(customizationSelection.getFirstName());
				reservationGuest.setGuestIdType(customizationSelection.getGuestIdType());
				reservationGuest.setGuestIdValue(String.valueOf(customizationSelection.getGuestId()));
				reservationGuest.setLastName(customizationSelection.getLastName());
				reservationGuest.setXbandOwnerId(customizationSelection.getXbandOwnerId());
				reservationGuest.setXbandRequestId(customizationSelection.getXbandRequestId());
				reservationGuest.setIsPrimaryGuest(customizationSelection.getPrimaryGuest());
				reservationGuest.setAcquisitionId(xbandRequest.getAcquisitionId());

		    	startTime = System.currentTimeMillis();

				guestData = daoFactory.getGuestDAO().CreateReservationGuest(reservationGuest);
				
				StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().processValue(
	    				System.currentTimeMillis()-startTime);			
				
			}

            if(guestData != null) {
                if(xbandRequestLogger.isDebugEnabled()) {
                    xbandRequestLogger.debug("Created guest with id " + guestData.getGuestId());
                }
            }
            else {
                if(xbandRequestLogger.isDebugEnabled()) {
                    xbandRequestLogger.debug("Failed to create guest " + customizationSelection.getFirstName() + " " +
                            customizationSelection.getLastName());
                }

                return;
            }

			//Update name from SF/OV or from the value passed from xBMS.
			/*if(!ConfigurationProperties.INSTANCE.getAssemblyRootUrl().startsWith("#"))
			{
				GuestProfile guestProfile = GetGuestProfile(customizationSelection.getGuestIdType(),
						String.valueOf(customizationSelection.getGuestId()));
				
				UpdateGuestName(customizationSelection, guestData.getGuestId(),
						guestProfile.getFirstName(), guestProfile.getLastName(), guestData.getStatus());
			}
			else
			{
			*/
				UpdateGuestName(customizationSelection, guestData.getGuestId(),
						guestData.getFirstName(), guestData.getLastName(), guestData.getStatus());
			//}
		}
	}
	
	private void UpdateGuestName(CustomizationSelection customizationSelection, long guestId,
			String firstName, String lastName, String status) throws DAOException
	{
        if(customizationSelection == null) {
            return;
        }

        if((firstName == null) && (lastName == null)) {
            return;
        }

        if(firstName == null) {
            firstName = "";
        }

        if(lastName == null) {
            lastName = "";
        }

		if(!firstName.equals(customizationSelection.getFirstName()) ||
		   !lastName.equals(customizationSelection.getLastName()))
			{
				Guest updateGuest = new Guest();
				GuestName guestName = new GuestName();
				
				guestName.setLastName(customizationSelection.getLastName()); // Should it be "lastName"?
				guestName.setFirstName(customizationSelection.getFirstName());
				updateGuest.setName(guestName);
				updateGuest.setGuestId(guestId);
				updateGuest.setStatus(status);
				
		    	long startTime = System.currentTimeMillis();
		    	
				daoFactory.getGuestDAO().UpdateGuest(updateGuest);
				
				StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().processValue(
	    				System.currentTimeMillis()-startTime);

                if(xbandRequestLogger.isDebugEnabled()) {
                    xbandRequestLogger.debug("Updating guest name: " + customizationSelection.getFirstName() + " " +
                            customizationSelection.getLastName() + " ( guest id=" + guestId + ")");
                }
			}
	}

	private XbandRequest GetXbandRequest(String referenceId) throws IOException
	{
		long startTime = System.currentTimeMillis();

		URL url = new URL(this.getIdmsListenerConfiguration().getXbmsRootUrl()
				+ "xband-requests/" + referenceId);

		// Call xBMS and get the details
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/json;charset=utf-8");

		// Get the response
		BufferedReader rd = null;
		InputStreamReader isr = null;
		InputStream is = null;

		try
		{
			is = connection.getInputStream();
			isr = new InputStreamReader(connection.getInputStream());
			rd = new BufferedReader(isr);

			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}

            if (xbandRequestLogger.isTraceEnabled()) {
			    msgLogger.trace("XBANDREQUEST details: " + sb.toString());
            }

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
			
			XbandRequest xbandRequest = mapper.readValue(sb.toString(),
					XbandRequest.class);

			StatusInfo.INSTANCE.getListenerStatus()
					.getPerfXbandRequestCallbackMsec()
					.processValue(System.currentTimeMillis() - startTime);

			return xbandRequest;
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (Exception ignore)
				{
				}
			}

			if (isr != null)
			{
				try
				{
					isr.close();
				}
				catch (Exception ignore)
				{
				}
			}

			if (rd != null)
			{
				try
				{
					rd.close();
				}
				catch (Exception ignore)
				{
				}
			}
		}
	}
	
	private GuestProfile GetGuestProfile(String guestIdType, String guestIdValue) throws IOException
	{
		long startTime = System.currentTimeMillis();
		
		URL url = new URL(ConfigurationProperties.INSTANCE.getAssemblyRootUrl()
				+ "guest/id;" + guestIdType + "=" + guestIdValue + "/profile");

		// Call SF/OV and get the details
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/json;charset=utf-8");

		// Get the response
		BufferedReader rd = null;
		InputStreamReader isr = null;
		InputStream is = null;

		try
		{
			is = connection.getInputStream();
			isr = new InputStreamReader(connection.getInputStream());
			rd = new BufferedReader(isr);

			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}

            if (xbandRequestLogger.isDebugEnabled()) {
			    xbandRequestLogger.debug("GetGuestProfile details: " + sb.toString());
            }

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
			
			GuestProfile guestProfile = mapper.readValue(sb.toString(),
					GuestProfile.class);

			StatusInfo.INSTANCE.getListenerStatus()
					.getPerfGuestProfileCallbackMsec()
					.processValue(System.currentTimeMillis() - startTime);

			return guestProfile;
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (Exception ignore)
				{
				}
			}

			if (isr != null)
			{
				try
				{
					isr.close();
				}
				catch (Exception ignore)
				{
				}
			}

			if (rd != null)
			{
				try
				{
					rd.close();
				}
				catch (Exception ignore)
				{
				}
			}
		}
	}
}
