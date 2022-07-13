package com.disney.xband.idms.web;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.idms.performance.CelebrationPerformanceMetricType;
import com.disney.xband.idms.performance.Metrics;
import com.sun.jersey.api.JResponse;

@Path("/gxp/celebration")
public class FastPassTestCelebrationView
{

	private static Logger logger = Logger.getLogger(FastPassTestCelebrationView.class);
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
}
