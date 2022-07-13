package com.disney.xband.gxp;

public class UIEvent
{
	private static long lNextId = 0;
	
	private long lId = 0;
	private TapRequest treq;
	private TapResponse tres;
	
	public UIEvent()
	{
	}

	public UIEvent(TapRequest treq, TapResponse tres)
	{
		this.treq = treq;
		this.tres = tres;
		synchronized(this)
		{
			lId = lNextId++;
		}
	}
	
	public static void setlNextId(long lNextId)
	{
		UIEvent.lNextId = lNextId;
	}

	public void setlId(long lId)
	{
		this.lId = lId;
	}

	public void setTreq(TapRequest treq)
	{
		this.treq = treq;
	}

	public void setTres(TapResponse tres)
	{
		this.tres = tres;
	}

	public long getId()
	{
		return lId;
	}
	
	public TapRequest getTreq()
	{
		return treq;
	}

	public TapResponse getTres()
	{
		return tres;
	}
	
}
