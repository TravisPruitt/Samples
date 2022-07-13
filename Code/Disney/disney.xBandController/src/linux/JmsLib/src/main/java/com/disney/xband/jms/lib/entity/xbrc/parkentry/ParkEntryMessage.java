package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.disney.xband.jms.lib.entity.xbrc.XbrcMessage;


public class ParkEntryMessage extends XbrcMessage {
	
	private static final long serialVersionUID = 1L;
	
	private List<String> pidDecimal;
	
	private Boolean xpass;
	
	private String reason;
	
	private List<String> xbrcreferencenumber;
	
	private String sequence;
	
	private Integer entErrorCode;
	private String entErrorDescription;
	
	@XmlElements({
	@XmlElement(name="publicid"),
	@XmlElement(name="publicId")})
	public List<String> getPidDecimal() {
		return pidDecimal;
	}

	public void setPidDecimal(List<String> pidDecimal) {
		this.pidDecimal = pidDecimal;
	}
	
	/*
	 * These are not being used. publicid is always lowercase
	@XmlElement(name="publicid")
	public String getPidDecimal_lc() {
		return pidDecimal;
	}

	public void setPidDecimal_lc(String pidDecimal) {
		this.pidDecimal = pidDecimal;
	}
	*/
	@XmlElement(name="xpass")
	public Boolean getXpass() {
		return xpass;
	}

	public void setXpass(Boolean xpass) {
		this.xpass = xpass;
	}
	
	@XmlElement(name="reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@XmlElements({
	@XmlElement(name="xbrcReferenceNo"),
	@XmlElement(name="xbrcreferenceno")})
	public List<String> getXbrcReferenceNumber() {
			return xbrcreferencenumber;
	}

	public void setXbrcReferenceNumber(List<String> xbrcReferenceNumber) {
		this.xbrcreferencenumber = xbrcReferenceNumber;
	}
	/*
	@XmlElement(name="xbrcreferenceno")
	public String getXbrcReferenceNumber_lc() {
		return xbrcReferenceNumber;
	}
	
	public void setXbrcReferenceNumber_lc(String xbrcReferenceNumber) {
		this.xbrcreferencenumber = xbrcReferenceNumber;
	}
	*/
	@XmlElement(name="sequence")
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@XmlElement(name="enterrorcode")
	public Integer getEntErrorCode() {
		return entErrorCode;
	}

	public void setEntErrorCode(Integer entErrorCode) {
		this.entErrorCode = entErrorCode;
	}
	
	@XmlElement(name="enterrordescription")
	public String getEntErrorDescription() {
		return entErrorDescription;
	}

	public void setEntErrorDescription(String entErrorDescription) {
		this.entErrorDescription = entErrorDescription;
	}
}
