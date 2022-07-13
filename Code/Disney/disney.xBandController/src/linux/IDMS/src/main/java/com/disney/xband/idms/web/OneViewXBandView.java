package com.disney.xband.idms.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.disney.xband.idms.lib.model.GuestXBandAssign;
import com.disney.xband.idms.lib.model.XBand;
import com.disney.xband.idms.lib.model.XBandPut;
import com.disney.xband.idms.performance.Metrics;
import com.disney.xband.idms.performance.XBandPerformanceMetricType;
import com.sun.jersey.api.JResponse;


@Path("/xband")
public class OneViewXBandView 
{
	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	private static Logger logger = Logger.getLogger(OneViewXBandView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();
	
		
	@GET
	@Path("/{xbandId}/{guestId}")
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response AssignXBandToGuest(@PathParam("xbandId")long xbandId, @PathParam("guestId")long guestId )
	{
		boolean result = false;
		Date startTime = new Date();
		
		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();
		
		try
		{
			result = daoFactory.getXBandDAO().AssignXbandToGuest(guestId, xbandId);

			Metrics.INSTANCE.UpdateXBandMetric(startTime, XBandPerformanceMetricType.AssignXbandToGuest);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Assign XBand to Guest failed.", ex));
			throw new WebApplicationException(500);
		}

		if(result)
		{
			return Response.ok().build();
		}
		else
		{
			throw new WebApplicationException(404);
		}
	}



	@GET
	@Path("/unassign/{xbandId}/{guestId}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response UnasignXBandFromGuest(@PathParam("xbandId")long xbandId, @PathParam("guestId")long guestId )
	{
		boolean result = false;
		Date startTime = new Date();

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();
		try
		{
			result = daoFactory.getXBandDAO().UnassignXBand(guestId, xbandId);

			Metrics.INSTANCE.UpdateXBandMetric(startTime, XBandPerformanceMetricType.UnassignXbandToGuest);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Unassign XBand to Guest failed.", ex));
			throw new WebApplicationException(500);
		}

		if(result)
		{
			return Response.ok().build();
		}
		else
		{
			throw new WebApplicationException(404);
		}

	}

/*	@GET
	@Path("/updateType/{xbandId}/{newType}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response UpdateType(@PathParam("xbandId")long xbandId, @PathParam("newType")String newTypeName)
	{
		Date startTime = new Date();

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			daoFactory.getXBandDAO().UpdateType(xbandId, newTypeName);

			Metrics.INSTANCE.UpdateXBandMetric(startTime, XBandPerformanceMetricType.AssignXbandToGuest);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Update Band Type failed.", ex));
			throw new WebApplicationException(500);
		}

		return Response.ok().build();
	}*/

	@POST
	@Path("/assign")
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response AssignXBand(String guestXBandAssign) throws JsonParseException, JsonMappingException, IOException, URISyntaxException
	{
		boolean result = false;
		Date startTime = new Date();

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			GuestXBandAssign id = om.readValue(guestXBandAssign, GuestXBandAssign.class);

			long guestId = Long.parseLong(id.getGuestId());

			long xbandId = Long.parseLong(id.getXbandId());

			result = daoFactory.getXBandDAO().AssignXbandToGuest(guestId, xbandId);

			Metrics.INSTANCE.UpdateXBandMetric(startTime, XBandPerformanceMetricType.AssignXbandToGuest);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Assign XBand using identifier failed.",ex));
			throw new WebApplicationException(400);
		}
		
		if(result)
		{
			
			return Response.ok().build();
		}
		else
		{
			throw new WebApplicationException(404);
		}
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response CreateXBand(String xbandData)
	{
		Date startTime = new Date();
		
		URI location = null;
		long xbandId = 0;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			XBandPut xband = om.readValue(xbandData, XBandPut.class);

			xbandId = daoFactory.getXBandDAO().Create(xband);
			location = new URI("/" + xbandId);
			
			Metrics.INSTANCE.UpdateXBandMetric(startTime, 
					XBandPerformanceMetricType.SaveXband);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Create XBand failed.",ex));
			throw new WebApplicationException(500);
		}

		if (xbandId == 0)
		{
			throw new WebApplicationException(400);
		}
		
		return Response.created(location).build();
	}

	@GET
	@Path("/id/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandByxBandId(@PathParam("id") String xbandId)
	{
		return GetXBand(BandLookupType.XBANDID, xbandId);
	}
	
	
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandByxBandIdTwo(@PathParam("id") String xbandId)
	{
		return GetXBand(BandLookupType.XBANDID, xbandId);
	}
	
	@GET
	@Path("/bandid/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandByBandId(@PathParam("id") String bandId)
	{
		return GetXBand(BandLookupType.BANDID, bandId);
	}
	
	@GET
	@Path("/lrid/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandBylrid(@PathParam("id") String lrid)
	{
		return GetXBand(BandLookupType.LRID, lrid);
	}

	@GET
	@Path("/tapid/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandBytapid(@PathParam("id") String tapId)
	{
		return GetXBand(BandLookupType.TAPID, tapId);
	}
	
	@GET
	@Path("/secureid/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandBysecureid(@PathParam("id") String secureId)
	{
		return GetXBand(BandLookupType.SECUREID, secureId);
	}
	
	@GET
	@Path("/uid/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandByUid(@PathParam("id") String uid)
	{
		return GetXBand(BandLookupType.UID, uid);
	}

	@GET
	@Path("/public/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<XBand> GetXBandByPublic(@PathParam("id") String publicId)
	{
		return GetXBand(BandLookupType.PUBLICID, publicId);
	}
	private JResponse<XBand> GetXBand(BandLookupType lookupType, String id)
	{
		Date startTime = new Date();
		BandData bandData = null;
		XBand xband = null;
		
		try
		{
			bandData = daoFactory.getXBandDAO().GetBand(lookupType, id);
			if (bandData != null)
				xband = bandData.getXBand();

			//TODO: Change
			Metrics.INSTANCE.UpdateXBandMetric(startTime, XBandPerformanceMetricType.GetXbandByxBandId);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Xband failed.", ex));
			throw new WebApplicationException(500);
		}
		
		if (xband == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(xband).build();
	}
}
