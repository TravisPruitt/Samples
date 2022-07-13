package com.disney.xband.xbrms.server.rest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.ProblemsReportDto;
import com.disney.xband.xbrms.common.model.XbrmsStatusCompatDto;
import com.disney.xband.xbrms.common.model.XbrmsStatusDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:41 PM
 */
@Path("/status")
public class StatusResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public XbrmsStatusCompatDto getStatusCompat(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final XbrmsStatusCompatDto ret = new XbrmsStatusCompatDto(XbrmsUtils.getLocalCaller().getStatus());
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
    @Path("/info")
    public DtoWrapper<XbrmsStatusDto> getStatus(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrmsStatusDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getStatus(), logger, context);
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
    @Path("/problems")
    public DtoWrapper<ProblemsReportDto> getRecentXbrmsProblems(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ProblemsReportDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getRecentXbrmsProblems(), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/problems/clear")
    public void refreshHealthItemsStatus(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().clearXbrmsProblemsList();
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
