package com.disney.xband.xbrms.common.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.disney.xband.common.lib.health.StatusType;

@XmlRootElement(name="status")
public class XbrmsStatusDto implements IDataTransferObject
{
	private StatusType status;
	private String statusMessage;
	private final String requiredDatabaseVersion = "1.7.8";
	private StatusType dbStatus;
	private String dbStatusMessage;
	private Date time;			// xbrms startup time
	private String version;		// xbrms software version
	private String name;		// xbrms name
	private String id;			// xbrms id
    private ProblemsReportDto recentProblems;
    private String ip;
    private String hostname;
    // High Availability status
    private String haStatus;
    private Date startTime;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElement
    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    @XmlElement
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @XmlElement
    public String getRequiredDatabaseVersion() {
        return requiredDatabaseVersion;
    }

    @XmlElement
    public StatusType getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(StatusType dbStatus) {
        this.dbStatus = dbStatus;
    }

    @XmlElement
    public String getDbStatusMessage() {
        return dbStatusMessage;
    }

    public void setDbStatusMessage(String dbStatusMessage) {
        this.dbStatusMessage = dbStatusMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public ProblemsReportDto getRecentProblems() {
        return this.recentProblems;
    }

    public String getIp()
	{
		return ip;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public void setRecentProblems(ProblemsReportDto problems) {
        this.recentProblems = problems;
    }

    @XmlElement
    public String getHaStatus() {
        return haStatus;
    }

    public void setHaStatus(String haStatus) {
        this.haStatus = haStatus;
    }

    @XmlElement
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
