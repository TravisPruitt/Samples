package com.disney.xband.xbrc.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.common.lib.security.InputValidator;

public class Config
{
	private static Map<String, String> mapChanges = new Hashtable<String, String>();
	private static boolean bForce = false;
	private static boolean bNoUpdate = false;
	private static boolean bStartCalibrate = false;
	private static boolean bEndCalibrate = false;
	private static Properties prop = null;
	private static Pattern scriptPattern = Pattern
			.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+).sql");
    private static String user;
    private static String passwd;
    private static final String DBNAME = "Mayhem";
	
	private static class DatabaseInfo
	{
		String database = "Mayhem";
		String rootUser = null;
		String rootPassword = null;
		String newRootPassword = null;
		String user = null;
		String password = null;
		String viewUser = null;
		String viewUserPassword = null;
		
		String model = null;
		String configModel = null;
		String newModel = null; 
		
		boolean databaseExists = false;
		int tableCount = 0;
		String schemaVersion = null;
		boolean changeRootPassword = false;
		boolean createUser = false;
		boolean setUserPassword = false;
		boolean createViewUser = false;
		boolean setViewUserPassword = false;
		
		TreeMap<String,String> scriptsMap = new TreeMap<String,String>();
	}

	// alternate entry point for command-line config
	public static void main(String[] args) throws Exception
	{
        final List<String> argsL = new ArrayList<String>();

        for (int i = 0; i < args.length; i++)
        {
            String s = args[i];

            if (s.compareTo("--creds") == 0) {
                if(((i + 1) >= args.length) || (args[i + 1].indexOf(":") < 0) || (args[i + 1]).startsWith("--")) {
                    System.err.println("Option --creds must be followed by user credentials <username>:<password>" + s);
                    showUsage();
                    System.exit(1);
                }
                else {
                    String[] creds = args[i + 1].split("[:]");
                    user = creds[0];
                    passwd = creds[1];
                    ++i;
                }
            }
        }

		// parse args
		for (int i = 0; i < args.length; i++)
		{
			String s = args[i];
			
			if (s.compareTo("--help") == 0)
				showUsage();
			else if (s.compareTo("--calibrate")==0)
			{
				bStartCalibrate = true;
				bEndCalibrate = false;
			}
			else if (s.compareTo("--endcalibrate")==0)
			{
				bStartCalibrate = false;
				bEndCalibrate = true;
			}
			else if (s.compareTo("--list") == 0)
				showConfig();
			else if (s.startsWith("--list:"))
				showConfig(s.substring("--list:".length()));
			else if (s.startsWith("--remove:"))
				removeProperty(s.substring("--remove:".length()));
			else if (s.matches(".+=.*"))
				setConfig(s);
			else if (s.startsWith("--createdb"))
				createDB();
			else if (s.compareTo("--force") == 0)
				bForce = true;
			else if (s.compareTo("--version") == 0)
				showVersion();
			else if (s.compareTo("--testdb") == 0)
				checkDB();
			else if (s.compareTo("--updateconfig") == 0)
				reloadConfig();
			else if (s.compareTo("--clearcache") == 0)
				clearCache();
			else if (s.compareTo("--noupdate") == 0)
				bNoUpdate = true;
			else if (s.compareTo("--clearguests") == 0)
				clearGuests();
			else if (s.compareTo("--encrypt") == 0 && args.length > (i+2))
			{
				encrypt(Arrays.copyOfRange(args, i+1, args.length));
				i+= 2;
			}
			else
			{
				System.err.println("Unknown option: " + s);
				showUsage();
				System.exit(1);
			}
		}

		// if there's any changes, commit them now
		if (!mapChanges.isEmpty())
			commitChanges();
		
		// handle calibration
		if (bStartCalibrate)
			sendPut("http://localhost:8080/calibrate/start", "", 5000);
		if (bEndCalibrate)
			sendPut("http://localhost:8080/calibrate/end", "", 5000);
		
		System.exit(0);
	}

	private static void commitChanges() throws Exception
	{
		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();

		if (!confirm("Set property(-ies)?"))
			return;

		Connection dbConn = null;

		try
		{
            dbConn = Controller.getInstance().getPooledConnection();

            if (dbConn == null)
                return;

			int cTotalChanges = 0;
			for (String s : mapChanges.keySet())
			{
				// is there a class in the key?
				String sClass = null;
				String sProperty = s;
				int iPos = s.indexOf('.');
				if (iPos > 0 && iPos < (s.length() - 1))
				{
					// yes
					sClass = s.substring(0, iPos);
					sProperty = s.substring(iPos + 1);
					cTotalChanges += setProperty(dbConn, sClass, sProperty,
							mapChanges.get(s));
				}
				else
				{
					System.err
							.println("Properties must be of the form CLASS.NAME");
				}
			}
			if (cTotalChanges == 0)
				System.out.println("No property value changes were made");
			else
			{
				System.out.println("Changed " + cTotalChanges
						+ " property values");
				if (bNoUpdate)
					System.out
							.println("Database changed but xBRC configuration NOT updated");
				else
					reloadConfig();
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			System.err.println(e);
		}
		finally
		{
			if (dbConn != null) {
                try {
				    Controller.getInstance().releasePooledConnection(dbConn);
                }
                catch (Exception ignore) {
                }
            }
		}
	}

	private static int setProperty(Connection dbConn, String sClass,
			String sProperty, String sValue) throws SQLException
	{
        PreparedStatement ps = null;

        // first, try an update
        try {
            ps = dbConn.prepareStatement("UPDATE Config SET value=? WHERE property=? AND class=?");
            ps.setString(1, sValue);
            ps.setString(2, sProperty);
            ps.setString(3, sClass);
            int cRows = ps.executeUpdate();

            if (cRows == 0) {
                // failed, gotta do an insert
                ps.close();
                ps = dbConn
                        .prepareStatement("INSERT INTO Config(class,property,value) VALUES (?,?,?)");
                ps.setString(1, sClass);
                ps.setString(2, sProperty);
                ps.setString(3, sValue);
                cRows = ps.executeUpdate();
            }
            return cRows;
        }
        finally {
            if(ps != null) {
                try {
                    ps.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static void setConfig(String s)
	{
		// break up the property and value
		int iPos = s.indexOf('=');

		String sProperty = s.substring(0, iPos);
		String sValue = s.substring(iPos + 1);

		// validate that the property has a class
		iPos = sProperty.indexOf('.');
		if (iPos < 1 || iPos > sProperty.length() - 2)
		{
			System.err.println("Properties must be of the form CLASS.NAME");
			return;
		}

		// add to the map
		mapChanges.put(sProperty, sValue);
	}

	private static void showConfig() throws Exception
	{
		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();

		Connection dbConn = null;
        Statement stmt = null;
        ResultSet rs = null;

		try
		{
            dbConn = Controller.getInstance().getPooledConnection();

            if (dbConn == null)
                return;

			stmt = dbConn.createStatement();
			stmt.execute("SELECT * from Config");
			rs = stmt.getResultSet();

			while (rs.next())
			{
				String sClass = rs.getString("class");
				String sProperty = rs.getString("property");
				String sValue = rs.getString("value");

				System.out.println(formatProperty(sClass, sProperty) + "="
						+ sValue);
			}
		}
		catch (SQLException e)
		{
			System.err.println(e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                }
            }

            if (dbConn != null) {
                try {
                    Controller.getInstance().releasePooledConnection(dbConn);
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static void showVersion()
	{
		String sVersion = Config.class.getPackage().getImplementationTitle();
		System.out.println("Disney/Synapse xBRC version: " + sVersion);
	}

	private static void checkDB()
	{
		try
		{
			// this does a database read, throwing exceptions as needed
			ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
		}
		catch (Exception e)
		{
			System.out.println("xBRC database is not present or valid");
			System.exit(1);
		}

	}

	private static void showConfig(String sProp) throws Exception
	{
		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();

		String sClass = null;
		String sProperty = sProp;
		int iPos = sProp.indexOf('.');
		if (iPos > 0 && iPos < (sProp.length() - 1))
		{
			// yes
			sClass = sProp.substring(0, iPos);
			sProperty = sProp.substring(iPos + 1);
		}

        Connection dbConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

		try
		{
            dbConn = Controller.getInstance().getPooledConnection();

            if (dbConn == null)
                return;

			if (sClass == null)
			{
				ps = dbConn.prepareStatement("SELECT * from Config WHERE property=?");
				ps.setString(1, sProperty);
			}
			else
			{
				ps = dbConn.prepareStatement("SELECT * from Config WHERE property=? AND class=?");
				ps.setString(1, sProperty);
				ps.setString(2, sClass);
			}

			ps.execute();
			rs = ps.getResultSet();
			while (rs.next())
			{
				sClass = rs.getString("class");
				sProperty = rs.getString("property");
				String sValue = rs.getString("value");

				System.out.println(formatProperty(sClass, sProperty) + "="
						+ sValue);
			}
		}
		catch (SQLException e)
		{
			System.err.println(e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }

            if (dbConn != null) {
                try {
                    Controller.getInstance().releasePooledConnection(dbConn);
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static void removeProperty(String s) throws Exception
	{
		if (!confirm("Remove property(-ies)?"))
			return;

		ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
		Connection dbConn = null;

		try
		{
			dbConn = Controller.getInstance().getPooledConnection();
			if (dbConn == null)
				return;

			// see if there's a class
			String sClass = null;
			String sProperty = s;
			int iPos = s.indexOf('.');
			int cRows = 0;
			if (iPos > 0 && iPos < s.length() - 1)
			{
				sClass = s.substring(0, iPos);
				sProperty = s.substring(iPos + 1);
				cRows = deleteProp(dbConn, sClass, sProperty);
			}
			else
			{
				cRows = deleteProp(dbConn, sProperty);
			}
			if (cRows == 0)
				System.out.println("The property, "
						+ formatProperty(sClass, sProperty) + " was not found");
			else
			{
				System.out.println("Removed " + cRows + " properties");
				if (bNoUpdate)
					System.out
							.println("Database changed but xBRC configuration NOT updated");
				else
					reloadConfig();
			}
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
        finally
        {
            if (dbConn != null) {
                try {
                    Controller.getInstance().releasePooledConnection(dbConn);
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static String formatProperty(String sClass, String sProperty)
	{
		return (sClass == null ? "" : (sClass + ".")) + sProperty;
	}

	private static int deleteProp(Connection dbConn, String sProperty)
			throws SQLException
	{
		PreparedStatement ps = null;

        try {
            ps = dbConn.prepareStatement("DELETE FROM Config WHERE property=?");
		    ps.setString(1, sProperty);
		    int n = ps.executeUpdate();
		    return n;
        }
        finally
        {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}

	private static int deleteProp(Connection dbConn, String sClass,
			String sProperty) throws SQLException
	{
		PreparedStatement ps = null;

        try {
            ps = dbConn.prepareStatement("DELETE FROM Config WHERE property=? AND class=?");
		    ps.setString(1, sProperty);
		    ps.setString(2, sClass);
		    int n = ps.executeUpdate();
		    return n;
        }
        finally
        {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}

	private static void showUsage()
	{
		System.out.println("USAGE: xbrcconfig OPTIONS [CLASS.PROP=VALUE]*");
		System.out.println("Where OPTIONS:");
        System.out
                .println("    --creds <user>:<passwd> Connect to xBRC with the following credentials");
		System.out
				.println("    --createdb        	  Creates the xBRC mysql database using settings from /usr/share/xbrc/properties.xml file");
		System.out
				.println("    --testdb                Verifies that the database has been created");
		System.out
				.println("    --list                  Display current configuration settings");
		System.out
				.println("    --list:[CLASS.]PROP     Displays property setting");
		System.out
				.println("    --remove:[CLASS.]PROP   Removes the property");
		System.out
				.println("    --force                 Perform operations without prompting for confirmation");
		System.out
				.println("    --noupdate              Suppresses automatic reload of xBRC configuration (see --updateconfig)");
		System.out
				.println("    --updateconfig          Tells xBRC to reload its configuration information");
		System.out
				.println("    --clearcache            Clears the IDMS lookup cache");
		System.out
				.println("    --clearguests           Clears the GST table");
		System.out
				.println("    --calibrate             Enters xBR calibration mode (lowers signal thresholds)");
		System.out
				.println("    --endcalibrate          Exits xBR calibration mode (resets signal thresholds)");
		System.out
				.println("    --version               Displays xBRC version information");
		System.out
				.println("    --encrypt <key> <text>  Encrypt a string using the given key");
		System.out
				.println("    --help                  Displays this usage information");
		System.out
				.println("If \"CLASS.\" is omitted commands will act on PROP on all classes");
	}
	
	private static Connection tryDbConnection(String sUser, String sPassword, String sDatabase) throws Exception
	{	
		String sConnString = "jdbc:mysql://localhost";
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			if (sDatabase != null)
				sConnString += "/" + sDatabase;
			return DriverManager.getConnection(sConnString, sUser, sPassword);
		}
		catch (SQLException e)
		{
			return null;
		}
		catch(Exception e)
		{
			throw new Exception("Unable to connect using " + sUser + " user to " + sConnString + ": " + 
								e.getLocalizedMessage(), e);
		}
	}
	
/*
	private static Connection openRawDBConnection(String sUser, String sPassword)
	{
		Connection dbConn = null;
		String sConnString = "jdbc:mysql://localhost";

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			sConnString = "jdbc:mysql://localhost";
			dbConn = DriverManager.getConnection(sConnString, sUser, sPassword);
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Unable to load (2) jdbc driver: "
					+ e.getLocalizedMessage());
		}
		catch (SQLException e)
		{
			System.err
					.println("Unable to connect with " + sUser + " user to " + sConnString + ": "
							+ e.getLocalizedMessage());
		}
		catch (InstantiationException e)
		{
			System.err.println("Unable to load (2) jdbc driver: "
					+ e.getLocalizedMessage());
		}
		catch (IllegalAccessException e)
		{
			System.err.println("Unable to load (3) jdbc driver: "
					+ e.getLocalizedMessage());
		}

		return dbConn;
	}
	*/
	
	// 
	// Modify user password to a new one.
	//
	private static boolean changeUserPassword(Connection dbConn, String user, String password) throws Exception
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = dbConn.prepareStatement("update mysql.user set password=PASSWORD(?) where User=?");
            stmt.setString(1, password);
            stmt.setString(2, user);

			// now create it
			int cRows = stmt.executeUpdate();
			if (cRows > 0)
			{				
				stmt.executeUpdate("flush privileges");
				System.out.println("The password for user " + user + " has been changed.");
				return true;
			}
			else
			{
				System.out.println("The password for user " + user + " has not been changed. Update query updated 0 rows.");
				return false;
			}
		}			
		catch (SQLException e)
		{
			throw new Exception("Failed to modify the " +  user + " user password: " + 
					e.getLocalizedMessage(), e);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	// 
	// Modify view user password to a new one.
	//
	private static boolean changeViewUserPassword(Connection dbConn, String user, String password) throws Exception
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = dbConn.prepareStatement("update mysql.user set password=PASSWORD(?) where User=?");
            stmt.setString(1, password);
            stmt.setString(2, user);

			// now create it
			int cRows = stmt.executeUpdate();
			if (cRows > 0)
			{				
				stmt.executeUpdate("flush privileges");
				System.out.println("The password for view user " + user + " has been changed.");
				return true;
			}
			else
			{
				System.out.println("The password for view user " + user + " has not been changed. Update query updated 0 rows.");
				return false;
			}
		}			
		catch (SQLException e)
		{
			throw new Exception("Failed to modify the " +  user + " view user password: " + 
					e.getLocalizedMessage(), e);
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	private static void checkDatbaseExists(Connection dbConn, DatabaseInfo dbinfo) throws Exception
	{		
		dbinfo.tableCount = 0;
		dbinfo.databaseExists = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

		try
		{
			ps = dbConn.prepareStatement("show databases");
			ps.execute();
			rs = ps.getResultSet();
			while (rs.next() && !dbinfo.databaseExists)
			{
				String sName = rs.getString(1);
				if (sName.equals(dbinfo.database))
					dbinfo.databaseExists = true;
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			throw new Exception("Failed execute show databases: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

		if (!dbinfo.databaseExists)
			return;

        ps = null;
        rs = null;
        Statement st = null;

		try
		{
			st = dbConn.createStatement();
			st.executeUpdate("use " + dbinfo.database);
		}
		catch(SQLException e)
		{
			throw new Exception("Failed to execute use " + dbinfo.database + ": " + e.getLocalizedMessage(), e);
		}
        finally
        {
            if (st != null) {
                try {
                    st.close();
                }
                catch (Exception e) {
                }
            }
        }
	
		try
		{
			ps = dbConn.prepareStatement("show tables");
			ps.execute();
			rs = ps.getResultSet();
			while (rs.next())
			{
				dbinfo.tableCount++;
			}
		}
		catch (SQLException e)
		{
			throw new Exception("Failed execute show tables: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	private static void getSchemaVersion(Connection dbConn, DatabaseInfo dbinfo) throws Exception
	{		
		boolean found = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

		try
		{
			ps = dbConn.prepareStatement("show tables");
			ps.execute();
			rs = ps.getResultSet();
			while (rs.next() && !found)
			{
				String sName = rs.getString(1);
				if (sName.equals("SchemaVersion"))
					found = true;
			}
		}
		catch (SQLException e)
		{
			throw new Exception("Failed to execute show tables: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        ps = null;
        rs = null;

		if (!found)
			return;
		
		try
		{
			ps = dbConn.prepareStatement("select * from SchemaVersion");
			ps.execute();
			rs = ps.getResultSet();
			while (rs.next())
			{
				dbinfo.schemaVersion = rs.getString("version");
				dbinfo.model = rs.getString("model");
				break; // there should only be one record
			}

		}
		catch (SQLException e)
		{
			throw new Exception("Failed to select from SchemaVersion: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
		
		try
		{
			ps = dbConn.prepareStatement("select value from Config where class = \"ControllerInfo\" and property = \"model\"");
			ps.execute();
			rs = ps.getResultSet();
			while (rs.next())
			{
				dbinfo.configModel = rs.getString("value");
				break; // there should only be one record
			}

		}
		catch (SQLException e)
		{
			throw new Exception("Failed to select model from Config: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	private static void checkIfUserExists(Connection dbConn, DatabaseInfo dbinfo) throws Exception
	{
        PreparedStatement ps = null;
        ResultSet rs = null;

		try
		{
			dbinfo.createUser = true;
			dbinfo.setUserPassword = true;
			
			ps = dbConn.prepareStatement("select * from mysql.user where User = ? and Host = \"localhost\"");
            ps.setString(1, dbinfo.user);
			ps.execute();
			rs = ps.getResultSet();
			dbinfo.createUser = !rs.next();
		}
		catch(SQLException e)
		{
			throw new Exception("Failed to select form the mysql.user table: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

		try
		{
			if (dbinfo.createUser == true)
				return;
			
			ps = dbConn.prepareStatement("select * from mysql.user where User = ? and Host = \"localhost\" and Password = PASSWORD(?)");
            ps.setString(1, dbinfo.user);
            ps.setString(2, dbinfo.password);
			ps.execute();
			rs = ps.getResultSet();
			dbinfo.setUserPassword = !rs.next();
		}
		catch(SQLException e)
		{
			throw new Exception("Failed to check user password in the mysql.user table for user " + 
					dbinfo.user + ": " + e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}

	private static void checkIfViewUserExists(Connection dbConn, DatabaseInfo dbinfo) throws Exception
	{
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (dbinfo.viewUser == null || dbinfo.viewUser == "" || dbinfo.viewUserPassword == null || dbinfo.viewUserPassword == "")
        {
        	dbinfo.createViewUser = false;
        	dbinfo.setViewUserPassword = false;
        	return;
        }
        
		try
		{
			dbinfo.createViewUser = true;
			dbinfo.setViewUserPassword = true;
			
			ps = dbConn.prepareStatement("select * from mysql.user where User = ?");
            ps.setString(1, dbinfo.viewUser);
			ps.execute();
			rs = ps.getResultSet();
			dbinfo.createViewUser = !rs.next();
		}
		catch(SQLException e)
		{
			throw new Exception("Failed to select from the mysql.user table: " + 
					e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

		try
		{
			if (dbinfo.createViewUser == true)
				return;
			
			ps = dbConn.prepareStatement("select * from mysql.user where User = ? and Password = PASSWORD(?)");
            ps.setString(1, dbinfo.viewUser);
            ps.setString(2, dbinfo.viewUserPassword);
			ps.execute();
			rs = ps.getResultSet();
			dbinfo.setViewUserPassword = !rs.next();
		}
		catch(SQLException e)
		{
			throw new Exception("Failed to check view user password in the mysql.user table for user " + 
					dbinfo.viewUser + ": " + e.getLocalizedMessage(), e);
		}
        finally
        {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
	}
	
	private static void processScriptsDirectory(File dir, DatabaseInfo dbinfo) throws IOException
	{
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				// Process directory...
				processScriptsDirectory(file,dbinfo);
			}
			else
			{
				// Process file...
				Matcher m = scriptPattern.matcher(file.getName());
				if (!m.matches())
				{
					if (!file.getName().equals("initdb.sql") && !file.getName().equals("1.0.0.0.sql"))
						System.err.println(file.getName()
								+ " does not look like a valid database script file. Ignoring...");
					continue;
				}

				if (m.groupCount() < 2)
				{
					System.err.println(file.getName()
							+ " does not look like a valid database script file. Ignoring...");
					continue;
				}

				String sVersion = m.group(1);
				dbinfo.scriptsMap.put(sVersion, file.getCanonicalPath());
			}
		}
	}
	
	private static void parseModelNameFromScripts(String scriptsDirectory, DatabaseInfo dbinfo)
	{
		//reading file line by line in Java using BufferedReader      
        FileInputStream fis = null;
        BufferedReader reader = null;
        String filePath = scriptsDirectory + "/1.0.0.0.sql";
     
        try 
        {
            fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis));
         
            String line = reader.readLine();
            while(line != null)
            {
            	int iPos = line.indexOf("com.disney.xband.xbrc.");
            	if (iPos >= 0)
            	{
            		int iEnd = line.indexOf('\'', iPos);
            		if (iEnd > iPos)
            		{
            			dbinfo.newModel = line.substring(iPos, iEnd);
            			break;
            		}
            	}
                line = reader.readLine();
            }            
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("Caught FileNotFoundException exception in parseModelNameFromScripts: " + ex.getMessage());
        } 
        catch (IOException ex) 
        {
        	System.out.println("Caught IOException exception in parseModelNameFromScripts: " + ex.getMessage());
        } 
        finally 
        {
            try {
            	if (reader != null)
            		reader.close();
            	if (fis != null)
            		fis.close();
            } catch (IOException ex) {               
            }
        }
	}
	
	private static void findDBScripts(String scriptsDirectory, DatabaseInfo dbinfo) throws Exception
	{
		dbinfo.scriptsMap.clear();
		
		try
		{
			File dir = new File(scriptsDirectory);
			if (!dir.exists() && !dir.isDirectory())
			{							
				System.err.println(dir.getCanonicalPath()
						+ " is not a valid directory. Cannot find any database scripts.");
				return;
			}
			
			processScriptsDirectory(dir,dbinfo);
			parseModelNameFromScripts(scriptsDirectory, dbinfo);
		}
		catch(IOException e)
		{
			throw new Exception("Caught IOException while looking for database scripts in " + scriptsDirectory);
		}
	}
	
	private static boolean backupDatabase(DatabaseInfo dbinfo, String destDir)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String backupDbName = destDir + "/" + dbinfo.database + "_" + dateFormatter.format(new Date()) + ".sql";
		String password = dbinfo.changeRootPassword ? dbinfo.newRootPassword : dbinfo.rootPassword;
		// must escape password chars or else the shell may do funny things
		StringBuffer pwd = new StringBuffer();
		for (int i = 0; i < password.length(); i++)
		{
			pwd.append('\\');
			pwd.append(password.charAt(i));
		}
		String command = "mysqldump --user=root --password=" + pwd + " " + dbinfo.database + " > " + backupDbName;
		
		try {
			System.out.println("Backing up the existing " + dbinfo.database + " MySQL database to " + backupDbName);
            int retCode = execShellCmd(command);
            if (retCode != 0)
            	System.out.println("Got non zero return code (" + retCode + ") when executing command " + command);
            return retCode == 0;
        } catch (Exception e) {
        	System.out.println("Failed to execute command " + command + " " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
	}
	
    public static int execShellCmd(String cmd) throws IOException, InterruptedException 
    {    
        Runtime runtime = Runtime.getRuntime();  
        Process process = runtime.exec(new String[] { "/bin/sh", "-c", cmd });  
        int exitValue = process.waitFor();  
        BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));  
        String line = "";  
        while ((line = buf.readLine()) != null) {  
            System.out.println("exec response: " + line);  
        }  
        return exitValue;
    }  
	
	private static Connection openRootConnection(DatabaseInfo dbinfo) throws Exception
	{
		try
		{
			Connection dbConn = tryDbConnection(dbinfo.rootUser, dbinfo.rootPassword,
					null);
			if (dbConn == null)
			{
				dbConn = tryDbConnection(dbinfo.rootUser,
						dbinfo.newRootPassword, null);
				if (dbConn == null)
				{
					throw new Exception("Cannot login as the "
							+ dbinfo.rootUser
							+ " to the MySQL server. Bad credentials.");
				}
				dbinfo.rootPassword = dbinfo.newRootPassword;
			}
			else
			{
				dbinfo.changeRootPassword = !dbinfo.rootPassword
						.equals(dbinfo.newRootPassword);
			}
			
			return dbConn;
		}
		catch (Exception e)
		{
			throw new Exception("Cannot login to the MySQL server as the "
					+ dbinfo.rootUser + " user: " + e, e);
		}
	}
	
	private static void createDB()
	{	
		Connection dbConn = null;
		DatabaseInfo dbinfo = new DatabaseInfo();
		String currentOperation = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;
        PreparedStatement stmt5 = null;
        PreparedStatement stmt6 = null;
        PreparedStatement stmt7 = null;

		try
		{
			readConfigFile();
			
			findDBScripts("/usr/share/xbrc/dbscripts", dbinfo);
		
			dbinfo.rootUser = prop.getProperty("nge.xconnect.xbrc.dbserver.rootuid");
			dbinfo.rootPassword = prop.getProperty("nge.xconnect.xbrc.dbserver.rootpwd");
			dbinfo.newRootPassword = prop.getProperty("nge.xconnect.xbrc.dbserver.newrootpwd");		
			dbinfo.user = prop.getProperty("nge.xconnect.xbrc.dbserver.uid");
			dbinfo.password = prop.getProperty("nge.xconnect.xbrc.dbserver.pwd");
			dbinfo.viewUser = prop.getProperty("nge.xconnect.xbrc.dbserver.view.uid");
			dbinfo.viewUserPassword = prop.getProperty("nge.xconnect.xbrc.dbserver.view.pwd");
			
			if (dbinfo.rootPassword == null)
				dbinfo.rootPassword = "";
			
			if (dbinfo.rootUser == null ||dbinfo.rootUser.isEmpty())
			{
				System.err.println("The dbrootuser configuration parameter is not set. Cannot continue.");
				return;
			}
		
			dbConn = openRootConnection(dbinfo);
			
			if (dbinfo.changeRootPassword)
				changeUserPassword(dbConn, dbinfo.rootUser, dbinfo.newRootPassword);
			
			boolean tryAgain = false;
			do
			{
				checkIfUserExists(dbConn, dbinfo);
			
				checkIfViewUserExists(dbConn, dbinfo);

				checkDatbaseExists(dbConn, dbinfo);

				if (!dbinfo.databaseExists)
				{
					// No database, create it
					currentOperation = "create database " + DBNAME;
					stmt = dbConn.prepareStatement(currentOperation);
					stmt.executeUpdate(currentOperation);
					stmt.close();
	                stmt = null;
					currentOperation = null;
				}

				currentOperation = "use " + DBNAME;
				stmt1 = dbConn.prepareStatement(currentOperation);
				stmt1.executeUpdate(currentOperation);
				stmt1.close();
	            stmt1 = null;
				currentOperation = null;
				
				if (dbinfo.tableCount == 0)
				{
					// create latest, full database schema
					initDB(dbConn, "/usr/share/xbrc/dbscripts/1.0.0.0.sql");
				}
				
				getSchemaVersion(dbConn, dbinfo);
				
				if (tryAgain)
					tryAgain = false;
				else
				{
					tryAgain = dbinfo.model != null && dbinfo.newModel != null && !dbinfo.newModel.contains(dbinfo.model);
					tryAgain = tryAgain || (dbinfo.newModel != null && dbinfo.configModel != null && !dbinfo.newModel.equals(dbinfo.configModel));
							
					if (tryAgain)
					{
						// Looks like we are installing over an existing installation, but different model.
						// Need to rename the Mayhem database to make room for the new one.
						if (!backupDatabase(dbinfo, "/var/log/xbrc"))
							throw new Exception("Failed to backup the " + dbinfo.database + " database. Aborting database install.");
						
						// Drop the old database
						currentOperation = "drop database " + DBNAME;
						stmt = dbConn.prepareStatement(currentOperation);
						stmt.executeUpdate(currentOperation);
						stmt.close();
		                stmt = null;
						currentOperation = null;
						
						System.out.println("Dropped the " + dbinfo.database + " database because " +
								" models did not match. Expected model " + dbinfo.newModel + " but found " + dbinfo.model);
					}
				}
			}
			while(tryAgain);
				
			String scriptToRun = null;
			String currentSchemaVersion = dbinfo.schemaVersion;

			if (dbinfo.schemaVersion == null)
				scriptToRun = dbinfo.scriptsMap.get("0.0.0.0");
			else
				scriptToRun = dbinfo.scriptsMap.get(dbinfo.schemaVersion);

			if (scriptToRun == null)
				System.out
						.println("The database seems to be at the latest version "
								+ dbinfo.schemaVersion
								+ ". No schema changes are being made.");

			// Apply all incremental schema changes
			while (scriptToRun != null)
			{
				initDB(dbConn, scriptToRun);

				// make sure that the schema version had changed, otherwise
				// we will be going in circles
				getSchemaVersion(dbConn, dbinfo);

				boolean success = true;
				if (currentSchemaVersion == null)
				{
					if (dbinfo.schemaVersion == null)
						success = false;
				}
				else if (currentSchemaVersion.equals(dbinfo.schemaVersion))
					success = false;

				if (!success)
				{
					System.err.println("The database schema version ["
							+ dbinfo.schemaVersion
							+ "] did not change after running script "
							+ scriptToRun + ". Aborting database upgrade.");
					break;
				}

				// get the new script to run if any
				scriptToRun = dbinfo.scriptsMap.get(dbinfo.schemaVersion);
			}
			
			if (dbinfo.createUser)
			{
				currentOperation =
                        "create user '" +
                        InputValidator.validateUserName(dbinfo.user) + "'@'localhost' identified by '" +
                        InputValidator.validateUserPw(dbinfo.password) + "'";

				stmt2 = dbConn.prepareStatement(currentOperation);
				stmt2.executeUpdate(currentOperation);
				stmt2.close();
                stmt2 = null;
				currentOperation = null;
			}
			else if (dbinfo.setUserPassword)
			{
				changeUserPassword(dbConn, dbinfo.user, dbinfo.password);
			}
			
			if (dbinfo.createViewUser)
			{
				currentOperation =
                        "create user '" +
                        InputValidator.validateUserName(dbinfo.viewUser) + "'@'%' identified by '" +
                        InputValidator.validateUserPw(dbinfo.viewUserPassword) + "'";

				stmt4 = dbConn.prepareStatement(currentOperation);
				stmt4.executeUpdate(currentOperation);
				stmt4.close();
				stmt4 = null;
				currentOperation = null;
				
				currentOperation =
                        "create user '" +
                        InputValidator.validateUserName(dbinfo.viewUser) + "'@'localhost' identified by '" +
                        InputValidator.validateUserPw(dbinfo.viewUserPassword) + "'";

				stmt6 = dbConn.prepareStatement(currentOperation);
				stmt6.executeUpdate(currentOperation);
				stmt6.close();
				stmt6 = null;
				currentOperation = null;
				
			}
			else if (dbinfo.setViewUserPassword)
			{
				changeViewUserPassword(dbConn, dbinfo.viewUser, dbinfo.viewUserPassword);
			}
						
			currentOperation =
                    "grant all privileges on " + DBNAME + ".* to '" +
                    InputValidator.validateUserName(dbinfo.user) + "'@'localhost'";

			stmt3 = dbConn.prepareStatement(currentOperation);
			stmt3.executeUpdate(currentOperation);
			stmt3.close();
			currentOperation = null;

			if (dbinfo.viewUser != null && dbinfo.viewUser != "" && dbinfo.viewUserPassword != null && dbinfo.viewUserPassword != "")
			{
				currentOperation =
                    "grant select on " + DBNAME + ".* to '" +
                    InputValidator.validateUserName(dbinfo.viewUser) + "'@'%'";

				stmt5 = dbConn.prepareStatement(currentOperation);
				stmt5.executeUpdate(currentOperation);
				stmt5.close();
				currentOperation = null;

                currentOperation =
                    "grant select on " + DBNAME + ".* to '" +
                    InputValidator.validateUserName(dbinfo.viewUser) + "'@'localhost'";

				stmt7 = dbConn.prepareStatement(currentOperation);
				stmt7.executeUpdate(currentOperation);
				stmt7.close();
				currentOperation = null;
}
		}
		catch (Exception e)
		{
			if (currentOperation != null)
				System.err.println("Failed to execute: " + currentOperation + ": " + e.getLocalizedMessage());
			else
				System.err.println(e.getLocalizedMessage());
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

            if (stmt1 != null) {
                try {
                    stmt1.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt2 != null) {
                try {
                    stmt2.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt3 != null) {
                try {
                    stmt3.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt4 != null) {
                try {
                    stmt4.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt5 != null) {
                try {
                    stmt5.close();
                }
                catch (Exception e) {
                }
            }

            if (stmt6 != null) {
                try {
                    stmt6.close();
                }
                catch (Exception e) {
                }
            }
            
            if (stmt7 != null) {
                try {
                    stmt7.close();
                }
                catch (Exception e) {
                }
            }

            if (dbConn != null) {
                try {
                    Controller.getInstance().releasePooledConnection(dbConn);
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	public static void setEcho(boolean bEcho)
	{
		String cmd;
		if (bEcho)
			cmd = "stty echo < /dev/tty";
		else
			cmd = "stty -echo < /dev/tty";

		try
		{
			Runtime.getRuntime().exec(new String[] { "sh", "-c", cmd });
		}
		catch (IOException e)
		{
		}
	}

	private static void initDB(Connection dbConn, String scriptPath) throws Exception
	{
		if (scriptPath == null)
			return;
		
		// validate directory path
		String validScriptPath = InputValidator.validateDirectoryName(InputValidator.getDirName(scriptPath))
								+ "/" + InputValidator.validateFileName(InputValidator.getFileName(scriptPath));
		
		// look for the database init script in the product directory and then
		// in the local one if not found
		File file = new File(validScriptPath);
		if (!file.exists())
		{
			throw new Exception("Can not find database initialization script " + validScriptPath);
		}
		
		// open the file
		FileReader fin = null;
		try
		{
			fin = new FileReader(file);
			System.out.println("Applying database script " + validScriptPath);
			ScriptRunner run = new ScriptRunner(dbConn, false, true);
			run.runScript(fin);
		}
		catch (FileNotFoundException e)
		{
			throw new Exception("Can not open database initialization script " + scriptPath + ": " + e, e);
		}
		catch (IOException e)
		{
			throw new Exception("Error running initialization script " + scriptPath + ": " + e, e);
		}
		catch (SQLException e)
		{
			throw new Exception("SQL Error running initialization script " + scriptPath + ": " + e, e);
		}
        finally {
            if(fin != null) {
                try {
                    fin.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static String readLine()
	{
		BufferedReader br = null;
		String sLine = null;

		try
		{
            br = new BufferedReader(new InputStreamReader(System.in));
			sLine = br.readLine();
		}
		catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch (Exception ignore) {
                }
            }
        }

		return sLine;
	}

	private static boolean confirm(String sPrompt)
	{
		if (bForce)
			return true;

		System.out.print(sPrompt + " (yes/no): ");
		String sResponse = readLine();
		if (sResponse.compareToIgnoreCase("yes") == 0)
			return true;
		return sResponse.compareToIgnoreCase("y") == 0;
	}

	private static void reloadConfig()
	{
		if (sendPut("http://localhost:8080/updateconfig", "", 500) == 200)
			System.out.println("xBRC configuration updated");
		else
			System.out.println("xBRC configuration update failed");
	}

	private static void clearCache()
	{
		if (sendDelete("http://localhost:8080/cache", 500) == 200)
			System.out.println("xBRC IDMS cache cleared");
		else
			System.out.println("xBRC cache clearing failed");
	}

	private static void clearGuests()
	{
		if (sendDelete("http://localhost:8080/gueststatus", 500) == 200)
			System.out.println("xBRC GST cleared");
		else
			System.out.println("xBRC GST clearing failed");
	}

	private static int sendPut(String sUrl, String sData, int msecTimeout)
	{
        OutputStreamWriter wr = null;

		try
		{
			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestProperty("Authorization", XbConnection.getAuthorizationString(user, passwd));
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			httpCon.setConnectTimeout(msecTimeout);
			httpCon.setReadTimeout(msecTimeout);
			wr = new OutputStreamWriter(
					httpCon.getOutputStream());
			wr.write(sData);
			wr.flush();
			return httpCon.getResponseCode();

		}
		catch (MalformedURLException e)
		{
			System.out.println("Error: " + e);
			return 500;
		}
		catch (IOException e)
		{
			System.out.println("Error: " + e);
			return 500;
		}
        finally {
            if(wr != null) {
                try {
                    wr.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}

	private static int sendDelete(String sUrl, int msecTimeout)
	{

		try
		{
			URL url = new URL(sUrl);

			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setRequestMethod("DELETE");
			httpCon.setConnectTimeout(msecTimeout);
			httpCon.setReadTimeout(msecTimeout);
			return httpCon.getResponseCode();

		}
		catch (MalformedURLException e)
		{
			System.out.println("Error: " + e);
			return 500;
		}
		catch (IOException e)
		{
			System.out.println("Error: " + e);
			return 500;
		}
	}
	
	private static NGEPropertiesDecoder getPropertiesDecoder(String password) throws IOException
	{
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
		
		// get property settings
		// is there is a system property to identify where the environemtn.properites is
		String sPropFile = System.getProperty("xbrc.properties");
		if (sPropFile != null)
		{
			decoder.setPropertiesPath(sPropFile);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sPropFile != null)
		{
			decoder.setJasyptPropertiesPath(sJasyptPropFile);
		}
		
		decoder.initialize(password);
		
		return decoder;
	}
	
	private static void readConfigFile() throws Exception
	{
		if (prop != null)
			return;
		
		NGEPropertiesDecoder decoder = getPropertiesDecoder(null);
		
		try
		{
			prop = decoder.read();
		}
		catch (Exception ex)
		{
			throw new Exception("!! Could not read " + decoder.getPropertiesPath()  + " file: "
					+ ex.getLocalizedMessage(), ex);
		}
	}
	
	private static void encrypt(String[] args)
	{			
		try
		{
			if (args.length != 2)
			{
				System.out.println("Missing required parameters key and text to encrypt.");
				return;
			}
			
			NGEPropertiesDecoder decoder = getPropertiesDecoder(args[0]);
			
			System.out.println("encrypting " + args[1] + " using key: " + args[0]);
			System.out.println(decoder.encrypt(args[1]));
		}
		catch (Exception ex)
		{
			System.out.println("!! Failed to encrypt text: " + ex.getLocalizedMessage());
		}
	}
}
