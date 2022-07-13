package com.disney.xband.xbrms.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.disney.xband.xbrms.common.ConfigProperties;
import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.XbrmsDto;

/*
 * Singleton class holding our properties.
 */
public class ClientConfigProperties extends ConfigProperties {
    private static class SingletonHolder {
        public static final ClientConfigProperties instance = new ClientConfigProperties();
    }

    public static ClientConfigProperties getInstance() {
        return SingletonHolder.instance;
    }

    private ClientConfigProperties() {
        if (logger.isInfoEnabled())
            logger.info("Starting xBRMS UI ...");
    }

    public void readConfigurationOptionsFromEnvironmentProperties() throws IOException
    {
        if (logger.isInfoEnabled())
             logger.info("Reading configuration from the properties file...");

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

        if(XbrmsUtils.isEmpty((String) this.get(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE))) {
            this.setProperty(PkConstants.PROP_NAME_LOCAL_SETTINGS_FILE, PkConstants.DEFAULT_LOCAL_SETTINGS_FILE);
        }
    }

    public void readConfigurationOptionsFromDatabase() throws ClassNotFoundException, SQLException, IllegalAccessException {
    }
}
