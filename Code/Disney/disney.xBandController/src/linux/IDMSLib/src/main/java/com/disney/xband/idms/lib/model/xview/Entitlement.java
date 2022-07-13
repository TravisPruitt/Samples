package com.disney.xband.idms.lib.model.xview;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.List;

@XmlRootElement
public class Entitlement 
{
	private String entitlementId;

	private BigInteger experienceId;

	private BigInteger guestId;

	private BigInteger resortReservationId;

	private String title;

	private List<Guest> guests;

    public Entitlement() {
    }

	public String getEntitlementId() {
		return this.entitlementId;
	}

	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}

	public BigInteger getExperienceId() {
		return this.experienceId;
	}

	public void setExperienceId(BigInteger experienceId) {
		this.experienceId = experienceId;
	}

	public BigInteger getGuestId() {
		return this.guestId;
	}

	public void setGuestId(BigInteger guestId) {
		this.guestId = guestId;
	}

	public BigInteger getResortReservationId() {
		return this.resortReservationId;
	}

	public void setResortReservationId(BigInteger resortReservationId) {
		this.resortReservationId = resortReservationId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Guest> getGuests() {
		return this.guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	
}