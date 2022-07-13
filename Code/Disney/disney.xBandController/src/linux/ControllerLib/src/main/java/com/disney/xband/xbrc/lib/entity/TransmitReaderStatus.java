package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="transmitReaderStatus")
public class TransmitReaderStatus {
	private String name;
	private String time;
	private String facilityId;
	
	private Collection<TransmitLocationStatus> locations;

	public TransmitReaderStatus() {
		locations = new LinkedList<TransmitLocationStatus>();
	}
	
	@XmlElementWrapper(name="locations")
	@XmlElement(name="location")
	public Collection<TransmitLocationStatus> getLocations() {
		return locations;
	}

	public void setLocations(Collection<TransmitLocationStatus> locations) {
		this.locations = locations;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@XmlAttribute
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
}
