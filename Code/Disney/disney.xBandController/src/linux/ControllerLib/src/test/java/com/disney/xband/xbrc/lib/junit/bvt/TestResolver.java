package com.disney.xband.xbrc.lib.junit.bvt;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrc.lib.idms.IDMSResolver;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

import junit.framework.TestCase;

public class TestResolver extends TestCase
{
	private Properties config;
	private boolean bEnabled;
	
	@Before
	public void setUp() throws Exception 
	{
		//read configuration
		config = new Properties();

        InputStream is = null;

        try 
        {
            is = getClass().getResourceAsStream("test-bvt-config.properties");
		    config.load(is);
        }
        finally 
        {
            if(is != null) 
            {
                try 
                {
                    is.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }

		IDMSResolver.INSTANCE.initialize(2);
		IDMSResolver.INSTANCE.setCacheTimeouts( IDMSResolver.secPositiveCacheTimeoutDefault, 
										   		IDMSResolver.secPositiveCacheTimeoutDefault);
		IDMSResolver.INSTANCE.setHttpTimeout(IDMSResolver.msecHttpTimeoutDefault);
		IDMSResolver.INSTANCE.setIDMSUrl(config.getProperty("idmsurl"));
		bEnabled = IDMSResolver.INSTANCE.isEnabled();
	}
	
	@Test
	public void testResolver() 
	{
		// if disabled, just quit
		if (!bEnabled)
		{
			System.out.println("No IDMS URL configured - test exiting");
			return;
		}
		
		// start with a bandid
		String sBandId = "3E4F040C7E30AE00";
		
		// get the guest associated with the band
		Guest g = IDMSResolver.INSTANCE.getGuestFromBandId(sBandId);
		assertTrue(g!=null);
		
		// get a band from the guest
		List<Xband> liBands = g.getXbands();
		assertTrue(liBands!=null);
		assertTrue(liBands.size()>0);
		
		Xband xb = liBands.get(0);
		assertTrue(xb!=null);
		
		// pull out the LRID, RFID and SecureID
		String sLRID = xb.getLRId();
		String sRFID = xb.getTapId();
		String sSecureId = xb.getSecureId();
		
		// pull the band via its various IDs
		Xband xb2 = null;
		xb2 = IDMSResolver.INSTANCE.getBandFromLRID(sLRID);
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		xb2 = IDMSResolver.INSTANCE.getBandFromRFID(sRFID);
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		xb2 = IDMSResolver.INSTANCE.getBandFromSecureId(sSecureId);
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		
		// get the guest from the guest Id
		Guest g2 = IDMSResolver.INSTANCE.getGuestFromGuestId(g.getGuestId());
		assertTrue(g2.getGuestId().equals(g.getGuestId()));
		
		// flush one item from the cache and verify it's missing
		IDMSResolver.INSTANCE.clearCacheByLRID(sLRID);
		assertNotNull(IDMSResolver.INSTANCE.getBandFromLRID(sLRID));
		assertNotNull(IDMSResolver.INSTANCE.getBandFromRFID(sRFID));
		assertNotNull(IDMSResolver.INSTANCE.getBandFromSecureId(sSecureId));
		
		// Guest should still be there
		assertNotNull(IDMSResolver.INSTANCE.getGuestFromGuestId(g.getGuestId()));
		
		// flush the entire cache and verify that items can't be retrieved
		IDMSResolver.INSTANCE.clearCache();
		assertNotNull(IDMSResolver.INSTANCE.getGuestFromBandId(sBandId));
		
		// try asynchronous ops
		xb2 = IDMSResolver.INSTANCE.getBandFromLRID(sLRID, true);
		assertTrue(IDMSResolver.INSTANCE.isPlaceholder(xb2));
		assertTrue(waitForComplete(xb2));
		assertNotNull(IDMSResolver.INSTANCE.getBandFromLRID(sLRID));
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		xb2 = IDMSResolver.INSTANCE.getBandFromRFID(sRFID, true);
		assertTrue(IDMSResolver.INSTANCE.isPlaceholder(xb2));
		assertTrue(waitForComplete(xb2));
		assertNotNull(IDMSResolver.INSTANCE.getBandFromRFID(sRFID));
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		xb2 = IDMSResolver.INSTANCE.getBandFromSecureId(sSecureId, true);
		assertTrue(IDMSResolver.INSTANCE.isPlaceholder(xb2));
		assertTrue(waitForComplete(xb2));
		assertNotNull(IDMSResolver.INSTANCE.getBandFromSecureId(sSecureId));
		assertTrue(xb2.getBandId().equals(xb.getBandId()));
		g2 = IDMSResolver.INSTANCE.getGuestFromGuestId(g.getGuestId(), true);
		assertTrue(IDMSResolver.INSTANCE.isPlaceholder(g2));
		assertTrue(waitForComplete(g2));
		assertNotNull(IDMSResolver.INSTANCE.getGuestFromGuestId(g.getGuestId()));
		assertTrue(g2.getGuestId().equals(g.getGuestId()));
		
		// do a quick performance test
		int nGuestId = 1700;
		int cGuests = 50;
		Date dtStart = new Date();
		for (int i=0; i<cGuests; i++)
		{
			String sGuestId = Integer.toString(nGuestId+i);
			g2 = IDMSResolver.INSTANCE.getGuestFromGuestId(sGuestId);
			assertTrue(g2!=null);
		}
		long msecDiff = new Date().getTime() - dtStart.getTime();
		double msecPerGuest = (double) msecDiff/cGuests;
		System.out.println("** Average of " + msecPerGuest + " msec per guest lookup");
	}
	
	private boolean waitForComplete(Xband xb)
	{
		Date dtStart = new Date();
		while(true)
		{
			if (!IDMSResolver.INSTANCE.isPlaceholder(xb))
				return true;
			
			// timed out?
			Date dtNow = new Date();
			long msec = dtNow.getTime() - dtStart.getTime();
			if (msec > 100000)
				return false;
			
			// yield for a while
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	private boolean waitForComplete(Guest g)
	{
		Date dtStart = new Date();
		while(true)
		{
			if (!IDMSResolver.INSTANCE.isPlaceholder(g))
				return true;
			
			// timed out?
			Date dtNow = new Date();
			long msec = dtNow.getTime() - dtStart.getTime();
			if (msec > 100000)
				return false;
			
			// yield for a while
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	
	@After
	public void tearDown() throws Exception 
	{
		IDMSResolver.INSTANCE.stop();
	}

	

}
