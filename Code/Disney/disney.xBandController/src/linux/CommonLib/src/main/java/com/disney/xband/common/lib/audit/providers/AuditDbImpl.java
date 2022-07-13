package com.disney.xband.common.lib.audit.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.audit.AuditBase;
import com.disney.xband.common.lib.audit.model.DbField;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;

/**
 * This class works with both SQL Server and MySQL databases. MySQL lacks a date type with milliseconds precision.
 * Therefore, the Audit table defines two columns for dates. The first holds the date
 * in GMT in the largest precision the given database allows for. The second column holds the milliseconds value
 * from the epoch.
 * <p/>
 * All dates are converted to GMT before being persisted. The original time zone is separately preserved for reference.
 */
public class AuditDbImpl extends AuditBase {
    protected static long CLEANUP_INTERVAL_MS = 3 * 60 * 60 * 1000; // Every 3 hours

    private static final String INSERT_AUDIT = "INSERT INTO Audit " +
            "(" +
            DbField.AID + "," +
            DbField.TYPE + "," +
            DbField.CATEGORY + "," +
            DbField.APPCLASS + "," +
            DbField.APPID + "," +
            DbField.HOST + "," +
            DbField.VHOST + "," +
            DbField.UID + "," +
            DbField.SID + "," +
            DbField.DESCRIPTION + "," +
            DbField.RID + "," +
            DbField.RDATA + "," +
            DbField.DATETIME + "," +
            DbField.DATETIMEMILLIS + "," +
            DbField.SOURCETIMEZONE + "," +
            DbField.COLLECTORHOST + "," +
            DbField.CLIENT +
            ") " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    // How often to check if the audit table needs force-cleanup.
    protected long lastCleanupTime;

    public AuditDbImpl(final AuditConfig conf) {
        super(conf);
    }

    //////////////////////////////////////////////////////////////////////////////
    //                          IAudit implementation                           //
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Delete the oldest 1/3 events of the allowed maximum from the cache if "useDates" is false. Otherwise, delete
     * the events that have been stored for more then a number of days specified in the audit configuration.
     *
     * @param useDates Should the cleanup strategy be based on dates?
     */
    @Override
    public void cleanup(boolean useDates) {
        if (useDates) {
            cleanupGlobalCollector();
            return;
        }

        if ((System.currentTimeMillis() - lastCleanupTime) < CLEANUP_INTERVAL_MS) {
            return;
        }

        lastCleanupTime = System.currentTimeMillis();

        if (getRecordsNum() < this.conf.getKeepInCacheEventsMax()) {
            return;
        }

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
            pstmt = conn.prepareStatement(this.getCleanupSqlStatement());
            pstmt.clearParameters();
            pstmt.setLong(1, this.conf.getKeepInCacheEventsMax() / 3);
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

    @Override
    public boolean audit(final AuditEvent event) {
        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
            return false;
        }

        if (conn == null) {
            return false;
        }

        if (event == null) {
            return false;
        }

        PreparedStatement pstmt = null;

        int transactionIsolation = 0;
        boolean autoCommit = false;

        try {
            //open transaction
            transactionIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(INSERT_AUDIT);
            pstmt.clearParameters();

            final Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));

            storeEvent(pstmt, event, cal);

            pstmt.execute();

            conn.commit();
        }
        catch (SQLException e) {
            try {
                conn.rollback();
            }
            catch (Exception ignore) {
            }

            logger.error("Failed to write an audit event to database: " + event.toString(), e);
            return false;
        }
        catch (Exception e) {
            logger.error("Failed to write an audit event to database: " + event.toString(), e);
            return false;
        }
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            catch (Exception e) {
            }

            try {
                conn.setAutoCommit(autoCommit);
            }
            catch (Exception e) {
            }

            try {
                conn.setTransactionIsolation(transactionIsolation);
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

        return true;
    }

    @Override
    public boolean audit(final Collection<AuditEvent> events) {
        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
            return false;
        }

        if (conn == null) {
            return false;
        }

        if (events == null || events.size() == 0) {
            return false;
        }

        PreparedStatement pstmt = null;

        int transactionIsolation = 0;
        boolean autoCommit = false;

        try {
            //open transaction
            transactionIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(INSERT_AUDIT);
            pstmt.clearBatch();

            final Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));

            for (AuditEvent event : events) {
                try {
                    storeEvent(pstmt, event, cal);
                    pstmt.addBatch();
                }
                catch (SQLException e) {
                    logger.error("Failed to persist event: " + event.toString(), e);
                    return false;
                }
            }

            pstmt.executeBatch();

            conn.commit();
        }
        catch (SQLException e) {
            try {
                conn.rollback();
            }
            catch (Exception ignore) {
            }

            logger.error("Failed to write an audit event to database: ", e);
            return false;
        }
        catch (Exception e) {
            logger.error("Failed to write an audit event to database: ", e);
            return false;
        }
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            catch (Exception e) {
            }

            try {
                conn.setAutoCommit(autoCommit);
            }
            catch (Exception e) {
            }

            try {
                conn.setTransactionIsolation(transactionIsolation);
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

        return true;
    }

    //////////////////////////////////////////////////////////////////////////////
    //                     IAuditEventsProvider implementation                       //
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public List<AuditEvent> getAllEvents() {
        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
        }

        if (conn == null) {
            return null;
        }

        List<AuditEvent> events = new LinkedList<AuditEvent>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement("SELECT * FROM Audit");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    events.add(constructEvent(rs));
                }
                catch (SQLException e) {
                    logger.error("Failed to parse event with dabase id: " + rs.getLong(DbField.ID.toString()), e);
                }
            }

            return events;
        }
        catch (Exception e) {
            logger.error("Failed to read audit events from database: ", e);
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

        return events;
    }

    protected boolean reverseParams() {
        return false;
    }

    @Override
    public List<AuditEvent> getEvents(long afterEventId) {
        if (afterEventId < 0) {
            return getAllEvents();
        }

        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
        }

        if (conn == null) {
            return null;
        }

        List<AuditEvent> events = new LinkedList<AuditEvent>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(this.getSelectEventsSqlStatement(afterEventId == 0 ? true : false));
            pstmt.clearParameters();

            if(reverseParams()) {
                pstmt.setLong(1, this.conf.getBatchSizeMax());
                pstmt.setLong(2, afterEventId);
            }
            else {
                pstmt.setLong(1, afterEventId);
                pstmt.setLong(2, this.conf.getBatchSizeMax());
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    events.add(constructEvent(rs));
                }
                catch (SQLException e) {
                    logger.error("Failed to parse event with dabase id: " + rs.getLong(DbField.ID.toString()), e);
                }
            }

            return events;
        }
        catch (Exception e) {
            logger.error("Failed to read audit events from database: ", e);
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

        return events;
    }

    /**
     * Permanently removes events from the Audit table up to and including event with id <code>upToEventId</code>.
     * In case the <code>upToEventId</code> is negative, all events are removed.
     */
    @Override
    public void deleteEvents(long upToEventId) {
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
            pstmt = conn.prepareStatement("DELETE FROM Audit WHERE " + DbField.ID + " <= ?");
            pstmt.clearParameters();
            pstmt.setLong(1, upToEventId);
            int numDeleted = pstmt.executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to delete events from database: ", e);
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

    @Override
    public void deleteAllEvents() {
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
            pstmt = conn.prepareStatement("DELETE FROM Audit");
            int numDeleted = pstmt.executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to delete events from database: ", e);
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

    protected String getCleanupSqlStatement() {
        return "DELETE FROM Audit ORDER BY " + DbField.ID + " ASC limit ?";
    }

    protected String getSelectEventsSqlStatement(final boolean inclusive) {
        if (inclusive) {
            return "SELECT * FROM Audit WHERE " + DbField.ID + " >= ? limit ?";
        }
        else {
            return "SELECT * FROM Audit WHERE " + DbField.ID + " > ? limit ?";
        }
    }

    protected void cleanupGlobalCollector() {
    }

    private int getRecordsNum() {
        Connection conn = null;

        try {
            conn = conf.getConnectionPool().getConnection();
        }
        catch (Exception e) {
            logger.error("Failed to get a connection from the pool: ", e);
        }

        if (conn == null) {
            return 0;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Audit");
            rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch (Exception e) {
            logger.error("Failed to count the number of records in the audit table ", e);
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

        return 0;
    }

    /**
     * Assumes that the date is formatted as yyyy-MM-ddTHH:mm:ss.SSSZ
     *
     * @param date
     * @return
     */
    private String extractTimeZone(String date) {
        if (date == null) {
            return null;
        }

        int plus = date.lastIndexOf('+');
        if (plus > 0) {
            return date.substring(plus);
        }

        int minus = date.lastIndexOf('-');
        if (minus > 0) {
            return date.substring(minus);
        }

        return null;
    }

    private void storeEvent(PreparedStatement pstmt, AuditEvent event, Calendar cal)
            throws SQLException {
        String timezone = null;
        Date date = null;

        try {
            // convert the event's date to GMT for storage
            date = DateUtils.parseDate(
                    event.getDateTime(),
                    EXPECTED_DATE_FORMAT,
                    cal.getTimeZone());

            // save off the original time zone offset
            timezone = extractTimeZone(event.getDateTime());
        }
        catch (Exception e) {
        }

        pstmt.setLong(1, event.getAid());

        if (event.getType() == null) {
            pstmt.setNull(2, Types.VARCHAR);
        }
        else {
            pstmt.setString(2, event.getType());
        }

        if (event.getCategory() == null) {
            pstmt.setNull(3, Types.VARCHAR);
        }
        else {
            pstmt.setString(3, event.getCategory());
        }

        if (event.getAppClass() == null) {
            pstmt.setNull(4, Types.VARCHAR);
        }
        else {
            pstmt.setString(4, event.getAppClass());
        }

        if (event.getAppId() == null) {
            pstmt.setNull(5, Types.VARCHAR);
        }
        else {
            pstmt.setString(5, event.getAppId());
        }

        if (event.getHost() == null) {
            pstmt.setNull(6, Types.VARCHAR);
        }
        else {
            pstmt.setString(6, event.getHost());
        }

        if (event.getvHost() == null) {
            pstmt.setNull(7, Types.VARCHAR);
        }
        else {
            pstmt.setString(7, event.getvHost());
        }

        if (event.getUid() == null) {
            pstmt.setNull(8, Types.VARCHAR);
        }
        else {
            pstmt.setString(8, event.getUid());
        }

        if (event.getSid() == null) {
            pstmt.setNull(9, Types.VARCHAR);
        }
        else {
            pstmt.setString(9, event.getSid());
        }

        if (event.getDesc() == null) {
            pstmt.setNull(10, Types.VARCHAR);
        }
        else {
            pstmt.setString(10, event.getDesc());
        }

        if (event.getRid() == null) {
            pstmt.setNull(11, Types.VARCHAR);
        }
        else {
            pstmt.setString(11, event.getRid());
        }

        if (event.getrData() == null) {
            pstmt.setNull(12, Types.VARCHAR);
        }
        else {
            pstmt.setString(12, event.getrData());
        }

        if (date == null) {
            date = cal.getTime();
            timezone = "+0000";
            logger.warn("Original event's date failed to parse. Using time at logging to persist event: "
                    + event.toString());
        }

        // Human readable in GST, but no milliseconds in MySQL
        pstmt.setTimestamp(13, new Timestamp(date.getTime()), cal);
        // Numeric full date in GST including milliseconds
        pstmt.setLong(14, date.getTime());
        // The original timezone. Daylight savings info is still lost, but we'll let the client deal with that.
        pstmt.setString(15, timezone);

        if (event.getCollectorHost() == null) {
            pstmt.setNull(16, Types.VARCHAR);
        }
        else {
            pstmt.setString(16, event.getCollectorHost());
        }

        if (event.getClient() == null) {
            pstmt.setNull(17, Types.VARCHAR);
        }
        else {
            pstmt.setString(17, event.getClient());
        }
    }

    private AuditEvent constructEvent(ResultSet rs) throws SQLException {
        if (rs == null) {
            return null;
        }

        AuditEvent event = new AuditEvent();
        event.setId(rs.getLong(DbField.ID.toString()));
        event.setAid(rs.getLong(DbField.AID.toString()));
        if (rs.wasNull()) {
            event.setAid(-1);    // zero is a valid id
        }
        event.setType(rs.getString(DbField.TYPE.toString()));
        event.setCategory(rs.getString(DbField.CATEGORY.toString()));
        event.setAppClass(rs.getString(DbField.APPCLASS.toString()));
        if (rs.wasNull()) {
            event.setAppClass(null);
        }
        event.setAppId(rs.getString(DbField.APPID.toString()));
        if (rs.wasNull()) {
            event.setAppId(null);
        }
        event.setHost(rs.getString(DbField.HOST.toString()));
        event.setvHost(rs.getString(DbField.VHOST.toString()));
        if (rs.wasNull()) {
            event.setvHost(null);
        }
        event.setUid(rs.getString(DbField.UID.toString()));
        if (rs.wasNull()) {
            event.setUid(null);
        }
        event.setSid(rs.getString(DbField.SID.toString()));
        if (rs.wasNull()) {
            event.setSid(null);
        }
        event.setDesc(rs.getString(DbField.DESCRIPTION.toString()));
        if (rs.wasNull()) {
            event.setDesc(null);
        }
        event.setRid(rs.getString(DbField.RID.toString()));
        if (rs.wasNull()) {
            event.setRid(null);
        }
        event.setrData(rs.getString(DbField.RDATA.toString()));
        if (rs.wasNull()) {
            event.setrData(null);
        }

        long timestamp = rs.getLong(DbField.DATETIMEMILLIS.toString());
        String sourceTimeZone = rs.getString(DbField.SOURCETIMEZONE.toString());
        event.setDateTime(DateUtils.toString(
                new Date(timestamp),
                EXPECTED_DATE_FORMAT,
                Locale.ENGLISH,
                TimeZone.getTimeZone("GMT" + sourceTimeZone)));
        event.setDateTimeMillis(timestamp);
        event.setSourceTimeZone(sourceTimeZone);

        event.setCollectorHost(rs.getString(DbField.COLLECTORHOST.toString()));
        if (rs.wasNull()) {
            event.setCollectorHost(null);
        }

        event.setClient(rs.getString(DbField.CLIENT.toString()));
        if (rs.wasNull()) {
            event.setClient(null);
        }

        return event;
    }
}
