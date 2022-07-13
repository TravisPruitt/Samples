package com.disney.queries.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/")
public class Queries {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getQueriesUI()
	{
		String result = "";
		
		result += "<html>";
		result += "<header>";
		result += "<title>Query Viewer</title>";
		result += "<meta name='viewport' content='width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;'/>";
		result += "<link rel='stylesheet' href='iui/iui.css' type='text/css' />";
		result += "<link rel='stylesheet' href='iui/t/default/default-theme.css' type='text/css'/>";
		result += "<script type='application/x-javascript' src='iui/iui.js'></script>";
		result += "</header>";
		result += "<body>";
		result += "Queries";
		result += "</body>";
		result += "</html>";
		return result;
	}
}
