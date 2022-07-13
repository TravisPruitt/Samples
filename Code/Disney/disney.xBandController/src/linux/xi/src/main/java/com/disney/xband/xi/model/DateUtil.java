package com.disney.xband.xi.model;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
* encapsulate a lot of random date handling utility code
*/
public class DateUtil
{
	static Logger logger = Logger.getLogger(DateUtil.class);

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat tsformatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

	public static final String TIMESTAMP_FORMATW = "yyyy-MM-dd'T'HH:mm:ss";
    public static final java.text.SimpleDateFormat tswformatter= new java.text.SimpleDateFormat(TIMESTAMP_FORMATW);

	public static final String TIMESTAMP_FORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final java.text.SimpleDateFormat tstzformatter = new java.text.SimpleDateFormat(TIMESTAMP_FORMAT_TZ);

    public static final String TIMESTAMP_FORMAT_TZQ = "yyyy-MM-dd HH:mm:ssZ";
    public static final java.text.SimpleDateFormat tstzqformatter = new java.text.SimpleDateFormat(TIMESTAMP_FORMAT_TZQ);

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final java.text.SimpleDateFormat dateformatter = new java.text.SimpleDateFormat(DATE_FORMAT);

    public static Date[] getYesterday(Date d) {

        // get yesterday, at time 00:00:00
        DateMidnight dmToday = new DateMidnight(d);
        DateMidnight dmYesterday=dmToday.minusDays(1);

        //
        DateTime dt = new DateTime(dmToday.toDate());
        dt=dt.minusSeconds(1);
        return new Date[]{dmYesterday.toDate(), dt.toDate()};
        //                               this gets us yesterday from 00:00 to 23:59:59

        // now get a 00:00:00 version of that time
        /*
        GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.roll(Calendar.DAY_OF_MONTH, -1);

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

        Calendar startCal = (Calendar) cal.clone();

        startCal = DateUtil.setDayStartForCalendar(startCal);
        return new Date[]{startCal.getTime(), cal.getTime()};*/
    }

	public static String[] getYesterdayAsStrings(Date d)
	{
        Date[] yesterdayDates = getYesterday(d);

        return new String[]{tsformatter.format(yesterdayDates[0]), tsformatter.format(yesterdayDates[1])};
	}
	
	public static String getTodaysDate() {
		return tsformatter.format(DateUtil.getCurrentDate());
	}

    /*
    public static String[] getTimeStartToGivenDateAsString(Date d)
    {
		GregorianCalendar cal = new GregorianCalendar();
		String sEndTime = tsformatter.format(d);

		cal.setTime(d);
		// HOUR, MINUTE, and SECOND
		String sStartTime = tsformatter.format((DateUtil.setDayStartForCalendar(cal))
				.getTime());

		// 112-3:5 00:00:00 2012-04-06 15:46:46.958
		String[] slist = { sStartTime, sEndTime };
		return slist;
	}*/

	public static String[] strDayToStrTimestamp(String sDate)
	{
		String[] sRet = { sDate + " 00:00:00", sDate + " 23:59:59" };
		return sRet;
	}

    public static java.util.Date getYesterday() {
        return dateAdd(new Date(), -1);
    }

	public static java.util.Date getCurrentDate()
	{
		return new java.util.Date();
	}
	
	public static java.sql.Date convertStringToSQLDate(String inDate)
			throws DAOException
	{
		try
		{
			return new java.sql.Date(dateformatter.parse(inDate).getTime());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new DAOException("Invalid date string: " + inDate, e);
		}
	}

    // NEW -- for round 2
	public static java.util.Date convertStringToDate(String inDate)
			throws DAOException
	{
        logger.debug("convert to string called" + inDate);
		try
		{
			java.util.Date dateObj = null;
            int iLength = inDate.trim().length();

			// which datetime format did it use?  with or without timestamp
			if(iLength == (tswformatter.toPattern().length()-2))
			{
                // convert TZ to UTC...
				dateObj=tswformatter.parse(inDate);
                //UTCCal.setTime(dateObj);
                //dateObj = UTCCal.getTime();
			}
            else if(iLength == tsformatter.toPattern().length())
			{
				dateObj=tsformatter.parse(inDate);
			}
			else if(iLength == dateformatter.toPattern().length())
			{
				dateObj=dateformatter.parse(inDate);
			}
			else 
			{
                // convert TZ to UTC...
                logger.debug("fallthrough date -- " + inDate);
				dateObj=tstzformatter.parse(inDate);
                //UTCCal.setTime(dateObj);
                //dateObj = UTCCal.getTime();
			}
			
			return dateObj;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			throw new DAOException("Invalid date string: " + inDate + "expected: " + TIMESTAMP_FORMAT_TZ, e);
		}
	}

    /*
	public static Calendar convertStringToCalendar(String inDate)
			throws DAOException
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.convertStringToDate(inDate));
		return cal;
	}
    */

	public static Date minuteAdd(Date startDate, int delta) {
        DateTime d = new DateTime(startDate);
        return d.plusMinutes(delta).toDate();
        /*
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startDate);
		cal.roll(Calendar.MINUTE, delta);

		cal.roll(Calendar.DAY_OF_MONTH, delta);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
		*/
	}

	public static Date dateAdd(Date startDate, int delta) {

        // // new way
        DateTime d = new DateTime(startDate);
        return d.plusDays(delta).toDate();
	}

    /*
    public static Date[] getFullDayRangeForDate(Date d) {
        GregorianCalendar cal = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();

		cal.setTime(d);
        cal2.setTime(d);


     	cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		cal2.set(Calendar.HOUR_OF_DAY, 23);
		cal2.set(Calendar.MINUTE, 59);
		cal2.set(Calendar.SECOND, 59);

		return new Date[]{cal.getTime(), cal2.getTime()};
    }*/

    /*
	public static Timestamp convertDateToTimestamp(java.util.Date d) {
		return new Timestamp(d.getTime());
	}*/

    /*
	public static java.util.Date setDayStartToGivenTimeDate(java.util.Date useDate) {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(useDate);
        // HOUR, MINUTE, and SECOND
        return DateUtil.setDayStartForCalendar(cal).getTime();
    }
    public static Timestamp[] getDayStartToGivenTimeTS(java.util.Date useDate) {

        GregorianCalendar cal = new GregorianCalendar();
        Timestamp endTimestamp = DateUtil.convertDateToTimestamp(useDate);
        cal.setTime(useDate);
        // HOUR, MINUTE, and SECOND
        Timestamp startTimestamp = new Timestamp((DateUtil.setDayStartForCalendar(cal).getTimeInMillis()));

        // 112-3:5 00:00:00 2012-04-06 15:46:46.958
        Timestamp[] dates = {startTimestamp, endTimestamp};
        return dates;
    }
    */

    public static Date setDayStartForDate(Date d) {
        DateMidnight dm = new DateMidnight(d);

        return dm.toDate();
        /*
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(d);

        return (setDayStartForCalendar(g)).getTime();*/
    }

    /*
	public static Calendar setDayStartForCalendar(Calendar c)
	{
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

		return c;
	}*/

    public static Date setDayEndForDate(Date d) {

        DateMidnight dm = new DateMidnight(d);
        dm=dm.plusDays(1); // push it to tomorrow
        DateTime dt = new DateTime(dm.toDate());
        return dt.minusSeconds(1).toDate();
        /*
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(d);

        return (setDayEndForCalendar(g)).getTime();
        */
    }

    /*
	public static Calendar setDayEndForCalendar(Calendar c)
	{
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		return c;
	}*/

}