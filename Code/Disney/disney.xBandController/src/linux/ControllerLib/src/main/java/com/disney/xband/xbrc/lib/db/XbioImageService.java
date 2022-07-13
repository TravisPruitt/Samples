package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.xbrc.lib.entity.XbioImage;

public class XbioImageService {
	
	public static void save(Connection conn, XbioImage xbImage) throws SQLException {
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("insert into XbioImage (transactionId,templateId,images,timestamp) values (?,?,?,?)");
			
			ps.setLong(1,xbImage.getTransactionId());
			ps.setLong(2, xbImage.getTemplateId());
			ps.setString(3,xbImage.getImages());
			ps.setTimestamp(4, new java.sql.Timestamp(xbImage.getTimestamp().getTime()));
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
			ps = conn.prepareStatement("delete from XbioImage where timestamp < ?");
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
