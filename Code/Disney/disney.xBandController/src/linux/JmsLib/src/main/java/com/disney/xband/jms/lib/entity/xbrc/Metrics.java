package com.disney.xband.jms.lib.entity.xbrc;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

public class Metrics implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int guests;
	
	private int abandonments;
	
	private int mergetime;
	
	private int waittime;
	
	private int totaltime;

	@XmlElement(name="guests")
	public int getGuests()
	{
		return this.guests;
	}
	
	public void setGuests(int guests)
	{
		this.guests = guests;
	}
	
	@XmlElement(name="abandonments")
	public int getAbandonments()
	{
		return this.abandonments;
	}
	
	public void setAbandonments(int abandonments)
	{
		this.abandonments = abandonments;
	}

	@XmlElement(name="mergetime")
	public int getMergeTime()
	{
		return this.mergetime;
	}
	
	public void setMergeTime(int mergetime)
	{
		this.mergetime = mergetime;
	}
	
	@XmlElement(name="waittime")
	public int getWaitTime()
	{
		return this.waittime;
	}
	
	public void setWaitTime(int waittime)
	{
		this.waittime = waittime;
	}
	
	@XmlElement(name="totaltime")
	public int getTotalTime()
	{
		return this.totaltime;
	}
	
	public void setTotalTime(int totaltime)
	{
		this.totaltime = totaltime;
	}
}
