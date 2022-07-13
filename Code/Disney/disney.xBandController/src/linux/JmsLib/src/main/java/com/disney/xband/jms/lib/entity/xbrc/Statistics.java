package com.disney.xband.jms.lib.entity.xbrc;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

public class Statistics implements Serializable
{
	private static final long serialVersionUID = 1L;

	@XmlElement(name="waittime")
	private int waittime;

	@XmlElement(name="mergetime")
	private int mergetime;

	@XmlElement(name="totaltime")
	private int totaltime;

	public int getWaitTime()
	{
		return this.waittime;
	}
	
	public void setWaitTime(int waittime)
	{
		this.waittime = waittime;
	}
	
	public int getMergeTime()
	{
		return this.mergetime;
	}
	
	public void setMergeTime(int mergetime)
	{
		this.mergetime = mergetime;
	}
	
	public int getTotalTime()
	{
		return this.totaltime;
	}
	
	public void setTotalTime(int totaltime)
	{
		this.totaltime = totaltime;
	}
	
}
