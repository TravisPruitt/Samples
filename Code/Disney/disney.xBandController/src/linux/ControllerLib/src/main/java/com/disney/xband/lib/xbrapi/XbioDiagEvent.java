package com.disney.xband.lib.xbrapi;

public class XbioDiagEvent extends XbrEvent {
	
	// xBIO diagnostic data formatted as a base 64 encoded blob.
	private String xbioData;
	
	public XbioDiagEvent() {
		super(XbrEventType.XbioDiag);
	}

	@Override
	public String getID() {
		return null;
	}

	public String getXbioData() {
		return xbioData;
	}

	public void setXbioData(String xbioData) {
		this.xbioData = xbioData;
	}
}
