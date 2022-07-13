package com.disney.xband.xbrc.ui.edit.action;

import java.util.Collection;
import java.util.Map;

import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.bean.GuestStatusBean;
import com.disney.xband.xbrc.ui.bean.ReaderValue;
import com.disney.xband.xbrc.ui.db.GSTService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.disney.xband.xbrc.ui.db.UIReaderService;

public class XBandViewAction extends BaseAction {

	private GSTService gstService;
	private ReaderConfig readerConfig;
	private GuestStatusBean guestStatus;
	private Map<Location, Collection<ReaderValue>> locMap;
	private String bandId;
	private String guestId;

	@Override
	public void prepare() throws Exception {
		super.prepare();

		gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		readerConfig = ReaderConfig.getInstance();
		readerConfig.initialize(UIConnectionPool.getInstance());
	}
	
	/** 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		
		//get locations' readers
		locMap = UIReaderService.getReaderValuesByLocation();
		
		if (guestId == null || bandId == null)
			//show at least all the locations and their readers
			return SUCCESS;
		
		guestStatus = new GuestStatusBean(gstService.find(guestId));
		guestStatus.setBandId(bandId);
				
		return SUCCESS;
	}

	public GuestStatusBean getGuestStatus() {
		return guestStatus;
	}

	public void setGuestStatus(GuestStatusBean guestStatus) {
		this.guestStatus = guestStatus;
	}

	public Map<Location, Collection<ReaderValue>> getLocMap(){
		return locMap;
	}

	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	
	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	/**
	 * @return the readerConfig
	 */
	public ReaderConfig getReaderConfig() {
		return readerConfig;
	}
	
}
