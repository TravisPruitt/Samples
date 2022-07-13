package com.disney.xband.common.scheduler;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The SchedulerItemMetadata describes the scheduler item. New scheduler items are 
 * created based on the information provided by this class. Some of the information
 * contained in this class is shown directly to the user at the time when the user
 * creates a new scheduler item, for example the name and the shortHtmlDescription.
 */
public class SchedulerItemMetadata 
{
	private String key;
	private String name;
	private String shortDescription;
	private String shortHtmlDescription;
	private String longHtmlDescription;
	private Class<? extends XconnectSchedulerJob> jobClass;
	private String jobClassName;
	private String defaultSchedulingExpression;
	private boolean canRunConcurrently;
	private boolean onlyOnce;
	private boolean systemOnly;
	private List<SchedulerItemParameterMetadata> parameters;	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortHtmlDescription() {
		return shortHtmlDescription;
	}
	public void setShortHtmlDescription(String shortHtmlDescription) {
		this.shortHtmlDescription = shortHtmlDescription;
	}
	public String getLongHtmlDescription() {
		return longHtmlDescription;
	}
	public void setLongHtmlDescription(String longHtmlDescription) {
		this.longHtmlDescription = longHtmlDescription;
	}
	public String getDefaultSchedulingExpression() {
		return defaultSchedulingExpression;
	}
	public void setDefaultSchedulingExpression(String defaultSchedulingExpression) {
		this.defaultSchedulingExpression = defaultSchedulingExpression;
	}
	public boolean isCanRunConcurrently() {
		return canRunConcurrently;
	}
	public void setCanRunConcurrently(boolean canRunConcurrently) {
		this.canRunConcurrently = canRunConcurrently;
	}
	
	@XmlElementWrapper(name="parameters")
	@XmlElement(name="parameter")
	public List<SchedulerItemParameterMetadata> getParameters() {
		return parameters;
	}
	public void setParameters(List<SchedulerItemParameterMetadata> parameters) {
		this.parameters = parameters;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public boolean isOnlyOnce() {
		return onlyOnce;
	}
	public void setOnlyOnce(boolean onlyOnce) {
		this.onlyOnce = onlyOnce;
	}
	public boolean isSystemOnly() {
		return systemOnly;
	}
	public void setSystemOnly(boolean systemOnly) {
		this.systemOnly = systemOnly;
	}
	
	@XmlTransient
	public Class<? extends XconnectSchedulerJob> getJobClass() {
		return jobClass;
	}
	public void setJobClass(Class<? extends XconnectSchedulerJob> jobClass) {
		this.jobClass = jobClass;
	}
	
	@SuppressWarnings("unchecked")
	public void validateJobClassName() throws ClassNotFoundException {
		this.jobClass = (Class<? extends XconnectSchedulerJob>) Class.forName(jobClassName);
	}
	
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	
	public String getKey() 
	{
		return this.key;
	}
	public void setKey(String key)
	{
		this.key = key;
	}
}
