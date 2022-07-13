package com.disney.xband.xbrc.ui.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;

public class GridItemServiceImp implements GridItemService {

	private Logger logger = Logger.getLogger(GridItemServiceImp.class);
	
	@Override
	public GridItem find(Long id) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			if (conn==null)
				return null;
			
			String query = "select * from GridItem where id = ?";
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			GridItem gi = null;
			
			if (rs.next() == true)
				gi = instantiateGridItem(rs);
			
			return gi;
		} 
		catch (SQLException e)
		{
			logger.error("SQL error: " + e.getLocalizedMessage(), e);
			return null;
		}
		finally
		{		
			if (rs != null) {
                try {
                    rs.close();
                }
                catch(Exception e) {
                }
            }

	    	if (stmt != null) {
                try {
    				stmt.close();
                }
                catch(Exception e) {
                }
            }

			UIConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	@Override
	public Collection<GridItem> findAll() throws Exception {
		return null;
	}

	@Override
	public void insert(GridItem gi) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();		
			if (conn==null)
				return;
			
			String sSQL = "INSERT INTO GridItem(ItemType, XGrid, YGrid, State, Label, Description, Image, Sequence, XPassOnly, LocationId) Values(?,?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setString(1, gi.getItemType().toString());
			stmt.setInt(2, gi.getxGrid());
			stmt.setInt(3, gi.getyGrid());
			stmt.setString(4, gi.getState().toString());
			stmt.setString(5, gi.getLabel());
			stmt.setString(6, gi.getDescription());
			stmt.setString(7, gi.getImage());
			stmt.setInt(8, gi.getSequence());
			stmt.setInt(9, gi.getxPassOnly().ordinal());
		
			if (gi.getLocationId() == null)
				stmt.setObject(10, null);
			else
				stmt.setLong(10, gi.getLocationId());
			
			stmt.execute();
		}
		catch (SQLException e)
		{
			logger.error("SQL error inserting GridItem: " + e.getLocalizedMessage(), e);
		}
		finally
		{
            if (stmt != null) {
			    try {
					stmt.close();
			    }
                catch (Exception e) {
                }
            }
			
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	@Override
	public void update(GridItem gi) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();		
			if (conn==null)
				return;
			
			String sSQL = "update GridItem set ItemType = ?, XGrid = ?, YGrid = ?, State = ?, Label = ?, Description = ?, Image = ?, Sequence = ?, XPassOnly = ?, LocationId = ? where id = ?";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setString(1, gi.getItemType().toString());
			stmt.setInt(2, gi.getxGrid());
			stmt.setInt(3, gi.getyGrid());
			stmt.setString(4, gi.getState().toString());
			stmt.setString(5, gi.getLabel());
			stmt.setString(6, gi.getDescription());
			stmt.setString(7, gi.getImage());
			stmt.setInt(8, gi.getSequence());
			stmt.setInt(9, gi.getxPassOnly().ordinal());			
		
			if (gi.getLocationId() == null)
				stmt.setObject(10, null);
			else
				stmt.setLong(10, gi.getLocationId());
			
			stmt.setLong(11, gi.getId());			
			stmt.execute();
		}
		catch (SQLException e)
		{				
			logger.error("SQL error inserting GridItem: " + e.getLocalizedMessage(), e);
		}
		finally
		{
            if (stmt != null) {
			    try {
					stmt.close();
				
			    }
                catch(Exception e) {
                }
            }
			
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	@Override
	public void delete(Long id) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();		
			if (conn==null)
				return;
			
			String sSQL = "delete from GridItem where id = ?";
			stmt = conn.prepareStatement(sSQL);
			
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
		}
		catch (SQLException e)
		{
			logger.error("SQL error deleting GridItem: " + e.getLocalizedMessage(), e);
		}
		finally
		{
            if (stmt != null) {
			    try {
					stmt.close();
			    }
                catch(Exception e) {
                }
            }
			
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	private GridItem instantiateGridItem(ResultSet rs) throws SQLException {
        GridItem gi = new GridItem(rs.getInt("id"),
		GridItem.ItemType.valueOf(rs.getString("ItemType")),
		rs.getInt("XGrid"),
		rs.getInt("YGrid"),
		GuestStatusState.valueOf(rs.getString("State").isEmpty() ? "INDETERMINATE" : rs.getString("State")),
		rs.getString("Label"),
		rs.getString("Description"),
		rs.getString("Image"),
		rs.getInt("Sequence"),
		XpassOnlyState.fromOrdinal(rs.getInt("XPassOnly")),
		rs.getLong("LocationId"));

		return gi;
	}	
}
