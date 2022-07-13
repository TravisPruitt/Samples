package com.disney.xband.idms.lib.model.xview;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class GuestInfo 
{

	private String guestInfoid;

	private boolean active;

	private String createdBy;

	private Date createdDate;

	private String guest_Info;

	private String updatedBy;

	private Date updatedDate;

	//bi-directional many-to-one association to Guest
	private BigInteger guestId;

	private int guest_InfotypeId;

    public GuestInfo() {
    }

	public String getGuestInfoid() {
		return this.guestInfoid;
	}

	public void setGuestInfoid(String guestInfoid) {
		this.guestInfoid = guestInfoid;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public String getGuest_Info() {
		return this.guest_Info;
	}

	public void setGuest_Info(String guest_Info) {
		this.guest_Info = guest_Info;
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

	public BigInteger getGuestId() {
		return this.guestId;
	}

	public void setGuestId(BigInteger guestId) {
		this.guestId = guestId;
	}
	
	public int getGuest_InfotypeId() {
		return this.guest_InfotypeId;
	}

	public void setGuest_InfotypeId(int guest_InfotypeId) {
		this.guest_InfotypeId = guest_InfotypeId;
	}
	
}