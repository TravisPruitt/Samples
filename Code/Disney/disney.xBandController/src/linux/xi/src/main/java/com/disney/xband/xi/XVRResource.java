package com.disney.xband.xi;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 7/25/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */


import com.disney.xband.xi.model.XVRDAO;

import javax.ws.rs.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.text.SimpleDateFormat;


@Path("/xvr")
public class XVRResource extends XiResource {

    public XVRDAO dao = new XVRDAO();

    @Path("/test")
    @GET
    @Produces("text/javascript")
    public String TestEndPoint()
    {
        return  "The xVR Endpoints are working.";
    }

    @Path("/preheat/{attractionId}/start/{startTime}")
    @GET
    @Produces("text/javascript")
    public String GetEventsToPreHeatAttraction(@PathParam("attractionId") String attractionId, @PathParam("startTime") String startTime, @QueryParam("buster") String cacheBuster)
    {
        long facilityId = 0;

        Date startDateTime = new Date();


        try
        {
            facilityId = Long.valueOf(attractionId);
            startDateTime = parseCurrentDateUtil(startTime);

        }
        catch (Exception ex)
        {
             logger.error(ex);
        }

        return dao.GetEventsToPreHeatAttraction(startDateTime, facilityId, cacheBuster);


    }


    @Path("/attraction/{attractionId}/events/{startTime}/{endTime}")
    @GET
    @Produces("text/javascript")
    public String GetEventsForAttraction(@PathParam("attractionId") String attractionId, @PathParam("startTime") String startTime, @PathParam("endTime") String endTime, String cacheBuster)
    {
        long facilityId = 0;
        Date startDateTime = new Date();
        Date endDateTime = new Date();

        try
        {
            facilityId = Long.valueOf(attractionId);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            Date sDate =  df.parse(startTime);
            Date eDate = df.parse(endTime);

            startDateTime = sDate;
            endDateTime = eDate;

        }
        catch (Exception ex)
        {
            logger.error(ex);
        }

        return dao.GetEventsForAttraction(startDateTime, endDateTime, facilityId, cacheBuster);

    }



}
