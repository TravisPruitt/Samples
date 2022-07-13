package com.disney.xband.idms.web;

import java.util.Date;

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

import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.model.xview.Xband;
import com.disney.xband.idms.performance.Metrics;
import com.disney.xband.idms.performance.XviewPerformanceMetricType;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.sun.jersey.api.JResponse;

@Path("/xbands")
public class XBandView 
{
	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	private static Logger logger = Logger.getLogger(XBandView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/id/{id}")
	public JResponse<Xband> getXBandByXBandIdOld(@PathParam("id") String id) throws Exception
	{
		return this.GetXband(BandLookupType.XBANDID, id);
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/{id}")
	public JResponse<Xband> getXBandByXBandId(@PathParam("id") String id) throws Exception
	{
		return this.GetXband(BandLookupType.XBANDID, id);
	}
	

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/tapid/{id}")
	public JResponse<Xband> getXBandByTapId(@PathParam("id")String id)
	{
		return this.GetXband(BandLookupType.TAPID, id);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/lrid/{id}")
	public JResponse<Xband> getXBandBylrId(@PathParam("id")String id)
	{
		return this.GetXband(BandLookupType.LRID, id);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/bandid/{id}")
	public JResponse<Xband> getXBandByBandId(@PathParam("id")String id)
	{
		return this.GetXband(BandLookupType.BANDID, id);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/secureid/{id}")
	public JResponse<Xband> getXBandBySecureId(@PathParam("id") String id)
	{
		return this.GetXband(BandLookupType.SECUREID, id);
	}

	private JResponse<Xband> GetXband(BandLookupType lookupType, String id)
	{
		Date startTime = new Date();
		BandData bandData = null;
		Xband xband = null;
		
		try
		{
			bandData = daoFactory.getXBandDAO().GetBand(lookupType, id);
			if (bandData != null)
				xband = bandData.getxband();
			
			Metrics.INSTANCE.UpdateXviewMetric(startTime, 
					XviewPerformanceMetricType.GetXBandByTapId);
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
