package com.disney.xband.common.lib.junit.bvt;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.disney.xband.common.lib.DateUtils;

@RunWith(Parameterized.class)
public class TestDateUtils
{
	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private String inputDate;
	private String timezone;
	private long milliseconds;
	
	public TestDateUtils(String inputDate, String timezone, long milliseconds)
	{
		this.inputDate = inputDate;
		this.timezone = timezone;
		this.milliseconds = milliseconds;
	}

	@Parameters
	public static Collection<Object[]> data() 
	{
		return Arrays.asList(new Object[][] { 
		/*0*/	{ "1997-07-16T19:20:30.450", "+0100", 869077230450L },
		/*1*/	{ "1997-07-16T11:20:30.450", "-0700", 869077230450L },
		/*2*/	{ "1997-07-16T12:20:30.450", "+0000", 869055630450L },
		/*3*/	{ "1997-07-16T19:20:30.450", "+0000", 869080830450L },
		/*4*/	{ "1997-07-16T19:20:30.450", "-0800", 869109630450L },
		/*5*/	{ "1970-01-01T00:00:00.000", "+0100", -3600000L },
		/*6*/	{ "1970-01-01T00:00:00.000", "+0000", 0L },
		/*7*/	{ "1970-07-01T00:00:00.000", "+0000", 15638400000L }
		});
	}

	@Test
	public void test() throws ParseException
	{
		// expected, actual
		Date result = DateUtils.parseDate(
				inputDate + timezone, 
				DATE_FORMAT, 
				TimeZone.getTimeZone("GMT"));
		
		assertEquals(milliseconds, result.getTime());
		
		String date = DateUtils.toString(
				result, 
				DATE_FORMAT, 
				Locale.ENGLISH, 
				TimeZone.getTimeZone("GMT" + timezone));
		
		assertTrue(date.equals(inputDate + timezone));
	}

}
