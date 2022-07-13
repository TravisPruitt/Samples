package com.disney.xband.idms.web;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.idms.lib.model.GuestAddress;
import com.disney.xband.idms.lib.model.GuestExperience;
import com.disney.xband.idms.lib.model.GuestPhone;
import com.disney.xband.idms.lib.model.GuestTraveler;
import com.disney.xband.idms.lib.model.LinkCollection;
import com.disney.xband.idms.lib.model.XBand;

@XmlRootElement
public class FastPassTestGuestProfile
{

	private long guestId;
	private String dateOfBirth;
	private List<GuestAddress> addressList;
	private String status;
	private List<GuestPhone> phoneList;
	private String countryCode;
	private GuestTraveler traveler;
	private String swid;
	private String languageCode;
	private String emailAddress;
	private String parentEmail;
	private String middleName;
	private String lastName;
	private String firstName;
	private String title;
	private String suffix;
	private String gender;
	private String userName;
	private LinkCollection links;
	private String avatar;
	private int visitCount;
	private String guestType;
	private List<XBand> xbands;
	private List<FastPassTestGuestIdentifier> identifiers;
	private List<GuestExperience> experiences;
	private long partyId;

	public FastPassTestGuestProfile()
	{
	}
	
	@XmlElement(name="partyId")
	public long getPartyId()
	{
		return partyId;
	}
	
	public void setPartyId(long partyId)
	{
		this.partyId = partyId;
	}

	@XmlElement(name="expriences")
	public List<GuestExperience> getExperiences()
	{
		return this.experiences;
	}
	
	public void setExperiences(List<GuestExperience> experiences)
	{
		this.experiences = experiences;
	}
	
	@XmlElement(name="guestType")
	public String getGuestType()
	{
		return this.guestType;
	}
	
	public void setGuestType(String guestType)
	{
		this.guestType = guestType;
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

	@XmlElement(name="dateOfBirth")
	public String getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(String dateOfBirth) 
	{
		this.dateOfBirth = dateOfBirth;
	}


	@XmlElement(name="addressList")
	public List<GuestAddress> getAddressList() {
		return addressList;
	}


	public void setAddressList(List<GuestAddress> addressList) {
		this.addressList = addressList;
	}


	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	@XmlElement(name="phoneList")
	public List<GuestPhone> getPhoneList() {
		return phoneList;
	}


	@XmlElement(name="phoneList")
	public void setPhoneList(List<GuestPhone> phoneList) {
		this.phoneList = phoneList;
	}


	@XmlElement(name="countryCode")
	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	@XmlElement(name="traveler")
	public GuestTraveler getTraveler() {
		return traveler;
	}


	public void setTraveler(GuestTraveler traveler) {
		this.traveler = traveler;
	}


	@XmlElement(name="swid")
	public String getSwid() {
		return swid;
	}


	public void setSwid(String swid) {
		this.swid = swid;
	}


	@XmlElement(name="languageCode")
	public String getLanguageCode() {
		return languageCode;
	}


	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}


	@XmlElement(name="emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	@XmlElement(name="parentEmail")
	public String getParentEmail() {
		return parentEmail;
	}


	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}


	@XmlElement(name="middleName")
	public String getMiddleName() {
		return middleName;
	}
	
	@XmlElement(name="middleName")
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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


	@XmlElement(name="gender")
	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	@XmlElement(name="userName")
	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}


	public void setLinks(LinkCollection links) {
		this.links = links;
	}


	@XmlElement(name="avatar")
	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	@XmlElement(name="visitCount")
	public int getVisitCount() {
		return visitCount;
	}


	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	
	@XmlElement(name="xbands")
	public List<XBand> getXBands()
	{
		return this.xbands;
	}
	
	public void setXBands(List<XBand> xbands)
	{
		this.xbands = xbands;
	}
	
	@XmlElement(name="identifiers")
	public List<FastPassTestGuestIdentifier> getIdentifiers()
	{
		return this.identifiers;
	}
	
	public void setIdentifiers(List<FastPassTestGuestIdentifier> identifiers)
	{
		this.identifiers = identifiers;
	}
}
