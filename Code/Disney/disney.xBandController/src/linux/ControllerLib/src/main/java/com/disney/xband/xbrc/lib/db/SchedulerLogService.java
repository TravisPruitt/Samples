package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerLog;

public class SchedulerLogService {
	
	public static void insert(Connection conn, SchedulerLog log) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into SchedulerLog (itemKey, description, jobClassName, parameters, startDate, finishDate, success, statusReport)" +
									   " values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1,log.getItemKey());
			ps.setString(2,log.getDescription());
			ps.setString(3,log.getJobClassName());
			ps.setString(4,log.getParameters());
			ps.setTimestamp(5, log.getStartDate() == null ? null : new Timestamp(log.getStartDate().getTime()));
			ps.setTimestamp(6, log.getFinishDate() == null ? null : new Timestamp(log.getFinishDate().getTime()));
			ps.setBoolean(7, log.isSuccess());
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
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {					
				}
			}
		}
	}
	
	public static void update(Connection conn, SchedulerLog log) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("update SchedulerLog set itemKey=?, description=?, jobClassName=?, parameters=?, startDate=?, finishDate=?, success=?, statusReport=? " +
									   " where id = ?");
						
			ps.setString(1,log.getItemKey());
			ps.setString(2,log.getDescription());
			ps.setString(3,log.getJobClassName());
			ps.setString(4,log.getParameters());
			ps.setTimestamp(5, log.getStartDate() == null ? null : new Timestamp(log.getStartDate().getTime()));
			ps.setTimestamp(6, log.getFinishDate() == null ? null : new Timestamp(log.getFinishDate().getTime()));
			ps.setBoolean(7, log.isSuccess());
			ps.setString(8, log.getStatusReport());
			ps.setLong(9,log.getId());
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {					
				}
			}
		}
	}
	
	public static void delete(Connection conn, Date olderThan) throws SQLException {
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("delete from SchedulerLog where startDate < ?");
			
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()));
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {					
				}
			}
		}
	}
	
	public static void delete(Connection conn, Date olderThan, String jobClassName) throws SQLException {
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("delete from SchedulerLog where startDate < ? and jobClassName = ?");
			
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()));
			ps.setString(2, jobClassName);
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {					
				}
			}
		}
	}
	
	public static List<SchedulerLog> find(Connection conn, Date startDate) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from SchedulerLog where startDate >= ? order by startDate");
			ps.clearParameters();
			ps.setTimestamp(1, new Timestamp(startDate.getTime()));
			ps.execute();
			rs = ps.getResultSet();
			
			while (rs.next() == true)
			{
				items.add(instantiate(rs));
			}
		} 
		finally
		{
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
		}
		
		return items;
	}
	
	public static List<SchedulerLog> find(Connection conn, Date startDate, String jobClassName) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from SchedulerLog where startDate >= ? and jobClassName = ? order by startDate");
			ps.clearParameters();
			ps.setTimestamp(1, new Timestamp(startDate.getTime()));
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
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
		}
		
		return items;
	}
	
	public static List<SchedulerLog> findItemLogs(Connection conn, Date startDate, String itemKey) throws SQLException {
		List<SchedulerLog> items = new LinkedList<SchedulerLog>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from SchedulerLog where startDate >= ? and itemKey = ? order by startDate");
			ps.clearParameters();
			ps.setTimestamp(1, new Timestamp(startDate.getTime()));
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
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
		}
		
		return items;
	}
	
	public static SchedulerLog instantiate(ResultSet rs) throws SQLException {
		SchedulerLog item = new SchedulerLog();
		item.setId(rs.getLong("id"));
		item.setItemKey(rs.getString("itemKey"));
		item.setDescription(rs.getString("description"));
		item.setJobClassName(rs.getString("jobClassName"));
		item.setParameters(rs.getString("parameters"));
		Timestamp ts = rs.getTimestamp("startDate");
		item.setStartDate(ts == null ? null : new Date(ts.getTime()));
		ts = rs.getTimestamp("finishDate");
		item.setFinishDate(ts == null ? null : new Date(ts.getTime()));
		item.setSuccess(rs.getBoolean("success"));
		item.setStatusReport(rs.getString("statusReport"));
		return item;
	}
}
