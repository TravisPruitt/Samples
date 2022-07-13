package com.disney.xband.xview.views;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.disney.xband.xview.Utils;
import com.disney.xband.xview.controllers.XBandController;
import com.disney.xband.xview.lib.model.Xband;
import com.sun.jersey.api.JResponse;



@Path("/xbands")
public class XBandsView {
	
	// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			public JResponse<List<Xband>> getXBands(@QueryParam("createdBy") String createdBy) throws Exception
			{	
				List<Xband> xbands = null;
				
				if (createdBy == "" || createdBy == null)
				{
					 xbands = XBandController.GetAllXBands();
				}
				else
				{
					xbands = XBandController.GetAllXBands(createdBy);
				}
				
				return JResponse.ok(xbands).build();
			}
				
			// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			@Path("/id/{id}")
			public JResponse<Xband> getXBandByXBandId(@PathParam("id") String id) throws Exception
			{
				Xband xband = XBandController.GetXBandByXBandId(id);
				
				if  (xband == null)
				{
					ResponseBuilder builder = Response.status(Status.NOT_FOUND);
					builder.type("text/html");
					builder.entity("<h3>XBand not found.</h3>");
					throw new WebApplicationException(builder.build());
				}
											
				return JResponse.ok(xband).build();
			}
			
			// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			@Path("/lrid/{id}")
			public JResponse<Xband> getXBandByLRId(@PathParam("id") String id) throws Exception
			{
				Xband xband = XBandController.GetXBandByLRId(id);
				
				if  (xband == null)
				{
					ResponseBuilder builder = Response.status(Status.NOT_FOUND);
					builder.type("text/html");
					builder.entity("<h3>XBand not found.</h3>");
					throw new WebApplicationException(builder.build());
				}
				
				return JResponse.ok(xband).build();
			}
			
			// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			@Path("/bandid/{id}")
			public JResponse<Xband> getXBandByBandId(@PathParam("id") String id) throws Exception
			{
				Xband xband = XBandController.GetXBandByBandId(id);
				
				if  (xband == null)
				{
					ResponseBuilder builder = Response.status(Status.NOT_FOUND);
					builder.type("text/html");
					builder.entity("<h3>XBand not found.</h3>");
					throw new WebApplicationException(builder.build());
				}
							
				return JResponse.ok(xband).build();
			}
			
			// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			@Path("/tapid/{id}")
			public JResponse<Xband> getXBandByTapId(@PathParam("id") String id) throws Exception
			{
				Xband xband = XBandController.GetXBandByTapId(id);
				
				if  (xband == null)
				{
					ResponseBuilder builder = Response.status(Status.NOT_FOUND);
					builder.type("text/html");
					builder.entity("<h3>XBand not found.</h3>");
					throw new WebApplicationException(builder.build());
				}
				
				return JResponse.ok(xband).build();
			}
			
			public JResponse getXBandBySecureId(@PathParam("id") String id) throws Exception
			{
				Xband xband = XBandController.GetXBandBySecureId(id);
				
				if (xband == null)
				{
					throw new WebApplicationException(404);
				}
				
				return JResponse.ok(xband).build();
			}
			
			// This method is called if XML is request
			@GET
			@Produces({MediaType.APPLICATION_JSON})
			@Path("/friendlynameid/{id}")
			public String getXBandByFriendlyNameId(@PathParam("id") String id)
			{
															
				String retVal = "xband: FriendlyNameId = " + id;
															
				return retVal;
			}
			
			// This method is called if XML is request
			@GET
			@Produces({MediaType.TEXT_HTML ,MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
			@Path("/isbandactive/{id}")
			public String getXBandIsActiveByBandId(@PathParam("id") String id)
			{
																		
				String retVal = "xband: Active - BandId = " + id;
																		
				return retVal;
			}
			
			@SuppressWarnings("rawtypes")
			@POST
			@Produces({ MediaType.APPLICATION_JSON})
			public JResponse CreateNewXBand(Xband xband) throws Exception
			{
				
				Xband result =  XBandController.CreateXBand(xband, Utils.getUserName());
				if (result != null)
				{
					URI uri = new URI("/xbands/id/" + result.getXBandId());
					return JResponse.created(uri).build();
				}
				else
				{
					return JResponse.status(Status.NOT_ACCEPTABLE).build();
				}
				
			}
			
			
			// Uses a queryparam, createdBy, to create an xBand with a particular userName.
			@POST
			@Produces({ MediaType.APPLICATION_JSON})
			@Path("/createdby")
			public Xband CreateNewXBand(@QueryParam("createdBy") String createdBy, Xband xband) throws Exception
			{
				return XBandController.CreateXBand(xband, createdBy);
			}
			
			
			@DELETE
			@Path("/id/{xbandId}/")
			public void DeleteXBandById(@PathParam("xbandId") String xbandId) throws Exception
			{
				boolean result= XBandController.DeleteXBandById(xbandId);
				
				if (result == true)
					 	JResponse.ok().build();
					else
					{
						JResponse.notModified().build();
					}
			}
			
			@DELETE
			public void DeleteXBand(Xband xband) throws Exception
			{
				boolean result =  XBandController.DeleteXBand(xband);
				
				if (result == true)
					 	JResponse.ok().build();
					else
					{
						JResponse.notModified().build();
					}
			}
			
			
			@DELETE
			@Path("/createdby")
			public void DeleteXBandsCreatedBy(@QueryParam("createdBy") String createdBy) throws Exception
			{
				boolean result= XBandController.DeleteAllXBandsCreatedBy(createdBy);
				
				if (result == true)
					 	JResponse.ok().build();
					else
					{
						JResponse.notModified().build();
					}
			}

			
			// Activate an XBand
			@PUT
			@Path ("/id/{xbandId}/activate/")
			public void ActivateXBand(@PathParam("xbandId") String xbandId) throws Exception
			{
				boolean result =  XBandController.setXBandActiveState(xbandId, true);
				if (result == true)
				{
					Response.ok().build();
				}
				else
				{
					Response.notModified().build();
				}
			}
			

			@PUT
			@Path("/id/{xbandId}/deactivate/")
			public void deactivateBandById(@PathParam("xbandId") String xbandId) throws Exception
			{																
				boolean result= XBandController.setXBandActiveState(xbandId, false);
				
				if (result==true)
				{
					Response.ok().build();
				}
				else
				{
					Response.notModified().build();
				}
			}

			
			
			

}
