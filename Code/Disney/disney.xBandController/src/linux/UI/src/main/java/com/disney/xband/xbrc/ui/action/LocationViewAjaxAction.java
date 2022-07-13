package com.disney.xband.xbrc.ui.action;

import java.util.Collection;
import java.util.HashMap;

import com.disney.xband.xbrc.lib.entity.GuestStatus;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.ui.bean.GuestStatusBean;
import com.disney.xband.xbrc.ui.db.Data;
import com.opensymphony.xwork2.ActionSupport;

public class LocationViewAjaxAction extends BaseAction {
	private Long locationId;
	private HashMap<Reader,Collection<GuestStatusBean>> statusMap;
	
	@Override
	public String execute() throws Exception {
		statusMap = Data.getGuestStatusByReader(locationId);
		return super.execute();
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public HashMap<Reader, Collection<GuestStatusBean>> getStatusMap() {
		return statusMap;
	}
}
