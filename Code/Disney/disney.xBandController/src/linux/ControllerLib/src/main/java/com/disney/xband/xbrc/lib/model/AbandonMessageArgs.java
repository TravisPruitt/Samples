package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.MessageArgs;

public class AbandonMessageArgs extends MessageArgs
{
	private Date dtLastXMit;
	
	public AbandonMessageArgs(EventAggregate ev, boolean bxPass, Date dtLastXMit, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		super(ev, bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
		this.dtLastXMit = dtLastXMit;
	}
	
	public Date getLastXMit()
	{
		return dtLastXMit;
	}

}
