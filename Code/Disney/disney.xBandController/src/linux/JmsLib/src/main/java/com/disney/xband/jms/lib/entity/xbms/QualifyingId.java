package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QualifyingId
{
	private String qualifyingId;
	private String qualifyingIdType;
	
	@XmlElement(name="qualifyingId")
	public String getQualifyingId()
	{
		return this.qualifyingId;
	}

	public void setQualifyingId(String qualifyingId)
	{
		this.qualifyingId = qualifyingId;
	}

	@XmlElement(name="qualifyingIdType")
	public String getQualifyingIdType()
	{
		return this.qualifyingIdType;
	}

	public void setQualifyingIdType(String qualifyingIdType)
	{
		this.qualifyingIdType = qualifyingIdType;
	}
}
