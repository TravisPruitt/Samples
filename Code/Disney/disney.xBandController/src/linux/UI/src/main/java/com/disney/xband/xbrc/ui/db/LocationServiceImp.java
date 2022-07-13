package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.ui.exception.LocationCanNotBeDeletedException;

public class LocationServiceImp implements LocationService {

	private Logger logger = Logger.getLogger(LocationServiceImp.class);
	
	@Override
	public Location find(long id) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		 
		try
		{
			conn = Data.GetConnection();			
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
			
			Data.ReleaseConnection(conn);
		}
	}

	@Override
	public Collection<Location> findAll(boolean excludeUnknown) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;	
		ResultSet rs = null;
		
		Collection<Location> locations = new LinkedList<Location>();
		
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return locations;
			
			//return all location except for the UNKNOWN
			String query = null;
			if (excludeUnknown)
				query = "SELECT * FROM Location where locationTypeId <> 0 order by id";
			else 
				query = "SELECT * FROM Location order by id";
			
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			
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
			
			Data.ReleaseConnection(conn);
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
			conn = Data.GetConnection();			
			if (conn==null)
				return;
			
			if (location.getId() == null)
			{
				//determine next id
				long id = getNextAvailableId(conn);				
				
				//insert the new location
				String query = "INSERT INTO Location (id, locationTypeId, name, locationId, x, y, singulationTypeid, eventGenerationTypeId, useSecureId, successSeq, successTimeout, failureSeq, failureTimeout, errorSeq, errorTimeout, tapSeq, tapTimeout, idleSeq, transmitZoneGroup, sendBandStatus) VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setLong(1, id);
				stmt.setInt(2, location.getLocationTypeId());
				stmt.setString(3, Location.sanitizeName(location.getName()));
				stmt.setString(4, location.getLocationId() != null ? location.getLocationId() : "");
				stmt.setDouble(5, location.getX() != null ? location.getX() : 0);
				stmt.setDouble(6, location.getY() != null ? location.getY() : 0);
				stmt.setInt(7, location.getSingulationTypeId() != null ? location.getSingulationTypeId() : 0);
				stmt.setInt(8, location.getEventGenerationTypeId() != null ? location.getEventGenerationTypeId() : 0);
				
				if (location.isUseSecureId() == null)
					stmt.setNull(9, Types.BOOLEAN);
				else
					stmt.setBoolean(9, location.isUseSecureId());

				if (location.getSuccessSequence() != null && location.getSuccessSequence().trim().isEmpty())
					location.setSuccessSequence(null);				
				if (location.getFailureSequence() != null && location.getFailureSequence().trim().isEmpty())
					location.setFailureSequence(null);				 
				if (location.getErrorSequence() != null && location.getErrorSequence().trim().isEmpty())
					location.setErrorSequence(null);
				if (location.getTapSequence() != null && location.getTapSequence().trim().isEmpty())
					location.setTapSequence(null);
				
                stmt.setString(10, location.getSuccessSequence());
                stmt.setLong(11, location.getSuccessTimeout());
                stmt.setString(12, location.getFailureSequence());
                stmt.setLong(13, location.getFailureTimeout());
                stmt.setString(14, location.getErrorSequence());
                stmt.setLong(15, location.getErrorTimeout());
                stmt.setString(16, location.getTapSequence());
                stmt.setLong(17, location.getTapTimeout());
                stmt.setString(18, location.getIdleSequence());
                stmt.setString(19, location.getTransmitZoneGroup());
                stmt.setBoolean(20, location.getSendBandStatus());

                stmt.executeUpdate();
                
                location.setId(id);
				
			} else {
				//update location
				String query = "UPDATE Location SET" +
						" locationTypeId = ?, name = ?, x = ?, y = ?, locationId = ?, singulationTypeid = ?, eventGenerationTypeId = ?, useSecureId = ?, successSeq = ?, successTimeout = ?," + 
						" failureSeq = ?, failureTimeout = ?, errorSeq = ?, errorTimeout = ?, tapSeq = ?, tapTimeout = ?, idleSeq = ?, transmitZoneGroup=?, sendBandStatus=?" +
						" WHERE id = ?";
				
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setInt(1, location.getLocationTypeId());
				stmt.setString(2, location.getName());
				stmt.setDouble(3, location.getX());
				stmt.setDouble(4, location.getY());
				stmt.setString(5, location.getLocationId() != null ? location.getLocationId() : "");
				stmt.setInt(6, location.getSingulationTypeId());
				stmt.setInt(7, location.getEventGenerationTypeId());
				
				if (location.isUseSecureId() == null)
					stmt.setNull(8, Types.BOOLEAN);
				else
					stmt.setBoolean(8, location.isUseSecureId());
				
				if (location.getSuccessSequence() != null && location.getSuccessSequence().trim().isEmpty())
					location.setSuccessSequence(null);				
				if (location.getFailureSequence() != null && location.getFailureSequence().trim().isEmpty())
					location.setFailureSequence(null);				 
				if (location.getErrorSequence() != null && location.getErrorSequence().trim().isEmpty())
					location.setErrorSequence(null);

                stmt.setString(9, location.getSuccessSequence());
                stmt.setLong(10, location.getSuccessTimeout());
                stmt.setString(11, location.getFailureSequence());
                stmt.setLong(12, location.getFailureTimeout());
                stmt.setString(13, location.getErrorSequence());
                stmt.setLong(14, location.getErrorTimeout());
                stmt.setString(15, location.getTapSequence());
                stmt.setLong(16, location.getTapTimeout());
                stmt.setString(17, location.getIdleSequence());
                stmt.setString(18, location.getTransmitZoneGroup());
                stmt.setBoolean(19, location.getSendBandStatus());

                stmt.setLong(20, location.getId());
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

			Data.ReleaseConnection(conn);
		}
		return;
	}

	@Override
	public void delete(Long locationId, Collection<String> xbrcUrl) throws LocationCanNotBeDeletedException, Exception {
		if (locationId == null)
			throw new IllegalArgumentException("Missing the id of the location to be deleted.");
		
		Connection conn = null;
		PreparedStatement stmt = null;		
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = true;
		
		try
		{
			conn = Data.GetConnection();
			if (conn == null)
				return;
			
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			// clean up GridItems
			String query = "DELETE from GridItem where LocationId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.execute();
			stmt.close();
			
			// clean up transmit commands
			// remove the command from the database
			stmt = conn.prepareStatement("DELETE FROM TransmitRecipients WHERE commandId in (select id from TransmitCommand where locationId = " + locationId + ")");
			stmt.clearParameters();
			stmt.executeUpdate();
			stmt.close();
			
			stmt = conn.prepareStatement("DELETE FROM TransmitCommand WHERE locationId = ?");
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.executeUpdate();
			stmt.close();
			
			// unlink all readers from this location
			Location location = find(locationId);
			Collection<Reader> readers = ReaderService.findByLocation(conn, location);
			for (Reader reader : readers)
			{
				ReaderService.unlinkReader(conn, reader, xbrcUrl);
			}
			
			// delete the location
			query = "DELETE from Location WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.executeUpdate();
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
            
            if(conn != null) {
                try {
                	conn.setAutoCommit(autoCommit);
                	conn.setTransactionIsolation(transactionIsolation);
                	
                    Data.ReleaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
		}
		return;
	}
	
	@Override
	public Map<Integer, String> getLocationTypes(boolean excludeUnknown) throws Exception {
		Map<Integer, String> locationTypes = new HashMap<Integer, String>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null; 
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();			
			
			String query = "SELECT * FROM LocationType WHERE id >= ? order by id asc";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
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
			
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return null;
	}

	private Location instantiateLocation(ResultSet rs) throws SQLException
	{	
		Location location = new Location();
		
		location.setId(rs.getLong("id"));
		location.setLocationTypeId(rs.getInt("locationTypeId"));
		location.setName(rs.getString("name"));
		location.setLocationId(rs.getString("locationId"));
		location.setX(rs.getDouble("x"));
		location.setY(rs.getDouble("y"));
		location.setSingulationTypeId(rs.getInt("singulationTypeId"));
		location.setEventGenerationTypeId(rs.getInt("eventGenerationTypeId"));
		
		location.setUseSecureId(rs.getBoolean("useSecureId"));
		if (rs.wasNull())
			location.setUseSecureId(null);

        location.setSuccessSequence(rs.getString("successSeq"));
        location.setSuccessTimeout(rs.getLong("successTimeout"));
        if (location.getSuccessTimeout() == null)
        	location.setSuccessTimeout(0l);
        location.setFailureSequence(rs.getString("failureSeq"));
        location.setFailureTimeout(rs.getLong("failureTimeout"));
        if (location.getFailureTimeout() == null)
        	location.setFailureTimeout(0l);
        location.setErrorSequence(rs.getString("errorSeq"));
        location.setErrorTimeout(rs.getLong("errorTimeout"));
        if (location.getErrorTimeout() == null)
        	location.setErrorTimeout(0l);
        location.setTapSequence(rs.getString("tapSeq"));
        location.setTapTimeout(rs.getLong("tapTimeout"));
        if (location.getTapTimeout() == null)
        	location.setTapTimeout(0l);
        location.setIdleSequence(rs.getString("idleSeq"));
        
        location.setSendBandStatus(rs.getBoolean("sendBandStatus"));
		if (rs.wasNull())
			location.setSendBandStatus(false);
        
        location.setTransmitZoneGroup(rs.getString("transmitZoneGroup"));
		if (rs.wasNull() || location.getTransmitZoneGroup().isEmpty())
			location.setTransmitZoneGroup(null);

        return location;
	}
	
	private long getNextAvailableId(Connection conn) throws SQLException {
		String query = "SELECT max(id) as maxId FROM Location";
		PreparedStatement s = null;
        ResultSet rs = null;

        try {
            s = conn.prepareStatement(query);
		    s.execute();
		    rs = s.getResultSet();

		    long nextKey = -1L;
		    if (rs.next())
			    nextKey = rs.getLong("maxId");
		
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
