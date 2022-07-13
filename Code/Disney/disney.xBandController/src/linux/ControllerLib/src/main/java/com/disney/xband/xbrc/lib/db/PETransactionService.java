package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import com.disney.xband.xbrc.lib.entity.PETransaction;

public class PETransactionService {
	
	public static void save(Connection conn, PETransaction obj) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into PETransaction (bandId, readerName, locationName, startTime)" +
									   " values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1,obj.getBandId());
			ps.setString(2,obj.getReaderName());
			ps.setString(3,obj.getLocationName());
			ps.setTimestamp(4,new java.sql.Timestamp(obj.getStartTime().getTime()));
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getLong(1));
			}
			rs.close();
		}
		finally
		{
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void update(Connection conn, PETransaction obj) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			/* The startTime = startTime below is a workaround a MySQL bug where it tries to update a field
			 * not specified in the update statement with a default value.
			 */
			ps = conn.prepareStatement("update PETransaction set startTime = startTime, " +
					"omniEntitlementDuration = ?, omniEntitlementCount = ?, scanDuration = ?, scanCount = ?, omniBioMatchDuration = ?, " +
					"omniBioMatchCount = ?, totalDuration = ?, scanErrorCount = ?, scanErrorReasons = ?, blue = ?, abandoned = ?, finishTime = ?, " +
					"tdssnSite = ?, tdssnStation = ?, tdssnDate = ?, tdssnTicketId = ?, omniEntitlementErrorCode = ?, omniEntitlementErrorDesc = ?, " +
					"omniBioMatchErrorCode = ?, omniBioMatchErrorDesc = ?, decremented = ?, newEnrollment = ? " +
					"where id = ?");
			
			ps.setLong(1,obj.getOmniEntitlementDuration());
			ps.setInt(2,obj.getOmniEntitlementCount());
			ps.setLong(3,obj.getScanDuration());
			ps.setInt(4,obj.getScanCount());
			ps.setLong(5, obj.getOmniBioMatchDuration());
			ps.setInt(6, obj.getOmniBioMatchCount());
			ps.setLong(7,obj.getTotalDuration());
			ps.setLong(8, obj.getScanErrorCount());
			if (obj.getScanErrorReasons() == null)
				ps.setObject(9,obj.getScanErrorReasons());
			else
				ps.setString(9,obj.getScanErrorReasons());
			ps.setBoolean(10, obj.getBlue());
			ps.setBoolean(11, obj.getAbandoned());
			ps.setTimestamp(12,new java.sql.Timestamp(obj.getFinishTime().getTime()));
			if (obj.getTdssnSite() == null)
				ps.setObject(13,obj.getTdssnSite());
			else
				ps.setString(13,obj.getTdssnSite());
			if (obj.getTdssnStation() == null)
				ps.setObject(14,obj.getTdssnStation());
			else
				ps.setString(14,obj.getTdssnStation());
			if (obj.getTdssnDate() == null)
				ps.setObject(15,obj.getTdssnDate());
			else
				ps.setString(15,obj.getTdssnDate());
			if (obj.getTdssnTicketId() == null)
				ps.setObject(16,obj.getTdssnTicketId());
			else
				ps.setString(16,obj.getTdssnTicketId());			
			ps.setLong(17, obj.getOmniEntitlementErrorCode());
			if (obj.getOmniEntitlementErrorDesc() == null)
				ps.setObject(18, null);
			else
			{
				// Fix bug 6425 -- the database has a too small field
				String desc = obj.getOmniEntitlementErrorDesc();
				if (desc.length() > 64)
					desc = desc.substring(0, 63);
				ps.setString(18, desc);
			}
			ps.setLong(19, obj.getOmniBioMatchErrorCode());
			if (obj.getOmniBioMatchErrorDesc() == null)
				ps.setObject(20, null);
			else
				ps.setString(20, obj.getOmniBioMatchErrorDesc());
			ps.setBoolean(21, obj.getDecremented());
			ps.setBoolean(22, obj.getNewEnrollment());
			ps.setLong(23, obj.getId());
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteOlderThan(Connection conn, Date olderThan) throws SQLException {
		PreparedStatement ps = null;
		
		try
		{	
			ps = conn.prepareStatement("delete from PETransaction where startTime < ?");
			ps.clearParameters();
			ps.setTimestamp(1, new Timestamp(olderThan.getTime()));
			ps.execute();
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
}
