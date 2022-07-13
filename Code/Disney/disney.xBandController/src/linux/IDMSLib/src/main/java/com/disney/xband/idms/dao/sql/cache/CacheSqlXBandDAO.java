package com.disney.xband.idms.dao.sql.cache;

import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.dao.sql.SqlXBandDAO;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;



public class CacheSqlXBandDAO extends SqlXBandDAO {	
	

	@Override
	public BandData GetBand(BandLookupType bandLookupType, String id) throws DAOException 
	{
		BandData bandData = null;

		bandData = CacheSqlServerDAOFactory.getBand(bandLookupType, id);
		if (bandData == null)
		{
			bandData = super.GetBand(bandLookupType, id);
			if (bandData != null)
			{
				CacheSqlServerDAOFactory.put(bandData);
			}
		}	
		
		return bandData;
	}
	
	@Override
	public boolean AssignXbandToGuest(long guestId, long xbandId)
			throws DAOException 
	{
		boolean result = false;

		result = super.AssignXbandToGuest(guestId,  xbandId);

		if (result)
		{
			try
			{
				BandData bandData = CacheSqlServerDAOFactory.getBand(xbandId); 
				if (bandData != null)
					CacheSqlServerDAOFactory.remove(bandData);
				
				GuestData guest = CacheSqlServerDAOFactory.getGuest(guestId);
				if (guest != null)
					CacheSqlServerDAOFactory.remove(guest);
			}
			catch(Exception ex)
			{
			}
		}
		
		return result;
	}
	
	@Override
	public boolean UnassignXBand(long guestId, long xbandId)
			throws DAOException 
	{
		boolean result = false;

		result = super.UnassignXBand(guestId,  xbandId);

		if (result)
		{
			try
			{
				BandData bandData = CacheSqlServerDAOFactory.getBand(xbandId); 
				if (bandData != null)
					CacheSqlServerDAOFactory.remove(bandData);

				GuestData guest = CacheSqlServerDAOFactory.getGuest(guestId);
				if (guest != null)
					CacheSqlServerDAOFactory.remove(guest);
			}
			catch(Exception ex)
			{
			}
		}
		
		return result;
	}
	@Override
	public boolean UpdateType(long xbandId, String newTypeName)
			throws DAOException 
	{
		boolean result = false;

		result = super.UpdateType(xbandId,  newTypeName);
		
		if (result)
		{
			try
			{
				BandData bandData = CacheSqlServerDAOFactory.getBand(xbandId);
				if (bandData != null)
				{
					CacheSqlServerDAOFactory.remove(bandData);
					GuestData guest = bandData.getGuest();
					if (guest != null)
					{
						CacheSqlServerDAOFactory.remove(guest);
					}
				}
			}
			catch(Exception ex)
			{
			}
		}
		
		return result;
	}
}
