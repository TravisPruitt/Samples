package com.disney.xband.xfpe.action;

import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xfpe.bean.XfpeLocation;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.simulate.Simulator;
import com.opensymphony.xwork2.ActionSupport;

public class HomeAction extends ActionSupport {
	
	private static Logger logger = Logger.getLogger(HomeAction.class);	
	private Map<Long,XfpeLocation> locations;
	
	@BreadCrumb("Home")
	@Override
	public String execute() throws Exception {
		locations = XfpeController.getInstance().getLocations();		
		return super.execute();
	}
	
	public String getActionTitle(){
		return "Xfpe Home";
	}
	
	public Map<Long, XfpeLocation> getLocations() {
		return locations;
	}
}
