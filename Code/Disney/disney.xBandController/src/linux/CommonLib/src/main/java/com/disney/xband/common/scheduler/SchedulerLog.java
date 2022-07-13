package com.disney.xband.common.scheduler;

import java.util.Date;

public class SchedulerLog {
	private Long id;
	private String itemKey;
	private String description;
	private String jobClassName;
	private String parameters;
	private Date startDate;
	private Date finishDate;
	private boolean success;
	private String statusReport;
	
	public SchedulerLog() {
		
	}
	
	public SchedulerLog(SchedulerItem item) {
		this.itemKey = item.getItemKey();
		this.description = item.getDescription();
		this.jobClassName = item.getJobClassName();
		StringBuffer sb = new StringBuffer();
		for (SchedulerItemParameter param : item.getParameters()) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(param.getName()).append("=").append(param.getValue());
		}
		this.parameters = sb.toString();
	}
	
	public String getItemKey() {
		return itemKey;
	}
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getStatusReport() {
		return statusReport;
	}
	public void setStatusReport(String statusReport) {
		this.statusReport = statusReport;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "id=" + id + ", itemKey=" + itemKey + ", jobClassName=" + jobClassName + ", description=" + description;
	}	
}
