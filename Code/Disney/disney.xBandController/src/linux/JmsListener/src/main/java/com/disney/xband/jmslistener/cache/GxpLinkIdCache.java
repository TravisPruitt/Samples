package com.disney.xband.jmslistener.cache;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;


public class GxpLinkIdCache extends Cache<Long>
{
	private static Logger gxpLinklogger = Logger.getLogger(GxpLinkIdCache.class);
	
	private String idmsRootUrl;
	
	public GxpLinkIdCache(String idmsRootUrl, int expirationSeconds, int capacity)
	{
		super(expirationSeconds, capacity);
		this.idmsRootUrl = idmsRootUrl;
	}
	
	@Override
	protected Long getGuestIdFromKey(String key) 
    {
    	long guestId = 0; 
    	
		try
		{
    		URL url = new URL(this.idmsRootUrl + 
    				"guest/id;gxp-link-id=" + key + "/identifiers");

    		guestId = retrieveGuestFromIdentifier(url);
    	    
    		if(guestId != 0)
    		{
    			this.add(key, guestId);
    		}
    	    
		}
        catch(MalformedURLException mue)
        {
        	gxpLinklogger.error(ExceptionFormatter.format(
            		"Problem creating this.idmsUrl - " + idmsRootUrl,mue));
        }

    	return guestId;
    }
}
