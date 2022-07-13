package com.disney.xband.xbrms.server.messaging;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public abstract class Publisher
{
	private Logger logger = Logger.getLogger(Publisher.class);
	
	/**
	 * Thread pool responsible for servicing observers notification tasks.
	 */
	private ExecutorService executor;
	
	private ConcurrentMap<PublishEventType, Set<Subscriber>> subscribers;
	
	protected void register(PublishEventType eventType, Subscriber subscriber) throws IllegalArgumentException 
	{
		if (eventType == null)
			throw new IllegalArgumentException("Subscribers may only be registered to an explicit event type.");
		
		if (subscriber == null)
			return;
		
		logger.info("Registering subscriber " + subscriber.getClass() + " to be notified when about event type: " + eventType.name());
			
		subscribers.putIfAbsent(eventType, new LinkedHashSet<Subscriber>());
			
		subscribers.get(eventType).add(subscriber);
	}
	
	protected boolean unregister(PublishEventType eventType, Subscriber subscriber)
	{
		if (eventType == null)
			throw new IllegalArgumentException("Subscribers are registered to an explicit event type. To unregister a subscriber en event type must be provided.");
		
		if (subscriber == null)
			return false;
			
		return subscribers.get(eventType).remove(subscriber);
	}
	
	protected void notifySubscribersOf(PublishEventType eventType)
	{
		if (eventType == null)
			return;
		
		if (!subscribers.containsKey(eventType))
		{
			logger.fatal("There are no subscribers registered to handle event: " + PublishEventType.XBRMS_CONFIGURATION_UPDATE.name());
			return;
		}
		
		synchronized(subscribers)
		{
			for (Subscriber s : subscribers.get(eventType))
			{
				// send it off and keep going, don't wait for it to complete
				executor.submit(s);
			}
		}
	}
	
	protected void notifySubscribers()
	{
		synchronized(subscribers)
		{
			for (Set<Subscriber> subs : subscribers.values())
			{
				for (Subscriber s: subs)
				{
					// send it off and keep going, don't wait for it to complete
					executor.submit(s);
				}
			}
		}
	}
	
	public Set<Subscriber> getSubscribers(PublishEventType eventType)
	{
		return subscribers.get(eventType);
	}
	
	protected Publisher()
	{
		/*
		 *  Create a thread pool with dynamic size. Worker threads created by this pool
		 *  have the same permissions as the current thread, as well as the same
		 *  AccessControlContext and contextClassLoader.
		 */
		executor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
		
		/*
		 * Create a map to hold object that need to be notified when something of interest happens.
		 */
		subscribers = new ConcurrentHashMap<PublishEventType, Set<Subscriber>>(PublishEventType.values().length * 2);
	}
}
