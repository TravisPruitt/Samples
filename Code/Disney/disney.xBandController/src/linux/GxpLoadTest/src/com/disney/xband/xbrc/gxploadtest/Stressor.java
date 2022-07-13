package com.disney.xband.xbrc.gxploadtest;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.disney.xband.xbrc.gxploadtest.entity.Attraction;
import com.disney.xband.xbrc.gxploadtest.entity.GxpMessage;
import com.disney.xband.xbrc.gxploadtest.entity.Log;
import com.disney.xband.xbrc.gxploadtest.entity.Metrics;

public class Stressor
{
	public static Stressor INSTANCE = new Stressor();
	
	private Map<String,Attraction> mapAttractions = new Hashtable<String,Attraction>();
	private Metrics metrics = new Metrics();
	
	private Stressor()
	{
	
	}
	
	public void stress(CLOptions clo, List<GxpMessage> li)
	{
		// calculate the inter-request period in msec
		long msecPeriod = 1000 / clo.getRequestsPerSecond();
		
		// seed all the messages with times and build attractions as needed
		Date dtStart = (Date) clo.getTime().clone();
		for (GxpMessage req : li)
		{
			req.setTime(dtStart);
			
			// advance
			dtStart.setTime(dtStart.getTime() + msecPeriod);
			
			if (!mapAttractions.containsKey(req.getEntertainmentId()))
				mapAttractions.put(req.getEntertainmentId(), new Attraction(clo, req.getEntertainmentId(), metrics));
		}
		
		// now step through time
		dtStart = clo.getTime();
		Date dtSimStart = new Date();
		for (GxpMessage req: li)
		{
			// how long to wait for the current message?
			long msecDiff = req.getTime().getTime() - dtStart.getTime();
			
			while(true)
			{
				// what's the diff in clock time?
				long msecSimDiff = new Date().getTime() - dtSimStart.getTime();
				
				if (msecDiff < msecSimDiff)
				{
					// send it now
					try
					{
						System.out.println("Queue request for " + req.getEntertainmentId() + " band " + req.getBandId() + " at " + Log.displayDate(req.getTime()));
						mapAttractions.get(req.getEntertainmentId()).send(req);
					}
					catch (Exception e)
					{
						System.out.println("Error scheduling request: " + e);
					}
					
					break;
				}
				else
				{
					// wait a while
					try
					{
						Thread.sleep(msecDiff - msecSimDiff);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
			
		}
		
		// wait for threads to finish
		while(true)
		{
			int cCount = 0;
			for(Attraction a: mapAttractions.values())
				cCount += a.getPendingRequests();
					
			if (cCount==0)
				break;
					
			System.out.println("Waiting for " + cCount + " requests to complete");
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// shut down our threads
		for(Attraction a: mapAttractions.values())
			a.stop();
		
		// print out statistics
		System.out.println("Total requests:         " + metrics.getCount());
		System.out.println("Avg transaction (msec): " + metrics.getAverageTransactionTime());
		System.out.println("Total success:          " + metrics.getSuccesses());
		System.out.println("Avg transaction (msec): " + metrics.getAverageSuccessTransactionTime());
		System.out.println("Total failure:          " + metrics.getFailures());
		System.out.println("Avg transaction (msec): " + metrics.getAverageFailureTransactionTime());
		
		
	}
	
}
