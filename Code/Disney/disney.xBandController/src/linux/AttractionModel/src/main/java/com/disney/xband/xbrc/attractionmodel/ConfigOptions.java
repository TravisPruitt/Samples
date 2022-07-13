package com.disney.xband.xbrc.attractionmodel;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xbrc.lib.db.EventsLocationConfigService;
import com.disney.xband.xbrc.lib.db.VaLocationConfigService;
import com.disney.xband.xbrc.lib.entity.VaLocationConfig;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class ConfigOptions extends Configuration
{
	// singleton
	public static final ConfigOptions INSTANCE = new ConfigOptions();
	private static Logger logger = Logger.getLogger(ConfigOptions.class);
	
	// class data
	private CarInfo carInfo;
	private ModelInfo mi;
	private Map<Long,VaLocationConfig> vaLocations;
    private Map<Long,EventsLocationConfig> eventLocationConfigs;

	@Override
	protected void initHook(Connection conn) {}
	
	@PersistName("AttractionModelConfig")
	public class ModelInfo extends Configuration
	{
        @PersistName("abandonmenttimeout")
        @MetaData(name="abandonmenttimeout", min = "0", defaultValue = "3600", description="A guest is considered as having abandoned the attraction is no long range or tap events are received for the specified number of seconds.")
        private int csecAbandonmentTimeout;        
		@PersistName("metricsperiod")
		@MetaData(name="metricsperiod", min = "0", defaultValue = "600", description="The number of seconds allocated for each performace metric period.")
		private int csecMetricsPeriod;		
		@PersistName("gxpurl")
		@MetaData(name="gxpurl", defaultValue = "#", description="The HTTP URL of the GXP endpoint for guest entitlment processing")
		private String sGXPURL;
		@PersistName("gstsaveperiod")
		@MetaData(name="gstsaveperiod", min = "0", defaultValue = "1000", description="The period in milliseconds for saving the guest status to the GST database table.")
		private int cmsecGstSavePeriod;
		@PersistName("deferentrymessages")
		@MetaData(name="deferentrymessages", defaultValue="true", description="If set to true, the xBRC will hold off with sending ENTRY guest messages until guest is seen at merge.")
		private boolean bDeferEntryMessages;
		@PersistName("exitstatetimeout")
		@MetaData(name="exitstatetimeout", defaultValue="120", description="The number of seconds before a guest is removed from the model after arriving at EXIT location")
		private int csecExitStateTimeout;		
		@PersistName("cmsecReaderLockExpiry")
		@MetaData(name="cmsecReaderLockExpiry", min = "0", defaultValue = "30000", description="All events from a TAP reader are ignored for the number of milliseconds specified while waiting for a GXP response or cast member to clear a BLUE lane")
		private int cmsecReaderLockExpiry;		  
		@PersistName("cmsecGxpCallTimeout")
		@MetaData(name="cmsecGxpCallTimeout", min = "100", defaultValue = "4000", description="The amount of time to wait for GXP entitlement call response before performing the gxpTimeoutAction")
		private int cmsecGxpCallTimeout;	
		@PersistName("gxpCallTimeoutAction")
		@MetaData(name="gxpCallTimeoutAction", defaultValue = "off", description="The action to take after a timeout of a GXP entitlement call. Choices are: off, success, failure, error", choices="off,success,failure,error")
		private String gxpCallTimeoutAction;
		
        @Override
		protected void initHook(Connection conn) {}
	
		public int getAbandonmentTimeout()
		{
			return csecAbandonmentTimeout;
		}

		public void setAbandonmentTimeout(int csecAbandonmentTimeout)
		{
			this.csecAbandonmentTimeout = csecAbandonmentTimeout;
		}
		
		public int getMetricsPeriod()
		{
			return this.csecMetricsPeriod;
		}
		
		public void setMetricsPeriod(int csecMetricsPeriod)
		{
			this.csecMetricsPeriod = csecMetricsPeriod;
		}
		
		public String getGXPURL()
		{
			return this.sGXPURL;
		}
		
		public void setGXPURL(String sURL)
		{
			this.sGXPURL = sURL;
		}
		
		public void setGstSavePeriod(int msec)
		{
			this.cmsecGstSavePeriod = msec;
		}
		
		public int getGstSavePeriod()
		{
			return this.cmsecGstSavePeriod;
		}
		
		public void setDeferEntryMessages(boolean bDeferEntryMessages)
		{
			this.bDeferEntryMessages = bDeferEntryMessages;
		}
		
		public boolean getDeferEntryMessages()
		{
			return this.bDeferEntryMessages;
		}
		
		public int getExitStateTimeout()
		{
			return this.csecExitStateTimeout;
		}
		
		public void setExitStateTimeout(int cmsecExitStateTimeout)
		{
			this.csecExitStateTimeout = cmsecExitStateTimeout;
		}
		public int getCmsecReaderLockExpiry()
		{
			return cmsecReaderLockExpiry;
		}
		public void setCmsecReaderLockExpiry(int cmsecReaderLockExpiry)
		{
			this.cmsecReaderLockExpiry = cmsecReaderLockExpiry;
		}

		public int getCmsecGxpCallTimeout() {
			return cmsecGxpCallTimeout;
		}

		public void setCmsecGxpCallTimeout(int cmsecGxpCallTimeout) {
			this.cmsecGxpCallTimeout = cmsecGxpCallTimeout;
		}

		public String getGxpCallTimeoutAction() {
			return gxpCallTimeoutAction;
		}

		public void setGxpCallTimeoutAction(String gxpCallTimeoutAction) {
			this.gxpCallTimeoutAction = gxpCallTimeoutAction;
		}
		
    }

	public class CarInfo
	{
		private Map<String,String> map;
		
		public CarInfo()
		{
			map = new Hashtable<String,String>();
		}
		
		public void AddCar(String sBandID, String sCarID)
		{
			map.put(sBandID,  sCarID);
		}
		
		public boolean IsCar(String sBandID)
		{
			return map.containsKey(sBandID);
		}
		
		public String GetCarID(String sBandID)
		{
			return map.get(sBandID);
		}
	}

	private ConfigOptions()
	{
		// create the reader info
		carInfo = new CarInfo();
		mi = new ModelInfo();
		vaLocations = new HashMap<Long,VaLocationConfig>();
        eventLocationConfigs = new HashMap<Long, EventsLocationConfig>();
	}
	
	public CarInfo getCarInfo()
	{
		return carInfo;
	}
	
	public ModelInfo getModelInfo() {
		return mi;
	}
		
	public void readConfigurationOptionsFromDatabase() 
	{
    	// read property/value pairs from the Config table
		Connection conn = null;
    	try 
    	{
    		// now, open up the database and see if we have anything set there
    	    conn = XBRCController.getInstance().getPooledConnection();
    	    Config config = Config.getInstance();
			config.read(conn, mi);
			
		    // and the known band id table
		    processBandIDTable(conn);
		    
		    vaLocations = VaLocationConfigService.getAllMapped(conn);
		    eventLocationConfigs = EventsLocationConfigService.getAllMapped(conn);
		} 
    	catch (Exception e) 
    	{
			logger.error("Failed to read configuration for " + mi.getClass().getName(), e);
		}
    	finally
    	{
    		if (conn!=null)
    			XBRCController.getInstance().releasePooledConnection(conn);
    	}
	    
	}

	private int
	processBandIDTable(Connection conn)
	{
		Statement stmt = null;
		ResultSet rs = null;
				
	    try
		{
			String sSQL = "SELECT * from KnownBandIDs WHERE BandType='CAR'";
			stmt = conn.createStatement();
			stmt.execute(sSQL);
			rs = stmt.getResultSet();
			while(rs.next())
			{
				String sBandID = rs.getString("BandID");
				String sCarID = rs.getString("AssociatedData");
				
				ConfigOptions.INSTANCE.getCarInfo().AddCar(sBandID, sCarID);
			}			
		} 
	    catch (Exception e)
		{
	    	logger.error("!! Error reading KnownBandIDs table from database", e);
	    	return 2;
		}
	    finally 
	    {
	    	try 
	    	{
	    		if (rs != null)
	    			rs.close();
	    		if (stmt != null)
	    			stmt.close();
	    	} 
	    	catch(Exception e) {}
	    }

	    return 0;
		
	}

	public Map<Long, VaLocationConfig> getVaLocations() {
		return vaLocations;
	}

    public Map<Long, EventsLocationConfig> getEventLocationConfigs() {
        return eventLocationConfigs;
    }

}
