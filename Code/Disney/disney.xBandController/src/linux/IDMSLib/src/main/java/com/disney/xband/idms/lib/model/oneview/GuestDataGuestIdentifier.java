package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"type","value"})
public class GuestDataGuestIdentifier 
{
	private String type;
	private String value;

	@XmlElement(name="type")
	public String getType()
	{
		return type;
	}
	
	public void setType(String type) 
	{
		this.type = type;
	}
	
	@XmlElement(name="value")
	public String getValue() 
	{
		return value;
	}
	
	public void setValue(String value) 
	{
		this.value = value;
	}
	
}
