package com.disney.xband.idms.lib.model.xview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

import com.disney.xband.idms.lib.model.GuestIdentifier;

@XmlRootElement
public class Guest 
{

	private String guestId;

	private boolean active;

	private String address1;

	private String address2;

	private String birthdate;

	private String city;

	private String countryCode;

	private String createdBy;

	private Date createdDate;

	private String firstName;

	private String lastName;

	private String state;

	private String updatedBy;

	private Date updatedDate;

	private String xBMSId;

	private String zip;


	private List<Entitlement> entitlements;

    private List<Xband> xbands;

	private List<GuestInfo> guestInfos;
	
	private List<GuestIdentifier> identifiers;

	public String getGuestId() 
	{
		return this.guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

    @JsonProperty("XBMSId")
	@XmlElement(name="XBMSId")
	public String getXBMSId() 
    {
		return this.xBMSId;
	}
	
	public void setXBMSId(String xBMSId) 
	{
		this.xBMSId = xBMSId;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public List<Entitlement> getEntitlements() {
		return this.entitlements;
	}

	public void setEntitlements(ArrayList<Entitlement> entitlements) {
		this.entitlements = entitlements;
	}
	

	public List<Xband> getXbands() {
		return this.xbands;
	}


	public void setXbands(List<Xband> xbands) {
		this.xbands = xbands;
	}


	public List<GuestInfo> getGuestInfos() {
		return this.guestInfos;
	}


	public void setGuestInfos(List<GuestInfo> guestInfos) {
		this.guestInfos = guestInfos;
	}

	public List<GuestIdentifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<GuestIdentifier> identifiers) {
		this.identifiers = identifiers;
	}
	
}