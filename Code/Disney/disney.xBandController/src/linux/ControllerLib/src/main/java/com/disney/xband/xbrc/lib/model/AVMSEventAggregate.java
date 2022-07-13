package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.AVMSEvent;

public class AVMSEventAggregate extends EventAggregate
{
	private AVMSEvent avmsEvent;
	
	public AVMSEventAggregate(String sID, String sReader, Date Timestamp, AVMSEvent avmsEvent)
	{
		super(sID, "", sReader, Timestamp);
		this.avmsEvent = avmsEvent;
	}

	public AVMSEvent getAvmsEvent()
	{
		return avmsEvent;
	}

	public void setAvmsEvent(AVMSEvent avmsEvent)
	{
		this.avmsEvent = avmsEvent;
	}
}
