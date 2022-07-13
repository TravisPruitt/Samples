package com.disney.xband.xbrc.lib.entity;

public class GuestInfo
{
	private String sXID;
	private double x, y;
	private boolean bHasxPass;
	
	public GuestInfo(String sXID, double x, double y, boolean bHasxPass)
	{
		this.sXID = sXID;
		this.x = x;
		this.y = y;
		this.bHasxPass = bHasxPass;
	}

	public void setsXID(String sXID)
	{
		this.sXID = sXID;
	}
	
	public String getsXID()
	{
		return sXID;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public boolean getHasxPass()
	{
		return bHasxPass;
	}
	
	public void setHasxPass(boolean bHasxPass)
	{
		this.bHasxPass = bHasxPass;
	}
}
