package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IDMSTypeCollection {
	
	public String key;
	public int count;
	public List<IDMSTypeListItem> items;
	
	@XmlElement(name="count")
	public int getCount() {
		return count;
	}
	
	@XmlElement(name="count")
	public void setCount(int count) {
		this.count = count;
	}
	
	@XmlElement(name="items")
	public List<IDMSTypeListItem> getItems() {
		return items;
	}
	
	@XmlElement(name="items")
	public void setItems(List<IDMSTypeListItem> items) {
		this.items = items;
	}
	
	@XmlElement(name="key")
	public String getKey()
	{
		return key;
	}
	
	@XmlElement(name="key")
	public void setKey(String value)
	{
		this.key = value;
	}

	
}
