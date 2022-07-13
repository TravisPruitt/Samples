package com.disney.xband.xbrms.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/30/13
 * Time: 10:46 PM
 *
 * This class is for backward compatibility with xBRMS < 1.5.x
 */
@XmlRootElement(name = "xbrms")
public class FacilityListCompatDto implements IDataTransferObject {
    // envelope's attributes
    private Date time;
    private String version;
    private String name;
    private String id;

    // envelope's content
    private Collection<XbrcCompatDto> facilities;

    public FacilityListCompatDto() {
        facilities = new ArrayList<XbrcCompatDto>();
    }

    public FacilityListCompatDto(DtoWrapper<FacilityListDto> dto) {
        facilities = new ArrayList<XbrcCompatDto>();
        this.time = dto.getTime();
        this.name = dto.getName();
        this.version = dto.getVersion();
        this.id = dto.getId();

        if((dto.getContent() != null) && (dto.getContent().getFacility() != null)) {
            for(XbrcDto xbrc : dto.getContent().getFacility()) {
                facilities.add(new XbrcCompatDto(xbrc));
            }
        }
    }

    @XmlAttribute
    public Date getTime() {
        return time;
    }

    @XmlAttribute
    public String getVersion() {
        return version;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElementWrapper
    @XmlElement(name = "facility")
    public Collection<XbrcCompatDto> getFacilities() {
        return facilities;
    }

    public void setFacilities(Collection<XbrcCompatDto> facilities) {
        this.facilities = facilities;
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
}
