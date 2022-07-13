package com.disney.xband.idms.web;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.disney.xband.idms.lib.model.Guest;
import com.disney.xband.idms.lib.model.GuestEmailPut;
import com.disney.xband.idms.lib.model.GuestIdentifierCollection;
import com.disney.xband.idms.lib.model.GuestIdentifierPut;
import com.disney.xband.idms.lib.model.GuestLocatorCollection;
import com.disney.xband.idms.lib.model.GuestName;
import com.disney.xband.idms.lib.model.GuestProfile;
import com.disney.xband.idms.lib.model.GuestSearchCollection;
import com.disney.xband.idms.lib.model.Party;
import com.disney.xband.idms.lib.model.XBandCollection;
import com.disney.xband.idms.performance.GuestPerformanceMetricType;
import com.disney.xband.idms.performance.Metrics;
import com.sun.jersey.api.JResponse;

@Path("/guest")
public class OneViewGuestView
{

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	private static Logger logger = Logger.getLogger(OneViewGuestView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@GET
	@Path("/{id}/xbands")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<XBandCollection> getGuestXBandsByIDType(
			@PathParam("id") PathSegment pathSegment)
	{
		Date startTime = new Date();

		XBandCollection xbands = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			xbands = daoFactory.getXBandDAO().GetXBands(
					parameter.getIdentifierType(),
					parameter.getIdentifierValue());

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetXBandsByIdentifier);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get xBands by identifier.",
					ex));
			throw new WebApplicationException(500);
		}

		if (xbands == null)
		{
			throw new WebApplicationException(404);

		}
		else
		{
			return JResponse.ok(xbands).build();
		}
	}

	@GET
	@Path("/locators")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestLocatorCollection> getGuestLocators()
	{
		Date startTime = new Date();

		GuestLocatorCollection guestLocators = null;

		try
		{

			guestLocators = daoFactory.getGuestDAO().GetGuestLocators();

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestLocators);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Guest Locators.",
					ex));
			throw new WebApplicationException(500);
		}

		if (guestLocators == null)
		{
			throw new WebApplicationException(404);

		}
		else
		{
			return JResponse.ok(guestLocators).build();
		}
	}

	@GET
	@Path("/{id}/identifiers")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestIdentifierCollection> GetGuestIdentifiers(
			@PathParam("id") PathSegment pathSegment)
	{
		Date startTime = new Date();

		GuestData guest = null;
		GuestIdentifierCollection guestIdentifiers = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			guest = daoFactory.getGuestDAO().GetGuest(
					parameter.getIdentifierType(),
					parameter.getIdentifierValue());
			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestIdentifiers);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Get Guest Identifiers failed.", ex));
			throw new WebApplicationException(500);
		}

		if (guest == null)
		{
			throw new WebApplicationException(404);
		}

		guestIdentifiers = new GuestIdentifierCollection(guest);

		return JResponse.ok(guestIdentifiers).build();
	}

	/*
	 * @GET
	 * 
	 * @Path("/{id}/celebrations")
	 * 
	 * @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"}) public
	 * JResponse<GuestCelebrationCollection>
	 * getGuestCelebrations(@PathParam("id") PathSegment pathSegment) { Date
	 * startTime = new Date();
	 * 
	 * GuestCelebrationCollection guestCelebrations = null;
	 * 
	 * try { IdentifierParameter parameter = new
	 * IdentifierParameter(pathSegment);
	 * 
	 * guestCelebrations = daoFactory.getCelebrationDAO().GetCelebrations(
	 * parameter.getIdentifierType(), parameter.getIdentifierValue());
	 * 
	 * Metrics.INSTANCE.UpdateGuestMetric(startTime,
	 * GuestPerformanceMetricType.GetGuestCelebrations); } catch (Exception ex)
	 * {
	 * logger.error(ExceptionFormatter.format("Get Guest Celebrations failed.",
	 * ex)); throw new WebApplicationException(500); }
	 * 
	 * if (guestCelebrations == null) { throw new WebApplicationException(404);
	 * 
	 * } else { return JResponse.ok(guestCelebrations).build(); }
	 * 
	 * }
	 */

	@PUT
	@Path("/{id}/identifiers")
	@Consumes(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public Response putGuestIdentifier(
			@PathParam("id") PathSegment pathSegment, String identifier)
	{
		Date startTime = new Date();

		boolean result = false;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			ObjectMapper om = new ObjectMapper();
			GuestIdentifierPut id = om.readValue(identifier,
					GuestIdentifierPut.class);

			result = daoFactory.getGuestDAO().SaveGuestIdentifier(
					parameter.getIdentifierType(),
					parameter.getIdentifierValue(), id);
			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.PutGuestIdentifier);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Put Guest Identifier failed.", ex));
			throw new WebApplicationException(400);
		}

		if (result)
		{
			return Response.ok().build();
		}
		else
		{
			throw new WebApplicationException(404);
		}
	}

	@POST
	@Path("/{id}/identifiers")
	@Consumes(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public Response postGuestIdentifier(
			@PathParam("id") PathSegment pathSegment, String identifier)
	{
		Date startTime = new Date();

		boolean result = false;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			ObjectMapper om = new ObjectMapper();
			GuestIdentifierPut id = om.readValue(identifier,
					GuestIdentifierPut.class);

			result = daoFactory.getGuestDAO().SaveGuestIdentifier(
					parameter.getIdentifierType(),
					parameter.getIdentifierValue(), id);
			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.PutGuestIdentifier);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Post Guest Identifier failed.", ex));
			throw new WebApplicationException(400);
		}

		if (result)
		{
			return Response.status(Status.CREATED).build();
		}
		else
		{
			throw new WebApplicationException(404);
		}
	}

	@GET
	@Path("/{id}")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestProfile> GetGuest(
			@PathParam("id") PathSegment pathSegment)
	{
		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);
			return GetGuestProfile(parameter.getIdentifierType(),
					parameter.getIdentifierValue());
		}
		catch (WebApplicationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Guest failed.", ex));
			throw new WebApplicationException(500);
		}
	}

	private JResponse<GuestProfile> GetGuestProfile(String identifierType,
			String identifierValue)
	{
		Date startTime = new Date();
		GuestData guest = null;

		try
		{
			guest = daoFactory.getGuestDAO().GetGuest(identifierType,
					identifierValue);

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestProfileById);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Get Guest Profile by ID failed.", ex));
			throw new WebApplicationException(500);
		}

		if (guest == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(guest.getGuestProfile()).build();
	}

	@GET
	@Path("/{id}/online-profile")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestProfile> GetGuestOnlineProfile(
			@PathParam("id") PathSegment pathSegment)
	{
		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);
			return GetGuestProfile(parameter.getIdentifierType(),
					parameter.getIdentifierValue());
		}
		catch (WebApplicationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Get Guest Online Profile failed.", ex));
			throw new WebApplicationException(500);
		}
	}

	@GET
	@Path("/{id}/profile")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestProfile> GetGuestProfile(
			@PathParam("id") PathSegment pathSegment)
	{
		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);
			return GetGuestProfile(parameter.getIdentifierType(),
					parameter.getIdentifierValue());
		}
		catch (WebApplicationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Guest Profile failed.",
					ex));
			throw new WebApplicationException(500);
		}
	}

	@GET
	@Path("/{id}/visit/current")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<Party> GetOneViewParty(
			@PathParam("id") PathSegment pathSegment)
	{
		Date startTime = new Date();

		Party party = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			party = daoFactory.getPartyDao().getParty(
					parameter.getIdentifierType(),
					parameter.getIdentifierValue());
			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetOneViewParty);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get OnePartyView failed.",
					ex));
			throw new WebApplicationException(400);
		}

		if (party == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(party).build();

	}

	@GET
	@Path("/search/{searchName}")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestSearchCollection> SearchByName(
			@PathParam("searchName") String searchName)
	{
		Date startTime = new Date();

		GuestSearchCollection retVal = null;

		try
		{
			retVal = daoFactory.getGuestDAO().SearchGuestByName(searchName);
			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.SearchByName);

		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
			throw new WebApplicationException(500);
		}

		if (retVal == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(retVal).build();

	}

	@GET
	@Path("/searchEmail/{email}")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<Guest> GetGuestByEmail(@PathParam("email") String email)
	{
		Date startTime = new Date();

		Guest guest = null;

		try
		{
			GuestData guestData = daoFactory.getGuestDAO().GetGuestByEmail(email);
			
			if(guestData != null)
			{
				guest = guestData.getGuest();
			}

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestByEmail);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Get Guest by Email failed.", ex));
			throw new WebApplicationException(500);
		}

		if (guest == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(guest).build();
	}

	@GET
	@Path("/name/{id}")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<GuestName> GetGuestName(
			@PathParam("id") PathSegment pathSegment)
	{
		Date startTime = new Date();

		GuestName guestName = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			guestName = daoFactory
					.getGuestDAO()
					.GetGuest(parameter.getIdentifierType(),
							parameter.getIdentifierValue()).getGuestProfile()
					.getName();

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestNameById);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter
					.format("Get Guest Name failed.", ex));
			throw new WebApplicationException(500);
		}

		return JResponse.ok(guestName).build();
	}

	@POST
	@Consumes(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public Response SaveGuest(String guestString)
	{
		Date startTime = new Date();

		URI location = null;
		long guestId = 0;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			Guest guest = om.readValue(guestString, Guest.class);

			guestId = daoFactory.getGuestDAO().SaveOneViewGuest(guest);

			location = new URI("/" + guestId);

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.SaveGuest);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Save Guest failed.", ex));
			throw new WebApplicationException(500);
		}

		if (guestId == 0)
		{
			throw new WebApplicationException(400);
		}

		return Response.created(location).build();

	}

	@PUT
	@Path("/email")
	@Consumes(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public Response UpdateGuestEmail(String guestEmailPutString)
	{
		Date startTime = new Date();

		boolean retVal = false;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{
			ObjectMapper om = new ObjectMapper();
			GuestEmailPut emp = om.readValue(guestEmailPutString,
					GuestEmailPut.class);

			retVal = daoFactory.getGuestDAO().UpdateGuestEmail(emp);

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.UpdateGuestEmail);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Update Guest Email failed.", ex));
			throw new WebApplicationException(500);
		}

		if (!retVal)
		{
			throw new WebApplicationException(404);
		}

		return Response.status(Status.NO_CONTENT).build();

	}

	@PUT
	@Consumes(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public Response updateGuest(String guestString)
	{
		Date startTime = new Date();
		boolean result = false;

		if (ConfigurationSettings.INSTANCE.isReadOnly())
			throw new ReadOnlyException();

		try
		{

			ObjectMapper om = new ObjectMapper();
			Guest guest = om.readValue(guestString, Guest.class);

			result = daoFactory.getGuestDAO().UpdateGuest(guest);

			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.UpdateGuest);

		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Update Guest failed.", ex));
			throw new WebApplicationException(400);
		}

		if (!result)
		{
			throw new WebApplicationException(400);
		}

		return Response.status(Status.NO_CONTENT).build();
	}

}
