package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BandAccessory 
{
	private String bandAccessoryCode;
	private String bandAccessoryType;
	
	@XmlElement(name="bandAccessoryCode")
	public String getBandAccessoryCode()
	{
		return this.bandAccessoryCode;
	}

	public void setBandAccessoryCode(String bandAccessoryCode)
	{
		this.bandAccessoryCode = bandAccessoryCode;
	}

	@XmlElement(name="bandAccessoryType")
	public String getBandAccessoryType()
	{
		return this.bandAccessoryType;
	}

	public void setBandAccessoryType(String bandAccessoryType)
	{
		this.bandAccessoryType = bandAccessoryType;
	}
}
