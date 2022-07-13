package com.disney.xband.xbms.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jms.lib.entity.xbms.Xband;
import com.disney.xband.xbms.dao.DAOFactory;
import com.sun.jersey.api.JResponse;

@Path("/xbms-rest/app/xband")
public class XbandView
{
	private static Logger logger = Logger.getLogger(XbandView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/{id}")
	public JResponse<Xband> getXBand(@PathParam("id") String id) throws Exception
	{
		Xband xband = null;
		
		try
		{
			xband = daoFactory.getXbandDAO().GetXband(id);
			
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Band lookup failed.",ex));
			throw new WebApplicationException(500);
		}
		
		if (xband == null)
		{
			throw new WebApplicationException(404);
		}

		return JResponse.ok(xband).build();
	}
}
