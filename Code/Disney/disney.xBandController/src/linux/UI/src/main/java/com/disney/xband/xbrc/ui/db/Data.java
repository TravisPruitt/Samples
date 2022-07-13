package com.disney.xband.xbrc.ui.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.xbrc.lib.entity.GST;
import com.disney.xband.xbrc.lib.entity.GuestInfo;
import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.Wall;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.bean.Event;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GuestStatusBean;
import com.disney.xband.xbrc.ui.bean.GuestStatusCounts;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;
import com.disney.xband.xbrc.ui.httpclient.Xview;
import com.disney.xband.xview.lib.model.Guest;

public class Data {
	private static Logger logger = Logger.getLogger(Data.class.getName());
	private static ConnectionPool cp;
	
	static {
		cp = UIConnectionPool.getInstance();
	}
	
	public static Connection GetConnection() throws SQLException {
		return cp.getConnection();        
    }
	
	public static void ReleaseConnection(Connection conn) {
		cp.releaseConnection(conn);
	}
	
	public static List<GuestInfo> GetGuests()
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = GetConnection();			
			if (conn==null)
				return null;
			
			String sSQL = "SELECT BandId,x,y,HasxPass from GuestPosition";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			ArrayList<GuestInfo> al = new ArrayList<GuestInfo>();
			
			while(rs.next())
			{
				GuestInfo g = new GuestInfo(rs.getString("BandId"), rs.getDouble("x"), rs.getDouble("y"), rs.getBoolean("HasxPass"));
				al.add(g);
			}
			
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
			try {
				if (rs != null) {
					rs.close();
                }
			}
			catch(Exception e) {
            }
			
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

			ReleaseConnection(conn);
		}
	}
	
	public static List<Wall> GetWalls()
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = GetConnection();

			if (conn == null)
				return null;
			
			String sSQL = "SELECT x0,y0,x1,y1 from Walls";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			ArrayList<Wall> al = new ArrayList<Wall>();
			
			while(rs.next())
			{
				Wall obj = new Wall(rs.getDouble("x0"), rs.getDouble("y0"),rs.getDouble("x1"), rs.getDouble("y1"));
				al.add(obj);
			}
			
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{			
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }
			
            ReleaseConnection(conn);
		}
	}
	
	public static List<GridItem> GetGridItems()
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;	
		
		try
		{
			conn = GetConnection();			
			if (conn==null)
				return null;
			
			/* NOTE: some classes rely on the order of the records returned by this query so don't change it */
			String sSQL = "SELECT `id`, `ItemType`, `XGrid`, `YGrid`, `State`, `Label`, `Description`, `Image`, `Sequence`, `XPassOnly`, `LocationId` from GridItem order by State, Sequence";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			ArrayList<GridItem> al = new ArrayList<GridItem>();
			while(rs.next())
			{
				GridItem gi = new GridItem(rs.getInt("id"), 
										   GridItem.ItemType.valueOf(rs.getString("ItemType")), 
										   rs.getInt("XGrid"), 
										   rs.getInt("YGrid"), 
										   GuestStatusState.valueOf(rs.getString("State").isEmpty() ? "INDETERMINATE" : rs.getString("State")), 
										   rs.getString("Label"), 
										   rs.getString("Description"),
										   rs.getString("Image"),
										   rs.getInt("Sequence"),
										   XpassOnlyState.fromOrdinal(rs.getInt("XPassOnly")),
										   rs.getLong("LocationId"));
				al.add(gi);
			}
			
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error getting GridItem data: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }
			
            ReleaseConnection(conn);
		}
	}
	
	public static List<GuestStatusBean> GetGuestStatuses()
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		GSTService gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		try
		{
			conn = GetConnection();			
			if (conn==null)
				return null;
					  
			  
			String sSQL = "SELECT `GuestId`,`HasXPass`,`State`,`LastReader`,`TimeEarliestAtReader`,`TimeLatestAtReader`,`TimeEntered`,TimeMerged`"
						+ "`TimeLoaded`,`TimeExited`,`CarID`,`HasDeferredEntry` from GST ";
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			ArrayList<GuestStatusBean> al = new ArrayList<GuestStatusBean>();
			while(rs.next())
			{					
				GST gst = gstService.instantiateGST(rs);
				GuestStatusBean gsb = new GuestStatusBean(gst);
				Guest guest = Xview.getInstance().getGuestByGuestId(gst.getId());
				gsb.setGuest(guest);
				
				al.add(gsb);
			}
			
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }
			
            ReleaseConnection(conn);
		}
	}
	/*
	public static List<GuestStatus> GetGuestStatuses()
	{
		Connection conn = null;
		
		try
		{
			conn = GetConnection();			
			if (conn==null)
				return null;
					  
			  
			String sSQL = "SELECT `GuestId`,`HasXPass`,`State`,`LastReader`,`TimeEarliestAtReader`,`TimeLatestAtReader`,`TimeEntered`,TimeMerged`"
						+ "`TimeLoaded`,`TimeExited`,`CarID`,`HasDeferredEntry` from GST";
			Statement stmt = conn.createStatement();
			stmt.execute(sSQL);
			ResultSet rs = stmt.getResultSet();
			ArrayList<GuestStatus> al = new ArrayList<GuestStatus>();
			while(rs.next())
			{					
				GuestStatus gs = new GuestStatus(
						rs.getString("GuestId"), 
						rs.getBoolean("HasXPass"),
						GuestStatusState.valueOf(rs.getString("State")), 
						rs.getString("LastReader"), 
						new java.util.Date(rs.getLong("TimeEarliestAtReader")), 
						new java.util.Date(rs.getLong("TimeLatestAtReader")), 
						new java.util.Date(rs.getLong("TimeEntered")),
						new java.util.Date(rs.getLong("TimeMerged")),
						new java.util.Date(rs.getLong("TimeLoaded")),
						new java.util.Date(rs.getLong("TimeExited")),
						rs.getString("CarId"),
						rs.getBoolean("HasDeferredEntry"));
				al.add(gs);
			}
			conn.close();
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
			ReleaseConnection(conn);
		}
	}
	*/		
		
	public static List<GuestStatusCounts> GetGuestStatusCounts()
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
        ArrayList<GuestStatusCounts> al = new ArrayList<GuestStatusCounts>();
		
		try
		{	
			conn = GetConnection();			
			if (conn==null)
				return null;
			
			/* First get all the guests using the location */
			String sSQL = "SELECT gi.id, XGrid, YGrid, ItemType, xPassOnly, Label, gs.State, Sequence, gs.HasXPass, gi.LocationId, l.name as LocationName, count(*) as count " +
					"FROM GST gs, GridItem gi, Reader r, Location l " +
					"where " + 
					"gs.lastReader = r.readerId and r.locationId = l.id and gi.locationId = l.id and " +
					"gi.ItemType <> \"Gate\" and gi.LocationId is not null and " +
					"((gs.HasXPass = 1 and gi.xPassOnly in (1,0)) or (gs.HasXPass = 0 and gi.xPassOnly in (2,0))) " + 
					"group by gi.id, LocationId, gs.HasXPass order by gs.HasXPass, l.id, Sequence desc;";
			
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			rs = stmt.getResultSet();

			while(rs.next())
			{					
				GuestStatusCounts gs = new GuestStatusCounts(
						rs.getInt("id"),
						rs.getInt("XGrid"),
						rs.getInt("YGrid"),
						GridItem.ItemType.valueOf(rs.getString("ItemType")),
						XpassOnlyState.fromOrdinal(rs.getInt("xPassOnly")),
						rs.getString("Label"),
						GuestStatusState.valueOf(rs.getString("State").isEmpty() ? "INDETERMINATE" : rs.getString("State")), 
						rs.getInt("Sequence"),
						rs.getInt("count"),
						rs.getBoolean("HasXPass"),
						rs.getLong("LocationId"),
						rs.getString("LocationName"));
						
				al.add(gs);
			}
        }
        catch (SQLException e)
        {
            logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
            return null;
        }
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }
        }

        stmt = null;
        rs = null;
        
        StringBuffer sbLocations = new StringBuffer();
        
        try
		{	
			/* First get all the guests using the location */
			String sSQL = "select distinct LocationId from GridItem where LocationId is not null";
			
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			rs = stmt.getResultSet();			

			while(rs.next())
			{					
				if (sbLocations.length() > 0)
					sbLocations.append(",");
				sbLocations.append(rs.getInt("LocationId"));		
			} 
        }
        catch (SQLException e)
        {
            logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
            return null;
        }
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }
        }

        stmt = null;
        rs = null;

        try {
			/* Now the all guests by the State */
			String sSQL = "SELECT gi.id, XGrid, YGrid, ItemType, xPassOnly, Label, gs.State, Sequence, gs.HasXPass, count(*) as count " +
					"FROM GST gs, GridItem gi, Reader r, Location l " +
					"where " +
					"gs.lastReader = r.readerId and r.locationId = l.id and ";
			
			if (sbLocations.length() > 0)
				sSQL += "l.id not in (" + sbLocations.toString() + ") and ";
				
			sSQL +=	"gs.State = gi.State and gi.ItemType <> \"Gate\" and " +
					"((gs.HasXPass = 1 and gi.xPassOnly in (1,0)) or (gs.HasXPass = 0 and gi.xPassOnly in (2,0))) " +
					"group by gi.id, gs.State, gs.HasXPass order by gs.HasXPass, State, Sequence desc; ";
			
			stmt = conn.prepareStatement(sSQL);
			stmt.execute();
			
			rs = stmt.getResultSet();
			while(rs.next())
			{					
				GuestStatusCounts gs = new GuestStatusCounts(
						rs.getInt("id"),
						rs.getInt("XGrid"),
						rs.getInt("YGrid"),
						GridItem.ItemType.valueOf(rs.getString("ItemType")),
						XpassOnlyState.fromOrdinal(rs.getInt("xPassOnly")),
						rs.getString("Label"),
						GuestStatusState.valueOf(rs.getString("State").isEmpty() ? "INDETERMINATE" : rs.getString("State")), 
						rs.getInt("Sequence"),
						rs.getInt("count"),
						rs.getBoolean("HasXPass"),
						null,
						null);
						
				al.add(gs);
			}
			
			return al;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }
	}
	
	public static void moveGridItem(Long id, Integer xgrid, Integer ygrid) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = GetConnection();			
			if (conn==null)
				return;
			
			String sSQL = "update GridItem set XGrid = ?, YGrid = ? where id = ?";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setInt(1, xgrid);
			stmt.setLong(2, ygrid);
			stmt.setLong(3, id);
			stmt.execute();
		}
		catch (SQLException e)
		{
			logger.error("SQL error deleting GridItem: " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }
	}
		
	public static List<GuestStatusBean> getReaderGuests(String readerId) {		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		GSTService gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		try
		{
			conn = GetConnection();	
			if (conn == null)
				return null;
			
			// The following query is a bit complicated because it returns guest counts for all readers including the ones with guest count equal to zero.
			String sSQL = "select * from GST where LastReader = ? order by GuestId";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setString(1, readerId);
			stmt.execute();
			
			rs = stmt.getResultSet();
			List<GuestStatusBean> result = new LinkedList<GuestStatusBean>();
			while(rs.next()) {	
				
				GST gst = gstService.instantiateGST(rs);
				GuestStatusBean gsb = new GuestStatusBean(gst);
				Guest guest = Xview.getInstance().getGuestByGuestId(gst.getId());
				gsb.setGuest(guest);
				
				result.add(gsb);						
			}			
			
			return result;	
		}
		catch (SQLException e)
		{
			logger.error("SQL error reading from GST table " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }

		return null;
	}
	
	public static List<GuestStatusBean> getStateGuests(GuestStatusState state, GridItem.XpassOnlyState xPassOnly) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		GSTService gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		try
		{
			conn = GetConnection();	
			if (conn == null)
				return null;
			
			List<GuestStatusBean> result = new LinkedList<GuestStatusBean>();
			
			// Never show Guests in INDETERMINATE state
			//if (state == GuestStatusState.INDETERMINATE)
			//	return result;
			
			// The following query is a bit complicated because it returns guest counts for all readers including the ones with guest count equal to zero.
			String sSQL = null;
			switch (xPassOnly) {
			case AllGuests:
				sSQL = "select * from GST where State = ? order by GuestId";
				break;
			case NonXpassGuests:
				sSQL = "select * from GST where State = ? and (HasXPass = 0 or HasXPass is null) order by GuestId";
				break;
			case XpassGuests:
				sSQL = "select * from GST where State = ? and HasXPass = 1 order by GuestId";
				break;
			}	
			
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setString(1, state.toString());
			stmt.execute();
			
			rs = stmt.getResultSet();			
			while(rs.next()) {	
				
				GST gst = gstService.instantiateGST(rs);
				GuestStatusBean gsb = new GuestStatusBean(gst);
				Guest guest = Xview.getInstance().getGuestByGuestId(gst.getId());
				gsb.setGuest(guest);
				
				result.add(gsb);				
			}			
			
			return result;			
		}
		catch (SQLException e)
		{
			logger.error("SQL error reading from GST table " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }

		return null;
	}
	
	public static List<GuestStatusBean> getLocationGuests(Long locationId, GridItem.XpassOnlyState xPassOnly) {		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		GSTService gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		try
		{
			conn = GetConnection();	
			if (conn == null)
				return null;
			
			// The following query is a bit complicated because it returns guest counts for all readers including the ones with guest count equal to zero.
			String sSQL = "select t.* from GST t, Reader r where t.LastReader = r.readerId and r.locationId = ? ";
		
			switch (xPassOnly) {
			case AllGuests:
				break;
			case NonXpassGuests:
				sSQL += " and (t.HasXPass = 0 or t.HasXPass is null)";
				break;
			case XpassGuests:
				sSQL += " and t.HasXPass = 1";
				break;
			}	
			
			sSQL += " order by GuestId";
			
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.execute();
			
			rs = stmt.getResultSet();
			List<GuestStatusBean> result = new LinkedList<GuestStatusBean>();
			while(rs.next()) {
				
				GST gst = gstService.instantiateGST(rs);
				GuestStatusBean gsb = new GuestStatusBean(gst);
				Guest guest = Xview.getInstance().getGuestByGuestId(gst.getId());
				gsb.setGuest(guest);
				
				result.add(gsb);						
			}
			
			return result;			
		}
		catch (SQLException e)
		{
			logger.error("SQL error reading from GST table " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }

		return null;
	}
	
	public static HashMap<Reader,Collection<GuestStatusBean>> getGuestStatusByReader(Long locationId) {		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		GSTService gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		try
		{
			conn = GetConnection();	
			if (conn == null)
				return null;
			
			// The following query is a bit complicated because it returns guest counts for all readers including the ones with guest count equal to zero.
			String sSQL = "select r.id as rId, r.readerId, t.* from Reader r left join GST t on r.readerId = t.LastReader where r.locationId = ? order by r.id, t.GuestId";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.execute();
			
			rs = stmt.getResultSet();
			LinkedHashMap<Reader,Collection<GuestStatusBean>> result = new LinkedHashMap<Reader,Collection<GuestStatusBean>>();
			Reader reader = null;
			Collection<GuestStatusBean> statuses = null;			
			
			while(rs.next()) {
				Long rId = rs.getLong("rId");
				if (reader == null || !reader.getId().equals(rId)) {
					reader = new Reader();
					reader.setId(rId);
					reader.setReaderId(rs.getString("readerId"));
					statuses = new LinkedList<GuestStatusBean>();
					result.put(reader, statuses);
				}
						
				// get the gst.id to set the wasNull flag on the recordset
				rs.getLong("GuestId");
				if (!rs.wasNull())
				{
					GST gst = gstService.instantiateGST(rs);
					GuestStatusBean gsb = new GuestStatusBean(gst);
					Guest guest = Xview.getInstance().getGuestByGuestId(gst.getId());
					gsb.setGuest(guest);
					statuses.add(gsb);
				}				
			}
			
			return result;			
		}
		catch (SQLException e)
		{
			logger.error("SQL error reading from GST table " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }

		return null;
	}
	
	public static Event getLastEventForBand(Collection<String> bandIds) {		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = GetConnection();	
			if (conn == null)
				return null;
			
			// Build the in clause
			String inClause = "(";			
			boolean bFirst = true;
			for (String id : bandIds) {
				if (!bFirst) {
					inClause += ",";
				}
				inClause += "\"" + id + "\"";
				bFirst = false;
			}			
			inClause += ")";

			String sSQL = "select e.* from Events e where BandID in " + inClause +
						  " and Timestamp = (select max(Timestamp) from Events where BandID in " + inClause + " )";
			
		    stmt = conn.createStatement();
			stmt.execute(sSQL);
			
			// Long id, String readerId, String bandId, Date timestamp,Integer strength, Integer packetSequence, Integer frequencey,Integer channel
			rs = stmt.getResultSet();
			while(rs.next()) {
				Event event = new Event(
						rs.getLong("id"),
						rs.getString("ReaderID"),
						rs.getString("BandID"),
						new Date(rs.getLong("Timestamp")),
						rs.getInt("Strength"),
						rs.getInt("PacketSequence"),
						rs.getInt("Frequency"),
						rs.getInt("Channel"));
				return event;
			}
			
			return null;			
		}
		catch (SQLException e)
		{
			logger.error("SQL error reading from GST table " + e.getLocalizedMessage(), e);
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }

		return null;
	}
	
	public static String getProperty(String className, String propertyName)
	{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String result = null;

		try
		{
			conn = GetConnection();			
			if (conn==null)
				return null;
					  
			  
			String sSQL = "SELECT value FROM Config WHERE class=? AND property=?";
			stmt = conn.prepareStatement(sSQL);
			stmt.clearParameters();
			stmt.setString(1, className);
			stmt.setString(2, propertyName);
			stmt.execute();
			
			rs = stmt.getResultSet();
			if(rs != null && rs.next())
			{					
				result = rs.getString("value");
				if (rs.wasNull())
					result = null;
			}
			
			return result;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error getting GuestStatus data: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally
        {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch(Exception e) {
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch(Exception e) {
            }

            ReleaseConnection(conn);
        }
	}
}