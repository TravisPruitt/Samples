package com.disney.xband.xbrc.ui.bean;

import java.text.SimpleDateFormat;

import com.disney.xband.xbrc.lib.entity.GST;
import com.disney.xband.xview.lib.model.Guest;

public class GuestStatusBean {
	
	private static String dateFormat = "d MMM yyyy HH:mm:ss";
	private GST gst;
	private Guest guest;
	private String bandId;
	
	public GuestStatusBean(GST gst) {
		this.gst = gst;
	}

	public GST getGst() {
		return gst;
	}

	public void setGst(GST gst) {
		this.gst = gst;
	}
	
	public String getFormattedTimeLatestAtReader(){
		if (gst == null)
			return null;
		
		return format(gst.timeLatestAtReader);
	}
	
	private String format(Long date){
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(date);
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}
	
	//
	// Returns the long range id (not the band id)
	//
	public String getFirstBandId() {
		if (guest == null || guest.getXbands() == null || guest.getXbands().isEmpty())
			return "missing-band-id";
		
		return guest.getXbands().get(0).getLRId();
	}

	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
}
