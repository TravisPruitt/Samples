package com.disney.xband.common.scheduler.ui;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.scheduler.SchedulerItem;

@XmlRootElement(name="SchedulerRquest")
public class SchedulerRequest {
	private SchedulerMessageType type;
	private String itemKey;
	private SchedulerItem item;
	private String updatedBy;
	private Date startDate;
	private String jobClassName;

	public SchedulerMessageType getType() {
		return type;
	}

	public void setType(SchedulerMessageType type) {
		this.type = type;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public SchedulerItem getItem() {
		return item;
	}

	public void setItem(SchedulerItem item) {
		this.item = item;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	} 
}
