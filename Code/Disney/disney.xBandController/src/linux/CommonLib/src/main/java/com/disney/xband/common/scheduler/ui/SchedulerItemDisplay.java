package com.disney.xband.common.scheduler.ui;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.scheduler.SchedulerItem;

public class SchedulerItemDisplay {
	
	private static final String DEFAULT_FORMAT = "MMM d HH:mm:ss";
	private Integer maxStatusReportLength = 200;
	
	private SchedulerItem item;

	public SchedulerItemDisplay(SchedulerItem item) {
		this.item = item;
	}
	
	public SchedulerItemDisplay(SchedulerItem item, Integer maxStatusReportLength) {
		this.item = item;
		this.maxStatusReportLength = maxStatusReportLength;
	}
	
	public SchedulerItem getItem() {
		return item;
	}

	public void setItem(SchedulerItem item) {
		this.item = item;
	}
	
	public String getShortJobClassName() {
		return item.getJobClassName().substring(item.getJobClassName().lastIndexOf('.') + 1);
	}
	
	public String getLastStartDate() {
		if (item.getLastLog() == null || item.getLastLog().getStartDate() == null)
			return "Unknown";
		return DateUtils.toString(item.getLastLog().getStartDate(), DEFAULT_FORMAT, null, null);
	}
	
	public String getLastFinishDate() {
		if (item.getLastLog() == null || item.getLastLog().getFinishDate() == null)
			return "";
		return DateUtils.toString(item.getLastLog().getFinishDate(), DEFAULT_FORMAT, null, null);
	}
	
	public String getLastTaskSuccess() {
		if (item.getLastLog() == null || item.getLastLog().getFinishDate() == null)
			return "";
		
		return item.getLastLog().isSuccess() ? "OK" : "ERROR";
	}
	
	public String getLastTaskReport() {
		if (item.getLastLog() == null || item.getLastLog().getStatusReport() == null)
			return "";
		
		return item.getLastLog().getStatusReport();
	}
	
	public String getLastTaskTruncatedReport() {
		String ret = getLastTaskReport();
		if (ret != null)
		{
			if (ret.length() > maxStatusReportLength)
				return ret.substring(0,maxStatusReportLength-4) + "...";
			return ret;
		}
		return "";
	}
}
