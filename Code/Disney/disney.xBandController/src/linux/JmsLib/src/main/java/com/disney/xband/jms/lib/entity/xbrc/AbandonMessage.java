package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;

public class AbandonMessage  extends EventMessage {

	private static final long serialVersionUID = 1L;
		
	private String lastxmit;

	@XmlElement(name="lastxmit")
	public String getLastXmit()
	{
		return this.lastxmit;
	}
	
	public void setLastXmit(String lastxmit)
	{
		this.lastxmit = lastxmit;
	}
}
