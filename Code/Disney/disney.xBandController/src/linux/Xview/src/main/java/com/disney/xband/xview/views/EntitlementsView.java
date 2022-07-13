package com.disney.xband.xview.views;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/entitlements")
public class EntitlementsView {
	
	
	// This method is called if XML is request
		@GET
		@Produces({MediaType.TEXT_HTML ,MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
		public String getEntitlements()
		{
			
			String retVal = "List of all Entitlements.";
			
			return retVal;
		}
			
		// This method is called if XML is request
		@GET
		@Produces({MediaType.TEXT_HTML ,MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
		@Path("/id/{id}")
		public String getEntitlementById(@PathParam("id") String id)
		{
					
			String retVal = "Entitlement: id = " + id;
					
			return retVal;
		}

}
