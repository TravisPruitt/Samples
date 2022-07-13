package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;

@XmlRootElement(name="venue")
public class GuestMessagePayload extends MessagePayload {

	private static final long serialVersionUID = 1L;

	private PEGuestMessage message;
	
	@XmlElement(name="message")
	public PEGuestMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(PEGuestMessage message)
	{
		this.message = message;
	}
	
}
