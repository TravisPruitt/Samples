package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestEmailPut
{
	
	private long guestId;
	private String emailAddress;
	private int visitCount;
	
	@XmlElement(name="guestId")
	public long getGuestId() {
		return guestId;
	}
	
	@XmlElement(name="guestId")
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}
	
	@XmlElement(name="emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	@XmlElement(name="emailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@XmlElement(name="visitCount")
	public int getVisitCount()
	{
		return this.visitCount;
	}
	
	@XmlElement(name="visitCount")
	public void setVisitCount(int visitCount)
	{
		this.visitCount = visitCount;
	}
	

}
