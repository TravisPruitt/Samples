package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;

public class XBandPut 
{

	private String bandId;
	private String longRangeTag;
	private String shortRangeTag;
	private String secureId;
	private String uid;
	private String publicId;
	private String printedName;
	private String xbmsId;
	private String bandType;

	@XmlElement(name="bandId")
	public String getBandId() 
	{
		return this.bandId;
	}

	public void setBandId(String bandId) 
	{
		this.bandId = bandId;;
	}

	@XmlElement(name="shortRangeTag")
	public String getShortRangeTag() 
	{
		return shortRangeTag;
	}

	public void setShortRangeTag(String shortRangeTag) 
	{
		this.shortRangeTag = shortRangeTag;
	}

	@XmlElement(name="longRangeTag")
	public String getLongRangeTag() 
	{
		return this.longRangeTag;
	}

	public void setLongRangeTag(String longRangeTag)
	{
		this.longRangeTag = longRangeTag;
	}

	@XmlElement(name="secureId")
	public String getSecureId() 
	{
		return secureId;
	}

	public void setSecureId(String secureId) 
	{
		this.secureId = secureId;
	}

	@XmlElement(name="uid")
	public String getUID() 
	{
		return this.uid;
	}

	@XmlElement(name="uid")
	public void setUID(String uid) 
	{
		this.uid = uid;
	}

	@XmlElement(name="publicId")
	public String getPublicId() 
	{
		return this.publicId;
	}

	public void setPublicId(String publicId) 
	{
		this.publicId = publicId;
	}

	@XmlElement(name="printedName")
	public String getPrintedName() 
	{
		return this.printedName;
	}

	public void setPrintedName(String printedName) 
	{
		this.printedName = printedName;
	}

	@XmlElement(name="xbmsId")
	public String getXbmsId() 
	{
		return this.xbmsId;
	}

	public void setXbmsId(String xbmsId) 
	{
		this.xbmsId = xbmsId;
	}

	@XmlElement(name="bandType")
	public String getBandType() 
	{
		return this.bandType;
	}

	public void setBandType(String bandType) 
	{
		this.bandType = bandType;
	}
}
