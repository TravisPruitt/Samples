package com.disney.xband.idms.dao.sql;

//SQL Server concrete DAO Factory implementation
import java.sql.*;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.disney.xband.idms.dao.CelebrationDAO;
import com.disney.xband.idms.dao.ConnectionStatus;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.dao.GuestDAO;
import com.disney.xband.idms.dao.PartyDAO;
import com.disney.xband.idms.dao.XBandDAO;
import com.disney.xband.idms.dao.sql.SqlCelebrationDAO;
import com.disney.xband.idms.dao.sql.SqlGuestDAO;
import com.disney.xband.idms.dao.sql.SqlPartyDAO;
import com.disney.xband.idms.dao.sql.SqlXBandDAO;
import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.health.StatusType;
import com.mchange.v2.c3p0.DataSources;

public class SqlServerDAOFactory extends DAOFactory 
{
	private static Logger logger = Logger.getLogger(SqlServerDAOFactory.class);
	private static DataSource pooled = null;
	private static String CURRENT_VERSION = "1.7.2";

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
	public ConnectionStatus getStatus() 
	{
		ConnectionStatus status = new ConnectionStatus();
		status.setStatus(StatusType.Red);
		
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet resultSet = null;
		
		try 
		{
			connection = SqlServerDAOFactory.createConnection();
			
			if (connection == null)
			{
				status.setMessage("No connection");
				status.setStatus(StatusType.Red);
			}
			else 
			{
				statement = connection.prepareCall("{call usp_schema_version_retrieve}");
				resultSet = statement.executeQuery();
				if(resultSet.next())
				{
					status.setVersion(resultSet.getString("version"));
				}
			
			
				if(!status.getVersion().startsWith(CURRENT_VERSION))
				{
					status.setStatus(StatusType.Red);
					status.setMessage("Incorrect database version. Expected version " + CURRENT_VERSION + ".xxxx but found " + status.getVersion() + "." );
				}
				else
				{
					status.setStatus(StatusType.Green);
					status.setMessage("");
				}
			}
		}
		catch( Exception e) 
		{
			status.setStatus(StatusType.Red);
			status.setMessage(e.getMessage());
		}
		finally 
		{
            if(resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception ignore) {
                }
            }

			SqlServerDAOFactory.close(statement);
			SqlServerDAOFactory.release(connection);
		}
		
		return status;
	}

	@Override
	public CelebrationDAO getCelebrationDAO() 
	{
		return new SqlCelebrationDAO();
	}

	@Override
	public GuestDAO getGuestDAO() 
	{
		return new SqlGuestDAO();
	}

	@Override
	public PartyDAO getPartyDao() 
	{
		return new SqlPartyDAO();
	}
	
	@Override
	public XBandDAO getXBandDAO() 
	{
		return new SqlXBandDAO();
	}
}
