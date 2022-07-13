package com.disney.xband.xbrc.lib.idms;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.xbrc.lib.idms.IDMSRequest.RequestTypeEnum;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public class IDMSResolver
{
	// defaults
	public static final int msecHttpTimeoutDefault = 1000;
	public static final int secPositiveCacheTimeoutDefault = 1800;
	public static final int secNegativeCacheTimeoutDefault = 300;
	
	// logger
	private static Logger logger = Logger.getLogger(IDMSResolver.class);

	// singleton
	public static IDMSResolver INSTANCE = new IDMSResolver();
	
	// caches
	Cache<String, Xband> cacheL2B = new Cache<String, Xband>();		// LRID to band
	Cache<String, Xband> cacheR2B = new Cache<String, Xband>();		// RFID to band
	Cache<String, Xband> cacheS2B = new Cache<String, Xband>();		// SecureId to band
	Cache<String, Guest> cacheB2G = new Cache<String, Guest>();		// BandId to Guest
	Cache<String, Guest> cacheG2G = new Cache<String, Guest>();		// GuestId to Guest
	Cache<String, List<Celebration>> cacheG2CL = 
			new Cache<String, List<Celebration>>();					// GuestId to Celebration list
	
	// Prevent multiple calls to IDMS for the same long range ID.
	// This is useful because single band ping is detected by multiple readers which all try resolve the 
	// long range ID to a band.
	
	Map<String,Lock> lockIDMSL2B = new Hashtable<String,Lock>(); 
	Map<String,Lock> lockIDMSB2G = new Hashtable<String,Lock>();
	
	// lookup threads and work queue
	private boolean bStop = false;
	private List<Thread> liThreads = new ArrayList<Thread>();
	private LinkedList<IDMSRequest> workQueue = new LinkedList<IDMSRequest>();
	
	// IO layer
	private IDMSClient client = new IDMSClient();
	
	private IDMSResolver()
	{
	}
	
	public void initialize(int cThreads)
	{
		// create the threads
		for (int i=0; i<cThreads; i++)
		{
			Thread th = new Thread()
								{
									public void run()
									{
										runSendThread();
									}
								};
		
			liThreads.add(th);
			th.start();
		}
	}
	
	public void setIDMSUrl(String sURL)
	{
		client.setIDMSUrl(sURL);
	}
	
	public void setHttpTimeout(int msec)
	{
		client.setIDMSTimeout(msec);
	}
	
	public void setCacheTimeouts(int msecPositiveTimeout, int msecNegativeTimeout)
	{
		cacheB2G.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
		cacheG2G.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
		cacheL2B.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
		cacheR2B.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
		cacheS2B.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
		cacheG2CL.setCacheTimeouts(msecPositiveTimeout, msecNegativeTimeout);
	}
	
	public void stop()
	{
		bStop = true;
		synchronized(workQueue)
		{
			workQueue.notifyAll();
		}
		
		// wait until all the threads have died or 5 seconds at the latest
		Date dtStart = new Date();
		for(;;)
		{
			boolean bAllStopped = true;
			for(Thread th : liThreads)
			{
				if (th.isAlive())
				{
					bAllStopped = false;
					break;
				}
			}
			
			if (bAllStopped || (new Date().getTime() - dtStart.getTime()) > 5000)
				break;
		}
	}
	
	public boolean isEnabled()
	{
		String sURL = client.getIDMSUrl();
		return sURL!=null && sURL.length()>0 && !sURL.startsWith("#");
	}
	
	public void clearCache()
	{
		cacheB2G.clear();
		cacheG2G.clear();
		cacheL2B.clear();
		cacheR2B.clear();
		cacheS2B.clear();
		cacheG2CL.clear();
	}
	
	public void clearCacheByGuestId(String sGuestId)
	{
		Guest g = getGuestFromGuestId(sGuestId);
		
		if (g != null)
		{
			// remove band to guest mapping
			List<Xband> li = g.getXbands();
			if (li!=null)
			{
				for (Xband xb : li)
				{
					// clear band to guest cache
					cacheB2G.clear(xb.getBandId());
				}
			}			
		}
		
		// remove guest from the cache
		cacheG2G.clear(sGuestId);
					
		// remove any celebrations for the guest
		cacheG2CL.clear(sGuestId);
	}
	
	public void clearCacheBySecureId(String sSecureId)
	{
		Xband xb = getBandFromSecureId(sSecureId);
		
		// find the band
		if (xb != null)
		{			
			// remove from other caches
			if (xb.getLRId()!=null)
				cacheL2B.clear(xb.getLRId());
			if (xb.getTapId()!=null)
				cacheR2B.clear(xb.getTapId());			
		}
		
		// remove from secureId cache
		cacheS2B.clear(sSecureId);
	}
	
	public void clearCacheByLRID(String sLRID)
	{
		Xband xb = getBandFromLRID(sLRID);
		
		if (xb != null)
		{			
			// remove from other caches
			if (xb.getTapId()!=null)
				cacheR2B.clear(xb.getTapId());
			if (xb.getSecureId()!=null)
				cacheS2B.clear(xb.getSecureId());			
		}
		
		// remove from LRID cache
		cacheL2B.clear(sLRID);
	}
	
	public void clearCacheByRFID(String sRFID)
	{
		Xband xb = getBandFromRFID(sRFID);
		
		if (xb != null)
		{			
			// remove from other caches
			if (xb.getLRId()!=null)
				cacheL2B.clear(xb.getLRId());
			if (xb.getSecureId()!=null)
				cacheS2B.clear(xb.getSecureId());
			
			cacheR2B.clear(xb.getTapId());
		}
		
		cacheR2B.clear(sRFID);		
	}
	
	/*
	 * Add request to workqueue
	 */
	private void addRequest(IDMSRequest.RequestTypeEnum requestType, String sID)
	{
		synchronized(workQueue)
		{
			workQueue.add(new IDMSRequest(requestType, sID));
			workQueue.notify();
		}
	}

	public boolean isPlaceholder(Xband xb)
	{
		synchronized(xb)
		{
			return xb.getBandId()==null;
		}
	}
	
	public boolean isPlaceholder(Guest g)
	{
		synchronized(g)
		{
			return g.getGuestId()==null;
		}
	}
	
	public Xband getBandFromLRID(String sLRID)
	{
		return getBandFromLRID(sLRID, false);
	}
	
	public Xband getBandFromLRID(String sLRID, boolean bAsynchronous)
	{	
		Xband xb = cacheL2B.get(sLRID);
		
		if (xb != null || cacheL2B.isItemInCache(sLRID))
			return xb;
		
		if (!this.isEnabled())
			return null;
		
		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}
			
			// synthesize return value
			xb = createPlaceholderBand();
			xb.setLRId(sLRID);
			
			// put in cache (to be patched later by asynch processing)
			cacheL2B.add(sLRID, xb);
			
			// schedule lookup
			addRequest(RequestTypeEnum.bandFromLRID, sLRID);
		}
		else
		{
			Lock existingQueryLock = null;
			Lock newLock = null;
			
			synchronized(lockIDMSL2B)
			{
				existingQueryLock = lockIDMSL2B.get(sLRID);
								
				if (existingQueryLock == null)
				{
					newLock = new ReentrantLock();
					newLock.lock();
					lockIDMSL2B.put(sLRID, newLock);
				}
			}
				
			if (existingQueryLock != null)
			{
				// Wait for the IDS query to finish 
				existingQueryLock.lock();
				existingQueryLock.unlock();
				return cacheL2B.get(sLRID);				
			}
			else
			{
				try 
				{
					xb = idmsBandFromLRID(sLRID);
				}
				finally
				{
					lockIDMSL2B.remove(sLRID);
					newLock.unlock();
				}
			}
		}	
		
		return xb;
	}
	
	public Xband getBandFromRFID(String sRFID)
	{
		return getBandFromRFID(sRFID, false);
	}
	
	public Xband getBandFromRFID(String sRFID, boolean bAsynchronous)
	{
		Xband xb = cacheR2B.get(sRFID);
		
		if (xb != null || cacheR2B.isItemInCache(sRFID))
			return xb;
		
		if (!this.isEnabled())
			return null;

		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}

			// synthesize return value
			xb = createPlaceholderBand();
			xb.setTapId(sRFID);
			
			// put in cache (to be patched later by asynch processing)
			cacheR2B.add(sRFID, xb);
			
			// schedule lookup
			addRequest(RequestTypeEnum.bandFromRFID, sRFID);
			
		}
		else
		{
			xb = idmsBandFromRFID(sRFID);
		}
		
		return xb;
	}
	
	public Xband getBandFromSecureId(String sSecureId)
	{
		return getBandFromSecureId(sSecureId, false);
	}
		
	public Xband getBandFromSecureId(String sSecureId, boolean bAsynchronous)
	{
		Xband xb = cacheS2B.get(sSecureId);
		
		if (xb != null || cacheS2B.isItemInCache(sSecureId))
			return xb;
		
		if (!this.isEnabled())
			return null;

		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}

			// synthesize return value
			xb = createPlaceholderBand();
			xb.setSecureId(sSecureId);
			
			// put in cache (to be patched later by asynch processing)
			cacheS2B.add(sSecureId,  xb);
			
			// schedule lookup
			addRequest(RequestTypeEnum.bandFromSecureId, sSecureId);
			
		}
		else
		{
			xb = idmsBandFromSecureId(sSecureId);
		}
		
		return xb;
	}
	
	public Guest getGuestFromBandId(String sBandId)
	{
		return getGuestFromBandId(sBandId, false);
	}
	
	public Guest getGuestFromBandId(String sBandId, boolean bAsynchronous)
	{
		Guest g = cacheB2G.get(sBandId);
		if (g != null || cacheB2G.isItemInCache(sBandId))
			return g;
		
		if (!this.isEnabled())
			return null;

		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}
			
			// synthesize return value
			g = createPlaceholderGuest();
			g.setFirstName("BandId=" + sBandId);
			
			// put in cache (to be patched later by asynch processing)
			cacheB2G.add(sBandId, g);
			
			// schedule lookup
			addRequest(RequestTypeEnum.guestFromBandId, sBandId);
			
		}
		else
		{
			Lock existingQueryLock = null;
			Lock newLock = null;
			
			synchronized(lockIDMSB2G)
			{
				existingQueryLock = lockIDMSB2G.get(sBandId);
								
				if (existingQueryLock == null)
				{
					newLock = new ReentrantLock();
					newLock.lock();
					lockIDMSB2G.put(sBandId, newLock);
				}
			}
				
			if (existingQueryLock != null)
			{
				// Wait for the IDS query to finish 
				existingQueryLock.lock();
				existingQueryLock.unlock();
				return cacheB2G.get(sBandId);				
			}
			else
			{
				try 
				{
					g = idmsGuestFromBandId(sBandId);
				}
				finally
				{
					lockIDMSB2G.remove(sBandId);
					newLock.unlock();
				}
			}			
		}
		
		return g;
	}
	
	public Guest getGuestFromGuestId(String sGuestId)
	{
		return getGuestFromGuestId(sGuestId, false);
	}
	
	public Guest getGuestFromGuestId(String sGuestId, boolean bAsynchronous)
	{
		Guest g = cacheG2G.get(sGuestId);
		
		if (g != null || cacheG2G.isItemInCache(sGuestId))
			return g;
		
		if (!this.isEnabled())
			return null;
		
		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}
			
			// synthesize return value
			g = createPlaceholderGuest();
			g.setFirstName("GuestId=" + sGuestId);
			
			// put in cache (to be patched later by asynch processing)
			cacheG2G.add(sGuestId, g);
			
			// schedule lookup
			addRequest(RequestTypeEnum.guestFromGuestId, sGuestId);
			
		}
		else
		{	
			g = idmsGuestFromGuestId(sGuestId);
		}
		
		return g;
	}
	
	public List<Celebration> getCelebrationListFromGuestId(String sGuestId)
	{
		return getCelebrationListFromGuestId(sGuestId, false);
	}
	
	public List<Celebration> getCelebrationListFromGuestId(String sGuestId, boolean bAsynchronous)
	{
		List<Celebration> cl = cacheG2CL.get(sGuestId);
		
		if (cl != null || cacheG2CL.isItemInCache(sGuestId))
			return cl;
		
		if (!this.isEnabled())
			return null;

		if (bAsynchronous)
		{
			if (liThreads.size()==0)
			{
				logger.error("Requested asynchronous id lookup without initializing IDMSResolver");
				return null;
			}
			
			// synthesize return value
			cl = new LinkedList<Celebration>();
			
			// put in cache (to be patched later by asynch processing)
			cacheG2CL.add(sGuestId, cl);
			
			// schedule lookup
			addRequest(RequestTypeEnum.celebrationListFromGuestId, sGuestId);
			
		}
		else
		{
			cl = idmsCelebrationListFromGuestId(sGuestId);
		}
		
		return cl;
	}

	private Xband createPlaceholderBand()
	{
		Xband xb = new Xband();
		xb.setActive(true);
		xb.setBandId(null);
		
		return xb;
	}

	private Guest createPlaceholderGuest()
	{
		Guest g = new Guest();
		g.setActive(true);
		g.setGuestId(null);
		
		return g;
	}


	/*
	 * Background lookup thread
	 */
	
	private void runSendThread()
	{
		for (;;)
		{
			// wait for something to do
			IDMSRequest request = null;
			synchronized(workQueue)
			{
				// don't get stuck forever
				try
				{
					workQueue.wait(5000);
				}
				catch (InterruptedException e)
				{
				}
				
				// if we're stopping, bag
				if (bStop)
					return;
				
				// if nothing to do, pause
				if (workQueue.isEmpty())
					continue;
				
				// grab a work item
				request = workQueue.removeFirst();
			}
			
			// perform the request (patches value in cache)
			switch(request.getRequestType())
			{
				case bandFromLRID:
					idmsBandFromLRID(request.getID());
					break;
					
				case bandFromRFID:
					idmsBandFromRFID(request.getID());
					break;
					
				case bandFromSecureId:
					idmsBandFromSecureId(request.getID());
					break;
					
				case guestFromGuestId:
					idmsGuestFromGuestId(request.getID());
					break;
					
				case guestFromBandId:
					idmsGuestFromBandId(request.getID());
					break;
					
				case celebrationListFromGuestId:
					idmsCelebrationListFromGuestId(request.getID());
					break;
			}
		}
	}

	private Xband idmsBandFromLRID(String id)
	{
		// TODO: perf metrics
		
		// get item from IDMS
		Xband xb = client.getBandForLRIDFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (xb!=null)
		{
			Xband xbCache = cacheL2B.get(id);
			// patch value in the cache
			if (xbCache != null)
			{
				synchronized(xbCache)
				{
					xbCache.copy(xb);
				}
				xb = xbCache;
			}
			else
				cacheL2B.add(id, xb);
		}
		else
			cacheL2B.markAsBad(id);
		
		return xb;
	}
	
	private Xband idmsBandFromRFID(String id)
	{
		// TODO: perf metrics
		Xband xb = client.getBandForRFIDFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (xb!=null)
		{
			Xband xbCache = cacheR2B.get(id);
			if (xbCache != null)
			{
				synchronized(xbCache)
				{
					xbCache.copy(xb);
				}
				xb = xbCache;
			}
			else
				cacheR2B.add(id, xb);
		}
		else
			cacheR2B.markAsBad(id);
		
		return xb;
	}

	private Xband idmsBandFromSecureId(String id)
	{
		// TODO: perf metrics
		Xband xb = client.getBandForSecureIdFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (xb!=null)
		{
			Xband xbCache = cacheS2B.get(id);
			
			if (xbCache != null)
			{
				synchronized(xbCache)
				{
					xbCache.copy(xb);
				}
				xb = xbCache;
			}
			else
				cacheS2B.add(id, xb);
		}
		else
			cacheS2B.markAsBad(id);
		
		return xb;
	}
	
	private Guest idmsGuestFromGuestId(String id)
	{
		// Short circuit lookups that we know will fail.
		if (id != null && id.contains("?"))
			return null;
		
		// add to positive or negative cache (note that "add" overrides existing placeholder data with new)
		Guest g = client.getGuestFromGuestIDFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (g!=null)
		{
			Guest gCache = cacheG2G.get(id);
			if (gCache != null)
			{
				synchronized(gCache)
				{
					gCache.copy(g);
				}
				g = gCache;
			}
			else
				cacheG2G.add(id, g);
		}
		else
			cacheG2G.markAsBad(id);
		
		return g;
	}

	private Guest idmsGuestFromBandId(String id)
	{
		// add to positive or negative cache (note that "add" overrides existing placeholder data with new)
		Guest g = client.getGuestFromBandIDFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (g!=null)
		{
			Guest gCache = cacheB2G.get(id);
			
			if (gCache != null)
			{
				synchronized(gCache)
				{
					gCache.copy(g);
				}
				g = gCache;
			}
			else
				cacheB2G.add(id, g);
		}
		else
			cacheB2G.markAsBad(id);
		
		return g;
	}
	
	private List<Celebration> idmsCelebrationListFromGuestId(String id)
	{
		// add to positive or negative cache (note that "add" overrides existing placeholder data with new)
		List<Celebration> cl = client.getCelebrationListFromGuestIDFromIDMS(id);
		
		// add to cache, patch cache or add to negative cache
		if (cl!=null)
		{
			List<Celebration> clCache = cacheG2CL.get(id);
			
			if (clCache != null)
			{				
				synchronized(clCache)
				{
					clCache.clear();
					for (Celebration c : cl)
						clCache.add(c);
				}
				cl = clCache;
			}
			else
				cacheG2CL.add(id, cl);
		}
		else
			cacheG2CL.markAsBad(id);
		
		return cl;
	}
}