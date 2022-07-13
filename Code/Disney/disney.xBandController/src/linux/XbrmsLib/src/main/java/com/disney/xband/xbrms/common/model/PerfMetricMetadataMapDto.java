package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/24/13
 * Time: 11:54 AM
 */
@XmlRootElement(name = "perfMetricMetadataMap")
public class PerfMetricMetadataMapDto implements IDataTransferObject {
    private Map<String, PerfMetricMetadata> map;

    public Map<String, PerfMetricMetadata> getMap() {
        return map;
    }

    public void setMap(Map<String, PerfMetricMetadata> map) {
        this.map = map;
    }
}
