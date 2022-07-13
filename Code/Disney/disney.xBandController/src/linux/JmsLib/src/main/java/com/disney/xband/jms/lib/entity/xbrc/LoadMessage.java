package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.*;

public class LoadMessage extends EventMessage {
	
	private static final long serialVersionUID = 1L;
		
	private int waittime;

	private int mergetime;

	private String carid;

	@XmlElement(name="waittime")
	public int getWaitTime()
	{
		return this.waittime;
	}
	
	public void setWaitTime(int waittime)
	{
		this.waittime = waittime;
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

	@XmlElement(name="carid")
	public String getCarId()
	{
		return this.carid;
	}
	
	public void setCarId(String carid)
	{
		this.carid = carid;
	}
}
