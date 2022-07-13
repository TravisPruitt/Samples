package com.disney.xband.common.scheduler;

import java.util.Date;
import java.util.List;

public interface XconnectSchedulerSerializer {
	
	public void insertItem(SchedulerItem item, String updatedBy) throws Exception;
	public void updateItem(SchedulerItem item, String updatedBy) throws Exception;	
	public void deleteItem(String key) throws Exception;
	public List<SchedulerItem> findItems() throws Exception;;
	
	public void insertLog(SchedulerLog log) throws Exception;;
	public void updateLog(SchedulerLog log) throws Exception;;
	public void deleteLogs(Date olderThan) throws Exception;; 
	public void deleteLogs(Date olderThan, String jobClassName) throws Exception;;
	
	public List<SchedulerLog> findLogs(Date startDate) throws Exception;;
	public List<SchedulerLog> findLogs(Date startDate, String jobClassName) throws Exception;
	public List<SchedulerLog> findItemLogs(Date startDate, String itemKey) throws Exception;
}
