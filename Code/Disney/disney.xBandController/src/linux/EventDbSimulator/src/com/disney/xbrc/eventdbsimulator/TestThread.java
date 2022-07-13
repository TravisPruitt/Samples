package com.disney.xbrc.eventdbsimulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestThread implements Runnable
{
	
	private final CountDownLatch doneSignal;
	private int seconds;
	private String connectionString;
	private String user;
	private String password;
	private int eventid;
	private int events;
	
	public int getEvents()
	{
		return this.events;
	}
	
	public TestThread(CountDownLatch doneSignal,
			int startingEventID,
			int seconds, String connectionString, String user, String password)
	{
		this.doneSignal= doneSignal;
		this.eventid = startingEventID;
		this.seconds = seconds;
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
	}

	public void run()
	{
		doWork();
		doneSignal.countDown();
	}
	
	void doWork()
	{
		Random generator = new Random();

		try 
		{
			Connection conn = DriverManager.getConnection(
					this.connectionString, this.user, this.password);
	
			long startTime = System.currentTimeMillis();
			long endTime = startTime + (this.seconds * 1000);

			while (endTime > System.currentTimeMillis()) 
			{

				StringBuilder sql = new StringBuilder();

				sql.append("INSERT INTO SYSTEM.EVENTS VALUES (");
				sql.append(eventid);
				sql.append(",");
				sql.append("'ReaderID',");
				sql.append("'BandID',");
				sql.append("CURRENT_TIMESTAMP,");
				sql.append((generator.nextInt() % 50) - 90); // Strength
				sql.append(",");
				sql.append(generator.nextInt() % 256); // Packet Sequence
				sql.append(",");
				sql.append(generator.nextInt() % 4); // Frequency
				sql.append(",");
				sql.append(generator.nextInt() % 4); // Channel
				sql.append(")");

				Statement statement = conn.createStatement();
				try
				{
					statement.executeUpdate(sql.toString());
					this.events++;
				} 
				catch(Exception ex)
				{
					System.out.println("ERROR: failed to connect!");
					System.out.println("ERROR: " + ex.getMessage());
					ex.printStackTrace();
					return;
				}
				finally 
				{
					statement.close();
				}

				this.eventid++;

			}
			conn.close();
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
