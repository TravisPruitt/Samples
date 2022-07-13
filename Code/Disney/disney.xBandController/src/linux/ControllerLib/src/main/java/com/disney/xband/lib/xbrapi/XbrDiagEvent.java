package com.disney.xband.lib.xbrapi;

import com.disney.xband.common.lib.health.StatusType;

public class XbrDiagEvent extends XbrEvent {

	private StatusType status;
	private String statusMessage;
	private Integer batteryLevel;
	private Integer batteryTime;
	private Double temperature;
	
	
	public XbrDiagEvent() {
		super(XbrEventType.XbrDiag);
	}
	
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}
	public Integer getBatteryTime() {
		return batteryTime;
	}
	public void setBatteryTime(Integer batteryTime) {
		this.batteryTime = batteryTime;
	}
	
	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}
	
	public String getStatusMsg() {
		return statusMessage;
	}

	public void setStatusMsg(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Integer batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
}
