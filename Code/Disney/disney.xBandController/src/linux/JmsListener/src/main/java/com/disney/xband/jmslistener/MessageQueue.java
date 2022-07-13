package com.disney.xband.jmslistener;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.TextMessage;

public class MessageQueue 
{
	private ConcurrentLinkedQueue<TextMessage> queue;
	
	public MessageQueue()
	{
		this.queue = new ConcurrentLinkedQueue<TextMessage>();
	}
	
	public boolean isEmpty()
	{
		return this.queue.isEmpty();
	}

	public void add(TextMessage message)
	{
		queue.add(message);
	}
	
	public TextMessage get()
	{
		return queue.remove();
	}

    public int size() {
        return queue.size();
    }
}
