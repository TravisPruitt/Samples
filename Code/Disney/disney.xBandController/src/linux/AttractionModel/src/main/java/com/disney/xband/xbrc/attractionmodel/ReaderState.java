package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;

public class ReaderState
{
	Boolean waitingForGxp;
	Boolean available;
	Date expireTime;
	
	public ReaderState(Boolean available, Date expireTime, Boolean waitingForGxp)
	{
		setState(available,expireTime, waitingForGxp);
	}
	
	public void setState(Boolean available, Date expireTime, Boolean waitingForGxp)
	{
		this.available = available;
		this.expireTime = expireTime;
		this.waitingForGxp = waitingForGxp;
	} 
	
	public Boolean getAvailable()
	{
		return available;
	}
	public void setAvailable(Boolean available)
	{
		this.available = available;
	}
	public Date getExpireTime()
	{
		return expireTime;
	}
	public void setExpireTime(Date expireTime)
	{
		this.expireTime = expireTime;
	}

	public Boolean getWaitingForGxp() {
		return waitingForGxp;
	}

	public void setWaitingForGxp(Boolean waitingForGxp) {
		this.waitingForGxp = waitingForGxp;
	}
}
