package com.disney.xband.xbrms.common.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 3/13/13
 * Time: 11:53 PM
 */
@XmlRootElement(name = "xbrmsInfoList")
public class XbrmsListDto implements IDataTransferObject {
    private List<XbrmsDto> XbrmsItem;
    private String uiServer;

    public List<XbrmsDto> getXbrmsItem() {
        return XbrmsItem;
    }

    public void setXbrmsItem(List<XbrmsDto> XbrmsItem) {
        this.XbrmsItem = XbrmsItem;
    }

    public String getUiServer() {
        return uiServer;
    }

    public void setUiServer(String uiServer) {
        this.uiServer = uiServer;
    }
}
