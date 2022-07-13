package com.disney.xband.xbrc.lib.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@XmlType(propOrder={"name","id","type","macAddress","ipAddress","port","gain","signalStrengthThreshold",
					"lane","deviceId","x","y","timeLastHello","status","statusMessage", "minXbrcVersion", "version", "linuxVersion",
					"rfidDescription","rfidFwVersion","xbioFwVersion","xbioHwVersion","xbioSerialNumber", "transmitter", "transmitPayload",
					"useSecureId", "mediaPackageHash", "hardwareType","transmitterHaPriority", "modelData", "disabledReason", 
					"enabled", "bioDeviceType", "batteryLevel", "batteryTime", "temperature", "antennas", "lastReaderTestTime",
					"lastReaderTestSuccess", "lastReaderTestUser"})

public class ReaderInfo
{	
	public static Logger logger = Logger.getLogger(ReaderInfo.class);
	private static transient Pattern illegalChars = Pattern.compile("\r|\n|\r\n|<|>");
	private transient boolean sentHelloSettings = false;
	private transient boolean sentDiagnosticSettings = false;
	private transient StatusType deviceStatus;
	private transient String deviceStatusMessage;

	
	private int id;
	private String readerName;
	private ReaderType type;
	private String group;
	private String singulationGroup;
	private int signalStrengthThreshold;
	private double gain;
	private String macAddress;
	private String ipAddress;
	private int port;
	private long lastIDReceived;
	private LocationInfo location;
	private long timeLastHello;
	private boolean simulated;
	private int lane;
	private int deviceId;
	private int x, y;
	private StatusType status;	// "Red", "Green" or "Yellow"
	private String statusMessage;	// explanatory text for "Red" or "Yellow"
	private String version;
	/** Version of the linux operating system the reader is running **/
	private String linuxVersion;
	private String minXbrcVersion;
	private String lastVersionCheckMessage = null;
	/** Description provided by Feig Firmware **/
	private String rfidDescription;
	/** Firmware version provided by Feig Firmware **/
	private String rfidFwVersion;
	/** Finger print reader firmware version **/
	private String xbioFwVersion;
	/** Finger print reader hardware version **/
	private String xbioHwVersion;
	/** Finger print reader serial number **/
	private int xbioSerialNumber;
	private boolean bTransmitter = false;
	private String transmitPayload;
	private boolean useSecureId = false;
    private String mediaPackageHash = null;
    private int transmitterHaPriority = 1;
    private String hardwareType = null;
    private String modelData;
    private String disabledReason;
    private boolean enabled = true;
    private int bioDeviceType;
    private boolean invalidHeader = false;
    
	private Integer batteryLevel = null;
	private Integer batteryTime = null;
	private Double temperature = null;
	
	private Date lastReaderTestTime;
	private boolean lastReaderTestSuccess;
	private String lastReaderTestUser;

	private ArrayList<Boolean> antennas = null;

	/**
	 * @return the logger
	 */
	public static Logger getLogger()
	{
		return logger;
	}

	/**
	 * @return the linuxVersion
	 */
	public String getLinuxVersion()
	{
		return linuxVersion;
	}

	/**
	 * @return the rfidDescription
	 */
	public String getRfidDescription()
	{
		return rfidDescription;
	}

	/**
	 * @return the rfidFwVersion
	 */
	public String getRfidFwVersion()
	{
		return rfidFwVersion;
	}

	/**
	 * @return the xbioFwVersion
	 */
	public String getXbioFwVersion()
	{
		return xbioFwVersion;
	}

	/**
	 * @return the xbioHwVersion
	 */
	public String getXbioHwVersion()
	{
		return xbioHwVersion;
	}

	/**
	 * @return the xbioSerialNumber
	 */
	public int getXbioSerialNumber()
	{
		return xbioSerialNumber;
	}

	/**
	 * @param logger the logger to set
	 */
	public static void setLogger(Logger logger)
	{
		ReaderInfo.logger = logger;
	}

	/**
	 * @param linuxVersion the linuxVersion to set
	 */
	public void setLinuxVersion(String linuxVersion)
	{
		this.linuxVersion = linuxVersion;
	}

	/**
	 * @param rfidDescription the rfidDescription to set
	 */
	public void setRfidDescription(String rfidDescription)
	{
		this.rfidDescription = rfidDescription;
	}

	/**
	 * @param rfidFwVersion the rfidFwVersion to set
	 */
	public void setRfidFwVersion(String rfidFwVersion)
	{
		this.rfidFwVersion = rfidFwVersion;
	}

	/**
	 * @param xbioFwVersion the xbioFwVersion to set
	 */
	public void setXbioFwVersion(String xbioFwVersion)
	{
		this.xbioFwVersion = xbioFwVersion;
	}

	/**
	 * @param xbioHwVersion the xbioHwVersion to set
	 */
	public void setXbioHwVersion(String xbioHwVersion)
	{
		this.xbioHwVersion = xbioHwVersion;
	}

	/**
	 * @param xbioSerialNumber the xbioSerialNumber to set
	 */
	public void setXbioSerialNumber(int xbioSerialNumber)
	{
		this.xbioSerialNumber = xbioSerialNumber;
	}

	public ReaderInfo()
	{
	}
	
	public ReaderInfo(int id, String sReaderName, ReaderType type, String sGroup,
			String sSingulationGroup, int nPort,
			int nSignalStrengthThreshold, double dGain, String sMACAddress,
			String sIpAddress,
			long lLastIDReceived,
			long lTimeLastHello,
			boolean simulated,
			int lane,
			int deviceId,
			int x,
			int y,
			String version,
			String minXbrcVersion,
			boolean bTransmitter,
			String transmitPayload,
			boolean useSecureId,
			String hardwareType,
			String disabledReason,
			boolean enabled,
			int bioDeviceType,
			int transmitterHaPriority,
			Date lastReaderTestTime,
			boolean lastReaderTestSuccess,
			String lastReaderTestUser)
	{
		this.id = id;
		this.readerName = sReaderName;
		this.type = type;
		this.group = sGroup;
		this.singulationGroup = sSingulationGroup;
		this.port = nPort;
		this.signalStrengthThreshold = nSignalStrengthThreshold;
		this.gain = dGain;
		this.macAddress = sMACAddress;
		this.ipAddress = sIpAddress;
		this.lastIDReceived = lLastIDReceived;
		this.location = null;
		this.timeLastHello = lTimeLastHello;
		this.simulated = simulated;
		this.lane = lane;
		this.deviceId = deviceId;
		this.x = x;
		this.y = y;
		this.version = version;
		this.minXbrcVersion = minXbrcVersion;
		this.bTransmitter = bTransmitter;
		this.transmitPayload = transmitPayload;
		this.hardwareType = hardwareType;
		this.disabledReason = disabledReason;
		this.enabled = enabled;
		this.bioDeviceType = bioDeviceType;
		this.transmitterHaPriority = transmitterHaPriority;
		this.lastReaderTestTime = lastReaderTestTime;
		this.lastReaderTestSuccess = lastReaderTestSuccess;
		this.lastReaderTestUser = lastReaderTestUser;
		
		// initial state
		status = StatusType.Green;
		statusMessage = "";
		
		// only touch points work with secure IDs
		if (this.type.isCanProcessSecureId())
			this.useSecureId = useSecureId;
		else
			this.useSecureId = false;
	}
	
	public String getName()
	{
		return readerName;
	}
	public void setName(String name){
		readerName = name;
	}
	@XmlTransient
	@JsonIgnore
	public String getGroup()
	{
		return group;
	}
	public void setGroup(String group){
		this.group = group;
	}
	@XmlTransient
	@JsonIgnore
	public String getSingulationGroup()
	{
		return singulationGroup;
	}
	@XmlTransient
	@JsonIgnore
	public String getURL()
	{
		if (simulated && (type == ReaderType.xfp || type == ReaderType.xfpxbio))
			return "http://" + ipAddress + ":" + port + "/Xfpe/restful/" + readerName;
		
		// synthesize this
		return "http://" + ipAddress + ":" + port;
	}
	
	@XmlElement(name="threshold")
	public int getSignalStrengthThreshold()
	{
		return signalStrengthThreshold;
	}
	
	public double getGain()
	{
		return gain;
	}
	public void setGain(double gain){
		this.gain = gain;
	}	

	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	@XmlElement(name="ipaddress")
	public String getIpAddress()
	{
		return ipAddress;
	}
	
	public void setIpAddress(String sIpAddress)
	{
		this.ipAddress = sIpAddress;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port) 
	{
		this.port = port;
	}
	@XmlTransient
	@JsonIgnore
	public long getLastIDReceived()
	{
		return lastIDReceived;
	}
	
	public void setLastIDReceived(long lLastIDReceived)
	{
		this.lastIDReceived = lLastIDReceived;
	}
	@XmlTransient
	@JsonIgnore
	public LocationInfo getLocation()
	{
		return this.location;
	}
	
	public void setLocation(LocationInfo location)
	{
		this.location = location;
	}
	@XmlElement(name="timelasthello")
	public long getTimeLastHello()
	{
		return this.timeLastHello;
	}
	
	public String getFormattedTimeLastHello(String format){
		if (format == null || format.trim().isEmpty())
			format = "MM-dd-yyyy'T'HH:mm:ss";
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);		
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(timeLastHello);
	}
	
	@XmlTransient
	@JsonIgnore
	public long getTimeSinceLastHello()
	{
		long since = 0;
		Date now = new Date();
		since = now.getTime() - timeLastHello;
		return since;
	}
	
	public void setTimeLastHello(long lTimeLastHello)
	{
		this.timeLastHello = lTimeLastHello;
	}

	public String getHardwareType()
	{
		return hardwareType;
	}

	public void setHardwareType(String hardwareType)
	{
		this.hardwareType = hardwareType;
	}

	public boolean supportsMediaPackages()
	{
		return hardwareType != null && !hardwareType.equals("xTP1");
	}
	
	@XmlTransient
	@JsonIgnore
	public boolean isSimulated() 
	{
		if (simulated == false)
		{
			// treat zero same as null, otherwise we will start treating real readers as simulated
			if (x > 0 || y > 0)
				return true;
		}
		
		return simulated;
	}

	public void setSimulated(boolean simulated) 
	{
		this.simulated = simulated;
	}

	public int getLane() 
	{
		return lane;
	}

	public void setLane(int lane) 
	{
		this.lane = lane;
	}

	/**
	 * @return the type
	 */
	public ReaderType getType() 
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ReaderType type) 
	{
		this.type = type;
	}
	@XmlElement(name="deviceid")
	public int getDeviceId() 
	{
		return deviceId;
	}

	public void setDeviceId(int deviceId) 
	{
		this.deviceId = deviceId;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}

	public StatusType getStatus() 
	{
		return status;
	}

	public void setStatus(StatusType status) 
	{
		this.status = status;
	}

	public String getStatusMessage() 
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) 
	{
		if (statusMessage == null)
			this.statusMessage = null;
		
		this.statusMessage = illegalChars.matcher(statusMessage).replaceAll(" ");
	}

	public String getVersion() 
	{
		return version;
	}

	public void setVersion(String version) 
	{
		this.version = version;
	}
	
	public String getNormalizedVersion() 
	{
		// same version but only up to the first space
		int iSpace = this.version.indexOf(' ');
		if (iSpace > 0)
			return this.version.substring(0,iSpace);
		return this.version;
	}

	public String getMinXbrcVersion() 
	{
		return minXbrcVersion;
	}

	public void setMinXbrcVersion(String minXbrcVersion) 
	{
		this.minXbrcVersion = minXbrcVersion;
	}

	@XmlTransient
	@JsonIgnore
	public String getLastVersionCheckMessage()
	{
		return lastVersionCheckMessage;
	}

	public void setLastVersionCheckMessage(String lastVersionCheckMessage)
	{
		this.lastVersionCheckMessage = lastVersionCheckMessage;
	}

	@XmlElement(name="macaddress")
	public String getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	}

	public void setSingulationGroup(String singulationGroup)
	{
		this.singulationGroup = singulationGroup;
	}

	public void setSignalStrengthThreshold(int signalStrengthThreshold)
	{
		this.signalStrengthThreshold = signalStrengthThreshold;
	}
	
	@XmlElement(name="transmitter")
	public boolean isTransmitter()
	{
		return bTransmitter;
	}
	
	public void setTransmitter(boolean bTransmitter)
	{
		this.bTransmitter = bTransmitter;
	}
	
	@XmlElement
	public String getTransmitPayload()
	{
		return transmitPayload;
	}

	public void setTransmitPayload(String transmitPayload)
	{
		this.transmitPayload = transmitPayload;
	}

	@XmlElement
	public boolean isUseSecureId()
	{
		return useSecureId;
	}

	public void setUseSecureId(boolean useSecureId)
	{
		this.useSecureId = useSecureId;
	}

    public String getMediaPackageHash() {
        return mediaPackageHash;
    }

    public void setMediaPackageHash(String sMediaVersionHash) {
        this.mediaPackageHash = sMediaVersionHash;
    }
	
	public boolean isTapReader() {
		if (type != null)
			return ReaderType.isTapReader(type);
		
		return false;
	}

	public String getModelData()
	{
		return modelData;
	}

	public void setModelData(String modelData)
	{
		this.modelData = modelData;
	}
	
	@XmlTransient
	@JsonIgnore
	public boolean isInvalidHeader()
	{
		return invalidHeader;
	}
	
	public void setInvalidHeader(boolean comm)
	{
		this.invalidHeader = comm;
	}

	@XmlTransient
	@JsonIgnore
	public boolean isSentHelloSettings()
	{
		return sentHelloSettings;
	}

	public void setSentHelloSettings(boolean sentHelloSettings)
	{
		this.sentHelloSettings = sentHelloSettings;
	}
	
	@XmlTransient
	@JsonIgnore
	public boolean isSentDiagnosticSettings() {
		return sentDiagnosticSettings;
	}

	public void setSentDiagnosticSettings(boolean sentDiagnosticSettings) {
		this.sentDiagnosticSettings = sentDiagnosticSettings;
	}

	public static boolean validateName(String sName)
	{
		return !illegalChars.matcher(sName).matches();
	}

	public String getDisabledReason()
	{
		return disabledReason;
	}

	public void setDisabledReason(String disabledReason)
	{
		this.disabledReason = disabledReason;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	/**
	 * Use to sort a collection of <code>ReaderInfo</code> objects according
	 * to their health status. This comparator uses the natural sort order
	 * of the <code>StatusType</code> object.
	 * 
	 * @author Iwona Glabek
	 *
	 */
	public class HealthStatusComparator implements Comparator<ReaderInfo>
	{
		public int compare(ReaderInfo ri1, ReaderInfo ri2)
		{
			StatusType ri1Status = ri1.getStatus();
			StatusType ri2Status = ri2.getStatus();
			
			return StatusType.compare(ri1Status, ri2Status);
		}
	}
	
	/**
	 * Use to sort a collection of <code>ReaderInfo</code> objects first according
	 * to their health status and then by name in lexicographical order. 
	 * This comparator uses the natural sort order of the <code>StatusType</code> object.
	 * 
	 * @author Iwona Glabek
	 *
	 */
	public class HealthStatusAndNameComparator implements Comparator<ReaderInfo>
	{
		public int compare(ReaderInfo ri1, ReaderInfo ri2)
		{
			StatusType ri1Status = ri1.getStatus();
			StatusType ri2Status = ri2.getStatus();
			
			int result =  StatusType.compare(ri1Status, ri2Status);
			
			if (result != 0)
				return result;
			
			// sort by reader name
			String ri1Name = ri1.getName();
			String ri2Name = ri2.getName();
			
			if (ri1Name == null || ri2Name == null)
				return result;
			
			return ri1Name.compareTo(ri2Name);
		}
	}

	public int getBioDeviceType()
	{
		return bioDeviceType;
	}

	public void setBioDeviceType(int bioDeviceType)
	{
		this.bioDeviceType = bioDeviceType;
	}

	public Integer getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Integer batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public Integer getBatteryTime() {
		return batteryTime;
	}

	public void setBatteryTime(Integer batteryTime) {
		this.batteryTime = batteryTime;
	}
	
	public void setRadio0Power(Boolean power) {
		if (antennas == null)
		{
			antennas = new ArrayList<Boolean>(8);
			for (int i = 0; i < 8; i++)
				antennas.add(true);
		}
		antennas.set(0, power);
		antennas.set(1, power);
	}

	public void setRadio1Power(Boolean power) {
		if (antennas == null)
		{
			antennas = new ArrayList<Boolean>(8);
			for (int i = 0; i < 8; i++)
				antennas.add(true);
		}
		antennas.set(2, power);
		antennas.set(3, power);
	}

	public void setRadio2Power(Boolean power) {
		if (antennas == null)
		{
			antennas = new ArrayList<Boolean>(8);
			for (int i = 0; i < 8; i++)
				antennas.add(true);
		}
		antennas.set(4, power);
		antennas.set(5, power);
	}

	public void setRadio3Power(Boolean power) {
		if (antennas == null)
		{
			antennas = new ArrayList<Boolean>(8);
			for (int i = 0; i < 8; i++)
				antennas.add(true);
		}
		antennas.set(6, power);
		antennas.set(7, power);
	}
	
	public ArrayList<Boolean> getAntennas() {
		return antennas;
	}
	
	public void setAntennas(ArrayList<Boolean> antennas) throws Exception {
		if (antennas == null || (antennas != null && antennas.size() != 0))
			this.antennas = antennas;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	@XmlTransient
	@JsonIgnore
	public StatusType getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(StatusType deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	
	@XmlTransient
	@JsonIgnore
	public String getDeviceStatusMessage() {
		return deviceStatusMessage;
	}

	public void setDeviceStatusMessage(String deviceStatusMessage) {
		this.deviceStatusMessage = deviceStatusMessage;
	}

	public int getTransmitterHaPriority() {
		return transmitterHaPriority;
	}

	public void setTransmitterHaPriority(int transmitterHaPriority) {
		this.transmitterHaPriority = transmitterHaPriority;
	}

	public Date getLastReaderTestTime() {
		return lastReaderTestTime;
	}

	public boolean isLastReaderTestSuccess() {
		return lastReaderTestSuccess;
	}

	public String getLastReaderTestUser() {
		return lastReaderTestUser;
	}

	public void setLastReaderTestTime(Date lastReaderTestTime) {
		this.lastReaderTestTime = lastReaderTestTime;
	}

	public void setLastReaderTestSuccess(boolean lastReaderTestSuccess) {
		this.lastReaderTestSuccess = lastReaderTestSuccess;
	}

	public void setLastReaderTestUser(String lastReaderTestUser) {
		this.lastReaderTestUser = lastReaderTestUser;
	}
}




