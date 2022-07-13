package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class ReaderEventMessagePayload extends MessagePayload {

	private static final long serialVersionUID = 1L;

	private ReaderEventMessage message;
	
	@XmlElement(name="message")
	public ReaderEventMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(ReaderEventMessage message)
	{
		this.message = message;
	}
}

