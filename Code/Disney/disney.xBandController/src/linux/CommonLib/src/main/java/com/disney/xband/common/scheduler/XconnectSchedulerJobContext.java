package com.disney.xband.common.scheduler;

import java.util.Date;
import java.util.LinkedHashMap;

public class XconnectSchedulerJobContext {
	private String key;
	private String description;
	private LinkedHashMap<String, SchedulerItemParameter> parameters;
	private SchedulerLog log;
	
	public XconnectSchedulerJobContext(SchedulerItem item) {
		this.key = item.getItemKey();
		this.description = item.getDescription();
		this.parameters = new LinkedHashMap<String,SchedulerItemParameter>();
		for (SchedulerItemParameter param: item.getParameters()) {
			SchedulerItemParameter clone = (SchedulerItemParameter)param.clone();
			this.parameters.put(param.getName(), clone);
		}
		log = new SchedulerLog(item);
		log.setStartDate(new Date());
		log.setSuccess(true);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public LinkedHashMap<String, SchedulerItemParameter> getParameters() {
		return parameters;
	}
	public void setParameters(LinkedHashMap<String, SchedulerItemParameter> parameters) {
		this.parameters = parameters;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SchedulerLog getLog() {
		return log;
	}
	public void setLog(SchedulerLog log) {
		this.log = log;
	}
}
