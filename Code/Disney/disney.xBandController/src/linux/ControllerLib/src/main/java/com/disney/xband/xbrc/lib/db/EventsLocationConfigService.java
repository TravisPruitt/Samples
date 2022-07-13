package com.disney.xband.xbrc.lib.db;

import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EventsLocationConfigService {

    public static Map<Long,EventsLocationConfig> getAllMapped(Connection conn) throws Exception
    {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            ps = conn.prepareStatement("select * from EventsLocationConfig");
            ps.clearParameters();
            ps.execute();
            rs = ps.getResultSet();

            Map<Long,EventsLocationConfig> map = new HashMap<Long,EventsLocationConfig>();

            while (rs.next() == true)
            {
                EventsLocationConfig ob = instantiate(rs);
                map.put(ob.getLocationId(), ob);
            }

            return map;
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
        }
    }

    public static EventsLocationConfig find(Connection conn, Long id) throws Exception
    {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            ps = conn.prepareStatement("select * from EventsLocationConfig where locationId = ?");
            ps.clearParameters();
            ps.setLong(1, id);
            ps.execute();
            rs = ps.getResultSet();

            EventsLocationConfig obj = null;

            if (rs.next() == true)
                obj = instantiate(rs);

            return obj;
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
        }
    }

    public static void delete(Connection conn, Long locationId) throws SQLException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement("delete from EventsLocationConfig WHERE locationId = ?");
            ps.clearParameters();
            ps.setLong(1, locationId);
            ps.executeUpdate();
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e){}
            }
        }
    }

    public static void deleteAll(Connection conn) throws SQLException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement("delete from EventsLocationConfig");
            ps.executeUpdate();
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e){}
            }
        }
    }

    public static void save(Connection conn, EventsLocationConfig obj) throws SQLException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement("insert into EventsLocationConfig (" +
                    "locationId, " +
                    "abandonmentTimeout, " +
                    "castmemberDetectDelay, " +
                    "puckDetectDelay, " +
                    "guestDetectDelay, " +
                    "confidenceDelta, " +
                    "locationEventRatio, " +
                    "chirpRate, " +
                    "enableConfProcessing, " +
                    "sendTapToJMS, " +
                    "sendConfToJMS, " +
                    "sendDeltaConfToJMS, " +
                    "sendTapToHTTP )" +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            ps.setLong(1, obj.getLocationId());
            ps.setInt(2, obj.getAbandonmentTimeout());
            ps.setLong(3, obj.getCastMemberDetectDelay());
            ps.setLong(4, obj.getPuckDetectDelay());
            ps.setLong(5, obj.getGuestDetectDelay());
            ps.setInt(6, obj.getConfidenceDelta());
            ps.setInt(7, obj.getLocationEventRatio());
            ps.setLong(8, obj.getChirpRate());
            ps.setBoolean(9, obj.isEnableConfProcessing());
            ps.setBoolean(10, obj.isSendTapToJMS());
            ps.setBoolean(11, obj.isSendConfToJMS());
            ps.setBoolean(12, obj.isSendDeltaConfToJMS());
            ps.setBoolean(13, obj.isSendTapToHTTP());

            ps.executeUpdate();
            ps.close();
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }
        }
    }

    public static void update(Connection conn, EventsLocationConfig obj) throws SQLException
    {
        PreparedStatement ps = null;

        try
        {
            ps = conn.prepareStatement("update EventsLocationConfig set " +
                    "abandonmentTimeout = ?, " +
                    "castmemberDetectDelay = ?, " +
                    "puckDetectDelay = ?, " +
                    "guestDetectDelay = ?, " +
                    "confidenceDelta = ?, " +
                    "locationEventRatio = ?, " +
                    "chirpRate = ?, " +
                    "enableConfProcessing = ?, " +
                    "sendTapToJMS = ?, " +
                    "sendConfToJMS = ?, " +
                    "sendDeltaConfToJMS = ?, " +
                    "sendTapToHTTP = ? " +
                    "where locationId = ?");

            ps.setInt(1, obj.getAbandonmentTimeout());
            ps.setLong(2, obj.getCastMemberDetectDelay());
            ps.setLong(3, obj.getPuckDetectDelay());
            ps.setLong(4, obj.getGuestDetectDelay());
            ps.setInt(5, obj.getConfidenceDelta());
            ps.setInt(6, obj.getLocationEventRatio());
            ps.setLong(7, obj.getChirpRate());
            ps.setBoolean(8, obj.isEnableConfProcessing());
            ps.setBoolean(9, obj.isSendTapToJMS());
            ps.setBoolean(10, obj.isSendConfToJMS());
            ps.setBoolean(11, obj.isSendDeltaConfToJMS());
            ps.setBoolean(12, obj.isSendTapToHTTP());
            ps.setLong(13, obj.getLocationId());

            ps.executeUpdate();
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }
        }
    }

    public static EventsLocationConfig instantiate(ResultSet rs) throws Exception
    {
        EventsLocationConfig obj = new EventsLocationConfig();
        obj.setLocationId(rs.getLong("locationId"));
        obj.setAbandonmentTimeout(rs.getInt("abandonmentTimeout"));
        obj.setCastMemberDetectDelay(rs.getLong("castmemberDetectDelay"));
        obj.setPuckDetectDelay(rs.getLong("puckDetectDelay"));
        obj.setGuestDetectDelay(rs.getLong("guestDetectDelay"));
        obj.setConfidenceDelta(rs.getInt("confidenceDelta"));
        obj.setLocationEventRatio(rs.getInt("locationEventRatio"));
        obj.setChirpRate(rs.getInt("chirpRate"));
        obj.setEnableConfProcessing(rs.getBoolean("enableConfProcessing"));
        obj.setSendTapToJMS(rs.getBoolean("sendTapToJMS"));
        obj.setSendConfToJMS(rs.getBoolean("sendConfToJMS"));
        obj.setSendDeltaConfToJMS(rs.getBoolean("sendDeltaConfToJMS"));
        obj.setSendTapToHTTP(rs.getBoolean("sendTapToHTTP"));
        return obj;
    }
}
