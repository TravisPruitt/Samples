package com.disney.xband.xbms.web;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "message-state")
@XmlEnum
public enum MessageState
{
	@XmlEnumValue("Not Sent")
	NotSent(0),
		
	@XmlEnumValue("Sending")
	Sending(1),
			
	@XmlEnumValue("Sent")
	Sent(2);
	
	private final int value;   

	private MessageState(int value)
	{
		this.value = value;
	}
	
	public int getValue() 
	{
		return this.value;
	}
}
