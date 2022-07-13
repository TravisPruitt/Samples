package com.disney.xband.xi;

import com.disney.xband.xi.model.QueueDAO;

import javax.ws.rs.*;

import java.text.ParseException;
import java.util.Date;

@Path("/queueresource")
public class QueueResource extends XiResource
{
	QueueDAO dao = null;

	public QueueResource()
	{
		dao = new QueueDAO();
	}

	// who's in queue at {time} on {date}
    @Path("/{park}/now")
	@GET
	@Produces("text/javascript")
	public String getCurrentAttractionQueues(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                             @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return dao.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "queueresource/today", cacheBuster);
        }
        return dao.getQueueCountBreakdown(parkId, dateObj, useDate, cacheBuster);
    }

    @Path("/subway/{attractionid}")
    @GET
    @Produces("text/javascript")
    public String getCurrentAttractionQueuesForSubway (
            @PathParam("attractionid") String attractionId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return dao.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway attr", cacheBuster);
        }
        return dao.getSubwayQueueCount(attractionId, dateObj, useDate, cacheBuster);
	}

    @Path("/subway/guests/{attractionid}")
    @GET
    @Produces("text/javascript")
    public String getSubwayGuestsForAttraction (
            @PathParam("attractionid") String attractionId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error("bad date: " + useDate + ", ex:" + e.getLocalizedMessage());
            return dao.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway get guests attr", cacheBuster);
        }
        return dao.getSubwayGuestsForAttraction(attractionId, dateObj, useDate, cacheBuster);
	}



    /*
    @Path("/subway/diagram/save/{attractionid}/{subwaydata}")
    @GET
    @Produces("text/javascript")
    public String setSubwayDiagramForAttraction (
            @PathParam("attractionid") String attractionId,
            @PathParam("subwaydata") String subwayData,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        if(dao.getFacilityById(attractionId) == null) {
            String errMsg = "Invalid facility id: " + attractionId;
            logger.error(errMsg);
            return dao.errorMessage(errMsg);
        }
        return dao.setSubwayDiagram(attractionId, subwayData);
    }

    @Path("/subway/diagram/id/{rowid}")
    @GET
    @Produces("text/javascript")
    public String getSubwayDiagramById (@PathParam("attractionid") int rowId,
                                        @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        return dao.getSubwayDiagramById(rowId);
    }

    @Path("/subway/diagram/{attractionid}")
    @GET
    @Produces("text/javascript")
    public String getSubwayDiagramForAttraction (@PathParam("attractionid") String attractionId)
    {
        if(dao.getFacilityById(attractionId) == null) {
            String errMsg = "Invalid facility id: " + attractionId;
            logger.error(errMsg);
            return dao.errorMessage(errMsg);
        }
        return dao.getSubwayDiagram(attractionId);
    }

    @Path("/subway/diagram/list/{attractionid}")
    @GET
    @Produces("text/javascript")
    public String getSubwayDiagramListForAttraction (@PathParam("attractionid") String attractionId)
    {
        if(dao.getFacilityById(attractionId) == null) {
            String errMsg = "Invalid facility id: " + attractionId;
            logger.error(errMsg);
            return dao.errorMessage(errMsg);
        }
        return dao.getSubwayDiagramList(attractionId);
    }
    */
}
