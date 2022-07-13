package com.disney.xband.xbrms.server.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class SchedulerItemParameterService 
{
	
	public static void insert(Connection conn, List<SchedulerItemParameter> parameters) throws SQLException
	{		
		if (parameters == null || parameters.size() == 0)
			return;
		
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, 
					"insert into SchedulerItemParameter (itemKey, name, sequence, value) values (?,?,?,?)");
			ps.clearBatch();
			
			for (SchedulerItemParameter param : parameters) 
			{
				ps.setString(1,param.getItemKey());
				ps.setString(2,param.getName());
				ps.setInt(3,param.getSequence());
				ps.setString(4, param.getValue());
				
				ps.addBatch();
			}
			
			ps.executeBatch();
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static void delete(Connection conn, String itemKey) throws SQLException 
	{
		PreparedStatement ps = null;
		try
		{
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, 
					"delete from SchedulerItemParameter where itemKey = ?");
			
			ps.setString(1,itemKey);
			ps.executeUpdate();	
		}
		finally
		{
			SSConnectionPool.getInstance().releaseStatement(ps);
            ps = null;
		}
	}
	
	public static void update(Connection conn, String itemKey, List<SchedulerItemParameter> parameters) throws SQLException 
	{
		
		delete(conn, itemKey);
		
		insert(conn, parameters);
	}
	
	public static List<SchedulerItemParameter> findAll(Connection conn, String itemKey) throws SQLException 
	{		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = SSConnectionPool.getInstance().getPreparedStatement(conn, 
					"select * from SchedulerItemParameter where itemKey = ? order by sequence");
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
            SSConnectionPool.getInstance().releaseResources(ps, rs);
            ps = null;
            rs = null;
		}
	}
	
	private static SchedulerItemParameter instantiate(ResultSet rs) throws SQLException 
	{
		SchedulerItemParameter param = new SchedulerItemParameter();
		param.setItemKey(rs.getString("itemKey"));
		param.setName(rs.getString("name"));
		param.setValue(rs.getString("value"));
		param.setSequence(rs.getInt("sequence"));
		return param;
	}
}
