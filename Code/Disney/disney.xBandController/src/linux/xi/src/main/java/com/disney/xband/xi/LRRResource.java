package com.disney.xband.xi;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 6/4/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */

import com.disney.xband.xi.model.LRRDAO;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/lrr")
public class LRRResource extends XiResource {

    LRRDAO lrrdao = null;
    Logger logger = Logger.getRootLogger();


    public LRRResource()
    {
        lrrdao = new LRRDAO();
    }

    @Path("/test")
    @GET
    @Produces("text/javascript")
    public String test()
    {
        return ("LRR Test works correctly.");
    }


    @Path("/tieout/{parkId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String GetLRRResultsForPark(@PathParam("parkId") String parkId, @DefaultValue("") @QueryParam("buster") String cacheBuster )
    {
        try
        {
            return lrrdao.GetLRREventsForPark(parkId, cacheBuster);
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            return lrrdao.errorMessage("Bad value in return set.", "GetLRRResultsForPark", cacheBuster);
        }
    }

    @Path("/queuecounts/{parkId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String GetSubwayQueueCountForAttractions(@PathParam("parkId") String parkId, @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        try
        {
           return lrrdao.GetSubwayQueueCountForAttractions(parkId, cacheBuster);
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            return lrrdao.errorMessage("Bad value in return set.", "GetSubwayQueueCountForAttractions", cacheBuster);
        }
    }



}
