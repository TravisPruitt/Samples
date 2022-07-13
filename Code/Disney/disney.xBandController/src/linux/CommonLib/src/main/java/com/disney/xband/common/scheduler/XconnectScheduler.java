package com.disney.xband.common.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;

public class XconnectScheduler {
	
	private static Logger logger = Logger.getLogger(XconnectScheduler.class);
	
	private static class SingletonHolder { 
		public static final XconnectScheduler instance = new XconnectScheduler();
	}
	
	public static XconnectScheduler getInstance() {
		return SingletonHolder.instance;
	}
	
	private XconnectScheduler() {}

	private Scheduler scheduler;
	private HashMap<String, SchedulerItemMetadata> itemsMetadata = new HashMap<String, SchedulerItemMetadata>();
	private HashMap<String, SchedulerItem> items = new HashMap<String, SchedulerItem>();
	private XconnectSchedulerSerializer serializer;
	private Properties envProperties;
	// How long to wait for job to abort when asked to do so.
	private long jobInterruptWaitTimeMs = 2000;
	
	public void initialize(Properties envProperties, XconnectSchedulerSerializer serializer) throws SchedulerException
	{			
		this.envProperties = envProperties;		
		
		Properties quartzProps = null;
		
		InputStream is = SchedulerFactory.class.getResourceAsStream("quartz.properties");
		if (is != null) {
			try {
				quartzProps = new Properties();
				quartzProps.load(is);
			} catch (IOException e) {
				logger.warn("Failed to load quartz.properties file");
				quartzProps = null;
			}
			try {
				is.close();
			} catch (IOException e) {}
		}
		
		SchedulerFactory schedFact = null;
		
		if (quartzProps == null)
			schedFact = new StdSchedulerFactory();
		else
		{
			quartzProps.setProperty("org.quartz.scheduler.skipUpdateCheck","true");
			schedFact = new StdSchedulerFactory(quartzProps);
		}
		
		this.scheduler = schedFact.getScheduler();
		this.serializer = serializer;
	}
	
	public void registerItemMetadata(SchedulerItemMetadata metadata) {
		itemsMetadata.put(metadata.getJobClassName(), metadata);
	}	
	
	public void start() throws Exception {
		scheduler.start();
		scheduler.resumeAll();
	}
	
	public void stop() throws Exception {
		
		if (scheduler.isShutdown())
			return;
		
		scheduler.pauseAll();
		
		for (SchedulerItem item : items.values()) {			
			try {
				if (item.getEnabled())
					scheduler.interrupt(new JobKey(item.getItemKey(), item.getItemKey()));
			}
			catch(UnableToInterruptJobException e) {
				logger.warn("Scheduler was not able interrupt job " + item + " before stopping the scheduler. (" + e.getLocalizedMessage() + ")");
			}
		}
		
		// Wait for the jobs to finish running.
		boolean stillRunning;
		Date startWaitTime = new Date();
		do {
			stillRunning = false;
			for (JobExecutionContext jec : scheduler.getCurrentlyExecutingJobs()) {
				logger.info("Scheduler:stop: Waiting for job " + jec.getJobDetail().getKey().getName() + " to stop running before deleting it.");
				stillRunning = true;
			}
			
			if (stillRunning) {
				long waitTime = new Date().getTime() - startWaitTime.getTime();
				if (waitTime >= jobInterruptWaitTimeMs) {
					logger.warn("Scheduler:stop: Timed out while waiting for a jobs to abort for " + waitTime + " ms");
					break;
				}				
				Thread.sleep(100);					
			}
		} while (stillRunning);
		
		scheduler.shutdown();
	}
	
	public void standby() throws Exception {
		scheduler.standby();
	}
	
	/*
	 * Call this function to load all scheduler items from persistent storage for the first time.
	 */
	public void loadItems() throws Exception {
		List<SchedulerItem> items = serializer.findItems();
		if (items == null)
			return;
		
		for (SchedulerItem item : items) {			
			addItem(item);
		}
	}
	
	/*
	 * Call this function to reload all scheduler items from persistent storage because the items may have 
	 * been modified added or deleted.
	 */
	public void reloadItems() throws Exception {
		List<SchedulerItem> newitems = serializer.findItems();
		if (newitems == null)
			return;
		
		synchronized(items) {
			
			// find deleted items
			List<SchedulerItem> deleted = new LinkedList<SchedulerItem>();			
			for (SchedulerItem item : items.values()) {
				boolean found = false;
				for (SchedulerItem newItem: newitems) {
					if (newItem.getItemKey().equals(item.getItemKey())) {
						found = true;
						break;
					}
				}
				if (!found)
					deleted.add(item);
			}
			
			// find deleted and modified
			List<SchedulerItem> added = new LinkedList<SchedulerItem>();
			List<SchedulerItem> modified = new LinkedList<SchedulerItem>();
			
			for (SchedulerItem newItem : newitems) {
				SchedulerItem item = items.get(newItem.getItemKey());
				if (item == null) {
					added.add(newItem);
				} else if (!newItem.equals(item)) {
					modified.add(newItem);
				}
			}
			
			// process deleted
			for (SchedulerItem item : deleted) {
				deleteItem(item);
			}
			
			// process modified
			for (SchedulerItem item: modified) {
				deleteItem(item);
				addItem(item);
			}
			
			// process added
			for (SchedulerItem item: added) {
				addItem(item);
			}			
		}
	}

	public XconnectSchedulerSerializer getSerializer() {
		return serializer;
	}
	
	public void saveItem(SchedulerItem newItem, String updatedBy) throws Exception {				
		
		if (newItem.getItemKey().isEmpty())
			throw new Exception("Cannot insert or modify scheduler item because the item key is not set");
		
		SchedulerItem item = getItem(newItem.getItemKey());		
		
		if (item != null) {
			logger.info("Scheduler item " + item + " is being updated by user " + updatedBy);
			serializer.updateItem(newItem, updatedBy);
			deleteItem(item);
			// save off some statistical information from the previous item
			newItem.setLastLog(item.getLastLog());
			addItem(newItem);
		}
		else {
			logger.info("Scheduler item " + item + " is being created by user " + updatedBy);
			serializer.insertItem(newItem, updatedBy);
			addItem(newItem);
		}		
	}
	
	public void deleteItem(String itemKey, String updatedBy) throws Exception {
		SchedulerItem item = getItem(itemKey);
		if (item == null)
			throw new Exception("Item with itemKey " + itemKey + " cannot be deleted because it could not be found.");
		
		logger.info("Scheduler item " + item + " is being deleted by user " + updatedBy);		
		
		deleteItem(item);
		
		serializer.deleteItem(itemKey);		
	}

	public Collection<SchedulerItemMetadata> getItemsMetadata() {
		return itemsMetadata.values();
	}

	public Collection<SchedulerItem> getItems() {
		return items.values();
	}
	
	public SchedulerItem getItem(String key) {
		synchronized(items) {
			return items.get(key);
		}
	}
	
	public SchedulerItemMetadata getItemMetadata(String jobClassName) {
		return itemsMetadata.get(jobClassName);
	}

	public Properties getEnvProperties() {
		return envProperties;
	}

	public void setEnvProperties(Properties envProperties) {
		this.envProperties = envProperties;
	}
	
	public SchedulerLog getLastLog(String itemKey) {
		SchedulerItem item = getItem(itemKey);
		if (item == null)
			return null;
		return item.getLastLog();
	}
	
	public Collection<SchedulerLog> getLastLogs() {		
		Collection<SchedulerLog> logs = new LinkedList<SchedulerLog>();
		synchronized(items) {
			for (SchedulerItem it : items.values()) {
				if (it.getLastLog() != null)
					logs.add(it.getLastLog());
			}
		}
		return logs;
	}
	
	public Collection<SchedulerLog> get() {
		Collection<SchedulerLog> logs = new LinkedList<SchedulerLog>();
		synchronized(items) {
			for (SchedulerItem it : items.values()) {
				if (it.getLastLog() != null)
					logs.add(it.getLastLog());
			}
		}
		return logs;
	}
	
	public void deleteLogs(Date olderThan) throws Exception {
		serializer.deleteLogs(olderThan);
	}
	
	public void deleteLogs(Date olderThan, String jobClassName) throws Exception {
		serializer.deleteLogs(olderThan, jobClassName);
	}
	
	public List<SchedulerLog> findLogs(Date startDate) throws Exception {
		return serializer.findLogs(startDate);
	}
	
	public List<SchedulerLog> findLogs(Date startDate, String jobClassName) throws Exception {
		return serializer.findLogs(startDate, jobClassName);
	}
	
	public List<SchedulerLog> findItemLogs(Date startDate, String itemKey) throws Exception {
		return serializer.findItemLogs(startDate, itemKey);		
	}

	public long getJobInterruptWaitTimeMs() {
		return jobInterruptWaitTimeMs;
	}

	public void setJobInterruptWaitTimeMs(long jobInterruptWaitTimeMs) {
		this.jobInterruptWaitTimeMs = jobInterruptWaitTimeMs;
	}
	
	private Trigger getItemTrigger(SchedulerItem item) throws Exception {
		Trigger trigger;
		Date now = new Date();
		
		if (item.getRunOnceDate() != null) {
			
			if (item.getRunOnceDate().getTime() < now.getTime()) {
				throw new Exception("Ignoring scheduler item " + item + " because the runOnceDate was in the past. The item must be rescheduler in the future or deleted.");
			}
			
			trigger = (SimpleTrigger) TriggerBuilder.newTrigger() 
				    .withIdentity("trigger1", "group1")
				    .startAt(item.getRunOnceDate()) 
				    .forJob(item.getItemKey(), item.getItemKey())
				    .build();
		}
		else {
			
			trigger = TriggerBuilder.newTrigger()
				    .withIdentity(item.getItemKey(), item.getItemKey())
				    .withSchedule(CronScheduleBuilder.cronSchedule(item.getSchedulingExpression()))
				    .build();
		}
		
		return trigger;
	}
	
	private void scheduleItem(SchedulerItem item) throws Exception {
		
		SchedulerItemMetadata metadata = itemsMetadata.get(item.getJobClassName());	
		
		JobDetail job = JobBuilder.newJob(metadata.getJobClass())
			    .withIdentity(item.getItemKey(), item.getItemKey())
			    .build();
		
		job.getJobDataMap().put("schedulerItem", item);
		job.getJobDataMap().put("scheduler", this);
		
		item.setMetadata(metadata);
		
		// Set metadata for all parameters
		for (SchedulerItemParameterMetadata pm : metadata.getParameters()) {
			for (SchedulerItemParameter p : item.getParameters()) {
				if (p.getName().equals(pm.getName())) {
					p.setMetadata(pm);
				}
			}				
		}
		
		Trigger trigger = getItemTrigger(item);

		scheduler.scheduleJob(job, trigger);
	}
	
	/*
	private void rescheduleJob(SchedulerItem item) throws Exception {
		Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey(item.getItemKey(), item.getItemKey()));
		Trigger newTrigger = getItemTrigger(item);
		scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
	}
	*/
	
	
	
	public void addItem(SchedulerItem item) throws Exception {
		
		SchedulerItemMetadata metadata = itemsMetadata.get(item.getJobClassName());		
		
		if (metadata == null) {
			throw new Exception("Scheduler item metadata has not been registered for class " + item + ". Cannot load item.");
		}
		
		metadata.validateJobClassName();
		
		synchronized(items)
		{
			if (item.getEnabled())
				scheduleItem(item);
			
			items.put(item.getItemKey(), item);
		}
	}
	
	private void deleteItem(SchedulerItem item) throws Exception {
		
		try
		{
			// Need to pause scheduling of jobs while we try to interrupt and delete our job.
			scheduler.pauseJob(new JobKey(item.getItemKey(), item.getItemKey()));
			
			try {
				scheduler.interrupt(new JobKey(item.getItemKey(), item.getItemKey()));
				
				// Wait for the job to finish running
				boolean stillRunning;
				Date startWaitTime = new Date();
				do {
					stillRunning = false;
					for (JobExecutionContext jec : scheduler.getCurrentlyExecutingJobs()) {
						stillRunning = jec.getJobDetail().getKey().getName().equals(item.getItemKey());
						if (stillRunning)
							break;
					}
					if (stillRunning) {
						long waitTime = new Date().getTime() - startWaitTime.getTime();
						if (waitTime >= jobInterruptWaitTimeMs) {
							logger.warn("Scheduler:deleteItem: Timed out while waiting for a job " + item + " to abort for " + waitTime + " ms");
							break;
						}
						logger.info("Scheduler:deleteItem: Waiting for job " + item + " to stop running before deleting it. Current wait time: " + waitTime + " ms");
						Thread.sleep(100);					
					}
				} while (stillRunning);
				
			}
			catch(UnableToInterruptJobException e) {
				logger.warn("Scheduler was not able interrupt job " + item + " before deleting it. (" + e.getLocalizedMessage() + ")");
			}
		
			scheduler.deleteJob(new JobKey(item.getItemKey(), item.getItemKey()));
			
			synchronized(items) {
				items.remove(item.getItemKey());
			}
		}
		finally {
			scheduler.resumeJob(new JobKey(item.getItemKey(), item.getItemKey()));
		}
	}
}
