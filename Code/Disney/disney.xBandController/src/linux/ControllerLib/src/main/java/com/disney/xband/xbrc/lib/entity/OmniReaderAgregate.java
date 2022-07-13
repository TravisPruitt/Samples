package com.disney.xband.xbrc.lib.entity;

import com.disney.xband.xbrc.lib.entity.OmniServer;

public class OmniReaderAgregate
{
	private OmniServer omniServer;
	private long readerId;
	private int priority;
	
	public OmniReaderAgregate(OmniServer omniServer, long readerId, int priority){
		this.omniServer = omniServer;
		this.priority = priority;
		this.readerId = readerId;
	}
	
	public OmniServer getOmniServer()
	{
		return omniServer;
	}
	public void setOmniServer(OmniServer omniServer)
	{
		this.omniServer = omniServer;
	}
	public long getReaderId()
	{
		return readerId;
	}

	public void setReaderId(long readerId)
	{
		this.readerId = readerId;
	}

	public int getPriority()
	{
		return priority;
	}
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
}
