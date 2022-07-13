package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.xbrc.ui.UIProperties;
import com.mchange.v2.c3p0.DataSources;

public class UIConnectionPool extends ConnectionPool {

	private static Logger logger = Logger.getLogger(ConnectionPool.class.getName());
	private static DataSource pooled;
	public static AtomicInteger usedConnections = new AtomicInteger(0);

	static {
		String strUrl = UIProperties.getInstance().getProperty("nge.xconnect.xbrc.dbserver.service");
		String strUser = UIProperties.getInstance().getProperty("nge.xconnect.xbrc.dbserver.uid");
		String strPassword = UIProperties.getInstance().getProperty("nge.xconnect.xbrc.dbserver.pwd");
		
		try {
			logger.debug("Initializing database connection...");
			logger.debug("with database = " + strUrl + ", dbuser = " + strUser);
			
			Class.forName("com.mysql.jdbc.Driver");
			
			logger.debug("Initialized com.mysql.jdbc.Driver class");
			
			// Acquire the DataSource
            DataSource unpooled = DataSources.unpooledDataSource(strUrl,
            													 strUser,
                                                                 strPassword);
            
            HashMap<String,String> c3p0config = new HashMap<String,String>();
            
            c3p0config.put("maxPoolSize", UIProperties.getInstance().getProperty("nge.xconnect.xbrc.c3p0.maxPoolSize"));
            c3p0config.put("maxStatements", UIProperties.getInstance().getProperty("nge.xconnect.xbrc.c3p0.maxStatements"));
            c3p0config.put("maxStatementsPerConnection", UIProperties.getInstance().getProperty("nge.xconnect.xbrc.c3p0.maxStatementsPerConnection"));
            c3p0config.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");
            c3p0config.put("idleConnectionTestPeriod", "600"); // seconds
            
            pooled = DataSources.pooledDataSource( unpooled, c3p0config );

		} catch(ClassNotFoundException e) {
			logger.fatal("Could not find the sql driver class. Failed to initialize connection pool", e);
		} catch (SQLException e) {
			logger.fatal("Failed to initialize connection pool", e);
		}
	}
	
	public static UIConnectionPool getInstance() {
		return SingletonHolder.instance;
	}	
	
	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = pooled.getConnection();
		logger.trace("Acquired DB connection. Now using " + usedConnections.incrementAndGet() + " connections.");
		return conn;
	}

	@Override
	public void releaseConnection(Connection conn) {
		try {
			if (conn != null)
			{				
				conn.close();
				logger.trace("Released DB connection. Now using " + usedConnections.decrementAndGet() + " connections.");
			}
		}
		catch(Exception e) {
			logger.error("Failed to close connection", e);
		}
	}	
	
	private static class SingletonHolder { 
		public static final UIConnectionPool instance = new UIConnectionPool();
	}
	
	private UIConnectionPool() {}
}
