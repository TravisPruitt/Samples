package com.disney.xband.xbrc.ui.action;

import java.util.List;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;
import com.disney.xband.xbrc.ui.bean.GuestStatusBean;
import com.disney.xband.xbrc.ui.db.Data;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.opensymphony.xwork2.ActionSupport;

public class LocationViewGuestsAction extends BaseAction {
	private Location location;
	private Long locationId;
	private String readerId;
	private String xpass;

	private GridItem.XpassOnlyState xPassOnlyState = XpassOnlyState.AllGuests;
	private List<GuestStatusBean> guests;
	private String state;
	
	@Override
	public String execute() throws Exception {
		return super.execute();
	}
	
	public String getguests() throws Exception {
		if (locationId != null && locationId >= 0)
			return getlocationguests();
		return getstateguests();
	}
	
	public String getreaderguests() throws Exception {
		guests = Data.getReaderGuests(readerId);
		return SUCCESS;
	}
	
	public String getlocationguests() throws Exception {
		LocationService locationService = ServiceLocator.getInstance().createService(LocationService.class);
		location = locationService.find(locationId);
		guests = Data.getLocationGuests(locationId, xPassOnlyState);
		return SUCCESS;
	}
	
	public String getstateguests() throws Exception {
		guests = Data.getStateGuests(GuestStatusState.valueOf(state), xPassOnlyState);
		return SUCCESS;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public List<GuestStatusBean> getGuests() {
		return guests;
	}

	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getXpass() {
		return xpass;
	}

	public void setXpass(String xpass) {
		this.xpass = xpass;
		xPassOnlyState = GridItem.XpassOnlyState.valueOf(xpass);
	}
}
