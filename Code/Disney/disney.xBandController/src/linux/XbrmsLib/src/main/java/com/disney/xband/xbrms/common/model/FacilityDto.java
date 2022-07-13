package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "facility")
public class FacilityDto implements IDataTransferObject {
    private XbrcDto facility;

    public XbrcDto getFacility() {
        return facility;
    }

    public void setFacility(XbrcDto facility) {
        this.facility = facility;
    }
}
