package com.disney.xband.jms.lib.entity.gxp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 {
                "cacheXpassApntmtId":3079,
                "apntmtId":15722,
                "apntmtStatus":"INQ",
                "entertainmentId":80010176,
                "locationId":80010176,
                "xbandId":1234567,
                "apntmtReason":"STD",
                "tapDate":1342716587433
}
 
 */

@XmlRootElement(name="redemption")
public class RedemptionEvent 
{	
	private Long cacheXpassApntmtId;
	private Long apntmtId;
	private String apntmtStatus;
	private long entertainmentId;
	private long locationId;
	private Long xbandId;
	private String apntmtReason;
	private long tapDate;
	
	@XmlElement(name="cacheXpassApntmtId")
	public Long getCacheXpassApntmtId()
	{
		return this.cacheXpassApntmtId;
	}
	
	public void setCacheXpassApntmtId(Long cacheXpassApntmtId)
	{
		this.cacheXpassApntmtId = cacheXpassApntmtId;
	}
	
	@XmlElement(name="apntmtId")
	public Long getApntmtId()
	{
		return this.apntmtId;
	}
	
	public void setApntmtId(Long apntmtId)
	{
		this.apntmtId = apntmtId;
	}
	
	@XmlElement(name="apntmtStatus")
	public String getApntmtStatus()
	{
		return this.apntmtStatus;
	}
	
	public void setApntmtStatus(String apntmtStatus)
	{
		this.apntmtStatus = apntmtStatus;
	}
	
	@XmlElement(name="entertainmentId")
	public long getEntertainmentId()
	{
		return this.entertainmentId;
	}
	
	public void setEntertainmentId(long entertainmentId)
	{
		this.entertainmentId = entertainmentId;
	}
	
	@XmlElement(name="locationId")
	public long getLocationId()
	{
		return this.locationId;
	}
	
	public void setLocationId(long locationId)
	{
		this.locationId = locationId;
	}
	
	@XmlElement(name="xbandId")
	public Long getXbandId()
	{
		return this.xbandId;
	}
	
	public void setXbandId(Long xbandId)
	{
		this.xbandId = xbandId;
	}
	
	@XmlElement(name="apntmtReason")
	public String getApntmtReason()
	{
		return this.apntmtReason;
	}
	
	public void setApntmtReason(String apntmtReason)
	{
		this.apntmtReason = apntmtReason;
	}
	@XmlElement(name="tapDate")
	public long getTapDate()
	{
		return this.tapDate;
	}
	
	public void setTapDate(long tapDate)
	{
		this.tapDate = tapDate;
	}
}
