package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class GenericEventAggregate extends EventAggregate {
	
	private Object event;
	
	public GenericEventAggregate(String sID, String sReader, Date Timestamp, Object event) {
		super(sID, "", sReader, Timestamp);
		this.event = event;
	}

	public Object getEvent() {
		return event;
	}

	public void setEvent(Object event) {
		this.event = event;
	}
}
