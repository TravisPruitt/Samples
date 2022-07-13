package com.disney.xband.xbrc.attractionmodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="venue")
public class GSTInfo
{
	@XmlAttribute(name="name")
	public String name;
	
	@XmlAttribute(name="time")
	public String time;
	
	@XmlElement(name="guest")
	public List<GuestStatus<GuestStatusState>> guests;
	
	public GSTInfo() {};
}
