package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.lib.PersistName;

import java.util.List;

@PersistName("XBRMSConfig")
@XmlRootElement(name = "xbrmsConfig")
public class XbrmsConfigDto implements IDataTransferObject {
    private String jmsXbrcDiscoveryTopic = "#";
    private long jmsMessageExpiration_sec = 10;
    private String name = "XBRMS";
    private String id;
    private int httpConnectionTimeout_msec = 3000;
    private int statusThreadPoolCoreSize = 10;
    private int statusThreadPoolMaximumSize = 20; 
    private long statusThreadPoolKeepAliveTime = 2;
    private String ownIpPrefix = "10.";
    private long lastModified = 28800;	// long, long time ago
    private int masterPronouncedDeadAfter_sec = 5;
    private boolean isGlobalServer;
    private List<String> parksUrlList;
    private int ksConnectionToSecs = 15;
    private int ksExpireLogonDataAfterDays = 14;

    private int assignedReaderCacheRefresh_sec = 20;
    private int unassignedRreaderCacheCleanup_sec = 60000;	// 10 minutes

    public XbrmsConfigDto() {}

    @XmlElement
    public String getJmsXbrcDiscoveryTopic() {
        return this.jmsXbrcDiscoveryTopic;
    }

    public void setJmsXbrcDiscoveryTopic(String jmsXbrcDiscoveryTopic) {
        this.jmsXbrcDiscoveryTopic = jmsXbrcDiscoveryTopic;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public int getHttpConnectionTimeout_msec() {
        return this.httpConnectionTimeout_msec;
    }

    public void setHttpConnectionTimeout_msec(int httpConnectionTimeout_msec) {
        this.httpConnectionTimeout_msec = httpConnectionTimeout_msec;
    }

    @XmlElement
    public long getJmsMessageExpiration_sec() {
        return this.jmsMessageExpiration_sec;
    }

    public void setJmsMessageExpiration_sec(long jmsMessageExpiration_sec) {
        this.jmsMessageExpiration_sec = jmsMessageExpiration_sec;
    }
    
    @XmlElement
    public int getStatusThreadPoolCoreSize()
	{
		return statusThreadPoolCoreSize;
	}

	public int getStatusThreadPoolMaximumSize()
	{
		return statusThreadPoolMaximumSize;
	}
	
	@XmlElement
	public long getStatusThreadPoolKeepAliveTime()
	{
		return statusThreadPoolKeepAliveTime;
	}

	public void setStatusThreadPoolCoreSize(int statusThreadPoolCoreSize)
	{
		this.statusThreadPoolCoreSize = statusThreadPoolCoreSize;
	}
	
	@XmlElement
	public void setStatusThreadPoolMaximumSize(int statusThreadPoolMaximumSize)
	{
		this.statusThreadPoolMaximumSize = statusThreadPoolMaximumSize;
	}

	public void setStatusThreadPoolKeepAliveTime(long statusThreadPoolKeepAliveTime)
	{
		this.statusThreadPoolKeepAliveTime = statusThreadPoolKeepAliveTime;
	}

	public String getOwnIpPrefix()
	{
		return ownIpPrefix;
	}

	public void setOwnIpPrefix(String ownIpPrefix)
	{
		this.ownIpPrefix = ownIpPrefix;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}

	public int getMasterPronouncedDeadAfter_sec()
	{
		return masterPronouncedDeadAfter_sec;
	}

	public void setMasterPronouncedDeadAfter_sec(
			int masterPronouncedDeadAfter_sec)
	{
		this.masterPronouncedDeadAfter_sec = masterPronouncedDeadAfter_sec;
	}

	public int getAssignedReaderCacheRefresh_sec()
	{
		return assignedReaderCacheRefresh_sec;
	}

	public void setAssignedReaderCacheRefresh_sec(
			int assignedReaderCacheRefresh_sec)
	{
		this.assignedReaderCacheRefresh_sec = assignedReaderCacheRefresh_sec;
	}

	public int getUnassignedRreaderCacheCleanup_sec()
	{
		return unassignedRreaderCacheCleanup_sec;
	}

	public void setUnassignedRreaderCacheCleanup_sec(
			int unassignedRreaderCacheCleanup_sec)
	{
		this.unassignedRreaderCacheCleanup_sec = unassignedRreaderCacheCleanup_sec;
	}

    public boolean isGlobalServer() {
        return isGlobalServer;
    }

    public void setGlobalServer(boolean globalServer) {
        isGlobalServer = globalServer;
    }

    public List<String> getParksUrlList() {
        return parksUrlList;
    }

    public void setParksUrlList(List<String> parksUrlList) {
        this.parksUrlList = parksUrlList;
    }

    public int getKsConnectionToSecs() {
        return ksConnectionToSecs;
    }

    public void setKsConnectionToSecs(int ksConnectionToSecs) {
        this.ksConnectionToSecs = ksConnectionToSecs;
    }

    public int getKsExpireLogonDataAfterDays() {
        return ksExpireLogonDataAfterDays;
    }

    public void setKsExpireLogonDataAfterDays(int ksExpireLogonDataAfterDays) {
        this.ksExpireLogonDataAfterDays = ksExpireLogonDataAfterDays;
    }

	public XbrmsConfigDto clone() {
        return new XbrmsConfigDto(
                this.jmsXbrcDiscoveryTopic,
                this.jmsMessageExpiration_sec,
                this.name,
                this.id,
                this.httpConnectionTimeout_msec,
                this.statusThreadPoolCoreSize,
                this.statusThreadPoolMaximumSize,
                this.statusThreadPoolKeepAliveTime,
                this.ownIpPrefix,
                this.lastModified,
                this.masterPronouncedDeadAfter_sec,
                this.assignedReaderCacheRefresh_sec,
                this.unassignedRreaderCacheCleanup_sec
        );
    }

	private XbrmsConfigDto(String jmsXbrcDiscoveryTopic, long jmsMessageExpiration_sec,
			String name, String id, int httpConnectionTimeout_msec,
			int statusThreadPoolCoreSize, int statusThreadPoolMaximumSize,
			long statusThreadPoolKeepAliveTime, String ownIpPrefix, long lastModified,
			int masterPronouncedDeadAfter_sec, int assignedReaderCacheRefresh_sec, 
			int unassignedRreaderCacheCleanup_sec) 
	{
		this.jmsXbrcDiscoveryTopic = jmsXbrcDiscoveryTopic;
		this.jmsMessageExpiration_sec = jmsMessageExpiration_sec;
		this.name = name;
		this.id = id;
		this.httpConnectionTimeout_msec = httpConnectionTimeout_msec;
		this.statusThreadPoolCoreSize = statusThreadPoolCoreSize;
		this.statusThreadPoolMaximumSize = statusThreadPoolMaximumSize;
		this.statusThreadPoolKeepAliveTime = statusThreadPoolKeepAliveTime;
		this.ownIpPrefix = ownIpPrefix;
		this.lastModified = lastModified;
		this.masterPronouncedDeadAfter_sec = masterPronouncedDeadAfter_sec;
		this.assignedReaderCacheRefresh_sec = assignedReaderCacheRefresh_sec;
		this.unassignedRreaderCacheCleanup_sec = unassignedRreaderCacheCleanup_sec;
	}

}