package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItemParameter;

public class SchedulerItemParameterService {
	
	public static void insert(Connection conn, List<SchedulerItemParameter> parameters) throws SQLException
	{
		for (SchedulerItemParameter param : parameters) {
			SchedulerItemParameterService.insert(conn, param);
		}
	}
	
	public static void insert(Connection conn, SchedulerItemParameter param) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into SchedulerItemParameter (itemKey, name, value, sequence)" +
									   " values (?,?,?,?)");
			
			ps.setString(1,param.getItemKey());
			ps.setString(2,param.getName());
			ps.setString(3,param.getValue());
			ps.setInt(4, param.getSequence());
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
	
	public static void delete(Connection conn, String itemKey) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("delete from SchedulerItemParameter where itemKey = ?");
			
			ps.setString(1,itemKey);
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
	
	public static void update(Connection conn, String itemKey, List<SchedulerItemParameter> parameters) throws SQLException {
		
		delete(conn, itemKey);
		
		if (parameters != null)
			insert(conn, parameters);
	}
	
	public static List<SchedulerItemParameter> findAll(Connection conn, String itemKey) throws SQLException {		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from SchedulerItemParameter where itemKey = ? order by sequence");
			ps.setString(1, itemKey);
			ps.execute();
			rs = ps.getResultSet();
			
			List<SchedulerItemParameter> items = new LinkedList<SchedulerItemParameter>();
			
			while (rs.next() == true)
			{
				items.add(instantiate(rs));
			}			
			
			return items;
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
	}
	
	private static SchedulerItemParameter instantiate(ResultSet rs) throws SQLException {
		SchedulerItemParameter param = new SchedulerItemParameter();
		param.setItemKey(rs.getString("itemKey"));
		param.setName(rs.getString("name"));
		param.setValue(rs.getString("value"));
		param.setSequence(rs.getInt("sequence"));
		return param;
	}
}
