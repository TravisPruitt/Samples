package com.disney.xband.lib.xbrapi;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="transmitCommands")
public class XbrBandCommands
{
	private List<XbrBandCommand> liCommands;

	@XmlElement(name="transmitCommand")
	public List<XbrBandCommand> getListCommands()
	{
		return liCommands;
	}
	
	public void setListCommands(List<XbrBandCommand> liCommands)
	{
		this.liCommands = liCommands;
	}
	
}
