package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.StoredConfiguration;

public class StoredConfigService
{
	private static Logger logger = Logger.getLogger(StoredConfigService.class);
	
	/**
	 * Returns requested stored configuration as xml.
	 * 
	 * @param conn
	 * @param id
	 * @param internal configuration is a stored configuration created by the application as opposed
	 * 			to a user created one. The stored configuration used by the Audit system is an example
	 * 			of such configuration. Internal configuration should't be exposed to users.
	 * @param model is optional for backwards compatibility.
	 * @return
	 * @throws Exception
	 */
	public static String getAsXml(Connection conn, String name, boolean internal, String... model) throws Exception 
	{
		if (conn == null)
			return null;
		
		if (name == null || name.trim().length() == 0)
			name = "current";
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean modelProvided = model != null && model.length > 0;
		
		try
		{			
			String xml = null;
			
			StringBuffer query = new StringBuffer("SELECT * from StoredConfigurations WHERE name=? AND internal=?");
			if (modelProvided)
				query.append(" AND model=?");
			
			stmt = conn.prepareStatement(query.toString());
			
			stmt.clearParameters();
			stmt.setString(1, name);
			stmt.setBoolean(2, internal);
			
			if (modelProvided)
				stmt.setString(3, model[0]);
			
			rs = stmt.executeQuery();
			if (rs.next())
			{
				xml = rs.getString("xml");
			}
			
			return xml;
		} 
		finally 
		{
			if (rs != null)
			{
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	/**
	 * Returns requested stored configuration as xml.
	 * 
	 * @param conn
	 * @param id
	 * @param internal configuration is a stored configuration created by the application as opposed
	 * 			to a user created one. The stored configuration used by the Audit system is an example
	 * 			of such configuration. Internal configuration should't be exposed to users.
	 * @param model is optional for backwards compatibility.
	 * @return
	 * @throws Exception
	 */
	public static String getAsXml(Connection conn, int id, boolean internal, String... model) throws Exception 
	{
		if (conn == null)
			return null;
		
		if (id < 0)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean modelProvided = model != null && model.length > 0;
		
		try
		{	
			String xml = null;
			
			StringBuffer query = new StringBuffer("SELECT * from StoredConfigurations WHERE id=? AND internal=?");
			if (modelProvided)
				query.append(" AND model=?");
			
			stmt = conn.prepareStatement(query.toString());
			
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.setBoolean(2, internal);
			
			if (modelProvided)
				stmt.setString(3, model[0]);
			
			rs = stmt.executeQuery();
			if (rs.next())
			{
				xml = rs.getString("xml");
			}
			
			return xml;
		} 
		finally 
		{
			if (rs != null)
			{
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}

	/**
	 * 
	 * @param conn
	 * @param name
	 * @param internal configuration is a stored configuration created by the application as opposed
	 * 			to a user created one. The stored configuration used by the Audit system is an example
	 * 			of such configuration. Internal configuration should't be exposed to users.
	 * @return
	 * @throws Exception
	 */
	public static StoredConfiguration find(Connection conn, String name, boolean internal, String... model) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean modelProvided = model != null && model.length > 0;
		
		try
		{			
			StoredConfiguration storedConfig = null;
			
			StringBuffer query = new StringBuffer("SELECT * from StoredConfigurations WHERE name=? AND internal=?");
			if (modelProvided)
				query.append(" AND model=?");
			
			stmt = conn.prepareStatement(query.toString());
			stmt.clearParameters();
			stmt.setString(1, name);
			stmt.setBoolean(2, internal);
			
			if (modelProvided)
				stmt.setString(3, model[0]);
			
			rs = stmt.executeQuery();
			if (rs.next())
			{
				storedConfig = initStoredConfig(rs);
			}
			
			return storedConfig;
		} 
		finally 
		{
			if (rs != null)
			{
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	/**
	 * Returns requested stored configuration as xml.
	 * 
	 * @param conn
	 * @param id
	 * @param internal configuration is a stored configuration created by the application as opposed
	 * 			to a user created one. The stored configuration used by the Audit system is an example
	 * 			of such configuration. Internal configuration should't be exposed to users.
	 * @return
	 * @throws Exception
	 */
	public static StoredConfiguration find(Connection conn, int id, boolean internal, String... model) throws Exception 
	{
		if (conn == null)
			return null;
		
		if (id < 0)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean modelProvided = model != null && model.length > 0;
		
		try
		{			
			StoredConfiguration storedConfig = null;
			
			StringBuffer query = new StringBuffer("SELECT * from StoredConfigurations WHERE id=? AND internal=?");
			
			if (modelProvided)
				query.append(" AND model=?");
			
			stmt = conn.prepareStatement(query.toString());
			stmt.clearParameters();
			stmt.setInt(1, id);
			stmt.setBoolean(2, internal);
			
			if (modelProvided)
				stmt.setString(3, model[0]);
			
			rs = stmt.executeQuery();
			if (rs.next())
			{
				storedConfig = initStoredConfig(rs);
			}
			
			return storedConfig;
		} 
		finally 
		{
			if (rs != null)
			{
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param name
	 * @param internal configuration is a stored configuration created by the application as opposed
	 * 			to a user created one. The stored configuration used by the Audit system is an example
	 * 			of such configuration. Internal configuration should't be exposed to users.
	 * @return number of inserted rows. 
	 * @throws Exception
	 */
	public static int insert(Connection conn, String name, String description, String model, String xml, boolean internal) 
			throws IllegalArgumentException, SQLException, Exception
	{
		if (conn == null)
			return 0;
		
		if (name == null || name.trim().length() == 0)
			throw new IllegalArgumentException("Failed to store configuration. Stored configuration name is required.");
		
		if (xml == null || xml.trim().length() == 0)
			throw new IllegalArgumentException("Failed to store configuration. Stored configuration can not be persisted without the content.");
		
		if (model == null || model.trim().length() == 0)
			throw new IllegalArgumentException("Failed to store configuration. Stored configuration model is required.");
		
		PreparedStatement stmt = null;
		
		try
		{			
			stmt = conn.prepareStatement("INSERT INTO StoredConfigurations(name, description, model, xml, internal) VALUES (?, ?, ?, ?, ?)");
			stmt.clearParameters();
			stmt.setString(1, name);
			stmt.setString(2, description);
			stmt.setString(3, model);
			stmt.setString(4, xml);
			stmt.setBoolean(5, internal);
			
			return stmt.executeUpdate();
		} 
		finally 
		{
			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	/**
	 * Updates name, description, model, xml, and interval of already persisted stored configuration
	 * with id equal to sc.getId().
	 * 
	 * @param conn
	 * @param sc

	 * @return number of inserted rows, or zero if no records were inserted.
	 * @throws Exception
	 */
	public static int update(Connection conn, StoredConfiguration sc) throws Exception
	{
		if (conn == null)
			return 0;
		
		if (sc == null)
			return 0;
		
		if (sc.getId() < 0)
			return insert(conn, sc.getName(), sc.getDescription(), sc.getModel(), sc.getXml(), sc.isInternal());
		
		PreparedStatement stmt = null;
		
		try
		{			
			stmt = conn.prepareStatement("UPDATE StoredConfigurations SET name=?, description=?, model=?, xml=?, internal=? WHERE id=?");
			stmt.clearParameters();
			stmt.setString(1, sc.getName());
			stmt.setString(2, sc.getDescription());
			stmt.setString(3, sc.getModel());
			stmt.setString(4, sc.getXml());
			stmt.setBoolean(5, sc.isInternal());
			stmt.setLong(6, sc.getId());
			
			return stmt.executeUpdate();
		} 
		finally 
		{
			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	private static StoredConfiguration initStoredConfig(ResultSet rs) throws SQLException
	{
		if (rs == null)
			return null;
		
		StoredConfiguration sc = new StoredConfiguration();
		
		sc.setId(rs.getLong("id"));
		sc.setName(rs.getString("name"));
		sc.setModel(rs.getString("model"));
		sc.setDescription(rs.getString("description"));
		sc.setXml(rs.getString("xml"));
		sc.setInternal(rs.getBoolean("internal"));
		
		return sc;
	}
}
