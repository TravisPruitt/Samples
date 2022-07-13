package com.disney.xband.xi.model;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 6/3/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LRRDAO extends DAO {

    public LRRDAO ()
    {
        super();
    }

    //
    public String GetLRREventsForPark(String parkId, String buster)
    {
        Date startTime = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -15);

        startTime = c.getTime();

        Date endTime = new Date();

        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);


        List<Map> resultsList = new ArrayList<Map>();

        String sQuery = "{call dbo.usp_LocationTieOut_ETL(?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            List<List<Map>> results = new ArrayList<List<Map>>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);

            rs = statement.executeQuery();

            logger.info("Getting timeslices");
            while (rs.next())
            {

                Map<String, Object> timeSliceList = new HashMap<String, Object>();

                timeSliceList.put("facilityId", rs.getString(1));

                timeSliceList.put("begin", rs.getString(2));
                timeSliceList.put("end", rs.getString(3));
                timeSliceList.put("entry", rs.getDouble(4));
                timeSliceList.put("abandon", rs.getDouble(5));
                timeSliceList.put("merge", rs.getDouble(6));
                timeSliceList.put("vba", rs.getDouble(7));
                timeSliceList.put("load", rs.getDouble(8));
                timeSliceList.put("exit", rs.getDouble(9));

                resultsList.add(timeSliceList);

            }

            logger.info("Adding timeslices results to results");
            results.add(resultsList);

            logger.info("Adding results to returnMessage");
            returnMessage.addData("park", results);
            return "remoteData(" + gson.toJson(returnMessage) + ");";

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
        finally
        {
            if (rs != null)
                try
                {
                    connectionPool.close(rs);
                }
                catch (Exception ex)
                {
                    // ignore;
                }

            if (statement != null)
                try
                {

                    connectionPool.close(statement);
                }
                catch   (Exception ex)
                {
                    // Ignore;
                }

            if (connection != null)
                try
                {
                    connectionPool.release(connection);
                }
                catch (Exception ex)
                {
                    // Ignore;
                }
        }

    }

    //
    public String GetSubwayQueueCountForAttractions(String parkId, String buster)
    {
        Date startTime = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -15);

        startTime = c.getTime();

        Date endTime = new Date();

        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);


        List<Map> resultsList = new ArrayList<Map>();

        String sQuery = "{call usp_GetSubwayQueueCountForAttraction_LRR_ETL(?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            List<List<Map>> results = new ArrayList<List<Map>>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);

            rs = statement.executeQuery();

            while (rs.next())
            {

                Map<String, Object> queueCounts = new HashMap<String, Object>();

                queueCounts.put("facilityId", rs.getString(1));

                queueCounts.put("entry", rs.getString(2));
                queueCounts.put("fp_entry", rs.getString(3));
                queueCounts.put("merge", rs.getDouble(4));
                queueCounts.put("load", rs.getDouble(5));

                resultsList.add(queueCounts);

            }

            results.add(resultsList);

            returnMessage.addData("park", results);
            return "remoteData(" + gson.toJson(returnMessage) + ");";

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
        finally
        {
            if (rs != null)
                try
                {
                    connectionPool.close(rs);
                }
                catch (Exception ex)
                {
                    // ignore;
                }

            if (statement != null)
                try
                {

                    connectionPool.close(statement);
                }
                catch   (Exception ex)
                {
                    // Ignore;
                }

            if (connection != null)
                try
                {
                    connectionPool.release(connection);
                }
                catch (Exception ex)
                {
                    // Ignore;
                }
        }

    }

}
