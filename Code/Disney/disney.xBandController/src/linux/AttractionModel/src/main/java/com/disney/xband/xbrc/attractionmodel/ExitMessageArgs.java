package com.disney.xband.xbrc.attractionmodel;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.MessageArgs;

public class ExitMessageArgs extends MessageArgs
{
	private String sCarID;
	private int csecWaitTime;
	private int csecMergeTime;
	private int csecTotalTime;
	
	public ExitMessageArgs(EventAggregate ev, boolean bxPass, String sCarID, int csecWaitTime, int csecMergeTime, int csecTotalTime, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		super(ev, bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
		this.sCarID = sCarID;
		this.csecWaitTime = csecWaitTime;
		this.csecMergeTime = csecMergeTime;
		this.csecTotalTime = csecTotalTime;
	}
	
	public String getCarID()
	{
		return sCarID;
	}
	
	public int getWaitTime()
	{
		return csecWaitTime;
	}
	
	public int getMergeTime()
	{
		return csecMergeTime;
	}
	
	public int getTotalTime()
	{
		return csecTotalTime;
	}

}
