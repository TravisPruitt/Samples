package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.idms.lib.model.GuestIdentifier;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 7:26 PM
 */
@XmlRootElement(name="xbrms")
@XmlSeeAlso({
    XbrmsConfigDto.class, ProblemsReportDto.class, XbrmsStatusDto.class,
    FacilityListDto.class, FacilityDto.class, XbrcListDto.class, JmsListenerListDto.class,
    IdmsListDto.class, XbrcConfigurationListDto.class, XbrcConfiguration.class, XbrcConfigListMapDto.class,
    HealthItemListMapDto.class, LocationInfoDto.class, LocationInfoListDto.class,
    ReaderLocationInfo.class, ReaderInfo.class, ReaderInfoListDto.class, XbrcIdListDto.class,
    PerfMetricMetadataMapDto.class, XbrmsDto.class, XbrmsListDto.class, EnvPropertiesMapDto.class, String.class,
    AuditConfig.class, AuditEvent.class, AuditEventList.class, SchedulerRequest.class, SchedulerResponse.class
})
public class DtoWrapper <T> {
    private T content;

    // envelope's attributes
    private Date time;
    private String version;
    private String name;
    private String id;

    @XmlAttribute
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @XmlAttribute
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
