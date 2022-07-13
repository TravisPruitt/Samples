package com.disney.xband.xbrms.server.messaging.publisher;

import java.util.Set;

import com.disney.xband.xbrms.server.messaging.PublishEventType;
import com.disney.xband.xbrms.server.messaging.Publisher;
import com.disney.xband.xbrms.server.messaging.Subscriber;

/**
 * Use this publisher to refresh xbrms status and re-connect to jms and db, when appropriate.
 * 
 * Usage: XbrmsStatusRefreshPublisher.getInstance().notifySubscribers()
 */
public class XbrmsStatusRefreshPublisher extends Publisher
{	
	public static final PublishEventType PUBLISH_EVENT_TYPE = PublishEventType.XBRMS_CONFIGURATION_REFRESH;
	
	public static XbrmsStatusRefreshPublisher getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public void register(Subscriber subscriber) throws IllegalArgumentException 
	{
		super.register(PUBLISH_EVENT_TYPE, subscriber);
	}
	
	public synchronized void notifySubscribers()
	{
		notifySubscribersOf(PUBLISH_EVENT_TYPE);
	}
	
	public Set<Subscriber> getSubscribers()
	{
		return getSubscribers(PUBLISH_EVENT_TYPE);
	}

	private XbrmsStatusRefreshPublisher(){
		super();
	}
	
	private static class SingletonHolder
	{
		private static final XbrmsStatusRefreshPublisher INSTANCE = new XbrmsStatusRefreshPublisher();
	}
}
