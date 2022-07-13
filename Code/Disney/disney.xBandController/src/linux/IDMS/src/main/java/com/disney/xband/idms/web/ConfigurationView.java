package com.disney.xband.idms.web;

import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.sun.jersey.api.JResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/configuration")
public class ConfigurationView
{

	public ConfigurationView()
	{
	}

	@GET
	@Produces({"application/json"})
	public JResponse<ConfigurationSettings> getConfiguration() 
	{
		return JResponse.ok(ConfigurationSettings.INSTANCE).build();
	}
	
	@POST
	@Consumes({"application/json"})
	public Response saveConfiguration(String settings)
	{
	    
		//TODO: I don't think we want to allow database settings to be changed from the API. Once
		//we add some additional settings then we should allow those to be set.
		
		return Response.ok().build();
	}
}