package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/23/13
 * Time: 4:01 AM
 */
@XmlRootElement(name = "xbrcConfigListMap")
public class XbrcConfigListMapDto implements IDataTransferObject {
    private Map<String, XbrcConfigListDto> map;

    public Map<String, XbrcConfigListDto> getMap() {
        return map;
    }

    public void setMap(Map<String, XbrcConfigListDto> map) {
        this.map = map;
    }
}
