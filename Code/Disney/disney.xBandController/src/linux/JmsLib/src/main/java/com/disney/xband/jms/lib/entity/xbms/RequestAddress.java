package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;

/*"confirmedAddress" : false,
"phoneNumber" : "867-5309",
/*"address" : {
    "address1" : "#4 Privet Drive",
    "city" : "Atlanta",
    "country" : "US",
    "postalCode" : "30328",
    "state" : "GA"*/

public class RequestAddress 
{
	private boolean confirmedAddress;
	private String phoneNumber;
	private Address address;
	
	@XmlElement(name="confirmedAddress")
	public boolean getConfirmedAddress()
	{
		return this.confirmedAddress;
	}

	public void setConfirmedAddress(boolean confirmedAddress)
	{
		this.confirmedAddress = confirmedAddress;
	}

	@XmlElement(name="phoneNumber")
	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
	@XmlElement(name="address")
	public Address getAddress()
	{
		return this.address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}
}
