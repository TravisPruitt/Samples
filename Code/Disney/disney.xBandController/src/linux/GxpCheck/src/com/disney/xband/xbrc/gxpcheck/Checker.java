package com.disney.xband.xbrc.gxpcheck;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.disney.xband.xbrc.gxpcheck.entity.GxpMessage;
import com.disney.xband.xbrc.gxpcheck.entity.GxpQueue;
import com.disney.xband.xbrc.gxpcheck.entity.Log;
import com.disney.xband.xbrc.gxpcheck.entity.GxpMessage.State;
import com.disney.xband.xbrc.gxpcheck.entity.Xbrc;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;
import com.disney.xband.xview.lib.model.Xview;

public class Checker
{
	public static Checker INSTANCE = new Checker();
	
	private Checker()
	{
	}
	
	public void check(CLOptions clo)
	{
		Xview.getInstance().setXviewURL(clo.getIDMS());
		GxpQueue queue = new GxpQueue(clo.getGxp(), clo.getThreadCount());
		Xbrc xbrc = new Xbrc(clo.getXbrc());
		
		// get the list of guest ids at the designated location
		List<String> li = xbrc.getGuestsAtLocation(clo.getLocation());
		if (li.size()==0)
		{
			System.out.println("No guests at location");
			return;
		}
		Map<String,GxpMessage> map = new Hashtable<String, GxpMessage>();
		
		// create requests for all of them
		for (String sid : li)
		{
			// get the guests band information
			Guest g = Xview.getInstance().getGuestFromGuestID(sid);
			if (g==null)
			{
				System.err.println("Error getting guest info for: " + sid);
				continue;
			}
			if (g.getXbands().size()==0)
			{
				System.err.println("Error: Guest " + sid + " has no bands");
				continue;
			}
			if (g.getXbands().size()>1)
			{
				System.err.println("Error: Guest " + sid + " has " + g.getXbands().size() + " bands");
				continue;
			}
			Xband band = g.getXbands().get(0);
			
			GxpMessage msg = new GxpMessage();
			msg.setGuestId(sid);
			msg.setGuestName(g.getFirstName() + " " + g.getLastName());
			msg.setLRID(band.getLRId());
			msg.setRFID(band.getTapId());
			msg.setBandId(band.getBandId());
			msg.setEntertainmentId(clo.getFacilityId());
			msg.setLocation(clo.getLocationId());
			msg.setSide("Right");
			msg.setUnitType("Entrance");
			msg.setState(State.unchecked);
			msg.setTime(new Date());
			map.put(sid, msg);
			try
			{
				queue.send(msg);
			}
			catch (Exception e)
			{
				System.err.println("Error queueing request for " + sid + ": " + e);
				Log.log("Error queueing request for " + sid + ": " + e);
			}
		}
		
		// now, wait until they're all cleared
		while(true)
		{
			int cWaiting = 0;
			for(String sid : map.keySet())
				if (map.get(sid).getState()==State.unchecked)
					cWaiting++;
			if (cWaiting==0)
				break;
			System.out.println("Waiting for " + cWaiting + " to clear");
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
		}
		
		// display results
		int cTotal=0;
		int cSuccess=0;
		int cFailure=0;
		for (GxpMessage msg: map.values())
		{
			System.out.println(msg.getBandId() + "," + msg.getGuestName() + "," + msg.getRFID() + "," + msg.getLRID() + "," + msg.getState());
			cTotal++;
			if (msg.getState()==State.succeed)
				cSuccess++;
			else
				cFailure++;
		}
		System.out.println("Total:    " + cTotal);
		System.out.println("Success:  " + cSuccess);
		System.out.println("Failures: " + cFailure);
		
		// shut down
		queue.stop();
				
	}

}
