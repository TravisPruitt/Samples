package com.disney.xband.xbrms.client.action;

import java.net.ConnectException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.xbrms.client.XbrmsSchedulerClient;
import com.opensymphony.xwork2.Preparable;

public class SchedulerItemsAction extends BaseAction implements Preparable {
	
	private static Logger logger = Logger.getLogger(SchedulerItemsAction.class);
	
	private XbrmsSchedulerClient client;
	private List<SchedulerItemDisplay> items;
	private Collection<SchedulerItemMetadata> metadata; 
	private boolean noXbrcConnection = false;
	private Map<String, SchedulerLog> logsMap = new HashMap<String, SchedulerLog>();
	private String serverUrl;
	
	@Override
	public void prepare() throws Exception 
	{
		client = new XbrmsSchedulerClient();
		
		try
		{
			// user shouldn't be allowed to create new system only jobs, since they are not allowed to delete system jobs
			metadata = new LinkedList<SchedulerItemMetadata>();
			Collection<SchedulerItemMetadata> meta = client.getMetadata(serverUrl);
			if (meta != null)
				for (SchedulerItemMetadata m : meta)
					if (!m.isSystemOnly())
						metadata.add(m);
			
			Collection<SchedulerLog> itemLogs = client.getLastLogs(serverUrl);
			if (itemLogs != null) {
				for (SchedulerLog itemLog : itemLogs)
					logsMap.put(itemLog.getItemKey(), itemLog);
			}
			
			Collection<SchedulerItem> itemsCol = client.getItems(serverUrl);		
			items = new LinkedList<SchedulerItemDisplay>();
			for (SchedulerItem item : itemsCol) {
				items.add(new SchedulerItemDisplay(item));
				item.setLastLog(logsMap.get(item.getItemKey()));
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
	}
	
	@Override
	public String execute() throws Exception {
		return super.execute();
	}

	public List<SchedulerItemDisplay> getItems() {
		return items;
	}

	public Collection<SchedulerItemMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<SchedulerItemMetadata> metadata) {
		this.metadata = metadata;
	}

	public boolean isNoXbrcConnection() {
		return noXbrcConnection;
	}
	
	public String getServerUrl()
	{
		return this.serverUrl;
	}
	
	public void setServerUrl(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}
}
