package com.disney.xband.jms.lib.entity.gxp;

import javax.xml.bind.annotation.XmlElement;

public class ReturnWindow 
{
	private String startTime;
	
	private String endTime;
	
	@XmlElement(name="startTime")
	public String getStartTime()
	{
		return this.startTime;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	
	@XmlElement(name="endTime")
	public String getEndTime()
	{
		return this.endTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
}
