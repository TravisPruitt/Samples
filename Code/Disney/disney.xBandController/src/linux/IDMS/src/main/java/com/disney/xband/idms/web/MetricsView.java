package com.disney.xband.idms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.disney.xband.idms.performance.Metrics;

@Path("/metrics")
public class MetricsView 
{

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Metrics getMetrics()
	{
		return Metrics.INSTANCE;
	}
	
	@GET
	@Path("/reset")
	@Produces(MediaType.TEXT_HTML)
	public String getReset()
	{
		Metrics.INSTANCE.clear();
		
		return "<html><title>" + "Metrics Reset" + "</title>" + 
		"<body>" + "Metrics Have been reset." + "</body></html>";
	}
}

