package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class XBRCMessage {
	private String type;
	private Date timestamp;
	private String payload;
	
	public XBRCMessage(String type, Date timestamp, String payload) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.payload = payload;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	
}
