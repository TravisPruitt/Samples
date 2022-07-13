package com.disney.xband.idms.lib.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Celebration 
{
	
	private long celebrationId;
	private long guestId;
	private String celebrationName;
	private String celebrationMessage;
	private Date start;
	private Date end;
	private boolean active;
	private String IDMSTypeName;
	private long IDMSTypeId;
	
	private LinkCollection links;
	// Robert makes a comment.
	
	@XmlElement(name="celebrationId")
	public long getCelebrationId() {
		return celebrationId;
	}
	
	@XmlElement(name="celebrationId")
	public void setCelebrationId(long celebrationId) {
		this.celebrationId = celebrationId;
	}
	
	@XmlElement(name="guestId")
	public long getGuestId() {
		return guestId;
	}
	
	@XmlElement(name="guestId")
	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}
	
	@XmlElement(name="celebrationName")
	public String getCelebrationName() {
		return celebrationName;
	}
	
	@XmlElement(name="celebrationName")
	public void setCelebrationName(String celebrationName) {
		this.celebrationName = celebrationName;
	}
	
	@XmlElement(name="celebrationMessage")
	public String getCelebrationMessage() {
		return celebrationMessage;
	}
	
	@XmlElement(name="celebrationMessage")
	public void setCelebrationMessage(String celebrationMessage) {
		this.celebrationMessage = celebrationMessage;
	}
	
	@XmlElement(name="start")
	public Date getStart() {
		return start;
	}
	
	@XmlElement(name="start")
	public void setStart(Date start) {
		this.start = start;
	}
	
	@XmlElement(name="end")
	public Date getEnd() {
		return end;
	}
	
	@XmlElement(name="end")
	public void setEnd(Date end) {
		this.end = end;
	}
	
	@XmlElement(name="active")
	public boolean isActive() {
		return active;
	}
	
	@XmlElement(name="active")
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@XmlElement(name="type")
	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	@XmlElement(name="type")
	public void setIDMSTypeName(String iDMSTypeName) {
		IDMSTypeName = iDMSTypeName;
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}
	
	@XmlElement(name="links")
	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
	
	@XmlElement(name="idmstypeId")
	public long getIDMSTypeId()
	{
		return this.IDMSTypeId;
	}
	
	public void setIDMSTypeId(long idmsTypeId)
	{
		this.IDMSTypeId = idmsTypeId;
	}

}
