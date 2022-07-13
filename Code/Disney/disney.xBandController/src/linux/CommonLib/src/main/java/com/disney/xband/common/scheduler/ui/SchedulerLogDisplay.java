package com.disney.xband.common.scheduler.ui;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.scheduler.SchedulerLog;

public class SchedulerLogDisplay {
	private static final String DEFAULT_FORMAT = "MMM d HH:mm:ss";
	private Integer maxStatusReportLength = 200;
	
	private SchedulerLog log;
	
	public SchedulerLogDisplay(SchedulerLog log) {
		this.log = log;
	}
	
	public SchedulerLogDisplay(SchedulerLog log, Integer maxStatusReportLength) {
		this.log = log;
		this.maxStatusReportLength = maxStatusReportLength;
	}

	public SchedulerLog getLog() {
		return log;
	}

	public void setLog(SchedulerLog log) {
		this.log = log;
	}
	
	public String getShortJobClassName() {
		return log.getJobClassName().substring(log.getJobClassName().lastIndexOf('.') + 1);
	}
	
	public String getStatusReport() {
		if (log.getStatusReport() == null)
			return "";
		
		return log.getStatusReport();
	}
	
	public String getStatusReportTruncated() {
		String ret = getStatusReport();		
		if (ret != null)
		{
			if (ret.length() > maxStatusReportLength)
				return ret.substring(0,maxStatusReportLength-4) + "...";
			return ret;
		}
		return "";
	}
	
	public String getStartDate() {
		if (log.getStartDate() == null)
			return "Unknown";
		return DateUtils.toString(log.getStartDate(), DEFAULT_FORMAT, null, null);
	}
	
	public String getFinishDate() {
		if (log.getFinishDate() == null)
			return "";
		return DateUtils.toString(log.getFinishDate(), DEFAULT_FORMAT, null, null);
	}
	
	public String getRunTime() {
		if (log.getFinishDate() == null)
			return "Unknown";
		
		Long ms = log.getFinishDate().getTime() - log.getStartDate().getTime();
		if (ms == 0)
			return "0 ms";
		
		return DateUtils.formatMillis(log.getFinishDate().getTime() - log.getStartDate().getTime()); 
	}
}
