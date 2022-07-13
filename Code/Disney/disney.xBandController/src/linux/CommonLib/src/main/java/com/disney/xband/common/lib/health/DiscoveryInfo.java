package com.disney.xband.common.lib.health;

import java.io.Serializable;

/*
 * This object is sent via JSM message from XBRC to announce its presence.
 */
public class DiscoveryInfo implements Serializable {
	/**
	 * Be sure to change the version number if the fields change.
	 */
	private static final long serialVersionUID = 1L;
	 
	// IP address of the XBRC
	private String ip;
	// Port the xbrc listens on
	private int port;
	// Host name of the XBRC
	private String hostname;
	// Venue name
	private String venue;
	// Attraction model name
	private String model;
	// XBRC name (usually same as venue name unless multiple XBRC are installed at a venue)
	private String name;
	// Serial version of this object on the remote system (set by publisher only) .
	private long remoteSVUID = 0L;
	// Seconds between discovery messages
	private int discoveryInterval = 60;
	// HA status
	private String haStatus;
	// config info
	private String configurationChangedTime;
	
	public DiscoveryInfo() {	
	}
	
	public DiscoveryInfo(String ip, Integer port, String hostname, String venue, String model,
			String name, String haStatus, long remoteSVUID, int discoveryInterval, String configurationChangedTime) {
		super();
		this.ip = ip;
		this.port = port;
		this.hostname = hostname;
		this.venue = venue;
		this.name = name;
		this.model = model;
		this.haStatus = haStatus;
		this.remoteSVUID = remoteSVUID;
		this.discoveryInterval = discoveryInterval;
		this.configurationChangedTime = configurationChangedTime;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
	public String getHaStatus()
	{
		return haStatus;
	}

	public void setHaStatus(String haStatus)
	{
		this.haStatus = haStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public long getRemoteSVUID() {
		return remoteSVUID;
	}
	public void setRemoteSVUID(long remoteSVUID) {
		this.remoteSVUID = remoteSVUID;
	}
	
	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getDiscoveryInterval() {
		return discoveryInterval;
	}

	public void setDiscoveryInterval(int discoveryInterval) {
		this.discoveryInterval = discoveryInterval;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public String getConfigurationChangedTime()	{
		return configurationChangedTime;
	}

	public void setConfigurationChangedTime(String configurationChangedTime) {
		this.configurationChangedTime = configurationChangedTime;
	}

	public DiscoveryInfo clone() {
		return new DiscoveryInfo(
			this.ip,
			this.port,
			this.hostname,
			this.venue,
			this.model,
			this.name,
			this.haStatus,
			this.remoteSVUID,
			this.discoveryInterval,
			this.configurationChangedTime);
	}
}
