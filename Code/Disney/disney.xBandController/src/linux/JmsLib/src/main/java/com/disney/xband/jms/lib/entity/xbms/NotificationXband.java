package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"primaryStatus","secondaryStatus","publicXBandId","shortRangepublicId",
		"longRangepublicId","secureXBandId","externalNumber","shortRangeId","longRangeId","links"})
public class NotificationXband 
{
    private String primaryStatus;
    private String secondaryStatus;
    private String publicXBandId;
    private String shortRangepublicId;
    private String longRangepublicId;
    private String secureXBandId;
    private String externalNumber;
    private String shortRangeId;
    private String longRangeId;
    private LinkCollection links;

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

	@XmlElement(name="shortRangepublicId")
	public String getShortRangepublicId()
	{
		return this.shortRangepublicId;
	}

	public void setShortRangepublicId(String shortRangepublicId)
	{
		this.shortRangepublicId = shortRangepublicId;
	}

	@XmlElement(name="longRangepublicId")
	public String getLongRangepublicId()
	{
		return this.longRangepublicId;
	}

	public void setLongRangepublicId(String longRangepublicId)
	{
		this.longRangepublicId = longRangepublicId;
	}

	@XmlElement(name="secureXBandId")
	public String getSecureXBandId()
	{
		return this.secureXBandId;
	}

	public void setSecureXBandId(String secureXBandId)
	{
		this.secureXBandId = secureXBandId;
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

	@XmlElement(name="shortRangeId")
	public String getShortRangeId()
	{
		return this.shortRangeId;
	}

	public void setShortRangeId(String shortRangeId)
	{
		this.shortRangeId = shortRangeId;
	}

	@XmlElement(name="longRangeId")
	public String getLongRangeId()
	{
		return this.longRangeId;
	}

	public void setLongRangeId(String longRangeId)
	{
		this.longRangeId = longRangeId;
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

}
