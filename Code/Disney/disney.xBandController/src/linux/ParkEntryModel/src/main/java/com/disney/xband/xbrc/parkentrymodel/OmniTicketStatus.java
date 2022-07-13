package com.disney.xband.xbrc.parkentrymodel;

import com.disney.xband.common.lib.health.StatusType;

public class OmniTicketStatus
{
	private StatusType status = StatusType.Green;
	private String statusMessage = null;
	
	public StatusType getStatus() {
		return status;
	}
	public void setStatus(StatusType status) {
		this.status = status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	protected OmniTicketStatus clone() {
		OmniTicketStatus clone = new OmniTicketStatus();
		clone.setStatus(status);
		clone.setStatusMessage(statusMessage);
		return clone;
	}
}
