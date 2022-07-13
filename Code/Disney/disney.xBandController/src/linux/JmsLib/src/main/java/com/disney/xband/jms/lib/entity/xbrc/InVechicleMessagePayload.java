package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class InVechicleMessagePayload extends MessagePayload  
{

	private static final long serialVersionUID = 1L;

	private InVehicleMessage message;
	
	@XmlElement(name="message")
	public InVehicleMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(InVehicleMessage message)
	{
		this.message = message;
	}
}
