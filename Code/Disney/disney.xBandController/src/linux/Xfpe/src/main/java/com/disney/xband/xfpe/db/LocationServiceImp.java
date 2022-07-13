package com.disney.xband.xfpe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;

public class LocationServiceImp implements LocationService {

	private Logger logger = Logger.getLogger(LocationServiceImp.class);
	
	@Override
	public Location find(long id) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		 
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Location WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Location location = null;
			
			if (rs.next() == true)
				location = instantiateLocation(rs);
			
			return location;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
			if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {
                }
            }

			if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
			
			XfpeConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	@Override
	public Collection<Location> findAll() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;
		
		Collection<Location> locations = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			//return all location except for the UNKNOWN
			String query = "SELECT * FROM Location where locationTypeId <> 0 order by id";
			
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			
			locations = new LinkedList<Location>();
			
			while(rs.next())
				locations.add(instantiateLocation(rs));
		} 
		catch (SQLException e) {
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
		}
		finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }

			XfpeConnectionPool.getInstance().releaseConnection(conn);
		}
		return locations;
	}

	@Override
	public void save(Location location) throws Exception {
		if (location == null)
			return;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return;
			
			if (location.getId() == null)
			{
				//determine next id
				long id = getNextAvailableId(conn);
				
				//insert the new location
				String query = "INSERT INTO Location VALUE (?,?,?,?,?,?,?)";
				
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setLong(1, id);
				stmt.setInt(2, location.getLocationTypeId());
				stmt.setString(3, Location.sanitizeName(location.getName()));
				stmt.setDouble(4, location.getX());
				stmt.setDouble(5, location.getY());
				stmt.setInt(6, location.getSingulationTypeId());
				stmt.setInt(7, location.getEventGenerationTypeId());

                stmt.executeUpdate();
				
			} else {
				//update location
				String query = "UPDATE Location SET" +
						" locationTypeId = ?, name = ?, x = ?, y = ?, singulationTypeid = ?, eventGenerationTypeId = ?" +
						" WHERE id = ?";
				
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setInt(1, location.getLocationTypeId());
				stmt.setString(2, location.getName());
				stmt.setDouble(3, location.getX());
				stmt.setDouble(4, location.getY());
				stmt.setInt(5, location.getSingulationTypeId());
				stmt.setInt(6, location.getEventGenerationTypeId());
				stmt.setLong(7, location.getId());

				stmt.executeUpdate();
			}
		} 
		catch (SQLException e) {
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
		}
		finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }

			
			XfpeConnectionPool.getInstance().releaseConnection(conn);
		}
		return;
	}

	@Override
	public void delete(Location location) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public Map<Integer, String> getLocationTypes(boolean excludeUnknown) throws Exception {
		Map<Integer, String> locationTypes = new HashMap<Integer, String>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null; 
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			
			String query = "SELECT * FROM LocationType WHERE id >= ? order by id asc";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, (excludeUnknown ? 1 : 0));
			stmt.execute();
			rs = stmt.getResultSet();
			
			while (rs.next())
				locationTypes.put(rs.getInt("id"), rs.getString("LocationTypeName"));
			
			return locationTypes;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
		}
		finally
		{
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
			
			XfpeConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return null;
	}

	private Location instantiateLocation(ResultSet rs) throws SQLException
	{	
		Location location = new Location();
		
		location.setId(rs.getLong("id"));
		location.setLocationTypeId(rs.getInt("locationTypeId"));
		location.setName(rs.getString("name"));
		location.setX(rs.getDouble("x"));
		location.setY(rs.getDouble("y"));
		location.setSingulationTypeId(rs.getInt("singulationTypeId"));
		location.setEventGenerationTypeId(rs.getInt("eventGenerationTypeId"));
		location.setReaders(new LinkedList<Reader>());

        Boolean useSecureId = rs.getBoolean("useSecureId");
        if (rs.wasNull())
            useSecureId = null;

        location.setUseSecureId(useSecureId);

        location.setSuccessSequence(rs.getString("successSeq"));
        location.setFailureSequence(rs.getString("failureSeq"));
        location.setErrorSequence(rs.getString("errorSeq"));
        location.setIdleSequence(rs.getString("idleSeq"));

        return location;
	}
	
	private long getNextAvailableId(Connection conn) throws SQLException {
		String query = "SELECT max(id) FROM Location";
        PreparedStatement s = null;
        ResultSet rs = null;

        try {
		    s = conn.prepareStatement(query);
		    s.clearParameters();
		    s.execute();
		    rs = s.getResultSet();
		    long nextKey = rs.getLong(1);
		    return ++nextKey;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {
                }
            }

            if (s != null) {
                try {
                    s.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
}
