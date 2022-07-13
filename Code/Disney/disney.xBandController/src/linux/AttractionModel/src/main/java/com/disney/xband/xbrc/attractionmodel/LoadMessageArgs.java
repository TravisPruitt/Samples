package com.disney.xband.xbrc.attractionmodel;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.MessageArgs;

public class LoadMessageArgs extends MessageArgs
{
	private String sCarID;
	
	public LoadMessageArgs(EventAggregate ev, boolean bxPass, String sCarID, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		super(ev, bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
		this.sCarID = sCarID;
	}
	
	public String getCarID()
	{
		return sCarID;
	}
}
