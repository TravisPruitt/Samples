// POJO DUMP
// DATE : Fri Nov 04 14:29:20 PDT 2011
//
// TABLE : XView.Source_type
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  

import java.util.Date;

@XmlRootElement
public class Source_type 
{

	private long sourceTypeId; // Data_Type :-5 
	private long guestId; // Data_Type :-5 
	private String sourceSystemId; // Data_Type :-9 
	private String sourceTypeName; // Data_Type :-9 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93 


	public Source_type() {
	
	}
	
	

	public long getSourceTypeId() {
		return sourceTypeId;
	}

	public void setSourceTypeId(long sourceTypeId) {
		this.sourceTypeId=sourceTypeId;
	}

	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId=guestId;
	}

	public String getSourceSystemId() {
		return sourceSystemId;
	}

	public void setSourceSystemId(String sourceSystemId) {
		this.sourceSystemId=sourceSystemId;
	}

	public String getSourceTypeName() {
		return sourceTypeName;
	}

	public void setSourceTypeName(String sourceTypeName) {
		this.sourceTypeName=sourceTypeName;
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