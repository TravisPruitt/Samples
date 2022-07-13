package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="guest")
public class JmsGuestState
{
	private String guestId;
	private boolean xPass;
	private String state;
	private JmsLocationInfo location;
	
	@XmlElement(name="id")
	public String getGuestId()
	{
		return guestId;
	}
	public void setGuestId(String guestId)
	{
		this.guestId = guestId;
	}
	
	@XmlElement(name="xpass")
	public boolean isxPass()
	{
		return xPass;
	}
	public void setxPass(boolean xPass)
	{
		this.xPass = xPass;
	}
	
	@XmlElement(name="state")
	public String getState()
	{
		return state;
	}
	public void setState(String state)
	{
		this.state = state;
	}
	
	@XmlElement(name="location")
	public JmsLocationInfo getLocation()
	{
		return location;
	}
	public void setLocation(JmsLocationInfo location)
	{
		this.location = location;
	}
	
	
	

}
