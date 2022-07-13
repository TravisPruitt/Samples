package com.disney.xband.common.lib.health;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="status")
public class IDMSStatus implements Status
{	
	private String hostname;
	private String version;
	private StatusType status;
	private String statusMessage;
	private Date startTime;
	private String databaseURL;
	private String databaseUserName;
	private String databaseVersion;
	private boolean readOnly;
	private Date time;
	
	public IDMSStatus()
	{
	}

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
	public String getDatabaseVersion()
	{
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion)
	{
		this.databaseVersion = databaseVersion;
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
	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}
	
	@XmlElement
	public String getDatabaseUserName() {
		return databaseUserName;
	}

	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
}
