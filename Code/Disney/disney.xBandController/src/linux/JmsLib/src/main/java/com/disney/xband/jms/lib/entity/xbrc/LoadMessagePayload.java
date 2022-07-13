package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class LoadMessagePayload extends MessagePayload  {

	private static final long serialVersionUID = 1L;

	private LoadMessage message;
	
	@XmlElement(name="message")
	public LoadMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(LoadMessage message)
	{
		this.message = message;
	}
}
