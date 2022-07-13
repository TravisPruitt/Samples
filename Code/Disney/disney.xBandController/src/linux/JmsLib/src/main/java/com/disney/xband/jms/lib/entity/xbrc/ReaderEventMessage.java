package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;

public class ReaderEventMessage extends EventMessage {

	private static final long serialVersionUID = 1L;
		
	private String readerLocationId;
	
	private String readerName;
	
	private String readerId;
	
	private Boolean isWearingPrimaryBand;
	
	
	private int confidence;

	@XmlElement(name="readerlocationid")
	public String getReaderLocationId()
	{
		return this.readerLocationId;
	}
	
	public void setReaderLocationId(String readerLocationId)
	{
		this.readerLocationId = readerLocationId;
	}

	@XmlElement(name="readername")
	public String getReaderName()
	{
		return this.readerName;
	}
	
	public void setReaderName(String readerName)
	{
		this.readerName = readerName;
	}
	
	@XmlElement(name="readerid")
	public String getReaderId()
	{
		return this.readerId;
	}
	
	public void setReaderId(String readerId)
	{
		this.readerId = readerId;
	}

	@XmlElement(name="iswearingprimaryband")
	public Boolean getIsWearingPrimaryBand()
	{
		return this.isWearingPrimaryBand;
	}
	
	public void setIsWearingPrimaryBand(Boolean isWearingPrimaryBand)
	{
		this.isWearingPrimaryBand = isWearingPrimaryBand;
	}
	
	@XmlElement(name="confidence")
	public int getConfidence()
	{
		return this.confidence;
	}
	
	public void setConfidence(int confidence)
	{
		this.confidence = confidence;
	}
}
