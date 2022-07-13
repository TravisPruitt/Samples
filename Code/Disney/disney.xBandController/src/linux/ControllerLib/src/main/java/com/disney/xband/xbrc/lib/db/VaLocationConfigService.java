package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.disney.xband.xbrc.lib.entity.VaLocationConfig;

public class VaLocationConfigService
{	
	public static Map<Long,VaLocationConfig> getAllMapped(Connection conn) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from VaLocationConfig");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Map<Long,VaLocationConfig> map = new HashMap<Long,VaLocationConfig>(); 
			
			while (rs.next() == true)
			{
				VaLocationConfig ob = instantiate(rs);
				map.put(ob.getLocationId(), ob);
			}
			
			return map;
		} 
		finally
		{
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
		}
	}
	
	public static VaLocationConfig find(Connection conn, Long id) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from VaLocationConfig where locationId = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet(); 
			
			VaLocationConfig obj = null;
			
			if (rs.next() == true)
				obj = instantiate(rs);
			
			return obj;
		} 
		finally
		{
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                }
            }

            if(rs != null) {
                try
                {
                    rs.close();
                }
                catch(SQLException e)
                {
                }
            }
		}
	}
	
	public static void delete(Connection conn, Long locationId) throws SQLException
	{
		PreparedStatement ps = null;
		
		try
		{
			ps = conn.prepareStatement("delete from VaLocationConfig WHERE locationId = ?");
			ps.clearParameters();
			ps.setLong(1, locationId);
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e){}
			}
		}
	}
	
	public static void deleteAll(Connection conn) throws SQLException
	{
		PreparedStatement ps = null;
		
		try
		{
			ps = conn.prepareStatement("delete from VaLocationConfig");
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e){}
			}
		}
	}
	
	public static void save(Connection conn, VaLocationConfig obj) throws SQLException
	{
		PreparedStatement ps = null;
		
		try
		{			
			ps = conn.prepareStatement("insert into VaLocationConfig (locationId, onrideTimeoutSec ,maxAnalyzeGuestEvents," + 
					" maxAnalyzeGuestEventsPerVehicle, useVehicleEventTime, requireVehicleLaserEvent, vehicleTimeOffsetMs, minReadsToAssociate," + 
					" trainTimeoutSec, laserBreaksBeforeVehicle, laserBreaksAfterVehicle, vehicleHoldTimeMs, vaAlgorithm, carsPerTrain, vaTimeoutSec)" +
					" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");			
		
			ps.setLong(1, obj.getLocationId());
			ps.setInt(2, obj.getOnrideTimeoutSec());
			ps.setInt(3, obj.getMaxAnalyzeGuestEvents());
			ps.setInt(4, obj.getMaxAnalyzeGuestEventsPerVehicle());
			ps.setBoolean(5, obj.getUseVehicleEventTime());
			ps.setBoolean(6, obj.getRequireVehicleLaserEvent());
			ps.setLong(7, obj.getVehicleTimeOffsetMs());
			ps.setInt(8, obj.getMinReadsToAssociate());
			ps.setInt(9, obj.getTrainTimeoutSec());
			ps.setInt(10, obj.getLaserBreaksBeforeVehicle());
			ps.setInt(11, obj.getLaserBreaksAfterVehicle());
			ps.setLong(12, obj.getVehicleHoldTimeMs());
			ps.setString(13, obj.getVaAlgorithm());
			ps.setInt(14, obj.getCarsPerTrain());
			ps.setInt(15, obj.getVaTimeoutSec());
			
			ps.executeUpdate();
			ps.close();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void update(Connection conn, VaLocationConfig obj) throws SQLException
	{
		PreparedStatement ps = null;
		
		try
		{	
			ps = conn.prepareStatement("update VaLocationConfig set onrideTimeoutSec = ?, maxAnalyzeGuestEvents = ?," + 
					" maxAnalyzeGuestEventsPerVehicle = ?, useVehicleEventTime = ?, requireVehicleLaserEvent = ?, vehicleTimeOffsetMs = ?," + 
					" minReadsToAssociate = ?, trainTimeoutSec = ?, laserBreaksBeforeVehicle = ?, laserBreaksAfterVehicle = ?, vehicleHoldTimeMs = ?," +
					" vaAlgorithm = ?, carsPerTrain = ?, vaTimeoutSec = ? where locationId = ?");			
					
			ps.setInt(1, obj.getOnrideTimeoutSec());
			ps.setInt(2, obj.getMaxAnalyzeGuestEvents());
			ps.setInt(3, obj.getMaxAnalyzeGuestEventsPerVehicle());
			ps.setBoolean(4, obj.getUseVehicleEventTime());
			ps.setBoolean(5, obj.getRequireVehicleLaserEvent());
			ps.setLong(6, obj.getVehicleTimeOffsetMs());
			ps.setInt(7, obj.getMinReadsToAssociate());
			ps.setInt(8, obj.getTrainTimeoutSec());
			ps.setInt(9, obj.getLaserBreaksBeforeVehicle());
			ps.setInt(10, obj.getLaserBreaksAfterVehicle());
			ps.setLong(11, obj.getVehicleHoldTimeMs());
			ps.setString(12, obj.getVaAlgorithm());
			ps.setInt(13, obj.getCarsPerTrain());
			ps.setInt(14, obj.getVaTimeoutSec());
			ps.setLong(15, obj.getLocationId());			
			
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}	
	
	public static VaLocationConfig instantiate(ResultSet rs) throws Exception
	{
		VaLocationConfig obj = new VaLocationConfig();			
		obj.setLocationId(rs.getLong("locationId"));
		obj.setOnrideTimeoutSec(rs.getInt("onrideTimeoutSec"));
		obj.setMaxAnalyzeGuestEvents(rs.getInt("maxAnalyzeGuestEvents"));
		obj.setMaxAnalyzeGuestEventsPerVehicle(rs.getInt("maxAnalyzeGuestEventsPerVehicle"));		
		obj.setUseVehicleEventTime(rs.getBoolean("useVehicleEventTime"));
		obj.setRequireVehicleLaserEvent(rs.getBoolean("requireVehicleLaserEvent"));
		obj.setVehicleTimeOffsetMs(rs.getLong("vehicleTimeOffsetMs"));
		obj.setMinReadsToAssociate(rs.getInt("minReadsToAssociate"));		
		obj.setTrainTimeoutSec(rs.getInt("trainTimeoutSec"));
		obj.setLaserBreaksBeforeVehicle(rs.getInt("laserBreaksBeforeVehicle"));
		obj.setLaserBreaksAfterVehicle(rs.getInt("laserBreaksAfterVehicle"));
		obj.setVehicleHoldTimeMs(rs.getLong("vehicleHoldTimeMs"));
	    obj.setVaAlgorithm(rs.getString("vaAlgorithm"));
	    obj.setCarsPerTrain(rs.getInt("carsPerTrain"));
	    obj.setVaTimeoutSec(rs.getInt("vaTimeoutSec"));
		return obj;
	}
}
