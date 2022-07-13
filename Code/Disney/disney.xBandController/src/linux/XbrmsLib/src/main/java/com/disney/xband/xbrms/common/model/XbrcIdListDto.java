package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/23/13
 * Time: 5:42 AM
 */
@XmlRootElement(name = "xbrcIdList")
public class XbrcIdListDto implements IDataTransferObject {
    private List<String> xbrcId;

    public List<String> getXbrcId() {
        return xbrcId;
    }

    public void setXbrcId(List<String> xbrcId) {
        this.xbrcId = xbrcId;
    }
}
