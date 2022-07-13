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
import com.disney.xband.idms.dao.BandLookupType;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.data.GuestData;
import com.sun.jersey.api.JResponse;

@Path("/gxp/guests")
public class FastPassTestGuestsView
{
	@Context
	UriInfo uriInfo;

	@Context
	Request request;
	
	private static Logger logger = Logger.getLogger(FastPassTestGuestsView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();

	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<FastPassTestGuestsResult> GetGuestData(
			@QueryParam("entitlement-id-type") String entitlementIdType,
			@QueryParam("entitlement-id-value") String entitlementIdValue)
	{
		BandData bandData = null;
		GuestData guestData = null;
		
		try
		{
			if(entitlementIdType.compareToIgnoreCase("xband-secure-id") == 0)
			{
				bandData = daoFactory.getXBandDAO().GetBand(BandLookupType.SECUREID, entitlementIdValue);
				
				if(bandData.getGuest() != null)
				{
					guestData = daoFactory.getGuestDAO().GetGuest("guestId", 
							String.valueOf(bandData.getGuest().getGuestId()));
				}
			}
		} 
		catch (Exception ex) 
		{
			logger.error(ExceptionFormatter.format("Get Guest Data.",ex));
			throw new WebApplicationException(500);
		}

		if (bandData == null)
		{
			throw new WebApplicationException(404);

		}
		else
		{
			if(guestData != null)
			{
				FastPassTestGuestsResult result = new FastPassTestGuestsResult();
				
				result.addGuest(guestData);

				return JResponse.ok(result).build();
			}
			else
			{
				throw new WebApplicationException(404);

			}
		}
	}
}
