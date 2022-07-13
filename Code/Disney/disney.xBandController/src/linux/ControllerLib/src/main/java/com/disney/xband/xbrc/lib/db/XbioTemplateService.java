package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.disney.xband.xbrc.lib.entity.XbioTemplate;
import java.sql.PreparedStatement;
import java.util.Date;

public class XbioTemplateService {
	
	public static void save(Connection conn, XbioTemplate xbTemplate) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into XbioTemplate (transactionId,template,totalScanDuration,timestamp)" +
									   " values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setLong(1, xbTemplate.getTransactionId());
			ps.setString(2,xbTemplate.getTemplate());
			ps.setLong(3,xbTemplate.getTotalScanDuration());
			ps.setTimestamp(4, new java.sql.Timestamp(xbTemplate.getTimestamp().getTime()));			
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				xbTemplate.setId(rs.getLong(1));
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
	
	public static void deleteOlderThan(Connection conn, Date olderThan) throws SQLException {
		PreparedStatement ps = null;
		
		try
		{	
			ps = conn.prepareStatement("delete from XbioTemplate where timestamp < ?");
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
