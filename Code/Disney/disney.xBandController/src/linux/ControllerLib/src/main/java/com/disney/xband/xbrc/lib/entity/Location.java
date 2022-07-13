package com.disney.xband.xbrc.lib.entity;

import java.util.List;
import java.util.regex.Pattern;

import com.disney.xband.lib.xbrapi.XbrBandCommand;

public class Location {
	
	private static final transient Pattern illegalChars = Pattern
			.compile("\r\n|\r|\n|\u0085|\u2028|u2029|\\||<|>|u003E|u003C|\"|u0022|'|u0027|`|u0060|\\*|u0028|=|u003D");
	
	private Long id;
	private Integer locationTypeId;
	@Column(length=32)
	private String name;
	private String locationId;
	private Double x;
	private Double y;
	private Integer singulationTypeId;
	private Integer eventGenerationTypeId;
	private List<Reader> readers;
	private Boolean useSecureId = null;
    private String successSequence;
    private Long successTimeout = 0l;
    private String failureSequence;
    private Long failureTimeout = 0l;
    private String errorSequence;
    private Long errorTimeout = 0l;  
    private String tapSequence;
    private Long tapTimeout = 0l;
    private String idleSequence;
    private String transmitZoneGroup;
    private List<XbrBandCommand> transmitCommands;
    private Boolean sendBandStatus = false;
    
    public void sanitizeName()
	{
		if (this.name == null || this.name.trim().isEmpty())
			return;
		
		this.name = illegalChars.matcher(this.name).replaceAll("");
	}
    public static String sanitizeName(String suspect)
	{
		return illegalChars.matcher(suspect != null ? suspect : "").replaceAll("");
	}
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getLocationTypeId() {
		return locationTypeId;
	}
	public void setLocationTypeId(Integer locationTypeId) {
		this.locationTypeId = locationTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Integer getSingulationTypeId() {
		return singulationTypeId;
	}
	public void setSingulationTypeId(Integer singulationTypeId) {
		this.singulationTypeId = singulationTypeId;
	}
	public Integer getEventGenerationTypeId() {
		return eventGenerationTypeId;
	}
	public void setEventGenerationTypeId(Integer eventGenerationTypeId) {
		this.eventGenerationTypeId = eventGenerationTypeId;
	}
	public List<Reader> getReaders() {
		return readers;
	}
	public void setReaders(List<Reader> readers) {
		this.readers = readers;
	}
	public Boolean isUseSecureId()
	{
		return useSecureId;
	}
	public void setUseSecureId(Boolean useSecureId)
	{
		this.useSecureId = useSecureId;
	}

    public String getSuccessSequence()
    {
        return successSequence;
    }

    public void setSuccessSequence(String successSequence)
    {
        this.successSequence = stripDefaultDecoration(successSequence);
    }

    public String getFailureSequence()
    {
        return failureSequence;
    }

    public void setFailureSequence(String failureSequence)
    {
        this.failureSequence = stripDefaultDecoration(failureSequence);
    }

    public String getErrorSequence()
    {
        return errorSequence;
    }

    public void setErrorSequence(String errorSequence)
    {
        this.errorSequence = stripDefaultDecoration(errorSequence);
    }

    public String getIdleSequence()
    {
        return idleSequence;
    }

    public void setIdleSequence(String idleSequence)
    {
        this.idleSequence = stripDefaultDecoration(idleSequence);
    }
	public Long getSuccessTimeout()
	{
		return successTimeout;
	}
	public void setSuccessTimeout(Long successTimeout)
	{
		this.successTimeout = successTimeout;
	}
	public Long getFailureTimeout()
	{
		return failureTimeout;
	}
	public void setFailureTimeout(Long failureTimeout)
	{
		this.failureTimeout = failureTimeout;
	}
	public Long getErrorTimeout()
	{
		return errorTimeout;
	}
	public void setErrorTimeout(Long errorTimeout)
	{
		this.errorTimeout = errorTimeout;
	}

    private String stripDefaultDecoration(String sequenceName) {
        // Strip the asterisk if necessary.
        if ( sequenceName != null && sequenceName.contains("*") ) {
            sequenceName = sequenceName.replace("*", "");
        }

        return sequenceName;
    }
	public String getTapSequence()
	{
		return tapSequence;
	}
	public void setTapSequence(String tapSequence)
	{
		this.tapSequence = stripDefaultDecoration(tapSequence);
	}
	public Long getTapTimeout()
	{
		return tapTimeout;
	}
	public void setTapTimeout(Long tapTimeout)
	{
		this.tapTimeout = tapTimeout;
	}
	public String getTransmitZoneGroup() {
		return transmitZoneGroup;
	}
	public void setTransmitZoneGroup(String transmitZoneGroup) {
		this.transmitZoneGroup = transmitZoneGroup;
	}
	public List<XbrBandCommand> getTransmitCommands()
	{
		return transmitCommands;
	}

	public void setTransmitCommands(List<XbrBandCommand> transmitCommands)
	{
		this.transmitCommands = transmitCommands;
	}
	public Boolean getSendBandStatus() {
		return sendBandStatus;
	}
	public void setSendBandStatus(Boolean sendBandStatus) {
		this.sendBandStatus = sendBandStatus;
	}
}
