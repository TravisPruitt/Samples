package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class BandStatusMessage extends EventMessage {

	private static final long serialVersionUID = 1L;
	
	private String readerName;
	private Integer readerDeviceId;
	
	@XmlElement(name="readername")
	public String getReaderName() {
		return readerName;
	}
	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
	
	@XmlElement(name="readerdeviceid")
	public Integer getReaderDeviceId() {
		return readerDeviceId;
	}
	public void setReaderDeviceId(Integer readerDeviceId) {
		this.readerDeviceId = readerDeviceId;
	}
}
