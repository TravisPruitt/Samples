package com.disney.xband.xbrc.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xbrc.ui.bean.GuestStatusCounts;
import com.disney.xband.xbrc.ui.db.Data;

/*
 * The purpose of this class is to provide the UI with any data that is repeatedly obtained from the database
 * or directly from the xBRC using restful calls. Since there may be many WEB sessions requesting the same data,
 * it makes sense to keep in-memory cache that is refreshed at some defined interval.
 */
public class XbrcDataCache
{
	private static Logger logger = Logger.getLogger(XbrcDataCache.class.getName());

	// How often to get the data. Note that this time includes the time it takes
	// to read the data.
	private static final long refreshFrequencyMs = 1100;

	private List<GuestStatusCounts> guestCounts;
	private XbrcStatus xbrcStatus;
	private Date lastRefreshTime = null;
	private Thread refreshStatusThread = null;
	private boolean keepRunning = true;

	private XbrcDataCache()
	{
		RefreshThread refreshThread = new RefreshThread();
		refreshStatusThread = new Thread(refreshThread);
		refreshStatusThread.start();
	}
	
	private static class SingletonHolder
	{
		public static final XbrcDataCache instance = new XbrcDataCache();
	}
	
	public void stop()
	{
		keepRunning = false;
		synchronized(refreshStatusThread) 
		{
			refreshStatusThread.notify();
		}
	}
	
	private class RefreshThread implements Runnable
	{
		@Override
		public void run()
		{
			String url = UIProperties.getInstance().getUiConfig()
					.getControllerURL()
					+ "/status";			

			while (keepRunning)
			{
				try
				{
					synchronized(refreshStatusThread)
					{
						refreshStatusThread.wait();
					}
					
					logger.trace("Trying to connect to xBRC: " + url);			
					xbrcStatus = getStatus(url);
					logger.trace("Finished contacting xBRC: " + url);
				}
				catch (Exception e)
				{
					logger.error("Failed to get status from " + url, e);
				}
			}
		}
	}

	public static XbrcDataCache getInstance()
	{			
		return SingletonHolder.instance;
	}

	/*
	 * It's critical that this function is protected from re-entry.
	 */
	private synchronized boolean needRefresh()
	{
		Date now = new Date();
		if (lastRefreshTime == null
				|| (now.getTime() - lastRefreshTime.getTime() >= refreshFrequencyMs))
		{
			lastRefreshTime = new Date();
			return true;
		}
		return false;
	}

	public List<GuestStatusCounts> getGuestCounts()
	{
		if (needRefresh())
			doRefresh();

		return guestCounts;
	}

	public XbrcStatus getXbrcStatus()
	{
		if (needRefresh())
			doRefresh();

		return xbrcStatus;
	}	

	/*
	 * This call will be done in the context of a request thread. Alternatively,
	 * we could switch to a dedicated thread but since the data is displayed
	 * using ajax, the user will not notice the possible delay.
	 */
	private void doRefresh()
	{
		try
		{
			logger.trace("Refreshing xBRC data...");
			
			// java guarantees atomic assignments so no lock necessary here
			guestCounts = Data.GetGuestStatusCounts();
			
			// ask the status thread to wake up
			synchronized(refreshStatusThread)
			{
				refreshStatusThread.notify();
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to refresh xBRC data: ", e);
		}
	}

	public XbrcStatus getStatus(String url)
	{
        InputStream is = null;

		try
		{
			is = makeRestfullGETRequest(url);

			if (is == null)
			{
				logger.warn("Xbrc status request produced no response");
				return null;
			}

			XbrcStatus status = XmlUtil.convertToPojo(is, XbrcStatus.class);
			return status;
		}
		catch (Exception e)
		{
			logger.error("Failed to get Xbrc status: ", e);
		}
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		return null;
	}

	public InputStream makeRestfullGETRequest(String url)
	{
		try
		{
			HttpURLConnection conn = UISSLUtil.getConnection(new URL(url));

			logger.trace("Executing a call to " + conn.getURL().toString());

			int responseCode = conn.getResponseCode();
			if (responseCode < 0 || responseCode >= 400)
			{
				logger.error("Error: " + conn.getResponseCode()
						+ " received from Controller ("
						+ conn.getURL().toString() + ")");
				return null;
			}
			return conn.getInputStream();
		}
		catch (MalformedURLException e)
		{
			logger.error("URL Error:", e);
		}
		catch (IOException e)
		{
			logger.error("IO Error: ", e);
		}
		catch (Exception e)
		{
			logger.error("Error: ", e);
		}

		return null;
	}
}
