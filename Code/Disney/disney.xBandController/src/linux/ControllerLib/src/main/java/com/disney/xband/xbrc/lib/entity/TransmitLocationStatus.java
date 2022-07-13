package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.disney.xband.xbrc.lib.model.LocationInfo;

public class TransmitLocationStatus {
	private String name;
	private String statusMessage;
	private String currentTransmitter;
	private Collection<TransmitReaderState> readers;
	
	public TransmitLocationStatus()
	{
	}
	
	public TransmitLocationStatus(LocationInfo loc)
	{
		this.name = loc.getName();
		this.readers = new LinkedList<TransmitReaderState>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name="readers")
	@XmlElement(name="reader")
	public Collection<TransmitReaderState> getReaders() {
		return readers;
	}
	public void setReaders(Collection<TransmitReaderState> readers) {
		this.readers = readers;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getCurrentTransmitter() {
		return currentTransmitter;
	}

	public void setCurrentTransmitter(String currentTransmitter) {
		this.currentTransmitter = currentTransmitter;
	}
}
