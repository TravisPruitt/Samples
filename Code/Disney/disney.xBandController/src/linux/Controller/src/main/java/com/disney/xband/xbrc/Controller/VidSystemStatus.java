package com.disney.xband.xbrc.Controller;

import java.util.Date;

import com.disney.xband.common.lib.health.StatusType;

public class VidSystemStatus 
{
	private Date lastHelloTime = new Date();
	private StatusType status = StatusType.Green;
	private String statusMessage;
	
	public Date getLastHelloTime() 
	{
		return lastHelloTime;
	}
	public StatusType getStatus() 
	{
		return status;
	}
	public String getStatusMessage() 
	{
		return statusMessage;
	}
	public void setLastHelloTime(Date lastHelloTime) 
	{
		this.lastHelloTime = lastHelloTime;
	}
	public void setStatus(StatusType status) 
	{
		this.status = status;
	}
	public void setStatusMessage(String statusMessage) 
	{
		this.statusMessage = statusMessage;
	}
}
