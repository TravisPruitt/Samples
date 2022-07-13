package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*
 import java.util.ArrayList;

 import com.google.gson.Gson;
 */

public class BlueLaneDAO extends DAO
{
    private static final String[] reasonCodes = new String[] {
        "No Xpass",
        "Early",
        "null",
        "Next Experience",
        "Wrong Experience",
        "Already Redeemed",
        "Different Viewing Area",
        "Late"
    };

	public BlueLaneDAO()
	{
		super();
		return;
	}

	public String getAllBlueLane(int parkId, Date startDate, Date endDate, String label, String buster)
	{
		// two parts -- list of all blue lane occurances by attraction
		// and list of all blue lane guests
		// first we need attraction, bluelane, overridden
		String sQuery = "{call dbo.usp_GetBlueLaneForAttractionETL(?,?,?)}";
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.setParkId(parkId);
		rmsg.addData("date", label);

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;


        HashMap<Integer, Facility> localFacilityMap = parkFacilityMap.get(parkId);

		List<BlueLane> bluelanelist = new ArrayList<BlueLane>();
		try
		{
			connection = this.getConnection();
			Set<Integer> attrIdList = localFacilityMap.keySet();
			Iterator<Integer> attrIdIter = attrIdList.iterator();
			while (attrIdIter.hasNext())
			{
                int attrId = attrIdIter.next();
                Facility f = localFacilityMap.get(attrId);
                if (Integer.valueOf(f.getParkId()) == parkId) {
                    try {
                        statement = connection.prepareCall(sQuery);
                        statement.setInt(1, attrId);
                        statement.setString(2, DateUtil.tsformatter.format(startDate));
                        statement.setString(3, DateUtil.tsformatter.format(endDate));
                        rs = statement.executeQuery();
                        rs.next();

                        BlueLane bl = new BlueLane();
                        bl.setAttractionId(attrId);
                        bl.setAttraction(f.getShortName());
                        bl.setBlEventCount(rs.getInt(1));
                        bl.setBlOverrideCount(rs.getInt(2));
                        bluelanelist.add(bl);
                    }
                    finally {
                        if (rs != null) {
                            try {
                                connectionPool.close(rs);
                                rs = null;
                            }
                            catch(Exception ignore) {
                            }
                        }

                        if (statement != null) {
                            try {
                                connectionPool.close(statement);
                                statement = null;
                            }
                            catch(Exception ignore) {
                            }
                        }
                    }
                }
			}

			String[] header = { "attraction", "events", "overrides" };
			rmsg.addData("dataHeader", header);
			rmsg.addData("data", bluelanelist);

			sQuery = "{call dbo.usp_GetBlueLaneReasonCodesETL(?, ?,?)}";

			statement = connection.prepareCall(sQuery);
            statement.setString(1, DateUtil.tsformatter.format(startDate));
            statement.setString(2, DateUtil.tsformatter.format(endDate));
            statement.setInt(3, parkId);
			rs = statement.executeQuery();

			Map<String, Integer> reasonMap = new HashMap<String, Integer>();
			for (String reasonCode : reasonCodes)
			{
				reasonMap.put(reasonCode, new Integer(0));
			}
			while (rs.next())
			{
				reasonMap.put(rs.getString(4), new Integer(rs.getInt(2)));
			}

			rmsg.addData("reasonCodes", reasonMap);
			return "remoteData(" + gson.toJson(rmsg) + ");";

		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "getAllBlueLane", buster);
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "getAllBlueLane", buster);
		}
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch(Exception ignore) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch(Exception ignore) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch(Exception ignore) {
                }
            }
        }
	}


    public String getAttractionReasonCodes(int attractionId, Date startDate, Date endDate,
                                           String label, String buster) {

		String sQuery = "{call dbo.usp_GetBlueLaneReasonCodesForAttractionETL(?,?,?)}";
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.addData("date", label);

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;

        HashMap<String, Integer> rCodeCountMap = new HashMap<String, Integer>();
		try
		{
			connection = this.getConnection();
			statement = connection.prepareCall(sQuery);
            statement.setInt(1,attractionId);
            statement.setString(2, DateUtil.tsformatter.format(startDate));
            statement.setString(3, DateUtil.tsformatter.format(endDate));
			rs = statement.executeQuery();

			for (String reasonCode : reasonCodes)
			{
				rCodeCountMap.put(reasonCode, new Integer(0));
			}
            while(rs.next()) {
                String key=rs.getString(2);
                int value = rs.getInt(3);
                rCodeCountMap.put(key, value);
			}
			rmsg.addData("reasonCodeCount", rCodeCountMap);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "getAllBlueLane", buster);
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "getAllBlueLane", buster);
		}
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch(Exception ignore) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch(Exception ignore) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch(Exception ignore) {
                }
            }
        }
    }
}
