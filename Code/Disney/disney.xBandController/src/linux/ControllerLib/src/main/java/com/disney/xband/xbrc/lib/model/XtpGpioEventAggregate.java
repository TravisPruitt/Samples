package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.XtpGpioEvent;

public class XtpGpioEventAggregate extends EventAggregate
{
	XtpGpioEvent xtpGpioEvent = null;
	
	public XtpGpioEventAggregate(String sID, String sReader, Date Timestamp, XtpGpioEvent ev)
	{
		super(sID, "", sReader, Timestamp);
		xtpGpioEvent = ev;
	}

	public XtpGpioEvent getXtpGpioEvent()
	{
		return xtpGpioEvent;
	}

	public void setXtpGpioEvent(XtpGpioEvent xtpGpioEvent)
	{
		this.xtpGpioEvent = xtpGpioEvent;
	}
}
