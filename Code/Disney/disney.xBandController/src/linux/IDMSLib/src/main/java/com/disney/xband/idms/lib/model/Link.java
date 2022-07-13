package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Link
{
	
	private String href;
	private String name;
	
	@XmlElement(name="name")
	public String getName()
	{
		return this.name;
	}
	
	@XmlElement(name="name")
	public void setName(String name)
	{
		this.name = name;
	}
	
	

	@XmlElement(name="href")
	public String getHref() {
		return href;
	}

	@XmlElement(name="href")
	public void setHref(String href) {
		this.href = href;
	}

}
