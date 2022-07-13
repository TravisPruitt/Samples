package com.disney.xband.lib.xbrapi;

import java.util.LinkedList;

public class XbrEvents {
	private String readerName;
	private LinkedList<XbrEvent> events;
	
	public XbrEvents() {
		events = new LinkedList<XbrEvent>();
	}

	public String getReaderName() {
		return readerName;
	}

	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}

	public LinkedList<XbrEvent> getEvents() {
		return events;
	}

	public void setEvents(LinkedList<XbrEvent> events) {
		this.events = events;
	}
	
}
