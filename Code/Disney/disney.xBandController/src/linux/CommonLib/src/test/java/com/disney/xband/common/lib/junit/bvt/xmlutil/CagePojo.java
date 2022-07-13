package com.disney.xband.common.lib.junit.bvt.xmlutil;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cage")
public class CagePojo {
	private Collection<AnimalPojo> animals;
	
	@XmlElement(name="animal")
	public Collection<AnimalPojo> getAnimals(){
		return animals;
	}

	public void setAnimals(Collection<AnimalPojo> animals){
		this.animals = animals;
	}
}
