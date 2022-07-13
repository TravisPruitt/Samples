package com.disney.xband.idms.performance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.disney.xband.common.lib.XmlDoubleAdapter;

public class PerformanceMetric 
{
	private String name;

	// currently compiled data
	private double dblTotal;
	private double dblMax;
	private double dblMin;
	private long lCount;
		
	
	public PerformanceMetric()
	{
		clear();
	}
	
	@XmlAttribute()
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void processValue(double dblValue)
	{
		synchronized(this)
		{
			dblTotal += dblValue;
			if (dblValue > dblMax)
				dblMax = dblValue;
			if (dblValue < dblMin)
				dblMin = dblValue;
			lCount++;
		}
	}
	
	@XmlAttribute
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMean()
	{
		synchronized(this)
		{
			if (lCount>0)
				return dblTotal / lCount;
			else
				return 0.0;
		}
	}
	
	@XmlAttribute
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMax()
	{
		synchronized(this)
		{
			if (lCount>0)
				return dblMax;
			else
				return 0.0;
		}
	}
	
	@XmlAttribute
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMin()
	{
		synchronized(this)
		{
			if (lCount>0)
				return dblMin;
			else
				return 0.0;
		}
	}
	
	@XmlAttribute
	public long getCount()
	{
		synchronized(this)
		{
			return lCount;
		}
	}

	/*
	 *  setters - these are provided to facilitate deserialization of metrics data by the xBRMS. 
	 *  The presumption is that the xBRMS never calls processValue() or 
	 */
	public void setMean(Double d)
	{
		synchronized(this)
		{
			dblTotal = d;
			lCount = 1;
		}
	}
	
	public void setMin(Double d)
	{
		synchronized(this)
		{
			dblMin = d;
		}
	}
	
	public void setMax(Double d)
	{
		synchronized(this)
		{
			dblMax = d;
		}
	}
	
	public void clear()
	{
		// set sentinel values
		lCount = 0;
		dblTotal = 0.0;
		dblMax = Double.MIN_VALUE;
		dblMin = Double.MAX_VALUE;
	}
}
