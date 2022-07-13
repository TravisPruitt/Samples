package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="location")
public class JmsLocationInfo
{
	private String name;
	private String arrived;
	
	@XmlElement(name="name")
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	@XmlElement(name="arrived")
	public String getArrived()
	{
		return arrived;
	}
	public void setArrived(String arrived)
	{
		this.arrived = arrived;
	}
}
