package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.BioScanErrorEvent;

public class BioScanErrorEventAggregate extends EventAggregate {

	private BioScanErrorEvent bioScanErrorEvent;
	
	public BioScanErrorEventAggregate(String sID, String sReader, Date Timestamp, BioScanErrorEvent bioScanErrorEvent) {
		super(sID, "", sReader, Timestamp);
		this.bioScanErrorEvent = bioScanErrorEvent;
	}

	public BioScanErrorEvent getBioScanErrorEvent() {
		return bioScanErrorEvent;
	}

	public void setBioScanErrorEvent(BioScanErrorEvent bioScanErrorEvent) {
		this.bioScanErrorEvent = bioScanErrorEvent;
	}
}
