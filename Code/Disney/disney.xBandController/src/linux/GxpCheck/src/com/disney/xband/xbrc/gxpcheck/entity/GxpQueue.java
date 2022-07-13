package com.disney.xband.xbrc.gxpcheck.entity;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.disney.xband.xbrc.gxpcheck.entity.GxpMessage.State;


import net.sf.json.JSONObject;

public class GxpQueue 
{
	private String sGXPURL;
	private boolean bStop = false;
	private List<Thread> liThreads = new ArrayList<Thread>();
	private LinkedList<GxpMessage> workQueue = new LinkedList<GxpMessage>();
	
	public GxpQueue(String sGXPURL, int cThreads)
	{
		this.sGXPURL = sGXPURL;
		
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
	
	public void send(GxpMessage req) throws Exception
	{
		synchronized(workQueue)
		{
			workQueue.add(req);
			workQueue.notify();
		}
	}
	
	public int getPending()
	{
		synchronized(workQueue)
		{
			return workQueue.size();
		}
	}
	
	public void stop()
	{
		bStop = true;
		synchronized(workQueue)
		{
			workQueue.notifyAll();
		}
		
		// wait until all the threads have died or 1 second at the latest
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

	/*
	 * Worker thread
	 */
	private void runSendThread()
	{
		for (;;)
		{
			// wait for something to do
			
			GxpMessage msg = null;
			synchronized(workQueue)
			{
				// don't get stuck forever
				try
				{
					workQueue.wait(50);
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
				msg = workQueue.removeFirst();
			}
			
			// send the message to the GXP
			sendToGxp(msg);
		}
		
	}

	private void sendToGxp(GxpMessage msg)
	{
		if (sGXPURL==null || sGXPURL.isEmpty() || sGXPURL.startsWith("#"))
		{
			Log.log("Logging gxp request for facility: " + msg.getEntertainmentId() + " for bandid " + msg.getBandId() + " at time " + msg.getTime());
			return;
		}
		
		// compose JSON
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("    \"tapRequest\":\n");
		sb.append("    {\n");
		sb.append("        \"tapDate\": " + msg.getTime().getTime() + ",\n");
		sb.append("        \"xBandId\": \"" + msg.getBandId() + "\",\n");
		sb.append("        \"location\": " + msg.getLocation() + ",\n");
		sb.append("        \"unitType\": \"" + msg.getUnitType() + "\",\n");
		sb.append("        \"entertainmentId\": " + msg.getEntertainmentId() + ",\n");
		sb.append("        \"side\": \"" + msg.getSide()+ "\",\n");
		sb.append("        \"callback\" : \"" + "http://localhost:8080/light/GxpLoadTest\"\n");
		sb.append("    }\n");
		sb.append("}\n");
		
		InputStream ins = null;

		try
		{
			URL url = new URL(sGXPURL + "/tap");	
			
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("Content-type", "application/json");
			OutputStreamWriter wr = new OutputStreamWriter(httpCon.getOutputStream());
			wr.write(sb.toString());
			wr.flush();			
			wr.close();
			Date dtStart = new Date();
			Log.log("Sending gxp request for facility: " + msg.getEntertainmentId() + " for bandid " + msg.getBandId() + " at scheduled time " + Log.displayDate(msg.getTime()) + " clock time: " + Log.displayDate(dtStart));
			int nResponse = httpCon.getResponseCode();
			Date dtEnd = new Date();
			long msec = dtEnd.getTime() - dtStart.getTime();
			if (nResponse==200)
			{
				// process response JSON
				ins = httpCon.getInputStream();
			
				String sPutData = new Scanner(ins).useDelimiter("\\A").next();

				JSONObject jo = null;
				JSONObject joSC = null;
				try
				{
					jo = JSONObject.fromObject(sPutData);
					joSC = jo.getJSONObject("tapResponse");
					
					// fetch fields
					boolean bGreen = joSC.getBoolean("green");
					// String sReason = joSC.getString("reason");
					
					// turn the light blue/green
					if (bGreen)
					{
						Log.log("++Success for facility: " + msg.getEntertainmentId() + " for bandid " + msg.getBandId() + " at time " + Log.displayDate(dtEnd));
						msg.setState(State.succeed);
					}
					else
					{
						Log.log("--Failure for facility: " + msg.getEntertainmentId() + " for bandid " + msg.getBandId() + " at time " + Log.displayDate(dtEnd));
						msg.setState(State.failed);
					}
				}
				catch(Exception ex)
				{
					System.err.println("Exception error deserializing tap response: " + ex);
				}
			}
			else
			{
				System.err.println("HTTP error processing tap response: " + nResponse);
			}
			
		}
		catch (Exception e)
		{
			System.err.println("Error exception processing process request: " + e);
		}
		finally
		{
			if(ins != null)
			{
				try {
					ins.close();
				}
				catch(Exception ignore)
				{
				}
			}
		}
		
	}
	
}
