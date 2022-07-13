package com.disney.xband.xbrc.lib.entity;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Reader {
	
	private static final transient Pattern illegalChars = 
			Pattern.compile("\\s|\r\n|\\||<|>|u003E|u003C|\"|u0022|'|u0027|`|u0060|\\*|u0028|=|u003D");
	
	private Long id;
	@Column(length=32)
	private String readerId;
	private ReaderType type;
	@Column(length=16)
	private String group;
	@Column(length=16)
	private String singulationGroup;
	private Integer signalStrengthThreshold;
	private Double gain;
	@Column(length=32)
	private String macAddress;
	@Column(length=32)
	private String ipAddress;
	private Integer port;
	@Column(length=20)
	private Long lastIdReceived;
	private Integer x;
	private Integer y;
	private Long locationId;
	private Location location;
	@Column(length=20)
	private Date timeLastHello;
	private Integer lane;
	private Integer deviceId;
	private String version;
	private String minXbrcVersion;
	private boolean transmitter;
	private Integer transmitterHaPriority = 1;
	private String transmitPayload;
	private String modelData;
	private String disabledReason;
	private boolean enabled = true;
	private Integer bioDeviceType;	

    private Integer batteryLevel = null;
	private Integer batteryTime = null;
	private Double temperature = null;
	
	private Date lastReaderTestTime;
	private boolean lastReaderTestSuccess;
	@Column(length=128)
	private String lastReaderTestUser;

	private List<Boolean> antennas = null;
	
	private String hardwareType = null;

	public Reader()
	{
	}
	
	public void sanitizeReaderId()
	{
		if (this.readerId == null || this.readerId.trim().isEmpty())
			return;
		
		this.readerId = illegalChars.matcher(this.readerId).replaceAll("");
	}
	public static String sanitizeReaderId(String suspect)
	{
		return illegalChars.matcher(suspect != null ? suspect : "").replaceAll("");
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getSingulationGroup() {
		return singulationGroup;
	}
	public void setSingulationGroup(String singulationGroup) {
		this.singulationGroup = singulationGroup;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getSignalStrengthThreshold() {
		return signalStrengthThreshold;
	}
	public void setSignalStrengthThreshold(Integer signalStrengthThreshold) {
		this.signalStrengthThreshold = signalStrengthThreshold;
	}
	public Double getGain() {
		return gain;
	}
	public void setGain(Double gain) {
		this.gain = gain;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public Long getLastIdReceived() {
		return lastIdReceived;
	}
	public void setLastIdReceived(Long lastIdReceived) {
		this.lastIdReceived = lastIdReceived;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public Date getTimeLastHello() {
		return timeLastHello;
	}
	public void setTimeLastHello(Date timeLastHello) {
		this.timeLastHello = timeLastHello;
	}
	public Integer getLane() {
		return lane;
	}
	public void setLane(Integer lane) {
		this.lane = lane;
	}
	public ReaderType getType() {
		return type;
	}
	public void setType(ReaderType type) {
		this.type = type;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}
	/**
	 * @return the minXbrcVersion
	 */
	public String getMinXbrcVersion()
	{
		return minXbrcVersion;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
	/**
	 * @param minXbrcVersion the minXbrcVersion to set
	 */
	public void setMinXbrcVersion(String minXbrcVersion)
	{
		this.minXbrcVersion = minXbrcVersion;
	}
	public boolean isTransmitter()
	{
		return transmitter;
	}
	public void setTransmitter(boolean transmitter)
	{
		this.transmitter = transmitter;
	}

	public String getTransmitPayload()
	{
		return transmitPayload;
	}
	public void setTransmitPayload(String transmitPayload)
	{
		this.transmitPayload = transmitPayload;
	}
	
	public boolean getTransmitter()
	{
		return transmitter;
	}
	public String getModelData()
	{
		return modelData;
	}
	public void setModelData(String modelData)
	{
		this.modelData = modelData;
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
	public Integer getBioDeviceType()
	{
		return bioDeviceType;
	}
	public void setBioDeviceType(Integer bioDeviceType)
	{
		this.bioDeviceType = bioDeviceType;
	}

	public List<Boolean> getAntennas() {
		return antennas;
	}
	
	public void setAntennas(List<Boolean> antennas) throws Exception {
		if (antennas != null && antennas.size() != 8)
			throw new Exception("Antenna list must be of size 8 or null");

		this.antennas = antennas;
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
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public String getHardwareType() {
		return hardwareType;
	}

	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}

	public Integer getTransmitterHaPriority() {
		return transmitterHaPriority;
	}

	public void setTransmitterHaPriority(Integer transmitterHaPriority) {
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
