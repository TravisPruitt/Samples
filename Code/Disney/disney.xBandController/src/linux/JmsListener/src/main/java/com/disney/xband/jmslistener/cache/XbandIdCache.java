package com.disney.xband.jmslistener.cache;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;

public class XbandIdCache extends Cache<Long>
{
	private static Logger xbandIdLogger = Logger.getLogger(XbandIdCache.class);

	private String idmsRootUrl;
	
	public XbandIdCache(String idmsRootUrl, int expirationSeconds, int capacity)
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
			//Call IDMS and get the guest
    		URL url = new URL(this.idmsRootUrl + 
    				"xband/" + key);
    	    
    		guestId = retrieveGuestFromxBand(url);
    		
    		if(guestId != 0)
    		{
    			this.add(key,guestId);
    		}
    	    
		}
        catch(MalformedURLException mue)
        {
        	xbandIdLogger.error(ExceptionFormatter.format(
            		"Problem creating this.idmsUrl - " + 
            				this.idmsRootUrl,mue));
        }

    	return guestId;
    }
}
