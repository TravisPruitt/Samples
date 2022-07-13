package com.disney.xband.xbrc.gxpcheck.entity;

import java.util.Date;

public class GxpMessage
{
	public enum State
	{
		unchecked,
		failed,
		succeed
	};
	
	private State state;
	private Date dtDate;
	private String sBandId;
	private String sGuestId;
	private String sLRID;
	private String sRFID;
	private String sGuestName;
	private String sEntertainmentId;
	private String sLocation;
	private String sUnitType;
	private String sSide;
	
	public State getState()
	{
		return state;
	}
	
	public void setState(State state)
	{
		this.state = state;
	}
	
	public Date getTime()
	{
		return dtDate;
	}
	
	public void setTime(Date dtDate)
	{
		this.dtDate = (Date) dtDate.clone(); 
	}
	
	public String getBandId()
	{
		return sBandId;
	}
	public void setBandId(String sBandId)
	{
		this.sBandId = sBandId;
	}
	public String getEntertainmentId()
	{
		return sEntertainmentId;
	}
	public void setEntertainmentId(String sEntertainmentId)
	{
		this.sEntertainmentId = sEntertainmentId;
	}
	public String getLocation()
	{
		return sLocation;
	}
	public void setLocation(String sLocation)
	{
		this.sLocation = sLocation;
	}
	public String getUnitType()
	{
		return sUnitType;
	}
	public void setUnitType(String sUnitType)
	{
		this.sUnitType = sUnitType;
	}
	public String getSide()
	{
		return sSide;
	}
	public void setSide(String sSide)
	{
		this.sSide = sSide;
	}

	public String getGuestId()
	{
		return sGuestId;
	}

	public void setGuestId(String sGuestId)
	{
		this.sGuestId = sGuestId;
	}

	public String getLRID()
	{
		return sLRID;
	}

	public void setLRID(String sLRID)
	{
		this.sLRID = sLRID;
	}

	public String getRFID()
	{
		return sRFID;
	}

	public void setRFID(String sRFID)
	{
		this.sRFID = sRFID;
	}

	public String getGuestName()
	{
		return sGuestName;
	}
	
	public void setGuestName(String sName)
	{
		this.sGuestName = sName;
	}
	
}
