package com.disney.xband.jms.lib.entity.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"href"})
public class Link 
{
	private String href;
	
	@XmlElement(name="href")
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
