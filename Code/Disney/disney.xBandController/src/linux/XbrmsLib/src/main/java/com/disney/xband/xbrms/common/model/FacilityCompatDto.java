package com.disney.xband.xbrms.common.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlAttribute;
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
public class FacilityCompatDto implements IDataTransferObject {
    // envelope's attributes
    private Date time;
    private String version;

    // envelope's content
    private XbrcCompatDto facility;

    public FacilityCompatDto() {
    }

    public FacilityCompatDto(DtoWrapper <FacilityDto> dto) {
        this.time = dto.getTime();
        this.version = dto.getVersion();
        this.facility = new XbrcCompatDto(dto.getContent().getFacility());
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

    public XbrcCompatDto getFacility() {
        return facility;
    }

    public void setFacility(XbrcCompatDto facility) {
        this.facility = facility;
    }
}
