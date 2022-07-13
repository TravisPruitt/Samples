package com.disney.xband.xbrc.scheduler;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.common.scheduler.XconnectSchedulerSerializer;
import com.disney.xband.xbrc.Controller.Controller;
import com.disney.xband.xbrc.lib.db.DbTransactionManager;
import com.disney.xband.xbrc.lib.db.SchedulerItemService;
import com.disney.xband.xbrc.lib.db.SchedulerLogService;

public class XbrcSchedulerSerializer implements XconnectSchedulerSerializer {
	
	@Override
	public void insertItem(SchedulerItem item, String updatedBy) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.insert(conn, item, updatedBy);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void updateItem(SchedulerItem item, String updatedBy) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.update(conn, item, updatedBy);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}

	}

	@Override
	public void deleteItem(String key) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.delete(conn, key);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public List<SchedulerItem> findItems() throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			return SchedulerItemService.findAll(conn);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		} 
	}

	@Override
	public void insertLog(SchedulerLog log) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			SchedulerLogService.insert(conn, log);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void updateLog(SchedulerLog log) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			SchedulerLogService.update(conn, log);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}

	}

	@Override
	public void deleteLogs(Date olderThan) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			SchedulerLogService.delete(conn, olderThan);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void deleteLogs(Date olderThan, String jobClassName) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			SchedulerLogService.delete(conn, olderThan, jobClassName);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}

	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			return SchedulerLogService.find(conn, startDate);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate, String jobClassName) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			return SchedulerLogService.find(conn, startDate, jobClassName);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public List<SchedulerLog> findItemLogs(Date startDate, String itemKey) throws Exception {
		Connection conn = null;
		try
		{
			conn = Controller.getInstance().getPooledConnection();
			return SchedulerLogService.findItemLogs(conn, startDate, itemKey);
		}
		finally
		{
			Controller.getInstance().releasePooledConnection(conn);
		}
	}

}
