package com.disney.xband.common.scheduler.ui;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.SchedulerItemParameterMetadata;
import com.disney.xband.common.scheduler.SchedulerLog;

public abstract class SchedulerClient {
	
	private static final String UNKNOWN_USER = "unknown";
	
	private static Logger logger = Logger.getLogger(SchedulerClient.class);
	public abstract <T> SchedulerResponse send(SchedulerRequest req, T context) throws Exception;
	
	public <T> Collection<SchedulerItemMetadata> getMetadata(T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getMetadata);
		return send(req, context).getMetadata();
	}
	
	public <T> Collection<SchedulerItem> getItems(T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getItemlist);
		return send(req, context).getItems();
	}
	
	public <T> SchedulerItem getItem(String itemKey, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getItem);
		req.setItemKey(itemKey);
		SchedulerResponse resp = send(req, context);
		if (resp.getItem() != null)
		{
			// set the metadata for this item
			resp.getItem().setMetadata(resp.getItemMetadata());		
			
			// set the metadata for each parameter
			if (resp.getItemMetadata() != null) {
				// The parameters and their metadata are sorted in the same order
				Iterator<SchedulerItemParameter> it = resp.getItem().getParameters().iterator();
				Iterator<SchedulerItemParameterMetadata> itm = resp.getItemMetadata().getParameters().iterator();
				while (it.hasNext() && itm.hasNext()) {
					SchedulerItemParameter param = it.next();
					SchedulerItemParameterMetadata metadata = itm.next();
					
					// this should never happen, but..
					if (!param.getName().equals(metadata.getName())) {
						logger.error("Parameter " + param.getName() + " and metadata mismatch " + metadata.getName() + " for scheduler item " + resp.getItem());
						break;
					}
					
					param.setMetadata(metadata);
				}
			}
		}
		
		return resp.getItem();
	}
	
	public <T> void saveItem(SchedulerItem item, String updatedBy, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.saveItem);
		req.setItem(item);
		
		if (item.getRunOnceDate() == null)
			checkCronExpression(item.getSchedulingExpression());
		
		if (updatedBy == null || updatedBy.isEmpty())
			updatedBy = UNKNOWN_USER;
		req.setUpdatedBy(updatedBy);
		SchedulerResponse resp = send(req, context);
		
		if (resp.getSuccess() != true)
			throw new Exception(resp.getErrorMessage());
	}
	
	public <T> void deleteItem(String itemKey, String updatedBy, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.deleteItem);
		req.setItemKey(itemKey);
		
		if (updatedBy == null || updatedBy.isEmpty())
			updatedBy = UNKNOWN_USER;
		req.setUpdatedBy(updatedBy);
		SchedulerResponse resp = send(req, context);
		
		if (resp.getSuccess() != true)
			throw new Exception(resp.getErrorMessage());
	}
	
	public <T> SchedulerLog getLastLog(String itemKey, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getLastLog);
		req.setItemKey(itemKey);
		
		SchedulerResponse resp = send(req, context);		
		if (resp.getItemLogs() == null || resp.getItemLogs().isEmpty())
			return null;
		
		for (SchedulerLog itemLog : resp.getItemLogs()) {
			return itemLog;
		}
				
		return null;
	}
	
	public <T> Collection<SchedulerLog> getLastLogs(T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getLastLogs);
		
		SchedulerResponse resp = send(req, context);		
		return resp.getItemLogs();
	}
	
	public <T> Collection<SchedulerLog> getItemLogs(Date startDate, String itemKey, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getLogs);
		req.setItemKey(itemKey);
		req.setStartDate(startDate);
		
		SchedulerResponse resp = send(req, context);		
		return resp.getItemLogs();
	}
	
	public <T> Collection<SchedulerLog> getLogs(Date startDate, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getLogs);
		req.setStartDate(startDate);
		
		SchedulerResponse resp = send(req, context);		
		return resp.getItemLogs();
	}
	
	public <T> Collection<SchedulerLog> getLogs(Date startDate, String jobClassName, T context) throws Exception {
		SchedulerRequest req = new SchedulerRequest();
		req.setType(SchedulerMessageType.getLogs);		
		req.setStartDate(startDate);
		req.setJobClassName(jobClassName);
		
		SchedulerResponse resp = send(req, context);		
		return resp.getItemLogs();
	}
	
	public <T> void checkCronExpression(String expression) throws Exception {
		if (expression == null || expression.isEmpty())
			throw new Exception("The cron expression cannot be empty");
		
		@SuppressWarnings("unused")
		CronExpression exp = new CronExpression(expression);
		
		// The quartz guys allow for too many characters. The maximum should be 7.
		String[] groups = expression.split("\\s");
		if (groups.length > 7)
			throw new Exception("The expression is too long");
	}
	
	public Date getNextExecutionTime(String expression) throws Exception {
		if (expression == null || expression.isEmpty())
			throw new Exception("The cron expression cannot be empty");
		
		CronExpression exp = new CronExpression(expression);
		return exp.getNextValidTimeAfter(new Date());
	}
}
