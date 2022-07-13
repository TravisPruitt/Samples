package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.disney.xband.jms.lib.entity.xbrc.XbrcMessage;

public class TappedMessage extends XbrcMessage {

	private static final long serialVersionUID = 1L;
	
	private String pidDecimal;
	
	private List<String> readerLocation;
	
	private List<String> locationId;
	
	private boolean xpass;
	
	private String reason;
	

	private List<String> readerDeviceId;
	

	private List<String> readerName;
	

	private List<String> xbrcReferenceNo;
	
	private String sequence;
	
	@XmlElements({
		@XmlElement(name="readerdeviceid"),
		@XmlElement(name="readerDeviceId")})
	public List<String> getReaderDeviceId() {
		return readerDeviceId;
	}

	public void setReaderDeviceId(List<String> readerDeviceId) {
		this.readerDeviceId = readerDeviceId;
	}
	/*
	@XmlElement(name="readerdeviceid")
	public String getReaderDeviceId_lc() {
		return readerDeviceId;
	}

	public void setReaderDeviceId_lc(String readerDeviceId) {
		this.readerDeviceId = readerDeviceId;
	}
	*/
	
	@XmlElements({
	@XmlElement(name="readername"),
	@XmlElement(name="readerName")})
	public List<String> getReaderName() {
		return readerName;
	}

	public void setReaderName(List<String> readerName) {
		this.readerName = readerName;
	}
	

	@XmlElement(name="publicid")
	public String getPidDecimal() {
		return pidDecimal;
	}

	public void setPidDecimal(String pidDecimal) {
		this.pidDecimal = pidDecimal;
	}
	
	@XmlElements({
	@XmlElement(name="readerlocation"),
	@XmlElement(name="readerLocation")})
	public List<String> getReaderLocation() {
		return readerLocation;
	}

	public void setReaderLocation(List<String> readerLocation) {
		this.readerLocation = readerLocation;
	}
	
	@XmlElements({
	@XmlElement(name="readersection"),
	@XmlElement(name="readerSection"),
	@XmlElement(name="locationid")})
	public List<String> getLocationId() {
		return locationId;
	}

	public void setLocationId(List<String> locationId) {
		this.locationId = locationId;
	}
	
	@XmlElement(name="xpass")
	public boolean getXpass() {
		return xpass;
	}

	public void setXpass(boolean xpass) {
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
	@XmlElement(name="xbrcreferenceno"),
	@XmlElement(name="xbrcReferenceNo")})
	public List<String> getXbrcReferenceNo() {
		return xbrcReferenceNo;
	}


	public void setXbrcReferenceNo(List<String> xbrcReferenceNo) {
		this.xbrcReferenceNo = xbrcReferenceNo;
	}
	
	@XmlElement(name="sequence")
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	
}
