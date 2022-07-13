package com.disney.queries.managers;

import java.beans.PropertyVetoException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class ResourceManager {


	private static String defaultUserId = "IDMS";

	private static Logger logger = Logger.getLogger(ResourceManager.class);

	public static DataSource pooled;
	
	private static boolean connected=false; 
	
	private static boolean databaseFound;
	

	public static boolean isConnected()
	{
		return connected;
	}
	
	
	static {

		PropertyManager uip = new PropertyManager();		
		String driver = uip.getPropertyValue("databasedriver");
		String url = uip.getPropertyValue("databaseURL");
		String user = uip.getPropertyValue("databaseUser");
		String password = uip.getPropertyValue("databasePassword");


		try {
			Initialize(driver, url, user, password);
			
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			connected=false;

		} catch (SQLException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			connected=false;

		} catch (PropertyVetoException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			connected=false;

		}

	}
	
	
	
	static boolean checkConnection()
	{
		boolean retVal;
		Connection conn = null;
		try
		{
			conn = getConnection();
			
			if (conn != null)
			{
				retVal = true;
			}
			else
			{
				retVal = false;
			}

		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			logger.error("checkConnection FAILD");
			retVal = false;
		}
		finally
		{
			if  (conn != null)
			{
				releaseConnection(conn);
			}
		}
		
		
		return retVal;
	}

	static void Initialize(String driver, String url, String user, String password ) throws SQLException, ClassNotFoundException, PropertyVetoException
	{
		logger.info("\n[[[[[[[[[[[[STARTING DATABASE CONNECTION INITIALIZE]]]]]]]]]]]]]]]]]\n");
		logger.info(driver);
		logger.info(user);
		logger.info(url + "\n");
		//System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		//System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");



		try
		{
			if (pooled != null)
			{
				((ComboPooledDataSource)pooled).close();
				pooled = null;
			}
			
			
			Class.forName(driver);
			
			ComboPooledDataSource cpd = new ComboPooledDataSource();
			cpd.setDriverClass(driver);
			cpd.setPassword(password);
			cpd.setUser(user);
			cpd.setJdbcUrl(url);
			cpd.setMinPoolSize(5);
			cpd.setMaxPoolSize(150);
			cpd.setAcquireIncrement(3);
			cpd.setMaxStatements(180); 
			cpd.setCheckoutTimeout(20000);
		    cpd.setMaxConnectionAge(10000);
		    cpd.setIdleConnectionTestPeriod(8);
		    cpd.setNumHelperThreads(3);
			int poolSize = cpd.getInitialPoolSize();
			
			cpd.setInitialPoolSize(5);
			logger.info("Initial Pool Size: " + poolSize);

			// Set up a simple configuration that logs on the console.
			BasicConfigurator.configure();

			pooled = cpd; //DataSources.pooledDataSource(unpooled);
			logger.info("CONNECTION POOL INITIALIZED\n");
			connected = true; //checkConnection();
			if (!connected)
			{
				((ComboPooledDataSource)pooled).close();
				pooled = null;
			}
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			System.out.println(ex.getMessage());
			connected=false;
		}
	}

	
	// Execute an SQL Statement, returning only a boolean that it successfully ran with no exceptions.
	private boolean executeSQL(Connection conn, String sql) throws SQLException
	{
		boolean retVal = false;
		Statement statement = null;

		try
		{
			if (connected==true & pooled != null)
			{
			statement = conn.createStatement();
			statement.execute(sql);
			retVal = true;
			}
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			System.out.println(ex.getMessage());
			retVal = false;
		}
		finally
		{
            if(statement != null) {
                try {
    			    statement.close();
                }
                catch (Exception ignore) {
                }
            }
		}

		return retVal;
	}

	public static Connection getConnection() throws SQLException
	{
		Connection conn = null;
		try
		{
			if (pooled != null)
			{
				logger.info("\nCONNECTION REQUESTED\n");
				conn = pooled.getConnection();
			}
			else
			{
				logger.info("\nDATABASE CONNECTION IS NOT INTIALIZED\n");
				conn = null;
			}
			
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage());
			System.out.println(ex.getMessage());
		}

		logger.info("\nCONNECTION REQUEST COMPLETED\n");
		return conn;
	}

	public static void releaseConnection(Connection conn)
	{
		logger.info("\nCONNECTION RELEASED\n");
		try{
			if (conn != null)
				
				conn.close();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
		}

	}

	// Execute a SQL Statement against the datastore.
	public boolean ExecuteSQL(String sql)
	{
		boolean retVal = false;
		Connection conn = null;

		try
		{
			if (connected == true & pooled != null)
			{
				conn = getConnection();
				retVal = executeSQL(conn, sql);
			}
		}
		catch(Exception ex)
		{
			logger.error("NO CONNECTION TO DATABASE.");
			logger.error(ex.getMessage());
			logger.error(ex.getStackTrace());
		}
		finally
		{
			releaseConnection(conn);
		}

		return retVal;
	}


	public static boolean resetConnectionPool()
	{
		boolean retVal = false;
		
		if (pooled != null)
		{
			((ComboPooledDataSource)pooled).close();  //close the pool and set it to null.
			pooled = null;
		}
		 
		 // re-read the properties file and re-initialize the data connection.
		 PropertyManager uip = new PropertyManager();		
			String driver = uip.getPropertyValue("databasedriver");
			String url = uip.getPropertyValue("databaseURL");
			String user = uip.getPropertyValue("databaseUser");
			String password = uip.getPropertyValue("databasePassword");


			try {
				Initialize(driver, url, user, password);
				connected = true;
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				logger.error(e.getStackTrace());
				connected = false;
			} catch (SQLException e) {
				logger.error(e.getMessage());
				logger.error(e.getStackTrace());
				connected = false;
			} catch (PropertyVetoException e) {
				logger.error(e.getMessage());
				logger.error(e.getStackTrace());
				connected = false;
			}
		
		
		return retVal;
	}
	
	public static String getDefaultUser()
	{
		return defaultUserId;
	}


	public static String getDateTimeNow()
	{
		Date now = new Date();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return df.format(now);
	}

	public static Timestamp getTodayTimestamp ()
	{
		java.util.Date date = null;
		java.sql.Timestamp timeStamp = null;
		try
		{
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(new java.util.Date());
			java.sql.Date dt = new java.sql.Date(calendar.getTimeInMillis());
			java.sql.Time sqlTime=new java.sql.Time(calendar.getTime().getTime());
			java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			date = simpleDateFormat.parse(dt.toString()+" "+sqlTime.toString());
			timeStamp = new java.sql.Timestamp(date.getTime());
		}
		catch (ParseException pe)
		{
		}
		catch (Exception e)
		{
		}
		return timeStamp;
	}
	
	public static void close(PreparedStatement stmt)
	{
		try
		{
			stmt.close();
		}
		catch(Exception e)
		{
			//logger.error(e.getMessage());
		}
	}
	
	public static void close(ResultSet rs)
	{
		try
		{
			rs.close();
		}
		catch(Exception e)
		{
			//logger.error(e.getMessage());
		}
	}
	
	public static void close(CallableStatement csmt)
	{
		try
		{
			csmt.close();
		}
		catch(Exception e)
		{
			//logger.error(e.getMessage());
		}
	}
	
	public static void close(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch(Exception e)
		{
			//logger.error(e.getMessage());
		}
	}
	
	
}
