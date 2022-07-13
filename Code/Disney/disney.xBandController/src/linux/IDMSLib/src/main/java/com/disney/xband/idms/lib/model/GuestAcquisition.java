package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;

public class GuestAcquisition
{
	private String firstName;
	private String lastName;
	private String xbandRequestId;
	private String acquisitionId;
	private String acquisitionIdType;
	private String acquisitionStartDate;
	private String acquisitionUpdateDate;
	private Boolean primaryGuest;
	private String guestId;
	private String guestIdType;

	@XmlElement(name="firstName")
	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	@XmlElement(name="lastName")
	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	@XmlElement(name="xbandRequestId")
	public String getXbandRequestId()
	{
		return this.xbandRequestId;
	}

	public void setXbandRequestId(String xbandRequestId)
	{
		this.xbandRequestId = xbandRequestId;
	}

	@XmlElement(name="acquisitionId")
	public String getAcquisitionId()
	{
		return this.acquisitionId;
	}

	public void setAcquisitionId(String acquisitionId)
	{
		this.acquisitionId = acquisitionId;
	}
	
	@XmlElement(name="acquisitionIdType")
	public String getAcquisitionIdType()
	{
		return this.acquisitionIdType;
	}

	public void setAcquisitionIdType(String acquisitionIdType)
	{
		this.acquisitionIdType = acquisitionIdType;
	}
	
	@XmlElement(name="acquisitionStartDate")
	public String getAcquisitionStartDate()
	{
		return this.acquisitionStartDate;
	}

	public void setAcquisitionStartDate(String acquisitionStartDate)
	{
		this.acquisitionStartDate = acquisitionStartDate;
	}
	
	@XmlElement(name="acquisitionUpdateDate")
	public String getAcquisitionUpdateDate()
	{
		return this.acquisitionUpdateDate;
	}

	public void setAcquisitionUpdateDate(String acquisitionUpdateDate)
	{
		this.acquisitionUpdateDate = acquisitionUpdateDate;
	}

	@XmlElement(name="primaryGuest")
	public Boolean getPrimaryGuest()
	{
		return this.primaryGuest;
	}

	public void setPrimaryGuest(Boolean primaryGuest)
	{
		this.primaryGuest = primaryGuest;
	}

	@XmlElement(name="guestId")
	public String getGuestId()
	{
		return this.guestId;
	}

	public void setGuestId(String guestId)
	{
		this.guestId = guestId;
	}

	@XmlElement(name="guestIdType")
	public String getGuestIdType()
	{
		return this.guestIdType;
	}

	public void setGuestIdType(String guestIdType)
	{
		this.guestIdType = guestIdType;
	}
}
