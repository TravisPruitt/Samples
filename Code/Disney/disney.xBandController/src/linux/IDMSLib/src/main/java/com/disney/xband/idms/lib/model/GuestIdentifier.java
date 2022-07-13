package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestIdentifier 
{
	
	private String type;
	private String value;
	private long guestId;
	
	
	@XmlElement(name="type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement(name="guestId")
	public long getGuestId()
	{
		return this.guestId;
	}

	public void setGuestId(long guestId)
	{
		this.guestId = guestId;
	}
	

}
