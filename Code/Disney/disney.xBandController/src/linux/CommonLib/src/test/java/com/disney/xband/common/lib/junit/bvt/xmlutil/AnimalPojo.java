package com.disney.xband.common.lib.junit.bvt.xmlutil;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.disney.xband.common.lib.XmlDoubleAdapter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AnimalPojo {
	private String name;
	protected int age;
	private Double bodyTemperature;
	
	private Date transientField;
	
	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	/**
	 * @return the age
	 */
	public abstract int getAge();
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}
	
	@JsonIgnore
	@XmlTransient
	public Date getTransientField() {
		return transientField;
	}
	public void setTransientField(Date transientField) {
		this.transientField = transientField;
	}
	
	@XmlJavaTypeAdapter(XmlDoubleAdapter.class)
	public Double getBodyTemperature() {
		return bodyTemperature;
	}
	public void setBodyTemperature(Double bodyTemperature) {
		this.bodyTemperature = bodyTemperature;
	}

}
