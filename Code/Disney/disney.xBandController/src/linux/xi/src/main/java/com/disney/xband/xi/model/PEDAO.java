package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 4/30/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class PEDAO extends DAO {

    public PEDAO()
    {
        super();
    }

    public String GetPEReaderEventsForPark(String parkId, String buster)
    {
        Date startTime = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -15);

        startTime = c.getTime();

        Date endTime = new Date();

       return GetPEEventTotalsForPark(startTime, endTime, parkId, buster);

    }

    public String GetPEReaderEventsForParkIOS(String parkId, String buster)
    {
        Date startTime = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -15);

        startTime = c.getTime();

        Date endTime = new Date();

        return GetPEEventTotalsForParkIOS(startTime, endTime, parkId, buster);

    }

    public String GetPEEventTotalsForParkIOS(Date start, Date end, String parkId, String buster)
    {
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);
        String sQuery = "{call dbo.usp_GetPEEventTotalsForPark_ETL(?,?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            List<Map> results = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            statement.setString(2, DateUtil.tsformatter.format(start));
            statement.setString(3, DateUtil.tsformatter.format(end));
            rs = statement.executeQuery();



            while (rs.next())
            {
                Map<String, Object> parkResult = new HashMap<String, Object>();
                //Map<String, Object> parkResult = new HashMap<String, Object>();
                parkResult.put("hasEnteredCounts", rs.getInt(1));
                parkResult.put("blueLaneCounts", rs.getInt(2));
                parkResult.put("facilityID", rs.getString(3));
                returnMessage.addData("readers", GetPEReaderEventCountsByEventType(start, end, parkId));
                returnMessage.addData("events", GetPEReaderEventsByEventType());

                results.add(parkResult);
            }

            returnMessage.addData("facilities", results);

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


    public String GetEventTotalsForPark(String parkId, String buster)
    {
        Date startTime = new Date();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -15);

        startTime = c.getTime();

        Date endTime = new Date();

        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);
        String sQuery = "{call dbo.usp_GetPEEventTotalsForPark_ETL(?,?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {



            connection = this.getConnection();
            List<Map> results = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            statement.setString(2, DateUtil.tsformatter.format(startTime));
            statement.setString(3, DateUtil.tsformatter.format(endTime));
            rs = statement.executeQuery();

            while (rs.next())
            {
                Map<String, Object> parkResult = new HashMap<String, Object>();
                //Map<String, Object> parkResult = new HashMap<String, Object>();
                parkResult.put("hasEnteredCounts", rs.getInt(1));
                parkResult.put("blueLaneCounts", rs.getInt(2));
                parkResult.put("facilityID", rs.getString(3));

                results.add(parkResult);
            }

            returnMessage.addData("facilities", results);

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


    public String GetPEEventTotalsForPark(Date start, Date end, String parkId, String buster)
    {
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setBuster(buster);
        String sQuery = "{call dbo.usp_GetPEEventTotalsForPark_ETL(?,?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {


            connection = this.getConnection();
            List<Map> results = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            statement.setString(2, DateUtil.tsformatter.format(start));
            statement.setString(3, DateUtil.tsformatter.format(end));
            rs = statement.executeQuery();



            while (rs.next())
            {
                Map<String, Object> parkResult = new HashMap<String, Object>();
                //Map<String, Object> parkResult = new HashMap<String, Object>();
                parkResult.put("hasEnteredCounts", rs.getInt(1));
                parkResult.put("blueLaneCounts", rs.getInt(2));
                parkResult.put("facilityID", rs.getString(3));

                results.add(parkResult);
            }


            returnMessage.addData("facilities", results);
            returnMessage.addData("readers", GetPEReaderEventCountsByEventType(start, end, parkId));
            returnMessage.addData("events", GetPEReaderEventsByEventType());

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

    public List<Map> GetPEReaderEventCountsByEventType(Date start, Date end, String parkId)
    {
        String sQuery = "{call dbo.usp_GetPEReaderEventCountsByEventType_ETL(?,?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            HashMap<String, Map> retVal = new HashMap<String, Map>();
            List<Map> returnMap = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            statement.setString(2, DateUtil.tsformatter.format(start));
            statement.setString(3, DateUtil.tsformatter.format(end));
            rs = statement.executeQuery();
            while(rs.next())  {
                Map<String, String> qReaderCount = new HashMap<String, String>();
                qReaderCount.put("readerId", rs.getString(1));
                qReaderCount.put("hasEntered", rs.getString(2));
                qReaderCount.put("blueLane", rs.getString(3));
                returnMap.add(qReaderCount);
                //retVal.put("Reader", qReaderCount);
            }

            return returnMap;

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

    public List<Map> GetPEReaderEventsByEventType()
    {
        String sQuery = "{call dbo.usp_GetPEReaderEvents_ETL()}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            HashMap<String, Map> retVal = new HashMap<String, Map>();
            List<Map> returnMap = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);

            rs = statement.executeQuery();
            while(rs.next())  {
                Map<String, String> qReaderCount = new HashMap<String, String>();
                qReaderCount.put("readerId", rs.getString(1) );
                qReaderCount.put("baseTimestamp", rs.getString(2));
                qReaderCount.put("offset", rs.getString(3));
                qReaderCount.put("eventType", rs.getString(4));
                //retVal.put(EventType, qReaderCount);
                returnMap.add(qReaderCount);
            }

            return returnMap;

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
