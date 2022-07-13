package com.disney.xband.xbrc.ui.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xview.lib.model.Guest;

public class Xview {
	private static Logger logger = Logger.getLogger(Xview.class);
	private ConcurrentMap<String,GuestEntry> guestIdMap;
	private ConcurrentMap<String,GuestEntry> guestNameMap;
	private Timer timer;
	
	private static class SingletonHolder { 
		public static final Xview instance = new Xview();
	}
	
	public static Xview getInstance() {
		return SingletonHolder.instance;
	}	
	
	private Xview() {
		guestIdMap = new ConcurrentHashMap<String,GuestEntry>();
		guestNameMap = new ConcurrentHashMap<String,GuestEntry>();
		
		// Check for cache cleanup every minute.
		timer = new Timer();
		timer.schedule(new TimerTask(){
			  public void run(){
				  Xview.getInstance().doCleanup();
			  }
			  }, 60000, 60000);
	}
	
	private void doCleanup() {
		Long expireTime = new Date().getTime() - UIProperties.getInstance().getUiConfig().getGuestXviewCacheTimeSec() * 1000;
		
		for (Entry<String, GuestEntry> entry : guestIdMap.entrySet())
		{
		    if (entry.getValue().getEnteredTime().getTime() < expireTime)
		    {
		    	logger.trace("Xview::doCleanup - removing expired Xview Guest object with key: " + entry.getKey());
		    	if (entry.getValue().getGuest() != null)
		    		guestNameMap.remove(entry.getValue().getGuest().getFirstName().toLowerCase() + entry.getValue().getGuest().getLastName().toLowerCase());
		    	guestIdMap.remove(entry.getKey());		    	
		    }
		}
	}
	
	public Guest getGuestByGuestId(String guestId) {
		
		GuestEntry ge = guestIdMap.get(guestId);
		if (ge != null)
			return ge.getGuest();
		
		ge = new GuestEntry();
		Guest guest = getGuestFromXview(UIProperties.getInstance().getControllerInfo().getXviewurl() + "/guests/id/" + guestId);
		if (guest == null)
			return null;
		
		ge.setGuest(guest);
		guestIdMap.put(ge.getGuest().getGuestId(), ge);	
		guestNameMap.put(ge.getGuest().getFirstName().toLowerCase() + ge.getGuest().getLastName().toLowerCase(),ge);
		
		return ge.getGuest();
	}
	
	public Guest getGuestByName(String firstName, String lastName) {
		
		GuestEntry ge = guestIdMap.get(firstName.toLowerCase() + lastName.toLowerCase());
		if (ge != null)
			return ge.getGuest();
		
		ge = new GuestEntry();
		Guest guest = getGuestByNameFromXview(UIProperties.getInstance().getControllerInfo().getXviewurl() + "/guests/search/" + firstName + "%20" + lastName);
		if (guest == null)
			return null;
		
		ge.setGuest(guest);
		guestIdMap.put(ge.getGuest().getGuestId(), ge);
		guestNameMap.put(ge.getGuest().getFirstName().toLowerCase() + ge.getGuest().getLastName().toLowerCase(),ge);
		
		return ge.getGuest();
	}
	
	private Guest getGuestFromXview(String urlString)
	{
		URL url;
        InputStream ins = null;
        BufferedReader  br = null;
        InputStreamReader isr = null;

		try
		{
			url = new URL(urlString);
				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode()>=400)
			{
				logger.error("Error: " + conn.getResponseCode() + " received from xView (" + url.toString() + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			Guest guest = om.readValue(sb.toString(), Guest.class);
			if (guest == null)
			{
				logger.error("Error: received unexpected object type from xView (" + url.toString() + ")");
				return null;
			}
				
			return guest;
		} 
		catch (MalformedURLException e)
		{
			logger.error("URL Error: " + e.getLocalizedMessage(), e);
		} 
		catch (IOException e)
		{
			logger.error("IO Error: " + e.getLocalizedMessage(), e);
		}
		catch(Exception e)
		{
			logger.error("Error: " + e.getLocalizedMessage(), e);
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}
	
	private Guest getGuestByNameFromXview(String urlString)
	{
		URL url;
        InputStream ins = null;
        BufferedReader  br = null;
        InputStreamReader isr = null;

		try
		{
			url = new URL(urlString);
				
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode()>=400)
			{
				logger.error("Error: " + conn.getResponseCode() + " received from xView (" + url.toString() + ")");
				return null;
			}

			ins = conn.getInputStream();
            isr = new InputStreamReader(ins);
			br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());

			ObjectMapper om = new ObjectMapper();
			LinkedList<Guest> guests = om.readValue(sb.toString(), new TypeReference<LinkedList<Guest>>(){});
			if (guests == null)
			{
				logger.error("Error: received unexpected object type from xView (" + url.toString() + ")");
				return null;
			}
			
			for (Guest guest : guests) {
				return guest;
			}
				
			return null;
		} 
		catch (MalformedURLException e)
		{
			logger.error("URL Error: " + e.getLocalizedMessage(), e);
		} 
		catch (IOException e)
		{
			logger.error("IO Error: " + e.getLocalizedMessage(), e);
		}
		catch(Exception e)
		{
			logger.error("Error: " + e.getLocalizedMessage(), e);
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}
	
	private class GuestEntry {
		Date enteredTime;
		Guest guest;
		
		public GuestEntry() {
			enteredTime = new Date();
		}
		
		public Guest getGuest() {
			return this.guest;
		}
		
		public void setGuest(Guest guest) {
			this.guest = guest;
		}
		
		public Date getEnteredTime() {
			return enteredTime;
		}
	}
}
