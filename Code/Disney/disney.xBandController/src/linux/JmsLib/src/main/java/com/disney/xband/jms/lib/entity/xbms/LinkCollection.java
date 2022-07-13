package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.jms.lib.entity.common.Link;

@XmlRootElement
@XmlType(propOrder={"self"})
public class LinkCollection 
{
	private Link self;
	
	@XmlElement(name="self")
	public Link getSelf()
	{
		return this.self;
	}
	
	public void setSelf(Link self)
	{
		this.self = self;
	}

}
