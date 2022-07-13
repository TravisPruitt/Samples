package com.disney.xband.gxp;

/*
 * Class used to tell an xBRC to turn control a light
 */
public class LightMessage
{
	private String status;
	private long timeout;
	
	public LightMessage()
	{
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public long getTimeout()
	{
		return timeout;
	}

	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}
	
	

}
