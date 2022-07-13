package com.disney.xband.common.lib.performance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.disney.xband.common.lib.jaxbadapters.XmlDoubleAdapter;

@XmlRootElement
public class PerfMetric
{
	// currently compiled data
	private double dblTotal;
	private double dblMax;
	private double dblMin;
	private long lCount;
	
	// previous data
	private double dblTotal_p;
	private double dblMax_p;
	private double dblMin_p;
	private long lCount_p;
	
	// metadata
	private PerfMetricMetadata metadata;
	
	/**
	 * This no-argument constructor is needed to satisfy JAXB's requirements.
	 * Fortunately JAXB circumvents the private identifier, so this constructor
	 * doesn't need to be exposed.
	 */
	@SuppressWarnings("unused")
	private PerfMetric(){
		metadata = new PerfMetricMetadata();
	}
	
	public PerfMetric(PerfMetricMetadata metadata) throws IllegalArgumentException
	{
		if (metadata == null)
			throw new IllegalArgumentException("Missing metadata.");
		
		this.metadata = metadata;
		
		// set sentinel values
		dblMax = dblMin = dblTotal = 0.0;
		lCount = 0;
		clear();
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
	
	/*
	 * copies current into previous values
	 */
	public void clear()
	{
		synchronized(this)
		{
			// copy
			lCount_p = lCount;
			dblTotal_p = dblTotal;
			dblMin_p = dblMin;
			dblMax_p = dblMax;
			
			// initialize for next sample
			lCount = 0;
			dblTotal = 0.0;
			dblMax = Double.MIN_VALUE;
			dblMin = Double.MAX_VALUE;
		}
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMean()
	{
		synchronized(this)
		{
			if (lCount_p>0)
				return dblTotal_p / lCount_p;
			else
				return 0.0;
		}
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMax()
	{
		synchronized(this)
		{
			if (lCount_p>0)
				return dblMax_p;
			else
				return 0.0;
		}
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getMin()
	{
		synchronized(this)
		{
			if (lCount_p>0)
				return dblMin_p;
			else
				return 0.0;
		}
	}
	
	@XmlTransient
	public long getCount()
	{
		synchronized(this)
		{
			return lCount;
		}
	}
	

	/*
	 *  setters - these are provided to facilitate deserialization of metrics data by the xBRMS. The presumption
	 *  is that the xBRMS never calls processValue() or 
	 */
	public void setMean(Double d)
	{
		synchronized(this)
		{
			dblTotal_p = d;
			lCount_p = 1;
		}
	}
	
	public void setMin(Double d)
	{
		synchronized(this)
		{
			dblMin_p = d;
		}
	}
	
	public void setMax(Double d)
	{
		synchronized(this)
		{
			dblMax_p = d;
		}
	}
	
	public void setCount(long l)
	{
		synchronized(this)
		{
			lCount = l;
		}
	}

	@XmlTransient
	public String getName()
	{
		return metadata.getName();
	}
	
	public void setName(String name)
	{
		metadata.setName(name);
	}

	@XmlTransient
	public String getDisplayName()
	{
		return metadata.getDisplayName();
	}
	
	public void setDisplayName(String displayName)
	{
		metadata.setDisplayName(displayName);
	}
	
	@XmlTransient
	public PerfMetricType getType()
	{
		return metadata.getType();
	}
	
	public void setType(PerfMetricType type)
	{
		metadata.setType(type);
	}
	
	@XmlTransient
	public String getDescription()
	{
		return metadata.getDescription();
	}
	
	public void setDescription(String description)
	{
		metadata.setDescription(description);
	}
	
	@XmlAttribute
	public String getVersion()
	{
		return metadata.getVersion();
	}
	
	public void setVersion(String version)
	{
		metadata.setVersion(version);
	}
	
	@XmlTransient
	public PerfMetricMetadata getMetadata()
	{
		return metadata;
	}
}
