package com.disney.xband.xbrc.ui.edit.action;

import java.net.ConnectException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.xbrc.ui.UISchedulerClient;
import com.disney.xband.xbrc.ui.action.BaseAction;

public class SchedulerLogsAction extends BaseAction {
	
	private UISchedulerClient client;	
	private List<SchedulerItemDisplay> items;
	private Integer days = 2;
	private String itemKey; 
	private String jobClassName;
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		
		client = new UISchedulerClient();
		items = new LinkedList<SchedulerItemDisplay>();
		
		try {
			Collection<SchedulerItem> itemsCol = client.getItems(null);					
			for (SchedulerItem item : itemsCol) {
				items.add(new SchedulerItemDisplay(item));
			}
		}
		catch(ConnectException e) {
			// the xbrc is not running (expected)
		}
	}
	
	@Override
	public String execute() throws Exception {
		return super.execute();
	}

	public List<SchedulerItemDisplay> getItems() {
		return items;
	}

	public Integer getDays() {
		return days;
	}

	public String getItemKey() {
		return itemKey;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
}
