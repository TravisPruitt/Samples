package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "facilities")
public class FacilityListDto implements IDataTransferObject {
    private List<XbrcDto> facility;

    public List<XbrcDto> getFacility() {
        return facility;
    }

    public void setFacility(List<XbrcDto> facility) {
        this.facility = facility;
    }
}
