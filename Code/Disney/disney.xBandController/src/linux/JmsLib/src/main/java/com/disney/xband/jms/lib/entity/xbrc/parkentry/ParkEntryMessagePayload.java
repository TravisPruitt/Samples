package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;

@XmlRootElement(name="venue")
public class ParkEntryMessagePayload extends MessagePayload {
	private static final long serialVersionUID = 1L;

	private ParkEntryMessage message;
	
	@XmlElement(name="message", type=ParkEntryMessage.class)
	public ParkEntryMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(ParkEntryMessage message)
	{
		this.message = message;
	}
}
