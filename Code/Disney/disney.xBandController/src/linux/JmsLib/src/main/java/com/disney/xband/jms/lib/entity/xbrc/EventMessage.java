package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.*;

public class EventMessage extends XbrcMessage {

	private static final long serialVersionUID = 1L;
	
	
	private Long guestId;

	private String pidDecimal;
	
	private String linkId;
	
	private String linkIdType;

	private Boolean xpass;

	private String locationId;

	private String readerLocation;
	
	private String bandtype;
	
	private String rfid;
		
	@XmlElement(name="guestid")
	public Long getGuestId()
	{
		return this.guestId;
	}
	
	public void setGuestId(Long guestId)
	{
		this.guestId = guestId;
	}
		
	@XmlElement(name="publicid")
	public String getPidDecimal()
	{
		return this.pidDecimal;
	}
	
	public void setPidDecimal(String pidDecimal)
	{
		this.pidDecimal = pidDecimal;
	}
	
	@XmlElement(name="linkid")
	public String getLinkId()
	{
		return this.linkId;
	}
	
	public void setLinkId(String linkId)
	{
		this.linkId = linkId;
	}
	
	@XmlElement(name="linkidtype")
	public String getLinkIdType()
	{
		return this.linkIdType;
	}
	
	public void setLinkIdType(String linkIdType)
	{
		this.linkIdType = linkIdType;
	}
	
	@XmlElement(name="xpass")
	public Boolean getxPass()
	{
		return this.xpass;
	}
	
	public void setxPass(Boolean xpass)
	{
		this.xpass = xpass;
	}

	@XmlElement(name="locationid")
	public String getLocationId()
	{
		return this.locationId;
	}
	
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@XmlElement(name="readerlocation")
	public String getReaderLocation()
	{
		return this.readerLocation;
	}
	
	public void setReaderLocation(String readerLocation)
	{
		this.readerLocation = readerLocation;
	}

	@XmlElement(name="bandtype")
	public String getBandtype()
	{
		return this.bandtype;
	}
	
	public void setBandtype(String bandtype)
	{
		this.bandtype = bandtype;
	}

	@XmlElement(name="rfid")
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
}
