package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.jms.lib.entity.common.Link;

@XmlRootElement
@XmlType(propOrder={"self","entries"})
public class Xbands 
{
	private Link self;
	private List<NotificationXband> entries;
	
	@XmlElement(name="self")
	public Link getSelf()
	{
		return this.self;
	}

	public void setSelf(Link self)
	{
		this.self = self;
	}

	@XmlElement(name="entries")
	public List<NotificationXband> getEntries()
	{
		return this.entries;
	}

	public void setEntries(List<NotificationXband> entries)
	{
		this.entries = entries;
	}

}
