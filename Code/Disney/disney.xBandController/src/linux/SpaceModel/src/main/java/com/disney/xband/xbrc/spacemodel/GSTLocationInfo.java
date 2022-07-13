package com.disney.xband.xbrc.spacemodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="location")
public class GSTLocationInfo
{
	@XmlElement
	public String name;

	@XmlElement
	public int id;
	
	@XmlElement
	public String arrived;
	
	@XmlElement
	public String latest;
	
	public GSTLocationInfo(){}
	
	

}
