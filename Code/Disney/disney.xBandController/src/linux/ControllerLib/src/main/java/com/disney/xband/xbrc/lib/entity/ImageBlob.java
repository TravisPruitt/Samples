package com.disney.xband.xbrc.lib.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="imageblob")
public class ImageBlob
{
	private Long imageId;
	private byte[] bytes;
	private Image image;

	public byte[] getBytes()
	{
		return bytes;
	}

	public void setBytes(byte[] bytes)
	{
		this.bytes = bytes;
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
	}

	public Long getImageId()
	{
		return imageId;
	}

	public void setImageId(Long imageId)
	{
		this.imageId = imageId;
	}
}
