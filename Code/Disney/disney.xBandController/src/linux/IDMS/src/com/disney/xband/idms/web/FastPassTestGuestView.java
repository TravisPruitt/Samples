package com.disney.xband.idms.web;

import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.data.GuestDataIdentifier;
import com.disney.xband.idms.lib.model.ConfigurationSettings;
import com.disney.xband.idms.lib.model.GuestIdentifierCollection;
import com.disney.xband.idms.lib.model.GuestIdentifierPut;
import com.disney.xband.idms.performance.GuestPerformanceMetricType;
import com.disney.xband.idms.performance.Metrics;
import com.sun.jersey.api.JResponse;

@Path("gxp/guest")
public class FastPassTestGuestView
{

	private static Logger logger = Logger.getLogger(FastPassTestGuestView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

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
	@Path("/{id}/profile")
	@Produces(
	{ MediaType.APPLICATION_JSON + ";charset=utf-8" })
	public JResponse<FastPassTestGuestProfile> GetGuestProfile(
			@PathParam("id") PathSegment pathSegment)
	{
		Date startTime = new Date();

		GuestData guest = null;

		try
		{
			IdentifierParameter parameter = new IdentifierParameter(pathSegment);

			guest = daoFactory.getGuestDAO().GetGuest(parameter.getIdentifierType(),
					parameter.getIdentifierValue());


			Metrics.INSTANCE.UpdateGuestMetric(startTime,
					GuestPerformanceMetricType.GetGuestProfileById);
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Guest Profile failed.",
					ex));
			throw new WebApplicationException(500);
		}
		
		if(guest == null)
		{
			throw new WebApplicationException(404);
			
		}
		
		FastPassTestGuestProfile guestProfile = new FastPassTestGuestProfile();
		guestProfile.setGuestId(guest.getGuestId());
		guestProfile.setStatus(guest.getStatus());
		guestProfile.setEmailAddress(guest.getEmailAddress());
		guestProfile.setParentEmail(guest.getParentEmail());
		
		guestProfile.setFirstName(guest.getFirstName());
		guestProfile.setMiddleName(guest.getMiddleName());
		guestProfile.setLastName(guest.getLastName());
		guestProfile.setTitle(guest.getTitle());
		guestProfile.setSuffix(guest.getSuffix());
		
		guestProfile.setGender(guest.getGender());
		guestProfile.setGuestType(guest.getIDMSTypeName());
		
		ArrayList<com.disney.xband.idms.lib.model.XBand> xbands =  new ArrayList<com.disney.xband.idms.lib.model.XBand>(0);
		if (guest.getBandList() != null)
		{
			xbands.ensureCapacity(guest.getBandList().size());
			for (BandData band : guest.getBandList())
			{
				xbands.add(band.getXBand());
			}
		}
		guestProfile.setXBands(xbands);
		
		ArrayList<FastPassTestGuestIdentifier> identifiers = new ArrayList<FastPassTestGuestIdentifier>();
		if (guest.getIdentifierList() != null)
		{
			identifiers.ensureCapacity(guest.getIdentifierList().size());
			for (GuestDataIdentifier identifier : guest.getIdentifierList())
			{
				FastPassTestGuestIdentifier guestIdentifier = new FastPassTestGuestIdentifier();
				guestIdentifier.setType(identifier.getType());
				guestIdentifier.setValue(identifier.getValue());
				identifiers.add(guestIdentifier);
			}
		}
		guestProfile.setIdentifiers(identifiers);

		return JResponse.ok(guestProfile).build();

	}
}
