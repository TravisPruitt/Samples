package com.disney.xband.xi.model;

import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DAO
{
	static Logger logger = Logger.getLogger(DAO.class);
	Gson gson;
	private static String dbConnectString = null;
    private static String dbUserName=null;
    private static String dbUserPassword=null;
    /*
	protected static Date PROGRAM_START_DATE = null;
    protected static String PROGRAM_START_DATE_STRING = null;
    */
    protected static HashMap<Integer, HashMap<Integer, Facility>> parkFacilityMap = new HashMap<Integer, HashMap<Integer, Facility>>();
    protected static HashMap<Integer, Integer> facilityToParkMap = new HashMap<Integer, Integer>();
    protected static List<Integer> parkList = new ArrayList<Integer>();

    private static int windowOffsetStart = -30;
    private static int windowOffsetEnd = 75;

    private static String fileUrl = "/etc/nge/config/environment.properties";
    private static Properties props;

    protected static ConnectionPool connectionPool;

    protected static int recruitEligibleRefreshInterval=60;
    protected static int RECRUITMENT_REFRESH_INTERVAL; // in milliseconds
    protected static int MEHA_REFRESH_INTERVAL; // in milliseconds
    protected static int ENTITLE_REFRESH_INTERVAL; // in milliseconds

    protected static int SUMMARY_REFRESH_INTERVAL; // in milliseconds
    protected static int HOURLY_REDEM_REFRESH_INTERVAL; // in milliseconds

    static
	{
        /*
        try {
            PROGRAM_START_DATE_STRING="2012-07-01 00:00:00";
            Date d = DateUtil.tsformatter.parse(PROGRAM_START_DATE_STRING);
            PROGRAM_START_DATE = d;
        } catch (ParseException e) {
            logger.error("Invalid date format during static init.");
        }*/


        props = null;

        // first, read the properties file to get the database parameters
        NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();

        // get property settings
        // is there is a system property to identify where the environment.properites is
        String sPropFile = System.getProperty("environment.properties");
        if (sPropFile != null)
        {
            logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
            decoder.setPropertiesPath(sPropFile);
        }
        else {
            sPropFile = fileUrl;
        }

        String sJasyptPropFile = System.getProperty("jasypt.properties");
        if (sJasyptPropFile != null)
        {
            logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
            decoder.setJasyptPropertiesPath(sJasyptPropFile);
        }

        try
        {
            props = decoder.read();
        }
        catch (Exception ex)
        {
            logger.fatal("!! Could not read the properties file [" +
                    decoder.getPropertiesPath() + "]: "
                    + ex.getLocalizedMessage());
        }

        if(props == null) {
             logger.fatal("!! logger is null !! ");
        }
        else {
            try {
                recruitEligibleRefreshInterval = Integer.parseInt(props.getProperty("nge.xconnect.xi.recruiting.eligibleRefreshInterval", "60").trim());
            }
            catch (NumberFormatException ex) {
                recruitEligibleRefreshInterval = 60;
            }

            try {
                ENTITLE_REFRESH_INTERVAL = Integer.parseInt(props.getProperty("nge.xconnect.xi.entitlement.cacheTimeout", "10000"));
            }
            catch (NumberFormatException ex) {
                ENTITLE_REFRESH_INTERVAL=10000;
            }

            try {
                HOURLY_REDEM_REFRESH_INTERVAL=Integer.parseInt(props.getProperty("nge.xconnect.xi.hourlyRedemption.cacheTimeout", "15000").trim()); // in milliseconds
                logger.debug("hourly refresh interval set to " + HOURLY_REDEM_REFRESH_INTERVAL);
            }
            catch (NumberFormatException ex) {
                HOURLY_REDEM_REFRESH_INTERVAL=15000;
            }

            try {
                SUMMARY_REFRESH_INTERVAL=Integer.parseInt(props.getProperty("nge.xconnect.xi.execsummary.cacheTimeout", "5000").trim()); // in milliseconds
                logger.debug("execsummary refresh interval set to " + SUMMARY_REFRESH_INTERVAL);
            }
            catch (NumberFormatException ex) {
                SUMMARY_REFRESH_INTERVAL=5000;
            }

            try{
                RECRUITMENT_REFRESH_INTERVAL =Integer.parseInt(props.getProperty("nge.xconnect.xi.recruitment.cacheTimeout", "15000").trim()); // in milliseconds
                logger.debug("recruitment refresh interval set to " + RECRUITMENT_REFRESH_INTERVAL);
            }
            catch (NumberFormatException ex) {
                RECRUITMENT_REFRESH_INTERVAL=30000;
            }

            try{
                MEHA_REFRESH_INTERVAL =Integer.parseInt(props.getProperty("nge.xconnect.xi.recruitmeha.cacheTimeout", "300000").trim()); // in milliseconds
                logger.debug("MEHA refresh interval set to " + MEHA_REFRESH_INTERVAL);
            }
            catch (NumberFormatException ex) {
                MEHA_REFRESH_INTERVAL=300000;
            }
            String driver = props.getProperty("nge.xconnect.xi.dbserver.driver");
            String dbHost = props.getProperty("nge.xconnect.xi.dbserver.host");
            String dbPort = props.getProperty("nge.xconnect.xi.dbserver.port").trim();
            dbUserName = props.getProperty("nge.xconnect.xi.dbserver.user");
            dbUserPassword = props.getProperty("nge.xconnect.xi.dbserver.password").trim();
            String dbName = props.getProperty("nge.xconnect.xi.dbname");

            String maxPoolSize = props.getProperty("nge.xconnect.xi.c3p0.maxPoolSize", "40").trim();
            String maxStatements = props.getProperty("nge.xconnect.xi.c3p0.maxStatements", "400").trim();
            String maxStatementsPerConn = props.getProperty("nge.xconnect.xi.c3p0.maxStatementsPerConnection", "50").trim();

            dbConnectString = "jdbc:jtds:sqlserver://" + dbHost + ":" + dbPort + "/" + dbName;

            logger.debug("Creating connection pool with poolSize:" + maxPoolSize
                   + " maxstatements:" + maxStatements
                   + " and maxStatementsPerConn:" + maxStatementsPerConn);

            try
            {
                logger.debug("DB Host is: " + dbHost);
                logger.debug("DB Name is: " + dbName);
                logger.debug("Driver set to: " + driver);
                Class.forName(driver);
                connectionPool = new ConnectionPool(driver, dbConnectString,  dbUserName, dbUserPassword,
                    maxPoolSize, maxStatements, maxStatementsPerConn);
                databaseInit();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
                e.printStackTrace();
            }
        }
	}

    protected static void getFacilitiesFromDb() {
        String sQuery = "{call [dbo].[usp_GetFacilitiesListETL]}";

        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = connectionPool.createConnection();
            statement = connection.prepareCall(sQuery);
            rs = statement.executeQuery();
            while(rs.next()) {
                Facility f = new Facility();
                int facilityId= rs.getInt(1);
                f.setFacilityId(facilityId);
                f.setName(rs.getString(2));
                f.setShortName(rs.getString(3));
                int parkId = rs.getInt(4);
                // set up our park list while we're at it
                if(!parkList.contains(parkId))   {
                    parkList.add(parkId);
                }
                // populate f->p mapping
                facilityToParkMap.put(facilityId, parkId);
                f.setParkId(parkId);
                f.setFacilityConfiguration(rs.getString(5));
                f.setFacilityConfigurationId(rs.getInt(6));
                // TODO -- add parkId to sproc and here
                if(parkFacilityMap.containsKey(parkId)) {
                    HashMap<Integer, Facility> fm = parkFacilityMap.get(parkId);
                    fm.put(f.getFacilityId(), f);
                }
                else  {
                    HashMap<Integer, Facility> fm = new HashMap<Integer, Facility>();
                    fm.put(f.getFacilityId(), f);
                    parkFacilityMap.put(parkId, fm);
                }
            }
            // check size of hashmap
            if( parkFacilityMap.size() <= 0 ) {
                logger.error("empty facilities map created on init!");
            }
        }
        catch (SQLException sqle)
        {
            logger.error("getFacilityListFromDB error, " + sqle.getMessage());
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

    /*
    public static String getProgramStartDateAsString() {
        return PROGRAM_START_DATE_STRING;
    }*/

    /*
    public static Date getProgramStartDate() {
         return PROGRAM_START_DATE;
    }
    */

    protected static Date getPoolCreationDate() {
        return connectionPool.getCreateDate();
    }
    protected static HashMap<String, Integer> getDbState() {
        return connectionPool.getState();
    }

    protected static boolean resetDb() {
        return connectionPool.hardReset();
    }

    /*
    private static void getStartDateFromDb() {
        String sQuery = "{call [dbo].[usp_GetProgramStartDateETL]}";

        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = connectionPool.createConnection();
            statement = connection.prepareCall(sQuery);
            rs = statement.executeQuery();

            if(rs.next()) {
                String s  = rs.getString(1);
                try {
                    Date d = DateUtil.tstzformatter.parse(s);
                    PROGRAM_START_DATE = d;
                    PROGRAM_START_DATE_STRING = DateUtil.tsformatter.format(d);
                } catch (ParseException e) {
                    logger.error("Invalid date format.  Found:" + s + " -- expected:" + DateUtil.tstzformatter.toLocalizedPattern());
                }
            }
            else {
                logger.error("No value set in db for PROGRAM_START_DATE");
            }
        }
        catch (SQLException sqle)
        {
            logger.error("getStartDateFromdb error, " + sqle.getMessage());
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

    public static int getWindowOffsetStart() {
        return windowOffsetStart;
    }

    public static int getWindowOffsetEnd() {
        return windowOffsetEnd;
    }



    protected static void initWindowOffsets() {
        String sQuery = "{call [dbo].[usp_GetWindowOffsetsETL]}";

        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = connectionPool.createConnection();
            statement = connection.prepareCall(sQuery);
            rs = statement.executeQuery();

            if(rs.next()) {
                windowOffsetStart = rs.getInt(1);
                windowOffsetEnd =  rs.getInt(2);
                logger.debug("set windowOffset start:" + windowOffsetStart + " end:" + windowOffsetEnd);
            }
            else {
                logger.error("No value set in db for window offsets");
            }
        }
        catch (SQLException sqle)
        {
            logger.error("initWindowOffsets error, " + sqle.getMessage());
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

    protected static void databaseInit() {
        // get facilityMap
        getFacilitiesFromDb();

        initWindowOffsets();
        //logger.info("facilitiesMap populated with " + facilityMap.keySet().size() + " entries");
        // set the todate/pilot start date for queries
        //getStartDateFromDb();
        //logger.info("program start date set to: " + PROGRAM_START_DATE);
        logger.info("db init completed");
    }

	public DAO()
	{
		gson = new Gson();
	}

	protected Connection getConnection() throws DAOException
	{
		Connection conn;
        conn = connectionPool.createConnection();
        return conn;
	}

	public String toJSON(Object o)
	{
		return this.gson.toJson(o);
	}

	public String errorMessage(String msg, String source, String buster) {
		logger.error(msg);
		return "remoteData(" + gson.toJson(new ErrorMessage(msg, source, buster)) + ");";
	}
	
	public String errorMessage(String msg, String buster) {
		logger.error(msg);
		return "remoteData(" + gson.toJson(new ErrorMessage(msg, buster)) + ");";
	}

    public String errorMessage(String msg) {
		logger.error(msg);
		return "remoteData(" + gson.toJson(new ErrorMessage(msg)) + ");";
	}

    public Facility getFacilityById(int facilityId) {
        return parkFacilityMap.get(facilityToParkMap.get(facilityId)).get(facilityId);
    }

    protected GregorianCalendar initCalendar(Date sDate) throws DAOException
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(sDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal;
    }


}
