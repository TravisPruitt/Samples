package com.disney.xband.xbrms.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import com.disney.xband.xbrms.server.SSConnectionPool;
import org.apache.log4j.Logger;
import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.health.StatusType;

public class MetaDao {
	
	public static Logger logger = Logger.getLogger(MetaDao.class);
	public static Logger plogger = Logger.getLogger("performance." + MetaDao.class.toString());
	
    public static boolean wasMaster;
	
	public static long getIdentityColumn(PreparedStatement stmt) throws SQLException
	{
		ResultSet rs = null;
		long result = -1;
		
		try
		{
			rs = stmt.getGeneratedKeys();
			ResultSetMetaData rsmd = rs.getMetaData();
			if (rsmd.getColumnCount()==1)
			{
				if (rs.next()) 
				{
					result = rs.getLong(1);
				}
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return result;
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            	logger.warn("Result set failed to close. This might lead to overloading the database server, as it might take JDBC some time to free up this reasource.");
            }
        }
	}
	
	public static String getSchemaVersion() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String schemaVersion = null;
		
		try {
			conn = SSConnectionPool.getInstance().getConnection();
			
			// latest schema version
			String query = "SELECT version FROM schema_version ORDER BY schema_version_id DESC";
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.execute();
			rs = stmt.getResultSet();
			
			if (rs.next() == true)
				schemaVersion = rs.getString("version");
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving db schema version");
		}	
		catch (Exception e){
			String message = "Not able to check schema version.";
			
			logger.error(message);
			if (logger.isDebugEnabled())
				logger.error(message, e);
			else
				logger.error(message);
			
			// status message for the status report
			throw new Exception(message);
		}
        finally
        {
            SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;
        }

		return schemaVersion;
	}

	public static boolean amIMaster(int maxMasterInactivityPeriod)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		
		long begin = System.currentTimeMillis();
		long timeInTransaction = -1;
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			if (conn == null)
				return false;
			
			transactionIsolation = conn.getTransactionIsolation();
			autoCommit = conn.getAutoCommit();
			
			String ownIp = XbrmsStatusBo.getInstance().getDto().getIp();
			String ownHostname = XbrmsStatusBo.getInstance().getDto().getHostname();
			
			if (ownIp == null || ownIp.trim().isEmpty() || ownHostname == null || ownHostname.trim().isEmpty())
			{
				logger.warn("Failed to resolve my own IP address using net ip prefix " 
						+ XbrmsConfigBo.getInstance().getDto().getOwnIpPrefix()
						+ ". Please make sure that the configuration property 'ownipprefix' is set correctly. "
						+ "Otherwise this xBRMS will never become master.");
				
				return false;
			}
			
			if (logger.isTraceEnabled())
				logger.trace("Checking if I am master using ip " + ownIp + " and hostname " + ownHostname);
			
			/*
			 * Check if this xbrms is currently performing master's duties, insert if it is not yet in the database
			 */
			 
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "SELECT master FROM XbrmsHaGroup WHERE ip = ? AND hostname = ?");
			stmt.clearParameters();
			stmt.setString(1, ownIp);
			stmt.setString(2, ownHostname);
			
			stmt.execute();
			rs = stmt.getResultSet();
			
			boolean claimMasterhood = false;
			boolean master = false;
			boolean justAdded = false;
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));
			
			if (rs.next())
			{	
				master = rs.getBoolean("master");
				if (rs.wasNull())
					master = false;
				
				if (logger.isInfoEnabled())
					logger.trace("Found my master status to be " + master);
			}
			else
			{
                if(logger.isInfoEnabled())
				    logger.trace("I'm not yet in the database, inserting.");
				
                SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
                stmt = null;
				stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "INSERT INTO XbrmsHaGroup (ip, hostname, id, name, lastUpdateDate, master) VALUES (?,?,?,?,?,?)");
				stmt.clearParameters();
				stmt.setString(1, ownIp);
				stmt.setString(2, ownHostname);
				stmt.setString(3, XbrmsConfigBo.getInstance().getDto().getId());
				stmt.setString(4, XbrmsConfigBo.getInstance().getDto().getName());
				stmt.setTimestamp(5, new java.sql.Timestamp((new Date()).getTime()), cal);
				stmt.setBoolean(6, false);
				stmt.executeUpdate();
				
				justAdded = true;
				
				XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
			}
			
            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");

			/*
			 * Update the heartbeat
			 */
			 
			if (!justAdded)
			{
				SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
				stmt = null;
				stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "UPDATE XbrmsHaGroup SET id = ?, name = ?, lastUpdateDate = ? WHERE ip = ? AND hostname = ?");
				stmt.clearParameters();
				stmt.setString(1, XbrmsConfigBo.getInstance().getDto().getId());
				stmt.setString(2, XbrmsConfigBo.getInstance().getDto().getName());
				stmt.setTimestamp(3, new java.sql.Timestamp((new Date()).getTime()), cal);
				stmt.setString(4, ownIp);
				stmt.setString(5, ownHostname);
				stmt.executeUpdate();
				
				XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
			}
			
			if (master)
			{
				if(logger.isInfoEnabled())
				    logger.trace("I am performing the master duties.");
				
				return true;
			}
			
			if(logger.isInfoEnabled())
			    logger.trace("Checking if I should become the master.");
			
			/*
			 * Check if this xbrms should become a master
			 */
			 
			SSConnectionPool.getInstance().releaseStatement(stmt);
            stmt = null;
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "SELECT ip, hostname, lastUpdateDate FROM XbrmsHaGroup WHERE master = ?");
			stmt.clearParameters();
			stmt.setBoolean(1, true);
			stmt.execute();
			rs = stmt.getResultSet();
			
			int numberOfMasters = 0;	// this should either be 0 or 1
			while (rs.next())
			{
				Timestamp lastUpdated = rs.getTimestamp("lastUpdateDate", cal);
				if (rs.wasNull())
					lastUpdated = null;
				
				Date now = new Date();
				if (lastUpdated == null || (now.getTime() - lastUpdated.getTime() > (maxMasterInactivityPeriod * 1000))){
					claimMasterhood = true;
					
					if(logger.isInfoEnabled())
					    logger.info("Master " + rs.getString("ip") + "//" + rs.getString("hostname") 
					    		+ " hasn't updated its heartbeat for more than " 
					    		+ maxMasterInactivityPeriod + " seconds.");
				}
				
				numberOfMasters++;
			}
			
			if (numberOfMasters > 1)
			{
				logger.error("Detected more than one master xBRMS. Demoting all the masters.");
				
				SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
                stmt = null;
				stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "UPDATE XbrmsHaGroup SET master = ?");
				stmt.clearParameters();
				stmt.setBoolean(1, false);
				stmt.executeUpdate();
				
				logger.info("Masterhood is now for grabs.");
				
				claimMasterhood = true;
			}
			
			if (numberOfMasters == 0){
				if(logger.isInfoEnabled())
				    logger.info("No master found.");
				    
				claimMasterhood = true;
			}
			
			if (!claimMasterhood){
				if(logger.isTraceEnabled())
				    logger.trace("I am performing slave's duties.");
				
				return false;
			}
			
			/*
			 * Become master
			 */
			 
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			
			long startTransaction = System.currentTimeMillis();
			
			if(logger.isTraceEnabled())
			    logger.trace("Confirming that I should become a master, this time within a transaction.");
			    		
			/*
			 * Check again if this xbrms should become a master, this time inside a transaction
			 */
			 
			SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
            stmt = null;
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "SELECT ip, hostname, lastUpdateDate FROM XbrmsHaGroup WHERE master = ?");
			stmt.clearParameters();
			stmt.setBoolean(1, true);
			stmt.execute();
			rs = stmt.getResultSet();
			
			if (rs.next())
			{
				Timestamp lastUpdated = rs.getTimestamp("lastUpdateDate", cal);
				if (rs.wasNull())
					lastUpdated = null;
				
				Date now = new Date();
				if (lastUpdated == null || (now.getTime() - lastUpdated.getTime() > maxMasterInactivityPeriod))
					claimMasterhood = true;
			}
			
			if (claimMasterhood)
			{
				if(logger.isInfoEnabled())
				    logger.info("Attempting to become a master.");
				    		
				Date now = new Date();
				
				// un-check master flag for the old master
				if (numberOfMasters > 0)
				{
					if(logger.isTraceEnabled())
					    logger.trace("Demoting the old master(s)."); 
					    
					SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
                    stmt = null;
					stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "UPDATE XbrmsHaGroup SET master = ?");
					stmt.clearParameters();
					stmt.setBoolean(1, false);
					stmt.executeUpdate();
				}
				
				// become master
				SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
                stmt = null;
				stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "UPDATE XbrmsHaGroup SET master = ? WHERE ip = ? AND hostname = ?");
				stmt.clearParameters();
				stmt.setBoolean(1, true);
				stmt.setString(2, ownIp);
				stmt.setString(3, ownHostname);
				
				stmt.executeUpdate();
				
				if(logger.isInfoEnabled())
				    logger.info("I just became master using ip " + ownIp + " and hostname " + ownHostname); 

                if(!wasMaster && logger.isInfoEnabled()) {
    				logger.info("This xbrms became the master at " + DateUtils.format(now.getTime(), "MMM d yyyy HH:mm:ss Z"));
                }
				
				// clean up old data
				cal.add(Calendar.DAY_OF_YEAR, -1);
				SSConnectionPool.getInstance().releaseStatement(stmt);	// reusing a prepared statement object
                stmt = null;
				stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "DELETE FROM XbrmsHaGroup WHERE lastUpdateDate < ?");
				stmt.clearParameters();
				stmt.setTimestamp(1, new java.sql.Timestamp(cal.getTimeInMillis()), cal);
				stmt.execute();
                wasMaster = true;
			}
            else {
                wasMaster = false;
            }
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");

			conn.commit();
			
			timeInTransaction = System.currentTimeMillis() - startTransaction;
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return true;
		}
		catch(SQLException e)
		{
			try
			{
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			}
			catch (SQLException e1)
			{
				logger.error("While trying to become master.");
			}

            try {
			    SSConnectionPool.handleSqlException(e, "trying to become master.");
            }
            catch(Exception e1) {
            }
			
			return false;
		}
        catch(Exception e)
        {
            try
            {
                if (conn != null && !conn.getAutoCommit())
                    conn.rollback();
            }
            catch (Exception e1)
            {
                logger.error("While trying to become master.");
            }

            return false;
        }
        finally 
        {
            if (conn != null) {
                try {
                	conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
                }
                catch (Exception ignore) {}
            }
            
            SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            	
            conn = null;
            stmt = null;
            rs = null;
            
            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Inside transaction: " + timeInTransaction + "msec. Masterhood check.");
            }
        }
	}
}
