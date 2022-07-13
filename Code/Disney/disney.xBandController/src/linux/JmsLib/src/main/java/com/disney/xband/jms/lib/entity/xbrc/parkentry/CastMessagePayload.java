package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.MessagePayload;

@XmlRootElement(name="venue")
public class CastMessagePayload extends MessagePayload {

	private static final long serialVersionUID = 1L;

	private PECastMessage message;
	
	@XmlElement(name="message")
	public PECastMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(PECastMessage message)
	{
		this.message = message;
	}
	
}
