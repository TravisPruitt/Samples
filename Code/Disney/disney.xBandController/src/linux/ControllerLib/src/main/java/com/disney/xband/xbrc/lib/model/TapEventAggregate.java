package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.TapEvent;

public class TapEventAggregate extends EventAggregate
{
	private TapEvent tapEvent;
	
	public TapEventAggregate(String sBandID, String sPidDecimal, String sReaderName, Date dtTimestamp, TapEvent tapEvent)
	{
		super(sBandID, sPidDecimal, sReaderName, dtTimestamp);
		this.tapEvent = tapEvent;		
	}

	public TapEvent getTapEvent() {
		return tapEvent;
	}

	public void setTapEvent(TapEvent tapEvent) {
		this.tapEvent = tapEvent;
	}

}
