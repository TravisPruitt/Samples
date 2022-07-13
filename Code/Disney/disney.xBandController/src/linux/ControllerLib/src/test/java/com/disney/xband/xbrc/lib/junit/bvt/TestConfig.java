package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;

public class TestConfig {
	
	String url;
	Properties connProps;
	Properties config;

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
		connProps = new Properties();
		connProps.put("user", config.getProperty("user"));
		connProps.put("password", config.getProperty("password"));
		
		url = "jdbc:mysql://" + config.getProperty("serverName") + "/" + config.getProperty("dbName");
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testReadBulk() {
		Config config = Config.getInstance();
		ControllerInfo controller = new ControllerInfo();
		controller.pass = "test family pass";
		
		//initial test data
		seedData();
				
		//perform read
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, connProps);
			
			config.read(conn, controller);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
        finally {
            if(conn != null) {
    			try {
	    			conn.close();
		    	}
			    catch (SQLException e) {
			    }
            }
		}
		
		//pull data after the read
		ResultSet afterReadData = null;
        Statement stmt = null;
        conn = null;

		try {
			conn = DriverManager.getConnection(url, connProps);
			
			String query = "SELECT class, property, value from Config where class = 'ControllerInfo'";
			stmt = conn.createStatement();
			stmt.execute(query);
			afterReadData = stmt.getResultSet();

			Map<String, String> properties = new HashMap<String, String>();
			while (afterReadData.next())
				properties.put(afterReadData.getString("property").toLowerCase(), afterReadData.getString("value"));
			
			String fieldDBName = null;
			for (Field f: controller.getClass().getDeclaredFields())
			{
				//don't process compiler generated fields
				if (f.isSynthetic())
					continue;
				//don't process fields that should not get persisted
				if (f.getModifiers() == Modifier.TRANSIENT)
					continue;
				
				//use either field's defined name or the annotated version, if provided
				fieldDBName = determineName(f);
				
				assertTrue(properties.containsKey(fieldDBName));
			}
		}
        catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
        finally {
            if(afterReadData != null) {
    			try {
	    			afterReadData.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(stmt != null) {
    			try {
	    			stmt.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(conn != null) {
    			try {
	    			conn.close();
		    	}
			    catch (SQLException e) {
			    }
            }
		}
	}

	@Test
	public void testWriteBulk() {
		Config config = Config.getInstance();
		ControllerInfo controller = new ControllerInfo();
			
		//test data
		long testCmsecJMSRetry = 5L;
		String testPass = "test pass";
		boolean testPushMode = false;
		String testVenueName = "test xCoaster";
		
		//perform read
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, connProps);
			
			//change a few fields to test data
			controller.cmsecJMSRetry = testCmsecJMSRetry;
			controller.pass = testPass;
			controller.pushMode = testPushMode;
			controller.venueName = testVenueName;
			
			config.write(conn, controller);

		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		} finally {
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
			}
		}
		
		//pull data after the write
		ResultSet afterWriteData = null;
        conn = null;
        Statement stmt = null;

		try {
			conn = DriverManager.getConnection(url, connProps);
			
			String query = "SELECT class, property, value from Config where class = 'ControllerInfo'";
			stmt = conn.createStatement();
			stmt.execute(query);
			afterWriteData = stmt.getResultSet();
		
			//hash property/value pairs for O(1) lookups
			Map<String, String> properties = new HashMap<String, String>();
			while (afterWriteData.next())
				properties.put(afterWriteData.getString("property").toLowerCase(), afterWriteData.getString("value"));
			
			String fieldDBName = null;
			Method method = null;
			Object value = null;
			String methodPrefix = null;
			for (Field f: controller.getClass().getDeclaredFields())
			{	
				//don't process compiler generated fields
				if (f.isSynthetic())
					continue;
				//fields that should not get persisted
				if (f.getModifiers() == Modifier.TRANSIENT)
					continue;
				
				fieldDBName = determineName(f);
				
				if (f.getName().toLowerCase().equals("cmsecJMSRetry".toLowerCase()))
					assertEquals(String.valueOf(testCmsecJMSRetry), properties.get(fieldDBName));
				else if (f.getName().toLowerCase().equals("pass"))
					assertEquals(String.valueOf(testPass), properties.get(fieldDBName));
				else if (f.getName().toLowerCase().equals("pushMode".toLowerCase()))
					assertEquals(String.valueOf(testPushMode), properties.get(fieldDBName));
				else if (f.getName().toLowerCase().equals("venueName".toLowerCase()))
					assertEquals(String.valueOf(testVenueName), properties.get(fieldDBName));
				else {
					//the rest of the controller's state should not have changed
					methodPrefix = f.getType() == boolean.class ? "is" : "get";
					
					method = controller.getClass().getMethod(
							//to create a getter's name out of a field name the first letter of the field name must be upper cased
							methodPrefix + String.valueOf(f.getName().charAt(0)).toUpperCase() + f.getName().substring(1));
					value = method.invoke(controller);
					
					assertEquals(value == null ? "" : String.valueOf(value), properties.get(fieldDBName));
				}
			}
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
        finally {
            if(afterWriteData != null) {
    			try {
	    			afterWriteData.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(stmt != null) {
    			try {
	    			stmt.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(conn != null) {
    			try {
	    			conn.close();
		    	}
			    catch (SQLException e) {
			    }
            }
		}
	}
	
	@Test
	public void testReadSingle() {
		Config configObj = Config.getInstance();
		ESBInfo esbInfoObj = new ESBInfo();
		ControllerInfo controller = new ControllerInfo();
		controller.pass = "test family pass";
		
		//initial test data
		seedData();
				
		/*
		 * Property we are trying to read doesn't exist on the object provided.
		 */
		String property = "jmstopic";
		Connection conn= null;
		try {
			conn = DriverManager.getConnection(url, connProps);
			
			configObj.read(conn, controller, "jmstopic");
			
			fail("Property " + esbInfoObj.getClass().getName() + "." + property	+ " shouldn't have been persisted, since doesn't make sense.");
			
		} catch(Exception e) { 
		}
		finally {
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
			}
		}
	}
	
	@Test
	public void testReadSingle1() {
		Config configObj = Config.getInstance();
		ESBInfo esbInfoObj = new ESBInfo();
		ControllerInfo controller = new ControllerInfo();
		controller.pass = "test family pass";
		
		//initial test data
		seedData();
		
		/*
		 * Successful read of a property that has not yet been persisted.
		 */
		String property = "notPersisted";
		PreparedStatement stmt = null;
		Connection conn = null;
        ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(url, connProps);
			
			stmt = conn.prepareStatement("SELECT class, property, value from Config where class = ? AND property = ?");
			stmt.clearParameters();
			stmt.setString(1, "ESBInfo");
			stmt.setString(2, property.toLowerCase());
			stmt.execute();
			rs = stmt.getResultSet();
			if (rs != null && rs.next())
			{
				fail("Corrupted test data. ESBInfo.notpersisted is not supposed to exist at this point.");
			}
			
			configObj.read(conn, esbInfoObj, property);
			
			stmt = conn.prepareStatement("SELECT class, property, value from Config where class = ? AND property = ?");
			stmt.clearParameters();
			stmt.setString(1, "ESBInfo");
			stmt.setString(2, property.toLowerCase());
			stmt.execute();
			rs = stmt.getResultSet();
			if (rs != null && rs.next()) {
				// the read is supposed to persist missing properties if they are defined on the object
				assertTrue(rs.getString("property").equals(property.toLowerCase()));
				assertTrue(rs.getString("value").equals(String.valueOf(esbInfoObj.getNotPersisted())));
			} else {
				fail();
			}
		} catch (Exception e) {
			//fail(e.getLocalizedMessage());
		}
        finally {
            if(rs != null) {
    			try {
	    			rs.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(stmt != null) {
    			try {
	    			stmt.close();
		    	}
			    catch (SQLException e) {
			    }
            }

            if(conn != null) {
    			try {
	    			conn.close();
		    	}
			    catch (SQLException e) {
			    }
            }
		}
	}
	
	@Test
	public void testList() throws SQLException, IllegalArgumentException, IllegalAccessException
	{
		Config configObj = Config.getInstance();
		ListTest listTest = new ListTest();Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, connProps);
			configObj.read(conn, listTest);
			assertEquals(listTest.getSomeList().size(), 3);
		}
		finally
		{
			conn.close();
		}
	}
	
	private void seedData(){
		
		int transactionIsolation = -1;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, connProps);
			
			transactionIsolation = conn.getTransactionIsolation();
			
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			//recreate the table
			String query = "DROP TABLE IF EXISTS `Config`;";
			executeQuery(conn, query);
			
			query = " CREATE TABLE `Config` (" +
					" `class` varchar(64) NOT NULL," +
					" `property` varchar(32) NOT NULL," +
					" `value` varchar(1024) NOT NULL," +
					" PRIMARY KEY (`class`, `property`)" +
					" ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Scalar configuration parameters for controller';";
			executeQuery(conn, query);

			query = "LOCK TABLES `Config` WRITE;";
			executeQuery(conn, query);
			
			/*
			 * the 'notPersisted' field of ESBInfo should not be persisted.
			 */
			
			query = " INSERT INTO `Config` VALUES" +
					"('ControllerInfo','url','" + config.getProperty("url") + "')," +
					"('ControllerInfo','pushmode','" + config.getProperty("pushmode") + "')," +
					"('ControllerInfo','verbose','" + config.getProperty("verbose") + "')," +
					"('ControllerInfo','eventdumpfile','" + config.getProperty("eventdumpfile") + "')," +
					"('ControllerInfo','venue','" + config.getProperty("venue") + "')," +
					"('ControllerInfo','list','" + config.getProperty("list") + "')," +
					"('ESBInfo','jmstopic','" + config.getProperty("jmstopic") + "')," +
					"('ESBInfo','jmspassword','" + config.getProperty("jmspassword") + "')," +
					"('ESBInfo','jmsuser','" + config.getProperty("jmsuser") + "')," +
					"('ESBInfo','jmsbroker','" + config.getProperty("jmsvroker") + "')," +
					"('ESBInfo','list', '" + config.getProperty("list") + "')," +
					"('ControllerInfo','onridetimeout','" + config.getProperty("onridetimeout") + "')," +
					"('ControllerInfo','abandonmenttimeout','" + config.getProperty("abandonmenttimeout") + "');";
			executeQuery(conn, query);

			conn.commit();
		} catch (Exception e){
			try {
				fail(e.getLocalizedMessage());
				conn.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			try
			{
				conn.setTransactionIsolation(transactionIsolation);
				conn.setAutoCommit(true);
				conn.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void executeQuery(Connection conn, String query) throws SQLException {
		Statement stmt = null;

        try {
            stmt = conn.createStatement();
		    stmt.execute(query);
        }
        finally {
            if(stmt != null) {
    			try {
	    			stmt.close();
		    	}
			    catch (SQLException e) {
			    }
            }
		}
	}
	
	private String determineName(Field f) 
	{
		String fieldDBName = null;
		if (f.isAnnotationPresent(PersistName.class))
			//field's name and DB name don't match, using annotated name
			fieldDBName = f.getAnnotation(PersistName.class).value().toLowerCase();
		else 
			fieldDBName = f.getName().toLowerCase();
		
		return fieldDBName;
	}
	
	@PersistName("ListTest")
	class ListTest extends Configuration
	{
		@PersistName("someList")
		@MetaData(name="someList", defaultValue="one|two|three",  description="List description")
		List<String> someList;

		public List<String> getSomeList()
		{
			return someList;
		}

		public void setSomeList(List<String> someList)
		{
			this.someList = someList;
		}

		@Override
		protected void initHook(Connection conn)
		{
		}
	}
	
	@PersistName("ControllerInfo")
	class ControllerInfo 
	{	
		@PersistName("venue")
		String venueName;
		String url;
		@PersistName("pushmode")
		boolean pushMode;
		String user;
		String pass;
		boolean verbose;
		@PersistName("eventDumpFile")
		String eventDumpFile;
		long cmsecJMSRetry;
		@PersistName("abandonmenttimeout")
		long csecAbandonmentTimeout;
		@PersistName("onridetimeout")
		long csecOnRideTimeout;
		@PersistName("list")
		private List<String> list;
		
		public String getVenueName() {
			return venueName;
		}
		public void setVenueName(String venueName) {
			this.venueName = venueName;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public boolean isPushMode() {
			return pushMode;
		}
		public void setPushMode(boolean pushMode) {
			this.pushMode = pushMode;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPass() {
			return pass;
		}
		public void setPass(String pass) {
			this.pass = pass;
		}
		public boolean isVerbose() {
			return verbose;
		}
		public void setVerbose(boolean verbose) {
			this.verbose = verbose;
		}
		public String getEventDumpFile() {
			return eventDumpFile;
		}
		public void setEventDumpFile(String eventDumpFile) {
			this.eventDumpFile = eventDumpFile;
		}
		public long getCmsecJMSRetry() {
			return cmsecJMSRetry;
		}
		public void setCmsecJMSRetry(long cmsecJMSRetry) {
			this.cmsecJMSRetry = cmsecJMSRetry;
		}
		public long getCsecAbandonmentTimeout() {
			return csecAbandonmentTimeout;
		}
		public void setCsecAbandonmentTimeout(long csecAbandonmentTimeout) {
			this.csecAbandonmentTimeout = csecAbandonmentTimeout;
		}
		public long getCsecOnRideTimeout() {
			return csecOnRideTimeout;
		}
		public void setCsecOnRideTimeout(long csecOnRideTimeout) {
			this.csecOnRideTimeout = csecOnRideTimeout;
		}
		public List<String> getList()
		{
			return list;
		}
		public void setList(List<String> list)
		{
			this.list = list;
		}
	}
	
	@PersistName("ESBInfo")
	public class ESBInfo
	{
		@PersistName("jmsbroker")
		private String sJMSBroker;
		@PersistName("jmsuser")
		private String sJMSUser;
		@PersistName("jmspassword")
		private String sJMSPassword;
		@PersistName("jmstopic")
		private String sJMSTopic;
		@PersistName("list")
		private List<String> list;
		private int notPersisted = 100;
		
		public String getsJMSBroker() {
			return sJMSBroker;
		}
		public void setsJMSBroker(String sJMSBroker) {
			this.sJMSBroker = sJMSBroker;
		}
		public String getsJMSUser() {
			return sJMSUser;
		}
		public void setsJMSUser(String sJMSUser) {
			this.sJMSUser = sJMSUser;
		}
		public String getsJMSPassword() {
			return sJMSPassword;
		}
		public void setsJMSPassword(String sJMSPassword) {
			this.sJMSPassword = sJMSPassword;
		}
		public String getsJMSTopic() {
			return sJMSTopic;
		}
		public void setsJMSTopic(String sJMSTopic) {
			this.sJMSTopic = sJMSTopic;
		}
		public void setNotPersisted(int notPersisted) {
			this.notPersisted = notPersisted;
		}
		public int getNotPersisted(){
			return notPersisted;
		}
		public List<String> getList()
		{
			return list;
		}
		public void setList(List<String> list)
		{
			this.list = list;
		}
	}
}
