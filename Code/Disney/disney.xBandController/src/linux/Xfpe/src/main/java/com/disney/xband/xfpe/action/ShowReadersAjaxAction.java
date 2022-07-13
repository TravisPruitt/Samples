package com.disney.xband.xfpe.action;

import java.util.List;
import java.util.Map;

import com.disney.xband.xfpe.bean.XfpeLocation;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.simulate.Simulator;
import com.opensymphony.xwork2.ActionSupport;

public class ShowReadersAjaxAction extends ActionSupport
{
	private String locationId;
	private XfpeLocation location;
	private Map<Long,XfpeLocation> locations;
	
	@Override
	public String execute() throws Exception
	{
		locations = XfpeController.getInstance().getLocations();
		location = locations.get(Long.parseLong(locationId));
		
		return super.execute();
	}

	public String getLocationId()
	{
		return locationId;
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public XfpeLocation getLocation()
	{
		return location;
	}

	public void setLocation(XfpeLocation location)
	{
		this.location = location;
	}

	public Map<Long, XfpeLocation> getLocations()
	{
		return locations;
	}

	public void setLocations(Map<Long, XfpeLocation> locations)
	{
		this.locations = locations;
	}
	
	public List<XfpeReader> getReaders() {		
		if (location == null)
			return null;
		return location.getReaders(); 
	}
	
	public boolean isTestRunning() {
		return Simulator.getInstance().isTestRunning();
	}
}
