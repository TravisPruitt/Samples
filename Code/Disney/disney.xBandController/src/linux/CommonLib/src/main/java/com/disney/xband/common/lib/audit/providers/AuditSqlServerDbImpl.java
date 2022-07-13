package com.disney.xband.common.lib.audit.providers;

import com.disney.xband.common.lib.audit.model.DbField;
import com.disney.xband.common.lib.audit.model.AuditConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/21/13
 * Time: 1:03 PM
 */

/**
 * SQL server-specific class.
 */
public class AuditSqlServerDbImpl extends AuditDbImpl {
    public AuditSqlServerDbImpl(final AuditConfig conf) {
        super(conf);
    }

    @Override
    protected String getSelectEventsSqlStatement(final boolean inclusive) {
        if(inclusive) {
            return "SELECT TOP(?) * FROM Audit WHERE " + DbField.ID + " >= ?";
        }
        else {
            return "SELECT TOP(?) * FROM Audit WHERE " + DbField.ID + " > ?";
        }
    }

    @Override
    protected boolean reverseParams() {
        return true;
    }

    @Override
    protected String getCleanupSqlStatement() {
        return "DELETE TOP(?) FROM Audit";
    }

    @Override
    public long getLastAuditIdForHost(final String host, final boolean isCollectorHost) {
        if((host == null) || (host.length() == 0)) {
             return -100;
        }

        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: " + e.getMessage());
        }

        if (conn == null) {
            return -100;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if(isCollectorHost) {
                pstmt = conn.prepareStatement("SELECT TOP(1) " + DbField.AID + " FROM Audit WHERE " + DbField.COLLECTORHOST + " = ? ORDER BY " + DbField.AID + " DESC");
            }
            else {
                pstmt = conn.prepareStatement("SELECT TOP(1) " + DbField.AID + " FROM Audit WHERE " + DbField.HOST + " = ? ORDER BY " + DbField.AID + " DESC");
            }

            pstmt.clearParameters();
            pstmt.setString(1, host);

            rs = pstmt.executeQuery();

            if((rs == null) || !rs.next()) {
                return -1;
            }

            return rs.getLong(DbField.AID.ordinal());
        }
        catch (Exception e) {
            logger.error("Failed to read ID of last audit event from database: " + e.getMessage());
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (Exception e) {

            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            catch (Exception e) {
            }

            if (conn != null && conf.getConnectionPool() != null) {
                try {
                    conf.getConnectionPool().releaseConnection(conn);
                    conn = null;
                }
                catch (Exception ignore) {
                }
            }
        }

        return -100;
    }

    @Override
    protected void cleanupGlobalCollector() {
        if((System.currentTimeMillis() - lastCleanupTime) < CLEANUP_INTERVAL_MS) {
            return;
        }

        lastCleanupTime = System.currentTimeMillis();

        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
        }

        if (conn == null) {
            return;
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement("DELETE FROM Audit WHERE " + DbField.DATETIME + " < getdate() - ?");
            pstmt.clearParameters();
            pstmt.setLong(1, this.conf.getKeepInGlobalDbDaysMax());
            int numDeleted = pstmt.executeUpdate();

            if (logger.isInfoEnabled()) {
                logger.info(numDeleted + " records have been deleted from the audit table during cleanup operation");
            }
        }
        catch (Exception e) {
            logger.error("Failed to cleanup the audit database: ", e);
        }
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            catch (Exception e) {
            }

            if (conn != null && conf.getConnectionPool() != null) {
                try {
                    conf.getConnectionPool().releaseConnection(conn);
                    conn = null;
                }
                catch (Exception ignore) {
                }
            }
        }
    }
}
