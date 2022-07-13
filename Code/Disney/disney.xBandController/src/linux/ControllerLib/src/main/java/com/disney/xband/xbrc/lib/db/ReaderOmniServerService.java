package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import com.disney.xband.xbrc.lib.entity.ReaderOmniServer;

public class ReaderOmniServerService
{
	public static Collection<ReaderOmniServer> getAll(Connection conn) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			// NOTE: do not change the sort order in this function. It is relied upon in the ParkEntryModel
			ps = conn.prepareStatement("select * from ReaderOmniServer order by readerid, priority");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Collection<ReaderOmniServer> list = new LinkedList<ReaderOmniServer>(); 
			
			while (rs.next() == true)
			{
				list.add(instantiate(rs));
			}
			
			return list;
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
	
	public static ReaderOmniServer find(Connection conn, Long id) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from ReaderOmniServer where id = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet(); 
			
			ReaderOmniServer obj = null;
			
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
			
			if (rs != null)
			{	
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
	
	public static void deleteByReaderId(Connection conn, Long readerid) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from ReaderOmniServer where readerid = ?");
			ps.setLong(1, readerid);
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
	
	public static void deleteByReaderAndOmniId(Connection conn, Long readerid, Long omniServerId) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from ReaderOmniServer where readerid = ? and omniServerId = ?");
			ps.setLong(1, readerid);
			ps.setLong(2, omniServerId);
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
	
	public static void deleteByOmniId(Connection conn, Long omniServerId) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from ReaderOmniServer where omniServerId = ?");
			ps.setLong(1, omniServerId);
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

	
	public static void save(Connection conn, ReaderOmniServer obj) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("insert into ReaderOmniServer (readerid, omniserverid, priority) values (?,?,?)");

			ps.setLong(1, obj.getReaderid());
			ps.setLong(2, obj.getOmniserverid());
			ps.setInt(3, obj.getPriority());
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
	
	public static void updatePriority(Connection conn, ReaderOmniServer obj) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("update ReaderOmniServer set priority=?, where readerid=? and omniserverid = ?");

			ps.setInt(1, obj.getPriority());
			ps.setLong(2, obj.getReaderid());
			ps.setLong(3, obj.getOmniserverid());
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
	
	public static ReaderOmniServer instantiate(ResultSet rs) throws Exception
	{
		ReaderOmniServer obj = new ReaderOmniServer();			
		obj.setReaderid(rs.getLong("readerid"));
		obj.setOmniserverid(rs.getLong("omniserverid"));
		obj.setPriority(rs.getInt("priority"));
		return obj;
	}
}
