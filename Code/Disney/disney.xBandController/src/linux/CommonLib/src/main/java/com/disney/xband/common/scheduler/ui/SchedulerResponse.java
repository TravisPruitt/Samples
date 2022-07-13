package com.disney.xband.common.scheduler.ui;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerLog;

@XmlRootElement(name="SchedulerResponse")
public class SchedulerResponse {
	
	private SchedulerMessageType type;
	private Collection<SchedulerItemMetadata> metadata;
	private Collection<SchedulerItem> items;
	private SchedulerItem item;
	private SchedulerItemMetadata itemMetadata;
	private Boolean success;
	private String errorMessage;
	private Collection<SchedulerLog> itemLogs;
	
	public SchedulerResponse() {	
	}
	
	public SchedulerResponse(SchedulerRequest req) {
		this.type = req.getType();
		this.success = true;
	}
	
	public SchedulerMessageType getType() {
		return type;
	}

	public void setType(SchedulerMessageType type) {
		this.type = type;
	}

	@XmlElementWrapper(name="SchedulerMetadataList")
	@XmlElement(name="SchedulerItemMetadata")
	public Collection<SchedulerItemMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(Collection<SchedulerItemMetadata> metadata) {
		this.metadata = metadata;
	}

	@XmlElementWrapper(name="SchedulerItems")
	@XmlElement(name="SchedulerItem")
	public Collection<SchedulerItem> getItems() {
		return items;
	}

	public void setItems(Collection<SchedulerItem> items) {
		this.items = items;
	}

	public SchedulerItem getItem() {
		return item;
	}

	public void setItem(SchedulerItem item) {
		this.item = item;
	}

	public SchedulerItemMetadata getItemMetadata() {
		return itemMetadata;
	}

	public void setItemMetadata(SchedulerItemMetadata itemMetadata) {
		this.itemMetadata = itemMetadata;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Collection<SchedulerLog> getItemLogs() {
		return itemLogs;
	}

	public void setItemLogs(Collection<SchedulerLog> itemLogs) {
		this.itemLogs = itemLogs;
	}
}
