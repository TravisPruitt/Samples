package com.disney.xband.common.lib;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

public class ScheduleClockHours {

	LocalTime start;
	LocalTime stop;
	DateTime overrideStartDateTime;
	int overrideMinutes = 0;

	DateTime nowDateTime;
	DateTime nextStartDateTime;
	DateTime nextStopDateTime;
	DateTime lastStartDateTime;
	DateTime lastStopDateTime;

	boolean started = false;
	long millisUntilStart = 0;
	
	private void update()
	{
		started = false;
		millisUntilStart = 0;
		
		lastStopDateTime = OnOrBefore(nowDateTime, stop);
		lastStartDateTime = OnOrBefore(nowDateTime, start);
					
		nextStartDateTime = OnOrAfter(nowDateTime, start);
		nextStopDateTime = OnOrAfter(nowDateTime, stop);

		if (lastStopDateTime.isBefore(lastStartDateTime) && nowDateTime.isBefore(nextStopDateTime))   // stop start NOW stop start    vs    start stop NOW start stop
			started = true;
		else
		{
			if (overrideStartDateTime != null)
			{
				if (lastStopDateTime.isBefore(lastStartDateTime) && overrideStartDateTime.isBefore(nextStopDateTime)) // Time added to started hours
				{
					if (nowDateTime.isBefore(nextStopDateTime.plusMinutes(overrideMinutes)))
					{
						started = true;
					}
				}
				else // Time window opened after hours
				{
					if (nowDateTime.isAfter(overrideStartDateTime) && nowDateTime.isBefore(overrideStartDateTime.plusMinutes(overrideMinutes)))
					{
						started = true;
					}
				}
			}
		}

		if (started == false)
		{
			DateTime next = nextStartDateTime;
			if (overrideStartDateTime != null && overrideStartDateTime.isAfter(nowDateTime) && overrideStartDateTime.isBefore(next))
				next = overrideStartDateTime;
			
			millisUntilStart = next.getMillis() - nowDateTime.getMillis();
		}
	}
	
	public ScheduleClockHours(LocalTime start, LocalTime stop)
	{
		this.start = start;
		this.stop = stop;
		this.nowDateTime = new DateTime(DateTimeZone.UTC);
		
		update();
	}
	
	public ScheduleClockHours(LocalTime start, LocalTime stop, DateTime overrideDateTime, int overrideMinutes)
	{
		this.start = start;
		this.stop = stop;
		this.nowDateTime = new DateTime(DateTimeZone.UTC);
		this.overrideStartDateTime = overrideDateTime;
		this.overrideMinutes = overrideMinutes;
		
		update();
	}
	
	public void updateCurrentTime(DateTime dt)
	{
		this.nowDateTime = new DateTime(DateTimeZone.UTC);
		update();
	}
	
	public boolean isStarted(Long millisUntilStart)
	{
		return started;
	}
	
	public long getMillisUntilStart()
	{
		return millisUntilStart;
	}
		
	/*
     *  Given: date/time February 14, 2013 1pm and a local time 6am, return February 14, 2013 6am.
     *  Given: date/time February 14, 2013 1pm and a local time 5pm, return February 13, 2013 5pm.
     * 
     */
    static DateTime OnOrBefore(DateTime dt, LocalTime lt)
    {
    	DateTime result = dt.withTime(lt.getHourOfDay(), lt.getMinuteOfHour(), lt.getSecondOfMinute(), lt.getMillisOfSecond());
    	if (result.isAfter(dt))
    	{
    		result = result.minusDays(1);
    	}
    	return result;
    }

    /*
     *  Given: date/time February 14, 2013 1pm and a local time 6am, return February 15, 2013 6am.
     *  Given: date/time February 14, 2013 1pm and a local time 5pm, return February 14, 2013 5pm.
     * 
     */
    static DateTime OnOrAfter(DateTime dt, LocalTime lt)
    {
    	DateTime result = dt.withTime(lt.getHourOfDay(), lt.getMinuteOfHour(), lt.getSecondOfMinute(), lt.getMillisOfSecond());
    	if (result.isBefore(dt))
    	{
    		result = result.plusDays(1);
    	}
    	return result;
    }
    
    
}
