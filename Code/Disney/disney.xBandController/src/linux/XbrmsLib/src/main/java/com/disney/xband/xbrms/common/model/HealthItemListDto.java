package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/16/13
 * Time: 12:55 PM
 */
@XmlRootElement(name = "healthItemList")
public class HealthItemListDto implements IDataTransferObject {
    private List<HealthItemDto> healthItem;

    public List<HealthItemDto> getHealthItem() {
        return healthItem;
    }

    public void setHealthItem(List<HealthItemDto> healthItem) {
        this.healthItem = healthItem;
    }
}
