package com.disney.xband.xbrc.parkentrymodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class GSTInfo
{	
	private String name;
	private String time;
	private List<GuestStatus<GuestStatusState>> guests;
	private List<CMST> cmstlist;
	
	public GSTInfo() {}

	@XmlAttribute(name="name")
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlAttribute(name="time")	
	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	@XmlElementWrapper(name="guests")
	@XmlElement(name="guest")
	public List<GuestStatus<GuestStatusState>> getGuests()
	{
		return guests;
	}

	public void setGuests(List<GuestStatus<GuestStatusState>> guests)
	{
		this.guests = guests;
	}

	@XmlElementWrapper(name="castmembers")
	@XmlElement(name="castmember")
	public List<CMST> getCmstlist()
	{
		return cmstlist;
	}

	public void setCmstlist(List<CMST> cmstlist)
	{
		this.cmstlist = cmstlist;
	};	
}