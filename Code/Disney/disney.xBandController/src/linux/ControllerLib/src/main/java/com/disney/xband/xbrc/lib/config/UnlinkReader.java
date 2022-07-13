package com.disney.xband.xbrc.lib.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnlinkReader extends UpdateConfigItem {
	private Long readerId;
	
	public void setReaderId(Long readerId)
	{
		this.readerId = readerId;
	}
	
	@XmlElement
	public Long getReaderId()
	{
		return readerId;
	}

}
