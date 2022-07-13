package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class EventAggregate
{
	private String sID;
	private String sPidDecimal;
	private ReaderInfo reader;
	private Date dtTimestamp;

	public EventAggregate(String sID, String sPidDecimal, String sReader, Date Timestamp)
	{			
		this.sID = sID;
		this.sPidDecimal = sPidDecimal;
		dtTimestamp = Timestamp;
		
		// lookup the reader
		this.reader = XBRCController.getInstance().getReader(sReader);
	}

	public String getID()
	{
		return sID;
	}

	public String getPidDecimal()
	{
		return sPidDecimal;
	}
	
	public Date getTimestamp()
	{
		return dtTimestamp;
	}
	
	public ReaderInfo getReaderInfo()
	{
		return this.reader;
	}
}
