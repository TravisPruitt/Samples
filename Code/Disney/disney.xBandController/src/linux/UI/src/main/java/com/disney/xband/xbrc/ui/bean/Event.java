package com.disney.xband.xbrc.ui.bean;

import java.util.Date;

public class Event {
	private Long id;
	private String readerId;
	private String bandId;
	private Date timestamp;
	private Integer strength;
	private Integer packetSequence;
	private Integer frequencey;
	private Integer channel;
	
	public Event(Long id, String readerId, String bandId, Date timestamp,
			Integer strength, Integer packetSequence, Integer frequencey,
			Integer channel) {
		super();
		this.id = id;
		this.readerId = readerId;
		this.bandId = bandId;
		this.timestamp = timestamp;
		this.strength = strength;
		this.packetSequence = packetSequence;
		this.frequencey = frequencey;
		this.channel = channel;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public String getBandId() {
		return bandId;
	}
	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Integer getStrength() {
		return strength;
	}
	public void setStrength(Integer strength) {
		this.strength = strength;
	}
	public Integer getPacketSequence() {
		return packetSequence;
	}
	public void setPacketSequence(Integer packetSequence) {
		this.packetSequence = packetSequence;
	}
	public Integer getFrequencey() {
		return frequencey;
	}
	public void setFrequencey(Integer frequencey) {
		this.frequencey = frequencey;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
}
