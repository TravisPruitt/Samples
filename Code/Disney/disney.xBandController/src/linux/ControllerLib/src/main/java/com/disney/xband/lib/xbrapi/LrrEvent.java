package com.disney.xband.lib.xbrapi;

import com.disney.xband.xview.lib.model.Xband;

public class LrrEvent extends XbrEvent {
	// Long range band id.
	private String xlrid;
	// Event number. (eno)
	private Long eno;
	// Packet sequence number (pno)
	private Long pno;
	// Frequency (freq)
	private Long freq;
	// Channel (chan)
	private Long chan;
	// Signal strength (ss)
	private Long ss;
	// associated band
	transient private Xband xband;
	// decimal version of xlrid
	transient private String piddecimal; 
	
	public LrrEvent() {
		super(XbrEventType.LRR);
	}
	
	public Long getEno() {
		return eno;
	}
	public void setEno(Long eno) {
		this.eno = eno;
	}
	public Long getPno() {
		return pno;
	}
	public void setPno(Long pno) {
		this.pno = pno;
	}
	public Long getFreq() {
		return freq;
	}
	public void setFreq(Long freq) {
		this.freq = freq;
	}
	public Long getChan() {
		return chan;
	}
	public void setChan(Long chan) {
		this.chan = chan;
	}
	public Long getSs() {
		return ss;
	}
	public void setSs(Long ss) {
		this.ss = ss;
	}

	public String getXlrid() {
		return xlrid;
	}

	public void setXlrid(String xlrid) {
		this.xlrid = xlrid;
	}

	@Override
	public String getID() {
		return xlrid;
	}
	
	public Xband getXband(){
		return xband;
	}
	public void setXband(Xband xband){
		this.xband = xband;
	}

 	public String getPidDecimal()
 	{
 		if (piddecimal == null && xlrid != null)
			piddecimal = publicIdFromLRID(xlrid);
 		
 		return piddecimal;
 	}

	public static String publicIdFromLRID(String sLRID)
	{
		return Long.toString(Long.parseLong(sLRID,16));
	}
}
