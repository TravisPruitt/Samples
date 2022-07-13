package com.disney.xband.xbrc.attractionmodel;

public class Metrics
{
	private int cGuests;
	private int cAbandonments;
	private int csecMergeTime;	// only relevant for xPass guests
	private int csecWaitTime;
	private int csecTotalTime;
	
	public Metrics()
	{
		cGuests = 0;
		cAbandonments = 0;
		csecMergeTime = csecWaitTime = csecTotalTime = 0;
	}
	
	public int getGuestCount()
	{
		return cGuests;
	}
	
	public int getAbandonments()
	{
		return cAbandonments;
	}
	
	public int getMergeTime()
	{
		return csecMergeTime;
	}
	
	public int getWaitTime()
	{
		return csecWaitTime;
	}
	
	public int getTotalTime()
	{
		return csecTotalTime;
	}
	
	public void AddGuest(int csecMergeTimeNew, int csecWaitTimeNew, int csecTotalTimeNew)
	{
		// handle running means
		if (csecMergeTimeNew>0)
		{
			double dDiscounted = (double) this.csecMergeTime * (double) cGuests / (cGuests+1);
			double dBump = (double) csecMergeTimeNew / (cGuests+1);
			this.csecMergeTime = (int)(dDiscounted + dBump);
		}
		if (csecWaitTimeNew>0)
		{
			double dDiscounted = (double) this.csecWaitTime * (double) cGuests / (cGuests+1);
			double dBump = (double) csecWaitTimeNew / (cGuests+1);
			this.csecWaitTime = (int)(dDiscounted + dBump);
		}
		if (csecTotalTimeNew>0)
		{
			double dDiscounted = (double) this.csecTotalTime * (double) cGuests / (cGuests+1);
			double dBump = (double) csecTotalTimeNew / (cGuests+1);
			this.csecTotalTime = (int)(dDiscounted + dBump);
		}
		cGuests++;
	}
	
	public void AddAbandonment(int csecMergeTimeNew, int csecWaitTimeNew)
	{
		// handle running means
		if (csecMergeTimeNew>0)
		{
			double dDiscounted = (double) this.csecMergeTime * (double) cGuests / (cGuests+1);
			double dBump = (double) csecMergeTimeNew / (cGuests+1);
			this.csecMergeTime = (int)(dDiscounted + dBump);
		}
		if (csecWaitTimeNew>0)
		{
			double dDiscounted = (double) this.csecWaitTime * (double) cGuests / (cGuests+1);
			double dBump = (double) csecWaitTimeNew / (cGuests+1);
			this.csecWaitTime = (int)(dDiscounted + dBump);
		}
		cGuests++;
		cAbandonments++;
	}
	
}
