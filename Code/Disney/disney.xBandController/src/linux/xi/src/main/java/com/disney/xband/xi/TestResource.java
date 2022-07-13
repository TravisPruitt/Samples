package com.disney.xband.xi;

import com.disney.xband.xi.model.ConfigDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/testresource")
public class TestResource
{
	public TestResource()
	{
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testDb()
	{
		ConfigDAO d = new ConfigDAO();
		return d.dbTest();
	}
}