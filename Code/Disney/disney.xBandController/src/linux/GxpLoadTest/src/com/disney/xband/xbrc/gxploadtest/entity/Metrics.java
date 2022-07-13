package com.disney.xband.xbrc.gxploadtest.entity;

public class Metrics
{
	private int cCount = 0;				// total transaction count
	private long msecTotal = 0;			// total msec's waiting
	private long msecTotalError = 0;	// total msec's waiting (errors)
	private int cFailures = 0;			// failure count

	public void addSuccess(long msec)
	{
		synchronized(this)
		{
			cCount++;
			msecTotal += msec;
		}
	}
	
	public void addFailure(long msec)
	{
		synchronized(this)
		{
			cCount++;
			cFailures++;
			msecTotal += msec;
			msecTotalError += msec;
		}
	}
	
	public int getCount()
	{
		synchronized(this)
		{
			return cCount;
		}
	}
	
	public int getFailures()
	{
		synchronized (this)
		{
			return cFailures;
		}
	}
	
	public int getSuccesses()
	{
		synchronized (this)
		{
			return cCount - cFailures;
		}
	}
	
	public double getAverageTransactionTime()
	{
		synchronized (this)
		{
			if (cCount>0)
				return (double) msecTotal / cCount;
			else
				return 0.0;
		}
	}
	
	public double getAverageFailureTransactionTime()
	{
		synchronized (this)
		{
			if (cFailures>0)
				return (double) msecTotalError / cFailures;
			else
				return 0.0;
		}
	}
	
	public double getAverageSuccessTransactionTime()
	{
		synchronized (this)
		{
			if (getSuccesses()>0)
				return (double) (msecTotal - msecTotalError) / getSuccesses();
			else
				return 0.0;
		}
	}
}
