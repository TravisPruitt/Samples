package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QueueDAO extends DAO
{
    private static final String sEntry = "entry";
    private static final String sMerge = "merge";

	public QueueDAO()
	{
		super();
	}

    public  HashMap<String, Map> getQueueCountBreakdown(int parkId, Date useDate) {
        String sQuery = "{call dbo.usp_GetSubwayQueueCountForAttractionETL(?)}";
     	Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try
		{
            HashMap<String, Map> mapQueueAttr = new HashMap<String, Map>();
			connection = this.getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setInt(1, parkId);
            rs = statement.executeQuery();
            while(rs.next()) {
                Map<String, Integer> qCountMap = new HashMap<String, Integer>();
                qCountMap.put(sEntry, rs.getInt(2));
                qCountMap.put(sMerge, rs.getInt(3));
                mapQueueAttr.put(rs.getString(1), qCountMap);
            }
            return mapQueueAttr;
        }
		catch (SQLException e)
		{
			logger.error(e.getMessage());
            return null;
		}
		catch (DAOException e)
		{
            logger.error(e.getMessage());
            return null;
		}
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }

    public String getQueueCountBreakdown(int parkId, Date useDate, String sLabel, String buster) {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
	    HashMap<String, Map> m = getQueueCountBreakdown(parkId, useDate);
        if(m == null) {

            rmsg.addData("data", m);
        }
        else
        rmsg.addData("data", m);
        rmsg.addData("date", sLabel);
        return "remoteData(" + gson.toJson(rmsg) + ");";
    }

    /*
	public String getQueueCount(Date useDate, String sLabel)
	{
		String sQuery = "{call dbo.usp_GetQueueCountForAttraction(?,?)}";

        ReturnMessage rmsg = new ReturnMessage();
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;

		try
		{
			connection = this.getConnection();

			// loop through list of attractions and get queue counts for each
			HashMap<String, Integer> mapQueueAttr = new HashMap<String, Integer>();
			Iterator<String> attrIdIter = DAO.facilityMap.keySet().iterator();
			while (attrIdIter.hasNext())
			{
				String sAttrId = attrIdIter.next();
				statement = connection.prepareCall(sQuery);
				statement.setString(1, sAttrId);
				statement.setString(2, DateUtil.tsformatter.format(useDate));
				rs = statement.executeQuery();
				if (rs.next())
				{
					mapQueueAttr.put(sAttrId, rs.getInt(1));
				}
				else 
				{
					mapQueueAttr.put(sAttrId, 0);
				}
			}
			rmsg.addData("data", mapQueueAttr);
			rmsg.addData("date", sLabel);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "" + "getQueueCount" );
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "" + "getQueueCount" );
		}
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
	}
    */

    public String getSubwayGuestsForAttraction(String attractionId, Date useDate, String sLabel, String buster) {
        String sQuery = "{call dbo.usp_GetSubwayGuestsForReaderETL(?,?,?)}";

        logger.debug("getsubwayguestsforattraction date used:" + useDate );

        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;

		try
		{
			connection = this.getConnection();
            HashMap<String, List> subWayGuestMap = new HashMap<String, List>();

            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            statement.setString(2, "ENTRY");
            statement.setString(3, DateUtil.tsformatter.format(useDate));
            rs = statement.executeQuery();
            List<HashMap> guestlist = new ArrayList<HashMap>();
            while (rs.next())
            {
                HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("email", rs.getString(2));
                mapGuest.put("guestId", rs.getString(1));
                mapGuest.put("queue", "Entry");
                //g.setEmail(rs.getString(2));
                //g.setGuestId(rs.getInt(1));
                //g.setLastRead(rs.getTimestamp(3));
                guestlist.add(mapGuest);
            }
            subWayGuestMap.put("Entry", guestlist);

            connectionPool.close(rs);
            connectionPool.close(statement);
            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            statement.setString(2, "MERGE");
            statement.setString(3, DateUtil.tsformatter.format(useDate));
            rs = statement.executeQuery();
            guestlist = new ArrayList<HashMap>();
            while (rs.next())
            {
                HashMap<String, String>mapGuest = new HashMap<String, String>();
                mapGuest.put("email", rs.getString(2));
                mapGuest.put("guestId", rs.getString(1));
                mapGuest.put("queue", "Merge");
                //g.setEmail(rs.getString(2));
                //g.setGuestId(rs.getInt(1));
                //g.setLastRead(rs.getTimestamp(3));
                guestlist.add(mapGuest);
            }
            subWayGuestMap.put("Merge", guestlist);

			rmsg.addData("data", subWayGuestMap);
			rmsg.addData("date", sLabel);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "" + "getQueueCount" );
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "" + "getQueueCount" );
		}
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }

    public String getSubwayQueueCount(String attractionId, Date useDate, String sLabel, String buster) {
       	String sQuery = "{call dbo.usp_GetSubwayQueueCountForAttractionETL(?,?)}";

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
            statement.setString(2, DateUtil.tsformatter.format(useDate));
            rs = statement.executeQuery();

            Map<String, Integer> qCountMap = new HashMap<String, Integer>();
            if(rs.next()) {
                qCountMap.put("entry", rs.getInt(1));
                qCountMap.put("merge", rs.getInt(2));
            }

			rmsg.addData("data", qCountMap);
			rmsg.addData("date", sLabel);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "" + "getQueueCount" );
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "" + "getQueueCount" );
		}
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }

    /*
    public String setSubwayDiagram(String attractionId, String subwayData) {
        String sQuery = "{call [dbo].[usp_AddSubwayDiagram](?,?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;

        try
        {
            connection = this.getConnection();

            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            statement.setString(2, subwayData);
            statement.execute();

            rmsg.addData("data", "map added for attraction" + attractionId);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "" + "setSubwayDiagram for id:" + attractionId );
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "" + "setSubwayDiagram for id:" + attractionId);
        }
        finally {
            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }

    public String getSubwayDiagram(String attractionId) {
        String sQuery = "{call [dbo].[usp_GetSubwayDiagramForAttraction](?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();

            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            rs=statement.executeQuery();
            String diagramData = null;
            if(rs.next()) {
                diagramData = rs.getString(1);
            }
            else {
                throw new DAOException("No diagram found for attractionid:" + attractionId);
            }
            rmsg.addData("data", diagramData);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return errorMessage(sqle.getMessage(), "" + "getSubwayDiagram for id:" + attractionId );
        }
        catch (DAOException dao)
        {
            return errorMessage(dao.getMessage(), "" + "getSubwayDiagram for id:" + attractionId );
        }
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }

    public String getSubwayDiagramById(int rowID) {
        String sQuery = "{call [dbo].[usp_GetSubwayDiagramForID](?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();

            statement = connection.prepareCall(sQuery);
            statement.setInt(1, rowID);
            rs=statement.executeQuery();
            String diagramData = null;
            if(rs.next()) {
                diagramData = rs.getString(1);
            }
            else {
                throw new DAOException("No diagram found for rowID:" + rowID);
            }
            rmsg.addData("data", diagramData);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return errorMessage(sqle.getMessage(), "" + "getSubwayDiagram for id:" + rowID );
        }
        catch (DAOException dao)
        {
            return errorMessage(dao.getMessage(), "" + "getSubwayDiagram for id:" + rowID );
        }
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }


    public String getSubwayDiagramList(String attractionId) {
        String sQuery = "{call [dbo].[usp_GetSubwayDiagramListForAttraction](?)}";
        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();

            statement = connection.prepareCall(sQuery);
            statement.setString(1, attractionId);
            rs=statement.executeQuery();
            String diagramData = null;
            List<HashMap> diagramList= new ArrayList<HashMap>();
            while(rs.next()) {
                HashMap<String, Object> diagramMap = new HashMap<String, Object>();
                diagramMap.put("ID", rs.getInt(1));
                diagramMap.put("diagramData", rs.getString(2));
                diagramMap.put("dateCreated", rs.getDate(3));

                diagramList.add(diagramMap);
            }
            rmsg.addData("data", diagramList);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return errorMessage(sqle.getMessage(), "" + "getSubwayDiagram for id:" + attractionId );
        }
        catch (DAOException dao)
        {
            return errorMessage(dao.getMessage(), "" + "getSubwayDiagram for id:" + attractionId );
        }
        finally {
            try {
                if (rs != null) {
                    connectionPool.close(rs);
                }
            }
            catch(Exception e) {
            }

            try {
                if (statement != null) {
                    connectionPool.close(statement);
                }
            }
            catch(Exception e) {
            }

            try {
                if (connection != null) {
                    connectionPool.release(connection);
                }
            }
            catch(Exception e) {
            }
        }
    }*/
}
