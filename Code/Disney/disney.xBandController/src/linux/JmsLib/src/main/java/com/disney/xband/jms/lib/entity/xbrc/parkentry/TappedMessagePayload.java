package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;

@XmlRootElement(name="venue")
public class TappedMessagePayload extends MessagePayload {
	
	private static final long serialVersionUID = 1L;

	private TappedMessage message;
	
	@XmlElement(name="message", type=TappedMessage.class)
	public TappedMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(TappedMessage message)
	{
		this.message = message;
	}
	
}
