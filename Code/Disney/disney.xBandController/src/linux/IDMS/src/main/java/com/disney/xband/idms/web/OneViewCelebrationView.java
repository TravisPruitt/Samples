package com.disney.xband.idms.web;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPut;
import com.disney.xband.idms.lib.model.oneview.CelebrationPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationPut;
import com.disney.xband.idms.performance.CelebrationPerformanceMetricType;
import com.disney.xband.idms.performance.GuestPerformanceMetricType;
import com.disney.xband.idms.performance.Metrics;
import com.sun.jersey.api.JResponse;

@Path("/celebration")
public class OneViewCelebrationView 
{
	private static Logger logger = Logger.getLogger(OneViewCelebrationView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<Celebration> GetCelebration(@PathParam("id") PathSegment pathSegment) 
	{
		Date startTime = new Date();

		Celebration celebration = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);
			
			if(parameter.getIdentifierType().compareToIgnoreCase("celebration-id") == 0)
			{
				celebration = daoFactory.getCelebrationDAO().GetCelebration(
						parameter.getIdentifierType(), parameter.getIdentifierValue());

				Metrics.INSTANCE.UpdateCelebrationMetric(startTime, 
						CelebrationPerformanceMetricType.GetCelebrationById);
			}
				
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Celebration failed.",ex));
			throw new WebApplicationException(500);
		}
		
		if (celebration == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(celebration).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response Save(String celebrationString)
	{
		Date startTime = new Date();
		
		URI location = null;
		long celebrationId = 0;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			CelebrationPost celebration = om.readValue(celebrationString, CelebrationPost.class);
			
			celebrationId = daoFactory.getCelebrationDAO().SaveCelebration(celebration);
			
			location = new URI("/id;celebration-id=" + celebrationId);
			
			Metrics.INSTANCE.UpdateGuestMetric(startTime, 
					GuestPerformanceMetricType.SaveGuest);

		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Save Guest failed.",ex));
			throw new WebApplicationException(500);
		}
		
		if (celebrationId == 0)
		{
			throw new WebApplicationException(400);
		}
		
		return Response.created(location).build();
		
	}

	@PUT
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response Update(String celebrationString)
	{
		boolean result = false;
		
		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			CelebrationPut celebration = om.readValue(celebrationString, CelebrationPut.class);
			
			result = daoFactory.getCelebrationDAO().UpdateCelebration(celebration);
			
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Update Celebration failed.",ex));
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

	@POST
	@Path("/guest")
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response AddCelebrationGuest(String celebrationGuestString)
	{
		boolean result = false;
		
		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			CelebrationGuestPost celebrationGuest = om.readValue(celebrationGuestString, CelebrationGuestPost.class);
			
			result = daoFactory.getCelebrationDAO().AddCelebrationGuest(celebrationGuest);

		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Save Guest failed.",ex));
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

	@PUT
	@Path("/guest")
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response UpdateCelebrationGuest(String celebrationGuestString)
	{
		boolean result = false;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();		

		try
		{
			ObjectMapper om = new ObjectMapper();
			CelebrationGuestPut celebrationGuest = om.readValue(celebrationGuestString, CelebrationGuestPut.class);
			
			result = daoFactory.getCelebrationDAO().UpdateCelebrationGuest(celebrationGuest);

		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Update Celebration Guest failed.",ex));
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
	
	@DELETE
	@Path("/guest")
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response DeleteCelebrationGuest(String celebrationGuestString)
	{
		boolean result = false;
		
		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			CelebrationGuestPut celebrationGuest = om.readValue(celebrationGuestString, CelebrationGuestPut.class);
			
			result = daoFactory.getCelebrationDAO().DeleteCelebrationGuest(celebrationGuest);

		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Delete Celebration Guest failed.",ex));
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
}
