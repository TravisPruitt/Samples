package com.disney.xband.xi.model;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 7/25/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.Format;




public class XVRDAO extends DAO {

    private boolean DEBUG = false;

    public XVRDAO()
    {
        super();
    }


    public String  GetEventsToPreHeatAttraction(Date startTime, long facilityId, String buster )
    {
        String retVal = "";

        // Subtract 15 seconds from the top of this.
        startTime.setTime(startTime.getTime() - 15000);

        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);


        List<Map> resultsList = new ArrayList<Map>();

        String sQuery = "{call usp_GetEventsToPreHeatAttractionETL(?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        // Get the UTC start time.

        try
        {
            connection = this.getConnection();
            List<List<Map>> results = new ArrayList<List<Map>>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, GetUTCdatetimeAsString(startTime));
            statement.setLong(2, facilityId);

            rs = statement.executeQuery();

            logger.info("Getting Events");
            while (rs.next())
            {

                Map<String, Object> timeSliceList = new HashMap<String, Object>();

                timeSliceList.put("GuestId", rs.getString(1));

                timeSliceList.put("xPass", rs.getBoolean(2));
                timeSliceList.put("CreatedDate", rs.getDate(3));
                timeSliceList.put("TimeStamp", rs.getDate(4));
                timeSliceList.put("EventType", rs.getDouble(5));

                resultsList.add(timeSliceList);

            }

            logger.info("Adding Events results to results");
            results.add(resultsList);

            logger.info("Adding results to returnMessage");
            returnMessage.addData("Attraction", results);
            retVal = gson.toJson(returnMessage);

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





        return retVal;
    }


    public String GetEventsForAttraction(Date startTime, Date endTime, long facilityId, String buster)
    {
        //usp_GetEventsForAttractionETL

        String retVal = "";

        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);


        List<Map> resultsList = new ArrayList<Map>();

        String sQuery = "{call usp_GetEventsForAttractionETL(?,?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            logger.info("XVRDAO - Start: " + GetUTCdatetimeAsString(startTime));
            logger.info("XVRDAO - End: " + GetUTCdatetimeAsString(endTime));

            connection = this.getConnection();
            List<List<Map>> results = new ArrayList<List<Map>>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, GetUTCdatetimeAsString(startTime));
            statement.setString(2, GetUTCdatetimeAsString(endTime));
            statement.setLong(3, facilityId);

            rs = statement.executeQuery();

            logger.info("Getting Events");

            while (rs.next())
            {

                Map<String, Object> timeSliceList = new HashMap<String, Object>();

                timeSliceList.put("GuestId", rs.getString(1));

                timeSliceList.put("xPass", rs.getBoolean(2));
                timeSliceList.put("CreatedDate", rs.getTimestamp(3));
                timeSliceList.put("TimeStamp", rs.getTimestamp(4));
                timeSliceList.put("EventType", rs.getString(5));

                resultsList.add(timeSliceList);
            }

            if (DEBUG)
            {

                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String s = formatter.format(new Date());

                Map<String, Object> debugTimeSlice=new HashMap<String, Object>();

                debugTimeSlice.put("GuestId", "12345");
                debugTimeSlice.put("xPass", true);
                debugTimeSlice.put("CreatedDate", s);
                debugTimeSlice.put("TimeStamp",s);
                debugTimeSlice.put("EventType", "Entry");

                resultsList.add(debugTimeSlice);

                debugTimeSlice=new HashMap<String, Object>();

                debugTimeSlice.put("GuestId", "45678");
                debugTimeSlice.put("xPass", false);
                debugTimeSlice.put("CreatedDate", s);
                debugTimeSlice.put("TimeStamp",s);
                debugTimeSlice.put("EventType", "Entry");

                resultsList.add(debugTimeSlice);

            }



            logger.info("XVRDAO Adding Event results to results: " + resultsList.size());
            results.add(resultsList);

            logger.info("XVRDAO Adding results to returnMessage");
            returnMessage.addData("Attraction", results);
            retVal =   gson.toJson(returnMessage);
            logger.info("XVRDAO retval:\n" + retVal);

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



        return retVal;
    }


    public static String GetUTCdatetimeAsString(Date date)
    {
        final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(date);

        return utcTime;
    }



}
