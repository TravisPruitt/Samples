package com.disney.xband.xbrc.Controller.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.Controller.Controller;

public class StoredStatus
{
	public static Logger logger = Logger.getLogger(StoredStatus.class);
	
	private long iLastMessageIdToJMS;
	private long iLastMessageIdToPostStream ;
	private Date dtLastStateStore;
	
	public StoredStatus()
	{
		iLastMessageIdToJMS = iLastMessageIdToPostStream = 0;
		dtLastStateStore = new Date(0);
	}

	public long getLastMessageIdToJMS()
	{
		return iLastMessageIdToJMS;
	}

	public void setLastMessageIdToJMS(long iLastMessageIdToJMS)
	{
		PreparedStatement stmt = null;

		this.iLastMessageIdToJMS = iLastMessageIdToJMS;
		Connection conn = null;
		try
		{
			// save to database
			conn = Controller.getInstance().getPooledConnection();
			if (conn != null)
			{
				String sSQL = "UPDATE Status SET Value=? WHERE Property='LastMessageIdToJMS'";
				stmt = conn.prepareStatement(sSQL);
				stmt.setString(1, Long.toString(this.iLastMessageIdToJMS));
				stmt.executeUpdate();					
			}
		}
		catch (Exception e)
		{
			logger.error("!! Error updating Status table: " + e);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{

			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	public long getLastMessageIdToPostStream()
	{
		return iLastMessageIdToPostStream;
	}

	public void setLastMessageIdToPostStream(long iLastMessageIdToPostStream)
	{
		Connection conn = null;
		PreparedStatement stmt = null;

		this.iLastMessageIdToPostStream = iLastMessageIdToPostStream;
		try
		{
			// save to database
			conn = Controller.getInstance().getPooledConnection();
			if (conn != null)
			{
				String sSQL = "UPDATE Status SET Value=? WHERE Property='LastMessageIdToPostStream'";
				stmt = conn.prepareStatement(sSQL);
				stmt.setString(1, Long.toString(this.iLastMessageIdToPostStream));
				stmt.executeUpdate();
			}
		}
		catch (Exception e)
		{
			logger.error("!! Error updating Status table: " + e);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}
	public Date getLastStateStore()
	{
		return dtLastStateStore;
	}
	public void setLastStateStore(Date dt)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		dtLastStateStore = dt;
		
		try
		{
			// save to database
			conn = Controller.getInstance().getPooledConnection();
			if (conn != null)
			{
				String sSQL = "UPDATE Status SET Value=? WHERE Property='LastStateStore'";
				stmt = conn.prepareStatement(sSQL);
				stmt.setString(1, formatDate(dtLastStateStore));
				stmt.executeUpdate();
			}
		}
		catch (Exception e)
		{
			logger.error("!! Error updating Status table: " + e);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
			if (conn != null)
				Controller.getInstance().releasePooledConnection(conn);
		}
	}

	public void processStatusTable(Connection conn)
	{
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			String sSQL = "SELECT * from Status";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			while (rs.next())
			{
				String sProperty = rs.getString("Property");
				String sValue = rs.getString("Value");

				// don't call setter functions since they'll write back out to
				// the DB!
				if (sProperty.compareTo("LastMessageIdToPostStream") == 0)
				{
					// status.setLastMessageIdToPostStream(ParseLong(sValue));
					iLastMessageIdToPostStream = parseLong(sValue);
				}
				else if (sProperty.compareTo("LastMessageIdToJMS") == 0)
				{
					// status.setLastMessageIdToJMS(ParseLong(sValue));
					iLastMessageIdToJMS = parseLong(sValue);
				}
				else if (sProperty.compareTo("LastStateStore")==0)
				{
					// status.setLastStateStore(ParseDate(sValue));
					dtLastStateStore = parseDate(sValue);
				}
				else
				{
					logger.error("!! Error: unexpected property name in Status table: "
							+ sProperty);
				}
			}
		}
		catch (SQLException e)
		{
			logger.error("!! Error reading Status table from database", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
		}

	}

	private long parseLong(String sValue)
	{
		return Long.parseLong(sValue);
	}

	private static String formatDate(Date dt)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}
	
	private static Date parseDate(String s)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try
		{
			return sdf.parse(s);
		}
		catch (ParseException e)
		{
			return new Date(0);
		}
	}
}
