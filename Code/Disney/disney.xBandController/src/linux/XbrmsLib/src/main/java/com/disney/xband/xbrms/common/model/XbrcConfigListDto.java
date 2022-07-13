package com.disney.xband.xbrms.common.model;

import com.disney.xband.lib.controllerapi.XbrcConfig;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/22/13
 * Time: 7:30 PM
 */
@XmlRootElement(name = "xbrcConfigList")
public class XbrcConfigListDto implements IDataTransferObject {
    private List<XbrcConfig> xbrcConfiguration;

    public List<XbrcConfig> getXbrcConfiguration() {
        return xbrcConfiguration;
    }

    public void setXbrcConfiguration(List<XbrcConfig> configs) {
        this.xbrcConfiguration = configs;
    }
}
