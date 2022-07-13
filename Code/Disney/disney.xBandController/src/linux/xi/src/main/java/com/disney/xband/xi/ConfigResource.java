package com.disney.xband.xi;

import com.disney.xband.xi.model.ConfigDAO;

import javax.ws.rs.*;

@Path("/config")
public class ConfigResource
{
	ConfigDAO configD = new ConfigDAO();
	public ConfigResource()
	{
	}

    @Path("/etlmon")
    @GET
    @Produces("application/json")
    public String getETLOperationalState() {
        return configD.dbEtlFreshness();
    }

    @Path("/etlmoninfo")
    @GET
    @Produces("application/json")
    public String getETLOperationalStateDef() {
        return configD.dbETLFreshMetaData();
    }

    @Path("/recruitwin")
    @GET
    @Produces("text/javascript")
    public String getRecruitmentWindows()  {
        return configD.getRecruitmentWindow();
    }

    @Path("/ccache")
    @GET
    @Produces("text/javascript")
    public String clearCache()  {
        return configD.clearCache();
    }

    @Path("/rreload")
    @GET
    @Produces("text/javascript")
    public String reload()  {
        return configD.reload();
    }

    @Path("/parks")
    @GET
    @Produces("text/javascript")
    public String getParkList(@DefaultValue("") @QueryParam("buster") String cacheBuster)  {
        return configD.getParksAsString(cacheBuster);
    }

    @Path("/dbstate")
    @GET
    @Produces("text/javascript")
    public String getDbPoolState(@DefaultValue("") @QueryParam("buster") String cacheBuster)  {
        return configD.getCurrentDbState(cacheBuster);
    }

    @Path("/dbreset")
    @GET
    @Produces("text/javascript")
    public String resetDbConnPool(@DefaultValue("") @QueryParam("buster") String cacheBuster)  {
        return configD.hardResetConnectionPool();
    }

    @Path("/facilities/{parkId}")
    @GET
    @Produces("text/javascript")
    public String getFacilityList(@PathParam("parkId") int parkId, @DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        return configD.getFacilityMapByParkAsString(parkId, cacheBuster);
    }

    @Path("/facilities")
    @GET
    @Produces("text/javascript")
    public String getFacilityList(@DefaultValue("") @QueryParam("buster") String cacheBuster)
    {
        return configD.getFacilityMapAsString(cacheBuster);
    }

    @Path("/param/set/{pname}/{pvalue}")
    @GET
    @Produces("text/javascript")
    public String SaveParam(  @PathParam("pname") String paramName, @PathParam("pvalue") String paramValue  ) {
        return configD.persistParameter(paramName, XiResource.convertHexToString(paramValue));
    }

    @Path("/param/{pname}/")
    @GET
    @Produces("text/javascript")
    public String PushParam(  @PathParam("pname") String paramName) {
        return configD.getParameter(paramName);
    }
}
