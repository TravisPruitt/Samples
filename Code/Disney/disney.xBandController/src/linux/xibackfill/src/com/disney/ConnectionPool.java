package com.disney;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class ConnectionPool
{
    private static Logger logger = Logger.getLogger(ConnectionPool.class);

    private DataSource pooled = null;

    private int poolMaxSize = 0;

    private Date createDate;

    public ConnectionPool(String databaseDriver, String databaseURL, String databaseUser, String databasePassword,
                          String maxPoolSize, String maxStatements, String maxStatementsPerConnection)
    {
        createDate = new Date();
        try
        {
            if(logger.isDebugEnabled()) {
                databaseURL = databaseURL.indexOf("password=") > 0 ?
                        databaseURL.substring(databaseURL.indexOf("password=") + "password=".length()) + "***" : databaseURL;

                logger.debug("Initializing database connection pool with driver = " + databaseDriver +
                        ", database = " + databaseURL +
                        ", dbuser = " + databaseUser);
            }

            Class.forName(databaseDriver);

            // Acquire the DataSource
            DataSource unpooled = DataSources.unpooledDataSource(databaseURL,
                    databaseUser,
                    databasePassword);

            HashMap<String, String> c3p0config = new HashMap<String, String>();

            poolMaxSize = Integer.parseInt(maxPoolSize);

            c3p0config.put("maxPoolSize", maxPoolSize);
            c3p0config.put("maxStatements", maxStatements);
            c3p0config.put("maxStatementsPerConnection", maxStatementsPerConnection);
            c3p0config.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");
            c3p0config.put("idleConnectionTestPeriod", "600"); // seconds

            pooled = DataSources.pooledDataSource(unpooled, c3p0config);

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

    public boolean hardReset() {
        // require that we wait 30 seconds between resets
        if( (new Date().getTime() - createDate.getTime()) > 30000) {
            try {
                ((PooledDataSource)pooled).hardReset();
                createDate = new Date();
                return true;
            } catch (SQLException ex) {
                logger.error(ExceptionFormatter.format("Error with pool hardreset - ",ex));
                return false;
            }
        }
        else {
            logger.error("HardReset called before timeout expired");
            return false;
        }
    }


    public Date getCreateDate() {
        return createDate;
    }


    public HashMap<String, Integer> getState() {
        HashMap<String, Integer> stateMap = new HashMap<String, Integer>(5);
        try {
            PooledDataSource pds = ((PooledDataSource)pooled);
            stateMap.put("max", poolMaxSize);
            stateMap.put("busy",pds.getNumBusyConnectionsAllUsers());
            stateMap.put("total", pds.getNumConnectionsAllUsers());
            stateMap.put("idle", pds.getNumIdleConnectionsAllUsers());
            stateMap.put("orphans", pds.getNumUnclosedOrphanedConnectionsAllUsers());
            return stateMap;
        } catch (SQLException ex) {
            logger.error(ExceptionFormatter.format("Error with pool hardreset - ",ex));
            return null;
        }
    }

    // method to create connections
    public Connection createConnection()
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
