package com.disney.xband.xbrc.ui.edit.action;

import java.net.ConnectException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.common.scheduler.ui.SchedulerClient;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.common.scheduler.ui.SchedulerLogDisplay;
import com.disney.xband.xbrc.ui.UISchedulerClient;
import com.disney.xband.xbrc.ui.action.BaseAction;

public class SchedulerLogsAjaxAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(SchedulerLogsAjaxAction.class);
	
	private UISchedulerClient client;	
	private boolean noXbrcConnection = false;
	private Collection<SchedulerLogDisplay> logs;
	private Integer days;
	private String itemKey; 
	private String jobClassName;
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
				
		client = new UISchedulerClient();		
	}
	
	@Override
	public String execute() throws Exception {
		super.execute();
		
		if (days == null)
			days = 2;
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days * -1);
        Date startDate = cal.getTime();
		
		try
		{			
			Collection<SchedulerLog> ret;
			
			if (itemKey != null && !itemKey.isEmpty())
				ret = client.getItemLogs(startDate, itemKey, null);
			else if (jobClassName != null && !jobClassName.isEmpty())
				ret = client.getLogs(startDate, jobClassName, null);
			else
				ret = client.getLogs(startDate, null);
			
			logs = new LinkedList<SchedulerLogDisplay>();
			
			if (ret == null)
				return SUCCESS;
			
			for (SchedulerLog log : ret) {
				logs.add(new SchedulerLogDisplay(log));
			}			
		}
		catch(ConnectException e) {
			noXbrcConnection = true;
		}
		catch(NullPointerException e) {
			addActionError("NullPointerException while processing your request. Please contact support.");
			logger.error(e);
		}
		catch(Exception e) {
			addActionError(e.getLocalizedMessage());
			logger.error(e);
		}
		
		return SUCCESS; 
	}

	public boolean isNoXbrcConnection() {
		return noXbrcConnection;
	}

	public Collection<SchedulerLogDisplay> getLogs() {
		return logs;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getItemKey() {
		return itemKey;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
}
