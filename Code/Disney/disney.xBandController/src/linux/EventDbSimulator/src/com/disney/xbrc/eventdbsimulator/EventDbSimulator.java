package com.disney.xbrc.eventdbsimulator;

import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class EventDbSimulator
{
	private String connectionString;
	private String user;
	private String password;
	private static Properties prop = new Properties();
	
	public EventDbSimulator(String connectionString, String user, String password)
	{
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			Class.forName ("oracle.jdbc.OracleDriver");
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println("ERROR: failed to connect!");
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		
		 String sPropFile = "properties.xml";

		try
		{
			FileInputStream fis = new FileInputStream(sPropFile);
			prop.loadFromXML(fis);
			fis.close();
		}
		catch (Exception ex)
		{
			System.out.println("!! Could not read properties.xml file: "
					+ ex.getLocalizedMessage());
			System.exit(1);
		}
		
		// must have some properties
		if (prop.getProperty("connectionString") == null
				|| prop.getProperty("dbuser") == null
				|| prop.getProperty("dbpassword") == null
				|| prop.getProperty("threads") == null
				|| prop.getProperty("runtimeSeconds") == null)
		{
			System.out.println("!! Error: properties.xml must contain, at least, settings for connectionString, dbuser, dbpassword, threads, and runtimeSeconds");
			System.exit(1);
		}
		
		String connectionString = prop.getProperty("connectionString");
		String user = prop.getProperty("dbuser");
		String password = prop.getProperty("dbpassword");
		
		CommandLineOptions commandLineOptions = CommandLineOptions.parse(args);
		if (commandLineOptions==null)
			return;
		
		if (commandLineOptions.hasDisplayUsage())
		{
			CommandLineOptions.usage();
			return;
		}

		System.out.printf("Writing events using %d threads for %d seconds.", 
				commandLineOptions.getThreads(), 
				commandLineOptions.getDuration());
		System.out.println("");
		System.out.printf("Destination: %s.", connectionString);
		System.out.println("");


		EventDbSimulator eventTest = new EventDbSimulator(connectionString, user,password);
		
		eventTest.TruncateTable();
		
		eventTest.Execute(commandLineOptions.getThreads(), 
				commandLineOptions.getDuration());
		
	}

	private void Execute(int threads, int duration)
	{
		try
		{
			ThreadPerTaskExecutor e = new ThreadPerTaskExecutor();
			CountDownLatch doneSignal = new CountDownLatch(threads);
			TestThread[] threadpool = new TestThread[threads];
			
			for(int threadIndex=0; threadIndex< threads; threadIndex++)
			{
				threadpool[threadIndex] = new TestThread(doneSignal, (threadIndex*1000000)+1, 
		        		 duration, connectionString, user, password );
		        e.execute(threadpool[threadIndex]);
			}
		       
			doneSignal.await();           // wait for all to finish
			
			int events = 0;
			for(int threadIndex=0; threadIndex< threads; threadIndex++)
			{
				events += threadpool[threadIndex].getEvents();
			}

			System.out.printf("Wrote %d events in %d seconds", events, duration);
			System.out.println("");
			System.out.printf("Events per second %d", events / duration);
			System.out.println("");
		}
		catch(InterruptedException ex)
		{
			System.out.println("ERROR: failed to connect!");
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}		
	
	}
	
	private void TruncateTable() 
	{
		try
		{
			Connection conn = DriverManager.getConnection(
					this.connectionString, this.user, this.password);
			
			try
			{
				Statement truncateStatement = conn.createStatement();
	
				try 
				{
					truncateStatement.execute("TRUNCATE TABLE SYSTEM.EVENTS");
				} 
				finally 
				{
					truncateStatement.close();
				}
			}
			finally
			{
				conn.close();
			}
		}
		catch(SQLException ex)
		{
			System.out.println("ERROR: failed to connect!");
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
	}
}
