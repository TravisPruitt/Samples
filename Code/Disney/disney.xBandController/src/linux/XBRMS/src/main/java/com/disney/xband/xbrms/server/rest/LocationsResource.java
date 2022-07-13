package com.disney.xband.xbrms.server.rest;

import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.LocationInfoListDto;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/8/12
 * Time: 12:56 PM
 */
@Path("/locations")
public class LocationsResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DtoWrapper<LocationInfoListDto> getLocations(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<LocationInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getLocations(), logger, context);
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
    @Path("/xbrc/{xbrcId}")
    public DtoWrapper<ReaderLocationInfo> getLocationInfo(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderLocationInfo> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getLocationInfo(xbrcId), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
