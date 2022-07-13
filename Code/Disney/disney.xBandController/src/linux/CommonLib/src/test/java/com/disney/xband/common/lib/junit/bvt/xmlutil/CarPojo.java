package com.disney.xband.common.lib.junit.bvt.xmlutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name="car")
@XmlType(propOrder={"model", "year", "color", "registration", "someSimpleType", "someObject", "tires"})
public class CarPojo {
	private String color;
	private String model;
	private int year;
	private Date registration;
	private Collection<TirePojo> tires;
	private String attribute;
	
	// This field should not get serialized...
	private Date transientField;

	private long someSimpleType;
	private String someObject;
	
	@SuppressWarnings("unused")
	private transient static Logger logger = Logger.getLogger(CarPojo.class);
	
	public CarPojo(){
		tires = new ArrayList<TirePojo>(4);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@XmlElementWrapper
	@XmlElement(name="tire")
	public Collection<TirePojo> getTires() {
		return tires;
	}

	public void setTires(Collection<TirePojo> tires) {
		this.tires = tires;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the someSimpleType
	 */
	public long getSomeSimpleType() {
		return someSimpleType;
	}

	/**
	 * @param someSimpleType the someSimpleType to set
	 */
	public void setSomeSimpleType(long someSimpleType) {
		this.someSimpleType = someSimpleType;
	}

	/**
	 * @return the someObject
	 */
	public String getSomeObject() {
		return someObject;
	}

	/**
	 * @param someObject the someObject to set
	 */
	public void setSomeObject(String someObject) {
		this.someObject = someObject;
	}

	public Date getRegistration() {
		return registration;
	}

	public void setRegistration(Date registration) {
		this.registration = registration;
	}
	@XmlAttribute
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@JsonIgnore
	@XmlTransient
	public Date getTransientField() {
		return transientField;
	}

	public void setTransientField(Date transientField) {
		this.transientField = transientField;
	}
}
