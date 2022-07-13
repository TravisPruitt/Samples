package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;

/*
"method": "PRIMARY_GUEST_ADDRESS_BEST",
"carrier" : "UPS",
"carrierLink" : "http://www.ups.com/WebTracking/track",
"shippingDate" : "2010-08-27T04:00:00Z",
"trackingNumber" : 66,
"address" : {
 */

public class Shipment
{
	private String method;
	private String carrier;
	private String carrierLink;
	private String shippingDate;
	private String trackingNumber;
	private Address address;

	@XmlElement(name="method")
	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}
	
	@XmlElement(name="carrier")
	public String getCarrier()
	{
		return this.carrier;
	}

	public void setCarrier(String carrier)
	{
		this.carrier = carrier;
	}
	
	@XmlElement(name="carrierLink")
	public String getCarrierLink()
	{
		return this.carrierLink;
	}

	public void setCarrierLink(String carrierLink)
	{
		this.carrierLink = carrierLink;
	}
	
	@XmlElement(name="shippingDate")
	public String getShippingDate()
	{
		return this.shippingDate;
	}

	public void setShippingDate(String shippingDate)
	{
		this.shippingDate = shippingDate;
	}
	@XmlElement(name="trackingNumber")
	public String getTrackingNumber()
	{
		return this.trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber)
	{
		this.trackingNumber = trackingNumber;
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
