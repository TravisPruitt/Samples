package com.disney.xband.xbrc.lib.model;

import java.util.Comparator;
import java.util.List;

import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.lib.xbrapi.XbrEvent;
import org.apache.log4j.Logger;

public class Singulator implements Comparator<XbrEvent>
{
	private XBRCController controller;
	private IXBRCModel model; 
    private static Logger logger = Logger.getLogger(Singulator.class);
	
	public Singulator(XBRCController controller, IXBRCModel model)
	{
		this.controller = controller;
		this.model = model;
	}
	
	public EventAggregate Singulate (List<LocationStats> liLocStats, String sAlgorithm)
	{
		// iterate, looking through the location with the highest average signal strength
		LocationStats lsM = null;
		String sBasis = "Only";
		for(LocationStats ls : liLocStats)
		{
			if (lsM==null)
				lsM = ls;
			else
			{
				if (sAlgorithm.equalsIgnoreCase("mean"))
				{
					// first try basing decision on mean signal strength
					if (ls.getMeanSS() > lsM.getMeanSS())
					{
						 lsM = ls;
						 sBasis = "MeanSS";
					}
					else if (ls.getMeanSS()==lsM.getMeanSS())
					{
						// same mean SS, try count
						if (ls.getCount() > lsM.getCount())
						{
							lsM = ls;
							sBasis = "ReaderCount";
						}
						else if (ls.getCount()==lsM.getCount())
						{
							// same count, try peak
							if (ls.getMaxSS() > lsM.getMaxSS())
							{
								lsM = ls;
								sBasis = "MaxSS";
							}
							else if (ls.getMaxSS()==lsM.getMaxSS())
							{
								sBasis = "First";
							}
						}
					}
				}
				else if (sAlgorithm.compareToIgnoreCase("max")==0)
				{
					// first try basing decision on mean signal strength
					if (ls.getMaxSS() > lsM.getMaxSS())
					{
						 lsM = ls;
						 sBasis = "MaxSS";
					}
					else if (ls.getMaxSS()==lsM.getMaxSS())
					{
						// same mean SS, try count
						if (ls.getCount() > lsM.getCount())
						{
							lsM = ls;
							sBasis = "ReaderCount";
						}
						else if (ls.getCount()==lsM.getCount())
						{
							// same count, try mean
							if (ls.getMeanSS() > lsM.getMeanSS())
							{
								lsM = ls;
								sBasis = "MeanSS";
							}
							else if (ls.getMeanSS()==lsM.getMeanSS())
							{
								sBasis = "First";
							}
						}
					}
				}		
			}
		}

        if((lsM == null) || (model == null)) {
            // logger.error("Singulation failed (lsM = null).");
            return null;
        }

		if (model.isEventLogged(lsM.getID()))
		{
			String sLog = lsM.getTimestamp().getTime() + ",SNG," + lsM.getReaderInfo().getLocation().getName() + "," + lsM.getID() + "," + sBasis;
			controller.logEKG(sLog);
		}
		
		return new LRREventAggregate(	lsM.getID(), 
										lsM.getPidDecimal(),
										lsM.getPacketSequence(),
										lsM.getReaderInfo().getName(), 
										lsM.getTimestamp(), 
										lsM.getMeanSS(), 
										lsM.getMaxSS(), 
										lsM.getCount(),
										lsM.getXband(),
										lsM.getGuest()); 
		
	}
	
	@Override
	public int compare(XbrEvent arg0, XbrEvent arg1)
	{
		int d = 0;
		
		// deal with null cases
		if (arg0!=null && arg1==null)
			return -1;
		else if (arg0==null && arg1!=null)
			return 1;
		else if (arg0==null && arg1==null)
			return 0;
		
		// first, sort by bandid but only if we have them (bio scan events don't)
		if (arg0.getID() != null && arg1.getID() != null)
		{
			d = arg0.getID().compareTo(arg1.getID());
			if (d!=0)
				return d;
		}
		
		// second, sort by time, rounded to the nearest minute
		long t0 = arg0.getTime().getTime()/60000;
		long t1 = arg1.getTime().getTime()/60000;
		if (t0<t1)
			return -1;
		else if (t0>t1)
			return 1;
		
		// next, sort by packet number
		if (arg0 instanceof LrrEvent && arg1 instanceof LrrEvent)
		{			
			long n0 = ((LrrEvent)arg0).getPno();
			long n1 = ((LrrEvent)arg1).getPno();
			if (n0 < n1)
				return -1;
			else if (n0 > n1)
				return 1;
		}
		
		// next, by reader location
		ReaderInfo ri0 = controller.getReader(arg0.getReaderName());
		ReaderInfo ri1 = controller.getReader(arg1.getReaderName());
		if (ri0.getLocation() == null || ri1.getLocation() == null)
			return d;
		String sLocation0 = ri0.getLocation().getName();
		String sLocation1 = ri1.getLocation().getName();
		d = sLocation0.compareTo(sLocation1);
		if (d!=0)
			return d;
		
		// else, they're equal
		return 0;
	}
}
