package com.disney.xband.xbrms.common.model;

import com.disney.xband.xbrc.lib.model.ReaderInfo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/23/13
 * Time: 6:07 AM
 */
@XmlRootElement(name = "readerInfoList")
public class ReaderInfoListDto implements IDataTransferObject {
    private List<ReaderInfo> readerInfoList;

    @XmlElement(name="readerInfo")
    public List<ReaderInfo> getReaderInfoList() {
        return readerInfoList;
    }

    public void setReaderInfoList(List<ReaderInfo> readerInfoList) {
        this.readerInfoList = readerInfoList;
    }
}
