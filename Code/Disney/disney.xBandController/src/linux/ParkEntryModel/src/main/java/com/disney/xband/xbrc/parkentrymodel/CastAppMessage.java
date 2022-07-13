package com.disney.xband.xbrc.parkentrymodel;

import java.util.Date;

import com.disney.xband.parkgreeter.lib.message.toxbrc.PGMessage;

public class CastAppMessage
{
	private CastAppQueue queue;
	private PGMessage message;
	private boolean requestTimedOut = false;	
	private Date receivedTime;
	
	public CastAppMessage(CastAppQueue queue, PGMessage message)
	{
		this.queue = queue;
		this.message = message;
		receivedTime = new Date();
	}
	
	public CastAppQueue getQueue()
	{
		return queue;
	}
	public void setQueue(CastAppQueue queue)
	{
		this.queue = queue;
	}
	public PGMessage getMessage()
	{
		return message;
	}
	public void setMessage(PGMessage message)
	{
		this.message = message;
	}

	public boolean isRequestTimedOut()
	{
		return requestTimedOut;
	}

	public void setRequestTimedOut(boolean requestTimedOut)
	{
		this.requestTimedOut = requestTimedOut;
	}

	public boolean isInternal() {
		return queue == null;
	}

	public Date getReceivedTime() {
		return receivedTime;
	}
}
