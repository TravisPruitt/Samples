package com.disney.xband.jmslistener.cache;

import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class CacheItem<T>
{
	private T item;
	private long expires;

	public T getItem()
	{
		this.expires = System.currentTimeMillis() + (ConfigurationProperties.INSTANCE.getCacheExpirationMinutes() * 1000 * 60);
		return this.item;
	}
			
	public void setItem(T item)
	{
		this.expires = System.currentTimeMillis() + (ConfigurationProperties.INSTANCE.getCacheExpirationMinutes() * 1000 * 60);
		this.item = item;
	}
	
	public long getExpires()
	{
		return this.expires;
	}
}
