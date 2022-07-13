package com.disney.xband.lib.xbrapi;

import com.disney.xband.xview.lib.model.Xband;

public class TapEvent extends XbrEvent {
	// RFID of the band.
	private String uid;
	transient private String uiddecimal;
	private String pid;
	transient private String piddecimal;
	private String sid;
	private String iin;
	private Xband xband;
	
	
	public TapEvent() {
		super(XbrEventType.RFID);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String getID() {
		return uid;
	}

	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	public String getSid()
	{
		return sid;
	}

	public void setSid(String sid)
	{
		this.sid = sid;
	}

	public String getIin()
	{
		return iin;
	}

	public void setIin(String iin)
	{
		this.iin = iin;
	}
	
	public Xband getXband()
	{
		return xband;
	}
	
	public void setXband(Xband xband)
	{
		this.xband = xband;
	}
	
	public String getPidDecimal()
	{
		if (piddecimal == null && pid != null)
			piddecimal = new Long(Long.parseLong(pid,16)).toString();
		
		return piddecimal;
	}
	
	public String getUidDecimal()
	{
		if (uiddecimal == null && uid != null)
			// convert the uid to decimal number after reversing the hex digits
			uiddecimal = Long.toString(Long.parseLong(XbrJsonMapper.reverseId(uid),16));
		
		return uiddecimal;
	}
}
