package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class ExitMessagePayload extends MessagePayload {

	private static final long serialVersionUID = 1L;

	private ExitMessage message;
	
	@XmlElement(name="message")
	public ExitMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(ExitMessage message)
	{
		this.message = message;
	}
}
