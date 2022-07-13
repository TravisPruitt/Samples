package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"id","startTime","endTime","links"})
public class Entitlement 
{
	private String id;
	private String startTime;
	private String endTime;
	private LinkCollection links;

	@XmlElement(name="id")
	public String getId()
	{
		return this.id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

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

	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
}
