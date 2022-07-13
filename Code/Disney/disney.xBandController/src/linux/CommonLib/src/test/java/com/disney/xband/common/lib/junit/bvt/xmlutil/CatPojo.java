package com.disney.xband.common.lib.junit.bvt.xmlutil;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cat")
public class CatPojo extends AnimalPojo {
	private Boolean whiskers;

	@XmlElement(name="catAge")
	public int getAge() {
		return age;
	}
	
	public Boolean isWhiskers() {
		return whiskers;
	}

	public void setWhiskers(Boolean whiskers) {
		this.whiskers = whiskers;
	}
}
