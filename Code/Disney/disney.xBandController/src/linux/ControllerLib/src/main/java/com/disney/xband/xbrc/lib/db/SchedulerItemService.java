package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;

public class SchedulerItemService {
	
	public static void insert(Connection conn, SchedulerItem item, String updatedBy) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into SchedulerItem (itemKey, description, jobClassName, schedulingExpression, runOnceDate, updatedBy, updatedDate, enabled)" +
									   " values (?,?,?,?,?,?,?,?)");
			
			ps.setString(1,item.getItemKey());
			ps.setString(2,item.getDescription());
			ps.setString(3,item.getJobClassName());
			ps.setString(4,item.getSchedulingExpression());
			ps.setTimestamp(5, item.getRunOnceDate() == null ? null : new Timestamp(item.getRunOnceDate().getTime()));
			ps.setString(6, updatedBy);
			ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			ps.setBoolean(8, item.getEnabled());
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
		
		if (item.getParameters() == null)
			return;
		
		SchedulerItemParameterService.insert(conn, item.getParameters());
	}
	
	public static void update(Connection conn, SchedulerItem item, String updatedBy) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("update SchedulerItem set description=?, jobClassName=?, schedulingExpression=?, runOnceDate=?, updatedBy=?, updatedDate=?, enabled=?" + 
										" where itemKey=?");
						
			ps.setString(1,item.getDescription());
			ps.setString(2,item.getJobClassName());
			ps.setString(3,item.getSchedulingExpression());
			ps.setTimestamp(4, item.getRunOnceDate() == null ? null : new Timestamp(item.getRunOnceDate().getTime()));
			ps.setString(5, updatedBy);
			ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			ps.setBoolean(7, item.getEnabled());
			ps.setString(8,item.getItemKey());
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
		
		SchedulerItemParameterService.update(conn, item.getItemKey(), item.getParameters());
	}
	
	public static void delete(Connection conn, String key) throws SQLException {
		
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
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {					
				}
			}
		}
	}
	
	public static List<SchedulerItem> findAll(Connection conn) throws SQLException {
		List<SchedulerItem> items = new LinkedList<SchedulerItem>();
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from SchedulerItem order by jobClassName, description");
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
		
		for (SchedulerItem item : items) {
			item.setParameters(SchedulerItemParameterService.findAll(conn, item.getItemKey()));
		}
		
		return items;
	}
	
	public static SchedulerItem instantiate(ResultSet rs) throws SQLException {
		SchedulerItem item = new SchedulerItem();
		item.setItemKey(rs.getString("itemKey"));
		item.setDescription(rs.getString("description"));
		item.setJobClassName(rs.getString("jobClassName"));
		item.setSchedulingExpression(rs.getString("schedulingExpression"));
		rs.getObject("runOnceDate");
		if (!rs.wasNull())
			item.setRunOnceDate(rs.getDate("runOnceDate"));
		item.setUpdatedBy(rs.getString("updatedBy"));
		rs.getObject("updatedDate");
		if (!rs.wasNull())
			item.setUpdatedDate(rs.getDate("updatedDate"));
		item.setEnabled(rs.getBoolean("enabled"));
		return item;
	}
}
