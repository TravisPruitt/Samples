package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/23/13
 * Time: 6:08 AM
 */
@XmlRootElement(name = "locationInfoList")
public class LocationInfoListDto implements IDataTransferObject {
    private List<LocationInfoDto> locationInfoList;

    public List<LocationInfoDto> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<LocationInfoDto> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }
}
