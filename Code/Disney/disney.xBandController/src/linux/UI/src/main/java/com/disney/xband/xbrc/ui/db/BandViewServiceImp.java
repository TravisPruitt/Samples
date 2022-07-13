package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.common.lib.Config;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.ui.bean.LocationBean;
import com.disney.xband.xbrc.ui.bean.ReaderChannel;
import com.disney.xband.xbrc.ui.bean.ReaderValue;
import com.disney.xband.xbrc.ui.httpclient.Controller;

public class BandViewServiceImp implements BandViewService {
	
	private Logger logger = Logger.getLogger(BandViewServiceImp.class);

	@Override
	public Collection<LocationBean> getByBandId(String id, long deltaTime) throws Exception {
		Connection conn = null;
		
		PreparedStatement debugStmt = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return null;

			//get latest events for this band from the Controller
			Collection<LrrEvent> events = Controller.getInstance().getEventsByGuestId(id);
			if (events == null || events.size() == 0)
				return null;
			
			//events less than deltaTime milliseconds old organized by reader id for O(n) lookups
			Map<String, List<LrrEvent>> eventsMap = new HashMap<String, List<LrrEvent>>();
			
			//get reader and location info for the readers we have events for
			StringBuffer query = new StringBuffer("select l.id as locationId, r.readerId as readerName, r.gain, " +
					"r.signalStrengthThreshold as threshold " +
					"from Reader r, Location l " +
					"where l.locationTypeId <> 0 and l.id = r.locationId and r.readerId in (");
			
			// construct a list of reader ID's
			List<LrrEvent> evList = null;
			
			long now = System.currentTimeMillis();
			
			for (LrrEvent e : events){
				
				long old =  now - e.getTime().getTime();
				
				if (old <= deltaTime){
					
					evList = eventsMap.get(e.getReaderName());
					
					if (evList == null) {
						// reader list for the query
						query.append("?").append(",");

						// new reader, so add it
						evList = new LinkedList<LrrEvent>();
						eventsMap.put(e.getReaderName(), evList);
					}
					
					evList.add(e);
				}
				else {
					logger.debug("Ignoring event " + old + " milliseconds old");
				}
			}
			
			// no events less than deltaTime milliseconds old found
			if (eventsMap.size() == 0)
				return null;
			
			// remove the last comma
			query.setCharAt(query.length() - 1, ' ');

			query.append(") order by l.locationTypeId, r.readerId");

			stmt = conn.prepareStatement(query.toString());			
			stmt.clearParameters();
			int index = 1;
			for (String readerName : eventsMap.keySet()){
				stmt.setString(index++, readerName);
			}
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			
			Map<Long, LocationBean> locationBeans = new LinkedHashMap<Long, LocationBean>();
			
			while (rs.next())
				instantiateLocationBean(rs, locationBeans, eventsMap);
			
			rs.close();
			
			// calculate location's signal strength
			for (LocationBean bean : locationBeans.values())
				bean.updateAvgSignalStrength();
			
			return locationBeans.values();
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
			if (debugStmt != null) debugStmt.close();
			if (stmt != null) stmt.close();
			
			Data.ReleaseConnection(conn);
		}
	}
	
	public void updateGain(String readerId, Double gain) throws Exception {
		if (readerId == null || gain == null)
			return; 
		
		Connection conn = null;		
		PreparedStatement updateReaderStmt = null;
		int transactionIsolation = 0;
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return;
			
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);

			//update gain value
			String query = "UPDATE Reader SET gain = ? WHERE readerId = ?";
			updateReaderStmt = conn.prepareStatement(query);
			updateReaderStmt.clearParameters();
			updateReaderStmt.setDouble(1, gain);
			updateReaderStmt.setString(2, readerId);
			updateReaderStmt.executeUpdate();
			
			//increment the xbrcConfigModSeq column to indicate that the Reader table has changed
			ReaderConfig readerConfig = ReaderConfig.getInstance();
			Config.getInstance().read(conn, readerConfig);
			readerConfig.incrementXbrcConfigModSeq();
			Config.getInstance().write(conn, readerConfig);
			
			//transaction closes on commit
			conn.commit();
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			if (updateReaderStmt != null) updateReaderStmt.close();
			
			conn.setAutoCommit(true);
			conn.setTransactionIsolation(transactionIsolation);
			
			Data.ReleaseConnection(conn);
		}
	}
	
	public void updateThreshold(String readerId, Integer threshold) throws Exception {
		if (readerId == null || threshold == null)
			return; 
		
		Connection conn = null;
		
		PreparedStatement updateReaderStmt = null;
		int transactionIsolation = 0;
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return;
			
			//open transaction
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);

			//update gain value
			String query = "UPDATE Reader SET signalStrengthThreshold = ? WHERE readerId = ?";
			updateReaderStmt = conn.prepareStatement(query);
			updateReaderStmt.clearParameters();
			updateReaderStmt.setDouble(1, threshold);
			updateReaderStmt.setString(2, readerId);
			updateReaderStmt.executeUpdate();
			
			//increment the xbrcConfigModSeq column to indicate that the Reader table has changed
			ReaderConfig readerConfig = ReaderConfig.getInstance();
			Config.getInstance().read(conn, readerConfig);
			readerConfig.incrementXbrcConfigModSeq();
			Config.getInstance().write(conn, readerConfig);
			
			//transaction closes on commit
			conn.commit();
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
		}
		finally
		{
			if (updateReaderStmt != null) updateReaderStmt.close();
			
			conn.setAutoCommit(true);
			conn.setTransactionIsolation(transactionIsolation);
			
			Data.ReleaseConnection(conn);
		}
	}
	
	private void instantiateLocationBean(ResultSet rs, Map<Long, LocationBean> locationBeans, 
			Map<String, List<LrrEvent>> events) throws Exception {
		
		//db info
		Long locationId = rs.getLong("locationId");
		String readerName = rs.getString("readerName");
		Double gain = rs.getDouble("gain");
		Integer threshold = rs.getInt("threshold");
		
		//each location can have 0 to many readers
		LocationBean bean = locationBeans.get(locationId);
		if (bean == null) {
			bean = new LocationBean(locationId);
			locationBeans.put(locationId, bean);
		}
		
		//construct/update a reader
		ReaderValue reader = bean.getReader(readerName);
		if (reader == null) {
			reader = new ReaderValue(readerName, gain, threshold, bean);
			bean.addReader(reader);
		}
		else {
			reader.setGain(gain);
			reader.setThreshold(threshold);
		}
		
		for (LrrEvent current : events.get(readerName)) {
			//info coming from the Controller		
			Long channelId = current.getChan();
			Long strength = current.getSs();
			Date timestamp = current.getTime();
			
			/*
			 * construct a channel and add it to the reader
			 * Channel and Strength will be null for readers that are not reading the band at the moment.
			 */
			if (channelId != null && strength != null){
				//reader can have 1 to many channels
				ReaderChannel channel = reader.getChannelsMap().get(channelId);
				
				//always grab the newest packet per channel
				//TODO refactor ReaderChannel to use Long for channelId and signal strength
				if (channel == null || channel.getTimestamp().before(timestamp)) {
					channel = new ReaderChannel(readerName, channelId.intValue(), strength.intValue(), timestamp, gain);
					reader.addChannel(channel);
				}
			}
		}				
	}
}
