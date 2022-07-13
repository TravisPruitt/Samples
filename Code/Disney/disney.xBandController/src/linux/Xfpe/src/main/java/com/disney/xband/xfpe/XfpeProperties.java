package com.disney.xband.xfpe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;

/*
 * Singleton class holding our properties.
 */
public class XfpeProperties extends Properties {
	private static Logger logger = Logger.getLogger(XfpeProperties.class);
	private static String CONFIG_URL = "/usr/share/disney.xband/Xfpe/config.properties";
	private Connection conn;
	
	private XfpeSimConfig xfpeSimConfig;

	private static class SingletonHolder { 
		public static final XfpeProperties instance = new XfpeProperties();
	}
	
	public static XfpeProperties getInstance() {
		return SingletonHolder.instance;
	}	
	
	private XfpeProperties() {
		
		xfpeSimConfig = new XfpeSimConfig();
		
        FileInputStream fis = null;

		try {
			String uiConfigFile = null;
			
			String configDir = System.getProperty("config.dir");
			if (configDir != null && !configDir.trim().isEmpty())
			{
				uiConfigFile = InputValidator.validateDirectoryName(configDir) + "/Xfpe/config.properties";
				fis = new FileInputStream(uiConfigFile);
			} 
			else 
			{
				// use the default properties file
				fis = new FileInputStream(InputValidator.validateFilePath(CONFIG_URL));
				logger.warn("-Dconfig.dir not set!!! Using default configuration: " + CONFIG_URL);
			}
			this.load(fis);
		}
	    catch (FileNotFoundException e) {
	    	logger.fatal("Cannot find config.properties", e);	    	
		} 
	    catch (InvalidPropertiesFormatException e) {
	    	logger.fatal("Invalid format for config.properties", e);
		} 
	    catch (IOException e) {
	    	logger.fatal("IO error reading config.properties", e);
	    }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch (Exception ignore) {
                }
            }
        }

		try {
			readAllConfig();
		} catch (IllegalArgumentException e) {
			logger.fatal("Failed to read Config table", e);
		} catch (ClassNotFoundException e) {
			logger.fatal("Failed to read Config table", e);
		} catch (SQLException e) {
			logger.fatal("Failed to read Config table", e);
		} catch (IllegalAccessException e) {
			logger.fatal("Failed to read Config table", e);
		}
	}
	
	public void readAllConfig() throws ClassNotFoundException, SQLException, IllegalArgumentException, IllegalAccessException {		
		
		conn = getConn();
		
		// Read all configuration.
		Config config = Config.getInstance();
		config.read(conn, xfpeSimConfig);
	}

	public XfpeSimConfig getXfpeSimConfig() {
		return xfpeSimConfig;
	}
	
	public Connection reconnect() throws ClassNotFoundException, SQLException {
		
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return getConn();
	}

	public Connection getConn() throws ClassNotFoundException, SQLException {
		
		if (conn == null) {
			// Open database connection
			String url = this.getProperty("db.url");
			String user = this.getProperty("db.user");
			String password = this.getProperty("db.password");
			
			Class.forName("com.mysql.jdbc.Driver");
			Properties connProps = new Properties();
			connProps.put("user", user);
			connProps.put("password", password);
			
			conn = DriverManager.getConnection(url, connProps);
		}
		
		return conn;
	}
}
