package com.disney.xband.xi;

import com.disney.xband.xi.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

@Path("/guests")
public class GuestResource extends XiResource
{
	GuestDAO gd = null;
	Logger logger = Logger.getRootLogger();

	public GuestResource()
	{
		this.gd = new GuestDAO();
	}

	@Path("/facility/{attraction}/{date}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String allGuestsByAttraction(
			@PathParam("attraction") String attraction,
			@PathParam("date") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "facility attr date");
        }
        //return gd.getGuestListForAttraction(attraction, DateUtil.setDayStartForDate(dateObj), DateUtil.setDayEndForDate(dateObj), useDate, cacheBuster);
        return gd.getGuestListForEntitlementFacilityView(attraction, DateUtil.setDayEndForDate(dateObj), useDate, cacheBuster);
	}
	
	@Path("/facility/{attraction}/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String allGuestsByAttractionToday( @PathParam("attraction") String attraction,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            if(useDate.length() == 0) {
                dateObj = DateUtil.getCurrentDate();
            }
            else {
                dateObj=DateUtil.tstzformatter.parse(useDate);
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "facility attr /today");
        }
        //return gd.getGuestListForAttraction(attraction, DateUtil.setDayStartForDate(dateObj), dateObj, "today", cacheBuster);
        return gd.getGuestListForEntitlementFacilityView(attraction, dateObj, "today", cacheBuster);
	}
	
	@Path("/facility/{attraction}/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String allGuestsByAttractionYesterday(
            @PathParam("attraction") String attraction,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        Date dateObj;
        try {
            if(useDate.length() == 0) {
                dateObj = DateUtil.getCurrentDate();
            }
            else {
                dateObj=DateUtil.tstzformatter.parse(useDate);
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "facility attr /yday");
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return gd.getGuestListForEntitlementFacilityView(attraction, yesterdayTimeRange[1], "yesterday", cacheBuster);
	}
	
	@Path("/guest/{guestid}/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getGuestByIdToday(@PathParam("guestid") int guestid,
            @DefaultValue("") @QueryParam("currentdate") String useDate)
    {
        Date dateObj;
        try {
            if(useDate.length() == 0) {
                dateObj = DateUtil.getCurrentDate();
            }
            else {
                dateObj=DateUtil.tstzformatter.parse(useDate);
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "guest by id/today");
        }
		return this.gd.getGuest(guestid, DateUtil.setDayStartForDate(dateObj), dateObj, "today");
	}

	@Path("/guest/{guestid}/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getGuestByIdYesterday(@PathParam("guestid") int guestid,
            @DefaultValue("") @QueryParam("currentdate") String useDate)
    {
        Date dateObj;
        try {
            if(useDate.length() == 0) {
                dateObj = DateUtil.getCurrentDate();
            }
            else {
                dateObj=DateUtil.tstzformatter.parse(useDate);
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "guest by id/yesterday");
        }
        Date[] yesterdayTimeRange = DateUtil.getYesterday(dateObj);
        return this.gd.getGuest(guestid, yesterdayTimeRange[0], yesterdayTimeRange[1], "yesterday");

	}
	
	@Path("/guest/{guestid}/{date}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getGuestById( @PathParam("guestid") int guestid, 
			@PathParam("date") String useDate )
	{
        Date dateObj;
        try {
            dateObj=DateUtil.dateformatter.parse(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "guest by id/date");
        }
		return this.gd.getGuest(guestid, DateUtil.setDayStartForDate(dateObj),
                DateUtil.setDayEndForDate(dateObj), useDate);
	}

    @Path("/search/{date}/{rowcount}")
    @GET
    @Produces("text/javascript")
    public String getGuestListAsSearchForDateRowCount(
            @PathParam("date") String useDate,
            @PathParam("rowcount") int rowCount,
            @DefaultValue("") @QueryParam("buster") String cacheBuster) {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error("bad date: " + useDate + ", ex:" + e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway get guests attr");
        }
        return gd.getGuestsForSearch(DateUtil.setDayEndForDate(dateObj), rowCount, useDate, cacheBuster);
    }

    @Path("/search/{date}")
    @GET
    @Produces("text/javascript")
    public String getGuestListAsSearchByDate(@DefaultValue("") @QueryParam("currentdate") String useDate,
                                             @DefaultValue("") @QueryParam("buster") String cacheBuster) {
        int rowCount = 300;
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error("bad date: " + useDate + ", ex:" + e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway get guests attr");
        }
        return gd.getGuestsForSearch(DateUtil.setDayEndForDate(dateObj), rowCount, useDate, cacheBuster);
    }

    @Path("/search/now/{rowcount}")
    @GET
    @Produces("text/javascript")
    public String getGuestListAsSearchRowCount(
            @PathParam("rowcount") int rowCount,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("buster") String cacheBuster) {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error("bad date: " + useDate + ", ex:" + e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway get guests attr");
        }
        return gd.getGuestsForSearch(dateObj, rowCount, useDate, cacheBuster);
    }

    @Path("/search/now")
    @GET
    @Produces("text/javascript")
    public String getGuestListAsSearch(@DefaultValue("") @QueryParam("currentdate") String useDate,
                                       @DefaultValue("") @QueryParam("buster") String cacheBuster) {
        int rowCount = 300;
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error("bad date: " + useDate + ", ex:" + e.getLocalizedMessage());
            return gd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "subway get guests attr");
        }
        return gd.getGuestsForSearch(dateObj, rowCount, useDate, cacheBuster);
    }
}
