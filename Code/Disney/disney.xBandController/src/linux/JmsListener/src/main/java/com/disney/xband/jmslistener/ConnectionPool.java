package com.disney.xband.jmslistener;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.configuration.ConnectionPoolConfiguration;
import com.mchange.v2.c3p0.DataSources;

public class ConnectionPool 
{
	
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	
	private DataSource pooled = null;
	
	private ConnectionPoolConfiguration configuration;
	
	private boolean connected;
	
	public ConnectionPool(ConnectionPoolConfiguration configuration)
	{
		this.configuration = configuration;
		this.connected = false;
	}
	
	public boolean isConnected()
	{
		return this.connected;
	}
	
	public void Connect()
	{
		try
		{
			logger.debug("Initializing database connection pool...");
			logger.debug("with driver = " + this.configuration.getDatabaseDriver() + 
					", database = " + this.configuration.getDatabaseService() + 
					", dbuser = " + this.configuration.getDatabaseUser());
	
			Class.forName(this.configuration.getDatabaseDriver());
	
			// Acquire the DataSource
			DataSource unpooled = DataSources.unpooledDataSource(this.configuration.getDatabaseService(),
					this.configuration.getDatabaseUser(), 
					this.configuration.getDatabasePassword());
	
			HashMap<String, String> c3p0config = new HashMap<String, String>();
	
			c3p0config.put("maxPoolSize", 
					String.valueOf(this.configuration.getMaxPoolSize()));
			c3p0config.put("maxStatements", 
					this.configuration.getMaxStatements());
			c3p0config.put("maxStatementsPerConnection",
					this.configuration.getMaxStatementsPerConnection());
			c3p0config.put("com.mchange.v2.log.MLog",
					"com.mchange.v2.log.log4j.Log4jMLog");
			c3p0config.put("idleConnectionTestPeriod", "600"); // seconds
	
			pooled = DataSources.pooledDataSource(unpooled, c3p0config);
			
			this.connected = true;
	
		}
		catch (ClassNotFoundException e)
		{
			logger.fatal(
					ExceptionFormatter.format("Could not find the sql driver class. Failed to initialize connection pool",e));
		}
		catch (SQLException e)
		{
			logger.fatal(
					ExceptionFormatter.format("Failed to initialize connection pool", e));
		}
	}
	
	 // method to create connections
	 public Connection createConnection() 
	 {
		 Connection connection = null;
		 try
		 {
			connection =  pooled.getConnection();
			this.connected = true;
		 }
		 catch(SQLException ex)
		 {
			logger.error(ExceptionFormatter.format("Error connecting to the database - ",ex));
			this.connected = false;
		 }
		 
		 return connection;
	 }
	  
	public void release(Connection conn)
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

	public void close(CallableStatement statement)
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

	public void close(ResultSet resultSet)
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
}
