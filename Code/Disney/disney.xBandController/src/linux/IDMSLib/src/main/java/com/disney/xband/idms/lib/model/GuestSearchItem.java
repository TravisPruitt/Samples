package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestSearchItem {
	
	private String groupName;
	private String leadGuest;
	private long leadGuestId;
	private String leadGuestXId;
	private GuestAddress address;
	private String emailAddress;
	private int groupCount;
	private long partyId;
	
	@XmlElement(name="groupName")
	public String getGroupName() {
		return groupName;
	}
	
	@XmlElement(name="groupName")
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@XmlElement(name="LeadGuestName")
	public String getLeadGuest() {
		return leadGuest;
	}
	
	@XmlElement(name="LeadGuestName")
	public void setLeadGuest(String leadGuest) {
		this.leadGuest = leadGuest;
	}
	
	@XmlElement(name="LeadGuestId")
	public long getLeadGuestId() {
		return leadGuestId;
	}
	
	@XmlElement(name="LeadGuestId")
	public void setLeadGuestId(long leadGuestId) {
		this.leadGuestId = leadGuestId;
	}
	
	@XmlElement(name="LeadGuestXId")
	public String getLeadGuestXId() {
		return leadGuestXId;
	}
	
	@XmlElement(name="LeadGuestXId")
	public void setLeadGuestXId(String leadGuestXId) {
		this.leadGuestXId = leadGuestXId;
	}
	
	@XmlElement(name="address")
	public GuestAddress getAddress() {
		return address;
	}
	
	@XmlElement(name="address")
	public void setAddress(GuestAddress address) {
		this.address = address;
	}
	
	@XmlElement(name="emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	@XmlElement(name="emailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@XmlElement(name="groupCount")
	public int getGroupCount() {
		return groupCount;
	}
	
	@XmlElement(name="groupCount")
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
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
	
	

}
