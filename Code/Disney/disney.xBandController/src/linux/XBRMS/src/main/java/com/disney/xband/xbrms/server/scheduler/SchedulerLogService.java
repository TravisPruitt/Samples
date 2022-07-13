package com.disney.xband.xbrms.server.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class SchedulerLogService 
{
	private static final TimeZone timezone = TimeZone.getTimeZone("GMT+0");
	
	public static void insert(Connection conn, SchedulerLog log) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "insert into SchedulerLog (itemKey, description, jobClassName, parameters, startDate, finishDate, success, statusReport)" +
									   " values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			if (log.getItemKey() == null)
				ps.setNull(1, Types.VARCHAR);
			else
				ps.setString(1,log.getItemKey());
			
			if (log.getDescription() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(2,log.getDescription());
			
			if (log.getJobClassName() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(3,log.getJobClassName());
			
			if (log.getParameters() == null)
				ps.setNull(4, Types.VARCHAR);
			else
				ps.setString(4,log.getParameters());
			
			Calendar cal = GregorianCalendar.getInstance(timezone);
			
			if (log.getStartDate() == null)
				ps.setNull(5, Types.TIMESTAMP);
			else
				ps.setTimestamp(5, new Timestamp(log.getStartDate().getTime()), cal);
			
			if (log.getFinishDate() == null)
				ps.setNull(6, Types.TIMESTAMP);
			else
				ps.setTimestamp(6, new Timestamp(log.getFinishDate().getTime()), cal);
			
			ps.setBoolean(7, log.isSuccess());
			
			if (log.getStatusReport() == null)
				ps.setNull(8, Types.VARCHAR);
			else
				ps.setString(8, log.getStatusReport());
			
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				log.setId(rs.getLong(1));
			}
			rs.close();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static void update(Connection conn, SchedulerLog log) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "update SchedulerLog set itemKey=?, description=?, jobClassName=?, parameters=?, startDate=?, finishDate=?, success=?, statusReport=? " +
									   " where id = ?");
						
			if (log.getItemKey() == null)
				ps.setNull(1, Types.VARCHAR);
			else
				ps.setString(1,log.getItemKey());
			
			if (log.getDescription() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(2,log.getDescription());
			
			if (log.getJobClassName() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(3,log.getJobClassName());
			
			if (log.getParameters() == null)
				ps.setNull(4, Types.VARCHAR);
			else
				ps.setString(4,log.getParameters());
			
			
			Calendar cal = GregorianCalendar.getInstance(timezone);
			
			if (log.getStartDate() == null)
				ps.setNull(5, Types.TIMESTAMP);
			else
				ps.setTimestamp(5, new Timestamp(log.getStartDate().getTime()), cal);
			
			if (log.getFinishDate() == null)
				ps.setNull(6, Types.TIMESTAMP);
			else
				ps.setTimestamp(6, new Timestamp(log.getFinishDate().getTime()), cal);
			
			ps.setBoolean(7, log.isSuccess());
			
			if (log.getStatusReport() == null)
				ps.setNull(8, Types.VARCHAR);
			else
				ps.setString(8, log.getStatusReport());
			
			ps.setLong(9,log.getId());
			
			ps.executeUpdate();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static void delete(Connection conn, Date olderThan) throws SQLException {
		
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "delete from SchedulerLog where startDate < ?");
			
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()));
			ps.executeUpdate();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static void delete(Connection conn, Date olderThan, String jobClassName) throws SQLException {
		
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "delete from SchedulerLog where startDate < ? and jobClassName = ?");
			
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()));
			ps.setString(2, jobClassName);
			ps.executeUpdate();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static List<SchedulerLog> find(Connection conn, Date startDate) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "select * from SchedulerLog where startDate >= ? order by startDate");
			ps.clearParameters();
			
			if (startDate == null)
				ps.setNull(1, Types.TIMESTAMP);
			else
				ps.setTimestamp(1, new Timestamp(startDate.getTime()), GregorianCalendar.getInstance(timezone));
			
			ps.execute();
			rs = ps.getResultSet();
			
			while (rs.next() == true)
			{
				items.add(instantiate(rs));
			}
		} 
		finally
		{
            SSConnectionPool.getInstance().releaseResources(ps, rs);
            ps = null;
            rs = null;
		}
		
		return items;
	}
	
	public static List<SchedulerLog> find(Connection conn, Date startDate, String jobClassName) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "select * from SchedulerLog where startDate >= ? and jobClassName = ? order by startDate");
			ps.clearParameters();
			
			if (startDate == null)
				ps.setNull(1, Types.TIMESTAMP);
			else
				ps.setTimestamp(1, new Timestamp(startDate.getTime()), GregorianCalendar.getInstance(timezone));
			
			ps.setString(2, jobClassName);
			ps.execute();
			rs = ps.getResultSet();
			
			while (rs.next() == true)
			{
				items.add(instantiate(rs));
			}
		} 
		finally
		{
            SSConnectionPool.getInstance().releaseResources(ps, rs);
            ps = null;
            rs = null;
		}
		
		return items;
	}
	
	public static List<SchedulerLog> findItemLogs(Connection conn, Date startDate, String itemKey) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "select * from SchedulerLog where startDate >= ? and itemKey = ? order by startDate");
			ps.clearParameters();
			
			if (startDate == null)
				ps.setNull(1, Types.TIMESTAMP);
			else
				ps.setTimestamp(1, new Timestamp(startDate.getTime()), GregorianCalendar.getInstance(timezone));
			
			ps.setString(2, itemKey);
			ps.execute();
			rs = ps.getResultSet();
			
			while (rs.next() == true)
			{
				items.add(instantiate(rs));
			}
		} 
		finally
		{
            SSConnectionPool.getInstance().releaseResources(ps, rs);
            ps = null;
            rs = null;
		}
		
		return items;
	}
	
	public static SchedulerLog instantiate(ResultSet rs) throws SQLException 
	{
		SchedulerLog item = new SchedulerLog();
		
		item.setId(rs.getLong("id"));
		if (rs.wasNull())
			item.setId(null);
		
		item.setItemKey(rs.getString("itemKey"));
		if (rs.wasNull())
			item.setItemKey(null);
		
		item.setDescription(rs.getString("description"));
		if (rs.wasNull())
			item.setDescription(null);
		
		item.setJobClassName(rs.getString("jobClassName"));
		if (rs.wasNull())
			item.setJobClassName(null);
		
		item.setParameters(rs.getString("parameters"));
		if (rs.wasNull())
			item.setParameters(null);
		
		rs.getString("startDate");
		if (rs.wasNull())
			item.setStartDate(null);
		else
			item.setStartDate(rs.getTimestamp("startDate", GregorianCalendar.getInstance(timezone)));
		
		rs.getString("finishDate");
		if (rs.wasNull())
			item.setFinishDate(null);
		else
			item.setFinishDate(rs.getTimestamp("finishDate", GregorianCalendar.getInstance(timezone)));
		
		item.setSuccess(rs.getBoolean("success"));
		
		item.setStatusReport(rs.getString("statusReport"));
		if (rs.wasNull())
			item.setStatusReport(null);
		
		return item;
	}
}
