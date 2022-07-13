package com.disney.xband.idms.web;

import java.util.Date;
import java.util.List;

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
import com.disney.xband.idms.lib.model.GuestAcquisition;
import com.disney.xband.idms.performance.GuestPerformanceMetricType;
import com.disney.xband.idms.performance.Metrics;
import com.sun.jersey.api.JResponse;

@Path("/v2/guest-acquisition")
public class GuestAcquisitionView
{
	private static Logger logger = Logger.getLogger(GuestView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();
	
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;
	

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<List<GuestAcquisition>> GetGuestAcquisitionByGuest(
			@QueryParam("guest-id-type") String guestIdType,
			@QueryParam("guest-id-value") String guestIdValue,
			@QueryParam("xbandRequestId") String xbandRequestId,
			@QueryParam("acquisition-id-type") String acquisitionIdType,
			@QueryParam("acquisition-id-value") String acquisitionIdValue) 
			throws WebApplicationException
	{		
		Date startTime = new Date();
		
		List<GuestAcquisition> result = null;

		try
		{
			if(guestIdType != null && guestIdValue != null)
			{
				result = daoFactory.getGuestDAO().GetGuestAcquisitionByGuest(
						guestIdType, guestIdValue);
	
				Metrics.INSTANCE.UpdateGuestMetric(startTime,
				GuestPerformanceMetricType.GetGuestAcquisitionByGuest);
			}
			else if(xbandRequestId != null)
			{
				result = daoFactory.getGuestDAO().GetGuestAcquisitionByXbandRequest(
						xbandRequestId);

				Metrics.INSTANCE.UpdateGuestMetric(startTime,
				GuestPerformanceMetricType.GetGuestAcquisitionByXbandRequest);
			}
			else if(acquisitionIdType != null && acquisitionIdValue != null)
			{
				result = daoFactory.getGuestDAO().GetGuestAcquisitionByAcquisition(
						acquisitionIdValue, acquisitionIdType);
		
				Metrics.INSTANCE.UpdateGuestMetric(startTime,
				GuestPerformanceMetricType.GetGuestAcquisitionByAcquisition);
				
			}
				
		}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Guest Acquistion data.",ex));
			throw new WebApplicationException(500);
		}

		if (result == null )
		{
			throw new WebApplicationException(404);
		}
		else if (result.size() == 0 )
		{
			throw new WebApplicationException(404);
		}
		else
		{
			return JResponse.ok(result).build();
		}
	}	
}
