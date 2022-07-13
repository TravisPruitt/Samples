package com.disney.xband.idms.lib.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestExperience
{
	
	private Date time;
	private String attraction;
	private String queue;
	
	@XmlElement(name="date")
	public Date getTime() {
		return time;
	}
	
	@XmlElement(name="date")
	public void setTime(Date time) {
		this.time = time;
	}
	
	@XmlElement(name="attraction")
	public String getAttraction() {
		return attraction;
	}
	
	@XmlElement(name="attraction")
	public void setAttraction(String attraction) {
		this.attraction = attraction;
	}
	
	@XmlElement(name="queue")
	public String getQueue() {
		return queue;
	}
	
	@XmlElement(name="queue")
	public void setQueue(String queue) {
		this.queue = queue;
	}

}
