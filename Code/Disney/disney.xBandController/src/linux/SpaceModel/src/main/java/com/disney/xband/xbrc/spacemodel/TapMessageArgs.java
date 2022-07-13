package com.disney.xband.xbrc.spacemodel;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.MessageArgs;

public class TapMessageArgs extends MessageArgs
{

	public TapMessageArgs(EventAggregate ev, boolean bxPass, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		super(ev, bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
	}

}
