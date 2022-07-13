package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class AbandonMessagePayload extends MessagePayload 
{
	private static final long serialVersionUID = 1L;

	private AbandonMessage message;
	
	@XmlElement(name="message")
	public AbandonMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(AbandonMessage message)
	{
		this.message = message;
	}

}
