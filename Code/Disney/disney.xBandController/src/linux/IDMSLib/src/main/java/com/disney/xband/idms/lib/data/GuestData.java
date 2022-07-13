package com.disney.xband.idms.lib.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.disney.xband.idms.lib.model.GuestIdentifier;
import com.disney.xband.idms.lib.model.GuestName;
import com.disney.xband.idms.lib.model.Link;


public final class GuestData  {
	private long guestId;
	private String dateOfBirth; // may be null
	private String status;
	private String countryCode;
	private String swid;
	private String languageCode;
	private String emailAddress;
	private String parentEmail;

	// Name
	private String middleName;
	private String lastName;
	private String firstName;
	private String title;
	private String suffix;
	
	private String gender;
	private String userName;
	private String avatar;
	private int visitCount;
	private String IDMSTypeName;
	private List<Link> links;
	private long partyId;
	
	
	private String createdBy;
	private Date createdDate;
	private String updatedBy;
	private Date updatedDate;
	private String xBMSId;	

	private boolean bandListComplete;
	private List<BandData> bandList;
	private boolean identifierListComplete;
	private List<GuestDataIdentifier> identifierList;

    private String foundBy;
    private String foundByValue;

	public GuestData()
	{
		bandListComplete = false;
		identifierListComplete = false;
	}
	
	public long getPartyId()
	{
		return this.partyId;
	}
	
	public void setPartyId(long partyId)
	{
		this.partyId = partyId;
	}

	public long getGuestId() {
		return guestId;
	}
	
	public void setGuestId(long guestId)
	{
		this.guestId = guestId;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}

	public String getSwid() {
		return swid;
	}

	public void setSwid(String swid)
	{
		this.swid = swid;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}
	
	public String getParentEmail() {
		return parentEmail;
	}

	public void setParentEmail(String parentEmail)
	{
		this.parentEmail = parentEmail;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	public int getVisitCount() {
		return visitCount;
	}
	
	public void setVisitCount(int visitCount)
	{
		this.visitCount = visitCount;
	}

	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	public void setIDMSTypeName(String IDMSTypeName)
	{
		this.IDMSTypeName = IDMSTypeName;
	}
	
	public List<Link> getLinks()
	{
		return this.links;
	}
	
	public void setLinkgs(List<Link> links)
	{
		this.links = links;
	}

	public String getMiddleName() {
		return middleName;
	}
	
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
	
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}

	public String getxBMSId() 
	{
		return xBMSId;
	}
	
	public void setxBMSId(String xBMSId)
	{
		this.xBMSId = xBMSId;
	}
	
	public List<BandData> getBandList() {
		return bandList;
	}
	
	public void setBandList(List<BandData> bandList)
	{
		this.bandList = bandList;
	}
	
	public boolean isBandListComplete() {
		return bandListComplete;
	}
	
	public void setBandListComplete(boolean bandListComplete)
	{
		this.bandListComplete = bandListComplete;
	}
	
	public List<GuestDataIdentifier> getIdentifierList() {
		return identifierList;
	}
	
	public void setIdentifierList(List<GuestDataIdentifier> identifierList)
	{
		this.identifierList = identifierList;
	}
	
	public boolean isIdentifierListComplete() {
		return identifierListComplete;
	}
	
	public void setIdentifierListComplete(boolean identifierListComplete)
	{
		this.identifierListComplete = identifierListComplete;
	}
	
	public com.disney.xband.idms.lib.model.GuestProfile getGuestProfile()
	{
		com.disney.xband.idms.lib.model.GuestProfile guest = new com.disney.xband.idms.lib.model.GuestProfile();
		
		guest.setGuestId(guestId);
		guest.setDateOfBirth(dateOfBirth);
		guest.setStatus(status);
		guest.setCountryCode(countryCode);
		guest.setSwid(swid);
		guest.setLanguageCode(languageCode);
		guest.setEmailAddress(emailAddress);
		guest.setParentEmail(parentEmail);
		
		GuestName name = new GuestName();
		name.setFirstName(firstName);
		name.setMiddleName(middleName);
		name.setLastName(lastName);
		name.setTitle(title);
		name.setSuffix(suffix);
		guest.setName(name);
		
		guest.setGender(gender);
		guest.setUserName(userName);
		guest.setAvatar(avatar);
		guest.setVisitCount(visitCount);
		guest.setGuestType(IDMSTypeName);
		guest.setPartyId(partyId);
		
		ArrayList<com.disney.xband.idms.lib.model.XBand> xbands =  new ArrayList<com.disney.xband.idms.lib.model.XBand>(0);
		if (bandList != null)
		{
			xbands.ensureCapacity(bandList.size());
			for (BandData band : bandList)
			{
				xbands.add(band.getXBand());
			}
		}
		guest.setXBands(xbands);
		
		ArrayList<com.disney.xband.idms.lib.model.GuestIdentifier> identifiers = new ArrayList<com.disney.xband.idms.lib.model.GuestIdentifier>(0);
		if (identifierList != null)
		{
			identifiers.ensureCapacity(identifierList.size());
			for (GuestDataIdentifier identifier : identifierList)
			{
				com.disney.xband.idms.lib.model.GuestIdentifier guestIdentifier = new com.disney.xband.idms.lib.model.GuestIdentifier();
				guestIdentifier.setGuestId(guestId);
				guestIdentifier.setType(identifier.getType());
				guestIdentifier.setValue(identifier.getValue());
				identifiers.add(guestIdentifier);
			}
		}
		guest.setIdentifiers(identifiers);
				
		//private List<GuestExperience> experiences;
		
		return guest;
	}
	
	public com.disney.xband.idms.lib.model.Guest getGuest()
	{
		com.disney.xband.idms.lib.model.Guest guest = new com.disney.xband.idms.lib.model.Guest();
		
		guest.setGuestId(guestId);
		guest.setDateOfBirth(dateOfBirth);
		guest.setStatus(status);
		
		guest.setCountryCode(countryCode);
		guest.setSwid(swid);
		guest.setLanguageCode(languageCode);
		guest.setEmailAddress(emailAddress);
		guest.setParentEmail(parentEmail);
		
		GuestName name = new GuestName();
		name.setFirstName(firstName);
		name.setMiddleName(middleName);
		name.setLastName(lastName);
		name.setTitle(title);
		name.setSuffix(suffix);
		guest.setName(name);
	
		guest.setGender(gender);
		guest.setUserName(userName);
		guest.setAvatar(avatar);
		guest.setVisitCount(visitCount);
		guest.setIDMSTypeName(IDMSTypeName);
		guest.setPartyId(partyId);
		
		guest.setLinks(links);
		
		return guest;
	}

	public com.disney.xband.idms.lib.model.xview.Guest getXviewGuest()
	{
		com.disney.xband.idms.lib.model.xview.Guest guest = new com.disney.xband.idms.lib.model.xview.Guest();
		
		guest.setGuestId(String.valueOf(guestId));
		guest.setBirthdate(dateOfBirth);
		
		if (status.equals("Active"))
			guest.setActive(true);
		else
			guest.setActive(false);

		guest.setCountryCode(countryCode);

		guest.setFirstName(firstName);
		guest.setLastName(lastName);
	
		guest.setCreatedBy(createdBy);
		guest.setCreatedDate(createdDate);

		guest.setCreatedBy(updatedBy);
		guest.setCreatedDate(updatedDate);
		
		guest.setXBMSId(xBMSId);
		
		List<com.disney.xband.idms.lib.model.xview.Xband> xbands = null;
		if (bandList != null)
		{
			xbands = new ArrayList<com.disney.xband.idms.lib.model.xview.Xband>(bandList.size());
			for (BandData band : bandList)
			{
				xbands.add(band.getxband());
			}
		}
		guest.setXbands(xbands);
		
		if (identifierList != null)
		{
			guest.setIdentifiers(new ArrayList<com.disney.xband.idms.lib.model.GuestIdentifier>());
			for (GuestDataIdentifier identifier : identifierList)
			{
				GuestIdentifier gi = new GuestIdentifier();
				gi.setType(identifier.getType());
				gi.setValue(identifier.getValue());
				gi.setGuestId(getGuestId());
				guest.getIdentifiers().add(gi);
			}
		}
			
		return guest;
	}

    public String getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(String foundBy) {
        this.foundBy = foundBy;
    }

    public String getFoundByValue() {
        return foundByValue;
    }

    public void setFoundByValue(String foundByValue) {
        this.foundByValue = foundByValue;
    }
}
