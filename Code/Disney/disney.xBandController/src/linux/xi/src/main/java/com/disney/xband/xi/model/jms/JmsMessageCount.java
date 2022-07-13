package com.disney.xband.xi.model.jms;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="jmsmessagecount")
public class JmsMessageCount
{
	private long messageCount;

	public long getMessageCount()
	{
		return messageCount;
	}

	public void setMessageCount(long messageCount)
	{
		this.messageCount = messageCount;
	}
}
