package com.disney.xband.xfpe.action;

import java.util.List;
import java.util.Map;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xfpe.bean.XfpeLocation;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.simulate.DB;
import com.disney.xband.xfpe.simulate.PEGuestTestSuite;
import com.disney.xband.xfpe.simulate.Simulator;
import com.disney.xband.xfpe.simulate.TestResult;
import com.opensymphony.xwork2.ActionSupport;

public class ShowReadersAction extends ActionSupport {
	
	private Map<Long,XfpeLocation> locations;
	private String locationId;
	private XfpeLocation location;
	private String action;
	private List<PEGuestTestSuite> testSuiteList;
	private String suiteId;
	private String maxReaders;
	
	@Override
	@BreadCrumb("Readers")
	public String execute() throws Exception {
		
		if (action != null) {
			if (action.equals("startTest")) {
				Simulator.getInstance().startTests(Long.parseLong(suiteId), Integer.parseInt(maxReaders));
			}		
			if (action.equals("stopTest")) {
				Simulator.getInstance().stopTests();
			}
		}
		
		locations = XfpeController.getInstance().getLocations();
		location = locations.get(Long.parseLong(locationId));
		testSuiteList = DB.findAllTestSuites();
		
		return super.execute();
	}
	
	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}	
	
	public List<XfpeReader> getReaders() {		
		if (location == null)
			return null;
		return location.getReaders(); 
	}
	
	public XfpeLocation getLocation() {
		return location;
	}
	
	public boolean isTestRunning() {
		return Simulator.getInstance().isTestRunning();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<PEGuestTestSuite> getTestSuiteList() {
		return testSuiteList;
	}

	public String getSuiteId() {
		return suiteId;
	}

	public void setSuiteId(String suiteId) {
		this.suiteId = suiteId;
	}

	public String getMaxReaders()
	{
		if (maxReaders == null && XfpeController.getInstance().getReaders() != null)
			return "" + XfpeController.getInstance().getReaders().size();
		return maxReaders;
	}

	public void setMaxReaders(String maxReaders)
	{
		this.maxReaders = maxReaders;
	}
	
	public PEGuestTestSuite getLastTestSuite()
	{
		return Simulator.getInstance().getLastSuite();
	}
	
	public List<TestResult> getLastTestResults()
	{
		return Simulator.getInstance().getLastSuiteResults();
	}
}
