package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScheduledGuestEntry {
	
	private LinkCollection links;
	
	private String role;
	
	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}
	
	@XmlElement(name="links")
	public void setLinks(LinkCollection links) {
		this.links = links;
	}
	
	@XmlElement(name="role")
	public String getRole() {
		return role;
	}
	
	@XmlElement(name="role")
	public void setRole(String role) {
		this.role = role;
	}

	

}
