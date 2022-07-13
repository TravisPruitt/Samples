package com.disney.xband.xbrms.common.model;

import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/22/13
 * Time: 7:20 PM
 */
@XmlRootElement(name = "xbrcConfigurationList")
public class XbrcConfigurationListDto implements IDataTransferObject {
    private List<XbrcConfiguration> xbrcConfiguration;

    public List<XbrcConfiguration> getXbrcConfiguration() {
        return xbrcConfiguration;
    }

    public void setXbrcConfiguration(List<XbrcConfiguration> healthItem) {
        this.xbrcConfiguration = healthItem;
    }
}
