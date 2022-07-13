package com.disney.xband.xbrc.lib.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StoredConfiguration
{
	private long id = -1L;	// -1 indicates no id, since 0 is a valid id
	private String name;
	private String description;
	private String model;
	private String xml;
	private boolean internal;
	
	public long getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public String getDescription()
	{
		return description;
	}
	public String getModel()
	{
		return model;
	}
	public String getXml()
	{
		return xml;
	}
	public boolean isInternal()
	{
		return internal;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public void setModel(String model)
	{
		this.model = model;
	}
	public void setXml(String xml)
	{
		this.xml = xml;
	}
	public void setInternal(boolean internal)
	{
		this.internal = internal;
	}
}
