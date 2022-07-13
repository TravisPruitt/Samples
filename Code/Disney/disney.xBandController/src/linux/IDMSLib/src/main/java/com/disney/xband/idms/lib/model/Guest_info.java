// POJO DUMP
// DATE : Fri Nov 04 14:28:30 PDT 2011
//
// TABLE : XView.Guest_info
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  
import java.util.Date;

@XmlRootElement
public class Guest_info 
{

	private long guestInfoId; // Data_Type :-5 
	private long guestId; // Data_Type :-5 
	private String cellPhone; // Data_Type :-9 
	private String address1; // Data_Type :-9 
	private String address2; // Data_Type :-9 
	private String city; // Data_Type :-9 
	private String state; // Data_Type :-9 
	private String countryCode; // Data_Type :-9 
	private String postalCode; // Data_Type :-9 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93 
	private String sourceId; // Data_Type :-9 
	private long sourceTypeId; // Data_Type :-5 


	public Guest_info() {

	}



	public long getGuestInfoId() {
		return guestInfoId;
	}

	public void setGuestInfoId(long guestInfoId) {
		this.guestInfoId=guestInfoId;
	}

	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId=guestId;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone=cellPhone;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1=address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2=address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city=city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state=state;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode=countryCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode=postalCode;
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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId=sourceId;
	}

	public long getSourceTypeId() {
		return sourceTypeId;
	}

	public void setSourceTypeId(long sourceTypeId) {
		this.sourceTypeId=sourceTypeId;
	}


	

}