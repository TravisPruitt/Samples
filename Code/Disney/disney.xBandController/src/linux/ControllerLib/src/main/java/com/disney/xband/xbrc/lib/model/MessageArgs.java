package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class MessageArgs
{
	protected EventAggregate ev;
	private boolean bxPass;
	private String guestID;
	private String pidDecimal;
	private String linkID;
	private String linkIDType;
	private String bandType;
	
	public MessageArgs(EventAggregate ev, boolean bxPass, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		this.ev = ev;
		this.bxPass = bxPass;
		this.guestID = GuestID;
		this.pidDecimal = PublicID;
		this.linkID = LinkID;
		this.linkIDType = LinkIDType;
		this.bandType = BandType;
	}
	
	public String getGuestID()
	{
		return this.guestID;
	}
	
	public String getPidDecimal()
	{
		return this.pidDecimal;
	}
	
	public String getLinkID()
	{
		return this.linkID;
	}	
	
	public String getLinkIDType()
	{
		return this.linkIDType;
	}
	
	public String getBandType()
	{
		if (this.bandType != null)
			return this.bandType;
		else
			return "";
	}
	
	public Date getTimestamp()
	{
		if (ev!=null)
			return ev.getTimestamp();
		else
			return new Date();
	}
	
	public String getBandID()
	{
		if (ev!=null)
			return ev.getID();
		else
			return "";
	}
	
	public ReaderInfo getReader()
	{
		return ev.getReaderInfo();
	}
	
	public String getReaderLocation()
	{
		if (getReader() == null)
			return "";
		if (getReader().getLocation() == null)
			return "";
		return getReader().getLocation().getName();
	}
	
	public int getReaderLocationId()
	{
		if (getReader() == null)
			return 0;
		if (getReader().getLocation() == null)
			return 0;
		return getReader().getLocation().getId();
	}
	
	public String getLocationId()
	{
		if (getReader() == null)
			return "";
		if (getReader().getLocation() == null)
			return "";
		return getReader().getLocation().getLocationId();
	}
	
	public boolean HasxPass()
	{
		return bxPass;
	}
	public boolean hasEvent()
	{
		return ev != null;
	}
	
	public String getRFID()
	{
		if (ev instanceof TapEventAggregate)
		{
			return ((TapEventAggregate)ev).getTapEvent().getID();
		}
		return null;
	}
}
