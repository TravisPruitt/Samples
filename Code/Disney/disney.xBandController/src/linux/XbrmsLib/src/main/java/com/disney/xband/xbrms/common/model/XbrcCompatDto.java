package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/30/13
 * Time: 11:52 PM
 */
@XmlType(propOrder={"name","id","url","model","lastDiscovery","nextDiscovery", "version","active","haStatus","haEnabled"})
public class XbrcCompatDto implements IDataTransferObject {
    private String name;
    private String id;
    private String url;
    private String model;
    private Date lastDiscovery;
    private Date nextDiscovery;
    private String version;
    private Boolean active;
    private String haStatus;
    private String haEnabled;

    public XbrcCompatDto() {
    }

    public XbrcCompatDto(XbrcDto dto) {
        this.name = dto.getName();
        this.id = dto.getFacilityId();
        this.url = dto.getUrl();
        this.model = dto.getModel();
        this.lastDiscovery = dto.getLastDiscovery();
        this.nextDiscovery = dto.getNextDiscovery();
        this.version = dto.version;
        this.active = dto.getActive();
        this.haStatus = dto.getHaStatus();
        this.haEnabled = dto.getHaEnabled();
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    @XmlElement
    public String getModel() {
        return model;
    }

    @XmlElement
    public Date getLastDiscovery() {
        return lastDiscovery;
    }

    @XmlElement
    public Date getNextDiscovery() {
        return nextDiscovery;
    }

    @XmlElement
    public String getVersion() {
        return version;
    }

    @XmlElement
    public Boolean getActive() {
        return active;
    }

    @XmlElement
    public String getHaStatus() {
        return haStatus;
    }

    @XmlElement
    public String getHaEnabled() {
        return haEnabled;
    }
}
