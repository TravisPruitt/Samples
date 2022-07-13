package com.disney.xband.idms.dao.sql.cache;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.ConnectionStatus;
import com.disney.xband.idms.dao.GuestDAO;
import com.disney.xband.idms.dao.XBandDAO;
import com.disney.xband.idms.dao.sql.SqlServerDAOFactory;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.ConfigurationSettings;


public class CacheSqlServerDAOFactory extends SqlServerDAOFactory {

	static ReentrantReadWriteLock readWriteLockGuest = null;
	static Lock readGuest = null;
	static Lock writeGuest = null;
	
	static ReentrantReadWriteLock readWriteLockBand = null;
	static Lock readBand = null;
	static Lock writeBand = null;
	
	static HashMap<Long, GuestData> guestIdToGuest;
	static HashMap<String, GuestData> gxpLinkIdToGuest;
	static HashMap<String, GuestData> xIdToGuest;

	static HashMap<BandLookupType, HashMap<String, BandData>> bandLookupTypeToMap = null;

	static long maxGuestCount = -1;
	static long maxBandCount = -1;
	
	static int identifierTypeGuestId = 0;
	static int identifierTypeGxpLinkId = 1;
	static int identifierTypeXId = 2;

	static
	{
		init();
	}
	
	private static void init()
	{
		readWriteLockGuest = new ReentrantReadWriteLock();
		readGuest = readWriteLockGuest.readLock();
		writeGuest = readWriteLockGuest.writeLock();
		
		readWriteLockBand = new ReentrantReadWriteLock();
		readBand = readWriteLockBand.readLock();
		writeBand = readWriteLockBand.writeLock();
		
		bandLookupTypeToMap = new HashMap<BandLookupType, HashMap<String, BandData>>();
		
	    for(BandLookupType item : EnumSet.allOf(BandLookupType.class))
	    {
	         bandLookupTypeToMap.put(item, new HashMap<String, BandData>());
	    }		
		
		guestIdToGuest = new HashMap<Long, GuestData>();
		gxpLinkIdToGuest = new HashMap<String, GuestData>();		
		xIdToGuest = new HashMap<String, GuestData>();
		
		
		maxGuestCount = Long.valueOf(ConfigurationSettings.INSTANCE.getCacheMaxGuests());
		maxBandCount = Long.valueOf(ConfigurationSettings.INSTANCE.getCacheMaxBands());
		
		if (ConfigurationSettings.INSTANCE.getCacheResetTime() != null)
		{
			String timeString = ConfigurationSettings.INSTANCE.getCacheResetTime();
			
			try
			{
				String time[] = timeString.split(":");
				
				//if (time.length != 2)
				//	bug;
					
				Calendar today = Calendar.getInstance();
				Calendar firstRun = (Calendar) today.clone();
				
				firstRun.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
				firstRun.set(Calendar.MINUTE, Integer.parseInt(time[1]));
				firstRun.set(Calendar.SECOND, 0);
			
				if (firstRun.before(today))
				{
					firstRun.add(Calendar.DAY_OF_MONTH, 1);
				}
					
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					clear();
				}
			}, firstRun.getTime(), 86400000); // Run every 24hrs * 60mins * 60secs *
			}
			catch (Exception ex)
			{
			}
		}
	}
	
	static void clear()
	{
		writeGuest.lock();
		try
		{
			writeBand.lock();
			try
			{
			    for(BandLookupType item : EnumSet.allOf(BandLookupType.class))
			    {
			         bandLookupTypeToMap.get(item).clear();
			    }
			    
				guestIdToGuest.clear();
				gxpLinkIdToGuest.clear();
				xIdToGuest.clear();
				
			}
			finally
			{
				writeBand.unlock();
			}
		}
		finally
		{
			writeGuest.unlock();
		}
	}
	
	static int MapStringToGuestIdentifierType(String identifierType)
	{
		if (identifierType.equals("guestId"))
			return identifierTypeGuestId;
		else if (identifierType.equals("gxp-link-id"))
			return identifierTypeGxpLinkId;
		else if (identifierType.equals("xid"))
			return identifierTypeXId;
		return -1;
	}	
	
	
	static public GuestData getGuest(long id)
	{
		GuestData guest = null;
		readGuest.lock();
		try
		{
			guest = guestIdToGuest.get(id);
		}
		finally
		{
			readGuest.unlock();
		}
		return guest;
	}
	
	static public GuestData getGuest(String identifierType, String identifierValue)
	{
		GuestData guest = null;
		int idType = MapStringToGuestIdentifierType(identifierType);
		
		readGuest.lock();
		try
		{
			if (idType == identifierTypeGuestId)
			{
				guest =  guestIdToGuest.get((Long.parseLong(identifierValue)));
			}
			else if (idType == identifierTypeGxpLinkId)
			{
				guest = gxpLinkIdToGuest.get(identifierValue);
			}
			else if (idType == identifierTypeXId)
			{
				guest = xIdToGuest.get(identifierValue);
			}
		}
		finally
		{
			readGuest.unlock();
		}
		
		return guest;
	}

	public static BandData getBand(long xbandId)
	{
		BandData band = null;
		HashMap<String, BandData> map = null;

		map = bandLookupTypeToMap.get(BandLookupType.XBANDID);
		
		readBand.lock();
		try
		{
			band = map.get(String.valueOf(xbandId));
		}
		finally
		{
			readBand.unlock();
		}
		
		return band;
	}
	
	public static BandData getBand(BandLookupType bandLookupType, String id)
	{
		BandData band = null;
		HashMap<String, BandData> map = null;

		map = bandLookupTypeToMap.get(bandLookupType);
		
		readBand.lock();
		try
		{
			band = map.get(id);
		}
		finally
		{
			readBand.unlock();
		}
		return band;	
	}
	
	public static void put(GuestData guest)
	{
		writeGuest.lock();
		try
		{
			if (guestIdToGuest.size() < maxGuestCount)
			{
				guestIdToGuest.put(guest.getGuestId(), guest);
				//gxpLinkIdToGuest.put(guest.getGxpLinkId(), guest);
				//xIdToGuest.put(guest.getXId(), guest);
			}
		}
		finally
		{
			writeGuest.unlock();
		}
	}
	
	public static void put(BandData band)
	{
		writeBand.lock();
		try
		{
			if (bandLookupTypeToMap.get(BandLookupType.XBANDID).size() < maxBandCount)
			{
				bandLookupTypeToMap.get(BandLookupType.XBANDID).put(String.valueOf(band.getXbandId()), band);
				bandLookupTypeToMap.get(BandLookupType.SECUREID).put(band.getSecureId(), band);
				bandLookupTypeToMap.get(BandLookupType.TAPID).put(band.getTapId(), band);
				bandLookupTypeToMap.get(BandLookupType.LRID).put(band.getLongRangeId(), band);
				bandLookupTypeToMap.get(BandLookupType.UID).put(band.getPublicId(), band);
				bandLookupTypeToMap.get(BandLookupType.BANDID).put(band.getBandId(), band);
			}
		}
		finally
		{
			writeBand.unlock();
		}
	}

	public static void remove(GuestData guest)
	{
		writeGuest.lock();
		try
		{
			guestIdToGuest.put(guest.getGuestId(), null);
			//gxpLinkIdToGuest.put(key, null);		
			//xIdToGuest(key, null);
		}
		finally
		{
			writeGuest.unlock();
		}
	}
	
	public static void remove(BandData bandData)
	{
		writeBand.lock();
		try
		{
			bandLookupTypeToMap.get(BandLookupType.XBANDID).put(String.valueOf(bandData.getXbandId()), null);
			bandLookupTypeToMap.get(BandLookupType.SECUREID).put(bandData.getSecureId(), null);
			bandLookupTypeToMap.get(BandLookupType.TAPID).put(bandData.getTapId(), null);
			bandLookupTypeToMap.get(BandLookupType.LRID).put(bandData.getLongRangeId(), null);
			bandLookupTypeToMap.get(BandLookupType.UID).put(bandData.getPublicId(), null);
			bandLookupTypeToMap.get(BandLookupType.BANDID).put(bandData.getBandId(), null);
		}
		finally
		{
			writeBand.unlock();
		}
	}
	
	@Override
	public ConnectionStatus getStatus()
	{
		ConnectionStatus status = super.getStatus();
		String message = status.getMessage();
	
		
		readGuest.lock();
		try
		{
			String guests = String.format(" Guest objects %d/%d\r\n", guestIdToGuest.size(), maxGuestCount);
			message = message + guests;		
			readBand.lock();
			try
			{
				String bands = String.format("Band objects %d/%d\r\n", bandLookupTypeToMap.get(BandLookupType.XBANDID).size(), maxBandCount);
				message = message + bands;
			}
			finally
			{
				readBand.unlock();
			}
		}
		finally
		{
			readGuest.unlock();
		}
		
		
		status.setMessage(message + "\r\nCache is enabled");
		
		return status;
	}
	
	@Override
	public GuestDAO getGuestDAO() 
	{
		return new CacheSqlGuestDAO();
	}
	
	@Override
	public XBandDAO getXBandDAO() 
	{
		return new CacheSqlXBandDAO();
	}	
}
