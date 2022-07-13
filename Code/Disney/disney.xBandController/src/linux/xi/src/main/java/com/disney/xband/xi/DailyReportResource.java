package com.disney.xband.xi;

import com.disney.xband.xi.model.DAO;
import com.disney.xband.xi.model.DailyReportDAO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.disney.xband.xi.model.DateUtil;
import org.apache.log4j.Logger;


@Path("/daily")
public class DailyReportResource
{
	DailyReportDAO drd = null;
	Logger logger = Logger.getRootLogger();

	public DailyReportResource()
	{
		this.drd = new DailyReportDAO();
	}
	
	@Path("/today")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getDailyReport() {
		return drd.getReportData(DateUtil.getTodaysDate(), "today");
	}
	
	@Path("/yesterday")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getDailyReportYesterday() {
		return drd.getReportData(DateUtil.getTodaysDate(), "yesterday");
	}
	
	@Path("/{date}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getDailyReport(@PathParam("date") String sDate) {
		return drd.getReportData(sDate, sDate);
	}
	
	@Path("/report/goofy/{guestCount}/{guestCountTarget}/{recruitedCount}/{reportDate}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String setDailyReport(
			@PathParam("guestCount") String guestCount, 
			@PathParam("guestCountTarget") String guestCountTarget, 
			@PathParam("recruitedCount") String recruitedCount, 
			@PathParam("reportDate") String reportDate
			) {
		
		return drd.setReportData(
				Integer.parseInt( guestCount), 
				Integer.parseInt( guestCountTarget), 
				Integer.parseInt( recruitedCount), 
				reportDate);
	}
}
