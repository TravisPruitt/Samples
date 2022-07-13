package com.disney.xband.xbrc.Controller.model;

import java.util.Collection;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.xbrc.Controller.ConfigOptions;

@XmlRootElement(name = "updatestream")
public class UpdateStreamInfo
{
	private String url = ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamURL();
	private long after = -1;
	private int interval = ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamIntervalMs();
	private int max = -1;
	private int batchsize = ConfigOptions.INSTANCE.getControllerInfo().getUpdateStreamBatchSize();
	private String preferredGuestIdType = ConfigOptions.INSTANCE.getControllerInfo().getPreferredGuestIdType();
	private Collection<String> messageTypes = null;
	
	public UpdateStreamInfo()
	{
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public long getAfter()
	{
		return after;
	}

	public void setAfter(long after)
	{
		this.after = after;
	}

	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		this.interval = interval;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
	}
	
	public int getBatchsize() 
	{
		return batchsize;
	}

	public void setBatchsize(int batchsize) 
	{
		this.batchsize = batchsize;
	}
	
	public String getPreferredGuestIdType()
	{
		return preferredGuestIdType;
	}
	
	public void setPreferredGuestIdType(String preferredIdType)
	{
		this.preferredGuestIdType = preferredIdType;
	}
	
	@XmlList
	public Collection<String> getMessageTypes()
	{
		return messageTypes;
	}

	public void setMessageTypes(Collection<String> messageTypes)
	{
		this.messageTypes = messageTypes;
	}
}
