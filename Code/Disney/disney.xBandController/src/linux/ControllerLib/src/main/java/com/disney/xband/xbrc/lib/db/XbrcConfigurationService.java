package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;

public class XbrcConfigurationService
{
	private static Logger logger = Logger.getLogger(XbrcConfigurationService.class);
	
	public static Collection<XbrcConfiguration> getAll(Connection conn, String orderBy) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from XbrcConfiguration order by " + (orderBy != null ? orderBy : "venuename, name"));
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Collection<XbrcConfiguration> list = new LinkedList<XbrcConfiguration>(); 
			
			while (rs.next() == true)
			{
				list.add(instantiate(rs,true));
			}
			
			return list;
		} 
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
			}
			catch (SQLException e)
			{
				logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}

			try
			{
				if (rs != null)
					rs.close();
			}
			catch(SQLException e)
			{					
				logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}
		}
	}
	
	public static Collection<XbrcConfiguration> getAllNoXml(Connection conn,  String orderBy) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select id, name, description, model, venuecode, venuename, createtime from XbrcConfiguration order by " + 
										(orderBy != null ? orderBy : "venuename, name"));
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Collection<XbrcConfiguration> list = new LinkedList<XbrcConfiguration>(); 
			
			while (rs.next() == true)
			{
				list.add(instantiate(rs,false));
			}
			
			return list;
		} 
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
			}
			catch (SQLException e)
			{
				logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}

			try
			{
				if (rs != null)
					rs.close();
			}
			catch(SQLException e)
			{					
				logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}
		}
	}
	
	public static XbrcConfiguration find(Connection conn, Long id) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from XbrcConfiguration where id = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet(); 
			
			XbrcConfiguration conf = null;
			
			if (rs.next() == true)
				conf = instantiate(rs,true);
			
			return conf;
		} 
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
			}
			catch (SQLException e)
			{
				logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}

			try
			{
				if (rs != null)
					rs.close();
			}
			catch(SQLException e)
			{					
				logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}
		}
	}
	
	public static void delete(Connection conn, Long id) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from XbrcConfiguration where id = ?");
			ps.setLong(1, id);
			ps.executeUpdate();
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
					logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
				}
			}
		}
	}
	
	public static void save(Connection conn, XbrcConfiguration conf) throws SQLException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = conn.prepareStatement("insert into XbrcConfiguration (name, description, model, xml, venuecode, venuename, createtime) values (?,?,?,?,?,?,?)",
									   Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, conf.getName());
			ps.setString(2, conf.getDescription());
			ps.setString(3, conf.getModel());
			ps.setString(4, conf.getXml());
			ps.setString(5, conf.getVenueCode());
			ps.setString(6, conf.getVenueName());
			ps.setTimestamp(7, new java.sql.Timestamp(conf.getCreateTime().getTime()));
			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				conf.setId(rs.getLong(1));
			}
		}
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
			}
			catch (SQLException e)
			{
				logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}

			try
			{
				if (rs != null)
					rs.close();
			}
			catch (SQLException e) 
			{
				logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
			}
		}
	}
	
	public static void update(Connection conn, XbrcConfiguration conf) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("update XbrcConfiguration set name=?, description=?, model=?, xml=?, venuecode=?, venuename=?, createtime=? where id = ?");

			ps.setString(1, conf.getName());
			ps.setString(2, conf.getDescription());
			ps.setString(3, conf.getModel());
			ps.setString(4, conf.getXml());
			ps.setString(5, conf.getVenueCode());
			ps.setString(6, conf.getVenueName());
			ps.setTimestamp(7, new java.sql.Timestamp(conf.getCreateTime().getTime()));
			ps.setLong(8, conf.getId());
			ps.executeUpdate();
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
					logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
				}
			}
		}
	}
	
	public static XbrcConfiguration instantiate(ResultSet rs, boolean setXml) throws Exception
	{
		XbrcConfiguration conf = new XbrcConfiguration();
				
		conf.setId(rs.getLong("id"));
		conf.setName(rs.getString("name"));
		conf.setDescription(rs.getString("description"));
		conf.setModel(rs.getString("model"));
		if (setXml)
			conf.setXml(rs.getString("xml"));
		conf.setVenueCode(rs.getString("venuecode"));
		conf.setVenueName(rs.getString("venuename"));
		conf.setCreateTime(rs.getTimestamp("createtime"));

		return conf;
	}
}
