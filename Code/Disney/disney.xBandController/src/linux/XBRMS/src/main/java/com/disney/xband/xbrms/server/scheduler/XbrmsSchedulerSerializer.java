package com.disney.xband.xbrms.server.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.common.scheduler.XconnectSchedulerSerializer;
import com.disney.xband.xbrc.lib.db.DbTransactionManager;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class XbrmsSchedulerSerializer implements XconnectSchedulerSerializer {
	
	@Override
	public void insertItem(SchedulerItem item, String updatedBy) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.insert(conn, item, updatedBy);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		catch(SQLException e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			SSConnectionPool.handleSqlException(e, "inserting scheduler item into the database");
			throw e;
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void updateItem(SchedulerItem item, String updatedBy) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.update(conn, item, updatedBy);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		catch(SQLException e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			SSConnectionPool.handleSqlException(e, "updating persistd scheduler item");
			throw e;
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void deleteItem(String key) throws Exception {
		Connection conn = null;
		DbTransactionManager.DbTransaction tr = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			tr = DbTransactionManager.startTransaction(conn);
			SchedulerItemService.delete(conn, key);
			DbTransactionManager.commitTransaction(tr, conn);
		}
		
		catch(SQLException e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			SSConnectionPool.handleSqlException(e, "deleting scheduler item from the databse");
			throw e;
		}
		catch(Exception e)
		{
			DbTransactionManager.rollbackTransaction(tr, conn);
			throw e;
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public List<SchedulerItem> findItems() throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			return SchedulerItemService.findAll(conn);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void insertLog(SchedulerLog log) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			SchedulerLogService.insert(conn, log);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void updateLog(SchedulerLog log) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			SchedulerLogService.update(conn, log);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void deleteLogs(Date olderThan) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			SchedulerLogService.delete(conn, olderThan);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public void deleteLogs(Date olderThan, String jobClassName) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			SchedulerLogService.delete(conn, olderThan, jobClassName);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			return SchedulerLogService.find(conn, startDate);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate, String jobClassName) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			return SchedulerLogService.find(conn, startDate, jobClassName);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

	@Override
	public List<SchedulerLog> findItemLogs(Date startDate, String itemKey) throws Exception {
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			return SchedulerLogService.findItemLogs(conn, startDate, itemKey);
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}

}
