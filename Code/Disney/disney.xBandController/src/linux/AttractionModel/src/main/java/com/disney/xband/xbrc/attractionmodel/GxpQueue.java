package com.disney.xband.xbrc.attractionmodel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.xbrc.attractionmodel.GxpCallback.Operation;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.utils.FileUtils;

public class GxpQueue 
{
	// logger
	private static Logger logger = Logger.getLogger(GxpQueue.class);
	private static Logger plogger = Logger.getLogger("performance." + GxpQueue.class.toString());
	
	class GxpMessage
	{
		private Integer deviceId;
		private ReaderInfo ri;
		private String sGuestId;
		private String sBandId;
		private Date dtEvent;
		
		public GxpMessage(String sGuestId, String sBandId, Integer deviceId, String readerName, Date dtEvent) throws Exception
		{
			this.deviceId = deviceId;
			
			// fetch the reader info
			this.ri = XBRCController.getInstance().getReaderFromDeviceId(deviceId);
			if (this.ri==null)
				throw new Exception("Reader " + readerName + " has an invalid device ID = " + deviceId + ". Cannot proces GXP entitlement for guest " + sGuestId);
			
			// stow
			this.sGuestId = sGuestId;
			this.sBandId = sBandId;
			this.dtEvent = dtEvent;
		}
	
		
		public String getGuestId()
		{
			return sGuestId;
		}
		
		public String getBandId()
		{
			return sBandId;
		}
		
		public ReaderInfo getReaderInfo()
		{
			return ri;
		}
		
		public Date getEventDate()
		{
			return dtEvent;
		}


		public Integer getDeviceId()
		{
			return deviceId;
		}


		public void setDeviceId(Integer deviceId)
		{
			this.deviceId = deviceId;
		}
	}
	
	private boolean bStop = false;
	private List<Thread> liThreads = new ArrayList<Thread>();
	private LinkedList<GxpMessage> workQueue = new LinkedList<GxpMessage>();
	
	public GxpQueue(int cThreads)
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
	
	public void sendTap(String sGuestId, String sBandId, ReaderInfo ri, Date dtEvent) throws Exception
	{
		logger.debug("In sendTap");
		plogger.trace("Queuing GXP request");
		synchronized(workQueue)
		{
			workQueue.add(new GxpMessage(sGuestId, sBandId, ri.getDeviceId(), ri.getName(), dtEvent));
			workQueue.notify();
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
			
			if (bAllStopped || (new Date().getTime() - dtStart.getTime()) > 1000)
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
				msg = workQueue.removeFirst();
			}
			
			// send the message to the GXP
			sendToGxp(msg.getGuestId(), msg.getBandId(), msg.getDeviceId(), msg.getReaderInfo(), msg.getEventDate());
		}
		
	}
	
	private void sendToGxp(String sGuestId, String sBandId, Integer deviceId, ReaderInfo ri, Date dtEvent)
	{
		logger.debug("In sendToGxp");
		
		// map the station type
		String sStationType = null;
		
		// map reader type
		if (LocationType.getByOrdinal(ri.getLocation().getLocationTypeID()) == LocationType.xPassEntry)
			sStationType = "Entrance";
		else if (LocationType.getByOrdinal(ri.getLocation().getLocationTypeID()) == LocationType.Merge)
			sStationType = "Merge";
		else if (LocationType.getByOrdinal(ri.getLocation().getLocationTypeID()) == LocationType.Combo)
			sStationType = "Combo";
		else
		{
			logger.error("Invalid location type");
			playErrorEvent(ri);
			return;
		}
		
		// interpret venue name as an entertainment id number
		long lEntertainmentId = 0;	
		try
		{
			// BUG#: 5850 - strip off any trailing, non-numeric data. This allow non-numerics
			// (e.g. "-1", "-2" to be used to distinguish xBRCs while not affecting GXP
			String venue = XBRCController.getInstance().getVenueName();
			venue = venue.replaceFirst("[^\\d].*$", "");
			lEntertainmentId = Long.parseLong(venue);
		}
		catch(Exception ex)
		{
		}
		
		String sGXPURL = ConfigOptions.INSTANCE.getModelInfo().getGXPURL() + "/tap";
		if (sGXPURL==null || sGXPURL.isEmpty() || sGXPURL.startsWith("#"))
		{
			logger.debug("no GXP. Setting light to green and returning");
						
			// queue a success response
			queueCallback(sBandId, sStationType, lEntertainmentId, lEntertainmentId, ri.getDeviceId(), true, dtEvent);
			return;
		}
		
		logger.debug("Sending /tap message to GXP");
		plogger.trace("Sending /tap message to GXP");
		
		Date dtStart = new Date();
		
		// figure out what base URL to use for the callback. Note
		// that the call returns the non-VIP address if it's not available
		String xbrcURL = XBRCController.getInstance().getXbrcVipURL();

		// compose JSON
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("    \"tapRequest\":\n");
		sb.append("    {\n");
		sb.append("        \"readerId\": \"" + ri.getDeviceId()  + "\",\n");
		sb.append("        \"tapDate\": " + new Date().getTime() + ",\n");
		sb.append("        \"xBandId\": " + sBandId + ",\n");
		sb.append("        \"location\": " + lEntertainmentId + ",\n");
		sb.append("        \"unitType\": \"" + sStationType + "\",\n");
		sb.append("        \"entertainmentId\": " + lEntertainmentId + ",\n");
		String sSide = "Right";
		if ( (ri.getLane() % 2) == 1 )
			sSide = "Left";
		sb.append("        \"side\": \"" + sSide + "\",\n");
		sb.append("        \"callback\" : \"" + xbrcURL + "/model/gxp/" + deviceId + "\"\n");
		sb.append("    }\n");
		sb.append("}\n");
		
		if (logger.isTraceEnabled())
			plogger.trace("GXP taprequest=" + sb.toString().replace(sBandId, FileUtils.hideLeadingChars(sBandId,4)));

        InputStream ins = null;
        BufferedReader reader = null;

		try
		{
			URL url = new URL(sGXPURL);	
			
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("Content-type", "application/json");
            OutputStreamWriter wr = null;

            try {
			    wr = new OutputStreamWriter(httpCon.getOutputStream());
			    wr.write(sb.toString());
			    wr.flush();
            }
            finally {
                if(wr != null) {
                    try {
                        wr.close();
                    }
                    catch(Exception e) {
                    }
                }
            }

			plogger.trace("Waiting for response from GXP");
			int nResponse = httpCon.getResponseCode();
			plogger.trace("Got response from GXP");
			logger.debug("Got response from GXP");

			if (nResponse==200)
			{
				// process response JSON
			
				logger.trace("GXP successful response");
				plogger.trace("GXP successful response");
				ins = httpCon.getInputStream();
			
				String sPutData = new Scanner(ins).useDelimiter("\\A").next();
				if (logger.isDebugEnabled())
					logger.debug("got GXP response: " + sPutData.replace(sBandId, FileUtils.hideLeadingChars(sBandId,4)));
				
				if (plogger.isTraceEnabled())
					plogger.trace("got GXP response: " + sPutData.replace(sBandId, FileUtils.hideLeadingChars(sBandId,4)));

				// parse json object
				plogger.trace("Deserializing GXP response ");
				
				JSONObject jo = null;
				JSONObject joSC = null;
				try
				{
					jo = JSONObject.fromObject(sPutData);
					joSC = jo.getJSONObject("tapResponse");
					
					// fetch fields
					boolean bGreen = joSC.getBoolean("green");

					// queue a callback response for the CEP
					queueCallback(sBandId, sStationType, lEntertainmentId, lEntertainmentId, ri.getDeviceId(), bGreen, dtEvent);
					
					// turn the light blue/green
					if (!bGreen)
					{
						logger.debug("Entitlement check for band: " + FileUtils.hideLeadingChars(sBandId,4) + " failed");
						plogger.trace("Entitlement check for band: " + FileUtils.hideLeadingChars(sBandId,4) + " failed");
					}
					else
					{
						logger.debug("Entitlement check for band: " + FileUtils.hideLeadingChars(sBandId,4) + " succeeded");
						plogger.trace("Entitlement check for band: " + FileUtils.hideLeadingChars(sBandId,4) + " succeeded");
					}
				}
				catch(Exception ex)
				{
					logger.error("Error deserializing tapResponse: " + ex.getLocalizedMessage() + "\n" + ex.getStackTrace().toString());
					playErrorEvent(ri);
				}
					
			}
			else
			{
				logger.error("GXP failure response: " + nResponse);

                // If we have additional information, output the returned text to
                // the logfile.
                ins = httpCon.getInputStream();
                if ( ins != null )
                {
                    reader = new BufferedReader(new InputStreamReader(ins));
                    String readLine = reader.readLine();
                    while ( readLine != null )
                    {
                        logger.error("GXP error detail: " + readLine);
                    }
                }

                playErrorEvent(ri);
			}
			
		}
		catch (Exception e)
		{
			logger.error("Failed to communicate with GXP: " + e);
			playErrorEvent(ri);
		}
        finally {
            if(ins != null) {
                try {
                    ins.close();
                }
                catch(Exception e) {
                }
            }

            if(reader != null) {
                try {
                    reader.close();
                }
                catch(Exception e) {
                }
            }
        }

		Date dtEnd = new Date();
		long msec = dtEnd.getTime() - dtStart.getTime();
		XBRCController.getInstance().getStatus().getPerfExternalMsec().processValue(msec);
	}
	
	private void queueCallback(String sBandId, String sStationType, long lEntertainmentId, long lLocationId, Integer deviceId, boolean bSuccess, Date dtStart)
	{
		GxpCallback m = new GxpCallback(); 
		m.setBandId(sBandId);
		m.setUnitType(GxpCallback.UnitType.valueOf(sStationType));
		//m.setEntertainmentId(lEntertainmentId);
		m.setLocationId(lEntertainmentId);
		m.setTime(new Date().getTime());
		m.setDeviceId(deviceId);
		m.setUnitType(GxpCallback.UnitType.valueOf(sStationType));
		m.setOperation(bSuccess ? Operation.success : Operation.fail);
		m.setTimeStart(dtStart);
		GxpCallbackQueue.INSTANCE.add(m);
	}

	/*
	 * Note that this is for "error" conditions not a gxp negative response
	 * TODO: review whether we need different configurable UE parameters
	 */
	private void playErrorEvent(ReaderInfo ri)
	{
/*		
		ColorValue cv = ColorValue.valueOf(ConfigOptions.INSTANCE.getModelInfo().getErrorLightEvent());
		long cvDuration = ConfigOptions.INSTANCE.getModelInfo().getErrorLightDuration();
		ReaderInfo ri = XBRCController.getInstance().getReader(sReaderName);
		ReaderApi.setReaderLight(ri, cv, cvDuration);
*/
		// We do not want to play error events any more. The CPE will handle GXP call timeouts.
        // XBRCController.getInstance().setReaderSequence(ri, Sequence.error);
	}
}
