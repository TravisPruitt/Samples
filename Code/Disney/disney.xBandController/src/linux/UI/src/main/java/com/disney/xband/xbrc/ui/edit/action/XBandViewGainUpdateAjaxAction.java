package com.disney.xband.xbrc.ui.edit.action;

import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.db.BandViewService;
import com.disney.xband.xbrc.ui.httpclient.Controller;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class XBandViewGainUpdateAjaxAction extends ActionSupport implements Preparable {
	
	private BandViewService bandViewService;
	//private EventConfig eventConfig;
	private String readerId;
	private Double gain;

	@Override
	public void prepare() throws Exception {
		bandViewService = ServiceLocator.getInstance().createService(BandViewService.class);
	}
	
	/** 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		if (readerId == null || gain == null)
			return INPUT;
		
		bandViewService.updateGain(readerId, gain);
		
		Controller.getInstance().notifyXbrcOfReaderSignalChange(readerId);
		
		return SUCCESS;
	}

	/**
	 * @return the readerId
	 */
	public String getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId the readerId to set
	 */
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	/**
	 * @return the gain
	 */
	public Double getGain() {
		return gain;
	}

	/**
	 * @param gain the gain to set
	 */
	public void setGain(Double gain) {
		if (gain == null)
			return;
		
		this.gain = gain;
	}
}
