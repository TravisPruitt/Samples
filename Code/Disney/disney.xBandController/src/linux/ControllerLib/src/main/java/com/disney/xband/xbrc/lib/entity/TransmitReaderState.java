package com.disney.xband.xbrc.lib.entity;

import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class TransmitReaderState {
	private String name;
	private String ipAddress;
	private Integer transmitterHaPriority;
	private Boolean alive;
	private Boolean currentTransmitter;
	
	public TransmitReaderState()
	{
	}
	
	public TransmitReaderState(ReaderInfo ri) {
		this.name = ri.getName();
		this.ipAddress = ri.getIpAddress();
		this.transmitterHaPriority = ri.getTransmitterHaPriority();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public Integer getTransmitterHaPriority() {
		return transmitterHaPriority;
	}
	public void setTransmitterHaPriority(Integer transmitterHaPriority) {
		this.transmitterHaPriority = transmitterHaPriority;
	}
	public Boolean getAlive() {
		return alive;
	}
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public Boolean getCurrentTransmitter() {
		return currentTransmitter;
	}
	public void setCurrentTransmitter(Boolean currentTransmitter) {
		this.currentTransmitter = currentTransmitter;
	}
}
