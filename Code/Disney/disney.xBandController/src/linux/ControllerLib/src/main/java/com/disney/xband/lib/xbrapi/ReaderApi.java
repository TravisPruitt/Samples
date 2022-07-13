package com.disney.xband.lib.xbrapi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.lib.xbrapi.XbrBandCommand.XMIT_MODE;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class ReaderApi
{
	private static Logger logger = Logger.getLogger(ReaderApi.class);
	// Reader connection timeout
	private static int connectionTimeout = 5000;
    private static int readTimeout = 5000;

	public static void setConnectionTimeout(int connectionTimeout)
	{
		ReaderApi.connectionTimeout = connectionTimeout;
        ReaderApi.readTimeout = connectionTimeout;
	}

	public static int setBiometricOptions(ReaderInfo reader, BioOptionsMsg options)
	{
		String url = "/biometric/options?image_capture=" + (options.isImageCapture() ? "on" : "off");
		return sendPost(reader.getURL() + url, "");
	}
	
	public static int setRfidOptions(ReaderInfo reader, RfidOptionsMsg options)
	{
		String url = "/rfid/options?test_loop=" + (options.isTestLoop() ? "on" : "off") + 
					"&secure_id=" + (options.isSecureId() ? "on" : "off");
		return sendPost(reader.getURL() + url, "");
	}
	
	public static int setTapOptions(ReaderInfo reader, String sequence, Long timeout)
	{
		String url;
		if (LightMsg.isColorValue(sequence))
			url = "/tap/options?color=" + sequence;
		else
			url = "/tap/options?sequence=" + sequence;
		if (timeout != null)
			url += "&timeout=" + timeout.toString();
		return sendPost(reader.getURL() + url, "");
	}
	
	/*
	 * Only for xTP2 and above
	 */
	public static int setIdleSequence(ReaderInfo reader, String sequence)
	{
		if (reader.getHardwareType() == null || reader.getHardwareType().toLowerCase().equals("xtp1"))
			return 200;
		
		String url = "/media/idle?name=" + sequence;
		return sendPost(reader.getURL() + url, "");
	}
	
	public static int deleteIdleSequence(ReaderInfo reader)
	{
		if (reader.getHardwareType() == null || reader.getHardwareType().toLowerCase().equals("xtp1"))
			return 200;
		
		return sendDelete(reader.getURL() + "/media/idle");
	}

	public static int setReaderTime(ReaderInfo reader)
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = sdf.format(now);
		String url = "/time?time=" + time;
		return sendPost(reader.getURL() + url, "");
	}

	public static int resetBioTapReader(ReaderInfo reader)
	{
		// NOTE: the order of these two operations matter.
		// biometric/cancel resets the idle sequence which then needs to be
		// restored by calling setReaderSequence..
		sendPost(reader.getURL() + "/biometric/cancel", "");
		return setReaderSequence(reader, "off", 1l);
	}

	public static int setReaderLight(ReaderInfo reader,
			LightMsg.ColorValue color, Long timeout)
	{
		String url = "/light" + "?color=" + color.name();
		if (timeout != null)
			url += "&timeout=" + timeout.toString();
		return sendPost(reader.getURL() + url, "");
	}
	
	/*
	 * setReaderColor() is intended for the new xTP2 readers with RGB colors
	 */
	public static int setReaderColor(ReaderInfo reader, String color, Long timeout)
	{
		String url;
		
		if (reader.getHardwareType() == null || reader.getHardwareType().toLowerCase().equals("xtp1"))
    	{
    		// talking to an old reader now..
    		url = "/light?color=" + color;
    	}
		else
		{
			url = "/media/color?name=" + color;
		}
		
		if (timeout != null)
			url += "&timeout=" + timeout.toString();
		return sendPost(reader.getURL() + url, "");
	}

    public static int setReaderSequence(ReaderInfo reader,
			String sSequenceName, Long timeout)
	{
    	String url = null;

        // Encode the reader name before sending it.
        try {
            sSequenceName = URLEncoder.encode(sSequenceName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unable to encode sequence name.");
        }

        if (reader.getHardwareType() == null || reader.getHardwareType().toLowerCase().equals("xtp1"))
    	{
    		// talking to an old reader now..
    		url = "/light" + "?sequence=" + sSequenceName;
    	}
    	else
    	{
	        url = "/media/sequence" + "?name=" + sSequenceName;	        
    	}
    	
    	if (timeout != null)
            url += "&timeout=" + timeout.toString();
                
        int retCode = sendPost(reader.getURL() + url, "");
        if ( retCode != 200 ) {
            logger.warn("Unable to play sequence. Code=" + retCode + ";Reader=" + reader.getName() +
                    ";Sequence=" + sSequenceName);
        }

        return retCode;
    }
    
    /**
     * Request from a Reader to play a light+sound identification sequence on demand.
     * Handy when we need to find a physical reader in a room full of readers.
     * 
     * @param reader Reader unit to send this request to.
     * @param successSequenceName
     * @param timeout
     */
    public static int readerPlayIdentificationSequence(ReaderInfo reader,
			String successSequenceName, Long timeout)
	{
    	String url = null;
    	
    	if (reader == null || successSequenceName == null)
    		return 400;

        // Encode the sequence name before sending it.
        try 
        {
        	successSequenceName = URLEncoder.encode(successSequenceName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unable to encode sequence name.");
            return -1;
        }

        if (reader.getHardwareType() == null || reader.getHardwareType().trim().isEmpty()
        		|| reader.getHardwareType().toLowerCase().equals("xtp1"))
    	{
    		// when talking to an old non-RGB reader, ask it to play the preconfigured success sequence
    		url = "/light" + "?sequence=" + successSequenceName;
    		
    		if (timeout != null)
                url += "&timeout=" + timeout.toString();
    		
    		if (logger.isDebugEnabled())
    			logger.debug("Executing PUT " + reader.getURL() + url + " For reader with hardware type " + reader.getHardwareType());
    		
    		return sendPost(reader.getURL() + url, "");
    	}
    	else
    	{
    		/*
    		 *  In case of RGB readers, ask it to play the special identify sequence and only when
    		 *  that fails, ask it to play the predefined success sequence
    		 */
    		url = "/media/sequence?name=identify";
    		if (timeout != null)
    			url += "&timeout=" + timeout.toString();
    		
    		if (logger.isDebugEnabled())
    			logger.debug("Executing PUT " + reader.getURL() + url + " For reader with hardware type " + reader.getHardwareType());

    		int codeReturned = sendPost(reader.getURL() + url, "");
    		
    		if (codeReturned >= 400)
    		{
    			url = "/media/sequence?name=" + successSequenceName;	
    			if (timeout != null)
        			url += "&timeout=" + timeout.toString();
    			
    			codeReturned = sendPost(reader.getURL() + url, "");
    		}
    		
    		return codeReturned;
    	}
    }

    public static int biometricEnroll(ReaderInfo reader)
	{
		return sendPost(reader.getURL() + "/biometric/enroll", "");
	}

	public static int install(ReaderInfo reader, String url)
	{
		return sendPost(reader.getURL() + "/install?url=" + url, "");
	}

    public static int installMediaPackage(ReaderInfo reader, InputStream mediaStream, String mediaFileHash)
    {
        return sendMediaPackage(reader.getURL() + "/media/package", mediaStream, mediaFileHash);
    }

    public static int upgrade(ReaderInfo reader, ReaderManifest rm, int readTimeout) throws Exception
	{
		ObjectMapper om = new ObjectMapper();
		String payload = om.writeValueAsString(rm);
		return sendPost(reader.getURL() + "/upgrade", payload, connectionTimeout, readTimeout);
	}

	public static int biometricMatch(ReaderInfo reader, List<String> templates)
	{
		BioMatchMsg msg = new BioMatchMsg();
		msg.setTemplates(templates);
		String str = XbrJsonMapper.serializeBioMatch(msg);
		return sendPost(reader.getURL() + "/biometric/match", str);
	}

	public static int askForBioImageEvent(ReaderInfo reader, String uid,
			Long transactionId, Long templateId)
	{
		return sendPost(reader.getURL() + "/biometric/image/send?uid=" + uid
				+ "&transactionId=" + transactionId + "&templateId="
				+ templateId, "");
	}
	
	public static int sendSetSignalThreshold(ReaderInfo reader, int nThreshold)
	{
		return sendPost(reader.getURL() + "/filter?min-ss=" + nThreshold, "");
	}
	
	public static int sendBandCommands(List<XbrBandCommand> commands, ReaderInfo ri, XMIT_MODE mode, String group)
	{
		return sendBandCommands(commands, ri.getURL(), mode, group);
	}
	
	public static int sendBandCommands(List<XbrBandCommand> commands, String readerUrl, XMIT_MODE mode, String group)
	{
		if (mode == null || (mode != null && mode == XMIT_MODE.BROADCAST))
		{
			for (XbrBandCommand cmd : commands)
			{
				if (cmd.getMode() != XMIT_MODE.BROADCAST)
					continue;
				
				String data = prepareBeaconArguments(null, cmd, group);
				
				if (!data.isEmpty())
				{
					return sendPut(readerUrl + "/xband/beacon" + data, "");
				}
			}
		} 
		
		if (mode == null || (mode != null && mode == XMIT_MODE.REPLY))
		{
			String data = serializeBandCommands(commands);
			
			// Send broadcast for command
			if (!data.isEmpty())
			{
				return sendPut(readerUrl + "/xband/commands", data);
			}
		}
		
		return 200;
	}
	
	public static int getXbrcUrl(ReaderInfo ri, StringBuffer url) throws Exception
	{
		StringBuffer temp = new StringBuffer();
		
		int retCode = sendGet(ri.getURL() + "/xbrc", temp);
		if (retCode != 200)
			return retCode;
		
		JSONObject jo = JSONObject.fromObject(temp.toString());
		url.append(jo.get("url"));
		
		return retCode;
	}

	public static int sendDeleteBandCommands(ReaderInfo ri) throws Exception
	{
		if (ri == null)
			return 0;
		
		if (!ReaderType.isLongRangeReader(ri.getType()))
		{
			logger.warn("DELETE xband/comands and xband/beacon command may only be invoked on long range readers. Reader " + ri.getIpAddress() + " is of type " + ri.getType().name());
			return 404;
		}
		
		try
		{
			// try the new api first
			logger.trace("Sending DELETE /xband/output to reader " + ri.getIpAddress());
			int returnCode = sendDelete(ri.getURL() + "/xband/output");
			if (returnCode != HttpURLConnection.HTTP_OK)
			{
				logger.warn("Failed to DELETE /xband/output to reader " + ri.getIpAddress());
				return returnCode;
			}
		}
		catch (Exception e) {}
		
		// result to doing it the old way
		logger.trace("Sending DELETE /xband/commands to reader " + ri.getIpAddress());
		int returnCode = sendDelete(ri.getURL() + "/xband/commands");
		if (returnCode != HttpURLConnection.HTTP_OK)
			logger.warn("DELETE /xband/commands on reader " + ri.getIpAddress() + " failed with http code " + returnCode);
		
		logger.trace("Sending DELETE /xband/beacon to reader " + ri.getIpAddress());
		returnCode = sendDelete(ri.getURL() + "/xband/beacon");
		if (returnCode != HttpURLConnection.HTTP_OK)
			logger.warn("DELETE /xband/beacon on reader " + ri.getIpAddress() + " failed with http code " + returnCode);
		
		return returnCode;
	}

	public static String prepareBeaconArguments(String sId, XbrBandCommand command, String group)
	{
		StringBuilder sb = new StringBuilder();
		
		if (command.getMode() != XMIT_MODE.BROADCAST)
			return sb.toString();

		sb.append("?");
		
		if (sId != null)
		{
	        sb.append("XLRID=" + sId + "&");
		}
		
		String sCmd = command.getCommand().formatInHex(command.getInterval(), command.getTimeout());
		sb.append("cmd=" + sCmd);
		
		if (group != null && !group.isEmpty())
			sb.append("&group=" + group);
		
		return sb.toString();
	}

	public static String serializeBandCommands(List<XbrBandCommand> cmds)
	{
		StringBuffer sb = new StringBuffer();
		
		if (cmds == null || cmds.size() == 0)
			return sb.toString();
		
		// this function is for reply commands only, use prepareBeaconArguments() method for broadcast commands
		List<XbrBandCommand> replyCommandsOnly = new LinkedList<XbrBandCommand>();
		for (XbrBandCommand cmd : cmds)
		{
			if (cmd.getMode() == XMIT_MODE.BROADCAST)
				continue;
			
			// REPLY commands with an empty band list cause trouble on the reader side
			if (cmd.getRecipients() != null && cmd.getRecipients().size() > 0)
			{
				if (cmd.getListLRIDs() == null || cmd.getListLRIDs().size() == 0)
					continue;
					
			}
			
			replyCommandsOnly.add(cmd);
		}
		
		if (replyCommandsOnly.size() == 0)
			return sb.toString();
		
		// construct the payload
		sb.append("{\n");
		sb.append("    \"commands\":\n");
		sb.append("    [\n");
		int cCmds = replyCommandsOnly.size();
		for (int iCmd=0; iCmd<cCmds; iCmd++)
		{
			XbrBandCommand cmd = replyCommandsOnly.get(iCmd);

			sb.append("        {\n");
			
			Integer threshold = null;
			
			// send either to a finite list of bands or by signal strength transit threshold
			if (cmd.getListLRIDs() != null && cmd.getListLRIDs().size() > 0)
			{
				sb.append("            \"XLRID\":\n");
				sb.append("            [\n");
				int cids = cmd.getListLRIDs().size();
				StringBuffer bandList = new StringBuffer();
				for (int id=0; id<cids; id++)
				{
					String sid = cmd.getListLRIDs().get(id);
					bandList.append("                \"" + sid + "\"");
					if (id!=(cids-1))
						bandList.append(",");
					bandList.append("\n");
				}
				sb.append(bandList);
				sb.append("            ],\n");
				
				// Send command to a list of bands
				if (logger.isTraceEnabled())
					logger.trace("Sending " + cmd.toString() + " to bands: " + bandList);
			}
			else
				threshold = -127;	// either recipients or threshold or both must be specified (but not none of the two)
			
			if (cmd.getThreshold() != null && (cmd.getEnableThreshold() != null && cmd.getEnableThreshold().equals(true)))
				threshold = cmd.getThreshold();
			
			if (threshold != null)
			{
				sb.append("            \"ss\": " + threshold + ",\n");
					
				// Send command to all bands within specified signal strength transmit threshold
				if (logger.isTraceEnabled())
					logger.trace("Sending " + cmd.toString() + " to all bands within signal strength transmit threshold of " + cmd.getThreshold());
			}
			
			sb.append("            \"cmd\" : \"" + cmd.getCommand().formatInHex(cmd.getInterval(), cmd.getTimeout()) + "\"\n");
			sb.append("        }");
			if (iCmd!=(cCmds-1))
				sb.append(",");
			sb.append("\n");

		}
		sb.append("    ]\n");
		sb.append("}\n");
		
		return sb.toString();
		
	}

	public static int sendPost(String sUrl, String sData, int connectionTimeout, int readTimeout)
	{
		OutputStreamWriter wr = null;
		try
		{
			logger.trace("Attempt POST to reader: " + sUrl + " " + sData);

			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setConnectTimeout(connectionTimeout);
			httpCon.setReadTimeout(readTimeout);
            httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");

			wr = new OutputStreamWriter(
					httpCon.getOutputStream());
			wr.write(sData);
			wr.flush();

			int retCode = httpCon.getResponseCode();

			logger.trace("POST to reader completed: " + sUrl + " " + sData);
			return retCode;
		}
		catch (MalformedURLException e)
		{
			logger.error("Bad URL when talking to reader. URL:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			logger.error("IO error when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		finally
		{
			if (wr != null)
			{
				try 
				{
					wr.close();	//flushes it first
				}
				catch (IOException e) {}
			}
		}
		
		return 500;
	}
	
	public static int sendPost(String sUrl, String sData)
	{
		return sendPost(sUrl, sData, connectionTimeout, readTimeout);
	}

    public static int sendMediaPackage(String sUrl, InputStream binaryData, String mediaFileHash)
    {
	/*
        Assert.assertNotNull(mediaFileHash);
        Assert.assertNotNull(binaryData);
        Assert.assertNotNull(sUrl);
	*/

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try
        {
            logger.trace("Attempt binary POST to reader: " + sUrl);

            URL url = new URL(sUrl);

            HttpURLConnection httpCon = (HttpURLConnection) url
                    .openConnection();
            httpCon.setConnectTimeout(connectionTimeout);
            httpCon.setReadTimeout(readTimeout);
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            // Set the binary header.
            httpCon.setRequestProperty("CONTENT-TYPE", "application/octet-stream");
            httpCon.setRequestProperty("X-Media-Hash", mediaFileHash);

            inputStream = new BufferedInputStream(binaryData);
            outputStream = new BufferedOutputStream(httpCon.getOutputStream());            

            byte[] buffer = new byte[8000];
            int bytesRead = inputStream.read(buffer, 0, 8000);
            while ( bytesRead > 0 )
            {            
                outputStream.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer, 0, 8000);
            }

            outputStream.flush();
            outputStream.close();
            int retCode = httpCon.getResponseCode();

            logger.trace("Binary POST to reader completed: " + sUrl);
            return retCode;
        }
        catch (MalformedURLException e)
        {
            logger.error("Bad URL when talking to reader. Url:" + sUrl + ",Error:"
                    + e.getLocalizedMessage());
        }
        catch (IOException e)
        {
            logger.error("IO error when talking to reader. Url:" + sUrl + ",Error:"
                    + e.getLocalizedMessage());
        }
        finally
        {
        	if (inputStream != null)
        	{
        		try 
        		{
        			inputStream.close();
        		}
        		catch (IOException e){}
        	}
        	
        	if (outputStream != null)
        	{
        		try 
        		{
        			outputStream.close();
        		}
        		catch (IOException e){}
        	}
        }

        return 500;
    }

    public static int sendPut(String sUrl, String sData)
	{
    	BufferedWriter bw = null;
		try
		{
			logger.trace("Attempt PUT to reader: " + sUrl + " " + sData);

			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setConnectTimeout(connectionTimeout);
            httpCon.setReadTimeout(readTimeout);
            httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			httpCon.setRequestProperty("Content-type", "text/plain; charset=UTF-8");
			httpCon.setRequestProperty("Content-length", Integer.toString(
					sData == null ? 0 : sData.length()));

			bw = new BufferedWriter(new OutputStreamWriter(httpCon.getOutputStream()));

			bw.write(sData);
			bw.flush();

			int retCode = httpCon.getResponseCode();

			logger.trace("PUT to reader completed: " + sUrl + " " + sData);
			
			return retCode;
		}
		catch (MalformedURLException e)
		{
			logger.error("Bad URL when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			logger.error("IO error when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		finally
		{
			if (bw != null)
			{
				try
				{
					bw.close();	//flushes the stream
				}
				catch (IOException e){}
			}
		}
		
		return 500;
	}

	public static int sendDelete(String sUrl)
	{
		try
		{
			logger.trace("Attempt DELETE to reader: " + sUrl);
			
			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setRequestMethod("DELETE");
			httpCon.setConnectTimeout(connectionTimeout);
			httpCon.setReadTimeout(readTimeout);
			httpCon.connect();
			httpCon.getInputStream();
            int retCode = httpCon.getResponseCode();

			logger.trace("DELETE to reader completed: " + sUrl);
			
			return retCode;
		}
		catch (MalformedURLException e)
		{
			logger.error("Bad URL when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			logger.error("IO error when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
        }
		
		return 500;
	}
	
	public static int sendGet(String sUrl, StringBuffer dataReceived)
	{
		BufferedReader br = null;
		
		try
		{
			logger.trace("Attempt to send GET to reader: " + sUrl);
			
			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setRequestMethod("GET");
			httpCon.setConnectTimeout(connectionTimeout);
			httpCon.setReadTimeout(readTimeout);
			httpCon.connect();
			httpCon.getInputStream();
            int retCode = httpCon.getResponseCode();
           
            br = new BufferedReader(
                    new InputStreamReader(
                    		httpCon.getInputStream()));
			String inputLine;			
			while ((inputLine = br.readLine()) != null) 
				dataReceived.append(inputLine);			

			logger.trace("GET to reader completed: " + sUrl);
			
			return retCode;
		}
		catch (MalformedURLException e)
		{
			logger.error("Bad URL when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			logger.error("IO error when talking to reader. Url:" + sUrl + ",Error:"
					+ e.getLocalizedMessage());
        }
		finally
		{
			if (br != null)
			{
				try 
				{
					br.close();
				} catch (Exception e){}
			}
		}
		
		return 500;
	}
	
	/**
	 * Restart the reader application.
	 * @param ri
	 * @return
	 * @throws Exception
	 */
	public static int sendRestartReader(ReaderInfo ri) throws Exception
	{
		if (ri == null)
			return 0;
		
		return sendPut(ri.getURL() + "/application/reset", "");
	}
	
	/**
	 * Reboot the reader
	 * @param ri
	 * @return
	 * @throws Exception
	 */
	public static int sendRebootReader(ReaderInfo ri) throws Exception
	{
		if (ri == null)
			return 0;
		
		return sendPut(ri.getURL() + "/system/reset", "");
	}

	/**
	 * Shutdown unused radio channels
	 * @param ri
     * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static int sendRadioPower(ReaderInfo ri) throws Exception
	{
		String options = "";
		List<Boolean> antennas = null;
		if (ri == null || !"xBR4".equals(ri.getHardwareType()))
			return 0;
		
		antennas = ri.getAntennas();
		if (antennas == null)
			options = "?group0=1&group1=1&group2=1";
		else
		{
			for (int i = 2; i < antennas.size(); i += 2) // First two antennas cannot be powered down and are not part of any group.
			{
				int groupId = i / 2 - 1;
				
				String group = null;
				Boolean power1 = antennas.get(i);
				Boolean power2 = antennas.get(i + 1);
				
				if (power1 == null || Boolean.TRUE.equals(power1) || power2 == null || Boolean.TRUE.equals(power2))
					group = "group" + groupId + "=1";
				else
					group = "group" + groupId + "=0";
	
				if (group != null)
				{
					if (options.isEmpty())
						options = options + "?" + group;
					else
						options = options + "&" + group;
				}
			}
		}
		
		if (!options.isEmpty())
			return sendPut(ri.getURL() + "/radio/power" + options, "");
		return 0;
	}
	
	/**
	 * Shutdown the reader
	 * @param ri
     * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static int sendShutdownReader(ReaderInfo ri, int timeout) throws Exception
	{
		if (ri == null)
			return 0;
		
		return sendPut(ri.getURL() + "/system/shutdown?timeout=" + String.valueOf(timeout), "");
	}
	
	/**
	 * To remove xbrc information from a reader, both DELETE update_stream and DELETE xbrc
	 * restful commands must be invoked.
	 * 
	 * @param ri
     * @param sendDelete
	 * @return <CODE>true</CODE> for success and <CODE>false</CODE> in case either the update_stream or xbrc command fail.
	 */
	public static boolean removeXbrcInfo(ReaderInfo ri, boolean sendDelete)
	{
		if (ri == null)
			return false;
		
		int result = ReaderApi.sendDelete(ri.getURL() + "/update_stream");
		if (result == 500)
			return false;

        if(sendDelete) { // Do not send "/xbrc" command in the case of "unlink" operation
		    result = ReaderApi.sendDelete(ri.getURL() + "/xbrc");
		    if (result == 500)
			    return false;
		    
		    result = ReaderApi.sendPut(ri.getURL() + "/reader/name?name=", "");
			if (result == 500)
				return false;
        }
		
		return true;
	}

}
