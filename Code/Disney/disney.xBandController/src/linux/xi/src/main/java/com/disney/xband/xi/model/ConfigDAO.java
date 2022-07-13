package com.disney.xband.xi.model;

import com.google.gson.Gson;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class ConfigDAO extends DAO
{
    private static long cacheDbStateTime = 0l;
    private static int cacheDbStateTimeout = 1000;
    private static ReturnMessage cachedDbStateMsg;

    /* one-time init of strings/metadata for Etl Operational Monitoring */
    private static final String s_etlRecency = "etlrecency";
    private static final String s_redemFresh = "redemfresh";
    private static final String s_recruFresh = "recrufresh";
    private static final String s_queueFresh = "queuefresh";
    private static final String s_etlErrorCount = "etlerrorct";

    private static String dbEtlMetaDataString = null;
    static  {
        HashMap<String, String> dbEtlMetaData = null;
        dbEtlMetaData = new HashMap<String, String>(5);
        dbEtlMetaData.put(s_etlRecency, "ETL Recency In Seconds");
        dbEtlMetaData.put(s_redemFresh,"Redemption Data Freshness In Seconds");
        dbEtlMetaData.put(s_recruFresh, "Recruitment Data Freshness In Seconds");
        dbEtlMetaData.put(s_etlErrorCount, "ETL Error Counts");
        dbEtlMetaData.put(s_queueFresh, "Queue Counts Freshness In Seconds" );
        Gson gson = new Gson();
        dbEtlMetaDataString = gson.toJson(dbEtlMetaData);
    }

    public String getCurrentDbState(String buster) {
        ReturnMessage rmsg;
        long now = System.currentTimeMillis();
        if( cachedDbStateMsg == null  || now - cacheDbStateTime > cacheDbStateTimeout) {
            rmsg = new ReturnMessage();
            rmsg.setBuster(buster);
            rmsg.addData("dbstate", getDbState());
            rmsg.addData("poolcreatedate", getPoolCreationDate());

            boolean fCanQueryDb=false;
            try {
                Timestamp ts = pingDatabase();
                if(ts != null)
                    fCanQueryDb=true;
            }
            catch (SQLException e) {
                logger.error("ping database error: " + e.getLocalizedMessage());
            } catch (DAOException e) {
                logger.error("ping database error: " + e.getLocalizedMessage());
            }
            rmsg.addData("dbping", fCanQueryDb);
            cachedDbStateMsg=rmsg;
            cacheDbStateTime=now;
        }
        else {
            rmsg=cachedDbStateMsg;
        }
        return "remoteData(" + gson.toJson(rmsg) + ");";
    }

    public String hardResetConnectionPool() {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.addData("db reset succeeded", resetDb());
        return "remoteData(" + gson.toJson(rmsg) + ");";
    }

    /*
    public String updateProgramStartDate(String d) {
        String sQuery = "{call dbo.usp_UpdateProgramStartDateETL(?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            Date dObj=DateUtil.tstzformatter.parse(d);

            connection = getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, d);
            statement.execute();
            PROGRAM_START_DATE = dObj;
            PROGRAM_START_DATE_STRING = DateUtil.tsformatter.format(dObj);
            rmsg.addData("programstartdate", d);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            logger.error("updateStartDate error, " + sqle.getMessage());
            return this.errorMessage(sqle.getMessage(), "updateStartDate");
        } catch (DAOException e) {
            logger.error("updateStartDate error, " + e.getMessage());
            return this.errorMessage(e.getMessage(), "updateStartDate");
        } catch (ParseException e) {
            logger.error("updateStartDate error, " + e.getMessage());
            return this.errorMessage(e.getMessage(), "updateStartDate");
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
    */

    public HashMap<Integer, Facility> getFacilityMapByPark(int parkId) {
        return parkFacilityMap.get(parkId);
    }

    public String getFacilityMapByParkAsString(int parkId, String cacheBuster) {
        ReturnMessage rm = new ReturnMessage();
        rm.setBuster(cacheBuster);
        rm.setParkId(parkId);
        rm.addData("facilities", parkFacilityMap.get(Integer.valueOf(parkId)));
        return "remoteData(" + gson.toJson(rm) + ");";
    }



    public HashMap getFacilityMap() {
        return parkFacilityMap;
    }

    public String getFacilityMapAsString(String cacheBuster) {
        ReturnMessage rm = new ReturnMessage();
        rm.setBuster(cacheBuster);
        rm.addData("facilities", parkFacilityMap);
        return "remoteData(" + gson.toJson(rm) + ");";
    }

    public String getParameter(String paramName) {
        String sQuery = "{call dbo.usp_ConfigGetParameterETL(?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, paramName);
            rs=statement.executeQuery();

            if(rs.next()) {
                rmsg.addData(paramName, rs.getString(1));
            }
            else {
                throw new DAOException("No parameter matched:" + paramName);
            }
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            logger.error("getParameter error, " + sqle.getMessage());
            return this.errorMessage(sqle.getMessage(), "getParameter");
        } catch (DAOException e) {
            logger.error("getParameter error, " + e.getMessage());
            return this.errorMessage(e.getMessage(), "getParameter");
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

    public String persistParameter(String paramName, String paramValue) {
        String sQuery = "{call dbo.usp_ConfigPersistParamETL(?,?)}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, paramName);
            statement.setString(2, paramValue);
            statement.execute();
            rmsg.addData("param updated", "");
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            logger.error("persistParameter error, " + sqle.getMessage());
            return this.errorMessage(sqle.getMessage(), "persistParameter");
        } catch (DAOException e) {
            logger.error("persistParameter error, " + e.getMessage());
            return this.errorMessage(e.getMessage(), "persistParameter");
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

    public String getRecruitmentWindow() {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.addData("windowStart", getWindowOffsetStart());
        rmsg.addData("windowEnd", getWindowOffsetEnd());
        return "remoteData(" + gson.toJson(rmsg) + ");";
    }

    public String clearCache() {
        // tick through all caches here -- nulling them out.
        logger.info("caches cleared");
        ExecSumDAO.clearCache();
        RecruitmentDAO.clearCache();
        EntitlementDAO.clearCache();
        return "OK";
    }

    public String reload() {
        // tick through all caches here -- nulling them out.
        logger.info("reload called.  database reinit performed and caches cleared");
        databaseInit();
        ExecSumDAO.clearCache();
        RecruitmentDAO.clearCache();
        EntitlementDAO.clearCache();
        return "OK";
    }
    public List getParks(){
        return parkList;
    }

    public String getParksAsString(String cacheBuster) {
        ReturnMessage rm = new ReturnMessage();
        rm.setBuster(cacheBuster);
        rm.addData("parks", parkList);
        return "remoteData(" + gson.toJson(rm) + ");";
    }

    public Timestamp pingDatabase() throws DAOException, SQLException {
        Connection connection = null;
        String query = "select getdate()";
        Statement s = null;
        ResultSet rs = null;

        try
        {
            connection = this.getConnection();
            if(connection == null) return null;
            s = connection.createStatement();
            rs = s.executeQuery(query);
            rs.next();
            return rs.getTimestamp(1);
        }
        finally
        {
            if (rs != null)
                connectionPool.close(rs);

            if(s != null) {
                try {
                    s.close();
                }
                catch (Exception ignore) {
                }
            }
            connectionPool.release(connection);
        }
    }



    public String dbETLFreshMetaData() {
        return dbEtlMetaDataString;
    }

    public String dbEtlFreshness() {
        String sQuery =  "{call dbo.usp_ETLHealthInfo}";

        ReturnMessage rmsg = new ReturnMessage();
        Connection connection = null;

        CallableStatement statement = null;
        ResultSet rs = null;

        try {
            connection = this.getConnection();
            statement = connection.prepareCall(sQuery);
            rs=statement.executeQuery();

            if(rs.next()) {
                HashMap<String, Double>etlOpsMap = new HashMap<String, Double>();
                rmsg.addData(s_etlRecency, rs.getDouble(1));
                rmsg.addData(s_redemFresh, rs.getDouble(2));
                rmsg.addData(s_recruFresh, rs.getDouble(3));
                rmsg.addData(s_queueFresh, rs.getDouble(4));
                rmsg.addData(s_etlErrorCount, rs.getDouble(5));
                return gson.toJson(rmsg);
            }
            else {
                return gson.toJson(this.errorMessage("No data returned"));
            }
        }
        catch (SQLException sqle) {
                logger.error("persistParameter error, " + sqle.getMessage());
                return this.errorMessage(sqle.getMessage(), "persistParameter");
        }
        catch (DAOException e) {
                logger.error("persistParameter error, " + e.getMessage());
                return this.errorMessage(e.getMessage(), "persistParameter");
        }
        finally
        {
            if (rs != null)
                connectionPool.close(rs);

            if(statement != null) {
                try {
                    statement.close();
                }
                catch (Exception ignore) {
                }
            }
            connectionPool.release(connection);
        }
    }

    public String dbTest()
    {
        ReturnMessage rmsg = new ReturnMessage();
        try
        {
            Timestamp ts = pingDatabase();
            if(ts != null) {
                rmsg.addData("timestamp", ts);
                return gson.toJson(rmsg);
            }
            else {
                return this.errorMessage("failed to connect", "dbtest");
            }
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "testdao");
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "testdao");
        }
    }

}
