package com.disney.xband.lib.controllerapi;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.lib.xbrapi.LrrEvent;

@XmlRootElement(name="venue")
public class LrrEventsByBand
{
	private Collection<LrrEvent> events = new ArrayList<LrrEvent>();
	
	private String sVenue;
	private String sTimestamp;
	
	public void setEvents(Collection<LrrEvent> events)
	{
		this.events = events;
	}
	
	public void setVenue(String sVenue)
	{
		this.sVenue = sVenue;
	}
	
	@XmlAttribute(name="name")
	public String getVenue()
	{
		return sVenue;
	}
	
	public void setTimestamp(String sTimestamp)
	{
		this.sTimestamp = sTimestamp;
	}
	
	@XmlAttribute(name="time")
	public String getTimestamp()
	{
		return sTimestamp;
	}
	
	@XmlElementWrapper
	@XmlElement(name="event")
	public Collection<LrrEvent> getEvents()
	{
		return events;
	}
}
