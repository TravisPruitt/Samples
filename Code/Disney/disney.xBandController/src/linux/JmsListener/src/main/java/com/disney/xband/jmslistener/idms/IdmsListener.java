package com.disney.xband.jmslistener.idms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.List;

import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestName;
import com.disney.xband.jms.lib.entity.common.GuestIdentifier;
import org.apache.log4j.Logger;

import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.model.oneview.GuestData;
import com.disney.xband.idms.lib.model.oneview.GuestDataResult;
import com.disney.xband.jmslistener.Listener;
import com.disney.xband.jmslistener.RetryMessageQueue;
import com.disney.xband.jmslistener.configuration.IdmsListenerConfiguration;

public abstract class IdmsListener extends Listener
{
    protected static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	// singleton
	private static Logger idmsListenerLogger = Logger.getLogger(IdmsListener.class);

	// https://wiki.nge.wdig.com/display/NGE/xBMS+Event+-+Book+xband
	// Tap to Assign. Book Xband.
	protected static DAOFactory daoFactory = DAOFactory.getDAOFactory();


	public IdmsListener(IdmsService service, RetryMessageQueue messageQueue)
	{
		super(service, messageQueue);
	}

	protected IdmsListenerConfiguration getIdmsListenerConfiguration()
	{
		return ((IdmsService) this.service).getIdmsListenerConfiguration();
	}

	public GuestData GetGuest(String identifierType, String identifierValue) throws DAOException
	{
		GuestDataResult guestDataResult =  
				daoFactory.getGuestDAO().GetGuestData(identifierType, identifierValue, null, null);
		
		if(guestDataResult == null)
		{
			return null;
		}
		
		return guestDataResult.getGuestData();
		
	}
	
	public com.disney.xband.idms.lib.data.GuestData GetGuestData(String identifierType, String identifierValue) throws DAOException
	{
		com.disney.xband.idms.lib.data.GuestData guestData =  
				daoFactory.getGuestDAO().GetGuest(identifierType, identifierValue);
		
		if(guestData == null)
		{
			return null;
		}
		
		return guestData;
	}

    public void CreateGuest(List<GuestIdentifier> ids) throws DAOException {
        if((ids == null) || (ids.size() == 0)) {
            idmsListenerLogger.warn("Guest data does not contain any identifies. Ignoring CreateGuest() request.");
            return;
        }

		// Create Guest
		final Guest guest = new Guest();
		final GuestName guestName = new GuestName();
        guestName.setFirstName("Unknown Guest");
		guest.setName(guestName);
		guest.setIDMSTypeName("Park Guest");
		final long guestId = daoFactory.getGuestDAO().SaveOneViewGuest(guest);

        // Add all known guest identifiers
        for (GuestIdentifier guestIdentifier : ids) {
            if (((IdmsService) this.service).getGuestLocators().contains(guestIdentifier.getType())) {
                CreateGuestIdentifier("guestId", String.valueOf(guestId), guestIdentifier.getType(), guestIdentifier.getValue());
            }
        }
	}

    /*
    public void CreateGuest(String firstname, String lastname,
			String guestIdType, String guestIdValue, String xbandOwnerId)
			throws DAOException
	{
		// Create Guest
		Guest guest = new Guest();
		GuestName guestName = new GuestName();

		guestName.setFirstName(firstname);
		guestName.setLastName(lastname);
		guest.setName(guestName);
		// Need to get from SF/OV
		// guest.setSwid(UUID.randomUUID().toString());
		guest.setIDMSTypeName("Park Guest");

		long guestId = daoFactory.getGuestDAO().SaveOneViewGuest(guest);

		CreateGuestIdentifier("guestId", String.valueOf(guestId), guestIdType, guestIdValue);

		CreateGuestIdentifier("guestId", String.valueOf(guestId), "xbms-link-id", xbandOwnerId);
	}
	*/

	protected void CreateGuestIdentifier(String guestIdentifierType,
			String guestIdentifierValue,
			String newGuestIdentifierType,
			String newGuestIdentifierValue) throws DAOException
	{
		com.disney.xband.idms.lib.model.GuestIdentifierPut guestIdentifier =
				new com.disney.xband.idms.lib.model.GuestIdentifierPut();
		
		guestIdentifier.setType(newGuestIdentifierType);
		guestIdentifier.setValue(newGuestIdentifierValue);

        idmsListenerLogger.info("Creating identifier of type " + guestIdentifier.getType() +
                " and value " + guestIdentifier.getValue() + " for guest id=" +
                guestIdentifierType + "=" + guestIdentifierValue);

		daoFactory.getGuestDAO().SaveGuestIdentifier(
				guestIdentifierType,
				guestIdentifierValue, guestIdentifier);
	}

	protected void DeleteGuestIdentifier(String guestIdentifierType, 
			String guestIdentifierValue,
			String newGuestIdentifierType,
			String newGuestIdentifierValue) throws DAOException
	{
		com.disney.xband.idms.lib.model.GuestIdentifierPut guestIdentifier = 
				new com.disney.xband.idms.lib.model.GuestIdentifierPut();
		
		guestIdentifier.setType(newGuestIdentifierType);
		guestIdentifier.setValue(newGuestIdentifierValue);

		daoFactory.getGuestDAO().DeleteGuestIdentifier(
				guestIdentifierType,
				guestIdentifierValue, guestIdentifier);
		
		idmsListenerLogger.info("Deleting identifier of type " + guestIdentifier.getType() +
				" and value " + guestIdentifier.getValue() + " for guest id=" +
				guestIdentifierType + "=" + guestIdentifierValue);
	}

	protected void ReassignBand(long guestId, String xbmsId) throws DAOException
	{
		daoFactory.getXBandDAO().ReassignXBand(guestId, xbmsId);
		
		idmsListenerLogger.info("Reassinging band: " + xbmsId + " to guest: " + String.valueOf(guestId));
	}

	public static String ReadResponse(URLConnection connection) throws IOException
	{
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}

			return sb.toString();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception ignore)
				{
				}
			}
		}
	}
}
