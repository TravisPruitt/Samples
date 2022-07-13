package com.disney.xband.xbrc.lib.entity;

import java.util.Date;

public class Event implements Comparable<Event>
{
	private long nEventNumber;
	private String sReader;
	private String sID;
	private Date ts;
	private int nPacketSequence;
	private int nSignalStrength;
	private int nChannel;
	private int nFrequency;
	
	public Event(String sReader, long nEventNumber, Date ts, String sID, int nPacketSequence, int nSignalStrength, int nChannel, int nFrequency)
	{
		this.nEventNumber = nEventNumber;
		this.sReader = sReader;
		this.ts = ts;
		this.sID = sID;
		this.nPacketSequence = nPacketSequence;
		this.nSignalStrength = nSignalStrength;
		this.nChannel = nChannel;
		this.nFrequency = nFrequency;
	}
	
	public long getEventNumber()
	{
		return nEventNumber;
	}
	
	public String getReader()
	{
		return sReader;
	}
	
	public Date getTimeStamp()
	{
		return ts;
	}
	
	public String getID()
	{
		return sID;
	}
	
	public int getPacketSequence()
	{
		return nPacketSequence;
	}
	
	public int getSignalStrength()
	{
		return nSignalStrength;
	}
	
	public int getChannel()
	{
		return nChannel;
	}
	
	public int getFrequency()
	{
		return nFrequency;
	}
	
	@Override
	public int compareTo(Event arg0)
	{
		return ts.compareTo(arg0.ts);
	}
	

}
