package com.disney.xband.xbrms.common.model;

import com.disney.xband.lib.controllerapi.ReaderLocationInfo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/5/12
 * Time: 10:35 AM
 */
@XmlRootElement(name = "xbrmsLocationInfo")
public class LocationInfoDto implements IDataTransferObject {
    private XbrcDto xbrcDto;
    private ReaderLocationInfo loc;

    public XbrcDto getXbrc() {
        return xbrcDto;
    }

    public void setXbrc(XbrcDto xbrcDto) {
        this.xbrcDto = xbrcDto;
    }

    public ReaderLocationInfo getLoc() {
        return loc;
    }

    public void setLoc(ReaderLocationInfo loc) {
        this.loc = loc;
    }
}
