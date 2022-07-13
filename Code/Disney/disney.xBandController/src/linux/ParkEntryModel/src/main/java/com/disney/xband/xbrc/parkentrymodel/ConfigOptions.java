package com.disney.xband.xbrc.parkentrymodel;


import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xbrc.lib.db.CastMemberService;
import com.disney.xband.xbrc.lib.entity.CastMember;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class ConfigOptions  extends Configuration
{
	// singleton
	public static final ConfigOptions INSTANCE = new ConfigOptions();
	private static Logger logger = Logger.getLogger(ConfigOptions.class);
	
	// all - save all bio images, failed - only in case of match failure, none - don't save any
	public static enum SaveBioImages { all, failed, none };
	
	// class data
	private Settings settings;
	private HashMap<String,CastMember> castMembers;
	private SaveBioImages saveBioImages;
	
	@Override
	protected void initHook(Connection conn){
		try {
	    	saveBioImages = SaveBioImages.valueOf(settings.getSaveBioImages());
	    } catch( Exception e) {
	    	logger.error("Invalid value for parameter saveBioImages: " + settings.getSaveBioImages() + 
	    				" defaulting to all");
	    	saveBioImages = SaveBioImages.all;
	    }
	}
	
	@PersistName("ParkEntryModelConfig")
	public class Settings extends Configuration
	{
		// Maximum times a Guest is allowed to retry a fingerprint scan.
		@PersistName("maxFpScanRetry")
		@MetaData(name="maxFpScanRetry", min = "0", defaultValue="3", description="Maximum number of bad finger scans before blue light")
		private int maxFpScanRetry;
		@PersistName("greenLightTimeoutMs")
		@MetaData(name="greenLightTimeoutMs", min = "0", defaultValue="5000", description="Greeter app green light duration in miliseconds")
		private int greenLightTimeoutMs;
		// Assume guest abandoned the reader if no events received after this time.
		@PersistName("abandonmentTimeSec")
		@MetaData(name="abandonmentTimeSec", min = "0", defaultValue="25", description="Assume that guest left the reader if no guest action was detected for the specified number of seconds")
		private int abandonmentTimeSec;
		@PersistName("omniTicketPort")
		@MetaData(name="omniTicketPort", min = "0", defaultValue="9920", description="Omni (TOR) ticket server port")
		private int omniTicketPort;
		@PersistName("castAppCoreThreadPoolSize")
		@MetaData(name="castAppCoreThreadPoolSize", min="1", defaultValue="10", description="Core thread pool size to service cast app requests.")
		private int castAppCoreThreadPoolSize = 10;
		@PersistName("castAppMaxThreadPoolSize")
		@MetaData(name="castAppMaxThreadPoolSize", min="10", defaultValue="50", description="Maximum thread pool size to service cast app requests.")
		private int castAppMaxThreadPoolSize = 50;
		@PersistName("castAppThreadKeepAliveSec")
		@MetaData(name="castAppThreadKeepAliveSec", min="1", defaultValue="10", description="Number of seconds keep idle cast app threads over the core thread pool size.")
		private int castAppThreadKeepAliveSec = 10;
		// the choices for saveBioImages are "all", "failed", "none"
		@PersistName("saveBioImages")
		@MetaData(name="saveBioImages", choices={"all","failed","none"}, defaultValue="none", description="all - save all XBio images, failed - save images for blue events, none - do not save XBio images")
		private String saveBioImages;
		// For performance reasons we may wish to throtle down on saving of bio images
		// 1 - all, 2 - every second one, 3 - every third one and so on...
		@PersistName("saveBioImagesFrequency")
		@MetaData(name="saveBioImagesFrequency", min="1", defaultValue="1", description="1 - save every XBio image, 2 - save every second one, 3 - save every third one, etc. Allows one to reduce CPU/Network utilization.")
		private int saveBioImagesFrequency;
		// How long trying to re-connect to omni before purging all requests.
		@PersistName("omniconnecttimeoutms")
		@MetaData(name="omniconnecttimeoutms", min="1000", max="20000", defaultValue="4000", description="Try to connect to TOR for this number of milliseconds after which time reject all Entitlement requests.")
		private long omniConnectTimeoutMs;
		// How long to wait for a single omni response before giving up.
		@PersistName("omnirequesttimeoutms")
		@MetaData(name="omnirequesttimeoutms", min="1000", max="30000", defaultValue="15000", description="Used when connecting a reader to TOR and logging on a cast member. If TOR does not respond to the connect or login request during this timeout then a new request is sent.")
		private long omniRequestTimeoutMs;
		// Cast app logon timeout after message type="95" is received
		@PersistName("castlogontimeoutsec")
		@MetaData(name="castlogontimeoutsec", min="5", max="180", defaultValue="45", description="Reset the cast app login request if not tap is received for the specified number of seconds")
		private long castLogonTimeoutSec;
		// Retap timeout to guard against guest tapping after receiving green light
		@PersistName("guestretaptimeoutms")
		@MetaData(name="guestretaptimeoutms", min="0", max="20000", defaultValue="1000", description="Following a green light event, ignore the repeated guest taps for the specified number of milliseconds")
		private long guestRetapTimeoutMs;
		// Reader connection timeout.
		@PersistName("readerconnecttimeoutms")
		@MetaData(name="readerconnecttimeoutms", min="500", max="5000", defaultValue="2000", description="TCP/IP connection timeout when trying to talk to a reader. Keep this short as this will affect all processing when a reader is turned off during park operation.")
		private int readerConnectTimeoutMs;
		
		// Reader light effects
		@PersistName("castloginoklight")
		@MetaData(name="castloginoklight", choices={"green","blue","outer_green","inner_green","entry_success","entry_exception","entry_login_ok","entry_start_scan","entry_retry"}, defaultValue="entry_login_ok", description="Reader light sequence for successful cast member login.")
		private String castLoginOkLight;

		@PersistName("castloginokdurationms")
		@MetaData(name="castloginokdurationms", min="0", defaultValue="0", description="Reader light duration for successful cast member login.")
		private long castLoginOkDurationMs;
		
		@PersistName("startscanlight")
		@MetaData(name="startscanlight", choices={"green","blue","outer_green","inner_green","entry_success","entry_exception","entry_login_ok","entry_start_scan","entry_retry"}, defaultValue="entry_start_scan", description="Reader scanner light sequence to initiate a scan.")
		private String startScanLight;

		@PersistName("startscandurationms")
		@MetaData(name="startscandurationms", min="0", defaultValue="0", description="Reader scanner light duration to initiate a scan.")
		private long startScanDurationMs;
		
		@PersistName("flashcolors")
		@MetaData(name="flashcolors", defaultValue="#5a1414|#505000|#005028|#002850|#3c0f3c",  description="Pipe separated list of #RGB color values such as: #FF0000|#00FF00")
		private List<String> falshColors;
		
		@PersistName("castappresponsetimeutsec")
		@MetaData(name="castappresponsetimeutsec", min="1", max="3600", defaultValue="30", description="If a cast app makes a request to the xBRC and the xBRC fails to respond the request, return a HTTP 500 response after this timeout.")
		private int castAppResponseTimeoutSec;
		
		@PersistName("castappnotifytimeoutsec")
		@MetaData(name="castappnotifytimeoutsec", min="1", max="60", defaultValue="5", description="Number of seconds to keep reader notification messages for a cast app that is no longer subscribed.")
		private int castappNotifyTimeoutSec;
	
		@PersistName("readerflashtimems")
		@MetaData(name="readerflashtimems", min="0", defaultValue="3000", description="Reader flash duration.")
		private long readerFlashTimeMs;
		
		@PersistName("omniid")
		@MetaData(name="omniid", description="Omni Ticket user id")
		private String omniid;
		
		@PersistName("omnipassword")
		@MetaData(name="omnipassword", description="Omni Ticket user password")
		private String omnipassword;
		
		@PersistName("testmode")
		@MetaData(name="testmode", description="Allows for testing park entry without cast app login.")
		private boolean testMode = false;
		
		@PersistName("fastsingulation")
		@MetaData(name="fastsingulation", description="Use faster, but less flexible long range singulation method.")
		private boolean fastSingulation = true;
		
		@PersistName("lrrabandonmenttimesec")
		@MetaData(name="lrrabandonmenttimesec", min = "0", defaultValue="20", description="Assume that guest left the park entry area if no long range reads were detected for the specified number of seconds")
		private int lrrAbandonmentTimeSec;
		
		public Settings() {
		}
		
		@Override
		protected void initHook(Connection conn){}

		public int getMaxFpScanRetry() {
			return maxFpScanRetry;
		}

		public void setMaxFpScanRetry(int maxFpScanRetry) {
			this.maxFpScanRetry = maxFpScanRetry;
		}

		public int getOmniTicketPort() {
			return omniTicketPort;
		}

		public void setOmniTicketPort(int omniTicketPort) {
			this.omniTicketPort = omniTicketPort;
		}

		public int getAbandonmentTimeSec() {
			return abandonmentTimeSec;
		}

		public void setAbandonmentTimeSec(int abandonmentTimeSec) {
			this.abandonmentTimeSec = abandonmentTimeSec;
		}

		public String getSaveBioImages() {
			return saveBioImages;
		}

		public void setSaveBioImages(String saveBioImages) {
			this.saveBioImages = saveBioImages;
		}

		public int getGreenLightTimeoutMs() {
			return greenLightTimeoutMs;
		}

		public void setGreenLightTimeoutMs(int greenLightTimeoutMs) {
			this.greenLightTimeoutMs = greenLightTimeoutMs;
		}

		public long getOmniRequestTimeoutMs() {
			return omniRequestTimeoutMs;
		}

		public void setOmniRequestTimeoutMs(long omniRequestTimeout) {
			this.omniRequestTimeoutMs = omniRequestTimeout;
		}

		public long getCastLogonTimeoutSec() {
			return castLogonTimeoutSec;
		}

		public void setCastLogonTimeoutSec(long castLogonTimeoutSec) {
			this.castLogonTimeoutSec = castLogonTimeoutSec;
		}

		public long getOmniConnectTimeoutMs() {
			return omniConnectTimeoutMs;
		}

		public void setOmniConnectTimeoutMs(long omniConnectTimeoutMs) {
			this.omniConnectTimeoutMs = omniConnectTimeoutMs;
		}

		public long getGuestRetapTimeoutMs() {
			return guestRetapTimeoutMs;
		}

		public void setGuestRetapTimeoutMs(long guestRetapTimeoutMs) {
			this.guestRetapTimeoutMs = guestRetapTimeoutMs;
		}

		public int getReaderConnectTimeoutMs() {
			return readerConnectTimeoutMs;
		}

		public void setReaderConnectTimeoutMs(int readerConnectTimeoutMs) {
			this.readerConnectTimeoutMs = readerConnectTimeoutMs;
		}

		public int getSaveBioImagesFrequency() {
			return saveBioImagesFrequency;
		}

		public void setSaveBioImagesFrequency(int saveBioImagesFrequency) {
			this.saveBioImagesFrequency = saveBioImagesFrequency;
		}

		public String getCastLoginOkLight()
		{
			return castLoginOkLight;
		}

		public void setCastLoginOkLight(String castLoginOkLight)
		{
			this.castLoginOkLight = castLoginOkLight;
		}

		public long getCastLoginOkDurationMs()
		{
			return castLoginOkDurationMs;
		}

		public void setCastLoginOkDurationMs(long castLoginOkDurationMs)
		{
			this.castLoginOkDurationMs = castLoginOkDurationMs;
		}
		
		public String getStartScanLight()
		{
			return startScanLight;
		}

		public void setStartScanLight(String startScanLight)
		{
			this.startScanLight = startScanLight;
		}

		public long getStartScanDurationMs()
		{
			return startScanDurationMs;
		}

		public void setStartScanDurationMs(long startScanDurationMs)
		{
			this.startScanDurationMs = startScanDurationMs;
		}

		public int getCastAppCoreThreadPoolSize()
		{
			return castAppCoreThreadPoolSize;
		}

		public void setCastAppCoreThreadPoolSize(int castAppCoreThreadPoolSize)
		{
			this.castAppCoreThreadPoolSize = castAppCoreThreadPoolSize;
		}

		public int getCastAppMaxThreadPoolSize()
		{
			return castAppMaxThreadPoolSize;
		}

		public void setCastAppMaxThreadPoolSize(int castAppMaxThreadPoolSize)
		{
			this.castAppMaxThreadPoolSize = castAppMaxThreadPoolSize;
		}

		public int getCastAppThreadKeepAliveSec()
		{
			return castAppThreadKeepAliveSec;
		}

		public void setCastAppThreadKeepAliveSec(int castAppThreadKeepAliveSec)
		{
			this.castAppThreadKeepAliveSec = castAppThreadKeepAliveSec;
		}

		public int getCastAppResponseTimeoutSec()
		{
			return castAppResponseTimeoutSec;
		}

		public void setCastAppResponseTimeoutSec(int castAppResponseTimeoutSec)
		{
			this.castAppResponseTimeoutSec = castAppResponseTimeoutSec;
		}

		public long getReaderFlashTimeMs()
		{
			return readerFlashTimeMs;
		}

		public void setReaderFlashTimeMs(long readerFlashTimeMs)
		{
			this.readerFlashTimeMs = readerFlashTimeMs;
		}

		public List<String> getFalshColors()
		{
			return falshColors;
		}

		public void setFalshColors(List<String> falshColors)
		{
			this.falshColors = falshColors;
		}

		public String getOmniid()
		{
			return omniid;
		}

		public void setOmniid(String omniid)
		{
			this.omniid = omniid;
		}

		public String getOmnipassword()
		{
			return omnipassword;
		}

		public void setOmnipassword(String omnipassword)
		{
			this.omnipassword = omnipassword;
		}

		public boolean isTestMode()
		{
			return testMode;
		}

		public void setTestMode(boolean testMode)
		{
			this.testMode = testMode;
		}

		public int getCastappNotifyTimeoutSec()
		{
			return castappNotifyTimeoutSec;
		}

		public void setCastappNotifyTimeoutSec(int castappNotifyTimeoutSec)
		{
			this.castappNotifyTimeoutSec = castappNotifyTimeoutSec;
		}

		public boolean isFastSingulation() {
			return fastSingulation;
		}

		public void setFastSingulation(boolean fastSingulation) {
			this.fastSingulation = fastSingulation;
		}

		public int getLrrAbandonmentTimeSec() {
			return lrrAbandonmentTimeSec;
		}

		public void setLrrAbandonmentTimeSec(int lrrAbandonmentTimeSec) {
			this.lrrAbandonmentTimeSec = lrrAbandonmentTimeSec;
		}
	}	

	private ConfigOptions()
	{
		// create the reader info
		settings = new Settings();
	}
		
	public void readConfigurationOptionsFromDatabase() 
	{
		Connection conn = null;
		// read property/value pairs from the Config table
    	try 
    	{
    		// now, open up the database and see if we have anything set there
    	    conn = XBRCController.getInstance().getPooledConnection();
    	    Config configReader = Config.getInstance();
    	    configReader.read(conn, settings);
    	    
    	    try {
    	    	saveBioImages = SaveBioImages.valueOf(settings.getSaveBioImages());
    	    } catch( Exception e) {
    	    	logger.error("Invalid value for parameter saveBioImages: " + settings.getSaveBioImages() + 
    	    				" defaulting to all");
    	    	saveBioImages = SaveBioImages.all;
    	    }
    	    
    	    Collection<CastMember> castMemberList = CastMemberService.findAll(conn);
    	    castMembers = new HashMap<String,CastMember>();
    	    for (CastMember cm : castMemberList) {
    	    	castMembers.put(cm.getBandId(), cm);
    	    }
		} 
    	catch (Exception e) 
    	{
			logger.error("Failed to read configuration for " + settings.getClass().getName(), e);
		}
    	finally {
    		XBRCController.getInstance().releasePooledConnection(conn);
    	}
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public HashMap<String, CastMember> getCastMembers() {
		return castMembers;
	}

	public SaveBioImages getSaveBioImages() {
		return saveBioImages;
	}
}
