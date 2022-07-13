package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*"address" : {
    "address1" : "#4 Privet Drive",
    "city" : "Atlanta",
    "country" : "US",
    "postalCode" : "30328",
    "state" : "GA"*/

@XmlRootElement
public class Address 
{
	private String address1;
	private String address2;
	private String city;
	private String country;
	private String postalCode;
	private String state;

	@XmlElement(name="address1")
	public String getAddress1()
	{
		return this.address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	@XmlElement(name="address2")
	public String getAddress2()
	{
		return this.address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	@XmlElement(name="city")
	public String getCity()
	{
		return this.city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	@XmlElement(name="country")
	public String getCountry()
	{
		return this.country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}
	
	@XmlElement(name="postalCode")
	public String getPostalCode()
	{
		return this.postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}
	
	@XmlElement(name="state")
	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}
}
