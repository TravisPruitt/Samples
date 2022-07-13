package com.disney.xband.lib.controllerapi;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.xbrc.lib.model.LocationInfo;

@XmlRootElement(name="venue")
public class ReaderLocationInfo {
	private List<LocationInfo> readerlocationinfo;
	
	// attributes
	private String name;
	private String time;

	@XmlAttribute
	public String getName() {
		return name;
	}
	@XmlAttribute	
	public String getTime() {
		return time;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@XmlElementWrapper
	@XmlElement(name="readerlocation")
	public List<LocationInfo> getReaderlocationinfo() {
		return readerlocationinfo;
	}

	public void setReaderlocationinfo(List<LocationInfo> locations) {
		this.readerlocationinfo = locations;
	}
}
