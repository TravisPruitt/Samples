package com.disney.xband.xfpe.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.xfpe.XfpeProperties;
import com.mchange.v2.c3p0.DataSources;

public class XfpeConnectionPool extends ConnectionPool {

	private static Logger logger = Logger.getLogger(ConnectionPool.class.getName());
	private static DataSource pooled;

	static {
		String strUrl = XfpeProperties.getInstance().getProperty("db.url");
		String strUser = XfpeProperties.getInstance().getProperty("db.user");
		String strPassword = XfpeProperties.getInstance().getProperty("db.password");
		
		try {
			logger.debug("Initializing database connection...");
			logger.debug("with db.url = " + strUrl + ", db.user = " + strUser);
			
			Class.forName("com.mysql.jdbc.Driver");
			
			logger.debug("Initialized com.mysql.jdbc.Driver class");
			
			// Acquire the DataSource
            DataSource unpooled = DataSources.unpooledDataSource(strUrl,
            													 strUser,
                                                                 strPassword);
            
            HashMap<String,String> c3p0config = new HashMap<String,String>();
            
            c3p0config.put("maxPoolSize", XfpeProperties.getInstance().getProperty("c3p0.maxPoolSize"));
            c3p0config.put("maxStatements", XfpeProperties.getInstance().getProperty("c3p0.maxStatements"));
            c3p0config.put("maxStatementsPerConnection", XfpeProperties.getInstance().getProperty("c3p0.maxStatementsPerConnection"));
            c3p0config.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");
            c3p0config.put("idleConnectionTestPeriod", "600"); // seconds
            
            pooled = DataSources.pooledDataSource( unpooled, c3p0config );

		} catch(ClassNotFoundException e) {
			logger.fatal("Could not find the sql driver class. Failed to initialize connection pool", e);
		} catch (SQLException e) {
			logger.fatal("Failed to initialize connection pool", e);
		}
	}
	
	public static XfpeConnectionPool getInstance() {
		return SingletonHolder.instance;
	}	
	
	@Override
	public Connection getConnection() throws SQLException {
		return pooled.getConnection();
	}

	@Override
	public void releaseConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		}
		catch(Exception e) {
			logger.error("Failed to close connection", e);
		}
	}	
	
	private static class SingletonHolder { 
		public static final XfpeConnectionPool instance = new XfpeConnectionPool();
	}
	
	private XfpeConnectionPool() {}
}
