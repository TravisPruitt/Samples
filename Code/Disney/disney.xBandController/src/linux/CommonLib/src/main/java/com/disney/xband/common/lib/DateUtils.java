package com.disney.xband.common.lib;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {
	
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DEFAULT_TIME_ZONE = "UTC";
	
	public static String format(long time, String format)
	{
		
		return format(time, format, null);
	}

	public static String format(long time)
	{
		
		return format(time, null, null);
	}
	
	public static String format(long time, String format, String timeZone)
	{
		
		if (format == null || format.trim().isEmpty())
			format = DEFAULT_FORMAT;
		
		if (timeZone == null || timeZone.trim().isEmpty())
			timeZone = DEFAULT_TIME_ZONE;
		
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.setTimeZone(tz);
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);		
		sdf.setTimeZone(tz);
		
		return sdf.format(calendar.getTime());
	}

	public static Date parseDate(String sDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try
		{
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.parse(sDate);
		}
		catch (ParseException e)
		{
			// try again without milliseconds
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			try
			{
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				return sdf.parse(sDate);
			}
			catch (ParseException e2)
			{
				return new Date(0);
			}
		}
		
	}

	/**
	 * Uses GMT if time zone isn't explicitly provided.
	 * 
	 * @param date
	 * @param dateFormat
	 * @param timeZone
	 * @return
	 * @throws ParseException
	 * @throws IllegalArgumentException
	 */
	public static Date parseDate(String date, String dateFormat, TimeZone timeZone) 
			throws ParseException, IllegalArgumentException
	{
		if (date == null)
			throw new IllegalArgumentException("Date is required.");
		if (dateFormat == null)
			throw new IllegalArgumentException("Date format is required.");
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(timeZone != null ? timeZone : TimeZone.getTimeZone("GMT"));
		
		return sdf.parse(date);
	}
	
	public static String convert(String date, String fromFormat, String toFormat, Locale locale) throws ParseException
	{
		if (date == null || date.trim().length() == 0)
			return null;
		if (fromFormat == null || fromFormat.trim().length() == 0)
			return date;
		if (toFormat == null || toFormat.trim().length() == 0)
			return date;
		
		if (locale == null)
			locale = Locale.ENGLISH;
		
		DateFormat sourceFormat = new SimpleDateFormat(fromFormat, locale);
		DateFormat targetFormat = new SimpleDateFormat(toFormat, locale);
		
		Date result = sourceFormat.parse(date);
		
		return targetFormat.format(result);
	}
	
	public static String toString(Date date, String format, Locale locale, TimeZone timezone)
	{
		if (date == null)
			return null;
		
		if (format == null || format.trim().length() == 0)
			return null;
		
		if (locale == null)
			locale = Locale.ENGLISH;
		
		DateFormat targetFormat = new SimpleDateFormat(format, locale);
		
		if (timezone != null)
			targetFormat.setTimeZone(timezone);
		
		return targetFormat.format(date);
	}
	
	public static String formatAgo(Long date) {
		if (date == 0L)
			return "never";
		
		long time = (new Date().getTime() - date) / 1000;
		long months = time / 60 / 60 / 24 / 30;
		if (months != 0l){
			return months + " month(s) ago";
		}
		
		long weeks = time / 60 / 60 / 24 / 7;
		if (weeks != 0l){
			return weeks + " week(s) ago";
		}
		
		long days = time / 60 / 60 / 24;
		if (days != 0l){
			return days + " day(s) ago";
		}
		
		long hours = time / 60 / 60;
		if (hours != 0l){
			return hours + "h ago";
		}
		
		long minutes = time / 60;
		if (minutes != 0l){
			return minutes + "m ago";
		}
		
		return time + "s ago";				
	}
	
	static public String formatMillis(long val) 
	{
	    StringBuilder buf=new StringBuilder(20);
	    	    
	    if (val >= 86400000)
	    {
	    	buf.append(val / 86400000);
	    	buf.append(" days ");
	    	val %= 86400000;
	    }
	    
	    if (val >= 3600000)
	    {
	    	buf.append(val / 3600000);
	    	buf.append(" hr ");
	    	val %= 3600000;
	    }
	    
	    if (val >= 60000)
	    {
	    	buf.append(val / 60000);
	    	buf.append(" min ");
	    	val %= 60000;
	    }
	    
	    if (val >= 1000)
	    {
	    	buf.append(val / 1000);
	    	buf.append(" sec ");
	    	val %= 1000;
	    }
	    
	    if (val > 0)
	    {
	    	buf.append(val);
	    	buf.append(" ms ");
	    }
	   
	    return buf.toString();
	}
}
