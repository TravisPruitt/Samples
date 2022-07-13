package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestAddress 
{

	private long guestId;
	private long guest_addressId;
	private String postalCode;
	private String state;
	private String IDMSTypeName;
	private String address1;
	private String address2;
	private String address3;
	private String country;
	private String city;
	
	@XmlElement(name="addressId")
	public long getGuestAddressId()
	{
		return guest_addressId;
	}
	
	@XmlElement(name="addressId")
	public void setGuestAddressId(long guest_addressId)
	{
		this.guest_addressId = guest_addressId;
	}
	
	@XmlElement(name="guestId")
	public long getGuestId()
	{
		return this.guestId;
	}
	
	@XmlElement(name="guestId")
	public void setGuestId(long guestId)
	{
		this.guestId = guestId;
	}
	
	
	@XmlElement(name="postalCode")
	public String getPostalCode() {
		return postalCode;
	}
	
	@XmlElement(name="postalCode")
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	@XmlElement(name="state")
	public String getState() {
		return state;
	}
	
	@XmlElement(name="state")
	public void setState(String state) {
		this.state = state;
	}
	
	@XmlElement(name="type")
	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	@XmlElement(name="type")
	public void setIDMSTypeName(String iDMSTypeName) {
		IDMSTypeName = iDMSTypeName;
	}
	
	@XmlElement(name="address1")
	public String getAddress1() {
		return address1;
	}
	
	@XmlElement(name="address1")
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	@XmlElement(name="address2")
	public String getAddress2() {
		return address2;
	}
	
	@XmlElement(name="address2")
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	@XmlElement(name="address3")
	public String getAddress3() {
		return address3;
	}
	
	@XmlElement(name="address3")
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	
	@XmlElement(name="country")
	public String getCountry() {
		return country;
	}
	
	@XmlElement(name="country")
	public void setCountry(String country) {
		this.country = country;
	}
	
	@XmlElement(name="city")
	public String getCity() {
		return city;
	}
	
	@XmlElement(name="city")
	public void setCity(String city) {
		this.city = city;
	}
	
	
	
	
}
