package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GST;
import com.disney.xband.xbrc.lib.entity.Location;

public class GSTServiceImp implements GSTService {

	private Logger logger = Logger.getLogger(GSTServiceImp.class);
	
	@Override
	public GST find(String id) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = Data.GetConnection();			
			if (conn==null)
				return null;
			
			String query = "SELECT * FROM GST WHERE GuestId = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			GST gst = null;
			
			if (rs.next() == true)
				gst = instantiateGST(rs);
			
			return gst;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{
			if (stmt != null) stmt.close();
			if (rs != null) rs.close();
			
			Data.ReleaseConnection(conn);
		}
	}

	@Override
	public Collection<GST> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void save(GST gst) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(GST gst) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public GST instantiateGST(ResultSet rs) throws SQLException
	{	
		GST gst = new GST();
		
		gst.setId(rs.getString("GuestId"));		
		gst.setXPass(rs.getBoolean("HasXPass"));
		gst.setState(rs.getString("State"));
		gst.setLastReader(rs.getString("LastReader"));
		gst.setTimeEarliestAtReader(rs.getLong("TimeEarliestAtReader"));
		gst.setTimeLatestAtReader(rs.getLong("TimeLatestAtReader"));
		gst.setTimeEntered(rs.getLong("TimeEntered"));
		gst.setTimeMerged(rs.getLong("TimeMerged"));
		gst.setTimeLoaded(rs.getLong("TimeLoaded"));
		gst.setTimeExited(rs.getLong("TimeExited"));
		gst.setCarId(rs.getString("CarId"));
		gst.setDeferredEntry(rs.getBoolean("HasDeferredEntry"));
		
		return gst;
	}
}
