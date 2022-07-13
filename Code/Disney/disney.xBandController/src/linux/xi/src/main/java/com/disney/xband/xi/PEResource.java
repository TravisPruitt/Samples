package com.disney.xband.xi;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 4/30/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */

import com.disney.xband.xi.model.PEDAO;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/PEEvents")
public class PEResource extends XiResource {

    PEDAO pedao = null;
    Logger logger = Logger.getRootLogger();

    public PEResource()
    {
        pedao = new PEDAO();
    }

    @Path("/test")
    @GET
    @Produces("text/javascript")
    public String testPE()
    {
        return "This is a test endpoint and it works. Now with closing connections.";
    }

    // This path gets Reader Events for an individual park.
    @Path("/facility/{attraction}")
    @GET
    @Produces("text/javascript")
    public String GetAllPEEventsForPark(@PathParam("attraction") String attraction,
                                        @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {

        try
        {
           return pedao.GetPEReaderEventsForPark(attraction, cacheBuster);
        }
        catch (Exception e)
        {

            logger.error(e.getLocalizedMessage());
            return pedao.errorMessage("Bad value in return set.", "GetAllPEEventsForPark", cacheBuster);
        }

    }

    // This path gets Reader Events for an individual park.
    @Path("/facilityios/{attraction}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String GetAllPEEventsForParkIOS(@PathParam("attraction") String attraction,
                                        @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {

        try
        {
            return pedao.GetPEReaderEventsForParkIOS(attraction, cacheBuster);
        }
        catch (Exception e)
        {

            logger.error(e.getLocalizedMessage());
            return pedao.errorMessage("Bad value in return set.", "GetAllPEEventsForPark", cacheBuster);
        }

    }

    @Path("/PE/totals")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String GetPEEventTotals(@PathParam("attraction") String attraction, @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        try
        {
            return pedao.GetEventTotalsForPark(attraction, cacheBuster);
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            return pedao.errorMessage("Bad value in return set.", "GetAllPEEventsForPark", cacheBuster);
        }
    }

    // This path gets all of the attractions - if the attraction ID is 0, expect all attractions.
    @Path("/facilities")
    @GET
    @Produces("text/javascript")
    public String GetAllPEEventsForAllPark(@DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        String attraction = "0";

        try
        {
            return GetAllPEEventsForPark(attraction, cacheBuster);
        }
        catch (Exception e)
        {

            logger.error(e.getLocalizedMessage());
            return pedao.errorMessage("Bad value in return set.", "GetAllPEEventsForAllPark", cacheBuster);
        }

    }


}
