// POJO DUMP
// DATE : Tue Nov 08 15:01:12 PST 2011
//
// TABLE : XView.Source_system_link
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@XmlRootElement
public class Source_system_link
{

	private long sourceTypeId; // Data_Type :-5 
	private long guestId; // Data_Type :-5 
	private String sourceSystemIdValue; // Data_Type :-9 
	private int iDMSTypeId; // Data_Type :4 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93 


	// Extended.
	private IDMSTypeListItem idmsType;
	
	public Source_system_link() {
	
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

	public String getSourceSystemIdValue() {
		return sourceSystemIdValue;
	}

	public void setSourceSystemIdValue(String sourceSystemIdValue) {
		this.sourceSystemIdValue=sourceSystemIdValue;
	}

	public int getIDMSTypeId() {
		return iDMSTypeId;
	}

	public void setIDMSTypeId(int iDMSTypeId) {
		this.iDMSTypeId=iDMSTypeId;
	}

	@XmlTransient
	public String getCreatedBy() {
		return createdBy;
	}

	@XmlTransient
	public void setCreatedBy(String createdBy) {
		this.createdBy=createdBy;
	}

	@XmlTransient
	public Date getCreatedDate() {
		return createdDate;
	}

	@XmlTransient
	public void setCreatedDate(Date createdDate) {
		this.createdDate=createdDate;
	}

	@XmlTransient
	public String getUpdatedBy() {
		return updatedBy;
	}

	@XmlTransient
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy=updatedBy;
	}

	@XmlTransient
	public Date getUpdatedDate() {
		return updatedDate;
	}

	@XmlTransient
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate=updatedDate;
	}

	// Extended
	
	public IDMSTypeListItem getIDMSType()
	{
		return this.idmsType;
	}
	
	public void setIDMSType(IDMSTypeListItem idmsTypeListItem)
	{
		this.idmsType = idmsTypeListItem;
	}

}