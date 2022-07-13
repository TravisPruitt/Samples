package com.disney.xband.xbrc.lib.model;

import java.util.Comparator;
import java.util.Date;

import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;
import com.disney.xband.lib.xbrapi.LrrEvent;

public class LocationStats implements Comparator<EventAggregate>
{
	private String sLRID;
	private String sPidDecimal;
	private long nPacketSequence;
	private ReaderInfo readerInfo;
	private Date dtTimestamp;
	private long cCount;
	private long sumSS;
	private long nMaxSS;		
	private Xband xb;
	private Guest guest;
	
	public LocationStats(String sLRID, long nPacketSequence, ReaderInfo readerInfo, Date dtTimestamp, Xband xb, Guest guest)
	{
		this.sLRID = sLRID;
		this.sPidDecimal = LrrEvent.publicIdFromLRID(sLRID);
		this.nPacketSequence = nPacketSequence;
		this.readerInfo = readerInfo;
		this.dtTimestamp = dtTimestamp;
		this.cCount = this.sumSS = 0L;
		this.nMaxSS = -1;
		this.xb = xb;
		this.guest = guest;
	}
	
	public String getID()
	{
		return sLRID;
	}
	
	public String getPidDecimal()
	{
		return sPidDecimal;
	}
	
	public long getPacketSequence()
	{
		return nPacketSequence;
	}

	public ReaderInfo getReaderInfo()
	{
		return readerInfo;
	}
	
	public LocationInfo getLocationInfo()
	{
		return readerInfo.getLocation();
	}
	
	public Date getTimestamp()
	{
		return dtTimestamp;
	}

	public long getCount()
	{
		return cCount;
	}

	public void setCount(long cCount)
	{
		this.cCount = cCount;
	}

	public long getSumSS()
	{
		return sumSS;
	}

	public void setSumSS(long sumSS)
	{
		this.sumSS = sumSS;
	}

	public long getMaxSS()
	{
		return nMaxSS;
	}

	public void setMaxSS(long nMaxSS)
	{
		this.nMaxSS = nMaxSS;
	}
	
	public double getMeanSS()
	{
		return (double) sumSS/cCount;
	}
	
	public Xband getXband()
	{
		return xb;
	}
	
	public Guest getGuest() {
		return guest;
	}
	
	@Override
	public int compare(EventAggregate arg0, EventAggregate arg1)
	{
		return arg0.getTimestamp().compareTo(arg1.getTimestamp());
	}
}
