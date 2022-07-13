package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;



@XmlRootElement
public class GuestIdentifierPut{

	private String type;
	private String value;
	
	public GuestIdentifierPut()
	{
		
	}
	
	@JsonProperty("identifier-type")
	@XmlElement(name="identifier-type")
	public String getType() {
		return type;
	}
	
	@JsonProperty("identifier-type")
	@XmlElement(name="identifier-type")
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty("identifier-value")
	@XmlElement(name="identifier-value")
	public String getValue() {
		return value;
	}
	
	@JsonProperty("identifier-value")
	@XmlElement(name="identifier-value")
	public void setValue(String value) {
		this.value = value;
	}
}
