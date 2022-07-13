package com.disney.xband.xbrms.client.action;

import java.util.List;

import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.HealthItemDto;
import org.apache.log4j.Logger;

public class ReaderHealthAjaxAction extends ReaderHealthAction {
    private static Logger logger = Logger.getLogger(ReaderHealthAction.class);

    private List<ReaderInfo> locationReaders;
    private int xbrcId;
    private String xbrcIp;
    private int xbrcPort;
    private Long locationId;
    private String locationName;

	public String locationReaders() throws Exception 
	{
        try 
        {
            final HealthItemDto xbrcDto;

            if(this.xbrcId <= 0) {
                xbrcDto = XbrmsUtils.getRestCaller().getHealthItemByAddr(this.xbrcIp, "" + this.xbrcPort);
                this.xbrcId = xbrcDto.getId().intValue();
            }

            this.locationReaders = XbrmsUtils.getRestCaller().getReadersForLocation(this.xbrcId + "", this.locationId.toString()).getReaderInfoList();
        }
        catch (Exception e) 
        {
            final String errorMessage = "Failed to retrieve readers inventory. ";

            if (logger.isDebugEnabled())
                logger.error(errorMessage, e);
            else 
                logger.error(errorMessage);
            
            this.errMsg = e.toString();
            return ERROR;
        }

		return super.execute();
	}


	public Long getLocationId()
	{
		return locationId;
	}

	public void setLocationId(Long locationId)
	{
		this.locationId = locationId;
	}

	public void setXbrcIp(String xbrcIp)
	{
		this.xbrcIp = xbrcIp;
	}

	public void setXbrcPort(int xbrcPort)
	{
		this.xbrcPort = xbrcPort;
	}

	public List<ReaderInfo> getLocationReaders()
	{
		return locationReaders;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}
}
