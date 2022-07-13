package com.disney.xband.xbrc.ui.action;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.opensymphony.xwork2.ActionSupport;

public class LocationViewAction extends BaseAction {

	private Long locationId;
	private Location location;
	
	@Override
	public String execute() throws Exception {
		LocationService locationService = ServiceLocator.getInstance().createService(LocationService.class);
		location = locationService.find(locationId);
		return super.execute();
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public Location getLocation() {
		return location;
	}
}
