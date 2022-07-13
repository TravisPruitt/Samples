package com.disney.xband.xbrms.server.model;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.disney.xband.xbrms.server.SSConnectionPool;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 3/12/13
 * Time: 6:29 PM
 */
public class XbrmsDao {
    private static Logger logger = Logger.getLogger(XbrmsDao.class);

    public static XbrmsDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private XbrmsDao() {
    }

    private static class SingletonHolder {
        private final static XbrmsDao INSTANCE = new XbrmsDao();
    }

    public Collection<XbrmsDto> getAll() {
        final Collection<XbrmsDto> results = new ArrayList<XbrmsDto>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            final String query = "SELECT * FROM ParksConfig";
            stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
            stmt.execute();
            rs = stmt.getResultSet();

            XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");

            while (rs.next()) {
                final XbrmsDto dto = instantiateParksConfig(rs);

                if (dto != null) {
                    results.add(dto);
                }
            }
        }
        catch (SQLException e) {
        	SSConnectionPool.handleSqlException(e, "Retrieving all from ParksConfig");
        }
        finally {
            if (conn != null) {
                try {
                    SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
                    conn = null;
                    stmt = null;
                    rs = null;
                }
                catch (Exception ignore) {
                }
            }
        }

        return results;
    }

    public synchronized boolean delete(XbrmsDto ob) {
        return this.delete(ob, true);
    }

    public synchronized boolean insert(XbrmsDto ob) {
        if (ob == null) {
            return false;
        }

        this.delete(ob, false);

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement dstmt = null;
        ResultSet rs = null;

        int transactionIsolation = 0;
        boolean autoCommit = false;

        try {
            conn = SSConnectionPool.getInstance().getConnection();
            transactionIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            dstmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "DELETE FROM ParksConfig WHERE url=?");
            dstmt.clearParameters();
            dstmt.setString(1, ob.getUrl());
            dstmt.execute();

            stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, 
                    "INSERT INTO ParksConfig" +
                    " (url, description, alias, isGlobal)" +
                    " VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.clearParameters();
            stmt.setString(1, ob.getUrl());
            stmt.setString(2, ob.getDesc());
            stmt.setString(3, ob.getFqdnHostAlias());
            stmt.setInt(4, ob.isGlobal() ? 1 : 0);

            final int count = stmt.executeUpdate();
            conn.commit();
            XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");

            return count == 1;
        }
        catch (SQLException e) 
        {
        	try
        	{
	        	if (conn != null && !conn.getAutoCommit())
					conn.rollback();
	        	}
        	catch (Exception ignore){}

        	SSConnectionPool.handleSqlException(e, "Persisting xBRMS park server " + ob.getUrl());
            
            return false;
        }
        finally 
        {
            SSConnectionPool.getInstance().releaseResultSet(rs);
            rs = null;
            SSConnectionPool.getInstance().releaseStatement(stmt);
            stmt = null;
            SSConnectionPool.getInstance().releaseStatement(dstmt);
            dstmt = null;

            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
                }
                catch (Exception ignore) {
                }
            }

            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
        }
    }

    private synchronized boolean delete(XbrmsDto ob, boolean logErrors) {
        if (XbrmsUtils.isEmpty(ob.getUrl())) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SSConnectionPool.getInstance().getConnection();

            stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "DELETE FROM ParksConfig WHERE url=?");
            stmt.clearParameters();
            stmt.setString(1, ob.getUrl());
            final boolean deleted = stmt.execute();
            
            XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");

            return deleted;
        }
        catch (SQLException e) 
        {
            if (logErrors) {
            	SSConnectionPool.handleSqlException(e, "Deleting persisted park service: " + ob.getUrl());
            }
        }
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt);
            conn = null;
            stmt = null;
        }

        return false;
    }

    private XbrmsDto instantiateParksConfig(ResultSet rs) throws SQLException {
        final XbrmsDto dto = ConfigProperties.getInstance().createXbrmsServer(
            rs.getString("url"),
            rs.getString("alias"),
            rs.getString("description"),
            null, rs.getInt("isGlobal") != 0,
            -1);

        return dto;
    }
}
