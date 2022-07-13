package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.BioEvent;

public class BioEventAggregate extends EventAggregate {

	private BioEvent bioEvent;
	
	public BioEventAggregate(String sID, String sReader, Date Timestamp, BioEvent bioEvent) {
		super(sID, "", sReader, Timestamp);
		this.bioEvent = bioEvent;
	}

	public BioEvent getBioEvent() {
		return bioEvent;
	}

	public void setBioEvent(BioEvent bioEvent) {
		this.bioEvent = bioEvent;
	}
}
