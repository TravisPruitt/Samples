package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.XfpDiagEvent;

public class XfpDiagnosticsEventAggregate extends EventAggregate {

	private XfpDiagEvent event;
	
	public XfpDiagnosticsEventAggregate(String sID, String sReader, Date Timestamp, XfpDiagEvent event) {
		super(sID, "", sReader, Timestamp);
		this.event = event;
	}

	public XfpDiagEvent getEvent() {
		return event;
	}

	public void setEvent(XfpDiagEvent event) {
		this.event = event;
	}
}
