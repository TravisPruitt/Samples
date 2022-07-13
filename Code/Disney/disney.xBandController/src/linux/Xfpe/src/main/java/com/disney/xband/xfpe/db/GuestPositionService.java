package com.disney.xband.xfpe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GuestInfo;

public class GuestPositionService {
	private Logger logger = Logger.getLogger(GuestPositionService.class);
	
	public void save(GuestInfo gi) throws SQLException {
		if (gi == null)
			throw new IllegalArgumentException();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			conn = XfpeConnectionPool.getInstance().getConnection();			
			if (conn==null)
				return;
		
			//insert the new location
			String query = "update GuestPosition set x=?, y=?, HasxPass=? where BandID=?";
			
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setDouble(1, gi.getX());
			stmt.setDouble(2, gi.getY());
			stmt.setBoolean(3, gi.getHasxPass());
			stmt.setString(4, gi.getsXID());
			
			stmt.executeUpdate();
			if (stmt.getUpdateCount() > 0)
				return;
			
			stmt.close();
			
			// We must do an insert because nothing got updated.
				
			query = "insert into GuestPosition (BandId,x,y,HasxPass) values (?,?,?,?)";
			
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, gi.getsXID());
			stmt.setDouble(2, gi.getX());
			stmt.setDouble(3, gi.getY());
			stmt.setBoolean(4, gi.getHasxPass());
			
			stmt.executeUpdate();
			
		} finally {
			XfpeConnectionPool.getInstance().releaseConnection(conn);
			if (stmt != null) stmt.close();
		}
		return;
	}
}
