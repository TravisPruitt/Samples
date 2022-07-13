package com.disney.xband.xbrc.Controller;

import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.NGEPropertiesDecoder;
import com.disney.xband.common.lib.ScheduleClockHours;
import com.disney.xband.common.lib.Sequences;
import com.disney.xband.lib.xbrapi.LightMsg;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.lib.xbrapi.Sequence;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.lib.model.BandMediaType;
import com.disney.xband.xbrc.lib.model.IXBRCConnector;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.ReaderSequence;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;
import com.mchange.v2.c3p0.DataSources;

public class Controller extends XBRCController
{
	private static Logger logger = Logger.getLogger(Controller.class);
	private static DataSource pooled = null;
	private static Properties prop = new Properties();
	private static DateTimeFormatter jodaDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	private static NGEPropertiesDecoder ngeDecoder = null;
	private static Collection<String> myAddresses = null;

	static 
	{
		// initialize the Connection Pool and the properties
		InitializeC3P0();
		initializeMyAddresses();
	}	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		logger.info("Starting xBRC.");
		
		// parse command line
		int err = ParseCommandLine(args);
		if (err != 0)
		{
			System.err.println("Stopping xBRC. Failed to parse command line with error " + err);
			logger.error("Stopping xBRC. Failed to parse command line with error " + err);
			System.exit(err);
		}

		// Initialize the web server
		try
		{
			WebServer.INSTANCE.Initialize(prop);
		}
		catch (Exception e)
		{
			System.err.println("Stopping xBRC. Failed to initialize webserver: " + e);
			logger.error("Stopping xBRC. Failed to initialize webserver: ", e);
			System.exit(1);
		}

		// start the web server
		try
		{
			WebServer.INSTANCE.Start();
		}
		catch (Exception e)
		{
			System.err.println("Stopping xBRC. Failed to start webserver: " + e);
			logger.error("Stopping xBRC. Failed to start webserver: ", e);
			System.exit(1);
		}

		// process the configuration table and properties file
		while (true)
		{
			try
			{
				ConfigOptions.INSTANCE.readConfigurationOptionsFromDatabase();
				break;
			}
			catch (Exception e)
			{
				System.err.println("Failed to read configuration from the database: " + e);
				logger.error("Failed to read configuration from the database: ", e);
			}
			
			try
			{
				Thread.sleep(5000);
			}
			catch (Exception e) {}
		}
				
		Log.initialize(ConfigOptions.INSTANCE.getControllerInfo().isUselog4jforeventdump() ? 
				new EventdumpLog4jLog(ConfigOptions.INSTANCE.getControllerInfo().getEventdumpBufferSize()) : 
					new EventdumpFileLog());
		
		TransmitManager.INSTANCE.reinitialize(ConfigOptions.INSTANCE.getLocationInfo());
		JMSAgent.INSTANCE.initialize();
		HTTPAgent.INSTANCE.initialize();

		// now run the Processor
		err = Processor.INSTANCE.Process();
		if (err != 0)
		{
			logger.info("Stopping xBRC. Processor.INSTANCE.Process() returned " + err);
			System.exit(err);
		}

		WebServer.INSTANCE.Stop();
		
		logger.info("Stopping xBRC. Clean exit.");
		System.exit(0);
	}

	public static void Terminate()
	{
		// stop the processor (this will stop the web server, too)
		Processor.INSTANCE.Terminate();
		// stop the JMSAgent thread
		JMSAgent.INSTANCE.Terminate();
		// stop the HTTPAgent thread
		HTTPAgent.INSTANCE.Terminate();
	}

	private static int ParseCommandLine(String[] args)
	{
		for (int i = 0; i < args.length;)
		{
			String s = args[i];
			if (s.compareTo("--help") == 0 || s.compareTo("-?") == 0)
			{
				Usage();
				return 1;
			}
			else
			{
				System.out.println("Unexpected option: " + s);
				return 2;
			}
		}

		return 0;
	}

	private static void Usage()
	{
		System.out.println("Controller [OPTIONS]");
		System.out.println("OPTIONS:");
		System.out.println("    --help,-?    Show this usage information");
		System.out
				.println("The xBRC requires a properties.xml file in the execution directory.");

	}

	private static void InitializeC3P0()
	{	
		try
		{
			initializeNGEDecoder();
			prop = ngeDecoder.read();
		}
		catch (Exception ex)
		{
			logger.fatal("!! Could not read the properties file [" + 
					ngeDecoder.getPropertiesPath() + "]: "
						 + ex.getLocalizedMessage());
			System.exit(1);
		}

		// must have some properties
		if (prop.getProperty("nge.xconnect.xbrc.dbserver.service") == null
				|| prop.getProperty("nge.xconnect.xbrc.dbserver.uid") == null
				|| prop.getProperty("nge.xconnect.xbrc.dbserver.pwd") == null)
		{
			logger.fatal("!! Error: the environment.properties must contain, at least, settings for database, dbuser and dbpassword");
			System.exit(1);
		}

		String strUrl = prop.getProperty("nge.xconnect.xbrc.dbserver.service");
		String strUser = prop.getProperty("nge.xconnect.xbrc.dbserver.uid");
		String strPassword = prop.getProperty("nge.xconnect.xbrc.dbserver.pwd");

		try
		{
			logger.debug("Initializing database connection pool...");
			logger.debug("with database = " + strUrl + ", dbuser = " + strUser);

			Class.forName("com.mysql.jdbc.Driver");

			logger.debug("Initialized com.mysql.jdbc.Driver class");

			// Acquire the DataSource
			DataSource unpooled = DataSources.unpooledDataSource(strUrl,
					strUser, strPassword);

			HashMap<String, String> c3p0config = new HashMap<String, String>();

			String sMaxPoolSize = "20";
			String sMaxStatements = "400";
			String sMaxStatementsPerConnection = "50";

			if (prop.getProperty("nge.xconnect.xbrc.c3p0.maxPoolSize") != null)
				sMaxPoolSize = prop.getProperty("nge.xconnect.xbrc.c3p0.maxPoolSize");
			if (prop.getProperty("nge.xconnect.xbrc.c3p0.maxStatements") != null)
				sMaxStatements = prop.getProperty("nge.xconnect.xbrc.c3p0.maxStatements");
			if (prop.getProperty("nge.xconnect.xbrc.c3p0.maxStatementsPerConnection") != null)
				sMaxStatementsPerConnection = prop
						.getProperty("nge.xconnect.xbrc.c3p0.maxStatementsPerConnection");

			c3p0config.put("maxPoolSize", sMaxPoolSize);
			c3p0config.put("maxStatements", sMaxStatements);
			c3p0config.put("maxStatementsPerConnection",
					sMaxStatementsPerConnection);
			c3p0config.put("com.mchange.v2.log.MLog",
					"com.mchange.v2.log.log4j.Log4jMLog");
			c3p0config.put("idleConnectionTestPeriod", "600"); // seconds
			
			// Don't ever do this for production.
			//c3p0config.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "TRACE");

			pooled = DataSources.pooledDataSource(unpooled, c3p0config);
			
			//Logger.getLogger("com.mchange.v2").setLevel(Level.TRACE);

		}
		catch (ClassNotFoundException e)
		{
			logger.fatal(
					"Could not find the sql driver class. Failed to initialize connection pool",
					e);
			System.exit(1);
		}
		catch (SQLException e)
		{
			logger.fatal("Failed to initialize connection pool", e);
			System.exit(1);
		}

	}

	private static void initializeNGEDecoder() throws IOException
	{
		// first, read the properties file to get the database parameters
		ngeDecoder = new NGEPropertiesDecoder();
		
		// get property settings
		// is there is a system property to identify where the environemtn.properites is
		String sPropFile = System.getProperty("environment.properties");
		if (sPropFile != null)
		{
			logger.info("The xbrc.properites java argument is set. Using the " + sPropFile + " properties file.");
			ngeDecoder.setPropertiesPath(sPropFile);
		}
		
		String sJasyptPropFile = System.getProperty("jasypt.properties");
		if (sJasyptPropFile != null)
		{
			logger.info("The jasypt.properites java argument is set. Using the " + sJasyptPropFile + " properties file.");
			ngeDecoder.setJasyptPropertiesPath(sJasyptPropFile);
		}
		
		ngeDecoder.initialize();
	}
	
	private static void initializeMyAddresses() {
		Collection<String> ips = null;
		String hostname = null;
		
		try {
			ips = NetInterface.getOwnIpAddress(null);
		} catch (SocketException e) {
			logger.error("Failed to retrieve own IP addresses", e);
		}
		
		try {
			hostname = NetInterface.getHostname();
		} catch (Exception e) {
			logger.error("Failed to retrieve own hostname", e);
		}
		
		myAddresses = new LinkedList<String>();
		if (ips != null)
			myAddresses.addAll(ips);
		if (hostname != null)
			myAddresses.add(hostname);
	}

	@Override
	public ReaderInfo getReader(String readerId)
	{
		return ConfigOptions.INSTANCE.getReadersInfo().getReader(readerId);
	}

	@Override
	public ReaderInfo getReader(String locationName, int lane)
	{
		synchronized (ConfigOptions.INSTANCE.getReadersInfo())
		{
			for (ReaderInfo ri : ConfigOptions.INSTANCE.getReadersInfo()
					.getReaders())
			{
				if (ri.getLocation().getName().equalsIgnoreCase(locationName)
						&& ri.getLane() == lane)
					return ri;
			}
			return null;
		}
	}

	@Override
	public Collection<ReaderInfo> getReaders(String locationName)
	{
		synchronized (ConfigOptions.INSTANCE.getReadersInfo())
		{
			LinkedList<ReaderInfo> readers = new LinkedList<ReaderInfo>();
			for (ReaderInfo ri : ConfigOptions.INSTANCE.getReadersInfo()
					.getReaders())
			{
				if (locationName == null
						|| ri.getLocation().getName()
								.equalsIgnoreCase(locationName))
					readers.add(ri);
			}
			return readers;
		}
	}

	@Override
	public String getXviewURL()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getxViewURL();
	}

	@Override
	public IXBRCConnector getConnector()
	{
		return JMSAgent.INSTANCE;
	}

	@Override
	public Connection getPooledConnection() throws Exception
	{		
		return pooled.getConnection();
	}

	@Override
	public void releasePooledConnection(Connection conn)
	{
		try
		{
			if (conn != null && !conn.isClosed())
			{								
				conn.close();
			}
		}
		catch (Exception e)
		{
			logger.error("Failed to close connection", e);
		}
	}

	@Override
	public boolean isVerbose()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getVerbose();
	}

	@Override
	public String getVenueName()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getVenue();
	}

	@Override
	public String getXbrcURL()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getXbrcURL();
	}
	
	// returns regular URL if vip URL is not available
	@Override
	public String getXbrcVipURL()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getXbrcVipURL();
	}

	@Override
	public boolean isReaderAlive(ReaderInfo ri)
	{
		Date now = new Date();
		Long msSinceHello = now.getTime() - ri.getTimeLastHello();
		return msSinceHello < ConfigOptions.INSTANCE.getControllerInfo()
				.getReaderHelloTimeoutSec() * 1000;
	}

	@Override
	public long shouldReaderBeOn()
	{
		long millisUntilOn = 0;

		String powerOnTime = ConfigOptions.INSTANCE.getControllerInfo().getReaderPowerOnTime(); 
		String powerOffTime = ConfigOptions.INSTANCE.getControllerInfo().getReaderPowerOffTime();
		
		String overrideOn = ConfigOptions.INSTANCE.getControllerInfo().getReaderPowerOverrideDate();
		int overrideOnMinutes = ConfigOptions.INSTANCE.getControllerInfo().getReaderPowerOverrideMinutes();
		
		if (powerOnTime != null && !powerOnTime.isEmpty() && powerOffTime != null && !powerOffTime.isEmpty())
		{
			try
			{
				LocalTime on = new LocalTime(jodaDateFormatter.parseDateTime(powerOnTime));
				LocalTime off = new LocalTime(jodaDateFormatter.parseDateTime(powerOffTime));
						
				if (overrideOn != null && !overrideOn.equals(""))
				{
					DateTime overrideOnDateTime = jodaDateFormatter.withZone(DateTimeZone.UTC).parseDateTime(overrideOn);
			
					if (overrideOnDateTime == null)
					{
						throw new Exception("Could not parse readerPowerOnOverrideTime " + overrideOn);
					}
					if (overrideOnMinutes < 1)
					{
						throw new Exception("Invalid readerPowerOverrideMinutes");
					}
					
						
					ScheduleClockHours s = new ScheduleClockHours(on, off, overrideOnDateTime, overrideOnMinutes);
					millisUntilOn = s.getMillisUntilStart();
				}
				else
				{
					ScheduleClockHours s = new ScheduleClockHours(on, off);
					millisUntilOn = s.getMillisUntilStart();
				}
			}
			catch (Exception ex)
			{
				logger.error("Error scheduling reader on/off: ", ex);
			}
		}
		
		return millisUntilOn;
	}
	@Override
	public Properties getProperties()
	{
		return prop;
	}
	
	@Override
	public String getHaStatus()
	{
		return Processor.INSTANCE.getHaStatus();
	}

	@Override
	public XbrcStatus getStatus()
	{
		return Processor.INSTANCE.getStatusObject();
	}

	@Override
	public String getSingulationAlgorithm()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getSingulationAlgorithm();
	}

	@Override
	public void logEKG(String s)
	{
		Log.INSTANCE.logEKG(s);
	}

	@Override
	public boolean isAnyReaderAtLocationType(LocationType lt, BandMediaType bmt)
	{
		if (bmt == BandMediaType.Unknown)
			logger.warn("Cannot find out if band can be read at location type " + lt.name() + " because band media type is unknown");
		return ConfigOptions.INSTANCE.getReadersInfo().canBandBeReadAtLocationType(lt.ordinal(), bmt);
	}

	@Override
	public Collection<LocationInfo> getReaderLocations()
	{
		return ConfigOptions.INSTANCE.getLocationInfo().values();
	}

	@Override public String getPreferredGuestIdType()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getPreferredGuestIdType();
	}

	@Override
	public long getXBrTransmitterPeriod()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().getTransmitXbrPeriod();
	}

	@Override
	public boolean useSecureId()
	{
		return ConfigOptions.INSTANCE.getControllerInfo().isSecureTapId();
	}

    @Override
	public ReaderInfo getReaderFromDeviceId(Integer deviceId)
	{
		return ConfigOptions.INSTANCE.getReadersInfo().getReadersByDeviceId().get(deviceId);
	}
    
    @Override
    public void setIdleSequence(ReaderInfo ri, boolean on)
    {
    	if (on)
    	{
    		// set idle sequence
			String idleSequnce = ConfigOptions.INSTANCE.getControllerInfo().getsIdleSequence();
			LocationInfo locationInfo = ri.getLocation();
			if (locationInfo != null && locationInfo.getIdleSequence() != null && !locationInfo.getIdleSequence().isEmpty())
				idleSequnce = locationInfo.getIdleSequence();			
			
			// Allow location to override the sequence if necessary
			if (idleSequnce == null || idleSequnce.isEmpty() || idleSequnce.equals("off"))
				ReaderApi.deleteIdleSequence(ri);
			else
				ReaderApi.setIdleSequence(ri, idleSequnce);
    	}
    	else
    	{
    		ReaderApi.deleteIdleSequence(ri);
    	}
    }

    public ReaderSequence getReaderSequence(ReaderInfo ri, Sequence sequence) throws Exception
    {
    	if (ri==null || ri.getLocation()==null || sequence == null)
    		throw new Exception("Invalid arguments in call to getReaderSequence");

        Sequences sequences = null;
        Long timeout = 0l;
        String sLocationSequence;

        LocationInfo locationInfo = ri.getLocation();

        // Get the value from the reader location. If it's not found
        // then use the value at the xBRC level.
        switch (sequence) {
            case off:
                sequences = Sequences.parseSequenceNames("off");
                break;

            case error:
                sLocationSequence = locationInfo.getErrorSequence();
                if ( sLocationSequence != null && !sLocationSequence.isEmpty())
                {
                    sequences = Sequences.parseSequenceNames(sLocationSequence);
                    timeout = locationInfo.getErrorTimeout();
                }
                else
                {
                    sequences = Sequences.parseSequenceNames(ConfigOptions.INSTANCE.getControllerInfo().getErrorSequence());
                    timeout = ConfigOptions.INSTANCE.getControllerInfo().getErrorTimeout();
                }
                break;

            case success:
                sLocationSequence = locationInfo.getSuccessSequence();
                if ( sLocationSequence != null && !sLocationSequence.isEmpty())
                {
                    sequences = Sequences.parseSequenceNames(sLocationSequence);
                    timeout = locationInfo.getSuccessTimeout();
                }
                else
                {
                    sequences = Sequences.parseSequenceNames(ConfigOptions.INSTANCE.getControllerInfo().getSuccessSequence());
                    timeout = ConfigOptions.INSTANCE.getControllerInfo().getSuccessTimeout();
                }
                break;

            case failure:
                sLocationSequence = locationInfo.getFailureSequence();
                if ( sLocationSequence != null && !sLocationSequence.isEmpty())
                {
                    sequences = Sequences.parseSequenceNames(sLocationSequence);
                    timeout = locationInfo.getFailureTimeout();
                }
                else
                {
                    sequences = Sequences.parseSequenceNames(ConfigOptions.INSTANCE.getControllerInfo().getFailureSequence());
                    timeout = ConfigOptions.INSTANCE.getControllerInfo().getFailureTimeout();
                }
                break;

            default:
            	throw new Exception("Unable to play sequence because Sequences object is null (ReaderName=" + ri.getName() + ")");
        }

        com.disney.xband.common.lib.Sequence selectedSequence = sequences.getRandom();
        
        if ( selectedSequence != null &&
                selectedSequence.getName() != null &&
                !selectedSequence.getName().isEmpty() &&
                !selectedSequence.equals("null"))
        {
            // Get the timeout if it has been configured.
            if ( selectedSequence.getTimeout() != null )
                timeout = selectedSequence.getTimeout();
            
            return new ReaderSequence(selectedSequence.getName(), timeout);              
        }
        
        return null;
    }
    
    @Override
    public int setReaderSequence(ReaderInfo ri, Sequence sequence)
    {
    	ReaderSequence readerSequence = null;
    	
    	try
    	{
    		readerSequence = getReaderSequence(ri, sequence);
    		
    		if (readerSequence == null)
    		{
    			logger.warn("The " + sequence.name() + " sequence is not configured. Cannot play sequence on reader " + ri.getName());
    			return 500;
    		}
    		
    		logger.trace("Sending sequence to reader: reader=" + ri.getName() + ";media sequence=" + sequence.name());
    		
    		if (LightMsg.isColorValue(readerSequence.getName()))
            {
            	return ReaderApi.setReaderColor(ri, readerSequence.getName(), readerSequence.getTimeout());
            }
            else
            {
                return ReaderApi.setReaderSequence(ri, readerSequence.getName(), readerSequence.getTimeout());
            }    		
    	}
    	catch(Exception e)
    	{
    		logger.warn("Failed to play the " + (readerSequence != null ? readerSequence.getName() : sequence.name()) + " sequence on reader " + ri.getName(), e);
			return 500;
    	}
    }

	@Override
	public void saveModelData(ReaderInfo ri)
	{	
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = getPooledConnection();
			String sSQL = "UPDATE Reader set modelData=? WHERE id = ?";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setString(1, ri.getModelData());
			pstmt.setInt(2, ri.getId());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader IP modelData", e));
		}
		finally
		{
			try
			{
				if (pstmt != null) {
					pstmt.close();
                }
			}
			catch (Exception ex)
			{
			}

			releasePooledConnection(dbConn);
		}
	}
	
	@Override
	public void disableReader(ReaderInfo ri, String disableReason)
	{
		enableReader(ri, false, disableReason);
	}
	
	@Override
	public void enableReader(ReaderInfo ri)
	{
		enableReader(ri, true, null);
	}
	
	private void enableReader(ReaderInfo ri, boolean enabled, String disabledReason)
	{
		ri.setEnabled(enabled);
		ri.setDisabledReason(disabledReason);
		
		Connection dbConn = null;
		PreparedStatement pstmt = null;
		try
		{
			dbConn = getPooledConnection();
			String sSQL = "UPDATE Reader set enabled=?, disabledReason=? WHERE id = ?";
			pstmt = dbConn.prepareStatement(sSQL);
			pstmt.setBoolean(1, ri.isEnabled());
			pstmt.setString(2, ri.getDisabledReason() == null ? "" : ri.getDisabledReason());
			pstmt.setInt(3, ri.getId());
			pstmt.execute();
		}
		catch (Exception e)
		{
			logger.error(ExceptionFormatter.format(
					"Error updating reader IP modelData", e));
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (Exception ex)
			{
			}
			if (dbConn != null)
				releasePooledConnection(dbConn);
		}
	}

	@Override
	public String encrypt(String sData)
	{
		try
		{
			return ngeDecoder.encrypt(sData);
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format("Encryption error", e));
			return sData;
		}
	}

	@Override
	public String decrypt(String sData)
	{
		try
		{
			return ngeDecoder.decrypt(sData);
		}
		catch (IOException e)
		{
			logger.error(ExceptionFormatter.format("Decryption error", e));
			return sData;
		}
	}

	@Override
	public void enableTapSequence(ReaderInfo ri, boolean enable)
	{
		if (ri==null || ri.getLocation()==null)
    	{
    		logger.error("Invalid arguments in call to setReaderSequence");
    		return;
    	}

        Long timeout = 0l;
        String sequence = null;       

        LocationInfo locationInfo = ri.getLocation();

        sequence = locationInfo.getTapSequence();
	    if (sequence != null && !sequence.isEmpty())
	    {
	        timeout = locationInfo.getTapTimeout();
	    }
	    else
	    {
	    	sequence = ConfigOptions.INSTANCE.getControllerInfo().getTapSequence();
	        timeout = ConfigOptions.INSTANCE.getControllerInfo().getTapTimeout();   
	    }
          
        try
        {
            if ( sequence != null && !sequence.equals("null"))
            {
            	if (enable == false)
            	{
            		sequence = "off";
            		timeout = null;
            	}
            	
            	ReaderApi.setTapOptions(ri, sequence, timeout);            
            }
        }
        catch(Exception ex)
        {
            logger.warn("Unable to set tap sequence: ReaderName=" + ri.getName() + ";Sequence=" + sequence +
                    " : " + ex.getLocalizedMessage());
        }		
	}

	@Override
	public String getScriptsDirectory() {
		return "/usr/share/xbrc/scripts";
	}

	@Override
	public Collection<String> getGuestIdentifierTypes() {
		return ConfigOptions.INSTANCE.getControllerInfo().getGuestIdentifierTypes();
	}

	@Override
	public String getReaderTestBandType() {
		return ConfigOptions.INSTANCE.getControllerInfo().getReaderTestBandType();
	}

	@Override
	public void processReaderTestTap(ReaderInfo ri, Xband xb, Guest guest) throws Exception {
		ReaderTestCoordinator.INSTANCE.onTap(ri, xb, guest);
	}
	
	public Collection<String> getXbrcAddresses() {
		Collection<String> ret = new LinkedList<String>();
		ret.addAll(myAddresses);
		if (ConfigOptions.INSTANCE.getControllerInfo().getVipAddress() != null && !ConfigOptions.INSTANCE.getControllerInfo().getVipAddress().isEmpty()
				&& !ConfigOptions.INSTANCE.getControllerInfo().getVipAddress().equals("#"))
			ret.add(ConfigOptions.INSTANCE.getControllerInfo().getVipAddress());
		
		return ret;
	}
}
