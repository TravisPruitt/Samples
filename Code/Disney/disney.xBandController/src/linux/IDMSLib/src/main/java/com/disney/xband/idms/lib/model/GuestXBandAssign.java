package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestXBandAssign
{

	private String xbandId;
	private String guestId;
	
	@XmlElement(name="xbandId")
	public String getXbandId() 
	{
		return xbandId;
	}
	
	public void setXbandId(String xbandId) {
		this.xbandId = xbandId;
	}
	
	@XmlElement(name="guestId")
	public String getGuestId() 
	{
		return guestId;
	}
	
	public void setGuestId(String guestId) 
	{
		this.guestId = guestId;
	}
	
	
	
}
