package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="jmsmessagerange")
public class JmsMessageRange
{
	private String sStartDate;
	private String sEndDate;
	
	@XmlElement(name="startdate")
	public String getStartDate()
	{
		return sStartDate;
	}
	
	public void setStartDate(String sStartDate)
	{
		this.sStartDate = sStartDate;
	}
	
	@XmlElement(name="enddate")
	public String getEndDate()
	{
		return sEndDate;
	}
	
	public void setEndDate(String sEndDate)
	{
		this.sEndDate = sEndDate;
	}
	
}
