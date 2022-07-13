package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.*;

public class MetricsMessage extends XbrcMessage {
	
	private static final long serialVersionUID = 1L;

	private String starttime;

	private String endtime;
	
	private Metrics standby;

	private Metrics xpass;

	@XmlElement(name="starttime")
	public String getStartTime()
	{
		return this.starttime;
	}
	
	public void setStartTime(String starttime)
	{
		this.starttime = starttime;
	}
	
	@XmlElement(name="endtime")
	public String getEndTime()
	{
		return this.endtime;
	}
	
	public void setEndTime(String endtime)
	{
		this.endtime = endtime;
	}
	
	@XmlElement(name="standby")
	public Metrics getStandBy()
	{
		return this.standby;
	}
	
	public void setStandBy(Metrics standby)
	{
		this.standby = standby;
	}
	
	@XmlElement(name="xpass")
	public Metrics getxPass()
	{
		return this.xpass;
	}
	
	public void setxPass(Metrics xpass)
	{
		this.xpass = xpass;
	}
}
