package com.disney.xband.xbrms.server.messaging.publisher;

import java.util.Set;

import com.disney.xband.xbrms.server.messaging.PublishEventType;
import com.disney.xband.xbrms.server.messaging.Publisher;
import com.disney.xband.xbrms.server.messaging.Subscriber;

/**
 * Use this publisher to notify the system of changes in xbrms
 * configuration settings.
 * 
 * Usage:
 * 
 * XbrmsConfigurationChangePublisher.getInstance().notifySubscribers()
 */
public class XbrmsConfigurationUpdatePublisher extends Publisher
{	
	public static final PublishEventType PUBLISH_EVENT_TYPE = PublishEventType.XBRMS_CONFIGURATION_UPDATE;
	
	public static XbrmsConfigurationUpdatePublisher getInstance()
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

	private XbrmsConfigurationUpdatePublisher(){
		super();
	}
	
	private static class SingletonHolder
	{
		private static final XbrmsConfigurationUpdatePublisher INSTANCE = new XbrmsConfigurationUpdatePublisher();
	}
}
