package com.disney.xband.xbrc.ui.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.disney.xband.lib.controllerapi.ReaderSequenceInfo;
import com.disney.xband.lib.controllerapi.XbrcConfig;

import org.apache.log4j.Logger;

import com.disney.xband.lib.controllerapi.LrrEventsByBand;
import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xbrc.lib.config.UpdateConfig;
import com.disney.xband.xbrc.ui.UIProperties;

public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	
	private static class SingletonHolder { 
		public static final Controller instance = new Controller();
	}
	
	public static Controller getInstance() {
		return SingletonHolder.instance;
	}	
	
	private Controller() {}
	
	public Collection<LrrEvent> getEventsByGuestId(String guestId) {
		
		return getEventsFromXview(UIProperties.getInstance().getUiConfig().getControllerURL() + "/bandevents/band/" + guestId);
	}
	
	private Collection<LrrEvent> getEventsFromXview(String urlString)
	{
		URL url = null;
		InputStream ins = null;
		try
		{
			url = new URL(urlString);
			
			logger.debug("Executing a call to " + urlString);
				
			HttpURLConnection conn = (HttpURLConnection) XbrcSSLUtil.getConnection(url);
			if (conn.getResponseCode()>=400)
			{
				logger.error("Error: " + conn.getResponseCode() + " received from Controller (" + url.toString() + ")");
				return null;
			}
			
			ins = conn.getInputStream();
			LrrEventsByBand events = (LrrEventsByBand)XmlUtil.convertToPojo(ins, LrrEventsByBand.class);
			
			if (events == null)
				return null;
			
			logger.debug("Call to " + urlString + " returned " + events.getEvents().size() + " events.");

			return events.getEvents();
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
		} finally {
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e){
				logger.trace(e.getLocalizedMessage());
			}
		}
		return null;
	}
	
	/*
	 * Make a restful call to the XBRC asking it to re-load its configuration from the Config table.
	 */
	public void notifyXbrcOfConfigChange(UpdateConfig updateConfig) throws Exception {
		
		OutputStreamWriter wr = null;
		OutputStream os = null;
		HttpURLConnection conn = null;
		URL url;
		try
		{
			// xbrc to go to
			String urlString = UIProperties.getInstance().getUiConfig().getControllerURL() + "/updateconfig";
			String xml = null;
			
			if (updateConfig != null)
				xml = XmlUtil.convertToXml(updateConfig, UpdateConfig.class);
			
			// connection url
			url = new URL(urlString);
		
			// establish PUT request
			conn = (HttpURLConnection) XbrcSSLUtil.getConnection(url);
			conn.setRequestMethod("PUT");
			
			if (xml!= null)
			{
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-type", "application/xml");
				conn.setRequestProperty("Content-length", Integer.toString(xml.length()));
				os = conn.getOutputStream();
				wr = new OutputStreamWriter(os);
				wr.write(xml);
				wr.flush();
			}
			else
			{
				conn.setDoOutput(false);
			}		
		
			if (conn.getResponseCode()>=400)
				logger.error("Error: " + conn.getResponseCode() + " received (" + url.toString() + ")");
		}
		finally
		{
			if (os != null)
			{
				try {
					os.close();
				} catch (IOException e) {
					logger.warn("Output stream failed to close. Possible memory leak.");
				}
			}
			
			if (wr != null)
			{
				try {
					wr.close();
				} catch (IOException e) {
					logger.warn("Output writer failed to close. Possible memory leak.");
				}
			}
			
			if (conn != null)
				conn.disconnect();
		}
	}

    /**
     * Gets a list of supported sequence names from the controller that are supported
     * across the attraction.
     * @return Collection of sequence names (not including their file extensions).
     * @throws Exception
     */
    public ReaderSequenceInfo getReaderSequenceInfo(boolean includeColors) throws Exception
    {
        URL url = null;
        InputStream inputStream = null;
        try
        {
            // xbrc to go to
            String urlString = UIProperties.getInstance().getUiConfig().getControllerURL() + (includeColors ? "/sequences" : "/sequencesnocolors");

            // connection url
            url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) XbrcSSLUtil.getConnection(url);
            if (conn.getResponseCode()>=400)
            {
                logger.error("Error: " + conn.getResponseCode() + " received from Controller (" + url.toString() + ")");
                return null;
            }

            inputStream = conn.getInputStream();
            ReaderSequenceInfo sequences = XmlUtil.convertToPojo(inputStream, ReaderSequenceInfo.class);

            if (sequences == null)
                return null;

            logger.debug("Call to " + urlString + " returned " + sequences.getReaderSequences().size() + " sequences.");

            return sequences;
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
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e){
                logger.trace(e.getLocalizedMessage());
            }
        }
        return null;
    }
    
    /*
	 * Make a restful call to the XBRC asking it to re-load its configuration from the Config table.
	 */
	public void notifyXbrcOfReaderSignalChange(String readerId) throws Exception {
		
		HttpURLConnection conn = null;
		URL url;
		try
		{
			// xbrc to go to
			String urlString = UIProperties.getInstance().getUiConfig().getControllerURL() + 
					"/updatereadersignalconfig" + "?readerid=" + readerId;		
			
			// connection url
			url = new URL(urlString);
		
			// establish PUT request
			conn = (HttpURLConnection) XbrcSSLUtil.getConnection(url);
			conn.setRequestMethod("PUT");
			conn.setDoOutput(false);		
		
			if (conn.getResponseCode()>=400)
				logger.error("Error: " + conn.getResponseCode() + " received (" + url.toString() + ")");
		}
		finally
		{			
			if (conn != null)
				conn.disconnect();
		}
	}
}
