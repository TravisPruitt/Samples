package com.disney.xband.xbrc.gxpcheck.entity;

import java.io.Serializable;

public class Location implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int id;
	private String arrived;
	private String latest;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getArrived()
	{
		return arrived;
	}
	public void setArrived(String arrived)
	{
		this.arrived = arrived;
	}
	public String getLatest()
	{
		return latest;
	}
	public void setLatest(String latest)
	{
		this.latest = latest;
	}
	
	

}
