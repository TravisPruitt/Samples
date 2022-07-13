package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 8:27 AM
 */
@XmlRootElement(name = "jmsListenerList")
public class JmsListenerListDto implements IDataTransferObject {
    private List<JmsListenerDto> healthItem;
    public List<JmsListenerDto> getHealthItem() {
        return healthItem;
    }

    public void setHealthItem(List<JmsListenerDto> healthItem) {
        this.healthItem = healthItem;
    }
}
