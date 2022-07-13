package com.disney.xband.common.scheduler.ui;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.common.scheduler.SchedulerLog;

public class SchedulerServer {
	
	private static Logger logger = Logger.getLogger(SchedulerServer.class);
	private XconnectScheduler scheduler;
	
	public void initialize(XconnectScheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public SchedulerResponse processRequest(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = null;
		
		try
		{				
			switch(req.getType())
			{
			case getMetadata:
				resp = processGetMetadata(req);
				break;
			case getItemlist:
				resp = processGetItemList(req);
				break;
			case getItem:
				resp = processGetItem(req);
				break;
			case saveItem:
				resp = processSaveItem(req);
				break;
			case deleteItem:
				resp = processDeleteItem(req);
				break;
			case getLastLog:
				resp = processGetLastLog(req);
				break;
			case getLastLogs:
				resp = processGetLastLogs(req);
				break;
			case getLogs:
				resp = processGetLogs(req);
				break;
			default:
				logger.warn("No handler defined for request type " + req.getType());
			}
		}
		catch(Exception e) {
			logger.error("Failed to prcess scheduler request of type " + req.getType(), e);
			if (resp == null)
				resp = new SchedulerResponse(req);
			
			resp.setSuccess(false);
			resp.setErrorMessage(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.toString());
		}
		
		if (resp == null) {
			throw new Exception("No handler defined for request type " + req.getType());
		}
		
		return resp;
	}
	
	public SchedulerResponse processGetMetadata(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = new SchedulerResponse(req);
		resp.setMetadata(scheduler.getItemsMetadata());
		return resp;
	}
	
	public SchedulerResponse processGetItemList(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = new SchedulerResponse(req);
		resp.setItems(scheduler.getItems());
		return resp;
	}
	
	public SchedulerResponse processGetLastLog(SchedulerRequest req) throws Exception {		
		SchedulerResponse resp = new SchedulerResponse(req);
		resp.setItemLogs(new LinkedList<SchedulerLog>());
		resp.getItemLogs().add(scheduler.getLastLog(req.getItemKey()));
		return resp;
	}
	
	public SchedulerResponse processGetLastLogs(SchedulerRequest req) throws Exception {		
		SchedulerResponse resp = new SchedulerResponse(req);
		resp.setItemLogs(scheduler.getLastLogs());
		return resp;
	}
	
	public SchedulerResponse processGetLogs(SchedulerRequest req) throws Exception {		
		SchedulerResponse resp = new SchedulerResponse(req);
		if (req.getItemKey() != null && !req.getItemKey().isEmpty())
			resp.setItemLogs(scheduler.findItemLogs(req.getStartDate(), req.getItemKey()));
		else if (req.getJobClassName() != null && !req.getJobClassName().isEmpty())			
			resp.setItemLogs(scheduler.findLogs(req.getStartDate(), req.getJobClassName()));
		else
			resp.setItemLogs(scheduler.findLogs(req.getStartDate()));
		return resp;
	}
	
	public SchedulerResponse processGetItem(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = new SchedulerResponse(req);
		resp.setItem(scheduler.getItem(req.getItemKey()));
		if (resp.getItem() != null)
			resp.setItemMetadata(scheduler.getItemMetadata(resp.getItem().getJobClassName()));
		return resp;
	}
	
	public SchedulerResponse processSaveItem(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = new SchedulerResponse(req);
		try {
			scheduler.saveItem(req.getItem(), req.getUpdatedBy());
		}
		catch(NullPointerException e) {
			resp.setSuccess(false);
			resp.setErrorMessage("NullPointerException in processSaveItem on the server - please contact support");
			logger.error("Failed to save scheduler item " + req.getItem(), e);
		}
		catch(Exception e) {
			resp.setSuccess(false);
			resp.setErrorMessage(e.getLocalizedMessage());
			logger.error("Failed to save scheduler item " + req.getItem(), e);
		}
		return resp;
	}
	
	public SchedulerResponse processDeleteItem(SchedulerRequest req) throws Exception {
		SchedulerResponse resp = new SchedulerResponse(req);
		try {
			scheduler.deleteItem(req.getItemKey(), req.getUpdatedBy());
		}
		catch(NullPointerException e) {
			resp.setSuccess(false);
			resp.setErrorMessage("NullPointerException in processDeleteItem on the server - please contact support");
			logger.error("Failed to delete scheduler item with itemKey " + req.getItemKey(), e);
		}
		catch(Exception e) {
			resp.setSuccess(false);
			resp.setErrorMessage(e.getLocalizedMessage());
			logger.error("Failed to delete scheduler item with itemKey " + req.getItemKey(), e);
		}
		return resp;
	}
}
