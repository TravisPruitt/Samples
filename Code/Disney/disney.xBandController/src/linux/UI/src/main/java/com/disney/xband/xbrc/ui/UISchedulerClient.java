package com.disney.xband.xbrc.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.scheduler.ui.SchedulerClient;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.xbrc.ui.httpclient.XbrcSSLUtil;

public class UISchedulerClient<T> extends SchedulerClient {

	private static Logger logger = Logger.getLogger(UISchedulerClient.class);

	@Override
	public SchedulerResponse send(SchedulerRequest req, Object context)
			throws Exception
	{
		OutputStreamWriter wr = null;
		OutputStream os = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		URL url;
		try
		{
			// xbrc to go to
			String urlString = UIProperties.getInstance().getUiConfig().getControllerURL() + "/scheduler/message";
			String xml = null;
			
			xml = XmlUtil.convertToXml(req, SchedulerRequest.class);
			
			// connection url
			url = new URL(urlString);
		
			// establish PUT request
			conn = (HttpURLConnection) XbrcSSLUtil.getConnection(url);
			conn.setRequestMethod("PUT");
		
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type", "application/xml");
			conn.setRequestProperty("Content-length", Integer.toString(xml.length()));
			os = conn.getOutputStream();
			wr = new OutputStreamWriter(os);
			wr.write(xml);
			wr.flush();
		
			if (conn.getResponseCode()>=400)
				logger.error("Error: " + conn.getResponseCode() + " received (" + url.toString() + ")");
			
			inputStream = conn.getInputStream();
			SchedulerResponse resp = XmlUtil.convertToPojo(inputStream, SchedulerResponse.class);
			
			return resp;
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
			
			if (inputStream != null)
			{
				try  {
					inputStream.close();
	            } catch (IOException e){}
			}
			
			if (conn != null)
				conn.disconnect();
		}
	}
	
	

}
