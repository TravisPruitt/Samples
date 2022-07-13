package com.disney.xband.idms.lib.model;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestPut
{
	
	private UUID IDMSId;
	private int IDMSTypeId;
	private String lastName;
	private String firstName;
	private String middleName;
	private String title;
	private String suffix;
	private Date DOB;
	private int visitCount;
	private String avatarName;
	private boolean active;
	private String emailAddress;
	private String parentEmail;
	private String countryCode;
	private String languageCode;
	private String gender;
	private String userName;
	
	@XmlElement(name="IDMSId")
	public UUID getIDMSId() {
		return IDMSId;
	}
	
	@XmlElement(name="IDMSId")
	public void setIDMSId(UUID iDMSId) {
		IDMSId = iDMSId;
	}
	
	@XmlElement(name="IDMSId")
	public int getIDMSTypeId() {
		return IDMSTypeId;
	}
	
	@XmlElement(name="IDMSId")
	public void setIDMSTypeId(int iDMSTypeId) {
		IDMSTypeId = iDMSTypeId;
	}
	
	@XmlElement(name="lastName")
	public String getLastName() {
		return lastName;
	}
	
	@XmlElement(name="lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@XmlElement(name="firstName")
	public String getFirstName() {
		return firstName;
	}
	
	@XmlElement(name="firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@XmlElement(name="middleName")
	public String getMiddleName() {
		return middleName;
	}
	
	@XmlElement(name="middleName")
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@XmlElement(name="title")
	public String getTitle() {
		return title;
	}
	
	@XmlElement(name="title")
	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement(name="suffix")
	public String getSuffix() {
		return suffix;
	}
	
	@XmlElement(name="suffix")
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	@XmlElement(name="dateOfBirth")
	public Date getDOB() {
		return DOB;
	}
	
	@XmlElement(name="dateOfBirth")
	public void setDOB(Date dOB) {
		DOB = dOB;
	}
	
	@XmlElement(name="visitCount")
	public int getVisitCount() {
		return visitCount;
	}
	
	@XmlElement(name="visitCount")
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	
	@XmlElement(name="avatarName")
	public String getAvatarName() {
		return avatarName;
	}
	
	@XmlElement(name="avatarName")
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	
	@XmlElement(name="active")
	public boolean isActive() {
		return active;
	}
	
	@XmlElement(name="active")
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@XmlElement(name="emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	@XmlElement(name="emailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@XmlElement(name="parentEmail")
	public String getParentEmail() {
		return parentEmail;
	}
	
	@XmlElement(name="parentEmail")
	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}
	
	@XmlElement(name="countryCode")
	public String getCountryCode() {
		return countryCode;
	}
	
	@XmlElement(name="countryCode")
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	@XmlElement(name="languageCode")
	public String getLanguageCode() {
		return languageCode;
	}
	
	@XmlElement(name="languageCode")
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	@XmlElement(name="gender")
	public String getGender() {
		return gender;
	}
	
	@XmlElement(name="gender")
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@XmlElement(name="userName")
	public String getUserName() {
		return userName;
	}
	
	@XmlElement(name="userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
