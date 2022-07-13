package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;

import com.disney.xband.xbrc.lib.model.MessageArgs;

public class MetricsMessageArgs extends MessageArgs
{
	private Date dtStart;
	private Date dtEnd;
	
	// xPass data
	private int cGuestsXP;
	private int cAbandonmentsXP;
	private int csecWaitTimeXP;
	private int csecMergeTimeXP;
	private int csecTotalTimeXP;
	
	// standby data
	private int cGuestsSB;
	private int cAbandonmentsSB;
	private int csecWaitTimeSB;
	private int csecTotalTimeSB;
	
	public MetricsMessageArgs(	Date dtStart, 
								Date dtEnd, 
								int cGuestsXP,
								int cAbandonmentsXP, 
								int csecWaitTimeXP, 
								int csecMergeTimeXP,
								int csecTotalTimeXP,
								int cGuestsSB,
								int cAbandonmentsSB, 
								int csecWaitTimeSB, 
								int csecTotalTimeSB
								)
	{
		super(null, false, "", "", "", "", "");
		this.dtStart = dtStart;
		this.dtEnd = dtEnd;
		this.cGuestsXP = cGuestsXP;
		this.cAbandonmentsXP = cAbandonmentsXP;
		this.csecWaitTimeXP = csecWaitTimeXP;
		this.csecMergeTimeXP = csecMergeTimeXP;
		this.csecTotalTimeXP = csecTotalTimeXP;
		this.cGuestsSB = cGuestsSB;
		this.cAbandonmentsSB = cAbandonmentsSB;
		this.csecWaitTimeSB = csecWaitTimeSB;
		this.csecTotalTimeSB = csecTotalTimeSB;
	}

	@Override
	public Date getTimestamp()
	{
		return getEndTime();
	}
	
	public Date getStartTime()
	{
		return dtStart;
	}

	public Date getEndTime()
	{
		return dtEnd;
	}

	public int getXPGuestCount()
	{
		return cGuestsXP;
	}

	public int getXPAbandonmentCount()
	{
		return cAbandonmentsXP;
	}

	public int getXPMeanWaitTime()
	{
		return csecWaitTimeXP;
	}

	public int getXPMeanMergeTime()
	{
		return csecMergeTimeXP;
	}

	public int getXPMeanTotalTime()
	{
		return csecTotalTimeXP;
	}

	public int getSBGuestCount()
	{
		return cGuestsSB;
	}

	public int getSBAbandonmentCount()
	{
		return cAbandonmentsSB;
	}

	public int getSBMeanWaitTime()
	{
		return csecWaitTimeSB;
	}

	public int getSBMeanTotalTime()
	{
		return csecTotalTimeSB;
	}
	
}
