package com.disney.xband.xbrc.ui.edit.action;

import java.net.ConnectException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.config.EventConfig;
import com.disney.xband.xbrc.lib.config.ReaderConfig;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.bean.GuestStatusBean;
import com.disney.xband.xbrc.ui.bean.LocationBean;
import com.disney.xband.xbrc.ui.db.BandViewService;
import com.disney.xband.xbrc.ui.db.GSTService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class XBandViewAjaxAction extends ActionSupport implements Preparable {
	
	private BandViewService bandViewService;
	private EventConfig eventConfig;
	private GSTService gstService;
	private String bandId;
	private String guestId;
	private Collection<LocationBean> locBeans;
	private GuestStatusBean guestStatus;
	private ReaderConfig readerConfig;
	
	private static Logger logger = Logger.getLogger(XBandViewAjaxAction.class);

	@Override
	public void prepare() throws Exception {
		bandViewService = ServiceLocator.getInstance().createService(BandViewService.class);
		gstService = ServiceLocator.getInstance().createService(GSTService.class);
		
		eventConfig = EventConfig.getInstance();
		eventConfig.initialize(UIConnectionPool.getInstance());
		
		readerConfig = ReaderConfig.getInstance();
		readerConfig.initialize(UIConnectionPool.getInstance());
	}
	
	/** 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		if (bandId == null)
			return INPUT;
		
		try {
			locBeans = bandViewService.getByBandId(
					bandId, 
					eventConfig.getDeltaTime());
		} catch (Exception e){
			logger.error("Couldn't get events from the Controller  !!", e);
			this.addActionError("Can not connect to the Controller !!");
		}
		
		/*
		 * This will happen a lot when, for example, the 
		 * @link com.disney.xband.xbrc.lib.config.EventConfig#deltaTime is set too low
		 */
		if (locBeans == null)
			return INPUT;
		
		guestStatus = new GuestStatusBean(gstService.find(guestId));
		guestStatus.setBandId(bandId);
		
		return SUCCESS;
	}

	/**
	 * @return the readerid
	 */
	public String getBandId() {
		return bandId;
	}

	/**
	 * @param readerid the readerid to set
	 */
	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	
	/**
	 * @return the guestId
	 */
	public String getGuestId() {
		return guestId;
	}

	/**
	 * @param guestId the guestId to set
	 */
	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public Collection<LocationBean> getLocBeans() {
		return locBeans;
	}

	public GuestStatusBean getGuestStatus() {
		return guestStatus;
	}

	public void setGuestStatus(GuestStatusBean guestStatus) {
		this.guestStatus = guestStatus;
	}

	/**
	 * @return the eventConfig
	 */
	public EventConfig getEventConfig() {
		return eventConfig;
	}
}
