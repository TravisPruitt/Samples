package com.disney.xband.idms.lib.model.oneview;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestDataResult 
{
	private GuestData guestData;
	
	public GuestDataResult()
	{
	}
	
	@XmlElement(name="guestData")
	public GuestData getGuestData()
	{
		return this.guestData;
	}

	public void setGuestData(GuestData guestData)
	{
		this.guestData = guestData;
	}
}
