package com.disney.xband.xbrms.client.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.performance.PerfMetricMetadata;
import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.client.model.ReaderHealthSortHelper;
import com.disney.xband.xbrms.client.model.ReaderHealthSortHelper.ReaderHealthPageSort;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.opensymphony.xwork2.Preparable;

public class XbrcAction extends BaseAction implements Preparable {

    private Logger logger = Logger.getLogger(XbrcAction.class);

    private HealthItem xbrc;
    private Long id;
    private Long readerId;
    private int tabSelected;
    private ReaderLocationInfo rlInfo;
    private Map<String, PerfMetricMetadata> metricsMeta;
    private ReaderHealthPageSort pageSort;
    private ReaderHealthPageSort[] pageSortOptions;

    @Override
    public void prepare() throws Exception 
    {
        pageSortOptions = new ReaderHealthPageSort[1];
        pageSortOptions[0] = ReaderHealthPageSort.READER_HEALTH_LOCATION;
    }

    @Override
    public String execute() throws Exception {

        if (id == null) {
            this.errMsg = this.getText("xbrcDto.id.missing");
            return INPUT;
        }

        HealthItemDto item = null;

        try {
            item = XbrmsUtils.getRestCaller().getHealthItemById("" + id);
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
        }

        if (item == null || !(item instanceof XbrcDto)) {
            this.addActionMessage(this.getText("xbrcDto.not.found", new String[]{id.toString()}));
            return INPUT;
        }

        xbrc = new HealthItem(item);

        try {
            rlInfo = XbrmsUtils.getRestCaller().getLocationInfo(id.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
        }

        // sort the results
        if (pageSort == null) {
            pageSort = ReaderHealthPageSort.READER_HEALTH_NAME_LOCATION;
        }

        ReaderHealthSortHelper.sort(rlInfo, pageSort);

        // retrieve performance metrics metadata
        if (item.getIp() != null && !item.getIp().isEmpty() && item.getPort() != null) {

            try {
                this.metricsMeta = XbrmsUtils.getRestCaller().getMetricMetaById(this.id.toString()).getMap();
            }
            catch (Exception e) {
                this.logger.warn("Unable to retrieve performance graph metadata: " + e.getMessage());
            }
        }
        else {
            logger.error("Xbrc health item with ip:"
                    + (item.getIp() != null ? item.getIp() : "null") + " and port:"
                    + (item.getPort() != null ? item.getPort() : "null"));
        }

        return super.execute();
    }

    public String identifyReader() throws Exception {
        if (id == null) {
            this.addActionError("Action aborted. Can not identify the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not identify a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().identifyReaderComp(this.id.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public String restartReader() throws Exception {
        if (id == null) {
            this.addActionError("Action aborted. Can not identify the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not identify a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().restartReaderComp(this.id.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public String rebootReader() throws Exception {
        if (id == null) {
            this.addActionError("Action aborted. Can not identify the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not identify a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().rebootReaderComp(this.id.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public HealthItem getXbrc() {
        return xbrc;
    }

    public void setXbrc(HealthItem xbrc) {
        this.xbrc = xbrc;
    }

    public int getTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public ReaderLocationInfo getRlInfo() {
        return rlInfo;
    }

    public Map<String, PerfMetricMetadata> getMetricsMeta() {
        return metricsMeta;
    }

    public ReaderHealthPageSort[] getPageSortOptions() {
        return pageSortOptions;
    }

    public void setPageSort(String pageSort) {
        if (pageSort == null || pageSort.isEmpty()) {
            return;
        }

        this.pageSort = ReaderHealthPageSort.valueOf(pageSort);
    }

    public String getPageSort() {
        return pageSort.name();
    }
}
