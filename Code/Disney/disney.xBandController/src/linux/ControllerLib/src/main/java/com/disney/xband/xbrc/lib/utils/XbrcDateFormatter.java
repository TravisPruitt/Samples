package com.disney.xband.xbrc.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class XbrcDateFormatter
{
	public static String formatTime(long lTime)
	{
		Date dt = new Date(lTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}
}
