package com.disney.xband.xbrc.lib.entity;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;

public class HAStatusMessage extends HAMessage
{
	private HAStatusEnum previousStatus;
	private HAStatusEnum currentStatus;
	
	public HAStatusMessage(HAStatusEnum previousStatus, HAStatusEnum currentStatus)
	{
		super();
		setMessageType("HASTATUS");
		setMessageText("");
		this.previousStatus = previousStatus;
		this.currentStatus = currentStatus;
	}

	public HAStatusEnum getPreviousStatus()
	{
		return previousStatus;
	}

	public void setPreviousStatus(HAStatusEnum previousStatus)
	{
		this.previousStatus = previousStatus;
	}

	public HAStatusEnum getCurrentStatus()
	{
		return currentStatus;
	}

	public void setCurrentStatus(HAStatusEnum currentStatus)
	{
		this.currentStatus = currentStatus;
	}
}
