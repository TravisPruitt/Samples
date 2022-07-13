package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Guest
{
	
	private long guestId;
	private String dateOfBirth;
	private String status;
	private String countryCode;
	private String swid;
	private String languageCode;
	private String emailAddress;
	private String parentEmail;
	private GuestName name;
	private String gender;
	private String userName;
	private String avatar;
	private int visitCount;
	private String IDMSTypeName;
	private List<Link> links;
	private long partyId;
	
	
	public Guest()
	{
		
	}
	
	@XmlElement(name="partyId")
	public long getPartyId()
	{
		return this.partyId;
	}
	
	@XmlElement(name="partyId")
	public void setPartyId(long partyId)
	{
		this.partyId = partyId;
	}
	
	
	@XmlElement(name="guestId")
	public long getGuestId() {
		return guestId;
	}
	
	@XmlElement(name="guestId")
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}
	
	@XmlElement(name="dateOfBirth")
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	@XmlElement(name="dateOfBirth")
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}
	
	@XmlElement(name="status")
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElement(name="countryCode")
	public String getCountryCode() {
		return countryCode;
	}
	
	@XmlElement(name="countryCode")
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	@XmlElement(name="swid")
	public String getSwid() {
		return swid;
	}
	
	@XmlElement(name="swid")
	public void setSwid(String swid) {
		this.swid = swid;
	}
	
	@XmlElement(name="languageCode")
	public String getLanguageCode() {
		return languageCode;
	}
	
	@XmlElement(name="languageCode")
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
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
	
	@XmlElement(name="name")
	public GuestName getName() {
		return name;
	}
	
	@XmlElement(name="name")
	public void setName(GuestName name) {
		this.name = name;
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
	
	@XmlElement(name="avatar")
	public String getAvatar() {
		return avatar;
	}
	
	@XmlElement(name="avatar")
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@XmlElement(name="visitCount")
	public int getVisitCount() {
		return visitCount;
	}
	
	@XmlElement(name="visitCount")
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	
	@XmlElement(name="type")
	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	@XmlElement(name="type")
	public void setIDMSTypeName(String iDMSTypeName) {
		IDMSTypeName = iDMSTypeName;
	}
	
	
	@XmlElement(name="links")
	public List<Link> getLinks()
	{
		return this.links;
	}
	
	@XmlElement(name="links")
	public void setLinks(List<Link> links)
	{
		this.links = links;
	}
	
}
