package com.disney.xband.xbrc.lib.idms;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/*
 * Implements a cache of T type indexed by key k
 */
public class Cache<K, T>
{
	public final int secPositiveCacheTimeoutDefault = 1800;
	public final int secNegativeCacheTimeoutDefault = 300;
	
	private Map<K, CacheItem<T>> positiveCache = new Hashtable<K, CacheItem<T>>();
	private Map<K, CacheItem<T>> negativeCache = new Hashtable<K, CacheItem<T>>();
	
	private int secPositiveCacheTimeout = secPositiveCacheTimeoutDefault;
	private int secNegativeCacheTimeout = secNegativeCacheTimeoutDefault;
	
	public void setCacheTimeouts(int secPositiveTimeout, int secNegativeTimeout)
	{
		secPositiveCacheTimeout = secPositiveTimeout;
		secNegativeCacheTimeout = secNegativeTimeout;
	}
	
	public void clear()
	{
		positiveCache.clear();
		negativeCache.clear();
	}
	
	public void clear(K k)
	{
		positiveCache.remove(k);
		negativeCache.remove(k);
	}
	
	public boolean isItemInCache(K k)
	{
		return inPositiveCache(k) || inNegativeCache(k);
	}
	
	private boolean inPositiveCache(K k)
	{
		CacheItem<T> ci = positiveCache.get(k);
		if (ci == null)
			return false;
		
		// if it's in the positive cache, make sure it hasn't timed out
		if (!ci.isTimedOut(secPositiveCacheTimeout))
			return true;
		
		// timed out - remove it
		positiveCache.remove(k);
		return false;
	}
	
	private boolean inNegativeCache(K k)
	{
		CacheItem<T> ci = negativeCache.get(k);		
		if (ci == null)
			return false;
		
		// if it's in the positive cache, make sure it hasn't timed out
		if (!ci.isTimedOut(secNegativeCacheTimeout))
			return true;
		
		// timed out - remove it
		negativeCache.remove(k);
		return false;
	}
	
	public void add(K k, T t)
	{
		// if it's in the negative cache, remove it
		negativeCache.remove(k);
		
		// create cache item first
		CacheItem<T> ci = new CacheItem<T>();
		ci.setWhenCached(new Date());
		ci.setItem(t);
		positiveCache.put(k, ci);
	}
	
	public T get(K k)
	{
		CacheItem<T> item = positiveCache.get(k);
		if (item != null)
			return item.getItem();
		
		item = negativeCache.get(k);
		if (item != null)
			return item.getItem();
		
		return null;
	}
	
	public void markAsBad(K k)
	{
		// if it's in the positive cache, remove it		
		positiveCache.remove(k);
		
		CacheItem<T> ci = new CacheItem<T>();
		ci.setWhenCached(new Date());
		ci.setItem(null);
		negativeCache.put(k, ci);
	}

}
