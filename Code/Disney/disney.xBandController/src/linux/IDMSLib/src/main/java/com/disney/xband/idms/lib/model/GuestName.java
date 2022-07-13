package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestName 
{
	
	private String middleName;
	private String lastName;
	private String firstName;
	private String title;
	private String suffix;
	
	@XmlElement(name="middleName")
	public String getMiddleName() {
		return middleName;
	}
	
	@XmlElement(name="middleName")
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@XmlElement(name="lastName")
	public String getLastName() {
		return lastName;
	}
	
	@XmlElement(name="lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@XmlElement(name="firstName")
	public String getFirstName() {
		return firstName;
	}
	
	@XmlElement(name="firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name="title")
	public String getTitle() {
		return title;
	}

	@XmlElement(name="title")
	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(name="suffix")
	public String getSuffix() {
		return suffix;
	}

	@XmlElement(name="suffix")
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	
	

}
