package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement
@XmlType(propOrder={"id","type","startDateTime","endDateTime","links"})
public class GuestDataEntitlement 
{
	private String id;
	private String type;
	private String startDateTime;
	private String endDateTime;
	private LinkCollection links;
	
	public GuestDataEntitlement()
	{
		this.links = new LinkCollection();
	}
	
	@XmlElement(name="id")
	public String getID()
	{
		return this.id;
	}
	
	public void setID(String id)
	{
		this.id = id;
	}

	@XmlElement(name="type")
	public String getType()
	{
		return this.type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

	@XmlElement(name="startDateTime")
	public String getStartDateTime()
	{
		return this.startDateTime;
	}
	
	public void setStartDateTime(String startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	@XmlElement(name="endDateTime")
	public String getEndDateTime()
	{
		return this.endDateTime;
	}
	
	public void setEndDateTime(String endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
}
