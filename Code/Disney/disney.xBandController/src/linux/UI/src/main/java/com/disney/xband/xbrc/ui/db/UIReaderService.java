package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.bean.LocationBean;
import com.disney.xband.xbrc.ui.bean.ReaderValue;

public class UIReaderService extends com.disney.xband.xbrc.lib.db.ReaderService 
{

	private static Logger logger = Logger.getLogger(UIReaderService.class);

	public static HashMap<Location,Collection<ReaderValue>> getReaderValuesByLocation() 
	{		
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement unLocationStmt = null;
		PreparedStatement tiStmt = null;
		PreparedStatement newLocationStmt = null;
		PreparedStatement lastLocTypeId = null;
		PreparedStatement newLocTypeStmt = null;
		
		ResultSet unRs = null;
		ResultSet rs = null;
		ResultSet tiRs = null;
		ResultSet lltRs = null;
		
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return null;
			
			// we must have the UNKNOWN location
			String query = "SELECT * FROM Location WHERE name = 'UNKNOWN'";
			unLocationStmt = conn.prepareStatement(query);
			unLocationStmt.execute();
			unRs = unLocationStmt.getResultSet();
			
			if (!unRs.next()){
				// UNKNOWN location missing, create
				
				// get UNKNOWN type id
				String getTypeQuery = "select id from LocationType where LocationTypeName = 'UNKNOWN'";
				tiStmt = conn.prepareStatement(getTypeQuery);
				tiStmt.execute();
				tiRs = tiStmt.getResultSet();
				
				int uLocTypeId = -1;
				while (tiRs.next()){
					uLocTypeId = tiRs.getInt("id");
				}
				
				if (uLocTypeId < 0){
					// UNKNOWN location type does not exist, create
					getTypeQuery = "select max(id) as maxId from LocationType";
					lastLocTypeId = conn.prepareStatement(getTypeQuery);
					lastLocTypeId.execute();
					lltRs = lastLocTypeId.getResultSet();
					
					if (lltRs.next()){
						uLocTypeId = lltRs.getInt("maxId");
					} else {
						uLocTypeId = -1;
					}
					
					query = "insert into LocationType values(?,?)";
					newLocTypeStmt = conn.prepareStatement(query);
					newLocTypeStmt.clearParameters();
					newLocTypeStmt.setInt(1, ++uLocTypeId);
					newLocTypeStmt.setString(2, "UNKNOWN");
					newLocTypeStmt.execute();
				}
				
				// create the UNKNOWN location: id, locationTypeId,name,x,y,singulationTypeId,eventGenerationTypeId
				String insertUnknownQuery = "insert into Location values(?,?,?,?,?,?,?)";
				newLocationStmt = conn.prepareStatement(insertUnknownQuery);
				newLocationStmt.clearParameters();
				newLocationStmt.setInt(1, 0);
				newLocationStmt.setInt(2, uLocTypeId);
				newLocationStmt.setString(3, "UNKNOWN");
				newLocationStmt.setInt(4, 0);
				newLocationStmt.setInt(5, 0);
				newLocationStmt.setInt(6, 0);
				newLocationStmt.setInt(7, 0);
				newLocationStmt.execute();
			}
			
			query = "SELECT l.id as locationId, l.name as locationName, l.locationTypeId, l.singulationTypeId, l.eventGenerationTypeId, r.*" +
							" FROM Location l LEFT JOIN Reader r" +
							" ON l.id <> 0 AND l.id = r.locationId" +
							" ORDER BY l.name, l.id, r.readerId";
			stmt = conn.prepareStatement(query);			
			stmt.execute();
			rs = stmt.getResultSet();
			HashMap<Location,Collection<ReaderValue>> results = new LinkedHashMap<Location,Collection<ReaderValue>>();
			
			Location location = null;
			Collection<ReaderValue> readers = null;
			
			Location unknown = null;
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
					readers = new LinkedList<ReaderValue>();
					results.put(location,readers);
					
					// save a pointer to the unknown location for later
					if (location.getName().equals("UNKNOWN"))
						unknown = location;
				}
				
				//location's readers String readerId, Double gain, Integer threshold, LocationBean location
				String readerId = rs.getString("readerId");
				if (rs.wasNull()){
					// location with no readers
					continue;
				}
					
				ReaderValue reader = new ReaderValue(readerId, 
													rs.getDouble("gain"), 
													rs.getInt("signalStrengthThreshold"), 
													new LocationBean(locationId));

                if(readers == null) {
                    final String error = "Programming error in ReaderServiceImp.getByLocation(): readers must never be null";
                    logger.error(error);
                    throw new AssertionError(error);
                }
                else {
				    readers.add(reader);
                }
			}
			
			// move the unknown location to the end of the list, so that it doesn't display as the first item
			Collection<ReaderValue> unknownLocReaders = results.get(unknown);
			results.remove(unknown);
			results.put(unknown, unknownLocReaders);
			
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

            if (unRs != null) {
                try {
                    unRs.close();
                }
                catch (Exception ignore) {}
            }

            if (tiRs != null) {
                try {
                    tiRs.close();
                }
                catch (Exception ignore) {}
            }

            if (lltRs != null) {
                try {
                    lltRs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            if (unLocationStmt != null) {
                try {
                    unLocationStmt.close();
                }
                catch (Exception ignore) {}
            }

            if (tiStmt != null) {
                try {
                    tiStmt.close();
                }
                catch (Exception ignore) {}
            }

            if (newLocationStmt != null) {
                try {
                    newLocationStmt.close();
                }
                catch (Exception ignore) {}
            }

            if (lastLocTypeId != null) {
                try {
                    lastLocTypeId.close();
                }
                catch (Exception ignore) {}
            }

            if (newLocTypeStmt != null) {
                try {
                    newLocTypeStmt.close();
                }
                catch (Exception ignore) {}
            }

            if(conn != null) {
                try {
                    Data.ReleaseConnection(conn);
                }
                catch (Exception ignore) {}
            }
        }
	}
}
