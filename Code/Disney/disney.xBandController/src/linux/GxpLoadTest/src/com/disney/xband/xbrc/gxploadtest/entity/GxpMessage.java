package com.disney.xband.xbrc.gxploadtest.entity;

import java.util.Date;

import com.disney.xband.xbrc.gxploadtest.CLOptions;

public class GxpMessage
{
	private Date dtDate;
	private String sBandId;
	private String sEntertainmentId;
	private String sLocation;
	private String sUnitType;
	private String sSide;
	
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
	
	public static GxpMessage processLine(String sLine, CLOptions clo)
	{
		// split the string
		String[] aParts = sLine.split(",");
		
		GxpMessage req = new GxpMessage();
		
		// set the defaults
		req.setEntertainmentId(clo.getFacilityId());
		req.setLocation(clo.getLocationId());
		req.setUnitType(clo.getReaderType());
		req.setSide(clo.getSide());
		
		// override with provided valuews
		for (int i=0; i<aParts.length; i++)
		{
			switch(i)
			{
				case 0:
				{
					req.setBandId(aParts[i]);
					break;
				}
				
				case 1:
				{
					req.setEntertainmentId(aParts[i]);
					break;
				}
				
				case 2:
				{
					req.setLocation(aParts[i]);
					break;
				}
				
				case 3:
				{
					req.setUnitType(aParts[i]);
					break;
				}
				
				case 4:
				{
					req.setSide(aParts[i]);
					break;
				}
				
				default:
				{
					System.err.println("Unexpected field in data file: " + aParts[i]);
					break;
				}
			}
		}
		return req;
	}

}
