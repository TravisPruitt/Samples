package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class IDMSTypeListItem 
{

	private int IDMSTypeId;
	private String IDMSTypeName;
	private String IDMSTypeValue;
	private String IDMSKey;
	
	public IDMSTypeListItem()
	{
		
	}
	
	@JsonProperty("IDMSTypeId")
	@XmlElement(name="IDMSTypeId")
	public int getIDMSTypeId() {
		return IDMSTypeId;
	}
	
	@JsonProperty("IDMSTypeId")
	@XmlElement(name="IDMSTypeId")
	public void setIDMSTypeId(int iDMSTypeId) {
		IDMSTypeId = iDMSTypeId;
	}
	
	@JsonProperty("IDMSTypeName")
	@XmlElement(name="IDMSTypeName")
	public String getIDMSTypeName() {
		return IDMSTypeName;
	}
	
	@JsonProperty("IDMSTypeName")
	@XmlElement(name="IDMSTypeName")
	public void setIDMSTypeName(String iDMSTypeName) {
		IDMSTypeName = iDMSTypeName;
	}
	
	@JsonProperty("IDMSTypeValue")
	@XmlElement(name="IDMSTypeValue")
	public String getIDMSTypeValue() {
		return IDMSTypeValue;
	}
	
	@JsonProperty("IDMSTypeValue")
	@XmlElement(name="IDMSTypeValue")
	public void setIDMSTypeValue(String iDMSTypeValue) {
		IDMSTypeValue = iDMSTypeValue;
	}
	
	@JsonProperty("IDMSKey")
	@XmlElement(name="IDMSKey")
	public String getIDMSKey() {
		return IDMSKey;
	}
	
	@JsonProperty("IDMSKey")
	@XmlElement(name="IDMSKey")
	public void setIDMSKey(String iDMSKey) {
		IDMSKey = iDMSKey;
	}
	
	
}
