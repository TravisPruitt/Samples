package com.disney.xband.idms.lib.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScheduledItem 
{
	
	private long id;
	private String type;
	private Date startDateTime;
	private Date endDateTime;
	private List<ScheduledGuestEntry> guests;
	
	@XmlElement(name="id")
	public long getId() {
		return id;
	}
	
	@XmlElement(name="id")
	public void setId(long id) {
		this.id = id;
	}
	
	@XmlElement(name="type")
	public String getType() {
		return type;
	}
	
	@XmlElement(name="type")
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="startDateTime")
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	@XmlElement(name="startDateTime")
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	@XmlElement(name="endDateTime")
	public Date getEndDateTime() {
		return endDateTime;
	}
	
	@XmlElement(name="endDateTime")
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	@XmlElement(name="guests")
	public List<ScheduledGuestEntry> getGuests() {
		return guests;
	}
	
	@XmlElement(name="guests")
	public void setGuests(List<ScheduledGuestEntry> guests) {
		this.guests = guests;
	}
	
	

}
