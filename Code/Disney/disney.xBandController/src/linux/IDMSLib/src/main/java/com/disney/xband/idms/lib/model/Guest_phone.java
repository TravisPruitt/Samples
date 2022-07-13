// POJO DUMP
// DATE : Tue Nov 08 15:02:47 PST 2011
//
// TABLE : XView.Guest_phone
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  
import java.util.Date;


@XmlRootElement
public class Guest_phone
{

	private long guest_phoneId; // Data_Type :-5 
	private long guestId; // Data_Type :-5 
	private int iDMSTypeId; // Data_Type :4 
	private String phonenumber; // Data_Type :-9 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93 


	public Guest_phone() {
	
	}
	
	

	public long getGuest_phoneId() {
		return guest_phoneId;
	}

	public void setGuest_phoneId(long guest_phoneId) {
		this.guest_phoneId=guest_phoneId;
	}

	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId=guestId;
	}

	public int getIDMSTypeId() {
		return iDMSTypeId;
	}

	public void setIDMSTypeId(int iDMSTypeId) {
		this.iDMSTypeId=iDMSTypeId;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber=phonenumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy=createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate=createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy=updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate=updatedDate;
	}
}