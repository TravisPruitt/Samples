package com.disney.xband.xbrc.lib.xbrms;



/**
 * POJO for an event serialized into jackson formatted as follows:
 * {"attractionname":"xCoaster1","eventtypename":"ENTRY","readerlocation":"Entry","timestamp":"1317655689140"}
 */
public class XbrmsEvent {
	private String attractionname;
	private String readerlocation;
	private String guestId;
	// event time in seconds
	private Long timestamp;
	private String eventtypename;
	
	public XbrmsEvent(){}
	
	public XbrmsEvent(String readerlocation, String guestId, Long timestamp,
					String attractionName, String eventtypename) 
	{
		this.guestId = guestId;
		this.timestamp = timestamp;
		this.attractionname = attractionName;
		this.readerlocation = readerlocation;
		this.eventtypename = eventtypename;
	}
	
	public String getBandId() {
		return guestId;
	}
	public void setBandId(String bandId) {
		this.guestId = bandId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public String getAttractionname() {
		return attractionname;
	}

	public void setAttractionname(String attractionname) {
		this.attractionname = attractionname;
	}

	public String getReaderlocation() {
		return readerlocation;
	}
	
	public void setReaderlocation(String readerlocation) {
		this.readerlocation = readerlocation;
	}
	
	public String getEventtypename() {
		return eventtypename;
	}

	public void setEventtypename(String eventtypename) {
		this.eventtypename = eventtypename;
	}
}
