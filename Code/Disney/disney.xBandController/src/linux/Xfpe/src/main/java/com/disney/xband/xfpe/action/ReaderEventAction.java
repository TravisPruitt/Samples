package com.disney.xband.xfpe.action;

import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.simulate.Simulator;
import com.opensymphony.xwork2.ActionSupport;

public class ReaderEventAction extends ActionSupport {
	
	private String readerId;
	private String action;
	private String bandId;
	private String bioData;
	
	@Override
	public String execute() throws Exception {				
		if (action.equals("tap")) {
			XfpeController.getInstance().handleGuestTap(readerId, bandId);
		}		
		if (action.equals("scan")) {
			XfpeController.getInstance().handleGuestScan(readerId, bioData);
		}
  		if (action.equals("logout")) {
  			XfpeController.getInstance().handleReaderLogout(readerId);
  		}
		return super.execute();
	}

	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}

	public String getBioData() {
		return bioData;
	}

	public void setBioData(String bioData) {
		this.bioData = bioData;
	}
}
