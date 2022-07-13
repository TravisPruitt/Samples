package com.disney.xband.xbrc.gxpcheck.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="guest")
public class Guest implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Location location;
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public Location getLocation()
	{
		return location;
	}
	public void setLocation(Location location)
	{
		this.location = location;
	}

}

