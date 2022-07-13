package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.OmniReaderAgregate;
import com.disney.xband.xbrc.lib.entity.Reader;

public class OmniServerService
{
	private static final String QUERY_ALL_OMNI_WITH_READER_COUNT = "SELECT os.*, count(ros.readerid) readercount FROM OmniServer AS os LEFT JOIN ReaderOmniServer AS ros ON os.id = ros.omniserverid GROUP BY os.hostname ORDER BY os.hostname";
	
	public static Collection<OmniServer> getAll(Connection conn) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from OmniServer order by hostname");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Collection<OmniServer> list = new LinkedList<OmniServer>(); 
			
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
	
	public static Map<OmniServer, Integer> getReaderCount(Connection conn) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement(QUERY_ALL_OMNI_WITH_READER_COUNT);
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Map<OmniServer, Integer> map = new LinkedHashMap<OmniServer, Integer>(); 
			
			while (rs.next())
			{
				map.put(instantiate(rs), rs.getInt("readercount"));
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
	
	public static Map<Long,OmniServer> getAllMapped(Connection conn) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from OmniServer order by hostname");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			Map<Long,OmniServer> map = new HashMap<Long,OmniServer>(); 
			
			while (rs.next() == true)
			{
				OmniServer os = instantiate(rs);
				map.put(os.getId(), os);
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
	
	public static Collection<OmniReaderAgregate> getAllForReader(Connection conn, Long readerId) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select os.*, ro.priority from OmniServer os, ReaderOmniServer ro " +
									   "where os.id = ro.omniserverid and ro.readerid = ? order by ro.priority, os.hostname");
			ps.clearParameters();
			ps.setLong(1, readerId);
			ps.execute();
			rs = ps.getResultSet();
			
			Collection<OmniReaderAgregate> list = new LinkedList<OmniReaderAgregate>(); 
			
			while (rs.next() == true)
			{
				OmniReaderAgregate ora = new OmniReaderAgregate(
						instantiate(rs),
						readerId,
						rs.getInt("priority"));
				
				list.add(ora);
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
	
	/**
	 * Returns all omni servers that are NOT configured for this reader.
	 * @param conn
	 * @param readerId if <CODE>null</CODE> all omni servers will be returned
	 * @return
	 * @throws Exception
	 */
	public static Collection<OmniServer> getAllMissingFromReader(Connection conn, Long readerId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			if (readerId != null)
			{
				ps = conn.prepareStatement("SELECT os.* FROM OmniServer AS os WHERE os.id NOT IN (SELECT omniserverid FROM ReaderOmniServer WHERE readerid = ?)");
				ps.clearParameters();
				ps.setLong(1, readerId);
				ps.execute();
			}
			else
			{
				ps = conn.prepareStatement("SELECT * FROM OmniServer ORDER BY hostname");
				ps.clearParameters();
				ps.execute();
			}
			
			rs = ps.getResultSet();
			
			Collection<OmniServer> list = new LinkedList<OmniServer>(); 
			
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
				
				try
				{
                    if ( rs != null )
					    rs.close();
				}
				catch(SQLException e)
				{					
				}
			}
		}
	}
	
	public static OmniServer find(Connection conn, Long id) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from OmniServer where id = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet(); 
			
			OmniServer obj = null;
			
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
	
	public static void delete(Connection conn, Long omniId, boolean startTransaction) throws SQLException
	{
		PreparedStatement ps = null;
		int previousTransactionIsolation = -1;
		boolean previousAutoCommit = true;
		try
		{
			//open transaction
			if (startTransaction)
			{
				previousTransactionIsolation = conn.getTransactionIsolation();
				previousAutoCommit = conn.getAutoCommit();
			
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				conn.setAutoCommit(false);
			}
			
			ps = conn.prepareStatement("DELETE FROM ReaderOmniServer WHERE omniserverid = ?");
			ps.clearParameters();
			ps.setLong(1, omniId);
			ps.executeUpdate();
			
			ps = conn.prepareStatement("DELETE FROM OmniServer WHERE id = ?");
			ps.clearParameters();
			ps.setLong(1, omniId);
			ps.executeUpdate();
			
			if (startTransaction)
				conn.commit();
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
			
			try
			{
				if (startTransaction)
				{
					conn.setAutoCommit(previousAutoCommit);
					conn.setTransactionIsolation(previousTransactionIsolation);
				}
			}
			catch (Exception e){}
		}
	}
	
	public static void save(Connection conn, OmniServer obj) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			Long id = obj.getId();
			
			ps = conn.prepareStatement("insert into OmniServer (hostname, port ,description, active, notActiveReason) values (?,?,?,?,?)",
									   Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, obj.getHostname());
			
			if (obj.getPort() != null)
				ps.setInt(2, obj.getPort());
			else
				ps.setNull(2, Types.INTEGER);
			
			if (obj.getDescription() != null)
				ps.setString(3, obj.getDescription());
			else
				ps.setNull(3, Types.VARCHAR);
			
			if (obj.getActive() != null)
				ps.setBoolean(4, obj.getActive());
			else
				ps.setNull(4, Types.TINYINT);
			
			if (obj.getNotActiveReason() != null)
				ps.setString(5, obj.getNotActiveReason());
			else
				ps.setNull(5, Types.VARCHAR);
			
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				obj.setId(rs.getLong(1));
			}
			rs.close();
			ps.close();
			
			if (id != null)
			{
				ps = conn.prepareStatement("update OmniServer set id = ? where id = ?");
				ps.setLong(1, id);
				ps.setLong(2, obj.getId());
				ps.executeUpdate();
				obj.setId(id);
				rs.close();
			}
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
	
	public static void update(Connection conn, OmniServer obj) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("update OmniServer set hostname=?, port=?, description=?, active=?, notActiveReason=? where id = ?");

			ps.setString(1, obj.getHostname());
			
			if (obj.getPort() != null)
				ps.setInt(2, obj.getPort());
			else
				ps.setNull(2, Types.INTEGER);
			
			if (obj.getDescription() != null)
				ps.setString(3, obj.getDescription());
			else
				ps.setNull(3, Types.VARCHAR);
			
			if (obj.getActive() != null)
				ps.setBoolean(4, obj.getActive());
			else
				ps.setNull(4, Types.TINYINT);
			
			if (obj.getNotActiveReason() != null)
				ps.setString(5, obj.getNotActiveReason());
			else
				ps.setNull(5, Types.VARCHAR);
			
			ps.setLong(6, obj.getId());
			
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
	
	public static Map<Reader, Integer> findReaders(Connection conn, Long omniServerId) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("SELECT r.readerId, r.macAddress, r.ipAddress, r.port, ros.priority FROM Reader AS r RIGHT JOIN ReaderOmniServer AS ros ON r.id = ros.readerid WHERE ros.omniserverid = ? ORDER BY r.readerId ASC");
			ps.clearParameters();
			ps.setLong(1, omniServerId);
			ps.execute();
			rs = ps.getResultSet(); 
			
			Map<Reader, Integer> map = new LinkedHashMap<Reader, Integer>();
			
			if (rs.next())
			{
				Reader reader = new Reader();
				reader.setReaderId(rs.getString("readerId"));
				reader.setMacAddress(rs.getString("macAddress"));
				reader.setIpAddress(rs.getString("ipAddress"));
				reader.setPort(rs.getInt("port"));
				
				map.put(reader, rs.getInt("priority"));
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
	
	public static void associateReader(Connection conn, Map<Integer, Integer> omnis, Long readerId) throws SQLException
	{
		if (omnis == null || omnis.size() == 0 || readerId == null || conn == null)
			return;

		PreparedStatement ps = null;
		int previousTransactionIsolation = -1;
		boolean previousAutoCommit = true;
		try
		{
			//open transaction
			previousTransactionIsolation = conn.getTransactionIsolation();
			previousAutoCommit = conn.getAutoCommit();
			
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			
			// clean up
			OmniServerService.disassociateAllOmniServers(conn, readerId);
			
			// do the association
			ps = conn.prepareStatement("INSERT INTO ReaderOmniServer (readerid, omniserverid ,priority) VALUES (?,?,?)");
			ps.clearBatch();
			
			for (Integer omniId : omnis.keySet()){
				ps.setInt(1, readerId.intValue());
				ps.setInt(2, omniId);
				ps.setInt(3, omnis.get(omniId));
				ps.addBatch();
			}
			
			ps.executeBatch();
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
			
			try 
			{
				conn.setAutoCommit(previousAutoCommit);
				conn.setTransactionIsolation(previousTransactionIsolation);
			}
			catch (Exception e){}
		}
	}
	
	public static void disassociateReader(Connection conn, Collection<Integer> omnis, Long readerId) throws SQLException
	{
		if (omnis == null || omnis.size() == 0 || readerId == null || conn == null)
			return;
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("DELETE FROM ReaderOmniServer WHERE readerid = ? AND omniserverid = ?");
			ps.clearBatch();
			
			for (Integer omniId : omnis){
				ps.setInt(1, readerId.intValue());
				ps.setInt(2, omniId);
				ps.addBatch();
			}
			
			ps.executeBatch();
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
	
	public static void disassociateAllOmniServers(Connection conn, Long readerId) throws SQLException
	{
		if (readerId == null || conn == null)
			return;

		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement("DELETE FROM ReaderOmniServer WHERE readerid = ?");
			ps.clearParameters();
			ps.setInt(1, readerId.intValue());
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
	
	public static OmniServer instantiate(ResultSet rs) throws Exception
	{
		OmniServer obj = new OmniServer();			
		obj.setId(rs.getLong("id"));
		obj.setHostname(rs.getString("hostname"));
		obj.setPort(rs.getInt("port"));
		obj.setDescription(rs.getString("description"));
		obj.setActive(rs.getBoolean("active"));
		obj.setNotActiveReason(rs.getString("notActiveReason"));
		return obj;
	}
}
