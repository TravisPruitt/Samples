package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.EventMessage;

@XmlRootElement(name="message")
public class PEGuestMessage  extends EventMessage
{
	private static final long serialVersionUID = 1L;
	
	// The xbrcGuestId should only be used by the xBRC for HA synchronization purposes.
	private String xbrcReferenceNo;
	private String reason;
	private String readerName;
	private Integer readerDeviceId;
	private String lastxmit;
	private Integer entErrorCode;
	private String entErrorDescription;
	
    @XmlElement(name="reason")
	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	@XmlElement(name="readername")
	public String getReaderName()
	{
		return readerName;
	}

	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}

	@XmlElement(name="readerdeviceid")
	public Integer getReaderDeviceId()
	{
		return readerDeviceId;
	}

	public void setReaderDeviceId(Integer readerDeviceId)
	{
		this.readerDeviceId = readerDeviceId;
	}

	@XmlElement(name="xbrcreferenceno")
	public String getXbrcReferenceNo()
	{
		return xbrcReferenceNo;
	}

	public void setXbrcReferenceNo(String xbrcReferenceNo)
	{
		this.xbrcReferenceNo = xbrcReferenceNo;
	}
	
	@XmlElement(name="lastxmit")
	public String getLastxmit() {
		return lastxmit;
	}

	public void setLastxmit(String lastxmit) {
		this.lastxmit = lastxmit;
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
