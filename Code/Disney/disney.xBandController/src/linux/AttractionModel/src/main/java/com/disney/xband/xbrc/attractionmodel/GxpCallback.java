package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;

public class GxpCallback
{
	public enum Operation
	{
		success,
		fail,
		override,
		nextGuest		// "reject"
	}
	
	public enum UnitType
	{
		Entrance,
		Merge,
		Combo;
	}
	
	private long lTime;
	private String sBandId;
	
	private Operation operation;
	private UnitType unitType;
	@SuppressWarnings("unused")
	private long lEntertainmentId;
	private long lLocationId;
	private Integer deviceId;
	private Date dtStart;
	private boolean synthesized = false;
	
	public GxpCallback()
	{
	}
	
	public long getTime()
	{
		return lTime;
	}
	
	public void setTime(long lTime)
	{
		this.lTime = lTime;
	}
	
	public String getBandId()
	{
		return sBandId;
	}
	
	public void setBandId(String sBandId)
	{
		this.sBandId = sBandId;
	}
	
	public Operation getOperation()
	{
		return operation;
	}
	
	public void setOperation(Operation operation)
	{
		this.operation = operation;
	}

	public UnitType getUnitType()
	{
		return unitType;
	}

	public void setUnitType(UnitType unitType)
	{
		this.unitType = unitType;
	}
	
	/*
	public long getEntertainmentId()
	{
		return lEntertainmentId;
	}

	public void setEntertainmentId(long lEntertainmentId)
	{
		this.lEntertainmentId = lEntertainmentId;
	}
	*/

	public long getLocationId()
	{
		return lLocationId;
	}

	public void setLocationId(long lLocationId)
	{
		this.lLocationId = lLocationId;
	}
	
	public Date getTimeStart()
	{
		return dtStart;
	}
	
	public void setTimeStart(Date dtStart)
	{
		this.dtStart = dtStart;
	}

	public Integer getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(Integer deviceId)
	{
		this.deviceId = deviceId;
	}

	public boolean isSynthesized() {
		return synthesized;
	}

	public void setSynthesized(boolean synthesized) {
		this.synthesized = synthesized;
	}
}
