package com.disney.xband.idms.lib.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XBandCollection
{

	private int startItem;
	private int itemLimit;
	private int itemCount;
	private List<XBand> xbands;

	public XBandCollection()
	{
		xbands = new ArrayList<XBand>();
	}

	@XmlElement(name = "startItem")
	public int getStartItem()
	{
		return startItem;
	}

	@XmlElement(name = "startItem")
	public void setStartItem(int startItem)
	{
		this.startItem = startItem;
	}

	@XmlElement(name = "itemLimit")
	public int getItemLimit()
	{
		return itemLimit;
	}

	@XmlElement(name = "itemLimit")
	public void setItemLimit(int itemLimit)
	{
		this.itemLimit = itemLimit;
	}

	@XmlElement(name = "itemCount")
	public int getItemCount()
	{
		return itemCount;
	}

	@XmlElement(name = "itemCount")
	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
	}

	@XmlElement(name = "xbands")
	public List<XBand> getXbands()
	{
		return xbands;
	}

	@XmlElement(name = "xbands")
	public void setXbands(List<XBand> xbands)
	{
		this.xbands = xbands;
	}

}
