package com.disney.xband.xbrc.lib.model;

import java.util.Date;

import com.disney.xband.lib.xbrapi.BioImageEvent;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

public class BioImageEventAggregate extends EventAggregate {

	private BioImageEvent bioEvent;
	
	public BioImageEventAggregate(String sID, String sReader, Date Timestamp, BioImageEvent bioEvent) {
		super(sID, "", sReader, Timestamp);
		this.bioEvent = bioEvent;
	}

	public BioImageEvent getBioEvent() {
		return bioEvent;
	}

	public void setBioEvent(BioImageEvent bioEvent) {
		this.bioEvent = bioEvent;
	}
}
