package com.disney.xband.xi;

import com.disney.xband.xi.model.BlueLaneDAO;
import com.disney.xband.xi.model.DateUtil;

import javax.ws.rs.*;
import java.text.ParseException;
import java.util.Date;

@Path("/bluelane")
public class BlueLaneResource extends XiResource
{
	BlueLaneDAO bd = null;

	public BlueLaneResource()
	{
		this.bd = new BlueLaneDAO();
	}

	@Path("/{park}/today")
	@GET
    @Produces("text/javascript")
	public String allAttractionsToday(@PathParam("park") int parkId,
                                      @DefaultValue("") @QueryParam("currentdate") String useDate,
                                       @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return bd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "bluelane/today", cacheBuster);
        }
        return bd.getAllBlueLane(parkId, DateUtil.setDayStartForDate(dateObj), dateObj, "today", cacheBuster);
    }
	
	@Path("/{park}/yesterday")
	@GET
    @Produces("text/javascript")
	public String allAttractionsYesterday(@PathParam("park") int parkId,
                                          @DefaultValue("") @QueryParam("currentdate") String useDate,
                                          @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {

        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return bd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "bluelane/yday", cacheBuster);
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return bd.getAllBlueLane(parkId, yesterdayTimeRange[0], yesterdayTimeRange[1], "yesterday", cacheBuster);

	}
	@Path("/{park}/todate")
	@GET
    @Produces("text/javascript")
	public String getAttractionsAll(@PathParam("park") int parkId,
                                    @DefaultValue("") @QueryParam("currentdate") String useDate,
                                    @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return bd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "bluelane/yday", cacheBuster);
        }
        return bd.getAllBlueLane(parkId, DateUtil.dateAdd(dateObj, getWindowStartDelta()), dateObj, "today", cacheBuster);
	}

	@Path("/{park}/{date}")
	@GET
    @Produces("text/javascript")
	public String allAttractions(@PathParam("park") int parkId,
                                 @PathParam("date") String useDate,
                                 @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return bd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "bluelane:" + useDate, cacheBuster);
        }
        return bd.getAllBlueLane(parkId, DateUtil.setDayStartForDate(dateObj), DateUtil.setDayEndForDate(dateObj), useDate, cacheBuster);

	}

	@Path("/reasoncodes/{attractionId}/")
	@GET
    @Produces("text/javascript")
	public String getBLAttractionReasonCodes(@PathParam("attractionId") int attractionId,
                                             @DefaultValue("") @QueryParam("date") String useDate,
                                             @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return bd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today", cacheBuster);
        }

        return bd.getAttractionReasonCodes(attractionId, DateUtil.setDayStartForDate(dateObj), dateObj,
                    useDate, cacheBuster);
	}


}
