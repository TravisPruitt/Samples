package com.disney.xband.xbms.web;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message
{
	private String firstName;	
	private String lastName;
	private String xbandOwnerId;	
	private String xbandRequestId;
	private String xbandId;	
	private MessageState xbandRequestMessageState;	
	private MessageState xbandMessageState;
	
	@XmlElement(name="firstName")
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	@XmlElement(name="lastName")
	public String getLastName()
	{
		return this.lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	@XmlElement(name="xbandOwnerId")
	public String getXbandOwnerId()
	{
		return this.xbandOwnerId;
	}
	
	public void setXbandOwnerId(String xbandOwnerId)
	{
		this.xbandOwnerId = xbandOwnerId;
	}
	
	@XmlElement(name="xbandId")
	public String getXbandId()
	{
		return this.xbandId;
	}
	
	public void setXbandId(String xbandId)
	{
		this.xbandId = xbandId;
	}	

	@XmlElement(name="xbandRequestId")
	public String getXbandRequestId()
	{
		return this.xbandRequestId;
	}
	
	public void setXbandRequestId(String xbandRequestId)
	{
		this.xbandRequestId = xbandRequestId;
	}	
	
	@XmlElement(name="xbandRequestMessageState")
	public MessageState getXbandRequestMessageState()
	{
		return this.xbandRequestMessageState;
	}
	
	public void setXbandRequestMessageState(MessageState xbandRequestMessageState)
	{
		this.xbandRequestMessageState = xbandRequestMessageState;
	}
	
	@XmlElement(name="xbandMessageState")
	public MessageState getXbandMessageState()
	{
		return this.xbandMessageState;
	}
	
	public void setXbandMessageState(MessageState xbandMessageState)
	{
		this.xbandMessageState = xbandMessageState;
	}
}
