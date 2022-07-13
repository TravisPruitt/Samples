package com.disney.xband.xbrms.server;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 12/27/12
 * Time: 4:48 PM
 */
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import com.disney.xband.ac.lib.XbUtils;
import com.disney.xband.ac.lib.client.XbConnection;
import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.xbrms.server.model.XbrmsDao;

/*
 * Singleton class holding our properties.
 */
public class ServerConfigProperties extends ConfigProperties 
{
	private static Logger plogger = Logger.getLogger("performance." + ServerConfigProperties.class.toString());
	
    private static class SingletonHolder {
        public static final ServerConfigProperties instance = new ServerConfigProperties();
    }

    public static ServerConfigProperties getInstance() {
        return SingletonHolder.instance;
    }

    private ServerConfigProperties() {
        // try { Thread.sleep(10000); } catch(Exception e) { }
        if (logger.isInfoEnabled())
            logger.info("Starting xBRMS !!!!! ...");
    }

    public void readConfigurationOptionsFromEnvironmentProperties() throws IOException
    {
        if (logger.isInfoEnabled())
             logger.info("Reading configuration from the properties file...");

        try {
            XbUtils.loadProperties(this, "xbrc", logger);
            XbConnection.init(XbUtils.propsToHashMap(this));
        }
        catch (Exception e) {
            logger.fatal("!! Failed to initialize XbConnection instance.");
            System.exit(1);
        }

        NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
                
        // get property settings
        // is there is a system property to identify where the environemtn.properites is
        String sPropFile = System.getProperty("environment.properties");
        if (sPropFile != null)
        {
            logger.info("The environment.properties java argument is set. Using the " + sPropFile + " properties file.");
            decoder.setPropertiesPath(sPropFile);
        }

        String sJasyptPropFile = System.getProperty("jasypt.properties");
        if (sJasyptPropFile != null)
        {
            logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
            decoder.setJasyptPropertiesPath(sJasyptPropFile);
        }
        
        decoder.initialize();
        
        Properties prop = decoder.read();
        
        for (Map.Entry<Object, Object> entry : prop.entrySet())
        {
            this.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
        
        if (getProperty("nge.xconnect.parkid") == null || getProperty("nge.xconnect.parkid").isEmpty())
			ProblemsReportBo.getInstance().setLastError(ProblemAreaType.JmsMessaging, "The nge.xconnect.parkid key is not set in environment.properties", null);

        if(XbrmsUtils.isEmpty((String) this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE))) {
            this.setProperty(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE, PkConstants.DEFAULT_LOCAL_SETTINGS_FILE);
        }
    }

    public void readConfigurationOptionsFromDatabase() throws ClassNotFoundException, SQLException, IllegalAccessException, IllegalArgumentException {

        if (logger.isInfoEnabled())
             logger.info("Reading configuration from the database...");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String query = "SELECT value FROM config WHERE property = 'lastmodified'";
        long begin = System.currentTimeMillis();
        
        try
        {
            conn = SSConnectionPool.getInstance().getConnection();

            // Read all configuration.
            Config config = Config.getInstance();
            
            // determine if the config table has any xbrms properties
            stmt = SSConnectionPool.getInstance().getPreparedStatement(conn, query);
            stmt.clearParameters();
            stmt.execute();
            rs = stmt.getResultSet();
            if (rs.next())
            {
            	// read the config values
            	config.read(conn, XbrmsConfigBo.getInstance().getDto());
            }
            else
            {
            	// assume empty config table, write out the defaults
            	XbrmsConfigBo.getInstance().getDto().setLastModified((new Date()).getTime());
            	config.write(conn, XbrmsConfigBo.getInstance().getDto());
            }

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
    }
}
