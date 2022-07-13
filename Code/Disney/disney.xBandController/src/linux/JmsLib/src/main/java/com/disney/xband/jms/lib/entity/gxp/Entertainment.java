package com.disney.xband.jms.lib.entity.gxp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Entertainment 
{
	private long entertainmentId;
	private String experienceType;
	private long locationId;

	@XmlElement(name="entertainmentId")
	public long getEntertainmentId()
	{
		return this.entertainmentId;
	}
	
	public void setEntertainmentId(long entertainmentId)
	{
		this.entertainmentId = entertainmentId;
	}
	
	@XmlElement(name="experienceType")
	public String getExperienceType()
	{
		return this.experienceType;
	}
	
	public void setExperienceType(String experienceType)
	{
		this.experienceType = experienceType;
	}
	
	@XmlElement(name="locationId")
	public Long getLocationId()
	{
		return this.locationId;
	}
	
	public void setLocationId(long locationId)
	{
		this.locationId = locationId;
	}
}
