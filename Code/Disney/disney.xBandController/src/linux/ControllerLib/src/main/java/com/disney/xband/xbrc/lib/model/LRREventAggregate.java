package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public class LRREventAggregate extends EventAggregate
{
	
	private long nPacketSequence;
	private double fAvgStrength;
	private long nMaxStrength;
	private long cCount;
	private Xband xb;
	private Guest guest;

	public LRREventAggregate(String BandId, String PidDecimal, long PacketSequence, String sReaderName,
			Date Timestamp, double Average, long Max, long Count, Xband xb, Guest guest)
	{
		super(BandId, PidDecimal, sReaderName, Timestamp);
		this.nPacketSequence = PacketSequence;
		this.fAvgStrength = Average;
		this.nMaxStrength = Max;
		this.cCount = Count;
		this.xb = xb;
		this.guest = guest;
	}

	public long getPacketSequence()
	{
		return nPacketSequence;
	}

	public double getAverageStrength()
	{
		return fAvgStrength;
	}

	public long getMaxStrength()
	{
		return nMaxStrength;
	}
	
	public long getCount()
	{
		return cCount;
	}

	// Normalizes the max signal strength of this aggregate
	// using a normalized curve.
	public int getConfidence()
    {
    	int confidence = 0;

    	// See TLP about how this formula has been calculated.
    	// This normalizes against a reasonable curve, but may
    	// need to be modified or made configurable.
    	confidence = (int)((-9399/(double)nMaxStrength) - 104.4);
    	
    	// Ensure we limit to mins and maxs.
    	confidence = Math.min(100, confidence);
    	confidence = Math.max(0, confidence);
    	
    	return confidence;
    }
	
	public Xband getXband()
	{
		return xb;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

}
