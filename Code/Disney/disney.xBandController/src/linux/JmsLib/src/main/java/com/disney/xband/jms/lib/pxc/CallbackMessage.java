package com.disney.xband.jms.lib.pxc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallbackMessage
{
	private String sorName;
	private String status;
	private String orchestrationId;
	
	@XmlElement(name="sor")
	public String getSor()
	{
		return this.sorName;
	}
	
	public void setSor(String sorName)
	{
		this.sorName = sorName;
	}
	
	@XmlElement(name="status")
	public String getStatus()
	{
		return this.status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	@XmlElement(name="orchestrationId")
	public String getOrchestrationId()
	{
		return this.orchestrationId;
	}
	
	public void setOrchestrationId(String orchestrationId)
	{
		this.orchestrationId = orchestrationId;
	}
}
