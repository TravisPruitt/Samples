package com.disney.xband.lib.xbrapi;

public class BioImageEvent extends XbrEvent {
	private String uid;
	private Long transactionId;
	private Long templateId;
	private String xbioImages;

	public BioImageEvent() {
		super(XbrEventType.BioImage);
	}
	
	@Override
	public String getID() {
		return uid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getXbioImages() {
		return xbioImages;
	}

	public void setXbioImages(String xbioImages) {
		this.xbioImages = xbioImages;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
}
