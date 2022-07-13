package com.disney.xband.common.lib.audit.interceptors.pwcache;

import com.disney.xband.common.lib.audit.IAuditConnectionPool;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 6/12/13
 * Time: 11:13 AM
 */
public class LogonCacheDao {
    private static Logger logger = Logger.getLogger(LogonCacheDao.class);

    public static LogonCacheDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Map<String, LogonCacheItem> getAll(final IAuditConnectionPool pool, final int validDays) {
        //try {Thread.sleep(15000);}catch(Exception e) {};

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String query = "SELECT * FROM PwHash WHERE lastUpdated > getdate() - ?";
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setInt(1, validDays);
            stmt.execute();
            rs = stmt.getResultSet();

            final Map<String, LogonCacheItem> results = new HashMap<String, LogonCacheItem>();

            while (rs.next()) {
                final String uid = rs.getString("uid");
                final LogonCacheItem item =
                    new LogonCacheItem(uid, rs.getString("token"), rs.getDate("lastUpdated").getTime());

                results.put(uid, item);
            }

            return results;
        }
        catch (Exception e) {
            logger.error("Failed to get PwHash items from the database: " + e.getMessage());
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                }
            }

            if (conn != null) {
                try {
                    pool.releaseConnection(conn);
                    conn = null;
                }
                catch (Exception ignore) {
                }
            }
        }

        return new HashMap<String, LogonCacheItem>();
    }

    public LogonCacheItem getOne(final IAuditConnectionPool pool, final String uid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String query = "SELECT * FROM PwHash WHERE uid = ?";
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setString(1, uid);
            stmt.execute();
            rs = stmt.getResultSet();

            final Collection<LogonCacheItem> results = new ArrayList<LogonCacheItem>();

            while (rs.next()) {
                    return new LogonCacheItem(rs.getString("uid"), rs.getString("token"), rs.getDate("lastUpdated").getTime());
            }
        }
        catch (Exception e) {
            logger.error("Failed to PwHash from the database for uid " + uid + ": " + e.getMessage());
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                }
            }

            if (conn != null) {
                try {
                    pool.releaseConnection(conn);
                    conn = null;
                }
                catch (Exception ignore) {
                }
            }
        }

        return null;
    }

    public boolean saveSerToken(final IAuditConnectionPool pool, final String uid, final String token) {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement cstmt = null;
        ResultSet rs = null;

        int transactionIsolation = 0;
        boolean autoCommit = false;

        try {
            conn = pool.getConnection();

            // open transaction
            transactionIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            LogonCacheItem item = this.getOne(pool, uid);

            if(item == null) {
                String query = "INSERT INTO PwHash (uid,token,lastUpdated) VALUES (?,?,?)";
                stmt = conn.prepareStatement(query, Statement.NO_GENERATED_KEYS);
                stmt.clearParameters();
                stmt.setString(1, uid);
                stmt.setString(2, token);
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }
            else {
                String sSQL = "update PwHash set token=?,lastUpdated=? where uid=?";
                stmt = conn.prepareStatement(sSQL);
                stmt.clearParameters();
                stmt.setString(1, token);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setString(3, uid);
            }

            stmt.executeUpdate();
            conn.commit();

            return true;
        }
        catch (Exception e) {
            logger.error("Failed to update PwHash in the database for uid " + uid + ": " + e.getMessage());

            if (conn != null) {
                try {
                    conn.rollback();
                }
                catch (Exception ignore) {
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                }
            }

            if (cstmt != null) {
                try {
                    cstmt.close();
                }
                catch (Exception e) {
                }
            }

            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
                }
                catch (Exception ignore) {
                }

                try {
                    pool.releaseConnection(conn);
                    conn = null;
                }
                catch (Exception ignore) {
                }
            }
        }

        return false;
    }

    private LogonCacheDao() {
    }

    private static class SingletonHolder {
        private final static LogonCacheDao INSTANCE = new LogonCacheDao();
    }
}
