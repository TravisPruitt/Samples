package com.disney.xband.xbms.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbms.dao.ConnectionStatus;
import com.disney.xband.xbms.dao.DAOFactory;

public class StatusInfo
{
	public static StatusInfo INSTANCE = new StatusInfo();
	private static Logger logger = Logger.getLogger(StatusInfo.class);
	
	
	private Date startTime;
	private String hostname;
	private static String version;
	
	static
	{
		
		InputStream manifestStream = null;

		try 
		{
            manifestStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("META-INF/MANIFEST.MF");

			Manifest manifest = new Manifest(manifestStream);
			Attributes attributes = manifest.getMainAttributes();
			version = attributes.getValue("Implementation-Title");
		}
		catch(IOException ex) 
		{
			logger.warn("Error while reading version: " + ex.getMessage());
		}
        finally 
        {
            if(manifestStream != null) 
            {
                try 
                {
                    manifestStream.close();
                }
                catch(Exception ignore) 
                {
                }
            }
        }

		if (version == null || version.isEmpty())
		{
			version = "Development";
		}
	}
	
	private ConnectionStatus connectionStatus = new ConnectionStatus();
	
	private StatusInfo()
	{
		this.startTime = new Date();
		InetAddress addr;
		try 
		{
			addr = InetAddress.getLocalHost();
			this.hostname = addr.getHostName();
		} 
		catch (UnknownHostException e) 
		{
			this.hostname = "Not found.";
		}

	}
	
	public XbmsStatus getXbmsStatus()
	{
		XbmsStatus status = new XbmsStatus();
		status.setHostname(hostname);
		status.setStartTime(startTime);
		status.setStatus(connectionStatus.getStatus());
		status.setStatusMessage(connectionStatus.getMessage());
		status.setVersion(version);
		return status;
	}
	
	public String getStartTime()
	{
		return formatTime(this.startTime.getTime());
	}	
	
	public StatusType getStatus()
	{
		return connectionStatus.getStatus();
	}

	public String getStatusMessage()
	{
		return connectionStatus.getMessage();
	}
	
	public String getVersion()
	{
		return version;
	}

	public String getDatabaseVersion()
	{
		return connectionStatus.getVersion();
	}

	public String getHostname()
	{
		return this.hostname;
	}
	
	public void Check()
	{
		this.connectionStatus = DAOFactory.getDAOFactory().getStatus();
	}
	
	private String formatTime(long lTime)
	{
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}
}

