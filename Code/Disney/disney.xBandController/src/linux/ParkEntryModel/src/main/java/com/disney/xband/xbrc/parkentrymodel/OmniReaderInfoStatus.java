package com.disney.xband.xbrc.parkentrymodel;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.parkgreeter.lib.message.fromxbrc.ErrorCode;

public class OmniReaderInfoStatus
{
	private StatusType status = StatusType.Green;
	private String statusMessage = null;
	private ErrorCode errorCode;
	
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
	public ErrorCode getErrorCode()
	{
		return errorCode;
	}
	public void setErrorCode(ErrorCode errorCode)
	{
		this.errorCode = errorCode;
	}
}
