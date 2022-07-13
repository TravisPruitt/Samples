package com.disney.xband.idms.dao;

import com.disney.xband.common.lib.health.StatusType;

public class ConnectionStatus 
{
	private StatusType status;
	private String version;
	private String message;
	
	public StatusType getStatus()
	{
		return this.status;
	}
	
	public void setStatus(StatusType status)
	{
		this.status = status;
	}
	
	public String getVersion()
	{
		if(this.version == null)
		{
			return "";
		}
		
		return this.version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public String getMessage()
	{
		if(this.message == null)
		{
			return "";
		}
		return this.message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
