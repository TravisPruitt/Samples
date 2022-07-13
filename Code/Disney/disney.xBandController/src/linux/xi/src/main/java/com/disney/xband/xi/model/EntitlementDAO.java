package com.disney.xband.xi.model;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import com.disney.xband.xi.model.DAO.ReturnMessage;

public class EntitlementDAO extends DAO
{
    private static Map<Integer, ReturnMessage> parkEntSummaryCache = new ConcurrentHashMap<Integer, ReturnMessage>();
    private static Map<Integer, Long> parkEntSummaryLastUpdate = new ConcurrentHashMap<Integer, Long>();

	public EntitlementDAO()
	{
		super();
	}

    public static void clearCache() {
        parkEntSummaryLastUpdate.clear();
    }

	/*
	 * This is the list of all attractions for a given date or ALL dates
	 * containing guestsinqueue, selEnt, redeemEnt, totalBlue
	 */
	public String getEntitlementSummary(int parkId, Date startDate, Date endDate,
			String label, String buster)
	{
        logger.debug("getEntitlementSummary called with " + startDate + "    " + endDate);
		String sQuery = "{call dbo.usp_GetEntitlementSummaryETL(?,?,?)}";

        long cachedMessageTime;
        long currentTimeMillis = System.currentTimeMillis();
        if(parkEntSummaryLastUpdate.containsKey(parkId)) {
            cachedMessageTime = parkEntSummaryLastUpdate.get(parkId);
        }
        else {
            cachedMessageTime = currentTimeMillis;
            parkEntSummaryLastUpdate.put(parkId, currentTimeMillis);
        }

		ReturnMessage rmsg = parkEntSummaryCache.get(parkId);
        if((currentTimeMillis - cachedMessageTime) > ENTITLE_REFRESH_INTERVAL || rmsg == null) {
            logger.debug("entitle summary data is too old -- hitting database");
            rmsg=new ReturnMessage();
            rmsg.setBuster(buster);
            rmsg.setParkId(parkId);
            Connection connection = null;
            CallableStatement statement = null;
            ResultSet rs = null;
            try {
                logger.debug("entitlementsummary called with " + startDate.toString()
                        + " " + endDate.toString() + " " + parkId);
                Map<Integer, Facility> facilityMap = parkFacilityMap.get(parkId);
                connection = this.getConnection();
                List<Entitlement> entList = new ArrayList<Entitlement>();
                Iterator<Integer> attrIdIter = facilityMap.keySet().iterator();
                statement = connection.prepareCall(sQuery);
                while (attrIdIter.hasNext()) {
                    Integer sAttrId = attrIdIter.next();
                    statement.setInt(1, sAttrId);
                    statement.setString(2, DateUtil.tsformatter.format(startDate));
                    statement.setString(3, DateUtil.tsformatter.format(endDate));

                    rs = statement.executeQuery();
                    if (rs.next()) {
                        Entitlement ent = new Entitlement();
                        ent.setAttraction(facilityMap.get(sAttrId).getName());
                        ent.setAttractionId(sAttrId);
                        ent.setAvailableEntitlements(rs.getInt(1));
                        ent.setRedeemedEntitlements(rs.getInt(3));
                        ent.setSelectedEntitlements(rs.getInt(2));
                        ent.setSelectedInPark(rs.getInt(7));
                        //ent.setSelectedInPark(0);
                        ent.setTotalBlueLane(rs.getInt(4));
                        ent.setGuestsInQueue(rs.getInt(5));
                        ent.setOverridesBlueLane(rs.getInt(6));
                        entList.add(ent);
                    }
                }
                String[] header = { "Facility", "Guests In Queue",
                        "Available Entitlement", "Selected Entitlement",
                        "Redeemed Entitlements", "Selected In Park", "Total Blue Occurrences" };
                rmsg.addData("dataHeader", header);
                rmsg.addData("data", entList);
                rmsg.addData("date", label);
                parkEntSummaryLastUpdate.put(parkId, currentTimeMillis);
                parkEntSummaryCache.put(parkId, rmsg);
                return "remoteData(" + gson.toJson(rmsg) + ");";
            }
            catch (SQLException sqle)
            {
                return this.errorMessage(sqle.getMessage(), "getEntitlementSummary", buster);
            }
            catch (DAOException dao)
            {
                return this.errorMessage(dao.getMessage(), "getEntitlementSummary", buster);
            }
            finally
            {
                if (rs != null) {
                    try {
                        connectionPool.close(rs);
                    }
                    catch (Exception ignored) {
                    }
                }

                if (statement != null) {
                    try {
                        connectionPool.close(statement);
                    }
                    catch (Exception ignored) {
                    }
                }

                if (connection != null) {
                    try {
                        connectionPool.release(connection);
                    }
                    catch (Exception ignored) {
                    }
                }
            }
        }
        else {
            logger.info("using cached entitlement summary");
            rmsg.setMessageTimeStamp(currentTimeMillis);
            rmsg.setBuster(buster);
            rmsg.addData("date", label);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
	}


	public String getAttractionSummary(int attractionId, Date startDate, Date endDate, String label, String buster)
	{
		String sQuery = "{call dbo.usp_GetEntitlementSummaryHourlyETL(?,?,?)}";
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
        String facilityName = getFacilityById(attractionId).getName();
        if(facilityName == null) {  // possible with bad user input
            return this.errorMessage("No facility matching:" + attractionId, "getAttractionSummary", buster);
        }
        logger.debug("getAttractionSummary attr:" + attractionId
                + " starttime:" + startDate.toString()
                + " endtime:" + endDate.toString());
		try {
			// timestamp always comes in as 00:00:00 time for start date
			connection = this.getConnection();
			// tsql -- DATEPART ( datepart , date ) -- returns integer
			// so we can start with: int starttime and just ++

            // 0 time dt -- that we need to bump up to 900
            DateTime endDT;
            if (startDate.equals(endDate)) {
                // set it to SAME DAY but at 23:59:59
                endDT = new DateMidnight(endDate).plusDays(1).toDateTime().minusSeconds(1);
            }
            else {
                endDT = new DateTime(endDate);
            }
            int endHour=endDT.getHourOfDay();
            int curHour = 9;
            // set first to time 00:00:00 and then add 9 hours
            DateTime curDT = new DateMidnight(startDate).toDateTime().plusHours(curHour);
            DateTime curDTEnd = curDT.plusMinutes(59).plusSeconds(59);
            Map<Integer, Entitlement> entMap = new ConcurrentHashMap<Integer, Entitlement>();
            while(curHour < 24) {
                try {
                    String useStartTime = DateUtil.tsformatter.format(curDT.toDate());
                    String useEndTime;

                    //
                    if(endHour == curHour){
                        // use endHour minutes
                        useEndTime = DateUtil.tsformatter.format(endDate);
                    }
                    else {
                        // use regular 59 minutes
                        useEndTime = DateUtil.tsformatter.format(curDTEnd.toDate());
                    }

                    statement = connection.prepareCall(sQuery);
                    statement.setInt(1, attractionId);
                    statement.setString(2, useStartTime);
                    statement.setString(3, useEndTime);
                    rs = statement.executeQuery();

                    rs.next();

                    Entitlement ent = new Entitlement();
                    ent.setAttraction(facilityName);
                    ent.setAttractionId(attractionId);
                    ent.setHour(curHour);
                    ent.setAvailableEntitlements(rs.getInt(1));
                    ent.setSelectedEntitlements(rs.getInt(2));
                    ent.setRedeemedEntitlements(rs.getInt(3));
                    ent.setSelectedInPark(rs.getInt(7));
                    ent.setTotalBlueLane(rs.getInt(4));
                    ent.setOverridesBlueLane(rs.getInt(6));

                    ent.setGuestsInQueue(rs.getInt(5));



                    //}
                    entMap.put(curHour, ent);
                    // increment the times
                    curDT=curDT.plusHours(1);
                    curDTEnd=curDTEnd.plusHours(1);
                    curHour++;
                }
                finally {
                    if (rs != null) {
                        try {
                            connectionPool.close(rs);
                            rs = null;
                        }
                        catch (Exception ignored) {
                        }
                    }

                    if (statement != null) {
                        try {
                            connectionPool.close(statement);
                            statement = null;
                        }
                        catch (Exception ignored) {
                        }
                    }
                }
            }

            Set<Integer> keys = entMap.keySet();
			Integer[] strkeys = keys.toArray(new Integer[keys.size()]);
			Arrays.sort(strkeys);
			List<Entitlement> entList = new ArrayList<Entitlement>(keys.size());
            for (Integer strkey : strkeys) {
                entList.add(entMap.get(strkey));
            }

			String[] header = { "Hour", "Facility", "Guests In Queue",
					"Available Entitlement", "Selected Entitlement",
					"Redeemed Entitlements", "Selected In Park", "Total Blue Occurrences" };
			rmsg.addData("dataHeader", header);
			rmsg.addData("data", entList);
			rmsg.addData("date", label);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "getAttractionSummary", buster);
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "getAttractionSummary", buster);
		}
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception ignored) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception ignored) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	/*
    public String getEntitlementAll(Date startDate, Date endDate, String label) {
		String sQuery = "{call dbo.usp_GetEntitlementAll(?,?)}";
		ReturnMessage rmsg = new ReturnMessage();
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try
		{
			connection = this.getConnection();
			statement = connection.prepareCall(sQuery);
			statement.setString(1, DateUtil.tsformatter.format(startDate));
			statement.setString(2, DateUtil.tsformatter.format(endDate));

			rs = statement.executeQuery();
            HashMap<String, Integer> retMap = new HashMap<String, Integer>();
			if (rs.next())
			{
                retMap.put("selected", rs.getInt(1));
                retMap.put("redeemed", rs.getInt(2));
                retMap.put("bluelane", rs.getInt(3));
                retMap.put("overrides", rs.getInt(4));
			}
			rmsg.addData("data", retMap);
			rmsg.addData("date", label);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException e)
		{
            logger.error(e.getLocalizedMessage());
			return errorMessage(e.getMessage(), "getEntitlementAll");
		}
		catch (DAOException e)
		{
            logger.error(e.getLocalizedMessage());
			return errorMessage(e.getMessage(), "getEntitlementAll");
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
    }*/
}
