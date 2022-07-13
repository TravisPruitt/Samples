package com.disney.xband.xi;

import com.disney.xband.xi.model.DateUtil;
import com.disney.xband.xi.model.RecruitmentDAO;

import javax.ws.rs.*;
import java.text.ParseException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 7/18/12
 * Time: 10:56 AM
 */
@Path("/recruitment")
public class RecruitmentResource extends XiResource
{
    private RecruitmentDAO rd = new RecruitmentDAO();
    public RecruitmentResource() { }

    @GET
    @Produces("text/javascript")
    public String getRecruitment(
            @DefaultValue("")  @QueryParam("buster") String cacheBuster,
            @DefaultValue("") @QueryParam("currentdate") String useDate,
            @DefaultValue("") @QueryParam("programstartdate") String startDate
            )
    {
        Date dateObj;
        try {
            dateObj=parseCurrentDateUtil(useDate);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return rd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "recruitment/currentdate");
        }
        Date startDateObj;
        try {
            if(startDate.equals("")) {
                startDateObj = DateUtil.dateAdd(dateObj, getWindowStartDelta());
            }
            else {
                logger.debug("programstartdate set as " + startDate);
                startDateObj = parseCurrentDateUtil(startDate);
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
            return rd.errorMessage("Bad Date format -- expect:yyyy-MM-ddTHH:mm:ssZ", "recruitment/programstartdate");
        }
        return rd.getRecruitment(startDateObj, DateUtil.dateAdd(dateObj, getWindowEndDelta()), dateObj, cacheBuster);
    }

    /*
    @Path("/updateTarget/{targetCount}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String updateTargetCount(@PathParam("targetCount") int targetCount) {
        return rd.updateTargetCount(targetCount);
    }*/

}
