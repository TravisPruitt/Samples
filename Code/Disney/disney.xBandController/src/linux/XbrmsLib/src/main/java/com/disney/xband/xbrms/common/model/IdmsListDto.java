package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 8:26 AM
 */
@XmlRootElement(name = "idmsList")
public class IdmsListDto implements IDataTransferObject {
    private List<IdmsDto> healthItem;
    public List<IdmsDto> getHealthItem() {
        return healthItem;
    }

    public void setHealthItem(List<IdmsDto> healthItem) {
        this.healthItem = healthItem;
    }
}
