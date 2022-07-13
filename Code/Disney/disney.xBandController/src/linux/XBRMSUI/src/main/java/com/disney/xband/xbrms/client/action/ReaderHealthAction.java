package com.disney.xband.xbrms.client.action;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.client.model.ReaderHealthSortHelper;
import com.disney.xband.xbrms.client.model.ReaderHealthSortHelper.ReaderHealthPageSort;
import com.disney.xband.xbrms.client.model.ViewType;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.LocationInfoDto;
import com.opensymphony.xwork2.Preparable;

public class ReaderHealthAction extends BaseAction implements Preparable {
    protected static Logger logger = Logger.getLogger(ReaderHealthAction.class);

    protected List<LocationInfoDto> inventory;
    protected Long readerId;
    protected Long xbrcId;

    protected ViewType viewType;
    protected ReaderHealthPageSort pageSort;

    // Preserve scroll position on page refresh. TODO Move to a cookie once we have them.
    private Integer scrollTop;

    @Override
    public void prepare() throws Exception 
    {
        this.buildInventory();
    }

    protected void buildInventory() throws Exception {
        try {
            List<LocationInfoDto> fullInventory = XbrmsUtils.getRestCaller().getLocations().getLocationInfoList();
            if (fullInventory == null)
            	return;
            
            this.inventory = new LinkedList<LocationInfoDto>();
            for (LocationInfoDto lid : fullInventory)
            {
            	// slave readers' health shouldn't be displayed
            	if (HAStatusEnum.getStatus(lid.getXbrc().getHaStatus()) != HAStatusEnum.slave) {
					this.inventory.add(lid);
				}
            }
        }
        catch(XbrmsServerNotSetException e) {
            throw e;
        }
        catch (Exception e) {
            final String errorMessage = "Failed to retrieve locations inventory.";
            this.addActionError(errorMessage);

            if (logger.isDebugEnabled()) {
                logger.error(errorMessage, e);
            }
            else {
                logger.error(errorMessage);
            }
        }
    }

    @Override
    public String execute() throws Exception {
        if (pageSort == null) {
            pageSort = ReaderHealthPageSort.VENUE_LOCATION_READER_HEALTH_NAME;
        }

        ReaderHealthSortHelper.sort(this.inventory, pageSort);

        if (viewType == null) {
            viewType = ViewType.SMALL_GRID;
        }

        return super.execute();
    }

    public String identifyReader() throws Exception {
        if (xbrcId == null) {
            this.addActionError("Action aborted. Can not identify the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not identify a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().identifyReaderComp(this.xbrcId.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public String rebootReader() throws Exception {
        if (xbrcId == null) {
            this.addActionError("Action aborted. Can not reboot the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not reboot a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().rebootReaderComp(this.xbrcId.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public String restartReader() throws Exception {
        if (xbrcId == null) {
            this.addActionError("Action aborted. Can not restart the health item. Health item id missing.");
            return INPUT;
        }

        if (readerId == null) {
            this.addActionError("Action aborted. Can not restart a reader. Reader id missing.");
            return INPUT;
        }

        try {
            XbrmsUtils.getRestCaller().restartReaderComp(this.xbrcId.toString(), this.readerId.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }

    public List<LocationInfoDto> getInventory() {
        return this.inventory;
    }

    public Long getXbrcId() {
        return xbrcId;
    }

    public void setXbrcId(Long xbrcId) {
        this.xbrcId = xbrcId;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public String getViewType() {
        if (viewType == null) {
            return ViewType.SMALL_GRID.name();
        }
        else {
            return viewType.name();
        }
    }

    public void setViewType(String viewType) {
        if (viewType == null || viewType.isEmpty()) {
            return;
        }

        this.viewType = ViewType.valueOf(viewType);
    }

    public Integer getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(Integer scrollTop) {
        this.scrollTop = scrollTop;
    }
}

