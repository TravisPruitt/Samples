package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestCelebrationCollection {
	
	private List<Celebration> celebrations;
	private LinkCollection links;
	
	@XmlElement(name="magicalCelebrations")
	public List<Celebration> getCelebrations() {
		return celebrations;
	}
	
	@XmlElement(name="magicalCelebrations")
	public void setCelebrations(List<Celebration> celebrations) {
		this.celebrations = celebrations;
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}
	
	@XmlElement(name="links")
	public void setLinks(LinkCollection links) {
		this.links = links;
	}
	

}
