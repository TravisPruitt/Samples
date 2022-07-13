package com.disney.xband.idms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.disney.xband.common.lib.health.IDMSStatus;

@Path("/status")
public class StatusView
{	
	public StatusView()
	{
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public IDMSStatus getStatus() 
	{		
		StatusInfo.INSTANCE.Check();
		return StatusInfo.INSTANCE.getIDMSStatus();
	}
}
