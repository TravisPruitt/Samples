package com.disney.xband.xbrc.lib.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="heartbeat")
public class XbrcHeartbeat
{
	private Integer mainThreadLoopCount;
	private Integer lastProcessingDuration;

	public Integer getMainThreadLoopCount()
	{
		return mainThreadLoopCount;
	}

	public void setMainThreadLoopCount(Integer mainThreadLoopCount)
	{
		this.mainThreadLoopCount = mainThreadLoopCount;
	}

	public Integer getLastProcessingDuration()
	{
		return lastProcessingDuration;
	}

	public void setLastProcessingDuration(Integer lastProcessingDuration)
	{
		this.lastProcessingDuration = lastProcessingDuration;
	}
}
