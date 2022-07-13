package com.disney.xband.xbrms.client.action;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.common.lib.ConfigInfo;
import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.LocationInfoDto;
import com.disney.xband.xbrms.common.model.XbrcConfigListDto;
import com.disney.xband.xbrms.common.model.XbrcIdListDto;
import com.opensymphony.xwork2.Preparable;

public class PowerManagementAction<locInfo> extends BaseAction implements Preparable {
	
	public class XbrcInfo
	{
		private Long id;
		private String name;
		private String openTime;
		private String closeTime;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOpenTime() {
			return openTime;
		}
		public void setOpenTime(String openTime) {
			this.openTime = openTime;
		}
		public String getCloseTime() {
			return closeTime;
		}
		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}
	}
	
	private static DateTimeFormatter jodaDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static Logger logger = Logger.getLogger(PowerManagementAction.class);
	
    private List<XbrcInfo> inventory;
    private Long xbrcId; // Returned by page for overriding schedule
    private Long hours; // Returned by page for overriding schedule
	
	@Override
    public void prepare() throws Exception {
        this.buildInventory();
    }

   private void buildInventory() throws Exception {
	    this.inventory = new ArrayList<XbrcInfo>();
	   
	    List<LocationInfoDto> xbrcs;
       
        try {
            xbrcs = XbrmsUtils.getRestCaller().getLocations().getLocationInfoList();
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
            return;
        }
        
        
        for (LocationInfoDto anXbrc: xbrcs)
        {
        	boolean bHasReaderWithBattery = false;
			for ( LocationInfo locInfo : anXbrc.getLoc().getReaderlocationinfo())
        	{
        		for (ReaderInfo reader : locInfo.getReaders())
        		{
        			if (reader.getBatteryLevel() != null)
        			{
        				bHasReaderWithBattery = true;
        				break;
        			}
        		}
        		if (bHasReaderWithBattery)
        			break;
        	}
			if (bHasReaderWithBattery)
			{
				XbrcInfo xbrcInfo = new XbrcInfo();
				
				xbrcInfo.setName(anXbrc.getXbrc().getName());
				xbrcInfo.setId(anXbrc.getXbrc().getId());
				this.inventory.add(xbrcInfo);
				
				final String xbrcId = anXbrc.getXbrc().getId().toString();
		        final List<String> xbrcsStr = new LinkedList<String>();
				xbrcsStr.add(xbrcId);

		        try {
		            final XbrcIdListDto xbrcsList = new XbrcIdListDto();
		            xbrcsList.setXbrcId(xbrcsStr);
	
		            final Map<String, XbrcConfigListDto> props = XbrmsUtils.getRestCaller().getXbrcProperties(xbrcsList).getMap();
		            XbrcConfigListDto configList = null;
		            
		            Iterator<Entry<String, XbrcConfigListDto>> it = props.entrySet().iterator();
		            while (it.hasNext()) {
		                Map.Entry<String, XbrcConfigListDto> pairs = (Map.Entry<String, XbrcConfigListDto>)it.next();
		                configList = pairs.getValue();
		                break;
		            }

                    if(configList == null) {
                        continue;
                    }

		            List<XbrcConfig> xbrcConfigs = configList.getXbrcConfiguration();
		            XbrcConfig xbrcConfig = xbrcConfigs.get(0);
		            LinkedList<ConfigInfo> configInfo = xbrcConfig.getConfiguration();
		            for (ConfigInfo i : configInfo)
		            {
		            	if ("readerPowerOnTime".equals(i.getName()))
		            	{
		            		String time = i.getValue();
		            		if (time != null && !time.isEmpty())
		            		{
		            			DateTime utc = jodaDateFormatter.withZoneUTC().parseDateTime(time);   // January 1, 1900  -- only time matters
		            			DateTimeZone orlando = DateTimeZone.forID("America/New_York");
		            			DateTime now = new DateTime(orlando);                                 // May 5, 2012, --- time does not matter
		            			DateTime utcIsh = utc.withDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());   // Filled in with something close to a valid date
		            			DateTime local = utcIsh.withZone(orlando);
		            			xbrcInfo.setOpenTime(local.toString("h:mm a z"));
		            		}
		            	}
		            	else if ("readerPowerOffTime".equals(i.getName()))
		            	{
		            		String time = i.getValue();
		            		if (time != null && !time.isEmpty())
		            		{
		            			DateTime utc = jodaDateFormatter.withZoneUTC().parseDateTime(time);   // January 1, 1900  -- only time matters
		            			DateTimeZone orlando = DateTimeZone.forID("America/New_York");
		            			DateTime now = new DateTime(orlando);                                 // May 5, 2012, --- time does not matter
		            			DateTime utcIsh = utc.withDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());   // Filled in with something close to a valid date
		            			DateTime local = utcIsh.withZone(orlando);
		            			xbrcInfo.setCloseTime(local.toString("h:mm a z"));
		            		}
		            	}
		            }
	
		        }
		        catch(Exception e) {
		            final String error = "Failed to retrieve xBRC properties for " + xbrcInfo.getName();
		            this.addActionMessage(error);
		            logger.error(error, e);
		        }
			}
        }
    }
	
	@Override
	public String execute() throws Exception {
		return super.execute();
	}
	
    public List<XbrcInfo> getInventory() {
        return this.inventory;
    }
  
    
    public String overrideSchedule() throws Exception {
        if (xbrcId == null) {
            this.addActionError("Action aborted. Can not override schedule. xBRC id missing.");
            return INPUT;
        }

        if (hours == null) {
            this.addActionError("Action aborted. Can not override schedule. Hours is missing.");
            return INPUT;
        }

        try {
        	XbrmsUtils.getRestCaller().overrideSchedule(this.xbrcId.toString(), this.hours.toString());
        }
        catch (Exception e) {
            logger.warn(e);
            this.addActionError(e.getMessage());
            return INPUT;
        }

        return SUCCESS;
    }
    public Long getXbrcId() {
        return xbrcId;
    }

    public void setXbrcId(Long xbrcId) {
        this.xbrcId = xbrcId;
    }

    public Long getHours() {
        return hours;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }
}
