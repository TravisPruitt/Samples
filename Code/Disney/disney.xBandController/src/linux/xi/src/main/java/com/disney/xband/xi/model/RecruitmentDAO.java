package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RecruitmentDAO extends DAO {

    private static final String RECRUIT_GET_VISITS = "{call dbo.usp_RecruitVisitsETL(?,?,?)}";
    private static final String RECRUIT_GET_DAILY = "{call dbo.usp_RecruitDailyETL(?,?)}";
    private static final String RECRUIT_GET_ENGAGED ="{call dbo.usp_RecruitEngagedETL(?)}";
    private static final String RECRUIT_GET_PREARRIVAL ="{call dbo.usp_RecruitPreArrivalETL()}";
    private static final String RECRUIT_GET_MEHA ="{call dbo.usp_RecruitBubblesETL()}";

    private static final String sPossibleGuests = "possibleGuests";
    private static final String sEntitlementUsedGuests = "entitlementUsedGuests";
    private static final String sRecruitedTarget = "target";

    // caching
    private static ReturnMessage cachedMessage = null;
    private static long cachedMessageTime = 0l;



    private static Map<String, Integer> cachedMehaMap = null;
    private static long cachedMehaTime = 0l;

    private Map getVisitsFromDb(Connection connection, Integer parkId, Date startDate, Date endDate)
            throws SQLException {
        CallableStatement statement=null;
        ResultSet rs=null;
        TreeMap<String, HashMap<String, Integer>> visitMap = new TreeMap<String, HashMap<String, Integer>>();
        try {
            statement= connection.prepareCall(RECRUIT_GET_VISITS);
            statement.setInt(1, parkId);
            statement.setString(2, DateUtil.tsformatter.format(startDate));
            statement.setString(3, DateUtil.tsformatter.format(endDate));

            rs = statement.executeQuery();
            while (rs.next())
            {
                HashMap<String, Integer> h = new HashMap<String, Integer>();
                String sDate = rs.getString(1);
                h.put(sEntitlementUsedGuests, rs.getInt(2));
                h.put(sPossibleGuests, rs.getInt(3));
                visitMap.put(sDate, h);
            }
        }
        finally {
            if (rs != null) {
                connectionPool.close(rs);
                rs = null;
            }
            if (statement != null) {
                connectionPool.close(statement);
                statement = null;
            }
        }
        return visitMap;
    }

    public String getRecruitment(Date windowStartDate, Date windowEndDate, Date currentDate, String buster) {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try
        {
            long currentTs = System.currentTimeMillis();
            if(cachedMessage == null || (currentTs - cachedMessageTime) > RECRUITMENT_REFRESH_INTERVAL) {
                cachedMessageTime = currentTs;
                connection = getConnection();

                // meha visualization
                if(cachedMehaMap == null || (currentTs - cachedMehaTime) > MEHA_REFRESH_INTERVAL) {
                    Map<String, Integer> mehaMap = new TreeMap<String, Integer>();
                    try {
                        statement = connection.prepareCall(RECRUIT_GET_MEHA);
                        rs = statement.executeQuery();
                        while (rs.next())
                        {
                            mehaMap.put(rs.getString(1), rs.getInt(2));
                        }

                        cachedMehaMap=mehaMap;
                        cachedMehaTime=currentTs;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        logger.debug(e.getMessage());
                    }
                    finally
                    {
                        if (rs != null) {
                            connectionPool.close(rs);
                            rs = null;
                        }
                        if (statement != null) {
                            connectionPool.close(statement);
                            statement = null;
                        }
                    }
                }
                rmsg.addData("mehats", cachedMehaTime);
                rmsg.addData("meha", cachedMehaMap);

                // change to daily
                Map<String, Integer> dailyMap = new TreeMap<String, Integer>();
                try {
                    statement = connection.prepareCall(RECRUIT_GET_DAILY);
                    statement.setString(1, DateUtil.tsformatter.format(windowStartDate));
                    statement.setString(2, DateUtil.tsformatter.format(windowEndDate));
                    rs = statement.executeQuery();
                    while (rs.next())
                    {
                        dailyMap.put(rs.getString(1), rs.getInt(2));
                    }
                    rmsg.addData("daily", dailyMap);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    logger.debug(e.getMessage());
                }
                finally
                {
                    if (rs != null) {
                        connectionPool.close(rs);
                        rs = null;
                    }
                    if (statement != null) {
                        connectionPool.close(statement);
                        statement = null;
                    }
                }

                // visits
                Map<String, Map> parkVisitorMap = new TreeMap<String, Map>();
                try {
                    Iterator parkKeys = parkFacilityMap.keySet().iterator();
                    while(parkKeys.hasNext()) {
                        Integer parkId = (Integer)parkKeys.next();
                        parkVisitorMap.put(String.valueOf(parkId), getVisitsFromDb(connection, parkId, windowStartDate, windowEndDate));
                    }
                    rmsg.addData("visits", parkVisitorMap);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    logger.debug(e.getMessage());
                }
                finally
                {
                    if (rs != null) {
                        connectionPool.close(rs);
                        rs = null;
                    }
                    if (statement != null) {
                        connectionPool.close(statement);
                        statement = null;
                    }
                }

                /*

                pre-arrival -- number of days in advance of target date a guest has booked
                    - basically, guests have bookings today, when did they book



                daily -- count of web site traffic hits -- what day someone came to book their visit

                 */

                //HashMap<Integer, Integer> preArrivalData = new HashMap<Integer, Integer>();

                ArrayList<Integer> preArrivalList = new ArrayList<Integer>();
                // pre-arrival
                try {
                    statement = connection.prepareCall(RECRUIT_GET_PREARRIVAL);
                    rs = statement.executeQuery();
                    while(rs.next())
                    {
                        preArrivalList.add(rs.getInt(2));
                    }
                }
                finally
                {
                    if (rs != null) {
                        connectionPool.close(rs);
                        rs = null;
                    }
                    if (statement != null) {
                        connectionPool.close(statement);
                        statement = null;
                    }
                }
                rmsg.addData("prearrival", preArrivalList);

                // engaged
                Iterator parkKeys = parkFacilityMap.keySet().iterator();
                HashMap<Integer, Integer> parkDailyEngagedMap=new HashMap<Integer, Integer>();
                while(parkKeys.hasNext()) {
                    Integer parkId = (Integer)parkKeys.next();
                    try {
                        statement = connection.prepareCall(RECRUIT_GET_ENGAGED);
                        statement.setString(1, DateUtil.tsformatter.format(currentDate));
                        rs = statement.executeQuery();
                        while(rs.next())
                        {
                            parkDailyEngagedMap.put(rs.getInt(1), rs.getInt(2));
                        }
                    }
                    finally
                    {
                        if (rs != null) {
                            connectionPool.close(rs);
                            rs = null;
                        }
                        if (statement != null) {
                            connectionPool.close(statement);
                            statement = null;
                        }
                    }
                }
                rmsg.addData("engaged", parkDailyEngagedMap);
                cachedMessage = rmsg;
                return "remoteData(" + gson.toJson(rmsg) + ");";
            }
            else {
                logger.info("using cached");
                // set new timestamp on cachedMessage
                cachedMessage.setMessageTimeStamp(currentTs);
                cachedMessage.setBuster(buster);
                // return cachedMessage
                return "remoteData(" + gson.toJson(cachedMessage) + ");";
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "getrecruit");
        }
        catch (DAOException e)
        {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "getrecruit");
        }
        finally
        {
            if (rs != null)
                connectionPool.close(rs);
            if (statement != null)
                connectionPool.close(statement);
            if (connection != null)
                connectionPool.release(connection);
        }
    }

    public static void clearCache() {
        cachedMessageTime=0l;
    }
}
