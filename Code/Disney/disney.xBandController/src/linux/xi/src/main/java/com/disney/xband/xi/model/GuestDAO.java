package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.*;

// TODO -- fix facilityMap to use park
public class GuestDAO extends DAO
{

	public GuestDAO()
	{
		super();
		return;
	}
	
	// used to populate Entitlement hourly screen clicks to show guests
	public String getGuestListForEntitlementFacilityView(String attractionId, Date currentTime, String label, String buster) {
		
        String sQuery="{call dbo.usp_GetGuestsForAttraction(?,?)}";

        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {	
 	        connection = this.getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            statement.setString(2, DateUtil.tsformatter.format(currentTime));
            int parkStartHour = 9;
            rs = statement.executeQuery();
            Map<String, List> dataList = new HashMap<String,List>();
            List<Map> hourlyGuestList = new ArrayList<Map>();
            while(rs.next()) {
            	HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("hour", rs.getString(1));
                mapGuest.put("queue", "fp_entry");
                mapGuest.put("guestId", rs.getString(2));
                mapGuest.put("firstname", rs.getString(3));
                mapGuest.put("lastname", rs.getString(4));
                mapGuest.put("email", rs.getString(5));
                mapGuest.put("lastRead", rs.getString(6));
                hourlyGuestList.add(mapGuest);
            }
            
            dataList.put("fp_entry", hourlyGuestList);
            dataList.put("merge", new ArrayList());
            dataList.put("exit", new ArrayList());
            
            /*
            List<List<Map>> hourlyGuestList = new ArrayList<List<Map>>();
            for(int x=parkStartHour; x<24; x++) {
            	hourlyGuestList.add(new ArrayList<Map>());
            }
            
            while(rs.next()) {
            	HashMap<String, String>mapGuest = new HashMap<String, String>();
            	int i = rs.getInt(1);
            	logger.debug("guest hour -- " +  i);
                mapGuest.put("hour", String.valueOf(i));
                mapGuest.put("guestId", rs.getString(2));
                mapGuest.put("firstname", rs.getString(3));
                mapGuest.put("lastname", rs.getString(4));
                mapGuest.put("email", rs.getString(5));
                mapGuest.put("lastRead", rs.getString(6));
                hourlyGuestList.get(i-parkStartHour).add((Map)mapGuest);
            }*/
            
            // gloriously mis-named to correlate with existing UI -- FIX after pilot
            rmsg.addData("data", dataList);
            String[] header = { "Guest Id","First Name", "Last Name", "Email", "Time" };
            rmsg.addData("dataHeader", header);

            rmsg.addData("facility", attractionId);
            rmsg.addData("date", label);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "" + "getGuestListForEntitlementFacilityView" );
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "" + "getGuestListForEntitlementFacilityView" );
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
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

	public String getGuestListForAttraction(String attraction,
			Date periodStart, Date periodEnd, String label, String buster)
	{
        String sQuery="{call dbo.usp_GetFacilityGuestsByReader(?,?,?)}";
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        List<HashMap> guestlist = new ArrayList<HashMap>();
        HashMap<String, List> subWayGuestMap = new HashMap<String, List>();

        try
        {
            connection = this.getConnection();

            statement = connection.prepareCall(sQuery);
            statement.setString(1, attraction);
            statement.setString(2, "ENTRY");
            statement.setString(3, DateUtil.tsformatter.format(periodEnd));
            rs = statement.executeQuery();

            while (rs.next())
            {
                HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("guestId", rs.getString(1));
                mapGuest.put("firstname", rs.getString(2));
                mapGuest.put("lastname", rs.getString(3));
                mapGuest.put("email", rs.getString(4));
                mapGuest.put("lastRead", rs.getString(5));
                mapGuest.put("queue", "fp_entry");
                guestlist.add(mapGuest);
            }
            subWayGuestMap.put("fp_entry", guestlist);
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "" + "getQueueCount" );
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "" + "getQueueCount" );
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
            statement = connection.prepareCall(sQuery);

            statement.setString(1, attraction);
            statement.setString(2, "MERGE");
            statement.setString(3, DateUtil.tsformatter.format(periodEnd));
            rs = statement.executeQuery();
            guestlist = new ArrayList<HashMap>();
            List<HashMap>mergelist = new ArrayList<HashMap>();

            while (rs.next())
            {
                HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("guestId", rs.getString(1));
                mapGuest.put("firstname", rs.getString(2));
                mapGuest.put("lastname", rs.getString(3));
                mapGuest.put("email", rs.getString(4));
                mapGuest.put("lastRead", rs.getString(5));
                mapGuest.put("queue", "merge");

                Date d = rs.getTimestamp(5);

                // if returned PeriodEnd - timestamp  > 10 minutes, then exit event
                // else merge
                logger.debug("attr test " + d.toString() + " compared to " + periodEnd);
                if(DateUtil.minuteAdd(d, 5).compareTo(periodEnd) >= 0) {
                    mapGuest.put("queue", "merge");
                    mergelist.add(mapGuest);
                }
                else {
                    mapGuest.put("queue", "exit");
                    guestlist.add(mapGuest);
                }
            }
                //else {
                //    mapGuest.put("queue", "exit");
                //    guestlist.add(mapGuest);
                //}
            //}
            //subWayGuestMap.put("exit", guestlist);
            subWayGuestMap.put("merge", mergelist);

            String[] header = { "Guest Id","First Name", "Last Name", "Email", "Time" };
            rmsg.addData("dataHeader", header);

            rmsg.addData("facility", attraction);
            rmsg.addData("data", subWayGuestMap);
            rmsg.addData("date", label);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "" + "getQueueCount" );
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
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

	public String getGuest(int guestId, Date startTime, Date endTime,
			String label)
	{
		ReturnMessage rmsg = new ReturnMessage();
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
        Guest gd = new Guest();

		try
		{
			connection = this.getConnection();
			String sQuery = "{call dbo.usp_GetGuest(?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setInt(1, guestId);

			statement.setString(2, DateUtil.tsformatter.format(startTime));
			statement.setString(3, DateUtil.tsformatter.format(endTime));
			rs = statement.executeQuery();
			rs.next();
            // T(g.GuestID), g.FirstName, g.LastName, g.EmailAddress, g.CelebrationType, g.RecognitionDate
			gd.setGuestId(guestId);
            gd.setFirstName(rs.getString(2));
            gd.setLastName(rs.getString(3));
			gd.setEmail(rs.getString(4));
            gd.setCelebrationType(rs.getString(5));
            gd.setRecognitionDate(rs.getString(6));
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            logger.debug(sqle.getLocalizedMessage());
            ErrorMessage em = new ErrorMessage(sqle.getMessage());
            em.setSource("getGuest");
            return this.toJSON(em);
        }
        catch (DAOException dao)
        {
            dao.printStackTrace();
            logger.debug(dao.getLocalizedMessage());
            ErrorMessage em = new ErrorMessage(dao.getMessage());
            em.setSource("getGuest");
            return this.toJSON(em);
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
			// entitlements
			String sQuery = "{call dbo.usp_GetGuestEntitlements(?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setInt(1, guestId);
            statement.setString(2, DateUtil.tsformatter.format(startTime));
            statement.setString(3, DateUtil.tsformatter.format(endTime));
			rs = statement.executeQuery();
            logger.debug("entitlements" + guestId + " " + DateUtil.tsformatter.format(startTime)+ " " + DateUtil.tsformatter.format(endTime));
			while (rs.next())
			{
				// returns attractionId, window start hour, minutes, window end,
				// minutes, status
				int attractionId = rs.getInt(1);
				gd.addEntitlement(
					attractionId,
					"", //facilityMap.get(String.valueOf(attractionId)).getName(),
					String.format("%2d%2d", rs.getInt(2), rs.getInt(3)),
					String.format("%2d%2d", rs.getInt(4), rs.getInt(5)), 
					rs.getString(6)
				);
                logger.debug(rs.getInt(2));
			}

        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            logger.debug(sqle.getLocalizedMessage());
            ErrorMessage em = new ErrorMessage(sqle.getMessage());
            em.setSource("getGuest");
            return this.toJSON(em);
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
			// t1.FacilityID, t1.Timestamp, WaitTime = DATEDIFF(MI,
			// t1.Timestamp, t2.Timestamp)
			// addGuestExperience(String timestamp, int attractionid, String
			// attraction, String queue, int waitTime)
			String sQuery = "{call[dbo].[usp_getGuestWaitTimeFP](?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setInt(1, guestId);
            statement.setString(2, DateUtil.tsformatter.format(startTime));
            statement.setString(3, DateUtil.tsformatter.format(endTime));
			rs = statement.executeQuery();
			while (rs.next())
			{
				String sAttrId = rs.getString(2);
				gd.addGuestExperience(String.valueOf(rs.getTimestamp(1)),
						Integer.parseInt(sAttrId),
						"", //facilityMap.get(sAttrId).getName(),
                        rs.getInt(3));
			}

        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            logger.debug(sqle.getLocalizedMessage());
            ErrorMessage em = new ErrorMessage(sqle.getMessage());
            em.setSource("getGuest");
            return this.toJSON(em);
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
			// guest read
			// public void addGuestRead(String timestamp, int attractionid,
			// String attraction, String queue, String readerlocation) {
			String sQuery = "{call dbo.usp_GetGuestReads(?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setInt(1, guestId);
            statement.setString(2, DateUtil.tsformatter.format(startTime));
            statement.setString(3, DateUtil.tsformatter.format(endTime));
			rs = statement.executeQuery();
			while (rs.next())
			{
				String sAttrId = rs.getString(2);
				gd.addGuestRead(String.valueOf(rs.getTimestamp(1)),
						Integer.parseInt(sAttrId),
                        " ", //facilityMap.get(sAttrId).getName()
						rs.getString(3),
						rs.getString(4));
			}
			rmsg.addData("data", gd);
			rmsg.addData("date", label);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
            sqle.printStackTrace();
            logger.debug(sqle.getLocalizedMessage());
			ErrorMessage em = new ErrorMessage(sqle.getMessage());
			em.setSource("getGuest");
			return this.toJSON(em);
		}
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
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

    public String getGuestsForSearch(Date dateObj, int returnRowCount, String label, String buster) {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            logger.debug("guests for search called label: " + label + " count:" + returnRowCount);
            connection = this.getConnection();
            String sQuery = "{call dbo.usp_GetGuestsForSearch(?,?,?)}";
            statement = connection.prepareCall(sQuery);
            statement.setString(1, DateUtil.tsformatter.format(DateUtil.setDayStartForDate(dateObj)));
            statement.setString(2, DateUtil.tsformatter.format(dateObj));
            statement.setInt(3, returnRowCount);

            rs = statement.executeQuery();
            List<HashMap> guestlist = new ArrayList<HashMap>();
            while (rs.next())
            {

                HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("guestId", rs.getString(1));
                mapGuest.put("email", rs.getString(2));
                mapGuest.put("lastRead", String.valueOf(rs.getTimestamp(3)));
                guestlist.add(mapGuest);
            }
            rmsg.addData("data", guestlist);
            rmsg.addData("date", label);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            ErrorMessage em = new ErrorMessage(sqle.getMessage());
            em.setSource("getGuestForSearch");
            return this.toJSON(em);
        }
        catch (DAOException dao)
        {
            ErrorMessage em = new ErrorMessage(dao.getMessage());
            em.setSource("getGuestForSearch");
            return this.toJSON(em);
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
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
}
