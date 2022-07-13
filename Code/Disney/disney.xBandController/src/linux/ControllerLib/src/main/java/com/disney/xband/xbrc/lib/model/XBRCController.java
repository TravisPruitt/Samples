package com.disney.xband.xbrc.lib.model;

import java.sql.Connection;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public abstract class XBRCController {

	private static Logger logger = Logger.getLogger(XBRCController.class);
	
	public static XBRCController getInstance() {
		return XBRControllerHolder.instance;
	}
	
	private static class XBRControllerHolder {
		public static XBRCController instance;

		static
		{
			String implementation = "com.disney.xband.xbrc.Controller.Controller";
			try {
				instance = (XBRCController)Class.forName(implementation).newInstance();
			} catch (Exception e) {
				logger.fatal("XBRController: Failed to create new instance of " + implementation, e);
			}
		}
	}
	
	public abstract Collection<LocationInfo> getReaderLocations(); 
	public abstract ReaderInfo getReader(String readerId);
	public abstract ReaderInfo getReader(String locationName, int lane);
	public abstract ReaderInfo getReaderFromDeviceId(Integer deviceId);
	public abstract Collection<ReaderInfo> getReaders(String locationName);
	public abstract String getXviewURL();
	public abstract String getVenueName();
	public abstract String getXbrcURL();
	public abstract String getXbrcVipURL();
	public abstract Collection<String> getXbrcAddresses();
	public abstract IXBRCConnector getConnector();
	public abstract Connection getPooledConnection() throws Exception;
	public abstract void releasePooledConnection(Connection conn);
	public abstract boolean isVerbose();
	public abstract boolean isReaderAlive(ReaderInfo ri);
	public abstract long shouldReaderBeOn(); // =0: Reader should be on.   >0 Reader should be off for milliseconds  
	public abstract Properties getProperties();
	public abstract XbrcStatus getStatus();
	public abstract String getHaStatus();
	public abstract String getSingulationAlgorithm();
	public abstract void logEKG(String s);
	public abstract boolean isAnyReaderAtLocationType(LocationType lt, BandMediaType bmt);
	public abstract String getPreferredGuestIdType();
	public abstract long getXBrTransmitterPeriod();
	public abstract boolean useSecureId();
    public abstract int setReaderSequence(ReaderInfo ri, Sequence sequence);
    public abstract void enableTapSequence(ReaderInfo ri, boolean on);
    public abstract void saveModelData(ReaderInfo ri);
    public abstract String encrypt(String sData);
    public abstract String decrypt(String sData);
    public abstract void disableReader(ReaderInfo ri, String disableReason);
    public abstract void enableReader(ReaderInfo ri);
    public abstract void setIdleSequence(ReaderInfo ri, boolean on);
    public abstract String getScriptsDirectory();
    public abstract Collection<String> getGuestIdentifierTypes();
    public abstract ReaderSequence getReaderSequence(ReaderInfo ri, Sequence sequence) throws Exception;
    public abstract String getReaderTestBandType();
    public abstract void processReaderTestTap(ReaderInfo ri, Xband xb, Guest guest) throws Exception;
}
