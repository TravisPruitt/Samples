package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.XtpGpioEventAggregate;

public class AvmsLaserEvent
{
	private Date time;
	private Date readerEventTime;
	private EventAggregate eventAggregate;

	public AvmsLaserEvent()
	{
		time = new Date();
		readerEventTime = new Date(time.getTime());
	}
	
	public AvmsLaserEvent(XtpGpioEventAggregate ev)
	{
		this.time = ev.getXtpGpioEvent().getTime();
		readerEventTime = new Date(time.getTime());
		eventAggregate = ev;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public Date getReaderEventTime()
	{
		return readerEventTime;
	}

	public void setReaderEventTime(Date readerEventTime)
	{
		this.readerEventTime = readerEventTime;
	}

	public EventAggregate getEventAggregate()
	{
		return eventAggregate;
	}

	public void setEventAggregate(EventAggregate eventAggregate)
	{
		this.eventAggregate = eventAggregate;
	}
}
