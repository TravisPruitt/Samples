package com.disney.xband.xbrms.client.action;

import java.net.ConnectException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.xbrms.client.XbrmsSchedulerClient;
import com.opensymphony.xwork2.Preparable;

public class SchedulerLogsAction extends BaseAction implements Preparable {
	
	private XbrmsSchedulerClient client;	
	private List<SchedulerItemDisplay> items;
	private Integer days = 2;
	private String itemKey; 
	private String jobClassName;
	private String serverUrl;
	
	@Override
	public void prepare() throws Exception 
	{
		client = new XbrmsSchedulerClient();
		items = new LinkedList<SchedulerItemDisplay>();
		
		try {
			Collection<SchedulerItem> itemsCol = client.getItems(serverUrl);					
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
	
	public void setServerUrl(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}
	
	public String getServerUrl()
	{
		return this.serverUrl;
	}
}
