package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestPhone
{
	
	private String extension;
	private String countryCode;
	private String number;
	private String IDMSTypeName;
	private long guestId;
	private long guest_phoneId;
	
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
	
	@XmlElement(name="guestPhoneId")
	public long getGuestPhoneId()
	{
		return this.guest_phoneId;
	}
	
	@XmlElement(name="guestPhoneId")
	public void setGuestPhoneId(long guest_phoneId)
	{
		this.guest_phoneId = guest_phoneId;
	}
	
	
	@XmlElement(name="extension")
	public String getExtension() {
		return extension;
	}
	
	@XmlElement(name="extension")
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@XmlElement(name="countryCode")
	public String getCountryCode() {
		return countryCode;
	}
	
	@XmlElement(name="countryCode")
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	@XmlElement(name="number")
	public String getNumber() {
		return number;
	}
	
	@XmlElement(name="number")
	public void setNumber(String number) {
		this.number = number;
	}
	
	@XmlElement(name="type")
	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	@XmlElement(name="type")
	public void setIDMSTypeName(String iDMSTypeName) {
		IDMSTypeName = iDMSTypeName;
	}
	
	

}
