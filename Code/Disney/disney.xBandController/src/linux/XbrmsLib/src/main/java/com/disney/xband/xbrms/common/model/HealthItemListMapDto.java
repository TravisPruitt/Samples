package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/16/13
 * Time: 4:45 PM
 */
@XmlRootElement(name = "healthItemListMap")
public class HealthItemListMapDto implements IDataTransferObject {
    private Map<String, HealthItemListDto> map;

    public Map<String, HealthItemListDto> getMap() {
        return map;
    }

    public void setMap(Map<String, HealthItemListDto> inventory) {
        this.map = inventory;
    }
}
