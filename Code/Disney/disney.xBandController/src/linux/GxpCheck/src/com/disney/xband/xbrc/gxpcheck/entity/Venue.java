package com.disney.xband.xbrc.gxpcheck.entity;

import java.util.List;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class Venue implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String time;
	
	@XmlElement(name="guest")
	private List<Guest> liGuests;

	public List<Guest> getGuests()
	{
		return liGuests;
	}

	public void setGuests(List<Guest> liGuests)
	{
		this.liGuests = liGuests;
	}

}
