package com.disney.xband.xbrms.server.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class SchedulerItemService 
{
	private static final TimeZone timezone = TimeZone.getTimeZone("GMT+0");
	
	public static void insert(Connection conn, SchedulerItem item, String updatedBy) throws SQLException 
	{
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "insert into SchedulerItem (itemKey, description, jobClassName, schedulingExpression, runOnceDate, updatedBy, updatedDate, enabled)" +
									   " values (?,?,?,?,?,?,?,?)");
			
			if (item.getItemKey() == null)
				ps.setNull(1, Types.VARCHAR);
			else
				ps.setString(1,item.getItemKey());
			
			if (item.getDescription() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(2,item.getDescription());
			
			if (item.getJobClassName() == null)
				ps.setNull(3, Types.VARCHAR);
			else
				ps.setString(3,item.getJobClassName());
			
			if (item.getSchedulingExpression() == null)
				ps.setNull(4, Types.VARCHAR);
			else
				ps.setString(4,item.getSchedulingExpression());
			
			if (item.getRunOnceDate() == null)
				ps.setNull(5, Types.TIMESTAMP);
			else
				ps.setTimestamp(5, new Timestamp(item.getRunOnceDate().getTime()), GregorianCalendar.getInstance(timezone));
			
			if (updatedBy == null)
				ps.setNull(6, Types.VARCHAR);
			else
				ps.setString(6, updatedBy);
			
			Calendar cal = GregorianCalendar.getInstance(timezone);
			ps.setTimestamp(7, new Timestamp(cal.getTimeInMillis()), cal);
			
			if (item.getEnabled() == null)
				ps.setBoolean(8, Boolean.TRUE);
			else
				ps.setBoolean(8, item.getEnabled());
			
			ps.executeUpdate();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
		
		SchedulerItemParameterService.insert(conn, item.getParameters());
	}
	
	public static void update(Connection conn, SchedulerItem item, String updatedBy) throws SQLException 
	{
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("update SchedulerItem set description=?, jobClassName=?, schedulingExpression=?, runOnceDate=?, updatedBy=?, updatedDate=?, enabled=?" + 
										" where itemKey=?");
						
			if (item.getDescription() == null)
				ps.setNull(1, Types.VARCHAR);
			else
				ps.setString(1,item.getDescription());
			
			if (item.getJobClassName() == null)
				ps.setNull(2, Types.VARCHAR);
			else
				ps.setString(2,item.getJobClassName());
			
			if (item.getSchedulingExpression() == null)
				ps.setNull(3, Types.VARCHAR);
			else
				ps.setString(3,item.getSchedulingExpression());
			
			if (item.getRunOnceDate() == null)
				ps.setNull(4, Types.TIMESTAMP);
			else
				ps.setTimestamp(4, new Timestamp(item.getRunOnceDate().getTime()), GregorianCalendar.getInstance(timezone));
			
			if (updatedBy == null)
				ps.setNull(5, Types.VARCHAR);
			else
				ps.setString(5, updatedBy);
			
			Calendar cal = GregorianCalendar.getInstance(timezone);
			ps.setTimestamp(6, new Timestamp(cal.getTimeInMillis()), cal);
			
			if (item.getEnabled() == null)
				ps.setBoolean(7, Boolean.TRUE);
			else
				ps.setBoolean(7, item.getEnabled());
			
			ps.setString(8, item.getItemKey());
			
			ps.executeUpdate();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
		
		SchedulerItemParameterService.update(conn, item.getItemKey(), item.getParameters());
	}
	
	public static void delete(Connection conn, String key) throws SQLException 
	{
		if (key == null)
			return;
		
		SchedulerItemParameterService.delete(conn, key);
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("delete from SchedulerItem where itemKey = ?");
			
			ps.setString(1,key);
			ps.executeUpdate();	
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static List<SchedulerItem> findAll(Connection conn) throws SQLException {
		List<SchedulerItem> items = new LinkedList<SchedulerItem>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "select * from SchedulerItem order by jobClassName, description");
			ps.clearParameters();
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
		
		for (SchedulerItem item : items) {
			item.setParameters(SchedulerItemParameterService.findAll(conn, item.getItemKey()));
		}
		
		return items;
	}
	
	public static boolean itemExists(Connection conn, String itemKey) throws SQLException
	{
		if (itemKey == null)
			throw new IllegalArgumentException("Scheduler item key must be provided to this lookup.");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, "select count(*) as count from SchedulerItem where itemKey = ?");
			ps.clearParameters();
			ps.setString(1, itemKey);
			ps.execute();
			rs = ps.getResultSet();
			
			int count = 0;
			if (rs.next())
			{
				count = rs.getInt("count");
			}
			
			return count == 1;
		} 
		finally
		{
			SSConnectionPool.getInstance().releaseResources(ps, rs);
            ps = null;
            rs = null;
		}
	}
	
	private static SchedulerItem instantiate(ResultSet rs) throws SQLException 
	{
		SchedulerItem item = new SchedulerItem();
		item.setItemKey(rs.getString("itemKey"));
		item.setDescription(rs.getString("description"));
		item.setJobClassName(rs.getString("jobClassName"));
		item.setSchedulingExpression(rs.getString("schedulingExpression"));
		rs.getTimestamp("runOnceDate");
		if (!rs.wasNull())
			item.setRunOnceDate(rs.getTimestamp("runOnceDate"));
		item.setUpdatedBy(rs.getString("updatedBy"));
		rs.getTimestamp("updatedDate");
		if (!rs.wasNull())
			item.setUpdatedDate(rs.getDate("updatedDate", GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))));
		item.setEnabled(rs.getBoolean("enabled"));
		return item;
	}
}
