package com.disney.xband.xview.views;


import javax.ws.rs.*;

import java.net.URI;
import java.util.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;


import com.disney.xband.xview.controllers.GuestController;
import com.disney.xband.xview.controllers.GuestInfoController;
import com.disney.xband.xview.lib.model.*;
import com.sun.jersey.api.JResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/guests")

public class GuestsView {
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public JResponse<List<Guest>> getGuests() throws Exception
	{
		List<Guest> guests = com.disney.xband.xview.controllers.GuestController.GetAllGuests();

		return JResponse.ok(guests).build();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/id/{id}/")
	public JResponse<Guest> getGuestbyId(@PathParam("id") String id) throws Exception
	{	
		Guest guest = com.disney.xband.xview.controllers.GuestController.GetGuestById(id);
		
		if (guest == null)
		{
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.type("text/html");
			builder.entity("<h3>Guest not found.</h3>");
			throw new WebApplicationException(builder.build());
			
		}
		
		
		return JResponse.ok(guest).build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/search/{name}")
	public JResponse<List<Guest>> SearchGuests(@PathParam("name") String name) throws Exception
	{
		List<Guest> guests = GuestController.SearchGuestName(name);
		if (guests.isEmpty())
		{
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.type("text/html");
			builder.entity("<h3>Guest not found.</h3>");
			throw new WebApplicationException(builder.build());
		}
		
		return JResponse.ok(guests).build();
	}
	
	@SuppressWarnings("rawtypes")
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/id/{id}/")
	public JResponse UpdateGuest(String guest) throws Exception
	{	
		System.out.println("Received PUT XML/JSON Request");
		ObjectMapper om = new ObjectMapper();
		Guest g = om.readValue(guest, Guest.class);
		
		//Guest guest = g.getValue();
		
		System.out.println("Guest ID: " + g.getGuestId());
		System.out.println("LastName : " + g.getLastName());
		
		boolean saved = com.disney.xband.xview.controllers.GuestController.UpdateGuest(g);
		JResponse result = JResponse.ok().build();
		
		//boolean saved = true;
		if (saved)
		{
			//result = JResponse.ok(saved).build();
		}
		else
		{
			ResponseBuilder builder = Response.status(Status.NOT_MODIFIED);
			builder.type("text/html");
			builder.entity("<h3>Guest not modified.</h3>");
			throw new WebApplicationException(builder.build());
		}
		
		return result;
		
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
    public void CreateGuest(String guest) throws Exception
	{
		ObjectMapper om = new ObjectMapper();
		Guest g = om.readValue(guest, Guest.class);
		
		System.out.println("Guest :" + g.getFirstName());
		
		Guest newGuest = GuestController.CreateGuest(g);
		if (newGuest == null)
		{
			ResponseBuilder builder = Response.status(Status.fromStatusCode(500));
			builder.type("application/json");
			builder.entity("<h3>Failed to create guest.</h3>");
			throw new WebApplicationException(builder.build());
		}
		
			URI location = uriInfo.getAbsolutePathBuilder().path(guest).build();
			Response.created(location);
		
	}

	@PUT
	@Path("/{guestName}/xbands/{xbandId}/phone/{phone}")
	@Produces({MediaType.APPLICATION_JSON})
	public void CreateDemoGuest(@PathParam("guestName")String guestName, @PathParam("xbandId")String xBandId, @PathParam("phone")String phone) throws Exception
	{
		Guest g = GuestController.CreateDemoGuest(guestName, xBandId, phone);
		
		if (g != null)
		{
			URI uri = new URI("/guests/id/" + g.getGuestId());
			
			Response.created(uri);
		}
		else
		{
			ResponseBuilder builder = Response.status(Status.fromStatusCode(500));
			builder.type("text/html");
			builder.entity("<h3>Failed to create guest.</h3>");
			throw new WebApplicationException(builder.build());
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	public JResponse DeleteGuest(Guest guest)
	{
		JResponse retVal = null;
		
		//boolean result = com.disney.xband.xview.controllers.GuestController.DeleteGuest(guest);
		boolean result = true;
		if (result)
		{
			retVal = JResponse.ok(result).build();
		}
		else
		{
			retVal = JResponse.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return retVal;
	}
	
	
	// Get only the xBands for a guest based on the guestId.
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/id/{id}/xbands/")
	public JResponse<ArrayList<Xband>> GetGuestXBands(@PathParam("id") String id) throws Exception
	{
		return JResponse.ok(new ArrayList<Xband>(GuestController.GetGuestXBands(id))).build();
	}
	
	@SuppressWarnings("rawtypes")
	@PUT
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/id/{guestId}/xbands/{xbandId}/")
	public JResponse AddXBandToGuest(@PathParam("guestId") String guestId, @PathParam("xbandId") String xbandId) throws Exception
	{
		boolean result = GuestController.AddXBandToGuest(guestId, xbandId);
		
		if (result == true)
		return JResponse.ok().build();
		else
		{
		return	JResponse.notModified().build();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/id/{guestId}/xbands/{xbandId}/")
	public JResponse RemoveXBandFromGuest(@PathParam("guestId") String guestId, @PathParam("xbandId") String xBandId) throws Exception
	{
		boolean result = GuestController.RemoveXBandFromGuest(guestId, xBandId);
		
		if (result == true)
		{
			return JResponse.ok().build();
		}
		else
		{
			return JResponse.status(javax.ws.rs.core.Response.Status.NOT_MODIFIED).build();
		}
	}
	

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/id/{id}/guestinfos")
	public JResponse<List<GuestInfo>> GetGuestInfos(@PathParam("id") String id) throws Exception
	{
		List<GuestInfo> guestInfos = GuestInfoController.GetGuestInfosForGuest(id);
		
		return JResponse.ok(guestInfos).build();

	}
	
	@POST
	@Path("/id/{id}/guestinfos")
	@Consumes({MediaType.APPLICATION_JSON})
	public boolean AddGuestInfo(GuestInfo gi) throws Exception
	{
		return GuestInfoController.SetGuestInfo(gi);
	}
	
	@PUT
	@Path("/id/{id}/guestinfos")
	@Consumes({MediaType.APPLICATION_JSON})
	public boolean UpdateGuestInfo(GuestInfo gi) throws Exception
	{
		return GuestInfoController.UpdateGuestInfo(gi);
	}
	
	
	@DELETE
	@Path("/id/{id}/guestinfos/{gid}")
	public boolean RemoveGuestInfo(@PathParam("id")String id, @PathParam("gid") String gid) throws Exception
	{
		return GuestInfoController.RemoveGuestInfo(id, gid);
	}
	

	
	
	
}
