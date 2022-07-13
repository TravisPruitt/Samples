package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class MetricsMessagePayload extends MessagePayload  
{
	private static final long serialVersionUID = 1L;

	private MetricsMessage message;
	
	@XmlElement(name="message")
	public MetricsMessage getMessage()
	{
		return this.message;
	}
	
	public void setMessage(MetricsMessage message)
	{
		this.message = message;
	}
}
