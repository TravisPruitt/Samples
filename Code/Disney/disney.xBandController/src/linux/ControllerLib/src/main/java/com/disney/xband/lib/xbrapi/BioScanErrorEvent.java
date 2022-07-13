package com.disney.xband.lib.xbrapi;

public class BioScanErrorEvent extends XbrEvent {
	private String uid;
	private String reason;
	
	public BioScanErrorEvent() {
		super(XbrEventType.BioScanError);
	}
	
	@Override
	public String getID() {
		return null;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
