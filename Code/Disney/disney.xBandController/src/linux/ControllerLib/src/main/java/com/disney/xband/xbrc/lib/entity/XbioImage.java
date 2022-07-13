package com.disney.xband.xbrc.lib.entity;

import java.util.Date;

/*
 * XbioImage stores Lumidigm images for a guest resulting from a finger scan.
 */
public class XbioImage {
	private Long id;	
	private Long transactionId;
	private Long templateId;	
	private String images;
	private Date timestamp;

	public XbioImage(Long id, Long transactionId, Long templateId,
			String images, Date timestamp) {
		super();
		this.id = id;
		this.transactionId = transactionId;
		this.templateId = templateId;
		this.images = images;
		this.timestamp = timestamp;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
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
