package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 8:27 AM
 */
@XmlRootElement(name = "xbrcList")
public class XbrcListDto implements IDataTransferObject {
    private List<XbrcDto> healthItem;
    public List<XbrcDto> getHealthItem() {
        return healthItem;
    }

    public void setHealthItem(List<XbrcDto> healthItem) {
        this.healthItem = healthItem;
    }
}
