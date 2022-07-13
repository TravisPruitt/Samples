package com.disney.xband.xbrc.ui.edit.action;

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
import com.disney.xband.common.scheduler.ui.SchedulerClient;
import com.disney.xband.common.scheduler.ui.SchedulerItemDisplay;
import com.disney.xband.xbrc.ui.UISchedulerClient;
import com.disney.xband.xbrc.ui.action.BaseAction;

public class SchedulerItemsAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(SchedulerItemsAction.class);
	
	private UISchedulerClient client;
	private List<SchedulerItemDisplay> items;
	private Collection<SchedulerItemMetadata> metadata; 
	private boolean noXbrcConnection = false;
	private Map<String, SchedulerLog> logsMap = new HashMap<String, SchedulerLog>();
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
				
		client = new UISchedulerClient();
		
		try
		{
			metadata = client.getMetadata(null);
			
			Collection<SchedulerLog> itemLogs = client.getLastLogs(null);
			if (itemLogs != null) {
				for (SchedulerLog itemLog : itemLogs)
					logsMap.put(itemLog.getItemKey(), itemLog);
			}
			
			Collection<SchedulerItem> itemsCol = client.getItems(null);		
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
}
