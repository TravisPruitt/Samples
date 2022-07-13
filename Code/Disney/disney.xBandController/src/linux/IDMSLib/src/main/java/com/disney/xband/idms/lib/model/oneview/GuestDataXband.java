package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement
@XmlType(propOrder={"links","shortRangePublicId","longRangePublicId","xBandRequestId","externalNumber",
		"secureId","shortRangeTag","longRangeTag","publicXBandId","primaryStatus","secondaryStatus"})
public class GuestDataXband 
{
	private LinkCollection links;
	
	private String shortRangePublicId;
	private String longRangePublicId;
	//TODO: Use properties
	@XmlElement(name="xBandRequestId")
	public String xBandRequestId;
	private String externalNumber;
	private String secureId;
	private String shortRangeTag;
	private String longRangeTag;
	private String primaryStatus;
	private String secondaryStatus;
	private String publicXBandId;
	
	public GuestDataXband()
	{
		this.links = new LinkCollection();
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}
	
	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
	
	@XmlElement(name="shortRangePublicId")
	public String getShortRangePublicId()
	{
		return this.shortRangePublicId;
	}
	
	public void setShortRangePublicId(String shortRangePublicId)
	{
		this.shortRangePublicId = shortRangePublicId;
	}

	@XmlElement(name="longRangePublicId")
	public String getLongRangePublicId()
	{
		return this.longRangePublicId;
	}
	
/*	public void setLongRangePublicId(String longRangePublicId)
	{
		this.longRangePublicId = longRangePublicId;
	}

	public String getXBandRequestId()
	{
		return this.xBandRequestId;
	}*/
	
	public void setXBandRequestId(String xBandRequestId)
	{
		this.xBandRequestId = xBandRequestId;
	}

	@XmlElement(name="externalNumber")
	public String getExternalNumber()
	{
		return this.externalNumber;
	}
	
	public void setExternalNumber(String externalNumber)
	{
		this.externalNumber = externalNumber;
	}

	@XmlElement(name="secureId")
	public String getSecureId()
	{
		return this.secureId;
	}
	
	public void setSecureId(String secureId)
	{
		this.secureId = secureId;
	}

	@XmlElement(name="shortRangeTag")
	public String getShortRangeTag()
	{
		return this.shortRangeTag;
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

	@XmlElement(name="primaryStatus")
	public String getPrimaryStatus()
	{
		return this.primaryStatus;
	}
	
	public void setPrimaryStatus(String primaryStatus)
	{
		this.primaryStatus = primaryStatus;
	}

	@XmlElement(name="secondaryStatus")
	public String getSecondaryStatus()
	{
		return this.secondaryStatus;
	}
	
	public void setSecondaryStatus(String secondaryStatus)
	{
		this.secondaryStatus = secondaryStatus;
	}

	@XmlElement(name="publicXBandId")
	public String getPublicXBandId()
	{
		return this.publicXBandId;
	}
	
	public void setPublicXBandId(String publicXBandId)
	{
		this.publicXBandId = publicXBandId;
	}
}
