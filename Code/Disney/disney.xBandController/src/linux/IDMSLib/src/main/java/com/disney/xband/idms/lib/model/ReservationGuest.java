package com.disney.xband.idms.lib.model;


public class ReservationGuest
{
	
	private String guestType;
	private String lastName;
	private String firstName;
	private String guestIdType;
	private String guestIdValue;
	private String xbandRequestId;
	private String acquisitionType;
	private String acquisitionId;
	private String acquisitionStartDate;
	private String acquisitionUpdateDate;
	private String xbandOwnerId;
	private boolean isPrimaryGuest;
	
	public String getGuestType() 
	{
		return this.guestType;
	}
	
	public void setGuestType(String guestType) 
	{
		this.guestType = guestType;
	}
	
	public String getLastName() 
	{
		return this.lastName;
	}
	
	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
	}
	
	public String getFirstName() 
	{
		return this.firstName;
	}
	
	public void setFirstName(String firstName) 
	{
		this.firstName = firstName;
	}	

	public String getGuestIdType() 
	{
		return this.guestIdType;
	}
	
	public void setGuestIdType(String guestIdType) 
	{
		this.guestIdType = guestIdType;
	}
	
	public String getGuestIdValue() 
	{
		return this.guestIdValue;
	}
	
	public void setGuestIdValue(String guestIdValue) 
	{
		this.guestIdValue = guestIdValue;
	}
	
	public String getXbandRequestId() 
	{
		return this.xbandRequestId;
	}
	
	public void setXbandRequestId(String xbandRequestId) 
	{
		this.xbandRequestId = xbandRequestId;
	}
	
	public String getAcquisitionType() 
	{
		return this.acquisitionType;
	}
	
	public void setAcquisitionType(String acquisitionType) 
	{
		this.acquisitionType = acquisitionType;
	}
	
	public String getAcquisitionId() 
	{
		return this.acquisitionId;
	}
	
	public void setAcquisitionId(String acquisitionId) 
	{
		this.acquisitionId = acquisitionId;
	}
	
	public String getAcquisitionStartDate() 
	{
		return this.acquisitionStartDate;
	}
	
	public void setAcquisitionStartDate(String acquisitionStartDate) 
	{
		this.acquisitionStartDate = acquisitionStartDate;
	}
	
	public String getAcquisitionUpdateDate() 
	{
		return this.acquisitionUpdateDate;
	}
	
	public void setAcquisitionUpdateDate(String acquisitionUpdateDate) 
	{
		this.acquisitionUpdateDate = acquisitionUpdateDate;
	}
	
	public String getXbandOwnerId() 
	{
		return this.xbandOwnerId;
	}
	
	public void setXbandOwnerId(String xbandOwnerId) 
	{
		this.xbandOwnerId = xbandOwnerId;
	}
	
	public boolean getIsPrimaryGuest()
	{
		return this.isPrimaryGuest;
	}
	
	public void setIsPrimaryGuest(boolean isPrimaryGuest) 
	{
		this.isPrimaryGuest = isPrimaryGuest;
	}
}
