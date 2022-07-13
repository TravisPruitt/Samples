package com.disney.xband.xbms.dao.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbms.dao.ConnectionStatus;
import com.disney.xband.xbms.dao.DAOFactory;
import com.disney.xband.xbms.dao.MessageDAO;
import com.disney.xband.xbms.dao.XbandDAO;
import com.disney.xband.xbms.dao.XbandRequestDAO;
import com.disney.xband.xbms.web.ConfigurationSettings;
import com.mchange.v2.c3p0.DataSources;

public class SqlDAOFactory extends DAOFactory 
{
	private static Logger logger = Logger.getLogger(SqlDAOFactory.class);
	private static DataSource pooled = null;

	static
	{
		// initialize the Connection Pool
		InitializeC3P0();
	}
	
	private static void InitializeC3P0()
	{
		try
		{
			logger.debug("Initializing database connection pool...");
			logger.debug("with driver = " + ConfigurationSettings.INSTANCE.getDatabaseDriver() + 
					", database = " + ConfigurationSettings.INSTANCE.getDatabaseURL() + 
					", dbuser = " + ConfigurationSettings.INSTANCE.getDatabaseUser());

			Class.forName(ConfigurationSettings.INSTANCE.getDatabaseDriver());

			logger.debug("Initialized " + ConfigurationSettings.INSTANCE.getDatabaseDriver() + " class");

			// Acquire the DataSource
			DataSource unpooled = DataSources.unpooledDataSource(ConfigurationSettings.INSTANCE.getDatabaseURL(),
					ConfigurationSettings.INSTANCE.getDatabaseUser(), 
					ConfigurationSettings.INSTANCE.getDatabasePassword());

			HashMap<String, String> c3p0config = new HashMap<String, String>();

			c3p0config.put("maxPoolSize", ConfigurationSettings.INSTANCE.getMaxPoolSize());
			c3p0config.put("maxStatements", ConfigurationSettings.INSTANCE.getMaxStatements());
			c3p0config.put("maxStatementsPerConnection",
					ConfigurationSettings.INSTANCE.getMaxStatementsPerConnection());
			c3p0config.put("com.mchange.v2.log.MLog",
					"com.mchange.v2.log.log4j.Log4jMLog");
			c3p0config.put("idleConnectionTestPeriod", "600"); // seconds

			pooled = DataSources.pooledDataSource(unpooled, c3p0config);

		}
		catch (ClassNotFoundException e)
		{
			logger.fatal(ExceptionFormatter.format(
					"Could not find the sql driver class. Failed to initialize connection pool",
					e));
			System.exit(1);
		}
		catch (SQLException e)
		{
			logger.fatal(ExceptionFormatter.format("Failed to initialize connection pool", e));
			System.exit(1);
		}

	}

	// method to create connections
	 public static Connection createConnection() 
	 {
		 try
		 {
			return pooled.getConnection();
		 }
		 catch(SQLException ex)
		 {
			logger.error(ExceptionFormatter.format("Error connecting to the database - ",ex));
		 }
		 
		 return null;
	 }
	  
	public static void release(Connection conn)
	{
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Closing database connection failed.",ex));
		}
	}

	public static void close(CallableStatement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Closing statement failed.",ex));
		}
	}

	public static void close(ResultSet resultSet)
	{
		try
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Closing result set failed.",ex));
		}
	}

	@Override
	public XbandDAO getXbandDAO() 
	{
		return new SqlXbandDAO();
	}

	@Override
	public ConnectionStatus getStatus()
	{
		ConnectionStatus status = new ConnectionStatus();
		status.setStatus(StatusType.Red);
		
		Connection connection = null;
		
		try 
		{
			connection = SqlDAOFactory.createConnection();
			
			if (connection == null)
			{
				status.setMessage("No connection");
				status.setStatus(StatusType.Red);
			}
			else 
			{
				status.setStatus(StatusType.Green);
				status.setMessage("");
			}
		}
		catch( Exception e) 
		{
			status.setStatus(StatusType.Red);
			status.setMessage(e.getMessage());
		}
		finally 
		{
			SqlDAOFactory.release(connection);
		}
		
		return status;
	}

	@Override
	public XbandRequestDAO getXbandRequestDAO()
	{
		return new SqlXbandRequestDAO();
	}

	@Override
	public MessageDAO getMessageDAO()
	{
		return new SqlMessageDAO();
	}
}
