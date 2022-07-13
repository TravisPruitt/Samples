package com.disney.xband.xbrc.lib.idms;

import java.util.Date;

/*
 * A generic cached item
 */
public class CacheItem<T>
{
	private Date whenCached;
	private T item;
	
	public Date getWhenCached()
	{
		return whenCached;
	}
	
	public void setWhenCached(Date whenCached)
	{
		this.whenCached = whenCached;
	}
	
	public T getItem()
	{
		return item;
	}
	
	public void setItem(T item)
	{
		this.item = item;
	}
	
	public boolean isTimedOut(int secTimeout)
	{
		int secDuration = (int) (new Date().getTime() - whenCached.getTime()) / 1000;
		return secDuration > secTimeout;
	}
	
}
