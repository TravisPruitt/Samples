package com.disney.xband.xbrc.lib.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name="storedXbrcConfig")
public class XbrcConfiguration
{
	private Long id;
	private String name;
	private String description;
	private String model;
	private String xml;
	private String venueCode;
	private String venueName;
	private Date createTime;

	public XbrcConfiguration()
	{		
	}
	
	public XbrcConfiguration(Long id, String name, String description,
			String model, String xml, String venueCode, String venueName,
			Date createTime)
	{
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.model = model;
		this.xml = xml;
		this.venueCode = venueCode;
		this.venueName = venueName;
		this.createTime = createTime;
	}
	
    @XmlAttribute
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

    @XmlAttribute
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

    @XmlElement
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

    @XmlElement
	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

    @XmlElement
	public String getXml()
	{
		return xml;
	}

	public void setXml(String xml)
	{
		this.xml = xml;
	}

    @XmlElement
	public String getVenueCode()
	{
		return venueCode;
	}

	public void setVenueCode(String venueCode)
	{
		this.venueCode = venueCode;
	}

    @XmlElement
	public String getVenueName()
	{
		return venueName;
	}

	public void setVenueName(String venueName)
	{
		this.venueName = venueName;
	}

    @XmlElement
	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}	
}
