package com.disney.xband.jms.lib.entity.xbrc;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

public class XbrcMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String messageType;

	private String timestamp;
	
	@XmlAttribute(name="type")
	public String getMessageType()
	{
		return this.messageType;
	}
	
	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}
	
	@XmlAttribute(name="time")
	public String getTimestamp()
	{
		return this.timestamp;
	}
	
	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}
	
}
