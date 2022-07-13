package com.disney.xband.xbrms.server.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.LocalCall;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:41 PM
 */
@Path("/facilities")
public class FacilitiesResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public FacilityListCompatDto getFacilities(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final FacilityListCompatDto ret =
                new FacilityListCompatDto(RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilities(), logger, context));
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/id/{id}")
    public FacilityCompatDto getFacilityById(@Context HttpServletRequest request, @PathParam("id") String id) {
        this.setAuditParams(request);

        try {
            final FacilityCompatDto ret = new FacilityCompatDto(RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilityById(id), logger, context));
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/ip/{addr}")
    public FacilityCompatDto getHealthItemByIp(@Context HttpServletRequest request, @PathParam("addr") String addr) {
        this.setAuditParams(request);

        try {
            final FacilityCompatDto ret = new FacilityCompatDto(RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilityByAddr(addr), logger, context));
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/gxp")
    public FacilityListCompatDto getFacilitiesForGxp(@Context HttpServletRequest request) {
        try {
            final FacilityListCompatDto ret =
                new FacilityListCompatDto(RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilities(), logger, context));
            
            Collection<XbrcCompatDto> allFacilities = ret.getFacilities();
            Collection<XbrcCompatDto> gxpFacilities = new ArrayList<XbrcCompatDto>();
            
            for (XbrcCompatDto xbrc : allFacilities)
            {
            	if (!xbrc.getActive())
            		continue;
            	if (xbrc.getLastDiscovery() == null)
            		continue;
            	if (((new Date()).getTime() - xbrc.getLastDiscovery().getTime()) > (PkConstants.STATUS_REFRESH_MS + 5000))
            		continue;
            	if (xbrc.getHaStatus() == null)
            		continue;
            	if (HAStatusEnum.slave.name().equalsIgnoreCase(xbrc.getHaStatus()) ||
            			HAStatusEnum.unknown.name().equalsIgnoreCase(xbrc.getHaStatus()))
            		continue;
            	
            	gxpFacilities.add(xbrc);
            }
            
            ret.setFacilities(gxpFacilities);
            
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

//	@GET
//	@Produces( {MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
//	@Path("/id/{id}/*")
//	public Response getFacilityByIdRedirect(
//			@PathParam("id") String id,
//			@PathParam("xbrcSpecific") Collection<PathSegment> xbrcSpecific)
//	{
//		StringBuffer facilityUrl = null;
//		for (XbrcDto x : XbrcDiscoveryConsumer.getInstance().getMap(false))
//		{
//			if (x.getVenue().equals(id))
//				facilityUrl = new StringBuffer(x.getUrl());
//		}
//		
//		if (facilityUrl == null)
//			return Response.noContent().build();
//		
//		facilityUrl.append("/rest");
//		
//		for (PathSegment ps : xbrcSpecific)
//			facilityUrl.append("/").append(ps);
//				
//		URI facilityURI = UriBuilder.fromUri(facilityUrl.toString()).build();
//		
//		return Response.temporaryRedirect(facilityURI).build();
//	}
}
