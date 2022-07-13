package com.disney.xband.xbrc.lib.entity;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Images
{
	private Collection<Image> images;

	@XmlElement(name="image")
	public Collection<Image> getImages()
	{
		return images;
	}

	public void setImages(Collection<Image> images)
	{
		this.images = images;
	}
}