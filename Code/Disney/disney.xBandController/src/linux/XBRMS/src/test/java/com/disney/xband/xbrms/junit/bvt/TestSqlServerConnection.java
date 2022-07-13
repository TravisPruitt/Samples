package com.disney.xband.xbrms.junit.bvt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.mchange.v2.c3p0.DataSources;

public class TestSqlServerConnection {
	
	Connection conn;
	Properties config;
	DataSource pooled;

	@Before
	public void setUp() throws Exception {
		//read configuration
		config = new Properties();
        InputStream is = null;

        try {
            is = getClass().getResourceAsStream("test-bvt-config.properties");
            config.load(is);
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch(Exception ignore) {
                }
            }
        }

		//open database connection
		Class.forName(config.getProperty("driver"));
//		SQLServerDataSource ds = new SQLServerDataSource();
//        ds.setUser(config.getProperty("user"));
//        ds.setPassword(config.getProperty("password"));
//        ds.setServerName("10.75.2.218");
//        ds.setPortNumber(1433); 
//        ds.setDatabaseName(config.getProperty("sql.db.name"));
//        conn = ds.getConnection();
		
		String url = config.getProperty("sql.db.url") + "/" + config.getProperty("sql.db.name");
		String user = config.getProperty("user");
		String password = config.getProperty("password");
		
//		Class.forName(config.getProperty("driver"));
//		Properties connProps = new Properties();
//		connProps.put("user", user);
//		connProps.put("password", password);
//		
//		conn = DriverManager.getConnection(url, connProps);
//		
		
		DataSource unpooled = DataSources.unpooledDataSource(url,
				user,
				password);

		HashMap<String,String> c3p0config = new HashMap<String,String>();

		c3p0config.put("maxPoolSize", "20");
		c3p0config.put("maxStatements", "50");
		c3p0config.put("maxStatementsPerConnection", "400");
		c3p0config.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.log4j.Log4jMLog");

		pooled = DataSources.pooledDataSource( unpooled, c3p0config );
	}

	@After
	public void tearDown() throws Exception {
		if (conn != null)
			conn.close();
		
		System.out.println("Closed connection.");
	}

	@Test
	public void testRead() {
		
		String query = "SELECT * from config";
		PreparedStatement stmt;
		try
		{
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			
			//hash property/value pairs for O(1) lookups
			Map<String, String> properties = new HashMap<String, String>();
			while (rs.next())
				properties.put(rs.getString("property").toLowerCase(), rs.getString("value"));
			
			assertTrue(properties.size() > 0);
		}
		catch (SQLException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParametrizedQuery() {
		
		String query = "SELECT EventId, GuestID, ReaderLocation from rdr.Event where GuestID = ? and ReaderLocation = ?";
		PreparedStatement stmt;
		try
		{
			stmt = conn.prepareStatement(query);
			stmt.clearParameters();
			stmt.setString(1, "1658");
			stmt.setString(2, "ENTRY");
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			
			int count = 0;
			while (rs.next())
			{
				System.out.println(rs.getLong("EventId") + ", " + rs.getString("GuestID") + "," + rs.getString("ReaderLocation"));
				count++;
			}

			assertTrue(count > 0);
		}
		catch (SQLException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testJoin() {
		
		StringBuffer query = new StringBuffer("SELECT f.FacilityName, e.ReaderLocation, e.Timestamp, et.EventTypeName")
			.append(" ").append("FROM rdr.Event AS e, rdr.Facility AS f, rdr.EventType as et")
			.append(" ").append("WHERE e.FacilityID = f.FacilityID and et.EventTypeID = e.EventTypeID and e.GuestID = ? and e.Timestamp > ?");
	
		PreparedStatement stmt;
		try
		{
			stmt = conn.prepareStatement(query.toString());
			stmt.clearParameters();
			stmt.setString(1, "1658");
			
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.DAY_OF_MONTH, 26);
			cal.set(Calendar.MONTH, 0);
			
			stmt.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
			
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			
			int count = 0;
			while (rs.next())
			{
				System.out.println(rs.getLong("FacilityName") + ", " + rs.getString("ReaderLocation") + 
						"," + rs.getString("Timestamp") + "," + rs.getString("EventTypeName"));
				count++;
			}

			assertTrue(count > 0);
		}
		catch (SQLException e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPreparedStatementLeak() {
		
		String query = "SELECT * from schema_version WHERE script_name = ?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try
		{
			for (int i = 0; i < 20; i++)
			{
				try
				{
					System.out.println("Getting connection " + 1);
					conn = pooled.getConnection();
					
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					conn.setAutoCommit(false);
					
					int count = 0;
					while (count < 100000)
					{
						stmt = conn.prepareStatement(query);
						stmt.clearParameters();
						stmt.setString(1, "xbrms_1.6.0.0001.sql");
						stmt.execute();
						rs = stmt.getResultSet();
						
						System.out.println(stmt.toString() + " -- count: " + ++count);
					}
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					fail(e.getMessage());
				}
				finally
				{
					if (rs != null)
					{
						try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
					}
					if (stmt != null)
					{
						try { 
							stmt.close(); 
							System.out.println("Closed statement " + stmt.toString());
						} catch (Exception e) { e.printStackTrace(); }
					}
					
					if (conn != null)
					{
						conn.setAutoCommit(true);
						
						try { conn.close(); } catch (Exception e) { e.printStackTrace(); }
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPreparedStatementUsedUp() {
		
		String query = "SELECT * from schema_version WHERE script_name = ?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn1 = null;
		Connection conn2 = null;
		
		try
		{
			try
			{
				conn1 = pooled.getConnection();
				
				int i = 0;
				while (i++ < 400)
				{
					stmt = conn1.prepareStatement(query);
					stmt.clearParameters();
					stmt.setString(1, "xbrms_1.6.0.0001.sql");
					stmt.execute();
					rs = stmt.getResultSet();
				}
				
				System.out.println("Used up " + (i - 1) + " prepared statements.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail(e.getMessage());
			}
			finally
			{
				// do not release the connection, therefore statements don't get released?
			}
			
			try
			{
				conn2 = pooled.getConnection();
				
				System.out.println("Requesting one more statement ...");
				
				stmt = conn2.prepareStatement(query);
				stmt.clearParameters();
				stmt.setString(1, "xbrms_1.6.0.0001.sql");
				stmt.execute();
				rs = stmt.getResultSet();
				
				System.out.println("Request didn't lock.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail(e.getMessage());
			}
			finally
			{
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		finally
		{
			if (rs != null)
			{
				try { rs.close(); } catch (Exception e) { e.printStackTrace(); }
			}
			if (stmt != null)
			{
				try { 
					stmt.close(); 
					System.out.println("Closed statement " + stmt.toString());
				} catch (Exception e) { e.printStackTrace(); }
			}
			
			if (conn1 != null)
			{
				try { conn1.close(); } catch (Exception e) { e.printStackTrace(); }
			}
			if (conn2 != null)
			{
				try { conn2.close(); } catch (Exception e) { e.printStackTrace(); }
			}
		}
	}
}
