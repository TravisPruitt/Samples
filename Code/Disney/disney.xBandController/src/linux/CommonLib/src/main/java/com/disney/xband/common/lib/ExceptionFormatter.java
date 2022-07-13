package com.disney.xband.common.lib;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionFormatter
{
	public static String format(String sName, Throwable ex)
	{
		if (ex==null)
			return "Null exception";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(sName);
		sb.append(": ");
		
		if (ex.getLocalizedMessage()!=null)
			sb.append(ex.getLocalizedMessage());
		else
			sb.append(ex.toString());
		
		sb.append("\nStack trace:\n");
		
		// output the stack trace to a string
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		ex.printStackTrace(ps);
		
		sb.append(bos.toString());
		
		return sb.toString();
	}

	public static String formatMessage(String sName, Throwable ex)
	{
		if (ex==null)
			return "Null exception";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(sName);
		sb.append(": ");
		
		if (ex.getLocalizedMessage()!=null)
			sb.append(ex.getLocalizedMessage());
		else
			sb.append(ex.toString());
				
		return sb.toString();
	}
}