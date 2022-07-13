package com.disney.xband.idms.lib.model.xview;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.List;

@XmlRootElement
public class Xband 
{
	private String xBandId;

	private boolean active;

	private String bandFriendlyName;

	private String bandId;

	private String createdBy;

	private Date createdDate;

	private String lRId;

	private String printedName;

	private String tapId;
	
	private String bandType;
	
	private String publicId;

	private String updatedBy;

	private Date updatedDate;

	private List<Guest> guests;

    public Xband() {
    }

    
 	public String getBandType() 
	{
		return bandType;
	}

	public void setBandType(String bandType) 
	{
		this.bandType = bandType;
	}

	public String getPublicId() 
	{
		return this.publicId;
	}

	public void setPublicId(String publicId) 
	{
		this.publicId = publicId;
	}

	public String getXBandId() {
		return this.xBandId;
	}

	public void setXBandId(String xBandId) {
		this.xBandId = xBandId;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBandFriendlyName() {
		return this.bandFriendlyName;
	}

	public void setBandFriendlyName(String bandFriendlyName) {
		this.bandFriendlyName = bandFriendlyName;
	}

	public String getBandId() {
		return this.bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
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

	public String getLRId() {
		return this.lRId;
	}

	public void setLRId(String lRId) {
		this.lRId = lRId;
	}

	public String getPrintedName() {
		return this.printedName;
	}

	public void setPrintedName(String printedName) {
		this.printedName = printedName;
	}

	public String getTapId() {
		return this.tapId;
	}

	public void setTapId(String tapId) {
		this.tapId = tapId;
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

	public List<Guest> getGuests() {
		return this.guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	
}