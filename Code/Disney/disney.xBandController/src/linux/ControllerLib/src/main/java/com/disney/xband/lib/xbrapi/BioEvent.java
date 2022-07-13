package com.disney.xband.lib.xbrapi;

/*
* Biometric read event from the xFP.
*/
public class BioEvent extends XbrEvent {

	// Biometric data.
	private String xbioTemplate;
	private String xbioImages;
	
	@Override
	public String getID() {
		return null;
	}
	
	public BioEvent(XbrEventType type) {
		super(type);
	}


	public String getXbioTemplate() {
		return xbioTemplate;
	}

	public void setXbioTemplate(String xbioTemplate) {
		this.xbioTemplate = xbioTemplate;
	}

	public String getXbioImages() {
		return xbioImages;
	}

	public void setXbioImages(String xbioImages) {
		this.xbioImages = xbioImages;
	}
}
