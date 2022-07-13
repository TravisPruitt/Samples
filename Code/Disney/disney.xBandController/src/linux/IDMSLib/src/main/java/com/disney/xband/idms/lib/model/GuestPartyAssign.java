package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestPartyAssign {

	private long guestId;
	
	private long partyId;
	
	public GuestPartyAssign() {}

	@XmlElement(name="guestId")
	public long getGuestId() {
		return guestId;
	}

	@XmlElement(name="guestId")
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}

	@XmlElement(name="partyId")
	public long getPartyId() {
		return partyId;
	}

	@XmlElement(name="partyId")
	public void setPartyId(long partyId) {
		this.partyId = partyId;
	}
	
	
	
}
