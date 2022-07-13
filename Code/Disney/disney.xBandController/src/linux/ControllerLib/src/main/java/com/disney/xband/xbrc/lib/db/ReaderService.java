package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.net.NetInterface;

public class ReaderService {

	private static Logger logger = Logger.getLogger(ReaderService.class);
	
	public static Reader find(Connection conn, long id) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{			
			String query = "SELECT * FROM Reader WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Reader reader = null;
			
			if (rs.next() == true) {
				reader = instantiateReader(rs);
				instantiateReaderAntennas(conn, reader);
			}
			
			return reader;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally {
			if (rs != null)
			{
                try 
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

			if (stmt != null)
			{
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
		}
	}
	
	public static Reader find(Connection conn, String readerId) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{			
			String query = "SELECT * FROM Reader WHERE readerId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, readerId);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Reader reader = null;
			
			if (rs.next() == true) {
				reader = instantiateReader(rs);
				instantiateReaderAntennas(conn, reader);
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
        }
	}

	public static Collection<Reader> findAll(Connection conn) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{	
			String query = "SELECT * FROM Reader";
			stmt = conn.prepareStatement(query);
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<Reader> results = new ArrayList<Reader>();
			
			while(rs.next()) {
				Reader reader = instantiateReader(rs);
				instantiateReaderAntennas(conn, reader);
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
        }
	}
	
	public static Collection<Reader> findByLocation(Connection conn, Location location)
	{
		if (conn == null)
			return null;
		
		if (location == null || location.getId() == null)
			return null;
					
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "SELECT * FROM Reader where locationId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, location.getId());
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<Reader> results = new LinkedList<Reader>();
			
			while(rs.next()){
				Reader reader = instantiateReader(rs);
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
        }
	}
	
	public static Collection<Reader> findTransmittersByLocation(Connection conn, Long locationId)
	{
		if (conn == null)
			return null;
					
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "SELECT * FROM Reader where locationId = ? and type = 1 order by transmitterHaPriority";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, locationId);
			stmt.execute();
			rs = stmt.getResultSet();
			Collection<Reader> results = new LinkedList<Reader>();
			
			while(rs.next()){
				Reader reader = instantiateReader(rs);
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
        }
	}

	private static void insertAntenna(Connection conn, long readerId, int antenna, boolean power, boolean commitTransaction) throws Exception
	{
		PreparedStatement stmt = null;

		int transactionIsolation = 0;
		boolean autoCommit = false;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);		
		
			String query = "INSERT INTO ReaderAntenna (`readerId`,`antenna`,`power`) " + 
					"VALUES (?,?,?)";

			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, readerId);
			stmt.setInt(2, antenna);
			stmt.setBoolean(3, power);
			
			stmt.executeUpdate();
			
			if (commitTransaction)
				conn.commit();
		}
		catch (SQLException e)
		{
			try
			{
				if (commitTransaction)
					conn.rollback();
			}
			catch (SQLException ignore) {}

			throw e;
		}
		finally
		{
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
			
            try
            {
                conn.setAutoCommit(autoCommit);
            }
            catch (SQLException e){}

            try
            {
                conn.setTransactionIsolation(transactionIsolation);
            }
            catch (SQLException e){}
		}
	}
	
	public static void updateLastReaderTest(Connection conn, long id, Date lastReaderTestTime, boolean lastReaderTestSuccess, String lastReaderTestUser) throws Exception
	{
		PreparedStatement stmt = null;
		try
		{			
			String query = "UPDATE Reader SET `lastReaderTestTime` = ?, `lastReaderTestSuccess` = ?, `lastReaderTestUser` = ? " +
					" WHERE `id` = ?";

			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			if (lastReaderTestTime == null)
				stmt.setNull(1, Types.INTEGER);
			else
				stmt.setLong(1, lastReaderTestTime.getTime());		
			stmt.setBoolean(2, lastReaderTestSuccess);
			stmt.setString(3, lastReaderTestUser);
			stmt.setLong(4, id);
			
			stmt.executeUpdate();
		}
		finally
		{
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
		}		
	}
	
	
	private static boolean updateAntenna(Connection conn, long readerId, int antenna, boolean power, boolean commitTransaction) throws Exception
	{
		boolean isUpdated = false;
		PreparedStatement stmt = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		try
		{
			int updates = 0;
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);		

			String query = "UPDATE ReaderAntenna SET `power` = ? WHERE `readerId` = ? AND `antenna` = ?";

			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setBoolean(1, power);
			stmt.setLong(2, readerId);
			stmt.setInt(3, antenna);
		
			updates = stmt.executeUpdate();

			if (commitTransaction)
				conn.commit();

			if (updates > 0)
				isUpdated = true;
		}
		catch (SQLException e)
		{
			try
			{
				if (commitTransaction)
					conn.rollback();
			}
			catch (SQLException ignore) {}

			throw e;
		}
		finally
		{
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
			
            try
            {
                conn.setAutoCommit(autoCommit);
            }
            catch (SQLException e){}

            try
            {
                conn.setTransactionIsolation(transactionIsolation);
            }
            catch (SQLException e){}
		}
		return isUpdated;
	}
	
	public static void save(Connection conn, Reader reader) throws Exception 
	{
		if (conn == null)
			return;
		
		if (reader == null)
			throw new IllegalArgumentException();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			if (reader.getId() == null)
			{
				//determine next id
				long id = getNextAvailableId(conn);
			
				String query = "INSERT INTO Reader (`id`,`type`,`readerId`,`positionX`,`positionY`,`group`,`singulationGroup`,`signalStrengthThreshold`,`gain`," +
							   "`macAddress`,`ipAddress`,`port`,`lastIdReceived`,`locationId`,`timeLastHello`,`lane`,`deviceId`, `isTransmitter`, `transmitterHaPriority`, " +
							   "`modelData`, `disabledReason`, `enabled`, `bioDeviceType`, `hardwareType`, `lastReaderTestTime`, `lastReaderTestSuccess`, `lastReaderTestUser`) " + 
							   "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				reader.sanitizeReaderId();
				
				stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				stmt.clearParameters();
				stmt.setLong(1, id);
				stmt.setInt(2, (reader.getType() == null ? ReaderType.undefined.ordinal() : reader.getType().ordinal()));
				stmt.setString(3, (reader.getReaderId() != null ? reader.getReaderId() : ("reader-" + id)));
				stmt.setInt(4, (reader.getX() != null ? reader.getX() : 0));
				stmt.setInt(5, (reader.getY() != null ? reader.getY() : 0));
				stmt.setString(6, (reader.getGroup() != null ? reader.getGroup() : ""));
				stmt.setString(7, (reader.getSingulationGroup() != null ? reader.getSingulationGroup() : ""));;
				stmt.setInt(8, (reader.getSignalStrengthThreshold() != null ? reader.getSignalStrengthThreshold() : -90));
				stmt.setDouble(9, (reader.getGain() != null ? reader.getGain() : 0));
				if (reader.getMacAddress() != null)
					stmt.setString(10, reader.getMacAddress());
				else
					stmt.setNull(10, Types.VARCHAR);
				
				if (reader.getIpAddress() != null)
					stmt.setString(11, reader.getIpAddress());
				else
					stmt.setNull(11, Types.VARCHAR);
			
				stmt.setInt(12, (reader.getPort() != null ? reader.getPort() : 80));
				stmt.setLong(13, (reader.getLastIdReceived() != null ? reader.getLastIdReceived() : -1L));
				stmt.setLong(14, (reader.getLocationId() != null ? reader.getLocationId() : -1L));
				stmt.setLong(15, reader.getTimeLastHello() != null ? reader.getTimeLastHello().getTime() : System.currentTimeMillis());
				stmt.setInt(16, (reader.getLane() != null ? reader.getLane() : 0));
				stmt.setInt(17, (reader.getDeviceId() != null ? reader.getDeviceId() : 0));
				stmt.setBoolean(18, false);
				stmt.setInt(19, reader.getTransmitterHaPriority());
				stmt.setString(20, reader.getModelData() != null ? reader.getModelData() : "");
				stmt.setString(21, reader.getDisabledReason() != null ? reader.getDisabledReason() : "");
				stmt.setBoolean(22, reader.isEnabled());
				stmt.setInt(23, reader.getBioDeviceType() != null ? reader.getBioDeviceType() : 0);
				if (reader.getHardwareType() != null)
					stmt.setString(24, reader.getHardwareType());
				else
					stmt.setNull(24, Types.VARCHAR);
				
				if (reader.getLastReaderTestTime() == null)
					stmt.setNull(25, Types.INTEGER);
				else
					stmt.setLong(25, reader.getLastReaderTestTime().getTime());
				
				stmt.setBoolean(26, reader.isLastReaderTestSuccess());
				stmt.setString(27, reader.getLastReaderTestUser());
				
				stmt.executeUpdate();
				
				rs = stmt.getGeneratedKeys();
				if (rs.next())
					reader.setId(rs.getLong(1));
				
				if (ReaderType.isLongRangeReader(reader.getType()) && reader.getAntennas() != null)
				{
					List<Boolean> antennaList = reader.getAntennas();
										
					for (int antenna = 0; antenna < antennaList.size(); antenna++)
					{
						Boolean antennaPower = antennaList.get(antenna);
						if (antennaPower != null)
						{
							boolean power = antennaPower.booleanValue();
							insertAntenna(conn, reader.getId(), antenna, power, false);
						}
					}
				}

				
			} else {
				//update location
				String query = "UPDATE Reader SET" +
						" type = ?,readerId = ?,positionX = ?,positionY = ?,`group` = ?,singulationGroup = ?," +
						" signalStrengthThreshold = ?,gain = ?,macAddress = ?,ipAddress = ?,port = ?, lastIdReceived = ?," +
						" locationId = ?,timeLastHello = ?,lane = ?,deviceId = ?,transmitterHaPriority = ?,modelData = ?, " +
						" disabledReason = ?, enabled = ?, lane = ?, bioDeviceType = ?, hardwareType = ?, lastReaderTestTime = ?, " +
						" lastReaderTestSuccess = ?, lastReaderTestUser = ?" + 
						" WHERE id = ?";
				
				reader.sanitizeReaderId();
				
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setInt(1, (reader.getType() == null ? ReaderType.undefined.ordinal() : reader.getType().ordinal()));
				stmt.setString(2, reader.getReaderId());
				stmt.setInt(3, (reader.getX() != null ? reader.getX() : 0));
				stmt.setInt(4, (reader.getY() != null ? reader.getY() : 0));
				stmt.setString(5, (reader.getGroup() != null ? reader.getGroup() : ""));
				stmt.setString(6, (reader.getSingulationGroup() != null ? reader.getSingulationGroup() : ""));
				stmt.setInt(7, reader.getSignalStrengthThreshold() != null ? reader.getSignalStrengthThreshold() : -90);
				stmt.setDouble(8, reader.getGain() != null ? reader.getGain() : 0);
				
				if (reader.getMacAddress() != null)
					stmt.setString(9, reader.getMacAddress());
				else
					stmt.setNull(9, Types.VARCHAR);
				
				if (reader.getIpAddress() != null)
					stmt.setString(10, reader.getIpAddress());
				else
					stmt.setNull(10, Types.VARCHAR);
				
				if (reader.getPort() != null)
					stmt.setInt(11, reader.getPort());
				else
					stmt.setNull(11, Types.INTEGER);
				
				if (reader.getLastIdReceived() != null)
					stmt.setLong(12, reader.getLastIdReceived());
				else
					stmt.setNull(12, Types.BIGINT);
				
				if (reader.getLocationId() != null)
					stmt.setLong(13, reader.getLocationId());
				else
					stmt.setNull(13, Types.INTEGER);
				
				if (reader.getTimeLastHello() != null)
					stmt.setLong(14, reader.getTimeLastHello().getTime());
				else
					stmt.setNull(14, Types.BIGINT);
				
				stmt.setInt(15, (reader.getLane() != null ? reader.getLane() : 0));
				stmt.setInt(16, (reader.getDeviceId() != null ? reader.getDeviceId() : 0));
				stmt.setInt(17, reader.getTransmitterHaPriority());
                stmt.setString(18, reader.getModelData() != null ? reader.getModelData() : "");
                stmt.setString(19, reader.getDisabledReason() != null ? reader.getDisabledReason() : "");
                stmt.setBoolean(20, reader.isEnabled());
                stmt.setInt(21, reader.getLane() != null ? reader.getLane() : 0);
                stmt.setInt(22, reader.getBioDeviceType() != null ? reader.getBioDeviceType() : 0);
                
				if (reader.getHardwareType() != null)
					stmt.setString(23, reader.getHardwareType());
				else
					stmt.setNull(23, Types.VARCHAR);
				
				if (reader.getLastReaderTestTime() == null)
					stmt.setNull(24, Types.INTEGER);
				else
					stmt.setLong(24, reader.getLastReaderTestTime().getTime());
				
				stmt.setBoolean(25, reader.isLastReaderTestSuccess());
				stmt.setString(26, reader.getLastReaderTestUser());
                
                stmt.setLong(27, reader.getId());                

				stmt.executeUpdate();
			
				if (!ReaderType.isLongRangeReader(reader.getType()))
				{
					deleteAntenna(conn, reader.getId(), false);
				}
				
				if (ReaderType.isLongRangeReader(reader.getType()) && reader.getAntennas() != null)
				{
					List<Boolean> antennaList = reader.getAntennas();
										
					for (int antenna = 0; antenna < antennaList.size(); antenna++)
					{
						Boolean antennaPower = antennaList.get(antenna);
						if (antennaPower != null)
						{
							boolean power = antennaPower.booleanValue();
							
							if (!updateAntenna(conn, reader.getId(), antenna, power, false))
								insertAntenna(conn, reader.getId(), antenna, power, false);							
						}
					}
				}
			}
			
			// increment the xbrcConfigModSeq column to indicate that the Reader table has changed
			ReaderConfig readerConfig = ReaderConfig.getInstance();
			Config.getInstance().read(conn, readerConfig);
			readerConfig.incrementXbrcConfigModSeq();
			Config.getInstance().write(conn, readerConfig);
			
			//transaction closes on commit
			conn.commit();
			
		}
		catch (SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException ignore) {}

            String macAddress = reader.getMacAddress();
            if ( macAddress == null )
                macAddress = "UNKNOWN";
            logger.error(ExceptionFormatter.format("Error saving reader: " + macAddress, e));
			
			throw e;
		}
        finally {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

			try
			{
				conn.setAutoCommit(autoCommit);
			}
			catch (SQLException ignore) {}
			
			try
			{
				conn.setTransactionIsolation(transactionIsolation);
			}
			catch (SQLException ignore) {}
        }

		return;
	}
	
	/*
	 * Only save the isTransmitter and transmitterHaPriority
	 */
	public static void saveTransmitInfo(Connection conn, Reader reader) throws Exception 
	{
		if (conn == null)
			return;
		
		if (reader == null)
			throw new IllegalArgumentException();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			//update location
			String query = "UPDATE Reader SET" +
					" isTransmitter = ?, transmitterHaPriority = ?" +
					" WHERE id = ?";
			
			reader.sanitizeReaderId();
			
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setBoolean(1, reader.isTransmitter());
            stmt.setInt(2, reader.getTransmitterHaPriority());
            stmt.setLong(3, reader.getId());

			stmt.executeUpdate();
		}
		catch (SQLException e)
		{
			throw e;
		}
        finally {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception ignore) {}
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
        }
	}
	
	public static HashMap<Location,Collection<Reader>> getByLocation(Connection conn) 
	{		
		if (conn == null)
			return null;
		
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
				String insertUnknownQuery = "insert into Location (id,locationTypeId,name,locationId,x,y,singulationTypeId,eventGenerationTypeId) values(?,?,?,?,?,?,?,?)";
				newLocationStmt = conn.prepareStatement(insertUnknownQuery);
				newLocationStmt.clearParameters();
				newLocationStmt.setInt(1, 0);
				newLocationStmt.setInt(2, uLocTypeId);
				newLocationStmt.setString(3, "UNKNOWN");
				newLocationStmt.setString(4, "");
				newLocationStmt.setInt(5, 0);
				newLocationStmt.setInt(6, 0);
				newLocationStmt.setInt(7, 0);
				newLocationStmt.setInt(8, 0);
				newLocationStmt.execute();
			}
			
			query = "SELECT l.id as locationAutoId, l.name as locationName, l.locationTypeId, l.singulationTypeId, l.eventGenerationTypeId, r.*" +
							" FROM Location l LEFT JOIN Reader r" +
							" ON l.id = r.locationId" +
							" ORDER BY l.name, l.id, r.readerId";
			stmt = conn.prepareStatement(query);			
			stmt.execute();
			rs = stmt.getResultSet();
			HashMap<Location,Collection<Reader>> results = new LinkedHashMap<Location,Collection<Reader>>();
			
			Location location = null;
			Collection<Reader> readers = null;
			
			Location unknown = null;
			while(rs.next()) {
				//location
				Long locationAutoId = rs.getLong("locationAutoId");
				if (location == null || !location.getId().equals(locationAutoId)) {		
					location = new Location();
					location.setId(locationAutoId);
					location.setName(rs.getString("locationName"));
					location.setLocationTypeId(rs.getInt("locationTypeId"));
					location.setSingulationTypeId(rs.getInt("singulationTypeId"));
					location.setEventGenerationTypeId(rs.getInt("eventGenerationTypeId"));
					readers = new LinkedList<Reader>();
					results.put(location,readers);
					
					// save a pointer to the unknown location for later
					if (location.getName().equals("UNKNOWN"))
						unknown = location;
				}
				
				//location's readers
				Reader reader = instantiateReader(rs);
				if (reader == null){
					// location with no readers
					continue;
				}
				
				reader.setLocation(location);

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
			Collection<Reader> unknownLocReaders = results.get(unknown);
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
        }
	}
	
	public static Collection<Reader> findUnlinked(Connection conn) throws Exception 
	{
		if (conn == null)
			return null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			String query = "SELECT r.* FROM Reader AS r, Location AS l, LocationType AS lt WHERE r.locationId = l.id AND l.locationTypeId = lt.id AND LocationTypeName = 'UNKNOWN' ORDER BY r.readerId";
			stmt = conn.prepareStatement(query);			
			stmt.execute();
			rs = stmt.getResultSet();

			Collection<Reader> readers = new LinkedList<Reader>();
			
			while(rs.next()) {
				Reader reader = instantiateReader(rs);
				instantiateReaderAntennas(conn, reader);
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
        }
	} 

	public static void deleteAntenna(Connection conn, Long readerId, boolean commitTransaction) throws SQLException
	{
		PreparedStatement stmt = null;
		int transactionIsolation = 0;
		boolean autoCommit = true;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			
			String query = "DELETE from ReaderAntenna WHERE readerId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, readerId);
			stmt.executeUpdate();
			
			if (commitTransaction)
				conn.commit();
		}
		catch (SQLException e)
		{
        	try
        	{
                if (commitTransaction)
                	conn.rollback();
        	}
        	catch (SQLException ignore) { }
        	
            throw e;
        }		
		finally
		{
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {
                }
            }
            
            try
            {
                conn.setAutoCommit(autoCommit);
            }
            catch (SQLException ignore){}

            try
            {
                conn.setTransactionIsolation(transactionIsolation);
            }
            catch (SQLException ignore){}
		}
	}
	
	public static void delete(Connection conn, Long readerId, boolean commitTransaction, Collection<String> expectedXbrcAddress) throws Exception 
	{
		if (conn == null)
			throw new IllegalArgumentException("Connection object not provided.");
		
		if (readerId == null)
			throw new IllegalArgumentException("Reader not provided.");
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = true;
		try
		{
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			stmt = conn.prepareStatement("SELECT * FROM Reader WHERE id = ?");
			stmt.clearParameters();
			stmt.setLong(1, readerId);
			stmt.execute();
			rs = stmt.getResultSet();
			
			if (rs.next())
			{
				Reader reader = new Reader();
				reader.setId(rs.getLong("id"));
				reader.setIpAddress(rs.getString("ipAddress"));
				reader.setPort(rs.getInt("port"));
				reader.setType(ReaderType.getByOrdinal(rs.getInt("type")));
				
				scrubReader(conn, reader, true, expectedXbrcAddress);
				
				//remove this reader
				String query = "DELETE from Reader WHERE id = ?";
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setLong(1, readerId);
				stmt.executeUpdate();
			}
			
			if (commitTransaction)
				conn.commit();
		}
		catch (SQLException e)
		{
        	try
        	{
                if (commitTransaction)
                	conn.rollback();
        	}
        	catch (SQLException ignore) { }
        	
            throw e;
        }
        finally {
        	if (rs != null){
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
            
            try
            {
                conn.setAutoCommit(autoCommit);
            }
            catch (SQLException e){}

            try
            {
                conn.setTransactionIsolation(transactionIsolation);
            }
            catch (SQLException e){}
        }

		return;
	}
	
	protected static long getNextAvailableId(Connection conn) throws SQLException {
		String query = "SELECT max(id) as maxId FROM Reader";
		PreparedStatement s = null;
        ResultSet rs = null;
        long nextKey = -1;

        try {
            s = conn.prepareStatement(query);
		    s.execute();
		    rs = s.getResultSet();

	    	if (rs.next())
		    	nextKey = rs.getLong("maxId");
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
        }

		return ++nextKey;
	}
	
	/**
	 * Removes the association between the reader and its location. DOES NOT delete the reader
	 * from the Reader table, but deletes all band comands associated with this reader.
	 * 
	 * @param readerId
	 * @throws IllegalArgumentException in case of a null id
	 * @throws SQLException
	 */
	public static void unlinkReader(Connection conn, Reader reader, Collection<String> expectedXbrcUrl) throws Exception 
	{
		if (conn == null)
			throw new IllegalArgumentException("Connecion object must be provided.");
		
		if (reader == null)
			throw new IllegalArgumentException("You must provide a reader id in order to remove a reader from location");
		
		PreparedStatement stmt = null;
		
		try
		{
			//remove a reader from location
			String query = "UPDATE Reader SET locationId = (select id from Location where name = 'UNKNOWN') WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.setLong(1, reader.getId());
			stmt.executeUpdate();
		} 
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }
        }

		scrubReader(conn, reader, false, expectedXbrcUrl);

		return;
	}
	
	public static boolean isNameTaken(Connection conn, String readerName) throws Exception
	{
		if (conn == null)
			throw new IllegalArgumentException("Connection object must be provided.");
		
		if (readerName == null)
			throw new IllegalArgumentException("No reader name provided to check.");
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			//remove a reader from location
			String query = "SELECT readerId from Reader WHERE readerId = ?";
			stmt = conn.prepareStatement(query);
			stmt.setString(1, Reader.sanitizeReaderId(readerName));
			stmt.execute();
			
			rs = stmt.getResultSet();
			if (rs.next())
				return true;
			else
				return false;
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
        }
	}
	
	private static Reader instantiateReader(ResultSet rs) throws SQLException
	{	
		Reader reader = new Reader();
		
		//instantiate simple types
		reader.setId(rs.getLong("id"));
		if (rs.wasNull())
			// location with no readers
			return null;
		
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
		
		long timeLastHello = rs.getLong("timeLastHello");
		Date timeLastHelloDate = null;
		if (!rs.wasNull()){
			timeLastHelloDate = new Date(timeLastHello);
		}
		reader.setTimeLastHello(timeLastHelloDate);
		
		if (rs.wasNull())
			reader.setTimeLastHello(null);
		reader.setLane(rs.getInt("lane"));
		if (rs.wasNull())
			reader.setLane(null);
		reader.setDeviceId(rs.getInt("deviceId"));
		if (rs.wasNull())
			reader.setDeviceId(null);
		reader.setVersion(rs.getString("version"));
		if (rs.wasNull())
			reader.setVersion(null);
		reader.setMinXbrcVersion(rs.getString("minXbrcVersion"));
		if (rs.wasNull())
			reader.setMinXbrcVersion(null);
		reader.setTransmitter(rs.getBoolean("isTransmitter"));
		reader.setTransmitPayload(rs.getString("transmitPayload"));
		if (rs.wasNull())
			reader.setTransmitPayload(null);
		reader.setTransmitterHaPriority(rs.getInt("transmitterHaPriority"));
		reader.setModelData(rs.getString("modelData"));
		if (rs.wasNull())
			reader.setModelData(null);
		reader.setDisabledReason(rs.getString("disabledReason"));
		if (rs.wasNull())
			reader.setDisabledReason(null);
		reader.setEnabled(rs.getBoolean("enabled"));
		reader.setBioDeviceType(rs.getInt("bioDeviceType"));
		if (rs.wasNull())
			reader.setBioDeviceType(null);
		reader.setHardwareType(rs.getString("hardwareType"));
		if (rs.wasNull())
			reader.setHardwareType(null);
		
		long lastReaderTestTime = rs.getLong("lastReaderTestTime");
		Date dtLastReaderTestTime = null;
		if (!rs.wasNull())
			dtLastReaderTestTime = new Date(lastReaderTestTime);
		reader.setLastReaderTestTime(dtLastReaderTestTime);
		
		reader.setLastReaderTestSuccess(rs.getBoolean("lastReaderTestSuccess"));
		reader.setLastReaderTestUser(rs.getString("lastReaderTestUser"));
		
		return reader;
	}
	
	/*
	public static void instantiateTransitCommands(Reader reader, Connection conn) throws Exception
	{
		if (!reader.isTransmitter() || reader.getId() == null)
			return;
		
		reader.setBandCommands(XbandCommandService.findByReader(conn, reader.getId()));
		
	}
	*/

	public static void instantiateReaderAntennas(Connection conn, Reader reader) throws Exception
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (ReaderType.isLongRangeReader(reader.getType()))
		{
			try
			{
				String query = "SELECT * FROM ReaderAntenna WHERE readerId = ?";
				stmt = conn.prepareStatement(query);
				stmt.clearParameters();
				stmt.setLong(1, reader.getId());
				stmt.execute();
				
				rs = stmt.getResultSet();
				
				HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
				Integer max = null;
				while (rs.next() == true)
				{
					Integer antenna = rs.getInt("antenna");
					Boolean power = rs.getBoolean("power");
					if (rs.wasNull())
						power = null;
					
					if (max == null)
						max = antenna;
					
					if (max < antenna)
						max = antenna;
					
					map.put(antenna,  power);
				}
				
				if (max != null)
				{
					List<Boolean> antennaList = new ArrayList<Boolean>(max + 1);
				
					for (Integer i = 0; i < max + 1; i++)
					{
						Boolean power = map.get(i);
						antennaList.add(i, power);
					}
					reader.setAntennas(antennaList);
				}
			}
			finally
			{
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
			}
		}
	}

	public static void replaceReader(Connection conn, Long readerBeingReplacedId, Long replacementReaderId, Collection<String> expectedXbrcUrl)
			throws Exception
	{	
		if (conn == null)
			return;
		
		Reader oldReader = find(conn, readerBeingReplacedId);
		if (oldReader == null)
		{
			logger.error("Failed to find a reader with id " + readerBeingReplacedId + " in the database.");
			throw new IllegalArgumentException("Failed to find a reader with id " + readerBeingReplacedId + " in the database.");
		}
		
		// select the new reader from the DB.
		Reader newReader = find(conn, replacementReaderId);
		if (newReader == null)
		{
			logger.error("Failed to find a reader with id " + replacementReaderId + " in the database.");
			throw new IllegalArgumentException("Failed to find a reader with id " + replacementReaderId + " in the database.");
		}

        ReaderService.replaceReader(conn, oldReader, newReader, true, expectedXbrcUrl);
	}
	
    public static void
    replaceReader(final Connection conn, final ReaderInfo oldReader, final ReaderInfo newReader, Collection<String> expectedXbrcUrl)
    throws Exception
    {
        ReaderService.replaceReader(conn, ReaderService.createReaderFromInfo(oldReader), ReaderService.createReaderFromInfo(newReader), true, expectedXbrcUrl);
    }

    public static Reader createReaderFromInfo(final ReaderInfo ri) {
        final Reader reader = new Reader();

        reader.setReaderId(ri.getName());
        reader.setHardwareType(ri.getHardwareType());
        reader.setType(ri.getType());
        reader.setId(new Long(ri.getId()));
        reader.setIpAddress(ri.getIpAddress());
        reader.setPort(ri.getPort());
        reader.setMacAddress(ri.getMacAddress());
        reader.setTransmitter(ri.isTransmitter());
        reader.setSignalStrengthThreshold(ri.getSignalStrengthThreshold());
        reader.setTransmitterHaPriority(ri.getTransmitterHaPriority());
        reader.setLastReaderTestTime(ri.getLastReaderTestTime());
        reader.setLastReaderTestSuccess(ri.isLastReaderTestSuccess());
        reader.setLastReaderTestUser(ri.getLastReaderTestUser());
        reader.setLane(ri.getLane());
        reader.setDeviceId(ri.getDeviceId());
        reader.setDisabledReason(ri.getDisabledReason());
        reader.setModelData(ri.getModelData());
        
        LocationInfo location = ri.getLocation();
        if (location != null && location.getId() >= 0)
        	reader.setLocationId(new Long(location.getId()));
        else
        	reader.setLocationId(0L);	// unknown location

        return reader;
    }

    public static void
    replaceReader(final Connection conn, final Reader oldReader, final Reader newReader, boolean commitTransaction, Collection<String> expectedXbrcUrl)
    throws Exception
    {
        if (conn == null)
            return;

        PreparedStatement stmt = null;
        int transactionIsolation = conn.getTransactionIsolation();
        boolean autoCommit = conn.getAutoCommit();

        try
        {
            //open transaction
            transactionIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            //After replacement, it is important that the reader status reflects the new reader's status instead of the one we'd just replaced.
            final long newReaderTimeLastHello = newReader.getTimeLastHello() != null? newReader.getTimeLastHello().getTime() : (new Date()).getTime();

            if (ReaderType.isTapReader(oldReader.getType()) && !ReaderType.isTapReader(newReader.getType()))
            {
                // replacing a tap reader with a long range
            	
            	// Antennas will be okay as they are associated with the oldReader's id which is kept.

                // remove omni associations
            	
    			scrubReader(conn, oldReader, true, expectedXbrcUrl);

                String query = "UPDATE Reader SET" +
                        " type = ?, macAddress = ?, ipAddress = ?, port = ?, isTransmitter = ?, signalStrengthThreshold = ?, transmitterHaPriority = ?, timeLastHello = ?" +
                        " WHERE id = ?";

                stmt = conn.prepareStatement(query);
                stmt.clearParameters();
                stmt.setInt(1, (newReader.getType() == null ? ReaderType.undefined.ordinal() : newReader.getType().ordinal()));
                stmt.setString(2, newReader.getMacAddress());
                stmt.setString(3, newReader.getIpAddress());
                stmt.setInt(4, newReader.getPort());
                stmt.setBoolean(5, newReader.isTransmitter());
                stmt.setInt(6, newReader.getSignalStrengthThreshold());
                stmt.setInt(7, newReader.getTransmitterHaPriority());
                stmt.setLong(8, newReaderTimeLastHello);
                stmt.setLong(9, oldReader.getId());
            }
            else if (!ReaderType.isTapReader(oldReader.getType()) && ReaderType.isTapReader(newReader.getType()))
            {
                // replacing a long range reader with a tap reader

            	// remove xBR antennas, xBand transmit commands
            	scrubReader(conn, oldReader, true, expectedXbrcUrl);

                String query = "UPDATE Reader SET" +
                        " type = ?, macAddress = ?, ipAddress = ?, port = ?, isTransmitter = ?, signalStrengthThreshold = ?, transmitterHaPriority = ?, timeLastHello = ? " +
                        " WHERE id = ?";

                stmt = conn.prepareStatement(query);
                stmt.clearParameters();
                stmt.setInt(1, (newReader.getType() == null ? ReaderType.undefined.ordinal() : newReader.getType().ordinal()));
                stmt.setString(2, newReader.getMacAddress());
                stmt.setString(3, newReader.getIpAddress());
                stmt.setInt(4, newReader.getPort());
                stmt.setBoolean(5, newReader.isTransmitter());
                stmt.setInt(6, newReader.getSignalStrengthThreshold());
                stmt.setInt(7, newReader.getTransmitterHaPriority());
                stmt.setLong(8, newReaderTimeLastHello);
                stmt.setLong(9, oldReader.getId());
            }
            else
            {
                String query = "UPDATE Reader SET" +
                        " type = ?, macAddress = ?, ipAddress = ?, port = ?, timeLastHello = ?" +
                        " WHERE id = ?";

                stmt = conn.prepareStatement(query);
                stmt.clearParameters();
                stmt.setInt(1, (newReader.getType() == null ? ReaderType.undefined.ordinal() : newReader.getType().ordinal()));
                stmt.setString(2, newReader.getMacAddress());
                stmt.setString(3, newReader.getIpAddress());
                stmt.setInt(4, newReader.getPort());
                stmt.setLong(5, newReaderTimeLastHello);
                stmt.setLong(6, oldReader.getId());
            }

            stmt.executeUpdate();

            // increment the xbrcConfigModSeq column to indicate that the Reader table has changed
            ReaderConfig readerConfig = ReaderConfig.getInstance();
            Config.getInstance().read(conn, readerConfig);
            readerConfig.incrementXbrcConfigModSeq();
            Config.getInstance().write(conn, readerConfig);

            // now delete the new reader
            // NOTE: don't call the delete() method on this reader, because that method also does the scrubReader(true)
            scrubReader(conn, newReader, false, expectedXbrcUrl);
            
    		// remove the reader from xBRC
            String query = "DELETE from Reader WHERE id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, newReader.getId());
			stmt.executeUpdate();
            
            //transaction closes on commit
            if (commitTransaction)
            	conn.commit();

            if (ReaderType.isLongRangeReader(oldReader.getType()))
            {
                ReaderInfo readerInfo = new ReaderInfo();
                readerInfo.setIpAddress(oldReader.getIpAddress());
                readerInfo.setPort(oldReader.getPort());
                readerInfo.setMacAddress(oldReader.getMacAddress());
                readerInfo.setType(oldReader.getType());

                ReaderApi.sendDeleteBandCommands(readerInfo);
            }
        }
        catch (SQLException e)
        {
        	try
        	{
                if (commitTransaction)
                	conn.rollback();
        	}
        	catch (SQLException ignore) { }
        	
            throw e;
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception ignore) {}
            }

            try
            {
                conn.setAutoCommit(autoCommit);
            }
            catch (SQLException e){}

            try
            {
                conn.setTransactionIsolation(transactionIsolation);
            }
            catch (SQLException e){}
        }
    }

	/**
	 * Remove type specific associations:
	 * - band commands for long range readers
	 * - omni server associations for tap readers
	 * 
	 * @param conn
	 * @param reader
     * @param sendDelete
	 */
	protected static void scrubReader(Connection conn, Reader reader, boolean sendDelete, Collection<String> expectedXbrcAddress) throws Exception
	{
		if (reader == null)
			return;
		
		// remove xBand transmit commands
		XbandCommandService.deleteCommands(conn, reader.getId(), false);
		
		// Remove entry from ReaderAntenna
		deleteAntenna(conn, (long)reader.getId(), false);
				
		/* 
		 * Attempt to remove xbrc info from the reader.
		 * This will only reach readers we can communicate with and will
		 * have no effect if the xbrc url is configured either manually or 
		 * through DHCP.
		 */
		ReaderInfo ri = new ReaderInfo();
		ri.setId(reader.getId().intValue());
		ri.setIpAddress(reader.getIpAddress());
		ri.setPort(reader.getPort());
		boolean deleteOnReader = true;
		
		// Check to see if the reader is still pointed to the expected xbrc url.
		// If it is, then we can safely delete the reader settings on the reader.
		if (expectedXbrcAddress != null)
		{ 
			try
			{
				StringBuffer url = new StringBuffer(); 
				int retCode = ReaderApi.getXbrcUrl(ri, url);
				if (retCode != 200)
				{
					logger.warn("Received " + retCode + " return code from reader at IP " + ri.getIpAddress() + " calling /url");
					deleteOnReader = false;
				}
				else
				{
					boolean matched = false;
					for (String expected : expectedXbrcAddress)
					{
						if (url.toString().toLowerCase().contains(expected.toLowerCase()))
						{
							matched = true;
							break;
						}
					}
					
					if (!matched)
					{
						logger.warn("Reader at IP " + ri.getIpAddress() + " is pointed to xbrc: " + url + " instead of expected: " + 
								expectedXbrcAddress);
						deleteOnReader = false;
					}
				}
			}
			catch(Exception e)
			{
				logger.warn("Cannot contact reader at IP " + ri.getIpAddress(), e);
				deleteOnReader = false;
			}
		}
		
		if (deleteOnReader)
		{
			boolean success = ReaderApi.removeXbrcInfo(ri, sendDelete);
			if (!success)
				logger.warn("Failed to clear xbrc information from reader " + reader.getMacAddress());
		}
		else 
		{
			logger.info("The xBRC information on the reader with IP " + ri.getIpAddress() + " will not deleted.");
		}
		
		// remove omni associations
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "ReaderOmniServer", null);
		if (tables.next()) {
			// table exists, so it is park entry
			OmniServerService.disassociateAllOmniServers(conn, (long)reader.getId());
		}
	}
}
