package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class JmsEntryMessage extends JmsMessage
{
	private String guestId;
	private boolean xPass;
	private String readerLocation;
	
	@XmlElement(name="guestid")
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
	
	@XmlElement(name="readerlocation")
	public String getReaderLocation()
	{
		return readerLocation;
	}
	public void setReaderLocation(String readerLocation)
	{
		this.readerLocation = readerLocation;
	}
}
