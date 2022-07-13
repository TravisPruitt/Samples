package com.disney.xband.lib.xbrapi;

public class XtpGpioEvent extends XbrEvent
{
	private String channel;
	
	@Override
	public String getID()
	{
		// There is no id for this event except the time when it occurred.
		return new Long(super.getTime().getTime()).toString();
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

}
