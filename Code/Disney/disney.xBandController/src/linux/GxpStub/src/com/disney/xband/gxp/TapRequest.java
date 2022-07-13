package com.disney.xband.gxp;

import java.util.Date;

public class TapRequest
{
	private long timestamp;
	private String xBandId;
	private long location;
	private String unitType;
	private String side;
	private long entertainmentId;
	private String callback;
	
	public long getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	public String getBandId()
	{
		return xBandId;
	}
	public void setBandId(String xBandId)
	{
		this.xBandId = xBandId;
	}
	
	public long getLocation()
	{
		return location;
	}
	public void setLocation(long location)
	{
		this.location = location;
	}
	
	public String getUnitType()
	{
		return unitType;
	}
	public void setUnitType(String unitType)
	{
		this.unitType = unitType;
	}
	public String getSide()
	{
		return side;
	}
	public void setSide(String side)
	{
		this.side = side;
	}
	public long getEntertainmentId()
	{
		return entertainmentId;
	}
	public void setEntertainmentId(long entertainmentId)
	{
		this.entertainmentId = entertainmentId;
	}
	public String getCallback()
	{
		return callback;
	}
	public void setCallback(String callback)
	{
		this.callback = callback;
	}
	
	
}
