package com.disney.xband.xbrc.Controller.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.Controller.ConfigOptions;
import com.disney.xband.xbrc.lib.db.XbandCommandService;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.model.BandMediaType;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class ReadersInfo
{
	public static Logger logger = Logger.getLogger(ReadersInfo.class);

	private Map<String, ReaderInfo> readers;
	private LinkedHashMap<String, List<ReaderInfo>> readersByLocation;
	private Map<Integer, LocationInfo> locationInfo;
	private Map<Integer,ReaderInfo> readersByDeviceId;
	// The following map allows us to keep track of what band types can be read
	// at each location type.
	private Map<Integer,HashSet<BandMediaType>> locationTypeReaders;

	public ReadersInfo()
	{
		readers = new Hashtable<String, ReaderInfo>();
		readersByLocation = new LinkedHashMap<String, List<ReaderInfo>>();
		locationInfo = new Hashtable<Integer, LocationInfo>();
		readersByDeviceId = new Hashtable<Integer,ReaderInfo>();
		locationTypeReaders = new Hashtable<Integer,HashSet<BandMediaType>>();
	}

	public void clear()
	{
		synchronized (this)
		{
			readers.clear();
			readersByLocation.clear();
			readersByDeviceId.clear();
			locationTypeReaders.clear();
		}
	}

	public void AddReader(ReaderInfo ri)
	{
		readers.put(ri.getName(), ri);
		List<ReaderInfo> list = readersByLocation.get(ri.getLocation()
				.getName());
		if (list == null)
		{
			list = new LinkedList<ReaderInfo>();
			readersByLocation.put(ri.getLocation().getName(), list);
		}
		list.add(ri);
		
		if (ri.getLocation() != null)
		{
			if (ri.getLocation().getReaders() == null)
				ri.getLocation().setReaders(new LinkedList<ReaderInfo>());
			if (!ri.getLocation().getReaders().contains(ri))
				ri.getLocation().getReaders().add(ri);
			
			// Only TAP readers need to have the device id set to a unique number. 
			// This is for GXP lookup and for park entry.
			if (ReaderType.isTapReader(ri.getType()) && ri.isEnabled() && ri.getLocation().getLocationTypeID() != 0)
			{
				if (readersByDeviceId.containsKey(ri.getDeviceId()))
					logger.warn("Detected duplicate deviceid " + ri.getDeviceId() + " for a reader " + ri.getName());
				readersByDeviceId.put(ri.getDeviceId(), ri);
			}
		}
		
		addToLocationTypeReaders(ri);
	}
	
	private void addToLocationTypeReaders(ReaderInfo ri)
	{
		// ignore readers at UNKNOWN location type
		if (ri.getLocation().getLocationTypeID() == 0)
			return;
		
		HashSet<BandMediaType> bmtypes = locationTypeReaders.get(ri.getLocation().getLocationTypeID());
		if (bmtypes == null)
		{
			bmtypes = new HashSet<BandMediaType>();
			locationTypeReaders.put(ri.getLocation().getLocationTypeID(), bmtypes);
		}
		
		if (ReaderType.isTapReader(ri.getType()))
		{
			bmtypes.add(BandMediaType.Band);
			bmtypes.add(BandMediaType.Card);
		}
		else if (ReaderType.isLongRangeReader(ri.getType()))
			bmtypes.add(BandMediaType.Band);
	}

	public int getLength()
	{
		return readers.size();
	}

	public Collection<ReaderInfo> getReaders()
	{
		return readers.values();
	}
	
	public List<ReaderInfo> getReadersClone()
	{
		List<ReaderInfo> copy = new ArrayList<ReaderInfo>(readers.size());
		synchronized(this)
		{
			for(ReaderInfo ri : readers.values())
				copy.add(ri);
			
		}
		return copy;
	}

	public ReaderInfo getReader(String sReaderName)
	{
		synchronized (this)
		{
			return readers.get(sReaderName);
		}
	}

	public List<ReaderInfo> getReaders(String locationName)
	{
		List<ReaderInfo> list = readersByLocation.get(locationName);
		if (list == null)
			return new LinkedList<ReaderInfo>();
		return list;
	}

	public Map<Integer, LocationInfo> getLocationInfo()
	{
		return locationInfo;
	}

	public int processReadersTable(Connection conn)
	{
		Statement stmt = null;
		ResultSet rs = null;
		
		ResultSet cmdrs = null;
		
		PreparedStatement lookupLocation = null;

		clear();

		try
		{
			String sSQL = "SELECT * FROM Reader";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			while (rs.next())
			{
				int id = rs.getInt("id");
				String sReaderName = rs.getString("readerId");
				String sGroup = rs.getString("group");
				String sSingulationGroup = rs.getString("singulationGroup");
				int nPort = rs.getInt("port");
				int nSignalStrengthThreshold = rs
						.getInt("signalStrengthThreshold");
				double dGain = rs.getDouble("gain");
				String sMACAddress = rs.getString("macAddress");
				String sIpAddress = rs.getString("ipAddress");
				long lLastIDReceived = rs.getLong("lastIdReceived");
				int iLocation = rs.getInt("locationId");
				long lTimeLastHello = rs.getLong("timeLastHello");
				int lane = rs.getInt("lane");
				ReaderType type = ReaderType.getByOrdinal(rs.getInt("type"));
				int deviceId = rs.getInt("deviceId");
				int x = rs.getInt("positionX");
				int y = rs.getInt("positionY");
				String sVersion = rs.getString("version");
				String sMinXbrcVersion = rs.getString("minXbrcVersion");
				boolean bIsTransmitter = rs.getBoolean("isTransmitter");
				String transmitPayload = rs.getString("transmitPayload");
				String modelData = rs.getString("modelData");
				String disabledReason = rs.getString("disabledReason");
				boolean enabled = rs.getBoolean("enabled");
				int bioDeviceType = rs.getInt("bioDeviceType");
				String hardwareType = rs.getString("hardwareType");
				int transmitterHaPriority = rs.getInt("transmitterHaPriority");
				
				long lastReaderTestTime = rs.getLong("lastReaderTestTime");
				Date dtLastReaderTestTime = null;
				if (!rs.wasNull())
					dtLastReaderTestTime = new Date(lastReaderTestTime);								
				boolean lastReaderTestSuccess = rs.getBoolean("lastReaderTestSuccess");
				String lastReaderTestUser = rs.getString("lastReaderTestUser");

				// validate locationID
				LocationInfo locInfo = locationInfo.get(iLocation);
				if (locInfo == null)
				{
					logger.error("!! Error: reader's location ID is not valid: "
							+ iLocation);
					return 3;
				}
				
				if (enabled == false)
					logger.warn("Reader " + sReaderName + " at IP " + sIpAddress + " is marked as disabled. All reader events will be ignored.");
				
				/*
				 * Global configuration setting is used to determine if a secure id
				 * should be used, unless overwritten by a location's setting.
				 */
				boolean useSecureId = ConfigOptions.INSTANCE.getControllerInfo().isSecureTapId();
				if (locInfo.isUseSecureId() != null)
					useSecureId = locInfo.isUseSecureId();
 
				ReaderInfo r = new ReaderInfo(id, sReaderName, type, sGroup,
						sSingulationGroup, nPort, nSignalStrengthThreshold,
						dGain, sMACAddress, sIpAddress, lLastIDReceived,
						lTimeLastHello, false, lane, deviceId, x, y, sVersion,
						sMinXbrcVersion, bIsTransmitter, transmitPayload, useSecureId, hardwareType,
						disabledReason, enabled,bioDeviceType, transmitterHaPriority,
						dtLastReaderTestTime, lastReaderTestSuccess, lastReaderTestUser);

				// set the location
				r.setLocation(locInfo);
				r.setModelData(modelData);

				if (type == ReaderType.lrr)
				{
					processReaderAntennas(conn, r);
				}

				// add it to the in-memory list
				AddReader(r);
			}
		}
		catch (Exception e)
		{
			logger.error("!! Error reading reader configuration table from database");
			return 2;
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				
				if (lookupLocation != null)
					lookupLocation.close();
				if (cmdrs != null)
					cmdrs.close();
			}
			catch (Exception e)
			{
			}
		}

		return 0;
	}

	public static void processReaderAntennas(Connection conn, ReaderInfo reader) throws Exception
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (reader.getType() == ReaderType.lrr)
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
					ArrayList<Boolean> antennaList = new ArrayList<Boolean>(max + 1);
				
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
	public int processLocationTable(Connection conn)
	{
		Statement stmt = null;
		ResultSet rs = null;

		locationInfo.clear();

		try
		{
			String sSQL = "SELECT l.*, t.locationTypeName from Location l, LocationType t where l.locationTypeId = t.id";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			while (rs.next())
			{
				int id = rs.getInt("id");
				String sName = rs.getString("name");
				String sLocationId = rs.getString("locationId");
				int nLocationTypeID = rs.getInt("locationTypeId");
				String locationTypeName = rs.getString("locationTypeName");
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				int nSingulationTypeID = rs.getInt("singulationTypeId");
				int nEventGenerationTypeID = rs.getInt("eventGenerationTypeId");
				Boolean useSecureId = rs.getBoolean("useSecureId");
				if (rs.wasNull())
					useSecureId = null;
                String sSuccessSequence = rs.getString("successSeq");
                Long iSuccessTimeout = rs.getLong("successTimeout");
                String sFailureSequence = rs.getString("failureSeq");
                Long iFailureTimeout = rs.getLong("failureTimeout");
                String sErrorSequence = rs.getString("errorSeq");
                Long iErrorTimeout = rs.getLong("errorTimeout");
                String sTapSequence = rs.getString("tapSeq");
                Long iTapTimeout = rs.getLong("tapTimeout");
                String sIdleSequence = rs.getString("idleSeq");
                String sTransmitZoneGroup = rs.getString("transmitZoneGroup");
                Boolean sendBandStatus = rs.getBoolean("sendBandStatus");

                LocationInfo li = new LocationInfo();
				li.setId(id);
				li.setName(sName);
				li.setLocationId(sLocationId);
				li.setLocationTypeID(nLocationTypeID);
				li.setLocationTypeName(locationTypeName);
				li.setX(x);
				li.setY(y);
				li.setSingulationTypeID(nSingulationTypeID);
				li.setEventGenerationTypeID(nEventGenerationTypeID);
				li.setUseSecureId(useSecureId);
                li.setSuccessSequence(sSuccessSequence);
                li.setSuccessTimeout(iSuccessTimeout);
                li.setFailureSequence(sFailureSequence);
                li.setFailureTimeout(iFailureTimeout);
                li.setErrorSequence(sErrorSequence);
                li.setErrorTimeout(iErrorTimeout);
                li.setTapSequence(sTapSequence);
                li.setTapTimeout(iTapTimeout);
                li.setIdleSequence(sIdleSequence);
                li.setTransmitZoneGroup(sTransmitZoneGroup);
                li.setSendBandStatus(sendBandStatus);
                locationInfo.put(id, li);
			}
		}
		catch (SQLException e)
		{
			logger.error("!! Error reading Location table from database", e);
			return 2;
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
		
		for (LocationInfo li : locationInfo.values() )
		{
			try {
				li.setTransmitCommands(XbandCommandService.findByLocation(conn, li.getId()));
			} catch (Exception e) {
				logger.error("!! Error reading transmit commands from database", e);
				return 2;
			}
		}

		return 0;
	}

	public Map<Integer, ReaderInfo> getReadersByDeviceId()
	{
		return readersByDeviceId;
	}

	public void setReadersByDeviceId(Map<Integer, ReaderInfo> readersByDeviceId)
	{
		this.readersByDeviceId = readersByDeviceId;
	}
	
	public boolean canBandBeReadAtLocationType(Integer locationTypeId, BandMediaType bmt)
	{
		HashSet<BandMediaType> bmtypes = locationTypeReaders.get(locationTypeId);
		return bmtypes != null && bmtypes.contains(bmt); 
	}
}
