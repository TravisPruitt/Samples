package com.disney.xband.xbrc.lib.model;

import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.lib.xbrapi.XbrBandCommand;

@XmlType(propOrder={"locationId","name","id","locationTypeID","locationTypeName","x","y","readers", "useSecureId", "successSequence", "successTimeout", "failureSequence", "failureTimeout", "errorSequence", "errorTimeout", "tapSequence", "tapTimeout", "idleSequence", "transmitZoneGroup", "transmitCommands", "sendBandStatus"})
public class LocationInfo
{
	private int id;
	private String sName=null;
	private String locationId=null;
	private int nLocationTypeID;
	private String locationTypeName;
	private double x, y;
	private int nSingulationTypeID;
	private int nEventGenerationTypeID;
	private List<ReaderInfo> readers;
	private List<XbrBandCommand> transmitCommands;
	private Boolean sendBandStatus = false;
	
	// null means that the global configuration setting should be used
	private Boolean useSecureId = null;
    private String sSuccessSequence = null;
    private Long successTimeout = 0l;
    private String sFailureSequence = null;
    private Long failureTimeout = 0l;
    private String sErrorSequence = null;
    private Long errorTimeout = 0l;
    private String tapSequence = null;
    private Long tapTimeout = 0l;
    private String sIdleSequence = null;
    private String transmitZoneGroup = null;

	public LocationInfo()
	{
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return sName;
	}

	public void setName(String sName)
	{
		this.sName = sName;
	}
	
	/**
	 * Used by the UI. Don't change without making sure that all the JSPs using it still work.
	 */
	public String getShortName()
	{
		if (sName == null)
			return sName;
		
		final String shortName = sName.trim();
		
		final int maxLength = 16;
		if (shortName.length() > maxLength)
			return shortName.substring(0, maxLength - 3) + "...";
		
		return shortName;
	}
	
	public String getLocationId()
	{
		return locationId;
	}
	
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@XmlElement(name="type")
	public int getLocationTypeID()
	{
		return nLocationTypeID;
	}

	public void setLocationTypeID(int nLocationTypeID)
	{
		this.nLocationTypeID = nLocationTypeID;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
	@XmlTransient
	public int getSingulationTypeID()
	{
		return nSingulationTypeID;
	}

	public void setSingulationTypeID(int nSingulationTypeID)
	{
		this.nSingulationTypeID = nSingulationTypeID;
	}
	@XmlTransient
	public int getEventGenerationTypeID()
	{
		return nEventGenerationTypeID;
	}

	public void setEventGenerationTypeID(int nEventGenerationTypeID)
	{
		this.nEventGenerationTypeID = nEventGenerationTypeID;
	}
	@XmlElementWrapper
	@XmlElement(name="reader")
	public List<ReaderInfo> getReaders() {
		return readers;
	}

	public void setReaders(List<ReaderInfo> readers) {
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
        return sSuccessSequence;
    }

    public void setSuccessSequence(String sSuccessSequence)
    {
        this.sSuccessSequence = sSuccessSequence;
    }

    public String getFailureSequence()
    {
        return sFailureSequence;
    }

    public void setFailureSequence(String sFailureSequence)
    {
        this.sFailureSequence = sFailureSequence;
    }

    public String getErrorSequence()
    {
        return sErrorSequence;
    }

    public void setErrorSequence(String sErrorSequence)
    {
        this.sErrorSequence = sErrorSequence;
    }

    public String getIdleSequence()
    {
        return sIdleSequence;
    }

    public void setIdleSequence(String sIdleSequence)
    {
        this.sIdleSequence = sIdleSequence;
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
	
	/**
	 * Use to sort a collection of <code>LocationInfo</code> objects by 
	 * location's name in lexicographical order.
	 * 
	 * @author Iwona Glabek
	 */
	public class ByNameComparator implements Comparator<LocationInfo> {
		public int compare(LocationInfo li1, LocationInfo li2){
			
			String locInfo1Name = li1.getName();
			String locInfo2Name = li2.getName();
			
			if (locInfo1Name == null || locInfo2Name == null)
			{
				return -1;
			}
			
			if (locInfo1Name.compareTo(locInfo2Name) > 0)
				return 1;
			if (locInfo1Name.compareTo(locInfo2Name) < 0)
				return -1;
			
			return 0;
		}
	}
	
	/**
	 * Use to sort a collection of <code>LocationInfo</code> objects by 
	 * the worst reader health status found and location name.
	 * 
	 * @author Iwona Glabek
	 */
	public class ByReaderHealth implements Comparator<LocationInfo> {
		public int compare(LocationInfo li1, LocationInfo li2){
			
			List<ReaderInfo> li1Readers = li1.getReaders();
			List<ReaderInfo> li2Readers = li2.getReaders();
			
			StatusType li1WorstHealthStatus = StatusType.Green;
			StatusType li2WorstHealthStatus = StatusType.Green;
			
			if (li1Readers != null)
			{
				for (ReaderInfo ri : li1Readers)
				{
					if (ri.getStatus() == null)
						continue;
					
					if (StatusType.compare(ri.getStatus(), li1WorstHealthStatus) < 0)
					{
						li1WorstHealthStatus = ri.getStatus();
						if (li1WorstHealthStatus == StatusType.Red)
							break;	// looking for worst case, found it
					}
				}
			}
			
			if (li2Readers != null)
			{
				for (ReaderInfo ri : li2Readers)
				{
					if (ri.getStatus() == null)
						continue;
					
					if (StatusType.compare(ri.getStatus(), li2WorstHealthStatus) < 0)
					{
						li2WorstHealthStatus = ri.getStatus();
						if (li2WorstHealthStatus == StatusType.Red)
							break;	// looking for worst case, found it
					}
				}
			}
			
			int result = StatusType.compare(li1WorstHealthStatus, li2WorstHealthStatus);
			
			if (result != 0)
				return result;
			
			// sort by location name next
			String locInfo1Name = li1.getName();
			String locInfo2Name = li2.getName();
			
			if (locInfo1Name == null || locInfo2Name == null)
			{
				return result;
			}
			
			return locInfo1Name.compareTo(locInfo2Name);
		}
	}

	@XmlElement(name="typename")
	public String getLocationTypeName()
	{
		return locationTypeName;
	}

	public void setLocationTypeName(String locationTypeName)
	{
		this.locationTypeName = locationTypeName;
	}

	public String getTapSequence()
	{
		return tapSequence;
	}

	public void setTapSequence(String tapSequence)
	{
		this.tapSequence = tapSequence;
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
	
	@XmlElementWrapper(name="transmitCommands")
	@XmlElement(name="transmitCommand")
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