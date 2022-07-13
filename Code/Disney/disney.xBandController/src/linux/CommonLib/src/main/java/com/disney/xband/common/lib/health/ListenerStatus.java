package com.disney.xband.common.lib.health;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.performance.PerfMetric;
import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.common.lib.performance.PerfMetricType;

@XmlRootElement(name="listener")
@XmlType(propOrder={"bootTime","status","statusMessage","version",
					"xiDatabaseVersion","gffDatabaseVersion","totalMessagesSinceStart","gffMessagesSinceStart",
					"gxpMessagesSinceStart","xbmsMessagesSinceStart","xbrcMessagesSinceStart", "pxcMessagesSinceStart",
                    "cacheSize", "cacheCapacity",
					"perfMetricsPeriod","perfDatabaseMsec","perfGxpMessageMsec","perfIDMSUpdateMsec",
					"perfIDMSQueryMsec","perfMessageMsec",
					"perfGxpCallbackMsec","perfXbandCallbackMsec","perfXbandRequestCallbackMsec","perfGuestProfileCallbackMsec", "perfPxcCallbackMsec"})
public class ListenerStatus implements Status
{
    private String version;
	private Date bootTime;
	private StatusType statusType;
	private String statusMessage;			// explanatory text for "Red" or "Yellow"
	private String xiDatabaseVersion;
	private String gffDatabaseVersion;

	private long xbrcMessagesSinceStart;
	private long gxpMessagesSinceStart;
	private long xbmsMessagesSinceStart;
	private long gffMessagesSinceStart;
    private long PxcMessagesSinceStart;
	private long cacheSize;
	private long cacheCapacity;
	
	private PerfMetric perfIDMSQueryMsec;
	private PerfMetric perfIDMSUpdateMsec;
	private PerfMetric perfDatabaseMsec;
	private PerfMetric perfMessageMsec;
	private PerfMetric perfGxpCallbackMsec;
	private PerfMetric perfGxpMessageMsec;
	private PerfMetric perfXbandRequestCallbackMsec;
	private PerfMetric perfXbandCallbackMsec;
	private PerfMetric perfGuestProfileCallbackMsec;
    private PerfMetric perfPxcCallbackMsec;
	private Date dtStartPerf;
	private int csecPerfMetricsPeriod;
	
	public ListenerStatus()
	{
		initPerfMetrics();
		clearPerfValues();
		
		this.version = this.getClass().getPackage().getImplementationTitle();
		if (this.version == null || this.version.isEmpty())
		{
			this.version = "Development";
		}
		
		this.bootTime = new Date();
		
	}
	
	
	@XmlElement
	public Date getBootTime()
	{
		return this.bootTime;
	}

	@XmlElement
	public int getPerfMetricsPeriod() 
	{
		return this.csecPerfMetricsPeriod;
	}

	@XmlElement
	public StatusType getStatus()
	{
		return statusType;
	}
	
	public void setStatus(StatusType statusType)
	{
		this.statusType = statusType;
	}

	@XmlElement
	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	@XmlElement
	public String getXiDatabaseVersion()
	{
		return xiDatabaseVersion;
	}

	public void setXiDatabaseVersion(String xiDatabaseVersion)
	{
		this.xiDatabaseVersion = xiDatabaseVersion;
	}

	@XmlElement
	public String getGffDatabaseVersion()
	{
		return gffDatabaseVersion;
	}

	public void setGffDatabaseVersion(String gffDatabaseVersion)
	{
		this.gffDatabaseVersion = gffDatabaseVersion;
	}

    @XmlElement
    public long getPxcMessagesSinceStart() {
        return PxcMessagesSinceStart;
    }

    public void setPxcMessagesSinceStart(long pxcMessagesSinceStart) {
        PxcMessagesSinceStart = pxcMessagesSinceStart;
    }

    public void incrementPxcMessagesSinceStart()
    {
        synchronized(this)
        {
            this.PxcMessagesSinceStart++;
        }
    }

	@XmlElement
	public long getXbrcMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		return this.xbrcMessagesSinceStart;
    	}
	}

	public void incrementXbrcMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		this.xbrcMessagesSinceStart++;
    	}
	}

	@XmlElement
	public long getGxpMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		return this.gxpMessagesSinceStart;
    	}
	}

	public void incrementGxpMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		this.gxpMessagesSinceStart++;
    	}
	}

	@XmlElement
	public long getXbmsMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		return this.xbmsMessagesSinceStart;
    	}
	}

	public void incrementXbmsMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		this.xbmsMessagesSinceStart++;
    	}
	}


	@XmlElement
	public long getGffMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		return this.gffMessagesSinceStart;
    	}
	}

	public void incrementGffMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		this.gffMessagesSinceStart++;
    	}
	}

	@XmlElement
	public long getTotalMessagesSinceStart()
	{
    	synchronized(this)
    	{
    		return this.xbrcMessagesSinceStart + this.gxpMessagesSinceStart +
    				this.xbmsMessagesSinceStart + this.gffMessagesSinceStart;
    	}
	}

	@XmlElement
	public long getCacheSize()
	{
		return this.cacheSize;
	}

	public void setCacheSize(long cacheSize)
	{
		this.cacheSize = cacheSize;
	}

	@XmlElement
	public long getCacheCapacity()
	{
		return this.cacheCapacity;
	}

	public void setCacheCapacity(long cacheCapcity)
	{
		this.cacheCapacity = cacheCapcity;
	}

	@XmlElement
	public PerfMetric getPerfIDMSQueryMsec()
	{
    	synchronized(this)
    	{
    		return this.perfIDMSQueryMsec;
    	}
	}
	
	@XmlElement
	public PerfMetric getPerfIDMSUpdateMsec()
	{
    	synchronized(this)
    	{
    		return this.perfIDMSUpdateMsec;
    	}
	}
	
	@XmlElement
	public PerfMetric getPerfDatabaseMsec()
	{
    	synchronized(this)
    	{
    		return this.perfDatabaseMsec;
    	}
	}

	@XmlElement
	public PerfMetric getPerfMessageMsec()
	{
    	synchronized(this)
    	{
    		return this.perfMessageMsec;
    	}
	}

	@XmlElement
	public PerfMetric getPerfGxpCallbackMsec()
	{
    	synchronized(this)
    	{
    		return this.perfGxpCallbackMsec;
    	}
	}

	@XmlElement
	public PerfMetric getPerfGxpMessageMsec()
	{
    	synchronized(this)
    	{
    		return this.perfGxpMessageMsec;
    	}
	}

	@XmlElement
	public PerfMetric getPerfXbandRequestCallbackMsec()
	{
    	synchronized(this)
    	{
    		return this.perfXbandRequestCallbackMsec;
    	}
	}

	@XmlElement
	public PerfMetric getPerfXbandCallbackMsec()
	{
    	synchronized(this)
    	{
    		return this.perfXbandCallbackMsec;
    	}
	}

    @XmlElement
    public PerfMetric getPerfPxcCallbackMsec()
    {
        synchronized(this)
        {
            return this.perfPxcCallbackMsec;
        }
    }

	@XmlElement
	public PerfMetric getPerfGuestProfileCallbackMsec()
	{
    	synchronized(this)
    	{
    		return this.perfGuestProfileCallbackMsec;
    	}
	}

	public String getStartPerfTime() 
	{
    	synchronized(this)
    	{
    		return DateUtils.format(dtStartPerf.getTime(),"yyyy-MM-dd'T'HH:mm:ss.SSS");
    	}
	}

	@XmlTransient
	public Date getStartPerfDate() 
	{
		return dtStartPerf;
	}

	private void initPerfMetrics()
	{
		this.perfIDMSQueryMsec = new PerfMetric(new PerfMetricMetadata(
				"perfIDMSQueryMsec", 
				PerfMetricType.milliseconds, 
				"perfIDMSQueryMsec", 
				"", 
				"1.0"
			));

		this.perfIDMSUpdateMsec = new PerfMetric(new PerfMetricMetadata(
				"perfIDMSUpdateMsec", 
				PerfMetricType.milliseconds, 
				"perfIDMSUpdateMsec", 
				"", 
				"1.0"
			));

		this.perfDatabaseMsec = new PerfMetric(new PerfMetricMetadata(
				"perfDatabaseMsec", 
				PerfMetricType.milliseconds, 
				"perfDatabaseMsec", 
				"", 
				"1.0"
			));
		
		this.perfMessageMsec = new PerfMetric(new PerfMetricMetadata(
				"perfMessageMsec", 
				PerfMetricType.milliseconds, 
				"perfMessageMsec", 
				"", 
				"1.0"
			));

		this.perfGxpCallbackMsec = new PerfMetric(new PerfMetricMetadata(
				"perfGxpCallbackMsec", 
				PerfMetricType.milliseconds, 
				"perfGxpCallbackMsec", 
				"", 
				"1.0"
			));

		this.perfGxpMessageMsec = new PerfMetric(new PerfMetricMetadata(
				"perfGxpMessageMsec", 
				PerfMetricType.milliseconds, 
				"perfGxpMessageMsec", 
				"", 
				"1.0"
			));
		
		this.perfXbandRequestCallbackMsec = new PerfMetric(new PerfMetricMetadata(
				"perfXbandRequestCallbackMsec", 
				PerfMetricType.milliseconds, 
				"perfXbandRequestCallbackMsec", 
				"", 
				"1.0"
			));
		
		this.perfXbandCallbackMsec = new PerfMetric(new PerfMetricMetadata(
				"perfXbandCallbackMsec", 
				PerfMetricType.milliseconds, 
				"perfXbandCallbackMsec", 
				"", 
				"1.0"
			));
		
        this.perfPxcCallbackMsec = new PerfMetric(new PerfMetricMetadata(
                "perfPxcCallbackMsec",
                PerfMetricType.milliseconds,
                "perfPxcCallbackMsec",
                "",
                "1.0"
            ));

		this.perfGuestProfileCallbackMsec = new PerfMetric(new PerfMetricMetadata(
				"perfGuestProfileCallbackMsec", 
				PerfMetricType.milliseconds, 
				"perfGuestProfileCallbackMsec", 
				"", 
				"1.0"
			));
	}

	public void clearPerfValues()
	{
		this.perfIDMSQueryMsec.clear();
		this.perfIDMSUpdateMsec.clear();
		this.perfDatabaseMsec.clear();
		this.perfMessageMsec.clear();
		this.perfGxpCallbackMsec.clear();
		this.perfGxpMessageMsec.clear();
		this.perfXbandRequestCallbackMsec.clear();
		this.perfXbandCallbackMsec.clear();
        this.perfPxcCallbackMsec.clear();
		this.perfGuestProfileCallbackMsec.clear();
		this.dtStartPerf = new Date();
	}

	public void setPerfMetricsPeriod(int csecPerfMetricsPeriod)
	{
		this.csecPerfMetricsPeriod = csecPerfMetricsPeriod;
	}

	/*
	 * All the methods below are here for jaxb's benefit and they are meant to be private.
	 */
	
	@SuppressWarnings("unused")
	private void setXbrcMessagesSinceStart(long xbrcMessagesSinceStart)
	{
    	synchronized(this)
    	{
    		this.xbrcMessagesSinceStart = xbrcMessagesSinceStart;
    	}
	}

	@SuppressWarnings("unused")
	private void setGxpMessagesSinceStart(long gxpMessagesSinceStart)
	{
    	synchronized(this)
    	{
    		this.gxpMessagesSinceStart = gxpMessagesSinceStart;
    	}
	}

	@SuppressWarnings("unused")
	private void setXbmsMessagesSinceStart(long xbmsMessagesSinceStart)
	{
    	synchronized(this)
    	{
    		this.xbmsMessagesSinceStart = xbmsMessagesSinceStart;
    	}
	}

	@SuppressWarnings("unused")
	private void setGffMessagesSinceStart(long gffMessagesSinceStart)
	{
    	synchronized(this)
    	{
    		this.gffMessagesSinceStart = gffMessagesSinceStart;
    	}
	}

	@SuppressWarnings("unused")
	private void setTotalMessagesSinceStart(long totalMessagesSinceStart)
	{
		//Do nothing the get is a calculation.
	}


	@SuppressWarnings("unused")
	private void setPerfIDMSQueryMsec(PerfMetric perfIDMSQueryMsec)
	{
		this.perfIDMSQueryMsec = perfIDMSQueryMsec;
		this.perfIDMSQueryMsec.setName("perfIDMSQueryMsec");
 	}
	
	@SuppressWarnings("unused")
	private void setPerfIDMSUpdateMsec(PerfMetric perfPerfIDMSUpdateMsec)
	{
    	this.perfIDMSUpdateMsec =  perfPerfIDMSUpdateMsec;
    	this.perfIDMSUpdateMsec.setName("perfPerfIDMSUpdateMsec");
	}
	
	@SuppressWarnings("unused")
	private void setPerfDatabaseMsec(PerfMetric perfPerfDatabaseMsec)
	{
    	this.perfDatabaseMsec = perfPerfDatabaseMsec;
    	this.perfDatabaseMsec.setName("perfPerfDatabaseMsec");
    	
	}

	@SuppressWarnings("unused")
	private void setPerfMessageMsec(PerfMetric perfMessageMsec)
	{
    	this.perfMessageMsec = perfMessageMsec;
    	this.perfMessageMsec.setName("perfPerfMessageMsec");
    	
	}

	@SuppressWarnings("unused")
	private void setPerfGxpCallbackMsec(PerfMetric perfGxpCallbackMsec)
	{
    	this.perfGxpCallbackMsec = perfGxpCallbackMsec;
    	this.perfGxpCallbackMsec.setName("perfGxpCallbackMsec");
	}

	@SuppressWarnings("unused")
	private void setPerfGxpMessageMsec(PerfMetric perfGxpMessageMsec)
	{
    	this.perfGxpMessageMsec = perfGxpMessageMsec;
    	this.perfGxpMessageMsec.setName("perfGxpMessageMsec");
    	
	}

	@SuppressWarnings("unused")
	private void setPerfXbandRequestCallbackMsec(PerfMetric perfXbandRequestCallbackMsec)
	{
    	this.perfXbandRequestCallbackMsec = perfXbandRequestCallbackMsec;
    	this.perfXbandRequestCallbackMsec.setName("perfXbandRequestCallbackMsec");
	}

	@SuppressWarnings("unused")
	private void setPerfXbandCallbackMsec(PerfMetric perfXbandCallbackMsec)
	{
		this.perfXbandCallbackMsec = perfXbandCallbackMsec;
		this.perfXbandCallbackMsec.setName("perfXbandCallbackMsec");
	}

    @SuppressWarnings("unused")
    private void setPerfPxcCallbackMsec(PerfMetric perfPxcCallbackMsec)
    {
        this.perfPxcCallbackMsec = perfPxcCallbackMsec;
        this.perfPxcCallbackMsec.setName("perfPxcCallbackMsec");
    }

	@SuppressWarnings("unused")
	private void setPerfGuestProfileCallbackMsec(PerfMetric perfGuestProfileCallbackMsec)
	{
		this.perfGuestProfileCallbackMsec = perfGuestProfileCallbackMsec;
		this.perfGuestProfileCallbackMsec.setName("perfGuestProfileCallbackMsec");
	}

	@SuppressWarnings("unused")
	private void setVersion(String version)
	{
		this.version = version;
	}

	@XmlElement
	public String getVersion()
	{
	    return version;
	}
        
	@SuppressWarnings("unused")
	private void setBootTime(String sDateBooted)
	{
		this.bootTime = DateUtils.parseDate(sDateBooted);
	}
}
