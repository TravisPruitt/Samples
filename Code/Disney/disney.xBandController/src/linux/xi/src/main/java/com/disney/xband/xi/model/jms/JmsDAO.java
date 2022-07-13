package com.disney.xband.xi.model.jms;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import com.disney.xband.xi.JmsResource;
import com.disney.xband.xi.model.DAO;

public class JmsDAO extends DAO implements Comparator<JmsMessage>
{
	public enum HistogramGrouping
	{
		WEEKLY,
		DAILY,
		HOURLY
	}
	
	public JmsDAO()
	{
		super();
	}
	
	public JmsMessages getJmsMessagesForAttractionByDate(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection conn = null;
		try
		{
			List<JmsMessage> lim = null;
			conn = this.getConnection();
			lim = getJmsEntryMessages(conn, sVenue, dtFrom, dtTo);
			lim.addAll(getJmsMergeMessages(conn, sVenue, dtFrom, dtTo));
			lim.addAll(getJmsLoadMessages(conn, sVenue, dtFrom, dtTo));
			lim.addAll(getJmsExitMessages(conn, sVenue, dtFrom, dtTo));
			lim.addAll(getJmsAbandonMessages(conn, sVenue, dtFrom, dtTo));
			
			// get the date for the last message
			String sTimeAttr = JmsResource.formatDate(dtTo, false);
			if (lim.size() > 0)
			{
				// sort the data
				Collections.sort(lim, this);
				
				sTimeAttr = lim.get(lim.size()-1).getTimestamp();
			}
			
			// return it
			JmsMessages msgs = new JmsMessages();
			msgs.setMessages(lim);
			msgs.setName(sVenue);
			msgs.setTime(sTimeAttr);
			return msgs;
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
		
	}
	
	private List<JmsMessage> getJmsEntryMessages(Connection conn, String sVenue, Date dtFrom, Date dtTo) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT * FROM rdr.Event WHERE " +
					"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='ENTRY') AND " +
		            "timestamp>=? AND timestamp<? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsEntryMessage m = new JmsEntryMessage();
				m.setType("ENTRY");

				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsEntryMessages(Connection conn, String sVenue, long iMessage) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT * FROM rdr.Event WHERE " +
					"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='ENTRY') AND " +
					"EventID>=? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsEntryMessage m = new JmsEntryMessage();
				m.setType("ENTRY");

				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsMergeMessages(Connection conn, String sVenue, Date dtFrom, Date dtTo) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT * FROM rdr.Event WHERE " +
					"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='MERGE') AND " +
					"timestamp>=? AND timestamp<? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsMergeMessage m = new JmsMergeMessage();
				m.setType("MERGE");

				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsMergeMessages(Connection conn, String sVenue, long iMessage) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT * FROM rdr.Event WHERE " +
					"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='MERGE') AND " +
					"EventID>=? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsMergeMessage m = new JmsMergeMessage();
				m.setType("MERGE");

				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsLoadMessages(Connection conn, String sVenue, Date dtFrom, Date dtTo) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, WaitTime, MergeTime, CarID, xPass, ReaderLocation " +
							"FROM rdr.Event, rdr.LoadEvent WHERE " + 
							"rdr.Event.EventID=rdr.LoadEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='LOAD') AND " +
					        "timestamp>=? AND timestamp<? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsLoadMessage m = new JmsLoadMessage();
				m.setType("LOAD");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setMergeTime(rs.getInt("WaitTime"));
				m.setWaitTime(rs.getInt("MergeTime"));
				m.setCarId(rs.getString("CarID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsLoadMessages(Connection conn, String sVenue, long iMessage) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, WaitTime, MergeTime, CarID, xPass, ReaderLocation " +
							"FROM rdr.Event, rdr.LoadEvent WHERE " + 
					        "rdr.Event.EventID=rdr.LoadEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='LOAD') AND " +
							"Event.EventID>? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsLoadMessage m = new JmsLoadMessage();
				m.setType("LOAD");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setMergeTime(rs.getInt("WaitTime"));
				m.setWaitTime(rs.getInt("WaitTime"));
				m.setCarId(rs.getString("CarID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsExitMessages(Connection conn, String sVenue, Date dtFrom, Date dtTo) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, xPass, ReaderLocation, WaitTime, MergeTime, TotalTime, CarID " +
							"FROM rdr.Event, rdr.ExitEvent WHERE " + 
					        "rdr.Event.EventID=rdr.ExitEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='EXIT') AND " +
							"timestamp>=? AND timestamp<? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsExitMessage m = new JmsExitMessage();
				m.setType("EXIT");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				m.setMergeTime(rs.getInt("MergeTime"));
				m.setWaitTime(rs.getInt("WaitTime"));
				m.setTotalTime(rs.getInt("WaitTime"));
				m.setCarId(rs.getString("CarID"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsExitMessages(Connection conn, String sVenue, long iMessage) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, xPass, ReaderLocation, WaitTime, MergeTime, TotalTime, CarID " +
							"FROM rdr.Event, rdr.ExitEvent WHERE " + 
					        "rdr.Event.EventID=rdr.ExitEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='EXIT') AND " +
					        "Event.EventID>? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsExitMessage m = new JmsExitMessage();
				m.setType("EXIT");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				m.setMergeTime(rs.getInt("MergeTime"));
				m.setWaitTime(rs.getInt("WaitTime"));
				m.setTotalTime(rs.getInt("WaitTime"));
				m.setCarId(rs.getString("CarID"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsAbandonMessages(Connection conn, String sVenue, Date dtFrom, Date dtTo) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, xPass, ReaderLocation, LastTransmit " +
							"FROM rdr.Event, rdr.AbandonEvent WHERE " + 
					        "rdr.Event.EventID=rdr.AbandonEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='ABANDON') AND " +
							"timestamp>=? AND timestamp<? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsAbandonMessage m = new JmsAbandonMessage();
				m.setType("ABANDON");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				m.setLastTransmit(rs.getTimestamp("LastTransmit"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	private List<JmsMessage> getJmsAbandonMessages(Connection conn, String sVenue, long iMessage) throws SQLException
	{
		List<JmsMessage> lim = new ArrayList<JmsMessage>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String sQuery = "SELECT TimeStamp, Event.EventID as EventID, GuestID, xPass, ReaderLocation, LastTransmit " +
							"FROM rdr.Event, rdr.AbandonEvent WHERE " + 
					        "rdr.Event.EventID=rdr.AbandonEvent.EventID AND " +
							"EventTypeId in (SELECT TOP 1 EventTypeId FROM rdr.EventType WHERE EventTypeName='ABANDON') AND " +
							"Event.EventID>? AND " +
					        "FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";

			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JmsAbandonMessage m = new JmsAbandonMessage();
				m.setType("ABANDON");
				
				m.setEventId(rs.getLong("EventID"));
				m.setTimestamp(rs.getTimestamp("Timestamp"));
				m.setGuestId(rs.getString("GuestID"));
				m.setxPass(rs.getBoolean("xPass"));
				m.setReaderLocation(rs.getString("ReaderLocation"));
				m.setLastTransmit(rs.getTimestamp("LastTransmit"));
				
				lim.add(m);
			}

			return lim;
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
					
		}
	}

	
	
	public JmsMessages getJmsMessagesForAttractionById(String sVenue, long iMessage) throws Exception
	{
		Connection conn = null;
		try
		{
			List<JmsMessage> lim = null;
			conn = this.getConnection();
			lim = getJmsEntryMessages(conn, sVenue, iMessage);
			lim.addAll(getJmsMergeMessages(conn, sVenue, iMessage));
			lim.addAll(getJmsLoadMessages(conn, sVenue, iMessage));
			lim.addAll(getJmsExitMessages(conn, sVenue, iMessage));
			lim.addAll(getJmsAbandonMessages(conn, sVenue, iMessage));

			// get the date for the last message
			String sTimeAttr = JmsResource.formatDate(new Date(), false);
			if (lim.size() > 0)
			{
				// sort the data
				Collections.sort(lim, this);
				
				sTimeAttr = lim.get(lim.size()-1).getTimestamp();
			}
			
			// return it
			JmsMessages msgs = new JmsMessages();
			msgs.setMessages(lim);
			msgs.setName(sVenue);
			msgs.setTime(sTimeAttr);
			return msgs;
			
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
		
	}

	public JmsMessageRange getJmsMessageRangeForAttraction(String sVenue) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JmsMessageRange mr = null;
		try
		{
			conn = this.getConnection();
			String sQuery = "SELECT MIN(timestamp) AS mintime, MAX(timestamp) AS maxtime FROM rdr.Event WHERE FacilityID IN " +
												   "(SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, sVenue);
			rs = pstmt.executeQuery();
			rs.next();
			mr = new JmsMessageRange();
			Date dtStart = JmsResource.parseSqlDate(rs.getString("mintime"));
			Date dtEnd = JmsResource.parseSqlDate(rs.getString("maxtime"));
			mr.setStartDate(JmsResource.formatDate(dtStart, false));
			mr.setEndDate(JmsResource.formatDate(dtEnd, false));
			return mr;
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (pstmt != null)
				try
				{
					pstmt.close();
				}
				catch (SQLException sqle)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
		
	}

	public JmsGuestStates getJmsGuestStatesForAttractionAtTime(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try
		{
			List<JmsGuestState> ligs = new ArrayList<JmsGuestState>();
			
			connection = this.getConnection();
			String sQuery = "{call xgs.usp_GuestLocation_Retrieve(?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setString(1, sVenue);
			statement.setString(2, JmsResource.formatDate(dtFrom));
			statement.setString(3, JmsResource.formatDate(dtTo));

			rs = statement.executeQuery();
			while (rs.next())
			{
				// map type into state
				String sState = null;
				String sMessageType = rs.getString("messageType");
				if (sMessageType.equals("EXIT"))
				{
					// ignore these - shouldn't happen
					continue;
				}
				else if (sMessageType.equals("ENTRY"))
				{
					sState = "HASENTERED"; 
				} 
				else if (sMessageType.equals("MERGE"))
				{
					sState = "HASMERGED";
				}
				else if (sMessageType.equals("LOAD"))
				{
					sState = "LOADING";
				}
				else 
				{
					// ignore everything else
					continue;
				}
				
				// create a location object
				String sReaderLocation = rs.getString("ReaderLocation");
				JmsLocationInfo loc = new JmsLocationInfo();
				loc.setName(sReaderLocation);
				String sTimestamp = rs.getString("Timestamp");
				Date dt = JmsResource.parseSqlDate(sTimestamp);
				loc.setArrived(JmsResource.formatDate(dt, false));
				
				JmsGuestState gs = new JmsGuestState();
				gs.setGuestId(rs.getString("GuestID"));
				gs.setState(sState);
				gs.setxPass(rs.getBoolean("xPass"));
				gs.setLocation(loc);
				
				// add it to the list
				ligs.add(gs);
			}
			
			// create the return object
			JmsGuestStates gss = new JmsGuestStates();
			gss.setGuestStates(ligs);
			gss.setName(sVenue);
			gss.setTime(JmsResource.formatDate(dtTo, false));
			return gss;
			
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException sqle)
				{
				}
			if (connection != null)
				try
				{
					connection.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}

	public JmsMessageCount getJmsMessageCountForAttractionByDate(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			String sQuery = "SELECT COUNT(EventID) AS Count FROM rdr.Event WHERE timestamp>=? AND timestamp<? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			pstmt.setString(3, sVenue);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				JmsMessageCount jmc = new JmsMessageCount();
				jmc.setMessageCount(rs.getLong("Count"));
				return jmc;
			}
			else
				throw new WebApplicationException(500);
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}

	public JmsMessageCount getJmsMessageCountForAttractionById(String sVenue, long iMessage) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			String sQuery = "SELECT COUNT(EventID) AS Count FROM rdr.Event WHERE EventID>? AND " +
					"FacilityID in (SELECT TOP 1 FacilityID FROM rdr.Facility WHERE FacilityName=?)";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setLong(1, iMessage);
			pstmt.setString(2, sVenue);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				JmsMessageCount jmc = new JmsMessageCount();
				jmc.setMessageCount(rs.getLong("Count"));
				return jmc;
			}
			else
				throw new WebApplicationException(500);
		}
		catch(SQLException ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}
	
	public JmsMessageHistogram getJmsMessageHistogram(String sVenue, Date dtFrom, Date dtTo, HistogramGrouping grouping) throws Exception
	{
		if (grouping == HistogramGrouping.WEEKLY)
			return getJmsMessageHistogramWeekly(sVenue, dtFrom, dtTo);
		else if (grouping == HistogramGrouping.DAILY)
			return getJmsMessageHistogramDaily(sVenue, dtFrom, dtTo);
		else if (grouping == HistogramGrouping.HOURLY)
			return getJmsMessageHistogramHourly(sVenue, dtFrom, dtTo);
		else
			return null;
			
	}

	private JmsMessageHistogram getJmsMessageHistogramWeekly(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			
			String sQuery;
			sQuery = "SELECT     [Year] = DATEPART(yy, [Timestamp])"  + 
					 "          ,[Week] = DATEPART(ww, [Timestamp])" +  
			         "          ,[Count] = COUNT(*) " + 
			         "FROM      rdr.Event " +
			         "WHERE     [Timestamp] BETWEEN ? AND ?" +
			         "GROUP BY  DATEPART(yy, [Timestamp]), DATEPART(ww, [Timestamp])" +
			         "ORDER BY  DATEPART(yy, [Timestamp]), DATEPART(ww, [Timestamp])";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			rs = pstmt.executeQuery();
			List<JmsMessageHistogramInterval> libs = new ArrayList<JmsMessageHistogramInterval>();
			while(rs.next())
			{
				int year = rs.getInt("Year");
				int week = rs.getInt("Week");
				int count = rs.getInt("Count");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.WEEK_OF_YEAR, week);
				cal.set(Calendar.HOUR_OF_DAY, 12);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				int month = cal.get(Calendar.MONTH) + 1;
				int day = cal.get(Calendar.DAY_OF_MONTH);
				int hour = 0;
				JmsMessageHistogramInterval bar = new JmsMessageHistogramInterval();
				bar.setYear(year);
				bar.setMonth(month);
				bar.setDay(day);
				bar.setHour(hour);
				bar.setCount(count);
				libs.add(bar);
			}
			JmsMessageHistogram histo = new JmsMessageHistogram();
			histo.setGrouping(HistogramGrouping.WEEKLY.name());
			histo.setIntervals(libs);
			return histo;
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}
	
	private JmsMessageHistogram getJmsMessageHistogramDaily(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			
			String sQuery;
			sQuery = "SELECT     [Year] = DATEPART(yy, [Timestamp])"  + 
					 "          ,[Month] = DATEPART(mm, [Timestamp])" +  
					 "          ,[Day] = DATEPART(dd, [Timestamp])" +
			         "          ,[Count] = COUNT(*) " + 
			         "FROM      rdr.Event " +
			         "WHERE     [Timestamp] BETWEEN ? AND ?" +
			         "GROUP BY  DATEPART(yy, [Timestamp]), DATEPART(mm, [Timestamp]), DATEPART(dd, [Timestamp])" +
			         "ORDER BY  DATEPART(yy, [Timestamp]), DATEPART(mm, [Timestamp]), DATEPART(dd, [Timestamp])";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			rs = pstmt.executeQuery();
			List<JmsMessageHistogramInterval> libs = new ArrayList<JmsMessageHistogramInterval>();
			while(rs.next())
			{
				int year = rs.getInt("Year");
				int month = rs.getInt("Month");
				int day = rs.getInt("Day");
				int hour = 0;
				int count = rs.getInt("Count");
				JmsMessageHistogramInterval bar = new JmsMessageHistogramInterval();
				bar.setYear(year);
				bar.setMonth(month);
				bar.setDay(day);
				bar.setHour(hour);
				bar.setCount(count);
				libs.add(bar);
			}
			JmsMessageHistogram histo = new JmsMessageHistogram();
			histo.setGrouping(HistogramGrouping.DAILY.name());
			histo.setIntervals(libs);
			return histo;
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}

	
	private JmsMessageHistogram getJmsMessageHistogramHourly(String sVenue, Date dtFrom, Date dtTo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			
			String sQuery;
			sQuery = "SELECT     [Year] = DATEPART(yy, [Timestamp])"  + 
					 "          ,[Month] = DATEPART(mm, [Timestamp])" +  
					 "          ,[Day] = DATEPART(dd, [Timestamp])" +
					 "          ,[Hour] = DATEPART(hh, [Timestamp])" + 
			         "          ,[Count] = COUNT(*) " + 
			         "FROM      rdr.Event " +
			         "WHERE     [Timestamp] BETWEEN ? AND ?" +
			         "GROUP BY  DATEPART(yy, [Timestamp]), DATEPART(mm, [Timestamp]), DATEPART(dd, [Timestamp]), DATEPART(hh, [Timestamp]) " +
			         "ORDER BY  DATEPART(yy, [Timestamp]), DATEPART(mm, [Timestamp]), DATEPART(dd, [Timestamp]), DATEPART(hh, [Timestamp])";
			
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, JmsResource.formatDate(dtFrom));
			pstmt.setString(2, JmsResource.formatDate(dtTo));
			rs = pstmt.executeQuery();
			List<JmsMessageHistogramInterval> libs = new ArrayList<JmsMessageHistogramInterval>();
			while(rs.next())
			{
				int year = rs.getInt("Year");
				int month = rs.getInt("Month");
				int day = rs.getInt("Day");
				int hour = rs.getInt("Hour");
				int count = rs.getInt("Count");
				JmsMessageHistogramInterval bar = new JmsMessageHistogramInterval();
				bar.setYear(year);
				bar.setMonth(month);
				bar.setDay(day);
				bar.setHour(hour);
				bar.setCount(count);
				libs.add(bar);
			}
			JmsMessageHistogram histo = new JmsMessageHistogram();
			histo.setGrouping(HistogramGrouping.HOURLY.name());
			histo.setIntervals(libs);
			return histo;
		}
		catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (pstmt!=null)
				try
				{
					pstmt.close();
				}
				catch(Exception ex2)
				{
				}
			if (rs!=null)
				try
				{
					rs.close();
				}
				catch(Exception ex2)
				{
				}
			if (conn != null)
				try
				{
					conn.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}

	
	@Override
	public int compare(JmsMessage arg0, JmsMessage arg1)
	{
		return arg0.getTimestamp().compareTo(arg1.getTimestamp());
	}

}
