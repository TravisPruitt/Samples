package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.health.StatusType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/30/13
 * Time: 10:46 PM
 *
 * This class is for backward compatibility with xBRMS < 1.5.x
 */
@XmlRootElement(name="xbrmsStatus")
@XmlType(propOrder={"requiredDatabaseVersion", "dbStatus", "dbStatusMessage", "recentProblems", "status", "statusMessage", "haStatus", "startTime"})
public class XbrmsStatusCompatDto {
    // envelope's attributes
    private String version;         // xbrms software version
    private Date time;              // xbrms startup time
    private String name;            // xbrms name
    private String id;              // xbrms id

    private String requiredDatabaseVersion;
    private StatusType dbStatus;
    private String dbStatusMessage;
    private ProblemsReportDto recentProblems;
    private StatusType status;
    private String statusMessage;
    // High Availability status
    private String haStatus;
    private Date startTime;

    public XbrmsStatusCompatDto() {
    }

    public XbrmsStatusCompatDto(XbrmsStatusDto dto) {
        this.version = dto.getVersion();
        this.time = dto.getTime();
        this.name = dto.getName();
        this.id = dto.getId();
        this.requiredDatabaseVersion = dto.getRequiredDatabaseVersion();
        this.dbStatus = dto.getDbStatus();
        this.dbStatusMessage = dto.getDbStatusMessage();
        this.recentProblems = dto.getRecentProblems();
        this.status = dto.getStatus();
        this.statusMessage = dto.getStatusMessage();
        this.haStatus = dto.getHaStatus();
        this.startTime = dto.getStartTime();
    }

    @XmlAttribute
    public String getVersion() {
        return version;
    }

    @XmlAttribute
    public Date getTime() {
        return time;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    @XmlElement
    public String getRequiredDatabaseVersion() {
        return requiredDatabaseVersion;
    }

    @XmlElement
    public StatusType getDbStatus() {
        return dbStatus;
    }

    @XmlElement
    public String getDbStatusMessage() {
        return dbStatusMessage;
    }

    @XmlElement
    public ProblemsReportDto getRecentProblems() {
        return recentProblems;
    }

    @XmlElement
    public StatusType getStatus() {
        return status;
    }

    @XmlElement
    public String getStatusMessage() {
        return statusMessage;
    }

    @XmlElement
    public String getHaStatus() {
        return haStatus;
    }

    @XmlElement
    public Date getStartTime() {
        return startTime;
    }
}
