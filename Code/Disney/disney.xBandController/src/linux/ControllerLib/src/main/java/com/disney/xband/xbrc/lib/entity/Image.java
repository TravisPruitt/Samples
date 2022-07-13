package com.disney.xband.xbrc.lib.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="image")
public class Image
{
	private Long id;
	private String filename;
	private String title;
	private String description;
	private Integer width;
	private Integer height;
	private ImageBlob blob;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getWidth()
	{
		return width;
	}

	public void setWidth(Integer width)
	{
		this.width = width;
	}

	public Integer getHeight()
	{
		return height;
	}

	public void setHeight(Integer height)
	{
		this.height = height;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public ImageBlob getBlob()
	{
		return blob;
	}

	public void setBlob(ImageBlob blob)
	{
		this.blob = blob;
	}
}