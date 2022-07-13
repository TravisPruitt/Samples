package com.disney.xband.idms.lib.model;

import java.util.List;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XBand
{

	private long xBandId;
	private String xbandRequestId;
	private String shortRangeTag;
	private String longRangeTag;
	private String printedName;
	private String state;
	private String secondaryState;
	private String productId;
	private String publicId;
	private String bandId;
	private Date assignmentDateTime;
	private LinkCollection links;
	private List<Guest> guests;
	private String bandType;

	public XBand()
	{
	}

	@XmlElement(name = "xbandId")
	public long getXbandId()
	{
		return xBandId;
	}

	public void setXbandId(long xBandId)
	{
		this.xBandId = xBandId;
	}

	@XmlElement(name = "xbandRequestId")
	public String getXbandRequestId()
	{
		return xbandRequestId;
	}

	@XmlElement(name = "xbandRequestId")
	public void setXbandRequestId(String xbandRequestId)
	{
		this.xbandRequestId = xbandRequestId;
	}

	@XmlElement(name = "shortRangeTag")
	public String getShortRangeTag()
	{
		return shortRangeTag;
	}

	@XmlElement(name = "shortRangeTag")
	public void setShortRangeTag(String shortRangeTag)
	{
		this.shortRangeTag = shortRangeTag;
	}

	@XmlElement(name = "longRangeTag")
	public String getLongRangeTag()
	{
		return longRangeTag;
	}

	@XmlElement(name = "longRangeTag")
	public void setLongRangeTag(String longRangeTag)
	{
		this.longRangeTag = longRangeTag;
	}

	@XmlElement(name = "printedName")
	public String getPrintedName()
	{
		return printedName;
	}

	@XmlElement(name = "printedName")
	public void setPrintedName(String printedName)
	{
		this.printedName = printedName;
	}

	@XmlElement(name = "state")
	public String getState()
	{
		return state;
	}

	@XmlElement(name = "state")
	public void setState(String state)
	{
		this.state = state;
	}

	@XmlElement(name = "secondaryState")
	public String getSecondaryState()
	{
		return secondaryState;
	}

	@XmlElement(name = "secondaryState")
	public void setSecondaryState(String secondaryState)
	{
		this.secondaryState = secondaryState;
	}

	@XmlElement(name = "productId")
	public String getProductId()
	{
		return productId;
	}

	@XmlElement(name = "productId")
	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	@XmlElement(name = "publicId")
	public String getPublicId()
	{
		return this.publicId;
	}

	public void setPublicId(String publicId)
	{
		this.publicId = publicId;
	}

	@XmlElement(name = "assignmentDateTime")
	public Date getAssignmentDateTime()
	{
		return assignmentDateTime;
	}

	@XmlElement(name = "assignmentDateTime")
	public void setAssignmentDateTime(Date assignmentDateTime)
	{
		this.assignmentDateTime = assignmentDateTime;
	}

	@XmlElement(name = "links")
	public LinkCollection getOneViewLinks()
	{
		return this.links;
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}

	@XmlElement(name = "guests")
	public List<Guest> getGuests()
	{
		return this.guests;
	}

	@XmlElement(name = "guests")
	public void setGuests(List<Guest> guests)
	{
		this.guests = guests;
	}

	@XmlElement(name = "bandId")
	public String getBandId()
	{
		return this.bandId;
	}

	@XmlElement(name = "bandId")
	public void setBandId(String bandId)
	{
		this.bandId = bandId;
	}

	@XmlElement(name = "bandType")
	public String getBandType()
	{
		return this.bandType;
	}

	@XmlElement(name = "bandType")
	public void setBandType(String bandType)
	{
		this.bandType = bandType;
	}
}
