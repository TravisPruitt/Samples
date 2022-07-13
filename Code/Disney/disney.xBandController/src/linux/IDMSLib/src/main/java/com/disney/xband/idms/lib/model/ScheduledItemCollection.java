package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScheduledItemCollection
{
	
	private int startItem;
	private int itemLimit;
	private int itemCount;
	private LinkCollection links;
	private List<ScheduledItem> entries;
	
	
	@XmlElement(name="startItem")
	public int getStartItem() {
		return startItem;
	}
	
	@XmlElement(name="startItem")
	public void setStartItem(int startItem) {
		this.startItem = startItem;
	}
	
	@XmlElement(name="itemLimit")
	public int getItemLimit() {
		return itemLimit;
	}
	
	@XmlElement(name="itemLimit")
	public void setItemLimit(int itemLimit) {
		this.itemLimit = itemLimit;
	}
	
	@XmlElement(name="itemCount")
	public int getItemCount() {
		return itemCount;
	}
	
	@XmlElement(name="itemCount")
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}
	
	@XmlElement(name="links")
	public void setLinks(LinkCollection links) {
		this.links = links;
	}
	
	@XmlElement(name="entries")
	public List<ScheduledItem> getEntries() {
		return entries;
	}
	
	@XmlElement(name="entries")
	public void setEntries(List<ScheduledItem> entries) {
		this.entries = entries;
	}
	
	

}
