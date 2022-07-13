package com.disney.xband.xbrc.ui;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.NGEPropertiesDecoder;

import javax.servlet.ServletContext;

/*
 * Singleton class holding our properties.
 */
public class UIProperties extends Properties {
	private static Logger logger = Logger.getLogger(UIProperties.class);
	
	private AttractionViewConfig attractionViewConfig;
	private ParkEntryConfigOptions parkEntryConfig;
	private UIConfig uiConfig;
	private ControllerInfo controllerInfo;
	private Date ciLastRead = null;
    private Properties manifest;

	public static UIProperties getInstance() {
		return SingletonHolder.instance;
	}	
	
	private UIProperties() {
		
		attractionViewConfig = new AttractionViewConfig();
		uiConfig = new UIConfig();
		controllerInfo = new ControllerInfo();
		parkEntryConfig = new ParkEntryConfigOptions();
					
		// first, read the properties file to get the database parameters
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
		
		// get property settings
		// is there is a system property to identify where the environemtn.properites is
		String sPropFile = System.getProperty("environment.properties");
		if (sPropFile != null)
		{
			logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
			decoder.setPropertiesPath(sPropFile != null ? InputValidator.validateFilePath(sPropFile) : null);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sJasyptPropFile != null)
		{
			logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
			decoder.setJasyptPropertiesPath(sJasyptPropFile != null ? InputValidator.validateFilePath(sJasyptPropFile) : null);
		}

		try
		{
			decoder.initialize();
			Properties prop = decoder.read();
			for (Map.Entry<Object, Object> entry : prop.entrySet())
			{
				this.setProperty((String)entry.getKey(), (String)entry.getValue());
			}
		}
		catch (Exception ex)
		{
			logger.fatal("!! Could not read the properties file [" + 
					decoder.getPropertiesPath() + "]: "
						 + ex.getLocalizedMessage());
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

        XbUtils.loadProperties(this, "xbrc", logger);

        try {
            XbConnection.init(XbUtils.propsToHashMap(this));
        }
        catch (Exception e) {
            logger.fatal("!! Failed to initialize XbConnection instance.");
            System.exit(1);
        }
	}

    public void initAudit() {
        AuditInit.initializeAudit(UIProperties.getInstance(), GetCachedControllerInfo().getVenue());
    }
	
	private Connection getConnection() throws ClassNotFoundException, SQLException
	{
		// Open database connection	
		String url = this.getProperty("nge.xconnect.xbrc.dbserver.service");
		String user = this.getProperty("nge.xconnect.xbrc.dbserver.uid");
		String password = this.getProperty("nge.xconnect.xbrc.dbserver.pwd");
		
		Class.forName("com.mysql.jdbc.Driver");
		Properties connProps = new Properties();
		connProps.put("user", user);
		connProps.put("password", password);
		
		Connection conn = DriverManager.getConnection(url, connProps);
		return conn;
	}
	
	private void readAllConfig() throws ClassNotFoundException, SQLException, IllegalArgumentException, IllegalAccessException {
		
		Connection conn = null;

        try {
            conn = getConnection();

		    // Read all configuration.
		    Config config = Config.getInstance();
		    config.read(conn, attractionViewConfig);
		    config.read(conn, uiConfig);
		    config.read(conn, controllerInfo);
		    config.read(conn, parkEntryConfig);
		
		    ciLastRead = new Date();
        }
        finally {
            if(conn != null) {
                try {
                    conn.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}
	
	public void saveUIConfig() throws ClassNotFoundException, SQLException, IllegalArgumentException, IllegalAccessException {
		synchronized(this)
		{
            Connection conn = null;

            try {
                conn = getConnection();
			    Config config = Config.getInstance();
			    config.write(conn, uiConfig);
            }
            finally {
                if(conn != null) {
                    try {
                        conn.close();
                    }
                    catch(Exception ignore) {
                    }
                }
            }
		}
	}
	
	public ControllerInfo getControllerInfo() {
		synchronized(this)
		{
			refresh();
			return controllerInfo;
		}
	}
	
	public AttractionViewConfig getAttractionViewConfig() {
		synchronized(this)
		{
			refresh();
			return attractionViewConfig;
		}
	}
	
	public ParkEntryConfigOptions getParkEntryConfig(){
		synchronized(this)
		{
			refresh();
			return parkEntryConfig;
		}
	}

	public UIConfig getUiConfig() {
		synchronized(this)
		{
			refresh();
			return uiConfig;
		}
	}
	
	private static class SingletonHolder { 
		public static final UIProperties instance = new UIProperties();
	}
	
	private void refresh()
	{
		if (ciLastRead == null || (new Date().getTime() - ciLastRead.getTime()) > 15000 ) 
		{			
			try
			{
				readAllConfig();
			}
			catch (Exception e)
			{
				// Don't attempt this more than every ....
				ciLastRead = new Date();
				logger.error("Failed to read UI configuration from the database", e);
			}			
		}
	}
	
	/*
	 * The ControllerInfo object is retrieved very frequenty, for every page load.
	 * We need to minimize the database I/O so we cache it for some time.
	 * This function must be thread safe.
	 */
	public ControllerInfo GetCachedControllerInfo() {
		
		synchronized(controllerInfo) {
			if (ciLastRead != null) {
				// Read very 15 seconds or so...
				if (new Date().getTime() - ciLastRead.getTime() < 15000) {				
					return controllerInfo.clone();
				}
			}	
				
			// Read from the database
			Connection conn = null;
			try {
				conn = getConnection();
				Config config = Config.getInstance();
				config.read(conn, controllerInfo);						
			} catch (Exception e) {
				logger.error("Failed to read ControllerInfo from the Config table", e);
			}
			finally {
				if (conn != null)
					try { conn.close(); } catch (SQLException e) {}
			}
			
			ciLastRead = new Date();
			return controllerInfo.clone();
		}
	}

    public String getUiVersion(ServletContext context) {
        if (this.manifest == null) {
            InputStream is = null;

            try {
                this.manifest = new Properties();
                is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
                this.manifest.load(is);
            }
            catch (IOException e) {
                this.logger.warn("Failed to read th MANIFEST.MF file. Version will not be available;");
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
        }

		String version = manifest.getProperty("Implementation-Title");

		if ((version == null) || (version.length() == 0)) {
			version = "Development";
        }

		return version;
	}
}
