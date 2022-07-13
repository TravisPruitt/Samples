package com.disney.xbrc.parksimulator.entity;

public class Attraction
{
	private String sName;
	private int nBaseTime;
	private int nDataPoints;
	private int[] anStandbyGC;
	private int[] anxPassGC;
	private int[] anStandbyWait;
	private int[] anxPassWait;
	
	public Attraction(String sName, int nBaseTime, int nDataPoints)
	{
		this.sName = sName;
		this.nBaseTime = nBaseTime;
		this.nDataPoints = nDataPoints;
		this.anStandbyGC = new int[nDataPoints];
		this.anxPassGC = new int[nDataPoints];
		this.anStandbyWait = new int[nDataPoints];
		this.anxPassWait = new int[nDataPoints];
	}
	
	public void processStandbyGC(String[] asParts) throws Exception
	{
		// set the numeric fields
		for (int i=0; i<nDataPoints; i++)
			anStandbyGC[i] = Integer.parseInt(asParts[i+1]);
	}

	public void processxPassGC(String[] asParts) throws Exception
	{
		// set the numeric fields
		for (int i=0; i<nDataPoints; i++)
			anxPassGC[i] = Integer.parseInt(asParts[i+1]);
	}
	
	public void processStandbyWait(String[] asParts) throws Exception
	{
		// set the numeric fields
		for (int i=0; i<nDataPoints; i++)
			anStandbyWait[i] = Integer.parseInt(asParts[i+1]);
	}

	public void processxPassWait(String[] asParts) throws Exception
	{
		// set the numeric fields
		for (int i=0; i<nDataPoints; i++)
			anxPassWait[i] = Integer.parseInt(asParts[i+1]);
	}
	
	public String getName()
	{
		return sName;
	}
	
	public int getStandbyGC(int nHour) throws Exception
	{
		int iBucket = getBucketNumber(nHour);
		return anStandbyGC[iBucket];
	}

	public int getxPassGC(int nHour) throws Exception
	{
		int iBucket = getBucketNumber(nHour);
		return anxPassGC[iBucket];
	}
	
	public int getStandbyWait(int nHour) throws Exception
	{
		int iBucket = getBucketNumber(nHour);
		return anStandbyWait[iBucket];
	}

	public int getxPassWait(int nHour) throws Exception
	{
		int iBucket = getBucketNumber(nHour);
		return anxPassWait[iBucket];
	}

	private int getBucketNumber(int nHour) throws Exception
	{
		if (nHour<nBaseTime || nHour>24)
			throw new Exception("Invalid simulation time");
		
		// calculate bucket
		int iBucket = nHour-nBaseTime;
		return iBucket;
	}

}
