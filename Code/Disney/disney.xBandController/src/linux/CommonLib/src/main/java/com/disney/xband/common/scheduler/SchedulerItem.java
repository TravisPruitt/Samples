package com.disney.xband.common.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SchedulerItem {
	private String itemKey;
	private String description;
	private String jobClassName;
	private String schedulingExpression;
	private List<SchedulerItemParameter> parameters;
	private Date runOnceDate;
	private String updatedBy;
	private Date updatedDate;
	private Boolean enabled;
	
	private transient SchedulerItemMetadata metadata;
	private transient boolean dirty;
	private transient SchedulerLog lastLog;
	
	public String getItemKey() {
		return itemKey;
	}
	public void setItemKey(String key) {
		this.itemKey = key;
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
	public String getSchedulingExpression() {
		return schedulingExpression;
	}
	public void setSchedulingExpression(String schedulingExpression) {
		this.schedulingExpression = schedulingExpression;
	}
	public Date getRunOnceDate() {
		return runOnceDate;
	}
	public void setRunOnceDate(Date runOnceDate) {
		this.runOnceDate = runOnceDate;
	}
	
	@XmlTransient
	@JsonIgnore
	public SchedulerItemMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(SchedulerItemMetadata metadata) {
		this.metadata = metadata;
	}
	
	@XmlTransient
	@JsonIgnore
	public SchedulerLog getLastLog() {
		return lastLog;
	}
	public void setLastLog(SchedulerLog lastLog) {
		this.lastLog = lastLog;
	}
	
	@Override
	public String toString() {
		return "key=" + itemKey + ", jobClassName=" + jobClassName;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	@XmlElementWrapper(name="parameters")
	@XmlElement(name="parameter")
	public List<SchedulerItemParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<SchedulerItemParameter> parameters) {
		this.parameters = parameters;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}	
	
	private boolean objectsMismatch(Object a, Object b) {
		if (a == null && b != null)
			return true;
		if (b == null && a != null)
			return true;
		if (b == null && a == null)
			return true;
		return a.equals(b);
	}
	
	@Override
	public boolean equals(Object obj) {
		SchedulerItem other = (SchedulerItem)obj;
		if (!objectsMismatch(itemKey, other.getItemKey()))
			return false;
		
		if (!objectsMismatch(description, other.getDescription()))
			return false;
		
		if (!objectsMismatch(jobClassName, other.getJobClassName()))
			return false;
		
		if (!objectsMismatch(schedulingExpression, other.getSchedulingExpression()))
			return false;		
		
		if (!objectsMismatch(parameters, other.getParameters()))
			return false;
		
		Iterator<SchedulerItemParameter> it1 = parameters.iterator();
		Iterator<SchedulerItemParameter> it2 = other.getParameters().iterator();
		
		while(it1.hasNext() && it2.hasNext()) {
			SchedulerItemParameter p1 = it1.next();
			SchedulerItemParameter p2 = it2.next();
			if (!p1.equals(p2))
				return false;
		}
		
		// both should be empty now
		if (it1.hasNext() || it2.hasNext())
			return false;
		
		if (!objectsMismatch(runOnceDate, other.getRunOnceDate()))
			return false;
		
		if (!objectsMismatch(updatedBy, other.getUpdatedBy()))
			return false;
		
		if (!objectsMismatch(updatedDate, other.getUpdatedDate()))
			return false;
		
		if (!objectsMismatch(enabled, other.getEnabled()))
			return false;
		
		return true;
	}
}