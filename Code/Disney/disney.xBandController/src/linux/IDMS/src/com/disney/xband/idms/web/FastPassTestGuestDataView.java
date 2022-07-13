package com.disney.xband.idms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.model.oneview.GuestDataResult;
import com.sun.jersey.api.JResponse;

@Path("/gxp/guest-data")
public class FastPassTestGuestDataView 
{
	
	@Context
	UriInfo uriInfo;

	@Context
	Request request;
	
	private static Logger logger = Logger.getLogger(FastPassTestGuestDataView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<GuestDataResult> GetGuestData(
			@QueryParam("guest-id-type") String guestIdType,
			@QueryParam("guest-id-value") String guestIdValue,
			@QueryParam("start-date-time") String startDateTime,
			@QueryParam("end-date-time") String endDateTime)
	{
		GuestDataResult guestData = null;

		try
		{
			
			guestData  = daoFactory.getGuestDAO().GetGuestData(guestIdType, guestIdValue, startDateTime, endDateTime);	
		} 
		catch (Exception ex) 
		{
			logger.error(ExceptionFormatter.format("Get Guest Data.",ex));
			throw new WebApplicationException(500);
		}

		if (guestData == null)
		{
			throw new WebApplicationException(404);

		}
		else
		{
			return JResponse.ok(guestData).build();
		}
	}

}
