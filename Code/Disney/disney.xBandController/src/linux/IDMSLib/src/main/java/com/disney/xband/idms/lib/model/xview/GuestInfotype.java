package com.disney.xband.idms.lib.model.xview;

import java.util.Date;

public class GuestInfotype 
{

	private int guest_InfoTypeId;

	private String createdBy;


	private Date createdDate;

	private String typeName;

	private String updatedBy;


	private Date updatedDate;

	//bi-directional many-to-one association to GuestInfo

	private GuestInfo guestInfo;

    public GuestInfotype() {
    }

	public int getGuest_InfoTypeId() {
		return this.guest_InfoTypeId;
	}

	public void setGuest_InfoTypeId(int guest_InfoTypeId) {
		this.guest_InfoTypeId = guest_InfoTypeId;
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

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
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

	public GuestInfo getGuestInfo() {
		return this.guestInfo;
	}

	public void setGuestInfo(GuestInfo guestInfo) {
		this.guestInfo = guestInfo;
	}
	
}