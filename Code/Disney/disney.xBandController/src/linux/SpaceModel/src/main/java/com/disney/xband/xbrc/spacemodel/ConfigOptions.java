package com.disney.xband.xbrc.spacemodel;


import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.Configuration;
import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class ConfigOptions extends Configuration
{
	// singleton
	public static final ConfigOptions INSTANCE = new ConfigOptions();
	private static Logger logger = Logger.getLogger(ConfigOptions.class);
	
	// class data
	private ControllerInfo ci;
	
	@Override
	protected void initHook(Connection conn){}
	
	@PersistName("SpaceModelConfig")
	public class ControllerInfo extends Configuration
	{
		@PersistName("abandonmenttimeout_msec")
		@MetaData(name="abandonmenttimeout_msec", min = "0", defaultValue = "3000", description="A guest is considered having abanonded the attraction/location if no events are received for the specified number of milliseconds.")	// 3 seconds by default
		private long cmsecAbandonmentTimeout;
		
		@Override
		protected void initHook(Connection conn){}
		
		public long getAbandonmentTimeout()
		{
			return cmsecAbandonmentTimeout;
		}

		public void setAbandonmentTimeout( long cmsecAbandonmentTimeout )
		{
			this.cmsecAbandonmentTimeout = cmsecAbandonmentTimeout;
		}

        @PersistName("castmemberdetectdelay_msec")
        @MetaData(name="castmemberdetectdelay_msec", min = "0", defaultValue = "0", description="The number of milliseconds during which to collect cast member events before performing location confidence calculation")	// 1 seconds by default
        private long castMemberDetectDelay;

        public long getCastMemberDetectDelay() {
            return castMemberDetectDelay;
        }

        public void setCastMemberDetectDelay( long castMemberDetectDelay ) {
            this.castMemberDetectDelay = castMemberDetectDelay;
        }

        @PersistName("puckdetectdelay_msec")
        @MetaData(name="puckdetectdelay_msec", min = "0", defaultValue = "0", description="The number of milliseconds during which to collect puck events before performing location confidence calculation")	// 1 seconds by default
        private long puckDetectDelay;

        public long getPuckDetectDelay() {
            return puckDetectDelay;
        }

        public void setPuckDetectDelay( long puckDetectDelay ) {
            this.puckDetectDelay = puckDetectDelay;
        }

        @PersistName("guestdetectdelay_msec")
        @MetaData(name="guestdetectdelay_msec", min = "0", defaultValue = "0", description="The number of milliseconds during which to collect guest events before performing location confidence calculation")	// 10 seconds by default
        private long guestDetectDelay;

        public long getGuestDetectDelay() {
            return guestDetectDelay;
        }

        public void setGuestDetectDelay( long guestDetectDelay ) {
            this.guestDetectDelay = guestDetectDelay;
        }

        @PersistName("confidencedelta")
        @MetaData(name="confidencedelta", min = "0", defaultValue = "10", description="Send out periodic CONF messages whenever the confidence varies by the specified percentage")
        private int confidenceDelta;

        public int getConfidenceDelta() {
            return confidenceDelta;
        }

        public void setConfidenceDelta( int confidenceDelta ) {
            this.confidenceDelta = confidenceDelta;
        }

        @PersistName("locationeventratio")
        @MetaData(name="locationeventratio", min = "0", defaultValue = "70", description="The percentage of required long range events collected during the detect delay period in order to perform confidence calculation.")
        private int locationEventRatio;

        public int getLocationEventRatio() {
            return locationEventRatio;
        }

        public void setLocationEventRatio( int locationEventRatio ) {
            this.locationEventRatio = locationEventRatio;
        }

        @PersistName("chirprate_msec")
        @MetaData(name="chirprate_msec", min = "0", defaultValue = "1000", description="The expected band chirp rate in missliseconds. The default is 1000.")
        private long msecChirpRate;

        public long getChirpRate() {
            return msecChirpRate;
        }

        public void setChirpRate( long msecChirpRate ) {
            this.msecChirpRate = msecChirpRate;
        }

        @PersistName("usequeue")
        @MetaData(name="usequeue", defaultValue = "false", description="Whether to use a limited queue to store long range events. The limited queue will only allow storage of the number of events calculated by dividing the detect delay by the band chirp rate.")
        private boolean bUseQueue;

        public boolean getUseQueue() {
            return this.bUseQueue;
        }

        public void setUseQueue( boolean bUseQueue ) {
            this.bUseQueue = bUseQueue;
        }

        @PersistName("usepassfilters")
        @MetaData(name="usepassfilters", defaultValue = "false", description="If set to false then the calculated confidence is the average of signal strength for all collected lrr events. If set to true, the confidence is calculated using a weighted arithmetic mean.")
        private boolean bUsePassFilters;

        public boolean getUsePassFilters() {
            return this.bUsePassFilters;
        }

        public void setUsePassFilters( boolean bUsePassFilters ) {
            this.bUsePassFilters = bUsePassFilters;
        }

        @PersistName("sendUnregisteredBandEvents")
        @MetaData(name="sendUnregisteredBandEvents", defaultValue = "false", description="Whether to send LOCATION event messages in response to TAP events where the xBRC failed to lookup a guest in IDMS.")
        private boolean bSendUnregisteredBandEvents;

        public boolean getSendUnregisteredBandEvents() {
            return this.bSendUnregisteredBandEvents;
        }

        public void setSendUnregisteredBandEvents( boolean bSendUnregisteredBandEvents ) {
            this.bSendUnregisteredBandEvents = bSendUnregisteredBandEvents;
        }

        @PersistName("maxageevents_msec")
        @MetaData(name="maxageevents_msec", defaultValue = "0", description="Whether to discard any long range events older than the specified number of milliseconds.")
        private long maxageevents;

        public long getMaxAgeEvents() {
            return this.maxageevents;
        }

        public void setMaxAgeEvents( long maxAgeEvents ) {
            this.maxageevents = maxAgeEvents;
        }
    }

	private ConfigOptions()
	{
		ci = new ControllerInfo();
	}
	
	public ControllerInfo getControllerInfo() 
	{
		return ci;
	}
		
	public void readConfigurationOptionsFromDatabase() 
	{
    	Connection conn = null;
    	
    	// read property/value pairs from the Config table
    	try 
    	{
    		// now, open up the database and see if we have anything set there
    	    conn = XBRCController.getInstance().getPooledConnection();    	    
    	    Config config = Config.getInstance();
			config.read(conn, ci);
		} 
    	catch (Exception e) 
    	{
			logger.error("Failed to read configuration for " + ci.getClass().getName(), e);
		}
    	finally
    	{
    		if (conn!=null)
    			XBRCController.getInstance().releasePooledConnection(conn);
    	}
	    
	}

}
