package com.disney.xband.xi;

import com.disney.xband.xi.model.DateUtil;
import com.disney.xband.xi.model.ExecSumDAO;

import javax.ws.rs.*;
import java.text.ParseException;
import java.util.Date;

@Path("/execsummary")
public class ExecutiveSummaryResource extends XiResource
{
	ExecSumDAO esd = new ExecSumDAO();

	public ExecutiveSummaryResource()
	{
	}

	@Path("/{park}/todate")
	@GET
    @Produces("text/javascript")
	public String getExecSummaryToDate(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                       @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/todate", cacheBuster);
        }
        // set the date to the default -- today
		return esd.getExecSummaryTodate(parkId, "todate", DateUtil.dateAdd(dateObj, getWindowStartDelta()), dateObj, cacheBuster);
	}

	@Path("/cal/{park}")
	@GET
    @Produces("text/javascript")
	public String getCalendarToday(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                   @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "cal/today", cacheBuster);
        }
        int windowEndOffset=esd.getWindowOffsetEnd();
        dateObj = DateUtil.dateAdd(dateObj,windowEndOffset);
		return esd.getCalendar(parkId, dateObj, windowEndOffset + Math.abs(esd.getWindowOffsetStart()), cacheBuster);
	}

	@Path("/cal/{park}/{date}")
	@GET
    @Produces("text/javascript")
	public String getCalendarForDate(@PathParam("park") int parkId, @PathParam("date") String sDate,
                                     @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        try {
            return esd.getCalendar(parkId, DateUtil.dateformatter.parse(sDate), 45, cacheBuster);
        } catch (ParseException dao) {
            logger.error(dao.getLocalizedMessage());
            return esd.errorMessage(dao.getMessage(), "cal/date", cacheBuster);
        }
	}

	@Path("/now")
	@GET
    @Produces("text/javascript")
    public String getExecSummaryNowDefault(@DefaultValue("") @QueryParam("currentdate") String useDate,
                                    @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today", cacheBuster);
        }
        return esd.getCurrentExecSummary(80007944, dateObj, "now", cacheBuster);
	}

	@Path("/{park}/now")
	@GET
    @Produces("text/javascript")
    public String getExecSummaryNow(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                    @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today", cacheBuster);
        }
        return esd.getCurrentExecSummary(parkId, dateObj, "now", cacheBuster);
	}

	@Path("/{park}/today")
	@GET
    @Produces("text/javascript")
    public String getExecSummaryToday(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                      @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today", cacheBuster);
        }
        return esd.getCurrentExecSummary(parkId, dateObj, "today", cacheBuster);
	}

	@Path("/{park}/yesterday")
	@GET
    @Produces("text/javascript")
	public String getExecSummaryYesterday(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                          @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/yday", cacheBuster);
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return esd.getExecSummary(parkId, "yesterday", yesterdayTimeRange[0], yesterdayTimeRange[1], cacheBuster);
	}

	@Path("/{park}/{date}")
	@GET
    @Produces("text/javascript")
	public String getExecSummary(@PathParam("park") int parkId, @PathParam("date") String useDate,
                                 @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/date", cacheBuster);
        }
        return esd.getExecSummary(parkId, useDate, DateUtil.setDayStartForDate(dateObj), DateUtil.setDayEndForDate(dateObj), cacheBuster);
	}

    @Path("/redemption/{park}/today")
    @GET
    @Produces("text/javascript")
    public String getHourlyRedemptionTotalToday(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                                @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today", cacheBuster);
        }
        return esd.getHourlyRedemptionTotal(parkId, DateUtil.setDayStartForDate(dateObj), dateObj, "today", cacheBuster);
    }

    @Path("/redemption/{park}/yesterday")
    @GET
    @Produces("text/javascript")
    public String getHourlyRedemptionTotalYesterday(@PathParam("park") int parkId, @DefaultValue("") @QueryParam("currentdate") String useDate,
                                                    @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/today");
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return esd.getHourlyRedemptionTotal(parkId, yesterdayTimeRange[0], yesterdayTimeRange[1], "today", cacheBuster);
    }

	@Path("/redemption/{park}/{date}")
	@GET
    @Produces("text/javascript")
	public String getHourlyRedemptionTotalDate(@PathParam("park") int parkId, @PathParam("date") String useDate,
                                               @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "execsummary/date", cacheBuster);
        }
        return esd.getHourlyRedemptionTotal(parkId, DateUtil.setDayStartForDate(dateObj),
                DateUtil.setDayEndForDate(dateObj),
                "redemption/date", cacheBuster);
    }

    @Path("/waittimes/{parkId}")
    @GET
    @Produces("text/javascript")
    public String getWaitTimesByPark(@PathParam("parkId") String parkId, @QueryParam("buster") String cacheBuster)
    {
        try
        {
            return esd.getWaitTimesForPark(parkId, cacheBuster);
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            return esd.errorMessage("Bad Request ", "parkId", cacheBuster);
        }

    }

}