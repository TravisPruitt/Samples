package com.disney.xband.xfpe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;

public class ReaderServiceImp implements ReaderService {

	private Logger logger = Logger.getLogger(ReaderServiceImp.class);
	
	@Override
	public Reader find(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Reader WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Reader reader = null;
			
			if (rs.next() == true) {
				reader = instantiateReader(rs, conn);
			}
			
			return reader;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}
	
	@Override
	public Reader find(String readerId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Reader WHERE readerId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, readerId);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Reader reader = null;
			
			if (rs.next() == true) {
				reader = instantiateReader(rs, conn);
			}
			
			return reader;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}

	@Override
	public Collection<Reader> findAll() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Reader order by readerId";
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<Reader> results = new ArrayList<Reader>();
			
			while(rs.next()) {
				Reader reader = instantiateReader(rs, conn);
				results.add(reader);
			}
			
			return results;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}
	
	public Collection<Reader> findByLocation(Location location){
		if (location == null || location.getId() == null)
			return null;
					
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Reader where locationId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, location.getId());
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<Reader> results = new LinkedList<Reader>();
			
			while(rs.next()){
				Reader reader = instantiateReader(rs, conn);
				reader.setLocation(location);
				results.add(reader);
			}
			
			return results;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}
	
	public HashMap<Location,Collection<Reader>> getByLocation() {		
					
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT l.id as locationId, l.name as locationName, l.locationTypeId, l.singulationTypeId, l.eventGenerationTypeId, r.* " +
							" FROM Location l, Reader r " +
							" WHERE l.id <> 0 AND r.locationId = l.id" +
							" ORDER BY l.name, l.id, r.readerId";
			stmt = conn.prepareStatement(query);			
			stmt.execute();
			rs = stmt.getResultSet();
			HashMap<Location,Collection<Reader>> results = new LinkedHashMap<Location,Collection<Reader>>();
			
			Location location = null;
			Collection<Reader> readers = null;
			
			while(rs.next()) {
				//location
				Long locationId = rs.getLong("locationId");
				if (location == null || !location.getId().equals(locationId)) {		
					location = new Location();
					location.setId(locationId);
					location.setName(rs.getString("locationName"));
					location.setLocationTypeId(rs.getInt("locationTypeId"));
					location.setSingulationTypeId(rs.getInt("singulationTypeId"));
					location.setEventGenerationTypeId(rs.getInt("eventGenerationTypeId"));
					readers = new LinkedList<Reader>();

                    Boolean useSecureId = rs.getBoolean("useSecureId");
                    if (rs.wasNull())
                        useSecureId = null;

                    location.setUseSecureId(useSecureId);

                    location.setSuccessSequence(rs.getString("successSeq"));
                    location.setFailureSequence(rs.getString("failureSeq"));
                    location.setErrorSequence(rs.getString("errorSeq"));
                    location.setIdleSequence(rs.getString("idleSeq"));

                    results.put(location,readers);
				}
				
				//location's readers
				Reader reader = instantiateReader(rs, conn);
				reader.setLocation(location);

                if(readers != null) {
				    readers.add(reader);
                }
                else {
                    final String error = "Programming error in ReaderServiceImp.getByLocation(): readers must never be null";
                    logger.error(error);
                    throw new AssertionError(error);
                }
			}
			
			return results;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}
	
	@Override
	public Collection<Reader> findUnlinked() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM Reader WHERE locationId = 0";
			stmt = conn.prepareStatement(query);			
			stmt.execute();
			rs = stmt.getResultSet();

			Collection<Reader> readers = new LinkedList<Reader>();
			
			while(rs.next()) {
				Reader reader = instantiateReader(rs, conn);
				readers.add(reader);				
			}
			
			return readers;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	} 

	@Override
	public void delete(Reader reader) {
		// TODO Auto-generated method stub
	}
	
	private long getNextAvailableId(Connection conn) throws SQLException {
		String query = "SELECT max(id) FROM Reader";
		PreparedStatement s = null;
        ResultSet rs = null;
        long nextKey = 0;

        try {
            s = conn.prepareStatement(query);
		    s.execute();
		    rs = s.getResultSet();
		    nextKey = rs.getLong(1);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (s != null) {
                try {
                    s.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }

		return ++nextKey;
	}
	
	/**
	 * Removes the association between the reader and its location. DOES NOT delete the reader
	 * from the Reader table.
	 * 
	 * @param id
	 * @throws IllegalArgumentException in case of a null id
	 * @throws SQLException
	 */
	@Override
	public void unlinkReader(Long id) throws IllegalArgumentException, SQLException {
		
		if (id == null)
			throw new IllegalArgumentException("You must provide a reader id in order to remove a reader from location");
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			
			//remove a reader from location
			String query = "UPDATE Reader SET locationId = 0 WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} 
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    XfpeConnectionPool.getInstance().releaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }

		return;
	}
	
	private Reader instantiateReader(ResultSet rs, Connection conn) throws SQLException
	{	
		Reader reader = new Reader();
		
		//instantiate simple types
		reader.setId(rs.getLong("id"));
		reader.setType(ReaderType.getByOrdinal(rs.getInt("type")));		
		reader.setReaderId(rs.getString("readerId"));
		if (rs.wasNull())
			reader.setReaderId(null);
		reader.setX(rs.getInt("positionX"));
		if (rs.wasNull())
			reader.setX(null);
		reader.setY(rs.getInt("positionY"));
		if (rs.wasNull())
			reader.setY(null);
		reader.setGroup(rs.getString("group"));
		if (rs.wasNull())
			reader.setGroup(null);
		reader.setSingulationGroup(rs.getString("singulationGroup"));
		if (rs.wasNull())
			reader.setSingulationGroup(null);
		reader.setIpAddress(rs.getString("ipAddress"));
		if (rs.wasNull())
			reader.setIpAddress(null);
		reader.setPort(rs.getInt("port"));
		if (rs.wasNull())
			reader.setPort(null);
		reader.setSignalStrengthThreshold(rs.getInt("signalStrengthThreshold"));
		reader.setGain(rs.getDouble("gain"));
		reader.setMacAddress(rs.getString("macAddress"));
		reader.setLastIdReceived(rs.getLong("lastIdReceived"));
		if (rs.wasNull())
			reader.setLastIdReceived(null);
		reader.setLocationId(rs.getLong("locationId"));
		if (rs.wasNull())
			reader.setLocationId(null);
		reader.setTimeLastHello(new Timestamp(rs.getLong("timeLastHello")));
		if (rs.wasNull())
			reader.setTimeLastHello(null);
		
		reader.setTransmitPayload(rs.getString("transmitPayload"));
		
		reader.setDeviceId(rs.getInt("deviceId"));
		
		reader.setTransmitter(rs.getBoolean("isTransmitter"));
		
		return reader;
	}
}
