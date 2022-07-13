package com.disney.xband.jmslistener.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestIdentifier;
import com.disney.xband.idms.lib.model.GuestIdentifierCollection;
import com.disney.xband.idms.lib.model.XBand;
import com.disney.xband.jmslistener.StatusInfo;

public abstract class Cache<T> 
{
	private static Logger logger = Logger.getLogger(Cache.class);
	
	private Map<String, T> cache;
	private Map<T, Long> expires;
	private int expirationMsec;
	private int capacity;
	private Map<String,Lock> keylock = new Hashtable<String,Lock>();
	
	protected Cache(int expirationMinutes, int capacity)
	{
	   	this.cache = new HashMap<String,T>();
	   	this.expires = new HashMap<T,Long>();
	   	this.expirationMsec = expirationMinutes * 1000 * 60;
	   	this.capacity = capacity;
	}

	public boolean isCached(String key)
	{
		return this.cache.containsKey(key);
	}
	
	public int getCacheCapacity()
	{
		return this.capacity;
	}

	public int getCacheSize()
	{
		return this.cache.size();
	}

	public T get(String key)
	{
		Lock existingQueryLock = null;
		Lock newLock = null;
		T item = null;
		
		try
		{
			synchronized(keylock)
			{
				existingQueryLock = keylock.get(key);
								
				if (existingQueryLock == null)
				{
					newLock = new ReentrantLock();
					newLock.lock();
					keylock.put(key, newLock);
				}
			}
			
			if (existingQueryLock != null)
			{
				// Wait for the IDS query to finish 
				existingQueryLock.lock();
				existingQueryLock.unlock();
				synchronized(this) {
					item = this.cache.get(key);
				}
			}
			else
			{
				try 
				{
					item = this.getGuestIdFromKey(key);
				}
				finally
				{
					keylock.remove(key);
					newLock.unlock();
				}
			}
		}
		finally {
			// We really don't need this here, but if anything throws an exception
			// we don't want to have a locked key.
			keylock.remove(key);
		}
		
		return item;
	}	

	public void add(String key, T item)
	{
		// block so other threads won't try and add same item
		synchronized(this)
		{
			this.cache.put(key, item);
			this.expires.put(item,System.currentTimeMillis() + this.expirationMsec);
		}
	}
	
	public void clearStaleItems()
	{
		synchronized(this)
		{
			//Use single value for current time
			long currentTime = System.currentTimeMillis();
			Iterator<Entry<T,Long>> it = expires.entrySet().iterator();
		    while (it.hasNext()) 
		    {
		        Map.Entry<T,Long> pairs = (Map.Entry<T,Long>)it.next();
		        if(pairs.getValue() < currentTime)
		        {
		        	try
		        	{
			        	//remove cache entry
			        	cache.remove(pairs.getKey());
	
			        	//Remove expires entry
			        	it.remove(); // avoids a ConcurrentModificationException
		        	}
		        	catch(Exception ex)
		        	{
		        		
		        	}
		        	
		        }
		    }
		}
	}

	public void clear()
	{
		synchronized(this)
		{
			this.cache.clear();
		}
	}
	
	protected abstract T getGuestIdFromKey(String key);

    protected long retrieveGuestFromIdentifier(URL url)
    {
    	long guestId = 0;
		InputStreamReader reader = null;
    	long startTime = System.currentTimeMillis();    	
    	
    	try
    	{
			//Call IDMS and get the guest
			URLConnection connection = url.openConnection ();
			
			//Open the reader
			reader = new InputStreamReader(connection.getInputStream());
			
			// Get the response
			BufferedReader rd = new BufferedReader(reader);
			
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
	 		
			rd.close();
			
			ObjectMapper mapper = new ObjectMapper(); 
			GuestIdentifierCollection guestIdentifierCollection = 
					mapper.readValue(sb.toString(), GuestIdentifierCollection.class);
			
			if(!guestIdentifierCollection.getIdentifiers().isEmpty())
			{
				GuestIdentifier guestIdentifier = 
						guestIdentifierCollection.getIdentifiers().get(0);
				guestId = guestIdentifier.getGuestId();
			}
    	}
        catch(IOException io)
        {
        	logger.error(ExceptionFormatter.format(
            		"Problem communicating with IDMS",io));
        }
    	finally
    	{
    		StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSQueryMsec().processValue(
    				System.currentTimeMillis()-startTime);

    		if(reader != null)
    		{
    			try
    			{
    				reader.close();
    			}
    			catch(IOException ex)
    			{
    				//Ignore???
    			}
    		}
    	}

    	return guestId;
    }

    protected long retrieveGuestFromxBand(URL url)
    {
    	long guestId = 0;
		InputStreamReader reader = null;
		long startTime = System.currentTimeMillis();
		
    	try
    	{
			//Call IDMS and get the guest
			URLConnection connection = url.openConnection();
			
			//Open the reader
			reader = new InputStreamReader(connection.getInputStream());

			// Get the response
			BufferedReader rd = new BufferedReader(reader);
			
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
	 		
			rd.close();
			
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			XBand band = mapper.readValue(sb.toString(), XBand.class);
			   
			if(!band.getGuests().isEmpty())
			{
				Guest guest = band.getGuests().get(0);
				guestId = guest.getGuestId();
			}
			else
			{
				logger.error("No guest found for xband with xbandid: " + 
	            	String.valueOf(band.getXbandId()));
			}
    	}
        catch(IOException io)
        {
        	logger.error(ExceptionFormatter.format(
            		"Problem communicating with IDMS",io));
        }
    	finally
    	{
    		StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSQueryMsec().processValue(
    				System.currentTimeMillis()-startTime);
    		if(reader != null)
    		{
    			try
    			{
    				reader.close();
    			}
    			catch(IOException ex)
    			{
    				//Ignore???
    			}
    		}
    	}

    	return guestId;
    }
}
