package com.disney.xband.xi;

import com.disney.xband.xi.model.DateUtil;
import com.disney.xband.xi.model.EntitlementDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Date;

@Path("/entitlements")
public class EntitlementResource extends XiResource
{
	EntitlementDAO ed = new EntitlementDAO();

	public EntitlementResource()
	{

	}

    /*
	@Path("/all/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummaryAll(@DefaultValue("") @QueryParam("currentdate") String useDate)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "entitlesummary/todate");
        }
        return ed.getEntitlementAll(DateUtil.setDayStartForDate(dateObj), dateObj, "today");
	}


	@Path("/all/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummaryAllYesterday(@DefaultValue("") @QueryParam("currentdate") String useDate)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "entitlesummary/todate");
        }

        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return ed.getEntitlementAll(yesterdayTimeRange[0], yesterdayTimeRange[1], "yesterday");
	}

    @Path("/all/{date}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getEntitleAllForDate(@PathParam("date") String useDate)
    {
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-dd", "entitlesummary/date");
        }
        return ed.getEntitlementAll(DateUtil.setDayStartForDate(dateObj), DateUtil.setDayEndForDate(dateObj), useDate);
    }*/

	@Path("/{park}/todate")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummary( @PathParam("park") int parkId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "entitlesummary/todate", cacheBuster);
        }
		return ed.getEntitlementSummary(parkId, DateUtil.dateAdd(dateObj, getWindowStartDelta()), dateObj, "todate", cacheBuster);
	}

	@Path("/{park}/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummaryYesterday( @PathParam("park") int parkId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "entitlesummary/yday", cacheBuster);
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return ed.getEntitlementSummary(parkId, yesterdayTimeRange[0], yesterdayTimeRange[1], "yesterday", cacheBuster);
	}
	
	@Path("/{park}/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummaryToday( @PathParam("park") int parkId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "entitlesummary/today", cacheBuster);
        }
        return ed.getEntitlementSummary(parkId, DateUtil.setDayStartForDate(dateObj), dateObj, "today", cacheBuster );
	}
	
	@Path("/{park}/{date}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getEntitleSummaryForDate(
            @PathParam("park") int parkId,
            @PathParam("date") String useDate,
                                           @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-dd", "entitlesummary/date", cacheBuster);
        }
        return ed.getEntitlementSummary(parkId, DateUtil.setDayStartForDate(dateObj),
                DateUtil.setDayEndForDate(dateObj), useDate, cacheBuster);
	}

	@Path("/facility/{attraction}/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String showAttractionSummaryToday(
			@PathParam("attraction") int attraction,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "showattrsummary/today", cacheBuster);
        }

        return ed.getAttractionSummary(attraction, DateUtil.setDayStartForDate(dateObj), dateObj, "today", cacheBuster );
	}
	
	@Path("/facility/{facilityid}/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String showAttractionSummaryYesterday(
			@PathParam("facilityid") int facilityId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "showattrsummary/yday", cacheBuster);
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);

        return ed.getAttractionSummary(facilityId, yesterdayTimeRange[0], yesterdayTimeRange[1], "yesterday", cacheBuster );
	}

	
	@Path("/facility/{facilityid}/todate")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String showAttractionSummaryToDate(
			@PathParam("facilityid") int facilityId,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "showattrsummary/today", cacheBuster);
        }

        return ed.getAttractionSummary(facilityId, DateUtil.dateAdd(dateObj, getWindowStartDelta()), dateObj, "yesterday", cacheBuster);
	}

	
	@Path("/facility/{attraction}/{date}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String showAttractionSummary(
			@PathParam("attraction") int attraction,
			@PathParam("date") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return ed.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "showattrsummary/date", cacheBuster);
        }
        return ed.getAttractionSummary(attraction, DateUtil.setDayStartForDate(dateObj),
                DateUtil.setDayEndForDate(dateObj), useDate, cacheBuster);
	}
}
