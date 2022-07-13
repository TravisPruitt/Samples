package com.disney.xband.xbms.web;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.lib.health.StatusType;

@XmlRootElement(name="status")
public class XbmsStatus
{
	private String hostname;
	private String version;
	private StatusType status;
	private String statusMessage;
	private Date startTime;
	private Date time;

	@XmlElement
	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	@XmlElement
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@XmlElement
	public StatusType getStatus()
	{
		return status;
	}

	public void setStatus(StatusType status)
	{
		this.status = status;
	}

	@XmlElement
	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	@XmlElement
	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	@XmlElement
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	@XmlElement
	public String getDatabaseURL() 
	{
		return ConfigurationSettings.INSTANCE.getDatabaseURL();
	}

	@XmlElement
	public String getDatabaseUser() 
	{
		return ConfigurationSettings.INSTANCE.getDatabaseUser();
	}

	@SuppressWarnings("unused")
	private void setDatabaseURL(String s) 
	{
	}
	
	@SuppressWarnings("unused")
	private void setDatabaseUser(String s) 
	{
	}

}
