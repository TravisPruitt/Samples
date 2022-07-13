package com.disney.xband.xbrms.common.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "JmsListener")
public class JmsListenerDto extends HealthItemDto implements IDataTransferObject {
	/*
	 * Dynamic fields
	 */
	private String bootTime;
	private String xiDatabaseVersion;
	private String gffDatabaseVersion;
	private String xbrcMessagesSinceStart;
	private String gxpMessagesSinceStart;
	private String xbmsMessagesSinceStart;
	private String gffMessagesSinceStart;
	private String cacheSize;
	private String cacheCapacity;

	public JmsListenerDto() { }
	
	public JmsListenerDto(Long id, String ip, String hostname, Integer port, String version, Date lastDiscovery, Date nextDiscovery, 
			HealthStatusDto status, Boolean active, String vip, Integer vport, String bootTime, String xiDatabaseVersion,
			String gffDatabaseVersion, String xbrcMessagesSinceStart, String gxpMessagesSinceStart, String xbmsMessagesSinceStart,
			String gffMessagesSinceStart, String cacheSize, String cacheCapacity)
	{
		this.id = id;
		this.ip = ip;
		this.hostname = (hostname == null || hostname.trim().isEmpty()) ? ip : hostname.toLowerCase();
		this.port = port;
		this.version = version;
		this.lastDiscovery = lastDiscovery;
		this.nextDiscovery = nextDiscovery;
		this.status = status;
		this.active = active;
		this.vip = vip;
		this.vport = vport;
		this.bootTime = bootTime;
		this.xiDatabaseVersion = xiDatabaseVersion;
		this.gffDatabaseVersion = gffDatabaseVersion;
		this.xbrcMessagesSinceStart = xbrcMessagesSinceStart;
		this.gxpMessagesSinceStart = gxpMessagesSinceStart;
		this.xbmsMessagesSinceStart = xbmsMessagesSinceStart;
		this.gffMessagesSinceStart = gffMessagesSinceStart;
		this.cacheSize = cacheSize;
		this.cacheCapacity = cacheCapacity;
	}
	
    @Override
	public HealthItemDto clone()
	{
		return new JmsListenerDto(id, ip, hostname, port, version, lastDiscovery, nextDiscovery, 
				status, active, vip, vport, bootTime, xiDatabaseVersion,
				gffDatabaseVersion, xbrcMessagesSinceStart, gxpMessagesSinceStart, xbmsMessagesSinceStart,
				gffMessagesSinceStart, cacheSize, cacheCapacity);
	}
	
    @Override
    public String getType() {
        return HealthItemType.JMSLISTENER.name();
    }

	@Override
	public String getName() {
		return "JmsListener";
	}

	@Override
	public void setName(String name) {
    }

	@IHealthField(id = "bootTime", name = "Boot Time", description = "When this application was started.")
	public String getBootTime()
	{
		return bootTime;
	}

	@IHealthField(id = "xiDatabaseVersion", name = "XI DB Version", description = "Version of XI database.")
	public String getXiDatabaseVersion()
	{
		return xiDatabaseVersion;
	}

	@IHealthField(id = "gffDatabaseVersion", name = "GFF DB Version", description = "Version of GFF database.")
	public String getGffDatabaseVersion()
	{
		return gffDatabaseVersion;
	}

	@IHealthField(id = "xbrcMessagesSinceStart", name = "Xbrc Message Count", description = "Number of XBRC jms messages processed since this application was started.")
	public String getXbrcMessagesSinceStart()
	{
		return xbrcMessagesSinceStart;
	}

	@IHealthField(id = "gxpMessagesSinceStart", name = "GXP Message Count", description = "Number of GXP jms messages processed since this application was started.")
	public String getGxpMessagesSinceStart()
	{
		return gxpMessagesSinceStart;
	}

	@IHealthField(id = "xbmsMessagesSinceStart", name = "XBMS Message Count", description = "Number of XBMS jms messages processed since this application was started.")
	public String getXbmsMessagesSinceStart()
	{
		return xbmsMessagesSinceStart;
	}

	@IHealthField(id = "gffMessagesSinceStart", name = "GFF Message Count", description = "Number of GFF jms messages processed since this application was started.")
	public String getGffMessagesSinceStart()
	{
		return gffMessagesSinceStart;
	}

	@IHealthField(id = "cacheSize", name = "Cache Size", description = "Cache size.")
	public String getCacheSize()
	{
		return cacheSize;
	}

	@IHealthField(id = "cacheCapacity", name = "Cache Capacity", description = "Cache Capacity.")
	public String getCacheCapacity()
	{
		return cacheCapacity;
	}

	public void setBootTime(String bootTime)
	{
		this.bootTime = bootTime;
	}

	public void setXiDatabaseVersion(String xiDatabaseVersion)
	{
		this.xiDatabaseVersion = xiDatabaseVersion;
	}

	public void setGffDatabaseVersion(String gffDatabaseVersion)
	{
		this.gffDatabaseVersion = gffDatabaseVersion;
	}

	public void setXbrcMessagesSinceStart(String xbrcMessagesSinceStart)
	{
		this.xbrcMessagesSinceStart = xbrcMessagesSinceStart;
	}

	public void setGxpMessagesSinceStart(String gxpMessagesSinceStart)
	{
		this.gxpMessagesSinceStart = gxpMessagesSinceStart;
	}

	public void setXbmsMessagesSinceStart(String xbmsMessagesSinceStart)
	{
		this.xbmsMessagesSinceStart = xbmsMessagesSinceStart;
	}

	public void setGffMessagesSinceStart(String gffMessagesSinceStart)
	{
		this.gffMessagesSinceStart = gffMessagesSinceStart;
	}

	public void setCacheSize(String cacheSize)
	{
		this.cacheSize = cacheSize;
	}

	public void setCacheCapacity(String cacheCapacity)
	{
		this.cacheCapacity = cacheCapacity;
	}

    @XmlTransient
    @Override
    public String getRid() {
        return "GET@/status";
    }

    @XmlTransient
    @Override
    public String getAppId() {
        return this.getId() + "";
    }
}
