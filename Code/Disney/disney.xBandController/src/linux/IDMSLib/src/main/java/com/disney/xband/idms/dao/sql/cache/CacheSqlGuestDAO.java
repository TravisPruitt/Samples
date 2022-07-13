package com.disney.xband.idms.dao.sql.cache;

import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.dao.sql.SqlGuestDAO;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.Guest;

public class CacheSqlGuestDAO extends SqlGuestDAO {

	public CacheSqlGuestDAO()
	{
	}
	
	@Override
	public GuestData GetGuest(String identifierType, String identifierValue) throws DAOException 
	{
		GuestData guest = null;
		
		guest = CacheSqlServerDAOFactory.getGuest(identifierType, identifierValue);
		
		if (guest == null || !guest.isBandListComplete() || guest.isIdentifierListComplete())
		{
			try
			{
				guest = super.GetGuest(identifierType, identifierValue);
				if (guest != null)
					CacheSqlServerDAOFactory.put(guest);
			}
			catch (Exception ex)
			{
			}
		}
		return guest;
	}

	@Override
	public boolean UpdateGuest(Guest guest) throws DAOException
	{
		boolean result = false;
		
		result = super.UpdateGuest(guest);
		
		if (result)
		{
			GuestData guestData = CacheSqlServerDAOFactory.getGuest(guest.getGuestId());
			if (guestData != null)
				CacheSqlServerDAOFactory.remove(guestData);
		}
		return result;
	}
}