package com.disney.xband.xbrc.lib.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.common.lib.health.Status;
import com.disney.xband.common.lib.health.StatusType;

import com.disney.xband.common.lib.performance.*;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;

@XmlRootElement(name="venue")
public class XbrcStatus implements Status
{
	/*
	 * Attributes
	 */
	private String name;
	private String time;
	private String facilityId;

	/*
	 * Elements
	 */
	private int readerLocationsCount;
	private long messageCount;
	private long httpMessageCount;
	private long lastMessageSeq;
	private long lastMessageToJMS;
	private long lastMessageToUpdateStream;
	
	private String version;					// software version
	private StatusType status;				// "Red", "Green" or "Yellow"
	private String statusMessage;			// explanatory text for "Red" or "Yellow"
	private String haStatus;				// unknown, master or slave
	private String databaseVersion;
	private Date dtConfigurationChanged;	// date/time configuration last changed
	private String vip;
	private Integer vport;
	private boolean haenabled;
	private String parkid;
	
	private String model;
	private String sJMSBroker;
	private String sUpdateStreamURL;
	private boolean readerTestMode;
	private Date dtStartPerf;
	private int csecPerfMetricsPeriod;
	
	private Date dtBooted;
	private int cTotalGuestsSinceStart;
	private int cTotalxPassGuestsSinceStart;

	private PerfMetric perfEvents;
	private PerfMetric perfEventAgeMsec;
	private PerfMetric perfIDMSQueryMsec;
	private PerfMetric perfEKGWriteMsec;
	private PerfMetric perfSingulationMsec;
	private PerfMetric perfPreModelingMsec;
	private PerfMetric perfModelingMsec;
	private PerfMetric perfPostModelingMsec;
	private PerfMetric perfExternalMsec;
	private PerfMetric perfWriteToReaderMsec;
	private PerfMetric perfSaveGSTMsec;
	private PerfMetric perfUpstreamMsec;
	private PerfMetric perfMainLoopUtilization;
	private PerfMetric perfModel1;
	private PerfMetric perfModel2;
	private PerfMetric perfModel3;

    private boolean isAuditEnabled;
	
	public XbrcStatus()
	{
		initPerfMetrics();
		clearPerfValues();
		dtConfigurationChanged = new Date();
		haStatus = HAStatusEnum.unknown.toString();
	}

	@XmlAttribute
	public String getName() 
	{
		return name;
	}
	
	@XmlAttribute
	public String getFacilityId()
	{
		return facilityId;
	}

	public void setFacilityId(String facilityId)
	{
		this.facilityId = facilityId;
	}
	
	@XmlAttribute
	public String getTime() 
	{
		return time;
	}
	
	@XmlElement
	public String getStartPerfTime() 
	{
		return formatTime(dtStartPerf.getTime());
	}
	
	public void setStartPerfTime(String sDate)
	{
		dtStartPerf = parseDate(sDate);
	}
	
	public int getPerfMetricsPeriod() 
	{
		return csecPerfMetricsPeriod;
	}
	
	@XmlTransient
	public Date getStartPerfDate() 
	{
		return dtStartPerf;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setTime(String time)
	{
		this.time = time;
	}
	
	public void setPerfMetricsPeriod(int csecPerfMetricsPeriod)
	{
		this.csecPerfMetricsPeriod = csecPerfMetricsPeriod;
	}

	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public int getReaderLocationsCount()
	{
		return readerLocationsCount;
	}
	public void setReaderLocationsCount(int readerLocationsCount)
	{
		this.readerLocationsCount = readerLocationsCount;
	}
	public long getMessageCount()
	{
		return messageCount;
	}
	public void setMessageCount(long messageCount)
	{
		this.messageCount = messageCount;
	}
	public long getHttpMessageCount() {
		return httpMessageCount;
	}

	public void setHttpMessageCount(long httpMessageCount) {
		this.httpMessageCount = httpMessageCount;
	}
	public long getLastMessageSeq()
	{
		return lastMessageSeq;
	}
	public void setLastMessageSeq(long lastMessageSeq)
	{
		this.lastMessageSeq = lastMessageSeq;
	}
	public long getLastMessageToJMS()
	{
		return lastMessageToJMS;
	}
	public void setLastMessageToJMS(long lastMessageToJMS)
	{
		this.lastMessageToJMS = lastMessageToJMS;
	}
	public long getLastMessageToUpdateStream()
	{
		return lastMessageToUpdateStream;
	}
	public void setLastMessageToUpdateStream(long lastMessageToUpdateStream)
	{
		this.lastMessageToUpdateStream = lastMessageToUpdateStream;
	}
	public StatusType getStatus()
	{
		return status;
	}
	public void setStatus(StatusType status)
	{
		this.status = status;
	}
	public String getStatusMessage()
	{
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	public String getHaStatus()
	{
		return haStatus;
	}
	public void setHaStatus(String haStatus)
	{
		this.haStatus = haStatus;
	}

	public String getDatabaseVersion()
	{
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion)
	{
		this.databaseVersion = databaseVersion;
	}
	
	@XmlElement
	public String getConfigurationChangedTime()
	{
		return formatTime(dtConfigurationChanged.getTime());
	}
	
	public void setConfigurationChangedTime(String sDate)
	{
		dtConfigurationChanged = parseDate(sDate);
	}
	
	@XmlTransient
	public Date getConfigurationChangedDate()
	{
		return dtConfigurationChanged;
	}

	public void setConfigurationChangedDate(Date dtConfigurationChanged)
	{
		this.dtConfigurationChanged = dtConfigurationChanged;
	}

	public String getModel()
	{
		return model;
	}
	public void setModel(String model)
	{
		this.model = model;
	}
	public String getJMSBroker()
	{
		return sJMSBroker;
	}
	public void setJMSBroker(String sJMSBroker)
	{
		this.sJMSBroker = sJMSBroker;
	}
	public String getUpdateStreamURL()
	{
		return sUpdateStreamURL;
	}
	public void setUpdateStreamURL(String sUpdateStreamURL)
	{
		this.sUpdateStreamURL = sUpdateStreamURL;
	}
	public boolean isReaderTestMode()
	{
		return readerTestMode;
	}
	public void setReaderTestMode(boolean readerTestMode)
	{
		this.readerTestMode = readerTestMode;
	}
	@XmlElement
	public PerfMetric getPerfEvents()
	{
		return perfEvents;
	}
	@XmlElement
	public PerfMetric getPerfEventAgeMsec()
	{
		return perfEventAgeMsec;
	}
	@XmlElement
	public PerfMetric getPerfIDMSQueryMsec()
	{
		return perfIDMSQueryMsec;
	}
	@XmlElement
	public PerfMetric getPerfEKGWriteMsec()
	{
		return perfEKGWriteMsec;
	}
	@XmlElement
	public PerfMetric getPerfSingulationMsec()
	{
		return perfSingulationMsec;
	}
	@XmlElement
	public PerfMetric getPerfModelingMsec()
	{
		return perfModelingMsec;
	}
	@XmlElement
	public PerfMetric getPerfExternalMsec()
	{
		return perfExternalMsec;
	}
	@XmlElement
	public PerfMetric getPerfSaveGSTMsec()
	{
		return perfSaveGSTMsec;
	}
	@XmlElement
	public PerfMetric getPerfUpstreamMsec()
	{
		return perfUpstreamMsec;
	}
	@XmlElement
	public PerfMetric getPerfPreModelingMsec()
	{
		return perfPreModelingMsec;
	}
	@XmlElement
	public PerfMetric getPerfPostModelingMsec()
	{
		return perfPostModelingMsec;
	}
	@XmlElement
	public PerfMetric getPerfWriteToReaderMsec()
	{
		return perfWriteToReaderMsec;
	}
	@XmlElement
	public PerfMetric getPerfMainLoopUtilization()
	{
		return perfMainLoopUtilization;
	}
	@XmlElement
	public PerfMetric getPerfModel1()
	{
		return perfModel1;
	}
	@XmlElement
	public PerfMetric getPerfModel2()
	{
		return perfModel2;
	}
	@XmlElement
	public PerfMetric getPerfModel3()
	{
		return perfModel3;
	}

	public int getTotalGuestsSinceStart()
	{
		return cTotalGuestsSinceStart;
	}

	public void setTotalGuestsSinceStart(int cTotalGuestsSinceStart)
	{
		this.cTotalGuestsSinceStart = cTotalGuestsSinceStart;
	}

	public void incrementTotalGuestsSinceStart()
	{
		cTotalGuestsSinceStart++;
	}

	public int getTotalxPassGuestsSinceStart()
	{
		return cTotalxPassGuestsSinceStart;
	}
	
	public void setTotalxPassGuestsSinceStart(int cTotalxPassGuestsSinceStart)
	{
		this.cTotalxPassGuestsSinceStart = cTotalxPassGuestsSinceStart;
	}

	public void incrementTotalxPassGuestsSinceStart()
	{
		cTotalxPassGuestsSinceStart++;
	}
	
	public void setBootTime(Date dtBooted)
	{
		this.dtBooted = dtBooted;
	}
	
	public void setBootTime(String sDateBooted)
	{
		this.dtBooted = parseDate(sDateBooted);
	}
	
	public String getBootTime()
	{
		if (dtBooted==null)
			return formatTime(new Date(0).getTime());
		return formatTime(dtBooted.getTime());
	}

	@XmlElement
	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public Integer getVport()
	{
		return vport;
	}

	public void setVport(Integer vport)
	{
		this.vport = vport;
	}
	
	public boolean isHaenabled() 
	{
		return haenabled;
	}

	public void setHaenabled(boolean haenabled) 
	{
		this.haenabled = haenabled;
	}

	public String getParkid() {
		return parkid;
	}

	public void setParkid(String parkid) {
		this.parkid = parkid;
	}

	public void clearPerfValues()
	{
		perfEvents.clear();
		perfEventAgeMsec.clear();
		perfIDMSQueryMsec.clear();
		perfEKGWriteMsec.clear();
		perfSingulationMsec.clear();
		perfPreModelingMsec.clear();
		perfModelingMsec.clear();
		perfPostModelingMsec.clear();
		perfExternalMsec.clear();
		perfSaveGSTMsec.clear();
		perfUpstreamMsec.clear();
		perfWriteToReaderMsec.clear();
		perfMainLoopUtilization.clear();
		perfModel1.clear();
		perfModel2.clear();
		perfModel3.clear();
		dtStartPerf = new Date();
	}
	
	private String formatTime(long lTime)
	{
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}
	
	private Date parseDate(String sDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try
		{
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.parse(sDate);
		}
		catch (ParseException e)
		{
			return new Date(0);
		}
		
	}
	
	private void initPerfMetrics()
	{
		// default meta data for all the perf metrics
		perfEvents = new PerfMetric(new PerfMetricMetadata(
				"perfEvents", 
				PerfMetricType.numeric, 
				"perfEvents", 
				"Number of events in the incoming double buffer queue.", 
				"1.0"
			));
		
		perfEventAgeMsec = new PerfMetric(new PerfMetricMetadata(
				"perfEventAgeMsec", 
				PerfMetricType.milliseconds, 
				"perfEventAgeMsec", 
				"The age of the oldest event in the queue.", 
				"1.0"
			));
		perfIDMSQueryMsec = new PerfMetric(new PerfMetricMetadata(
				"perfIDMSQueryMsec", 
				PerfMetricType.milliseconds, 
				"perfIDMSQueryMsec", 
				"Amount of time (in msec) spent, per event, talking to the IDMS.", 
				"1.0"
			));
		perfEKGWriteMsec = new PerfMetric(new PerfMetricMetadata(
				"perfEKGWriteMsec", 
				PerfMetricType.milliseconds, 
				"perfEKGWriteMsec", 
				"Amount of time (in msec) spent, per event, writing to the EKG file.", 
				"1.0"
			));
		perfSingulationMsec = new PerfMetric(new PerfMetricMetadata(
				"perfSingulationMsec", 
				PerfMetricType.milliseconds, 
				"perfSingulationMsec", 
				"Amount of time (in msec) spent, per event, singulating.", 
				"1.0"
			));
		perfPreModelingMsec = new PerfMetric(new PerfMetricMetadata(
				"perfPreModelingMsec", 
				PerfMetricType.milliseconds, 
				"perfPreModelingMsec", 
				"Amount of time (in msec) spent in the pre-modeling step.", 
				"1.0"
			));
		perfModelingMsec = new PerfMetric(new PerfMetricMetadata(
				"perfModelingMsec", 
				PerfMetricType.milliseconds, 
				"perfModelingMsec", 
				"Amount of time (in msec), per aggregated event, spent in the modeling step.", 
				"1.0"
			));
		perfPostModelingMsec = new PerfMetric(new PerfMetricMetadata(
				"perfPostModelingMsec", 
				PerfMetricType.milliseconds, 
				"perfPostModelingMsec", 
				"Amount of time (in msec) spent in the post-modeling step.", 
				"1.0"
			));
		perfExternalMsec = new PerfMetric(new PerfMetricMetadata(
				"perfExternalMsec", 
				PerfMetricType.milliseconds, 
				"perfExternalMsec", 
				"Amount of time (in msec) spent waiting for external systems (e.g. GXP, Omni, etc.).", 
				"1.0"
			));
		perfWriteToReaderMsec = new PerfMetric(new PerfMetricMetadata(
				"perfWriteToReaderMsec", 
				PerfMetricType.milliseconds, 
				"perfWriteToReaderMsec", 
				"Amount of time (in msec) spent sending information to readers.", 
				"1.0"
			));
		perfSaveGSTMsec = new PerfMetric(new PerfMetricMetadata(
				"perfSaveGSTMsec", 
				PerfMetricType.milliseconds, 
				"Save To GST",
				"Amount of time (in msec) spent writing the GST to the database.", 
				"1.1"
			));
		perfUpstreamMsec = new PerfMetric(new PerfMetricMetadata(
				"perfUpstreamMsec", 
				PerfMetricType.milliseconds, 
				"perfUpstreamMsec", 
				"Amount of time (in msec) spent sending messages (via HTTP or JMS) to other applications.", 
				"1.0"
			));
		perfMainLoopUtilization = new PerfMetric(new PerfMetricMetadata(
				"perfMainLoopUtilization", 
				PerfMetricType.milliseconds, 
				"perfMainLoopUtilization", 
				"Percentage of the main loops 'quantum' that was needed. Over 100% indicates that the xBRC is falling behind on its processing.", 
				"1.0"
			));
		perfModel1 = new PerfMetric(new PerfMetricMetadata(
				"perfModel1", 
				PerfMetricType.milliseconds, 
				"perfModel1", 
				"Model-specific datum", 
				"1.0"
			));
		perfModel2 = new PerfMetric(new PerfMetricMetadata(
				"perfModel2", 
				PerfMetricType.milliseconds, 
				"perfModel2", 
				"Model-specific datum", 
				"1.0"
			));
		perfModel3 = new PerfMetric(new PerfMetricMetadata(
				"perfModel3", 
				PerfMetricType.milliseconds, 
				"perfModel3", 
				"Model-specific datum", 
				"1.0"
			));
	}

	/*
	 * All the setPerf* methods below are here for jaxb's benefit and they are meant to be private.
	 */
	
	@SuppressWarnings("unused")
	private void setPerfEvents(PerfMetric perfEvents)
	{
		this.perfEvents = perfEvents;
		this.perfEvents.setName("perfEvents");
	}
	@SuppressWarnings("unused")
	private void setPerfEventAgeMsec(PerfMetric perfEventAgeMsec)
	{
		this.perfEventAgeMsec = perfEventAgeMsec;
		this.perfEventAgeMsec.setName("perfEventAgeMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfIDMSQueryMsec(PerfMetric perfIDMSQueryMsec)
	{
		this.perfIDMSQueryMsec = perfIDMSQueryMsec;
		this.perfIDMSQueryMsec.setName("perfIDMSQueryMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfEKGWriteMsec(PerfMetric perfEKGWriteMsec)
	{
		this.perfEKGWriteMsec = perfEKGWriteMsec;
		this.perfEKGWriteMsec.setName("perfEKGWriteMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfSingulationMsec(PerfMetric perfSingulationMsec)
	{
		this.perfSingulationMsec = perfSingulationMsec;
		this.perfSingulationMsec.setName("perfSingulationMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfPreModelingMsec(PerfMetric perfPreModelingMsec)
	{
		this.perfPreModelingMsec = perfPreModelingMsec;
		this.perfPreModelingMsec.setName("perfPreModelingMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfModelingMsec(PerfMetric perfModelingMsec)
	{
		this.perfModelingMsec = perfModelingMsec;
		this.perfModelingMsec.setName("perfModelingMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfPostModelingMsec(PerfMetric perfPostModelingMsec)
	{
		this.perfPostModelingMsec = perfPostModelingMsec;
		this.perfPostModelingMsec.setName("perfPostModelingMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfExternalMsec(PerfMetric perfExternalMsec)
	{
		this.perfExternalMsec = perfExternalMsec;
		this.perfExternalMsec.setName("perfExternalMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfWriteToReaderMsec(PerfMetric perfWriteToReaderMsec)
	{
		this.perfWriteToReaderMsec = perfWriteToReaderMsec;
		this.perfWriteToReaderMsec.setName("perfWriteToReaderMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfSaveGSTMsec(PerfMetric perfSaveGSTMsec)
	{
		this.perfSaveGSTMsec = perfSaveGSTMsec;
		this.perfSaveGSTMsec.setName("perfSaveGSTMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfUpstreamMsec(PerfMetric perfUpstreamMsec)
	{
		this.perfUpstreamMsec = perfUpstreamMsec;
		this.perfUpstreamMsec.setName("perfUpstreamMsec");
	}
	@SuppressWarnings("unused")
	private void setPerfMainLoopUtilization(PerfMetric perfMainLoopUtilization)
	{
		this.perfMainLoopUtilization = perfMainLoopUtilization;
		this.perfMainLoopUtilization.setName("perfMainLoopUtilization");
	}
	@SuppressWarnings("unused")
	private void setPerfModel1(PerfMetric perfModel1)
	{
		this.perfModel1 = perfModel1;
		this.perfModel1.setName("perfModel1");
	}
	@SuppressWarnings("unused")
	private void setPerfModel2(PerfMetric perfModel2)
	{
		this.perfModel2 = perfModel2;
		this.perfModel2.setName("perfModel2");
	}
	@SuppressWarnings("unused")
	private void setPerfModel3(PerfMetric perfModel3)
	{
		this.perfModel3 = perfModel3;
		this.perfModel3.setName("perfModel3");
	}

    public boolean isAuditEnabled() {
        return isAuditEnabled;
    }

    public void setAuditEnabled(boolean auditEnabled) {
        isAuditEnabled = auditEnabled;
    }
}
