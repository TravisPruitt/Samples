package com.disney.xband.xbrc.gxploadtest.entity;

import com.disney.xband.xbrc.gxploadtest.CLOptions;

public class Attraction
{
	private String sEntertainmentId;
	private GxpQueue gxp = null;;
	
	public Attraction(CLOptions clo, String sEntertainmentId, Metrics metrics)
	{
		this.sEntertainmentId = sEntertainmentId;
		this.gxp = new GxpQueue(clo.getGxp(), clo.getThreadCount(), clo.isNumericBandId(), metrics);
	}
	
	public String getEntertainmentId()
	{
		return sEntertainmentId;
	}
	
	public void send(GxpMessage msg) throws Exception
	{
		// confirm facility
		if (!msg.getEntertainmentId().equals(sEntertainmentId))
			throw new Exception("Attraction entertainment id doesn't match request");
		
		gxp.send(msg);
	}
	
	public void stop()
	{
		gxp.stop();
	}
	
	public int getPendingRequests()
	{
		return gxp.getPending();
	}

}
