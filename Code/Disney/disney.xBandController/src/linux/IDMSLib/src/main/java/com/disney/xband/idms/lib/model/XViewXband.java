// POJO DUMP
// DATE : Fri Nov 04 14:29:41 PDT 2011
//
// TABLE : XView.Xband
//////////////////////////////////////////////////////
package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.Date;
import java.util.List;

@XmlRootElement
public class XViewXband
{

	private long xbandId; // Data_Type :-5
	private String bandId; // Data_Type :-5
	private String longRangeId; // Data_Type :-5
	private String tapId; // Data_Type :-5
	private String uID; // Data_Type :-5
	private String bandFriendlyName; // Data_Type :-9
	private String printedName; // Data_Type :-9
	private boolean active; // Data_Type :-7
	private String createdBy; // Data_Type :-9
	private Date createdDate; // Data_Type :93
	private String updatedBy; // Data_Type :-9
	private Date updatedDate; // Data_Type :93
	private List<XViewGuest> guests;
	private String bandType;

	public XViewXband()
	{

	}

	public long getXbandId()
	{
		return xbandId;
	}

	public void setXbandId(long xbandId)
	{
		this.xbandId = xbandId;
	}

	public String getBandId()
	{
		return bandId;
	}

	public void setBandId(String bandId)
	{
		this.bandId = bandId;
	}

	@XmlElement(name = "lrid")
	public String getLongRangeId()
	{
		return longRangeId;
	}

	@XmlElement(name = "lrid")
	public void setLongRangeId(String longRangeId)
	{
		this.longRangeId = longRangeId;
	}

	public String getTapId()
	{
		return tapId;
	}

	public void setTapId(String tapId)
	{
		this.tapId = tapId;
	}

	public String getUID()
	{
		return uID;
	}

	public void setUID(String uID)
	{
		this.uID = uID;
	}

	public String getBandFriendlyName()
	{
		return bandFriendlyName;
	}

	public void setBandFriendlyName(String bandFriendlyName)
	{
		this.bandFriendlyName = bandFriendlyName;
	}

	public String getPrintedName()
	{
		return printedName;
	}

	public void setPrintedName(String printedName)
	{
		this.printedName = printedName;
	}

	public boolean getActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public List<XViewGuest> getGuests()
	{
		return this.guests;
	}

	public void setGuests(List<XViewGuest> guests)
	{
		this.guests = guests;
	}

	@XmlTransient
	public String getCreatedBy()
	{
		return createdBy;
	}

	@XmlTransient
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	@XmlTransient
	public Date getCreatedDate()
	{
		return createdDate;
	}

	@XmlTransient
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	@XmlTransient
	public String getUpdatedBy()
	{
		return updatedBy;
	}

	@XmlTransient
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	@XmlTransient
	public Date getUpdatedDate()
	{
		return updatedDate;
	}

	@XmlTransient
	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}

	@XmlElement(name = "bandType")
	public String getBandType()
	{
		return bandType;
	}

	public void setBandType(String bandType)
	{
		this.bandType = bandType;
	}

}