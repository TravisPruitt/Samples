package com.disney.xband.xbms.web;

import com.disney.xband.xbms.web.ConfigurationSettings;
import com.sun.jersey.api.JResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
	
}
