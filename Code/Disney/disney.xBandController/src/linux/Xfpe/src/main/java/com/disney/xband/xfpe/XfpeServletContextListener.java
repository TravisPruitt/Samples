package com.disney.xband.xfpe;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.disney.xband.xfpe.simulate.Simulator;

public class XfpeServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent context) {
		Simulator.getInstance().shutdown();		
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		Simulator.getInstance().startOmniSimulator();
	}
}
