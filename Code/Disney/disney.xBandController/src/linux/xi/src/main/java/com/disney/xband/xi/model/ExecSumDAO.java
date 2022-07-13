package com.disney.xband.xi.model;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExecSumDAO extends DAO
{
    QueueDAO qd = null;

    private static final int MAX_NUM_OFFERSETS=4;
    private static final String S_SEL_OFFSET="selected_offerset";
    private static final String S_RED_OFFSET="redeemed_offerset";
    private static final String RECRUIT_GET_PROJECTED = "{call dbo.usp_ProjectedDataETL(?,?)}";

    private static Map<Integer, List<Integer[]>> parkCalendarDataMap = new ConcurrentHashMap<Integer, List<Integer[]>>();
    private static Map<Integer, Date> parkCalendarLastRefresh = new ConcurrentHashMap<Integer, Date>();
    private static List<Integer[]> calendarData = null;

    private static Date dateYesterday = new DateTime(1970, 1,1,0,0).toDate();

    private static final Object cacheLock  = new Object();

    private static Map<Integer, ReturnMessage> parkMsgCacheMap = new ConcurrentHashMap<Integer, ReturnMessage>();
    private static Map<Integer, Long> parkMsgTimeMap = new ConcurrentHashMap<Integer, Long>();

    private static Map<Integer, List<Map>> parkHourlyRedemCacheMap = new ConcurrentHashMap<Integer, List<Map>>();
    private static Map<Integer, Long> parkHourlyRedemTimeMap = new ConcurrentHashMap<Integer, Long>();
    private static Map<Integer, Map<String, Object>> parkYesterdayCacheMap = new ConcurrentHashMap<Integer, Map<String, Object>>();

    // offerset caches
    private static Map<Integer, Map<String, List<Integer>>> parkHistOffersetData = new ConcurrentHashMap<Integer, Map<String, List<Integer>>>();
    private static Map<Integer, Date> parkOffersetRefreshDates = new ConcurrentHashMap<Integer, Date>();

	public ExecSumDAO()
	{
		super();

        qd = new QueueDAO();
	}

    public static void clearCache() {
        // clear out all the objects
        parkMsgTimeMap.clear();
        parkHourlyRedemTimeMap.clear();
        parkYesterdayCacheMap.clear();
        parkOffersetRefreshDates.clear();
    }

	public String getCurrentExecSummary(int parkId, Date currentTime, String label, String buster)
	{
        long currentTimeMillis = System.currentTimeMillis();
        long cachedMessageTime;

        String waitTimesResult = "";


        if(parkMsgTimeMap.containsKey(parkId)) {
            cachedMessageTime = parkMsgTimeMap.get(parkId);
        }
        else {
            cachedMessageTime = currentTimeMillis;
            parkMsgTimeMap.put(parkId, currentTimeMillis);
        }


        try
        {
            waitTimesResult =  ", \"waittimes\": " + getWaitTimesForParkLocal(String.valueOf(parkId), buster) + "}";
        }
        catch (Exception ex)
        {
            logger.error(ex.getLocalizedMessage());
        }

        ReturnMessage cachedMessage = parkMsgCacheMap.get(parkId);

        if((currentTimeMillis - cachedMessageTime) > SUMMARY_REFRESH_INTERVAL ||
                cachedMessage == null ) {
            logger.debug("data too old --hitting database");
            try
            {


                ReturnMessage rmsg = new ReturnMessage();
                rmsg.setBuster(buster);
                rmsg.addData("date", label);
                rmsg.setParkId(parkId);
                Map<String, Object> todayMap = this.getDaysData(
                        parkId,
                        DateUtil.tsformatter.format(DateUtil.setDayStartForDate(currentTime)),
                        DateUtil.tsformatter.format(currentTime));

                long cachedHourlyRedemTime;

                if(parkHourlyRedemTimeMap.containsKey(parkId)) {
                    cachedHourlyRedemTime = parkHourlyRedemTimeMap.get(parkId);
                }
                else {
                    cachedHourlyRedemTime = currentTimeMillis;
                    parkHourlyRedemTimeMap.put(parkId, currentTimeMillis);
                }
                List<Map> cachedHourlyRedemList = parkHourlyRedemCacheMap.get(parkId);

                // test if too old
                if(currentTimeMillis-cachedHourlyRedemTime > HOURLY_REDEM_REFRESH_INTERVAL || cachedHourlyRedemList == null) {

                    cachedHourlyRedemList= hourlyRedemptionTotal(parkId, DateUtil.setDayStartForDate(currentTime), currentTime);

                    // reset the cache
                    parkHourlyRedemTimeMap.put(parkId, currentTimeMillis);
                    parkHourlyRedemCacheMap.put(parkId, cachedHourlyRedemList);
                }
                // put the value into the map
                todayMap.put("redemptions", cachedHourlyRedemList);
                rmsg.addData("today", todayMap );

                Map<String, Map> hm = qd.getQueueCountBreakdown(parkId, currentTime);
                rmsg.addData("subway", hm);

                // returns yesterday with times between 00:00:00 as [0]
                //  and current time passed in as [1]
                Date d[] = DateUtil.getYesterday(currentTime);

                // start HERE with refactor for parkId
                Map<String, Object> yesterdayExecSummary = parkYesterdayCacheMap.get(parkId);
                if( (yesterdayExecSummary != null) && (dateYesterday.compareTo(d[0]) == 0)) {
                    logger.info("using cached data for yesterday execsummary");
                }
                else {
                    logger.info("retrieving yesterday data for execsummary");
                    String y[] = DateUtil.getYesterdayAsStrings(currentTime);
                    List<Map> yRedemList = hourlyRedemptionTotal(parkId, d[0], d[1]);
                    yesterdayExecSummary = this.getDaysData(parkId, y[0], y[1]);
                    yesterdayExecSummary.put("redemptions", yRedemList);
                    parkYesterdayCacheMap.put(parkId, yesterdayExecSummary);
                    // always use the 00:00:00 time
                    dateYesterday = d[0];
                }
                rmsg.addData("yesterday", yesterdayExecSummary);

                parkMsgTimeMap.put(parkId, currentTimeMillis);
                parkMsgCacheMap.put(parkId, rmsg);

                String rms = gson.toJson(rmsg);
                rms = rms.substring(0, rms.length()-1);

                return "remoteData(" + rms + waitTimesResult +  ");";
            }
            catch (SQLException sqle)
            {
                logger.error(sqle.getLocalizedMessage());
                return this.errorMessage(sqle.getMessage(), "execsummary", buster);
            }
            catch (DAOException dao)
            {
                logger.error(dao.getLocalizedMessage());
                return this.errorMessage(dao.getMessage(), "execsummary", buster);
            }
        }
        else {
            logger.info("using cached");
            // set new timestamp on cachedMessage
            cachedMessage.setMessageTimeStamp(System.currentTimeMillis());
            cachedMessage.setBuster(buster);
            cachedMessage.addData("date", label);
            // return cachedMessage
            String rms = gson.toJson(cachedMessage);
            rms = rms.substring(0, rms.length()-1);

            return "remoteData(" + rms + waitTimesResult + ");";
        }

    }

    private Map<String, Object> getDaysData(int parkId, String startTime, String endTime)
            throws SQLException, DAOException
    {
        return getDaysData(parkId, startTime, endTime, true);
    }

    private List<Integer> initArrayWithZeroes(int size) {
        List<Integer> l = new ArrayList<Integer>(size);
        for(int x = 0; x<size; x++) {
            l.add(0);
        }
        return l;
    }



    private Map<String, List<Integer>> processOffersetForSpecifiedDay(Connection connection, int parkId, String startTime, String endTime)
            throws SQLException, DAOException
    {
        CallableStatement statement = null;
        ResultSet rs = null;
        String sGetSelOffersets = "{call dbo.usp_GetSelectedOffersetsETL(?,?,?)}";
        String sGetRedOffersets = "{call dbo.usp_GetRedeemedOffersetsETL(?,?,?)}";
        //String sGetRedOvrOffersets = "{call dbo.usp_GetRedeemedOverrideOffersets(?,?,?)}";

        Map<String, List<Integer>> osMap = new ConcurrentHashMap<String, List<Integer>>();
        List<Integer> selectedOfferset = initArrayWithZeroes(4);
        List<Integer> redeemedOfferset = initArrayWithZeroes(4);
        int iCount =0;

        try {
            statement = connection.prepareCall(sGetSelOffersets);
            statement.setString(1, startTime);
            statement.setString(2, endTime);
            statement.setInt(3, parkId);
            rs = statement.executeQuery();
            while(rs.next() && iCount < MAX_NUM_OFFERSETS){
                selectedOfferset.set(iCount, selectedOfferset.get(iCount) + rs.getInt(2));
                iCount++;
            }
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
        }

        rs = null;
        statement = null;

        try {
            statement = connection.prepareCall(sGetRedOffersets);
            statement.setString(1, startTime);
            statement.setString(2, endTime);
            statement.setInt(3, parkId);
            rs = statement.executeQuery();

            iCount =0;
            while(rs.next()){
                redeemedOfferset.set(iCount,  redeemedOfferset.get(iCount) + rs.getInt(2));
                iCount++;
            }

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
        }

        /*
        rs = null;
        statement = null;


        try {
            // add the early late calls
            statement = connection.prepareCall(sGetRedOvrOffersets);
            statement.setString(2, endTime);
            statement.setString(1, startTime);
            statement.setString(2, endTime);
            rs = statement.executeQuery();

            iCount =0;
            while(rs.next()){
                int i=redeemedOfferset.get(iCount);
                redeemedOfferset.set(iCount,  redeemedOfferset.get(iCount) + rs.getInt(2));
                iCount++;
            }
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
        }*/
        osMap.put(S_SEL_OFFSET, selectedOfferset);
        osMap.put(S_RED_OFFSET, redeemedOfferset);
        return osMap;
    }

    private List<Integer> addListToList(List<Integer> source, List<Integer> target) {
        for(int i=0; i<source.size(); i++) {
            target.set(i, (target.get(i) + source.get(i)));
        }
        return target;
    }

    private Map<String, List> getOffersetsForDate(int parkId, Date startDate, Date endDate)
            throws SQLException, DAOException
    {
 		Connection connection = null;

        try {
            connection = this.getConnection();
            Map<String, List<Integer>> historicOffersetData = parkHistOffersetData.get(parkId);
            Date offersetRefreshDate = parkOffersetRefreshDates.get(parkId);
            Map<String, List<Integer>> todayOffersetData; //= parkTodayOffersetData.get(parkId);

            if((historicOffersetData != null) &&
                    (DateUtil.setDayStartForDate(endDate).compareTo(offersetRefreshDate) <= 0)) {

                // just update today's data -- todayOffersetData
                // so adjust the dates
                logger.debug("using cached offerset data");
                // switch
                DateMidnight dm = new DateMidnight(endDate);
                startDate = dm.toDate();  // same day as enddate, time of midnight 00:00:00

                todayOffersetData=processOffersetForSpecifiedDay(connection, parkId,
                        DateUtil.tsformatter.format(startDate),
                        DateUtil.tsformatter.format(endDate));
            }
            else { // else redo the whole shebang
                historicOffersetData = new ConcurrentHashMap<String, List<Integer>>();
                todayOffersetData = new ConcurrentHashMap<String, List<Integer>>();
                parkOffersetRefreshDates.put(parkId, new Date());

                DateTime currentDT= new DateTime(new DateMidnight(startDate).toDate());
                DateMidnight dmLastday = new DateMidnight(endDate);

                List<Integer> historicSelected = initArrayWithZeroes(4);
                List<Integer> historicRedeemed = initArrayWithZeroes(4);

                // loop from startdate to enddate, adding 1 day each iteration
                while(currentDT.isBefore(endDate.getTime())) {
                    if(currentDT.isBefore(dmLastday)) { // check if last "day" of the loop
                        todayOffersetData=processOffersetForSpecifiedDay(connection, parkId,
                                DateUtil.tsformatter.format(currentDT),
                                DateUtil.tsformatter.format(endDate)
                        );
                    }
                    else { // else
                        // one day at a time
                        Map<String, List<Integer>> m = processOffersetForSpecifiedDay(connection, parkId,
                                DateUtil.tsformatter.format(currentDT),
                                DateUtil.tsformatter.format(DateUtil.setDayEndForDate(currentDT.toDate()))
                        );
                        List<Integer> l = m.get(S_SEL_OFFSET);
                        addListToList(l, historicSelected);

                        List<Integer> r = m.get(S_RED_OFFSET);
                        addListToList(r, historicRedeemed);
                    }
                    currentDT = currentDT.plusDays(1);
                }

                historicOffersetData.put(S_SEL_OFFSET, historicSelected);
                historicOffersetData.put(S_RED_OFFSET, historicRedeemed);
                parkHistOffersetData.put(parkId, historicOffersetData);
            }

            // now merge historic with today
            Map<String, List> osMap = new ConcurrentHashMap<String, List>();
            List<Integer> ts = todayOffersetData.get(S_SEL_OFFSET);
            List<Integer> tr = todayOffersetData.get(S_RED_OFFSET);

            List<Integer> hs = historicOffersetData.get(S_SEL_OFFSET);
            List<Integer> hr = historicOffersetData.get(S_RED_OFFSET);

            List<Integer> retSelected = new ArrayList<Integer>(4);
            List<Integer> retRedeemed = new ArrayList<Integer>(4);

            for(int i=0; i<ts.size(); i++){
                logger.debug("selected ts:" + ts.get(i) + " hs:" + hs.get(i));
                retSelected.add((ts.get(i)+ hs.get(i))) ;
                logger.debug("redeemed tr:" + tr.get(i) + " hr:" + hr.get(i));
                retRedeemed.add((tr.get(i)+ hr.get(i))) ;
            }
            osMap.put(S_SEL_OFFSET, retSelected);
            osMap.put(S_RED_OFFSET, retRedeemed);
            return osMap;
        }
        finally
        {
            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception ignore) {
                }
            }
        }
    }


	private Map<String, Object> getDaysData(int parkId, String startTime, String endTime, boolean fGetOffersets)
		throws SQLException, DAOException
	{
		String sQuery = "{call dbo.usp_GetExecSummaryETL(?,?,?)}";
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
        Map<String, Object> summaryMap = new ConcurrentHashMap<String, Object>();

		try
		{
			connection = this.getConnection();

			statement = connection.prepareCall(sQuery);
            logger.debug("getDaysData: " + startTime + "," + endTime);
			statement.setString(1, startTime);
			statement.setString(2, endTime);
            statement.setInt(3, parkId);

			rs = statement.executeQuery();
			
			rs.next();
            summaryMap.put("park", parkId);
    		summaryMap.put("selected", rs.getInt(1));
            // redeemed INCLUDES early/late overrides from sproc
    		summaryMap.put("redeemed", rs.getInt(2));
    		summaryMap.put("participants", rs.getInt(3));
    		summaryMap.put("guests in queue", rs.getInt(4));
            summaryMap.put("total overrides", rs.getInt(5));
            summaryMap.put("early/late overrides", rs.getInt(6));
            summaryMap.put("total bluelanes", rs.getInt(7));
            summaryMap.put("participants in park", rs.getInt(8));
            summaryMap.put("selected in park", rs.getInt(9));

            // clean up the rs and statement
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

            if(fGetOffersets) {
                Map<String, List<Integer>> os = processOffersetForSpecifiedDay(connection, parkId, startTime, endTime);
                summaryMap.put(S_SEL_OFFSET, os.get(S_SEL_OFFSET));
                summaryMap.put(S_RED_OFFSET, os.get(S_RED_OFFSET));
            }

            rs = null;
            statement = null;

            statement = connection.prepareCall(RECRUIT_GET_PROJECTED);
            statement.setString(1, endTime);
            statement.setInt(2, parkId);
            rs = statement.executeQuery();
            Map<String, Integer> projection = new ConcurrentHashMap<String, Integer>();
            if (rs.next()) {
                projection.put("selected", rs.getInt(1));
            }
            summaryMap.put("projected", projection);

		}
        catch(SQLException e) {
            logger.error("Projected: " + e.getLocalizedMessage());
            summaryMap.put("projected", -1);
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
        return summaryMap;
	}


    public String getExecSummaryTodate(int parkId, String dateLabel, Date startDate, Date endDate, String buster) {
 		// {call rdr.usp_Event_Create(?,?,?,?,?,?,?,?)}
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.setParkId(parkId);
		try
		{
            // get the rest of the data
			Map<String, Object> dayMap = this.getDaysData(parkId,
                     DateUtil.tsformatter.format(startDate), DateUtil.tsformatter.format(endDate), false );

            Map osMap = getOffersetsForDate(parkId, startDate, endDate);

            // okay, now we have to run all of those offerset queries
            // in a loop for each day covered in the
            dayMap.put(S_SEL_OFFSET, osMap.get(S_SEL_OFFSET));
            dayMap.put(S_RED_OFFSET, osMap.get(S_RED_OFFSET));

			rmsg.addData("date", dateLabel);
			rmsg.addData("today", dayMap );

			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "execsummary", buster);
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "execsummary", buster);
		}
    }

	public String getExecSummary(int parkId, String dateLabel, Date startDate, Date endDate, String buster)
	{
		// {call rdr.usp_Event_Create(?,?,?,?,?,?,?,?)}
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.setParkId(parkId);
		try
		{
			Map<String, Object> dayMap = this.getDaysData( parkId,
                DateUtil.tsformatter.format(startDate),
                DateUtil.tsformatter.format(endDate)
            );
			rmsg.addData("date", dateLabel);
			rmsg.addData("today", dayMap );

			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "execsummary", buster);
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "execsummary", buster);
		}
	}


	public String getCalendar(int parkId, Date endDate, int number_days, String buster)
	{
		ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.setParkId(parkId);
		String sQueryRedeemed = "{call dbo.usp_GetRedeemedForCalETL(?,?,?)}";
		Connection connection=null;
		CallableStatement statement = null;
		ResultSet rs = null;

        // need to calculate the whole shebang
        List<Integer[]> calendarData=null;
		try
		{
			connection = this.getConnection();
            statement = connection.prepareCall(sQueryRedeemed);

            if(parkCalendarDataMap.containsKey(parkId)) {
                calendarData = parkCalendarDataMap.get(parkId);
            }

            Date currentDate = DateUtil.setDayStartForDate(endDate);
            Date calendarLastFullRefresh;
            if (parkCalendarLastRefresh.containsKey(parkId)){
                calendarLastFullRefresh = parkCalendarLastRefresh.get(parkId);
            }
            else {
                calendarLastFullRefresh= currentDate;
            }

            if((calendarData != null) && (currentDate.compareTo(calendarLastFullRefresh) < 0)) {
                logger.info("refreshing current day's calendar only");
                number_days = 1;
            	statement.setInt(1, number_days);
			    statement.setString(2, DateUtil.tsformatter.format(endDate));
                statement.setInt(3, parkId);
                logger.debug("redeemedforcal " + endDate);
			    rs = statement.executeQuery();
                rs.next();
                Integer i[] = { rs.getInt(2), rs.getInt(3) };
                calendarData.set(0, i);
            }
            else {
                // do a full refresh
                logger.info("doing a full calendar pull for "+ number_days + "days");
                List<Integer[]> lData = new ArrayList<Integer[]>();
                statement.setInt(1, number_days);
			    statement.setString(2, DateUtil.tsformatter.format(endDate));
                statement.setInt(3, parkId);

			    rs = statement.executeQuery();

                while (rs.next())
                {
                    // (1) IS TIMESTAMP that we don't use
                    Integer i[] = { rs.getInt(2), rs.getInt(3)};
                    lData.add(i);
                }
                parkCalendarDataMap.put(parkId, lData);
                parkCalendarLastRefresh.put(parkId, DateUtil.setDayEndForDate(DateUtil.getCurrentDate()));
                calendarData = lData;
            }

        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "getcal - execsummary", buster);
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "getcal - execsummary", buster);
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

        rmsg.addData("calendar", calendarData);
        return "remoteData(" + gson.toJson(rmsg) + ");";
	}


    private List<Map> hourlyRedemptionTotal(int parkId, Date startTime, Date endTime)
            throws DAOException, SQLException {

        String sQuery = "{call dbo.usp_GetHourlyRedemptionsETL(?,?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = this.getConnection();
            List<Map> redemList = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, DateUtil.tsformatter.format(startTime));
            statement.setInt(2, parkId);
            rs = statement.executeQuery();

            while(rs.next()){
                Map<String, Integer>hourRedems=new HashMap<String, Integer>();
                int curHour=rs.getInt(1);
                hourRedems.put("hour", curHour);
                hourRedems.put("selected", rs.getInt(2));
                hourRedems.put("redeemed", rs.getInt(3));
                redemList.add(hourRedems);
            }

            return redemList;
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


    public String getWaitTimesForPark(String parkId, String cacheBuster) throws SQLException, DAOException
    {
        String sQuery = "{call dbo.usp_GetWaitTimes_ETL(?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = this.getConnection();
            List<Map> waitTimes = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            rs = statement.executeQuery();

            while(rs.next()){
                Map<String, Integer>times=new HashMap<String, Integer>();
                times.put("parkId", rs.getInt(1));
                times.put("FacilityId", rs.getInt(2));
                times.put("E2M_FP", rs.getInt(3));
                times.put("E2M_SB", rs.getInt(4));
                times.put("E2L_FP", rs.getInt(5));
                times.put("E2L_SB", rs.getInt(6));
                times.put("M2L", rs.getInt(7));
                waitTimes.add(times);
            }

            //return "remoteData(" + gson.toJson(rmsg) + ");";
            return "remoteData(" + gson.toJson(waitTimes) + ");";
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

    public String getWaitTimesForParkLocal(String parkId, String cacheBuster) throws SQLException, DAOException
    {
        String sQuery = "{call dbo.usp_GetWaitTimes_ETL(?)}";
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        try {
            connection = this.getConnection();
            List<Map> waitTimes = new ArrayList<Map>();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, parkId);
            rs = statement.executeQuery();

            while(rs.next()){
                Map<String, Integer>times=new HashMap<String, Integer>();
                times.put("parkId", rs.getInt(1));
                times.put("FacilityId", rs.getInt(2));
                times.put("E2M_FP", rs.getInt(3));
                times.put("E2M_SB", rs.getInt(4));
                times.put("E2L_FP", rs.getInt(5));
                times.put("E2L_SB", rs.getInt(6));
                times.put("M2L", rs.getInt(7));
                waitTimes.add(times);
            }

            //return "remoteData(" + gson.toJson(rmsg) + ");";
            return  gson.toJson(waitTimes);
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

    public String getHourlyRedemptionTotal(int parkId, Date startTime, Date endTime, String label, String buster) {
        ReturnMessage rmsg = new ReturnMessage();
        rmsg.setBuster(buster);
        rmsg.setParkId(parkId);
        try
        {
            List<Map> redemList = hourlyRedemptionTotal(parkId, startTime, endTime);
            rmsg.addData("data", redemList);
            rmsg.addData("date", label);
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "hourly redem", buster);
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "hourly redem", buster);
        }

    }







}
