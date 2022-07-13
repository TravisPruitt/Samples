package com.disney.xband.xbrms.server.rest;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEventList;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/16/13
 * Time: 12:37 PM
 */
@Path("/audit")
public class AuditResource extends ResourceBase {
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @Path("/push")
    public void pushAuditEvents(AuditEventList events) {
        XbrmsUtils.getLocalCaller().pushAuditEvents(events);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/pull/{afterEventId}")
    public DtoWrapper<AuditEventList> pullAuditEvents(@Context HttpServletRequest request, @PathParam("afterEventId") long afterEventId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<AuditEventList> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().pullAuditEvents(afterEventId), logger, context);
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
    @Path("/pullall")
    public DtoWrapper<AuditEventList> pullAllAuditEvents(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<AuditEventList> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().pullAuditEvents(-1), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @DELETE
    @Path("/delete/{upToEventId}")
    public void deleteAuditEvents(@Context HttpServletRequest request, @PathParam("upToEventId") long upToEventId) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deleteAuditEvents(upToEventId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @DELETE
    @Path("/deleteall")
    public void deleteAuditEvents(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deleteAuditEvents(-1);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/config")
    public DtoWrapper<AuditConfig> getAuditConfig(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<AuditConfig> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getAuditConfig(), logger, context);
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
    @Path("/configs")
    public DtoWrapper<AuditConfig> getAuditConfigs(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<AuditConfig> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getSubordinateAuditConfigs(), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @Path("/config")
    public void setAuditConfig(@Context HttpServletRequest request, AuditConfig config) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().setAuditConfig(config);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
