package com.disney.xband.xbrc.parkentrymodel;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.disney.xband.xbrc.lib.entity.OmniServer;

public class OmniServerItem
{
	private OmniServer omniServer;
	private Integer priority;
	private AtomicInteger clientCount = new AtomicInteger(0);
	private Date lastConnectFailure = null;
	
	public OmniServerItem(OmniServer omniServer, Integer priority)
	{
		super();
		this.omniServer = omniServer;
		this.priority = priority;
	}
	
	public String getKey()
	{
		return omniServer.getHostname() + ":" + omniServer.getPort();
	}

	public int getClientCount()
	{
		return clientCount.get();
	}

	public int addClient()
	{
		return clientCount.addAndGet(1);
	}
	
	public int removeClient()
	{
		return clientCount.decrementAndGet();
	}

	public Date getLastConnectFailure()
	{
		return lastConnectFailure;
	}

	public void setLastConnectFailure(Date lastConnectFailure)
	{
		this.lastConnectFailure = lastConnectFailure;
	}

	public Integer getPriority()
	{
		return priority;
	}

	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}
	
	/*
	 * Sort by priority in ascending order.
	 */
	public static class OmniServerComparator implements Comparator<OmniServerItem>
	{
		public int compare(OmniServerItem k1, OmniServerItem k2)
		{		
			if (k1.getPriority() == k2.getPriority())
				return 0;
			if (k1.getPriority() > k2.getPriority())
				return 1;
			return -1;
		}
	}

	public OmniServer getOmniServer()
	{
		return omniServer;
	}
}
