package com.disney.xband.xbrms.server;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsStatusBo;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.common.lib.health.StatusType;
import com.mchange.v2.c3p0.DataSources;

public class SSConnectionPool extends ConnectionPool {

	private static final int DATABASE_LOGIN_TIMEOUT_MINUTES = 1;
	private static Logger logger = Logger.getLogger(ConnectionPool.class.getName());
	private static DataSource pooled;
	private static AtomicInteger usedConnections = new AtomicInteger(0);
	private static AtomicInteger usedStatements = new AtomicInteger(0);
	
	public static SSConnectionPool getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void initialize() throws IllegalArgumentException, ClassNotFoundException, SQLException
	{		
		if (logger.isInfoEnabled())
			 logger.info("Initializing database connection pool...");
		
		String strUrl = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_URL);
		if (strUrl == null || strUrl.trim().length() == 0)
		{
			throw new IllegalArgumentException("!!! Failed to initialize database connection pool. " + PkConstants.PROP_DB_URL + " not found in environment.properties. !!!");
		}
		
		String strUser = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_USER);
		if (strUser == null || strUser.trim().length() == 0)
		{
			throw new IllegalArgumentException("!!! Failed to initialize database connection pool. " + PkConstants.PROP_DB_USER + " missing from environment.properties. !!!");
		}
		
		String strPassword = ConfigProperties.getInstance().getProperty(PkConstants.PROP_DB_PASS);
		if (strPassword == null || strPassword.trim().length() == 0)
		{
			throw new IllegalArgumentException("!!! Failed to initialize database connection pool. " + PkConstants.PROP_DB_PASS + " missing from environment.properties. !!!");
		}

		logger.info("Initializing sql server connection...");
		logger.info("with url = " + strUrl + ", user = " + strUser);

		Class.forName("net.sourceforge.jtds.jdbc.Driver");

		// Acquire the DataSource
		DataSource unpooled = DataSources.unpooledDataSource(strUrl,
				strUser,
				strPassword);

		logger.info("Setting database logging timeout to " + DATABASE_LOGIN_TIMEOUT_MINUTES + " minutes.");
		unpooled.setLoginTimeout(DATABASE_LOGIN_TIMEOUT_MINUTES);

		HashMap<String,String> c3p0config = new HashMap<String,String>();

		c3p0config.put("maxPoolSize", ConfigProperties.getInstance().getProperty("nge.xconnect.xbrms.c3p0.maxPoolSize"));
		c3p0config.put("maxStatements", ConfigProperties.getInstance().getProperty("nge.xconnect.xbrms.c3p0.maxStatements"));
		c3p0config.put("maxStatementsPerConnection", ConfigProperties.getInstance().getProperty("nge.xconnect.xbrms.c3p0.maxStatementsPerConnection"));
		c3p0config.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");

		pooled = DataSources.pooledDataSource( unpooled, c3p0config );
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if (pooled == null)
			throw new SQLException("Database connection pool has not been initialized. No connection to the databse.");
		
		Connection conn = pooled.getConnection();
		
		if (logger.isTraceEnabled())
			logger.trace("Acquired DB connection. Now using " + usedConnections.incrementAndGet() + " connections.");
		
		return conn;
	}
	
	public PreparedStatement getPreparedStatement(Connection conn, String sql) throws IllegalArgumentException, SQLException
	{
		if (conn == null)
			throw new IllegalArgumentException("Need a connection to prepare a statement.");
		if (sql == null || sql.trim().isEmpty())
			throw new IllegalArgumentException("Need a query string to prepare a statement.");
		
		PreparedStatement statement = conn.prepareStatement(sql);
		
		if (logger.isTraceEnabled())
			logger.trace("Acquired DB statement. Now using " + usedStatements.incrementAndGet() + " prepared statements.");
		
		return statement;
	}
	
	public PreparedStatement getPreparedStatement(Connection conn, String sql, int autoGeneratedKeys) throws IllegalArgumentException, SQLException
	{
		if (conn == null)
			throw new IllegalArgumentException("Need a connection to prepare a statement.");
		if (sql == null || sql.trim().isEmpty())
			throw new IllegalArgumentException("Need a query string to prepare a statement.");
		
		PreparedStatement statement = conn.prepareStatement(sql, autoGeneratedKeys);
		
		if (logger.isTraceEnabled())
			logger.trace("Acquired DB statement. Now using " + usedStatements.incrementAndGet() + " prepared statements.");
		
		return statement;
	}
	
	public CallableStatement getCollableStatement(Connection conn, String sql) throws IllegalArgumentException, SQLException
	{
		if (conn == null)
			throw new IllegalArgumentException("Need a connection to prepare a statement.");
		if (sql == null || sql.trim().isEmpty())
			throw new IllegalArgumentException("Need a query string to prepare a statement.");
		
		CallableStatement statement = conn.prepareCall(sql);
		
		if (logger.isTraceEnabled())
			logger.trace("Acquired DB statement. Now using " + usedStatements.incrementAndGet() + " prepared statements.");
		
		return statement;
	}

	@Override
	public void releaseConnection(Connection conn) {
		try {
			// NOTE: conn.isClosed() is not implemented so don't use it
			if (conn != null)
			{
				conn.close();
				
				if (logger.isTraceEnabled())
					logger.trace("Released DB connection. Now using " + usedConnections.decrementAndGet() + " connections.");
			}
		}
		catch(Exception e) 
		{
			logger.error("Failed to close connection", e);
		}
	}
	
	public void releaseStatement(Statement stmt)
	{
		try
		{
			// NOTE: stmt.isClosed() is not implemented so don't use it
	        if (stmt != null)
	        {
	            stmt.close();
	                
	            if (logger.isTraceEnabled())
	                logger.trace("Released DB statement. Now using " + usedStatements.decrementAndGet() + " statements.");
	        }
		}
		catch (Exception e) 
		{
        	logger.warn("Prepared statement failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
        }
	}
	
	public void releaseResultSet(ResultSet rs)
	{
		try
		{
			if (rs != null)
                rs.close();
        }
		catch (Exception e) 
		{
        	logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
        }
	}
	
	public void releaseResources(Connection conn, Statement stmt, ResultSet rs)
	{
		releaseResultSet(rs);
		
        releaseStatement(stmt);

        releaseConnection(conn);
	}
	
	public void releaseResources(Connection conn, Statement stmt)
	{
		releaseStatement(stmt);

        releaseConnection(conn);
	}
	
	public void releaseResources(Statement stmt, ResultSet rs)
	{
		releaseResultSet(rs);
		
        releaseStatement(stmt);
	}
	
	public static String handleSqlException(SQLException e, String errorMessage)
	{
		String reason = translateSqlState(e.getSQLState());
		if (reason == null)
			reason = e.getMessage() != null ? e.getMessage() : "";

		reason += " [sql status: " + e.getSQLState() + ". " + errorMessage + "]";
		
		if (e.getSQLState() != null && e.getSQLState().startsWith("08"))
		{
			ProblemsReportBo.getInstance().setLastError(ProblemAreaType.ConnectToXbrmsDb, reason, e);
			logger.fatal(reason, e);
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Red, "No database connection [sql state: " + e.getSQLState() + "]");
		}
		else if (e.getSQLState() != null && e.getSQLState().equals("S1000")) 
		{
			ProblemsReportBo.getInstance().setLastError(ProblemAreaType.DatabaseFull, reason, e);
			logger.fatal(reason, e);
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Red, reason);
		}
		else if (e.getSQLState() != null && e.getSQLState().equals("23000"))
		{
			// integrity constraint violations are expected and are gracefully handled by the database
			if (logger.isDebugEnabled())
				logger.warn(reason, e);
		}
		else if (e.getSQLState() != null && e.getSQLState().equals("HYT00")) 
		{
			ProblemsReportBo.getInstance().setLastError(ProblemAreaType.QueryTimeout, reason, e);
			logger.error(reason, e);
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Red, reason);
		}
		else
		{
			if (logger.isDebugEnabled())
				logger.error(reason, e);
			else
				logger.error(reason);
		}
		
		return reason;
	}
	
	private static class SingletonHolder { 
		public static final SSConnectionPool INSTANCE = new SSConnectionPool();
	}
	
	private SSConnectionPool() {}
}
