package com.disney.xband.xbrms.server.model;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.disney.xband.xbrms.common.model.*;
import org.apache.log4j.Logger;
import org.objectweb.asm.Type;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrms.common.model.HealthItem;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class HealthItemDao
{
	private static Logger logger = Logger.getLogger(HealthItemDao.class);
	private static Logger plogger = Logger.getLogger("performance." + HealthItemDao.class.toString());
	
	private static final String INSERT_HEALTH_ITEM_FIELDS = "INSERT INTO HealthItemField (healthItemId,id,value,name,description,type,mandatory) VALUES(?,?,?,?,?,?,?)";
	private static final String INSERT_HEALTH_ITEM = "INSERT INTO HealthItem (ip,port,hostname,vip,vport,className,name,version,lastDiscovery,nextDiscovery,active,status,statusMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static HealthItemDao getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public boolean exists(String key) {
		if (key == null || key.trim().isEmpty())
			return false;
		
		String[] keyParts = key.split(":");
		if (keyParts.length != 2)
			return false;
		
		try 
		{
			Long port = Long.parseLong(keyParts[1]);
			if (port == null)
				return false;
			
			return exists(keyParts[0], port.longValue());
		}
		catch (NumberFormatException e)
		{
			logger.error("Health item cache key " + key + " failed to parse into ip and port.");
			return false;
		}
	}
	
	public boolean exists(String ip, long port) {
		if (ip == null)
			return false;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		long begin = System.currentTimeMillis();
		String query = "SELECT count(*) AS hiCount FROM HealthItem (NOLOCK) WHERE ip = ? AND port = ?";
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			/*
			 * Get the health item
			 */
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setString(1, ip);
			stmt.setLong(2, port);
			stmt.execute();
			rs = stmt.getResultSet();
			
			int count = 0;
			if (rs.next())
				count = rs.getInt("hiCount");
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			if (count == 1)
				return true;
			
			return false;
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Looking up health item by ip: " + ip + ":" + port);
		}
        finally {
            SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
		
		return false;
	}
	
	/**
	 * Retrieve a health item by id.
	 * @param id
	 * @return the health item or <code>null</code> if item was not found or an <code>SQLExceptio</code> occurred.
	 * @throws Exception
	 */
	public HealthItemDto find(Long id) {
		if (id == null)
			return null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		long begin = System.currentTimeMillis();
		String query = "SELECT hi.*, hif.id AS fieldKey, hif.value AS fieldValue FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId WHERE hi.id = ?";
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			/*
			 * Get the health item
			 */
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setLong(1, id);
			stmt.execute();
			rs = stmt.getResultSet();
			
			HealthItemDto hi = null;
			String fieldKey = null;
			String fieldValue = null;
			
			while (rs.next())
			{
				if (hi == null)
					hi = instantiateHealthItem(rs);
					
				if (hi == null)
					break;
				
				fieldKey = rs.getString("fieldKey");
				if (rs.wasNull())
					break;	// no dynamic fields on this health item
				
				fieldValue = rs.getString("fieldValue");
				if (rs.wasNull())
					fieldValue = null;
				
				addDynamicHealthItemField(fieldKey, fieldValue, hi);
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return hi;
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving health item by id: " + id);
			
			return null;
		}
        finally 
        {
            SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
    }
	
    public HealthItemDto findByIp(String ip, Integer port)
    {
        return this.findByIp(ip, port, false);
    }

	public HealthItemDto findByIp(String ip, Integer port, boolean isOrHostname)
	{
		if (ip == null || port == null)
			return null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String query = null;
		
		long begin = System.currentTimeMillis();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			query = isOrHostname ?
					"SELECT hi.*, hif.id AS fieldKey, hif.value AS fieldValue FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId WHERE (hi.ip = ? OR hi.hostname = ?) AND (hi.port = ?)" :
					"SELECT hi.*, hif.id AS fieldKey, hif.value AS fieldValue FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId WHERE hi.ip = ? AND hi.port = ?";

			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();

            if(isOrHostname) {
                stmt.setString(1, ip);
                stmt.setString(2, ip);
                stmt.setInt(3, port);
            }
            else {
                stmt.setString(1, ip);
                stmt.setInt(2, port);
            }

			stmt.execute();
			rs = stmt.getResultSet();

			String fieldKey = null;
			String fieldValue = null;
			HealthItemDto hi = null;
			
			while (rs.next())
			{
				if (hi == null)
					hi = instantiateHealthItem(rs);
					
				if (hi == null)
					break;
				
				fieldKey = rs.getString("fieldKey");
				if (rs.wasNull())
					break;	// no dynamic fields on this health item
				
				fieldValue = rs.getString("fieldValue");
				if (rs.wasNull())
					fieldValue = null;
				
				addDynamicHealthItemField(fieldKey, fieldValue, hi);
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return hi;
		} 
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving health item by ip: " + ip);
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
		
		return null;
	}
	
	public Collection<HealthItemDto> findAll(boolean active)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String query = "SELECT hi.*, hif.id AS fieldKey, hif.value AS fieldValue FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId WHERE hi.active = ? ORDER BY hi.className, hi.id";
		
		long begin = System.currentTimeMillis();
		
		// check for data corruption: more than 2 xBRCs with the same facility id
		Map<String,Integer> facilityIdCount = new HashMap<String,Integer>();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();

			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setBoolean(1, active);
			stmt.execute();
			rs = stmt.getResultSet();
			
			Collection<HealthItemDto> results = new ArrayList<HealthItemDto>();
			
			String fieldKey = null;
			String fieldValue = null;
			
			HealthItemDto hi = null;
			
			while (rs.next())
			{
				if (hi != null && hi.getId().longValue() != rs.getLong("id"))
				{
					// processing next health item, put the last one in the results
					results.add(hi.clone());
					
					// mark the old object for GC
					hi = null; 
				}
				
				if (hi == null)
				{
					// new health item
					hi = instantiateHealthItem(rs);
					if (hi == null)
						continue;
				}
				
				fieldKey = rs.getString("fieldKey");
				if (!rs.wasNull())
				{
					fieldValue = rs.getString("fieldValue");
					if (rs.wasNull())
						fieldValue = null;

					addDynamicHealthItemField(fieldKey, fieldValue, hi);
				}
			}
			
			if (hi != null)
			{
				// add the last one
				results.add(hi);
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return results;
		} 
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving all " + (active ? "active" : "inactive") + " health items.");
			
			return null;
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
	}
	
	public Map<String, HealthItemDto> findInventory(boolean active)
	{	
		Collection<HealthItemDto> hiCollection = findAll(active);
		
		Map<String,HealthItemDto> inventory = new HashMap<String,HealthItemDto>();
		
		if (hiCollection == null)
			return inventory;
		
		for (HealthItemDto hi : hiCollection)
			inventory.put(hi.getIp() + ":" + hi.getPort(), hi);
			
			return inventory;
	}
	
	public <T extends HealthItemDto> List<T> getHealthItems(Class<T> hiClass, boolean active)
	{
		List<T> results = new LinkedList<T>();
		
		if (hiClass == null)
			return results; 
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String query = "SELECT hi.*, hif.id AS fieldKey, hif.value AS fieldValue FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId WHERE hi.active = ? AND hi.className = ?";
		
		long begin = System.currentTimeMillis();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setBoolean(1, active);
			stmt.setString(2, hiClass.getName());
			stmt.execute();
			rs = stmt.getResultSet();
			
			String fieldKey = null;
			String fieldValue = null;
			
			T hi = null;
			
			while (rs.next())
			{
				if (hi != null && hi.getId().longValue() != rs.getLong("id"))
				{
					// processing next health item, put the last one in the results
					results.add((T)hi.clone());
					
					// mark the old object for GC
					hi = null; 
				}
				
				if (hi == null)
				{
					// new health item
					hi = instantiateHealthItem(rs);
					if (hi == null)
						continue;
				}
				
				fieldKey = rs.getString("fieldKey");
				if (!rs.wasNull())
				{
					fieldValue = rs.getString("fieldValue");
					if (rs.wasNull())
						fieldValue = null;

					addDynamicHealthItemField(fieldKey, fieldValue, hi);
				}
			}
			
			if (hi != null)
				results.add(hi);
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return results;
		} 
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving all " + (active ? "active" : "inactive") + " health items.");
			
			return null;
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
	}
	
	/**
	 * Returns the map of keys in the format of [health item ip:health item port, health item name]
	 * for all active health items of type hiClass
	 * 
	 * @param hiClass health item type
	 * @param active
	 * @return
	 */
	public <T> Map<String, String> discoverAlive(Class<T> hiClass, boolean active)
	{
		Map<String, String> results = new HashMap<String, String>();
		
		if (hiClass == null)
			return results; 
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String query = "SELECT * FROM HealthItem (NOLOCK) WHERE active = ? AND className = ?";
		
		long begin = System.currentTimeMillis();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setBoolean(1, active);
			stmt.setString(2, hiClass.getName());
			stmt.execute();
			rs = stmt.getResultSet();
			
			while (rs.next())
			{
				try
				{
					HealthItemDto hi = instantiateHealthItem(rs);
					if (hi != null && hi.isAlive())
						results.put(rs.getString("ip") + ":" + rs.getInt("port"), rs.getString("name"));
				}
				catch (Exception e)
				{
					logger.error("Couldn't construct a health item", e);
				}
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return results;
		} 
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Retrieving all " + (active ? "active" : "inactive") + " health items.");
			
			return null;
		}
        finally {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;
        	
        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
	}
	
	public boolean insert(HealthItemDto ob)
	{
		if (ob == null)
			return false;
		
		if (ob.getId() != null)
			update(ob);
		
		Connection conn = null;
		PreparedStatement insertHealthItemStmt = null;
		PreparedStatement insertHealthItemFieldsStmt = null;
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		
		long begin = System.currentTimeMillis();
		long timeInTransaction = -1;
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			if (conn == null)
				return false;

			/*
			 * Prepare HealthItem and HealthItemFields insert statements
			 */
			
			// health item insert
			insertHealthItemStmt = SSConnectionPool.getInstance().getPreparedStatement(conn, INSERT_HEALTH_ITEM, Statement.RETURN_GENERATED_KEYS);
			insertHealthItemStmt.clearParameters();
			
			insertHealthItemStmt.setString(1, ob.getIp());
			insertHealthItemStmt.setInt(2, ob.getPort());
			insertHealthItemStmt.setString(3, ob.getHostname());
			insertHealthItemStmt.setString(4, ob.getVip());
			
			if (ob.getVport() != null)
				insertHealthItemStmt.setInt(5, ob.getVport());
			else
				insertHealthItemStmt.setNull(5, Types.INTEGER);
			
			insertHealthItemStmt.setString(6, ob.getClass().getName());
			insertHealthItemStmt.setString(7, ob.getName());
			insertHealthItemStmt.setString(8, ob.getVersion());
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
			
			Date lastDiscovery = ob.getLastDiscovery();
			if (lastDiscovery == null)
				lastDiscovery = new Date(0L);	// January 1st 1970
			insertHealthItemStmt.setTimestamp(9, new java.sql.Timestamp(lastDiscovery.getTime()), cal);

			if (ob.getNextDiscovery() != null)
				insertHealthItemStmt.setTimestamp(10, new java.sql.Timestamp(ob.getNextDiscovery().getTime()), cal);
			else
				insertHealthItemStmt.setNull(10, Types.DATE);
			
			insertHealthItemStmt.setBoolean(11, Boolean.TRUE);
			insertHealthItemStmt.setString(12, (ob.getStatus() != null ? ob.getStatus().getStatus().name() : null));
			insertHealthItemStmt.setString(13, (ob.getStatus() != null ? ob.getStatus().getMessage() : null));
			
			// health item fields insert
			insertHealthItemFieldsStmt = SSConnectionPool.getInstance().getPreparedStatement(conn, INSERT_HEALTH_ITEM_FIELDS);
			
			/*
			 * Start transaction
			 */
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			long startTransaction = System.currentTimeMillis();
			
			// Insert health item
			insertHealthItemStmt.executeUpdate();
			rs = insertHealthItemStmt.getGeneratedKeys();
			
			if (rs.next()){
				ob.setId(rs.getLong(1));
			} else {
				return false;
			}
			
			// Insert the new HealthItem's dynamic fields
			persistDynamicHealthItemsFields(insertHealthItemFieldsStmt, ob);
			
			timeInTransaction = System.currentTimeMillis() - startTransaction;
			
			conn.commit();
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
			
			return true;
		}
		catch(SQLException e)
		{
			try {
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception ignore){}
			
			SSConnectionPool.handleSqlException(e, "Persisting health item " + ob.getIp() + ":" + ob.getPort());
			
			return false;
		}
        finally 
        {
        	try
        	{
        		if (conn != null)
        		{
                	conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
        		}	
        	}
        	catch (Exception ignore) {}
        	
        	SSConnectionPool.getInstance().releaseResultSet(rs);
            rs = null;
        	SSConnectionPool.getInstance().releaseStatement(insertHealthItemStmt);
            insertHealthItemStmt = null;
    		SSConnectionPool.getInstance().releaseStatement(insertHealthItemFieldsStmt);
            insertHealthItemFieldsStmt = null;
    		SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;

    		if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Inside transaction: " + timeInTransaction + "msec. Query1: " 
            						+ INSERT_HEALTH_ITEM + ". Query2: " + INSERT_HEALTH_ITEM_FIELDS);
            }
        }
	}

	public void update(HealthItemDto ob)
	{
		if (ob == null)
			return;
		
		if (ob.getId() == null)
			insert(ob);
		
		Connection conn = null;
		PreparedStatement updateHealthItemStmt = null;
		PreparedStatement deleteHealthItemFieldsStmt = null;
		PreparedStatement insertHealthItemFieldsStmt = null;

		int transactionIsolation = 0;
		boolean autoCommit = false;
		
		long begin = System.currentTimeMillis();
		long timeInTransaction = -1L;
		
		String query = "update HealthItem set ip=?,port=?,hostname=?,vip=?,vport=?,name=?,version=?,lastDiscovery=?,nextDiscovery=?,active=?,status=?,statusMessage=? where id=?";
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			/*
			 * Update the health item 
			 */
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+0"));
			
			updateHealthItemStmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);

			updateHealthItemStmt.clearParameters();
			updateHealthItemStmt.setString(1, ob.getIp());
			updateHealthItemStmt.setInt(2, ob.getPort());
			updateHealthItemStmt.setString(3, ob.getHostname());
			updateHealthItemStmt.setString(4, ob.getVip());
			
			if (ob.getVport() != null)
				updateHealthItemStmt.setInt(5, ob.getVport());
			else
				updateHealthItemStmt.setNull(5, Type.INT);
			
			updateHealthItemStmt.setString(6, ob.getName());
			updateHealthItemStmt.setString(7, ob.getVersion());
			updateHealthItemStmt.setTimestamp(8, new java.sql.Timestamp(ob.getLastDiscovery().getTime()), cal);
			updateHealthItemStmt.setTimestamp(9, new java.sql.Timestamp(ob.getNextDiscovery().getTime()), cal);
			updateHealthItemStmt.setBoolean(10, ob.isActive());
			updateHealthItemStmt.setString(11, (ob.getStatus() != null ? ob.getStatus().getStatus().name() : null));
			updateHealthItemStmt.setString(12, (ob.getStatus() != null ? ob.getStatus().getMessage() : null));
			updateHealthItemStmt.setLong(13,ob.getId());
			
			/*
			 * Delete dynamic fields
			 */
			deleteHealthItemFieldsStmt = SSConnectionPool.getInstance().getPreparedStatement(conn, "DELETE FROM HealthItemField WHERE healthItemId = ?");
			deleteHealthItemFieldsStmt.clearParameters();
			deleteHealthItemFieldsStmt.setInt(1, ob.getId().intValue());

			/*
			 * Re-create dynamic fields
			 */
			insertHealthItemFieldsStmt = SSConnectionPool.getInstance().getPreparedStatement(conn, INSERT_HEALTH_ITEM_FIELDS);
			
			/*
			 * Open transaction
			 */
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			long startTransaction = System.currentTimeMillis();
						
			// update HealthItem
			updateHealthItemStmt.execute();
			
			// delete old HealthItem fields
			deleteHealthItemFieldsStmt.execute();
			
			// insert new HealthItem fields
			persistDynamicHealthItemsFields(insertHealthItemFieldsStmt, ob);
			
			conn.commit();
			
			timeInTransaction = System.currentTimeMillis() - startTransaction;
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
		}
		catch(SQLException e)
		{
			try 
			{
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception ignore){}
			
			SSConnectionPool.handleSqlException(e, "updating persisted health item: " + ob.getIp() + ":" + ob.getPort());
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseStatement(updateHealthItemStmt);
            updateHealthItemStmt = null;
        	SSConnectionPool.getInstance().releaseStatement(deleteHealthItemFieldsStmt);
            deleteHealthItemFieldsStmt = null;
        	SSConnectionPool.getInstance().releaseStatement(insertHealthItemFieldsStmt);
            insertHealthItemFieldsStmt = null;
        	
        	try {
	            if (conn != null) 
	            {       
	                	conn.setAutoCommit(autoCommit);
	                    conn.setTransactionIsolation(transactionIsolation);
	                }
	            }
        	catch (Exception ignore) {}
        	
        	SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Inside transaction: " + timeInTransaction + "msec. Query: " 
            			+ query + ". Followed by a delete and insert of health item dynamic fields.");
            }
        }
	}

	/*
	 * Deletes the item and it slave, if found.
	 */
	public boolean delete(HealthItemDto ob)
	{
		if (ob == null || ob.getId() == null)
			return false;
		
		Connection conn = null;
		PreparedStatement stmtFindAllInHaGroup = null;
		PreparedStatement stmtDeletePerfMetrics = null;
		PreparedStatement stmtDeleteDynamicFields = null;
		PreparedStatement stmtDeleteHealthItems = null;
		ResultSet rs = null;
		
		int transactionIsolation = 0;
		boolean autoCommit = false;
		
		long begin = System.currentTimeMillis();
		long timeInTransaction = -1;
		
		try
		{
			//TODO add cascading delete to all auxiliary tables of HealthItem table. This will perform better and will eliminate the need for transaction.
			
			conn = SSConnectionPool.getInstance().getConnection();
			
			/*
			 * When deleting xbrc master, make sure to delete the slave as well, if present.
			 */
			Collection<Long> hagroup = new ArrayList<Long>();
			if (ob instanceof XbrcDto)
			{
				XbrcDto xbrc = (XbrcDto)ob;
				
				if (Boolean.parseBoolean(xbrc.getHaEnabled()))
				{	
					stmtFindAllInHaGroup = SSConnectionPool.getInstance().getPreparedStatement(conn, "SELECT hif1.healthItemId AS hiID FROM HealthItemField AS hif1 JOIN HealthItemField AS hif2 (NOLOCK) ON hif1.value = hif2.value WHERE hif2.id = 'FacilityID' AND hif2.value = ?");
					
					stmtFindAllInHaGroup.clearParameters();
					stmtFindAllInHaGroup.setString(1,xbrc.getFacilityId());
					stmtFindAllInHaGroup.execute();
					rs = stmtFindAllInHaGroup.getResultSet();

					while(rs.next())
					{
						hagroup.add(rs.getLong("hiID"));
					}
				}
			}
			
			StringBuffer deletePerformanceMetricQuery = new StringBuffer("DELETE FROM PerformanceMetric WHERE HealthItemID IN (");
			StringBuffer deleteDynamicFieldsQuery = new StringBuffer("DELETE FROM HealthItemField WHERE healthItemId IN (");
			StringBuffer deleteHealthItemsQuery = new StringBuffer("DELETE FROM HealthItem WHERE id IN (");
			
			// prepare all the queries in one go
			if (hagroup.size() == 0){
				deletePerformanceMetricQuery.append("?");
				deleteDynamicFieldsQuery.append("?");
				deleteHealthItemsQuery.append("?");
			} else {
				for (int i = 0; i < hagroup.size(); i++){
					if (i > 0){
						deletePerformanceMetricQuery.append(",");
						deleteDynamicFieldsQuery.append(",");
						deleteHealthItemsQuery.append(",");
					}
					deletePerformanceMetricQuery.append("?");
					deleteDynamicFieldsQuery.append("?");
					deleteHealthItemsQuery.append("?");
				}
			}
			deletePerformanceMetricQuery.append(")");
			deleteDynamicFieldsQuery.append(")");
			deleteHealthItemsQuery.append(")");
			
			/*
			 * Prepare all statements
			 */
			stmtDeletePerfMetrics = SSConnectionPool.getInstance().getPreparedStatement(conn, deletePerformanceMetricQuery.toString());
			stmtDeleteDynamicFields = SSConnectionPool.getInstance().getPreparedStatement(conn, deleteDynamicFieldsQuery.toString());
			stmtDeleteHealthItems = SSConnectionPool.getInstance().getPreparedStatement(conn, deleteHealthItemsQuery.toString());
			
			stmtDeletePerfMetrics.clearParameters();
			stmtDeleteDynamicFields.clearParameters();
			stmtDeleteHealthItems.clearParameters();
			
			if (hagroup.size() == 0){
				stmtDeletePerfMetrics.setInt(1, ob.getId().intValue());
				stmtDeleteDynamicFields.setInt(1, ob.getId().intValue());
				stmtDeleteHealthItems.setInt(1, ob.getId().intValue());
			} else {
				Iterator<Long> iter = hagroup.iterator();
				for (int i = 0; i < hagroup.size(); i++){
					Long current = iter.next();
					stmtDeletePerfMetrics.setLong(i + 1, current);
					stmtDeleteDynamicFields.setLong(i + 1, current);
					stmtDeleteHealthItems.setLong(i + 1, current);
				}
			}
			
			/*
			 * Open transaction
			 */
			transactionIsolation = conn.getTransactionIsolation();
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			long startTransaction = System.currentTimeMillis();
			
			/*
			 * Delete performance data
			 */
			stmtDeletePerfMetrics.execute();
			
			/*
			 * Delete dynamic fields
			 */
			stmtDeleteDynamicFields.execute();
			
			/*
			 * Delete the health items
			 */
			int deleted = stmtDeleteHealthItems.executeUpdate();
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
			
			conn.commit();
			
			timeInTransaction = System.currentTimeMillis() - startTransaction;
			
			return (deleted > 0);
		}
		catch(SQLException e)
		{
			try
			{
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			}
			catch (SQLException e1)
			{
				logger.error("Data corruption occurred while deleting health item " + ob.getIp() + ":" + ob.getPort() + " from the database.");
			}
			
			SSConnectionPool.handleSqlException(e, "Deleting persisted health item id: " + ob.getIp() + ":" + ob.getPort());
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResultSet(rs);
            rs = null;
        	
        	SSConnectionPool.getInstance().releaseStatement(stmtFindAllInHaGroup);
            stmtFindAllInHaGroup = null;
            SSConnectionPool.getInstance().releaseStatement(stmtDeletePerfMetrics);
            stmtDeletePerfMetrics = null;
            SSConnectionPool.getInstance().releaseStatement(stmtDeleteDynamicFields);
            stmtDeleteDynamicFields = null;
            SSConnectionPool.getInstance().releaseStatement(stmtDeleteHealthItems);
            stmtDeleteHealthItems = null;

            if (conn != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transactionIsolation);
                }
                catch (Exception e) {
                }
            }

            SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;

            if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Inside transaction: " + timeInTransaction + "msec. Query: deletes the HealthItem and all associated data.");
            }
        }
		
		return false;
	}
	
	public boolean toogleActiveFlag(Long id, boolean active) throws Exception 
	{
		if (id == null)
			return false;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		String query = "UPDATE HealthItem SET active = ? WHERE id = ? OR id IN (SELECT hif1.healthItemId FROM HealthItemField AS hif1 JOIN HealthItemField AS hif2 ON hif1.value = hif2.value WHERE hif2.id = 'FacilityID' AND hif2.healthItemId = ?)";
		
		long begin = System.currentTimeMillis();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.setBoolean(1, active);
			stmt.setLong(2, id);
			stmt.setLong(3, id);
			int success = stmt.executeUpdate();
			
			XbrmsStatusBo.getInstance().setDbWriteStatus(StatusType.Green, "OK");
			
			return (success > 0);
		}
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Inactivating persisted health item id: " + id);
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt);
            conn = null;
            stmt = null;

        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
		
		return false;
	}

	public <T extends HealthItemDto> T instantiateHealthItem(ResultSet rs) throws SQLException {
		
		T hi = null;
		String className = "";
		
		try
		{
			className = rs.getString("className");

			Class<?> cl = Class.forName(className);
			hi = (T)cl.newInstance();
		} 
		catch (Exception e)
		{
			logger.fatal("Data corruption. Undefined health item type detected: " + className);
			return null;
		}
		
		hi.setId(rs.getLong("id"));
		hi.setName(rs.getString("name"));
		hi.setIp(rs.getString("ip"));
		hi.setPort(rs.getInt("port"));
		hi.setHostname(rs.getString("hostname"));
		hi.setVip(rs.getString("vip"));
		hi.setVport(rs.getInt("vport"));
		hi.setVersion(rs.getString("version"));
		
		HealthItemType type = HealthItemType.getByClassName(className);
		if (type != null)
			hi.setType(type.name());
		
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		hi.setLastDiscovery(rs.getTimestamp("lastDiscovery", cal));
		hi.setNextDiscovery(rs.getTimestamp("nextDiscovery", cal));
		
		hi.setActive(rs.getBoolean("active"));
		hi.setStatus(new HealthStatusDto(rs.getString("status"), rs.getString("statusMessage")));

		return hi;
	}
	
	public void addDynamicHealthItemField(String fieldKey, String fieldValue, HealthItemDto hi) throws SQLException
	{	
		if (fieldKey == null || fieldKey.trim().isEmpty())
			return;
		
		Method m = null;
		try
		{
			m = hi.getClass().getDeclaredMethod("set" + fieldKey, String.class);

			m.setAccessible(true);

			m.invoke(hi, fieldValue);
		}
		catch (NoSuchMethodException e)
		{   
			if(!fieldKey.equalsIgnoreCase("url")) {
				if(logger.isDebugEnabled()) {
					logger.debug("Method set" + fieldKey + "(String value) is not defined on health item type " + hi.getType() + ". This could simply mean that only a getter is necessary for that property, or it might be an error.");
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to initialize variable " + fieldKey + " on health item " 
					+ hi.getIp() + ":" + hi.getPort() + " of type " + hi.getType(), e);
		}
	}
	
	public Map<String,Collection<String>> countFacilityIds()
	{
		Map<String,Collection<String>> countFacilityIds = new HashMap<String,Collection<String>>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String query = "SELECT hi.hostname,hi.ip,hif.value FROM HealthItem AS hi (NOLOCK) LEFT JOIN HealthItemField AS hif (NOLOCK) ON hi.id = hif.healthItemId AND hif.id = 'FacilityId' WHERE hi.active = 1 AND hi.className like '%XbrcDto'";
		
		long begin = System.currentTimeMillis();
		
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
			stmt.clearParameters();
			stmt.execute();
			rs = stmt.getResultSet();
			
			while (rs.next())
			{
				String ip = rs.getString("ip");
				String hostname = rs.getString("hostname");
				String facilityId = rs.getString("value");
				
				Collection<String> current = countFacilityIds.get(facilityId);
				if (current == null)
				{
					current = new ArrayList<String>();
					countFacilityIds.put(facilityId, current);
				}
				
				current.add(ip + "/" + hostname);
			}
			
			XbrmsStatusBo.getInstance().setDbReadStatus(StatusType.Green, "OK");
			
			return countFacilityIds;
		} 
		catch(SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "Counting facility ids for the Data Corruption Detection Job");
			
			return null;
		}
        finally 
        {
        	SSConnectionPool.getInstance().releaseResources(conn, stmt, rs);
            conn = null;
            stmt = null;
            rs = null;
        	
        	if (plogger.isTraceEnabled())
            {
            	long overall = System.currentTimeMillis() - begin;
            	plogger.trace("Overall processing: " + overall + "msec. Query: " + query);
            }
        }
	}
	
	private void persistDynamicHealthItemsFields(PreparedStatement stmt, HealthItemDto ob) throws SQLException
	{
		if (stmt == null || ob == null)
			return;
		
		stmt.clearBatch();

		HealthItem hi = new HealthItem(ob);

		List<HealthItem.HealthFieldInt> healthFields = hi.getFields();

		if (healthFields != null)
		{
			IHealthField metaData = null;
			for (HealthItem.HealthFieldInt hfd : healthFields)
			{
				metaData = hfd.getDesc();

				stmt.setInt(1, ob.getId().intValue());
				stmt.setString(2, hfd.getName());
				stmt.setString(3, hfd.getValue());
				stmt.setString(4, metaData.name());
				stmt.setString(5, metaData.description());
				stmt.setString(6, metaData.type().name());
				stmt.setBoolean(7, Boolean.valueOf(metaData.mandatory()));
				stmt.addBatch();
			}
		}

		stmt.executeBatch();
	}
	
	private HealthItemDao(){}
	
	private static class SingletonHolder
	{
		private final static HealthItemDao INSTANCE = new HealthItemDao();
	}
}
