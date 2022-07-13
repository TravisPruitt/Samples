package com.disney.xband.xbrms.server.messaging;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class Subscriber implements Callable<FutureResult<?>>
{
	/*
	 *  If a task doesn't complete within this time limit, it will be removed.
	 *  Implementing classes should set that appropriately.
	 */
	protected long resultTimeout_msec = 10;
	
	private Set<Future<FutureResult<?>>> results = null;
	
	protected Subscriber(PublishEventType type) throws IllegalArgumentException
	{
		if (type == null)
			throw new IllegalArgumentException(
					"Subscriber of type " + this.getClass().getName() +
					" can not be instantiated without an explicit event type.");
		
		if (!isTypeSupported(type))
			throw new IllegalArgumentException(
					"Subscriber of type " + this.getClass().getName() +
					" is not able to handle event type: " + type.getJmsMessageType());
		
		results = new HashSet<Future<FutureResult<?>>>();
	}
	
	@Override
	public abstract FutureResult<?> call() throws Exception;
	
	public abstract boolean isTypeSupported(PublishEventType messageType);
	
	public boolean addResult(Future<FutureResult<?>> event)
	{
		if (event == null)
			return false;
		
		synchronized(results)
		{
			return results.add(event);
		}
	}

	public boolean removeResult(Future<FutureResult<?>> event)
	{
		if (event == null)
			return false;
		
		synchronized(results)
		{
			return results.remove(event);
		}
	}
}
