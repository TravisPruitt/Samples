package com.disney.xband.idms.dao;

import java.util.List;

import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestAcquisition;
import com.disney.xband.idms.lib.model.GuestEmailPut;
import com.disney.xband.idms.lib.model.GuestIdentifierPut;
import com.disney.xband.idms.lib.model.GuestLocatorCollection;
import com.disney.xband.idms.lib.model.ReservationGuest;
import com.disney.xband.idms.lib.model.GuestSearchCollection;
import com.disney.xband.idms.lib.model.ScheduledItemCollection;
import com.disney.xband.idms.lib.model.oneview.GuestDataResult;

//Interface that all GuestDAOs must support
public interface GuestDAO 
{	
	public GuestData GetGuest(String identifierType, String identifierValue) throws DAOException; // GetGuestName, GuestGuestProfile

	public GuestData GetGuestByEmail(String email) throws DAOException;	

	public boolean DeleteGuestById(long guestId) throws DAOException;

	public boolean DeleteGuestIdentifier(String identifierType, String identifierValue, GuestIdentifierPut identifier) throws DAOException;

	public boolean SaveGuestIdentifier(String identifierType, String identifierValue, GuestIdentifierPut identifier) throws DAOException;
	
	public long SaveOneViewGuest(Guest guest) throws DAOException;
	
	public boolean UpdateGuestEmail(GuestEmailPut guestEmail) throws DAOException;
	
	public boolean UpdateGuest(Guest guest) throws DAOException;

	public GuestSearchCollection SearchGuestByName(String searchName) throws DAOException;
	
	public ScheduledItemCollection GetScheduledItems(String identifierType, String identifierValue) throws DAOException;

	public GuestDataResult GetGuestData(String identifierType, String identifierValue, String startDate, String endDate) throws DAOException;

	public GuestLocatorCollection GetGuestLocators() throws DAOException;

	public GuestData CreateReservationGuest(ReservationGuest reservationGuest) throws DAOException;
	
	public List<GuestAcquisition> GetGuestAcquisitionByGuest(String identifierType, String identifierValue) throws DAOException;

	public List<GuestAcquisition> GetGuestAcquisitionByXbandRequest(String xbandRequestId) throws DAOException;

	public List<GuestAcquisition> GetGuestAcquisitionByAcquisition(String acquisitionId, String acquisitionIdType) throws DAOException;
}