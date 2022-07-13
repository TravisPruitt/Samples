package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomizationSelection 
{
	private String xbandOwnerId;
	private String self;
	private String xband;
	private String birthDate;
	private String firstName;
	private long guestId;
	private String guestIdType;
	private Boolean primaryGuest;
	private String lastName;
	private List<BandAccessory> bandAccessories;
	private String xbandRequestId;
	private String bandProductCode;
	private String printedName;
	private String customizationSelectionId;
	private String createDate;
	private String updateDate;
	private List<String> entitlements;
	private List<QualifyingId> qualifyingIds;
	private Boolean confirmedCustomization;
	
	@XmlElement(name="xbandOwnerId")
	public String getXbandOwnerId()
	{
		return this.xbandOwnerId;
	}

	public void setXbandOwnerId(String xbandOwnerId)
	{
		this.xbandOwnerId = xbandOwnerId;
	}
	
	@XmlElement(name="self")
	public String getSelf()
	{
		return this.self;
	}

	public void setSelf(String self)
	{
		this.self = self;
	}

	@XmlElement(name="xband")
	public String getXband()
	{
		return this.xband;
	}

	public void setXband(String xband)
	{
		this.xband = xband;
	}
	
	@XmlElement(name="birthDate")
	public String getBirthDate()
	{
		return this.birthDate;
	}

	public void setBirthDate(String birthDate)
	{
		this.birthDate = birthDate;
	}
	
	@XmlElement(name="firstName")
	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	@XmlElement(name="guestId")
	public long getGuestId()
	{
		return this.guestId;
	}

	public void setGuestId(long guestId)
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

	@XmlElement(name="primaryGuest")
	public Boolean getPrimaryGuest()
	{
		return this.primaryGuest;
	}

	public void setPrimaryGuest(Boolean primaryGuest)
	{
		this.primaryGuest = primaryGuest;
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
	
	@XmlElement(name="bandAccessories")
	public List<BandAccessory> getBandAccessories()
	{
		return this.bandAccessories;
	}
	
	public void setBandAccessories(List<BandAccessory> bandAccessories)
	{
		this.bandAccessories = bandAccessories;
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

	@XmlElement(name="bandProductCode")
	public String getBandProductCode()
	{
		return this.bandProductCode;
	}

	public void setBandProductCode(String bandProductCode)
	{
		this.bandProductCode = bandProductCode;
	}

	@XmlElement(name="printedName")
	public String getPrintedName()
	{
		return this.printedName;
	}

	public void setPrintedName(String printedName)
	{
		this.printedName = printedName;
	}
	
	@XmlElement(name="customizationSelectionId")
	public String getCustomizationSelectionId()
	{
		return this.customizationSelectionId;
	}

	public void setCustomizationSelectionId(String customizationSelectionId)
	{
		this.customizationSelectionId = customizationSelectionId;
	}
	
	@XmlElement(name="createDate")
	public String getCreateDate()
	{
		return this.createDate;
	}

	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}

	@XmlElement(name="updateDate")
	public String getUpdateDate()
	{
		return this.updateDate;
	}

	public void setUpdateDate(String updateDate)
	{
		this.updateDate = updateDate;
	}

	@XmlElement(name="entitlements")
	public List<String> getEntitlements()
	{
		return this.entitlements;
	}

	public void setEntitlements(List<String> entitlements)
	{
		this.entitlements = entitlements;
	}

	@XmlElement(name="qualifyingIds")
	public List<QualifyingId> getQualifyingIds()
	{
		return this.qualifyingIds;
	}

	public void setQualifyingIds(List<QualifyingId> qualifyingIds)
	{
		this.qualifyingIds = qualifyingIds;
	}

	@XmlElement(name="confirmedCustomization")
	public Boolean getConfirmedCustomization()
	{
		return this.confirmedCustomization;
	}

	public void setConfirmedCustomization(Boolean confirmedCustomization)
	{
		this.confirmedCustomization = confirmedCustomization;
	}

}
