// POJO DUMP
// DATE : Fri Nov 04 14:28:50 PDT 2011
//
// TABLE : XView.Guest_xband
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  

import java.util.Date;

@XmlRootElement
public class Guest_xband
{

	private long guest_xband_id; // Data_Type :-5 
	private long guestId; // Data_Type :-5 
	private long xbandId; // Data_Type :-5 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93 
	private boolean active; // Data_Type :-7 
	
	// Extended
	private XViewGuest guest;
	private XViewXband xband;


	public Guest_xband() {

	}


	public long getGuest_xband_id() {
		return guest_xband_id;
	}

	public void setGuest_xband_id(long guest_xband_id) {
		this.guest_xband_id=guest_xband_id;
	}

	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId=guestId;
	}

	public long getXbandId() {
		return xbandId;
	}

	public void setXbandId(long xbandId) {
		this.xbandId=xbandId;
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

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active=active;
	}


	// Extended
	
	public XViewGuest getGuest()
	{
		return guest;
	}
	
	public void setGuest(XViewGuest guest)
	{
		this.guest = guest;
	}
	
	
	public XViewXband getXBand()
	{
		return xband;
	}
	
	public void setXBand(XViewXband xband)
	{
		this.xband = xband;
	}
	
	
}