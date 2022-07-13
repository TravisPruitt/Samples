// POJO DUMP
// DATE : Fri Nov 04 14:27:30 PDT 2011
//
// TABLE : XView.Guest
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;


import javax.xml.bind.annotation.XmlRootElement;  
import javax.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@XmlRootElement
public class XViewGuest 
{

	private long guestId; // Data_Type :-5 
	private UUID iDMSID; // Data_Type :1 
	private int iDMSTypeId; // Data_Type :4 
	private String lastName; // Data_Type :-9 
	private String firstName; // Data_Type :-9 
	private Date dOB; // Data_Type :91 
	private boolean active; // Data_Type :-7 
	private String createdBy; // Data_Type :-9 
	private Date createdDate; // Data_Type :93 
	private String updatedBy; // Data_Type :-9 
	private Date updatedDate; // Data_Type :93
	private int visitCount;
	private String avatarName;


	
	// Extended
	private List<XViewXband> xbands;
	private List<Source_system_link> sourceSystemLink;
	private IDMSTypeListItem idmsType;

	public XViewGuest() {
		
		// Extended
		xbands = new ArrayList<XViewXband>();
		sourceSystemLink = new ArrayList<Source_system_link>();

	}



	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId=guestId;
	}

	public UUID getIDMSID() {
		return iDMSID;
	}

	public void setIDMSID(UUID iDMSID) {
		this.iDMSID=iDMSID;
	}

	public int getIDMSTypeId() {
		return iDMSTypeId;
	}

	public void setIDMSTypeId(int iDMSTypeId) {
		this.iDMSTypeId=iDMSTypeId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName=lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName=firstName;
	}

	public Date getDOB() {
		return dOB;
	}

	public void setDOB(Date dOB) {
		this.dOB=dOB;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active=active;
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
	
	public int getVisitCount()
	{
		return this.visitCount;
	}
	
	public void setVisitCount(int visitCount)
	{
		this.visitCount = visitCount;
	}

	public String getAvatarName()
	{
		return this.avatarName;
	}
	
	public void setAvatarName(String avatarName)
	{
		this.avatarName = avatarName;
	}
	
//// --- Extended -----
	
	
	public List<XViewXband> getXBands()
	{
		return xbands;
	}
	
	public void setXBands(List<XViewXband> xbands)
	{
		this.xbands = xbands;
	}
	
	public List<Source_system_link> getSourceSystemLinks()
	{
		return this.sourceSystemLink;
	}
	
	public void setSourceSystemLinks(List<Source_system_link> sourceSystemLink)
	{
		this.sourceSystemLink = sourceSystemLink;
	}
	
	public IDMSTypeListItem getIDMSType()
	{
		return this.idmsType;
	}
	
	public void setIDMSType(IDMSTypeListItem idmsType)
	{
		this.idmsType = idmsType;
	}
	

}