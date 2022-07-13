package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class EventMessagePayload extends MessagePayload {

	private static final long serialVersionUID = 1L;

	private EventMessage message;
	
	@XmlElement(name="message", type=EventMessage.class)
	public EventMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(EventMessage message)
	{
		this.message = message;
	}
	
}
