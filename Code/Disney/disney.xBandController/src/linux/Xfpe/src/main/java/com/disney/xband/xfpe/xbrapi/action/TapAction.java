package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReaderLight;
import com.disney.xband.xfpe.model.XfpeReaderSequence;

public class TapAction extends BaseAction {
	private String uid;
	private String pid;
	private String sid;
	private String iin;
	private String error;
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			XfpeController.getInstance().onCommandSimulateTap(readerId, uid, pid, sid, iin);
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in TapAction.", e);
			error = e.toString();
			return INPUT;
		}
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
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

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}
}
