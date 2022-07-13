package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PartyMember
{
	
	private long guestId;
	private boolean isPrimary;
	private LinkCollection links;

	
	@XmlElement(name="guestId")
	public long getGuestId() {
		return guestId;
	}
	
	@XmlElement(name="guestId")
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}
	
	@XmlElement(name="isPrimary")
	public boolean isPrimary() {
		return isPrimary;
	}
	
	@XmlElement(name="isPrimary")
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}

	@XmlElement(name="links")
	public void setLinks(LinkCollection links) {
		this.links = links;
	}



}
