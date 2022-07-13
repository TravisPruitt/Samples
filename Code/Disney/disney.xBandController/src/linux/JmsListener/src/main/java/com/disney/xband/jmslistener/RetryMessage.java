package com.disney.xband.jmslistener;

import javax.jms.TextMessage;

public class RetryMessage
{
	private TextMessage message;
	private int attempts;
	private long lastRetry;
	
	public RetryMessage(TextMessage message)
	{
		this.message = message;
		this.attempts = 0;
		this.lastRetry = System.currentTimeMillis();
	}
	
	public TextMessage getMessage()
	{
		return this.message;
	}
	
	public void incrementAttempts()
	{
		this.attempts++;
		this.lastRetry = System.currentTimeMillis();
	}
	
	public int getAttempts()
	{
		return this.attempts;
	}

	public long getLastRetry()
	{
		return this.lastRetry;
	}
}
