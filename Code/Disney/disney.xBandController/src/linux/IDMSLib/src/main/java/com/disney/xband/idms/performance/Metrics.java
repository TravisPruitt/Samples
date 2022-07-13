package com.disney.xband.idms.performance;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.disney.xband.common.lib.XmlDoubleAdapter;

@XmlRootElement(name="metrics")
public class Metrics
{
	public static Metrics INSTANCE = new Metrics();
	
	private Date startTime;
	private long totalCount;
	private Double totalTransactionTime;

	private Map<XBandPerformanceMetricType,PerformanceMetric> xBandMetrics;
	private Map<GuestPerformanceMetricType,PerformanceMetric> guestMetrics;
	//private Map<PartyPerformanceMetricType,PerformanceMetric> partyMetrics;
	private Map<CelebrationPerformanceMetricType,PerformanceMetric> celebrationMetrics;
	private Map<XviewPerformanceMetricType,PerformanceMetric> xviewMetrics;

	public Metrics()
	{
		this.xBandMetrics = new HashMap<XBandPerformanceMetricType,PerformanceMetric>();
		this.guestMetrics = new HashMap<GuestPerformanceMetricType,PerformanceMetric>();
		//this.partyMetrics = new HashMap<PartyPerformanceMetricType,PerformanceMetric>();
		this.celebrationMetrics = new HashMap<CelebrationPerformanceMetricType,PerformanceMetric>();
		this.xviewMetrics = new HashMap<XviewPerformanceMetricType,PerformanceMetric>();
		
		for(XBandPerformanceMetricType metricType : XBandPerformanceMetricType.values())
		{
			PerformanceMetric metric = new PerformanceMetric();
			metric.setName(metricType.toString());
			this.xBandMetrics.put(metricType, metric);
		}
		
		for(GuestPerformanceMetricType metricType : GuestPerformanceMetricType.values())
		{
			PerformanceMetric metric = new PerformanceMetric();
			metric.setName(metricType.toString());
			this.guestMetrics.put(metricType, metric);
		}
		
//		for(PartyPerformanceMetricType metricType : PartyPerformanceMetricType.values())
//		{
//			PerformanceMetric metric = new PerformanceMetric();
//			metric.setName(metricType.toString());
//			this.partyMetrics.put(metricType, metric);
//		}
		
		for(CelebrationPerformanceMetricType metricType : CelebrationPerformanceMetricType.values())
		{
			PerformanceMetric metric = new PerformanceMetric();
			metric.setName(metricType.toString());
			this.celebrationMetrics.put(metricType, metric);
		}
		
		for(XviewPerformanceMetricType metricType : XviewPerformanceMetricType.values())
		{
			PerformanceMetric metric = new PerformanceMetric();
			metric.setName(metricType.toString());
			this.xviewMetrics.put(metricType, metric);
		}
		
		this.clear();
	}
	
	@XmlAttribute()
	public Date getStartTime()
	{
		return this.startTime;
	}
	
	@XmlAttribute()
	public long getTotalTransactions()
	{
		return this.totalCount;
	}

	@XmlAttribute()
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getAverageTransactionTime()
	{
		if(this.totalCount> 0)
		{
			return this.totalTransactionTime / this.totalCount;
		}
		else
		{
			return 0.0;
		}
	}

	@XmlElementWrapper(name = "xband") 
	@XmlElements(@XmlElement(name="metric"))
	public Collection<PerformanceMetric> getXbandMetrics()
	{
		return this.xBandMetrics.values();
	}

	@XmlElementWrapper(name = "guest") 
	@XmlElements(@XmlElement(name="metric"))
	public Collection<PerformanceMetric> getGuestMetrics()
	{
		return this.guestMetrics.values();
	}

/*	@XmlElementWrapper(name = "party") 
	@XmlElements(@XmlElement(name="metric"))
	public Collection<PerformanceMetric> getPartyMetrics()
	{
		return this.partyMetrics.values();
	}*/
	
	@XmlElementWrapper(name = "celebration") 
	@XmlElements(@XmlElement(name="metric"))
	public Collection<PerformanceMetric> getCelebrationMetrics()
	{
		return this.celebrationMetrics.values();
	}
	
	@XmlElementWrapper(name = "xview") 
	@XmlElements(@XmlElement(name="metric"))
	public Collection<PerformanceMetric> getXviewMetrics()
	{
		return this.xviewMetrics.values();
	}
	
	public void clear()
	{
		for(XBandPerformanceMetricType metricType : XBandPerformanceMetricType.values())
		{
			PerformanceMetric metric = this.xBandMetrics.get(metricType);
			metric.clear();
		}
		
		for(GuestPerformanceMetricType metricType : GuestPerformanceMetricType.values())
		{
			PerformanceMetric metric = this.guestMetrics.get(metricType);
			metric.clear();
		}
		
//		for(PartyPerformanceMetricType metricType : PartyPerformanceMetricType.values())
//		{
//			PerformanceMetric metric = this.partyMetrics.get(metricType);
//			metric.clear();
//		}
		
		for(CelebrationPerformanceMetricType metricType : CelebrationPerformanceMetricType.values())
		{
			PerformanceMetric metric = this.celebrationMetrics.get(metricType);
			metric.clear();
		}
		
		for(XviewPerformanceMetricType metricType : XviewPerformanceMetricType.values())
		{
			PerformanceMetric metric = this.xviewMetrics.get(metricType);
			metric.clear();
		}
		
		this.startTime = new Date();
		this.startTime = new Date();
		this.totalCount = 0;
		this.totalTransactionTime = 0.0;
		
	}

	public void UpdateXBandMetric(Date startTime, XBandPerformanceMetricType metricType)
	{
		synchronized(this)
		{
			Date endTime = new Date();
			long msec = endTime.getTime() - startTime.getTime();
			this.xBandMetrics.get(metricType).processValue(msec);
			UpdateTotals(msec);
		}
	}

	public void UpdateGuestMetric(Date startTime, GuestPerformanceMetricType metricType)
	{
		synchronized(this)
		{
			Date endTime = new Date();
			long msec = endTime.getTime() - startTime.getTime();
			this.guestMetrics.get(metricType).processValue(msec);
			UpdateTotals(msec);
		}
	}

//	public void UpdatePartyMetric(Date startTime, PartyPerformanceMetricType metricType)
//	{
//		synchronized(this)
//		{
//			Date endTime = new Date();
//			long msec = endTime.getTime() - startTime.getTime();
//			this.partyMetrics.get(metricType).processValue(msec);
//			UpdateTotals(msec);
//		}
//	}

	public void UpdateCelebrationMetric(Date startTime, CelebrationPerformanceMetricType metricType)
	{
		synchronized(this)
		{
			Date endTime = new Date();
			long msec = endTime.getTime() - startTime.getTime();
			this.celebrationMetrics.get(metricType).processValue(msec);
			UpdateTotals(msec);
		}
	}
	
	public void UpdateXviewMetric(Date startTime, XviewPerformanceMetricType metricType)
	{
		synchronized(this)
		{
			Date endTime = new Date();
			long msec = endTime.getTime() - startTime.getTime();
			this.xviewMetrics.get(metricType).processValue(msec);
			UpdateTotals(msec);
		}
	}
	
	private void UpdateTotals(double msec)
	{
		this.totalCount ++;
		this.totalTransactionTime += msec;
	}
}
