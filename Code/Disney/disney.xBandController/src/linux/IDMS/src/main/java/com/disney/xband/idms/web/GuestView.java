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

import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.model.xview.Guest;
import com.disney.xband.idms.performance.Metrics;
import com.disney.xband.idms.performance.XviewPerformanceMetricType;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.sun.jersey.api.JResponse;

@Path("/guests")
public class GuestView {
	
	private static Logger logger = Logger.getLogger(GuestView.class);
	private static DAOFactory daoFactory = DAOFactory.getDAOFactory();
	
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/id/{id}/")
	public JResponse<Guest> GetGuestByIdOld(@PathParam("id") String guestId)
	{
		return GetGuest(guestId);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	@Path("/{id}/")
	public JResponse<Guest> GetGuestById(@PathParam("id") String guestId) throws Exception
	{		
		return GetGuest(guestId);
	}	

	private JResponse<Guest> GetGuest(String guestId)
	{
        if(!isAllDigits(guestId)) {
            logger.error("Incorrect /guests/id endpoint usage. ID must be numeric.");
            throw new WebApplicationException(500);
        }

		Date startTime = new Date();
		Guest guest = null;
		
		try
		{
			GuestData guestData = daoFactory.getGuestDAO().GetGuest("guestId", guestId);
			
			if(guestData != null)
			{
				guest = guestData.getXviewGuest();
			}

			Metrics.INSTANCE.UpdateXviewMetric(startTime, 
					XviewPerformanceMetricType.GetGuestByIdOld);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get xView Guest failed.",ex));
			throw new WebApplicationException(500);
		}
		
		if (guest == null)
		{
			throw new WebApplicationException(404);
		}
		
		return JResponse.ok(guest).build();
	}

    static private boolean isAllDigits(String s) {
        if((s == null) || (s.length() == 0)) {
            return false;
        }

        for(char c : s.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
