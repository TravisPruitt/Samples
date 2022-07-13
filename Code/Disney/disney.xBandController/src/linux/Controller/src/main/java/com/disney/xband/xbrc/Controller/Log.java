package com.disney.xband.xbrc.Controller;

public class Log
{	
	// single instance pattern
	public static EventdumpLog INSTANCE;

	public interface EventdumpLog
	{
		public void logEKG(String s);
		public long getEKGPosition();
		public String readEKGFromPosition(long lPosition, int cMaxLines);
		public void close();
	}
	
	public static void initialize(EventdumpLog instance)
	{
		if (Log.INSTANCE != null)
			Log.INSTANCE.close();
		
		Log.INSTANCE = instance;
	}
}
