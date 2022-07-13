package com.disney.xband.xbrc.lib.entity;

import java.util.Date;

/*
 * XbioImage stores Lumidigm images for a guest resulting from a finger scan.
 */
public class XbioTemplate {
	private Long id;
	private Long transactionId;
	private String template;
	private Long totalScanDuration;
	private Date timestamp;
	
	public XbioTemplate(Long id, Long transactionId, String template,
			Long totalScanDuration, Date timestamp) {
		super();
		this.id = id;
		this.transactionId = transactionId;
		this.template = template;
		this.totalScanDuration = totalScanDuration;
		this.timestamp = timestamp;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getTotalScanDuration() {
		return totalScanDuration;
	}

	public void setTotalScanDuration(Long totalScanDuration) {
		this.totalScanDuration = totalScanDuration;
	}
}
