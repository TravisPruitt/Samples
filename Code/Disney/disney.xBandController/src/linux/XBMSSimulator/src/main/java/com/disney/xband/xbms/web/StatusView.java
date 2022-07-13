package com.disney.xband.xbms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
public class StatusView
{	
	public StatusView()
	{
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public XbmsStatus getStatus() 
	{		
		StatusInfo.INSTANCE.Check();
		return StatusInfo.INSTANCE.getXbmsStatus();
	}
}
