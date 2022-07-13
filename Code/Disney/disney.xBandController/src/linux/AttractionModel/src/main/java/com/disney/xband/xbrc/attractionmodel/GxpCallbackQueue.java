package com.disney.xband.xbrc.attractionmodel;

import java.util.LinkedList;

public class GxpCallbackQueue
{
	public static GxpCallbackQueue INSTANCE = new GxpCallbackQueue();
	
	private LinkedList<GxpCallback> llm = new LinkedList<GxpCallback>(); 
	
	private GxpCallbackQueue()
	{
	}
	
	public void add(GxpCallback m)
	{
		synchronized(llm)
		{
			llm.add(m);
		}
	}
	
	public GxpCallback getNextMessage()
	{
		synchronized(llm)
		{
			if (llm.isEmpty())
				return null;
			else
				return llm.removeFirst();
		}
	}

}
