package com.disney.xband.xbrms.server.rest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.FacilityDto;
import com.disney.xband.xbrms.server.LocalCall;

import com.disney.xband.xbrms.common.model.FacilityListDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:41 PM
 */
@Path("/xbrcs")
public class XbrcsResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DtoWrapper<FacilityListDto> getFacilities(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilities(), logger, context);
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
    public DtoWrapper<FacilityDto> getFacilityById(@Context HttpServletRequest request, @PathParam("id") String id) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilityById(id), logger, context);
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
    @Path("/addr/{addr}")
    public DtoWrapper<FacilityDto> getFacilityByAddr(@Context HttpServletRequest request, @PathParam("addr") String addr) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilityByAddr(addr), logger, context);
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
    @Path("/ip/{ip}")
    public DtoWrapper<FacilityDto> getFacilityByIp(@Context HttpServletRequest request, @PathParam("ip") String ip) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilityByAddr(ip), logger, context);
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
    @Path("/status/{statusDesc}")
    // Possible statusDesc values: red, green, yellow, notred, notgreen
    public DtoWrapper<FacilityListDto> getFacilitiesByStatus(@Context HttpServletRequest request, @PathParam("statusDesc") String statusDesc) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilitiesByStatus(statusDesc), logger, context);
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
    @Path("/readerStatus/{readerStatusDesc}")
    // Possible statusDesc values: red, green, yellow, notred, notgreen
    public DtoWrapper<FacilityListDto> getFacilitiesByReaderStatus(@Context HttpServletRequest request, @PathParam("readerStatusDesc") String readerStatusDesc) {
        try {
            final DtoWrapper<FacilityListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilitiesByReaderStatus(readerStatusDesc), logger, context);
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
    @Path("/type/{type}")
    public DtoWrapper<FacilityListDto> getFacilitiesByModelType(@Context HttpServletRequest request, @PathParam("type") String type) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilitiesByModelType(type), logger, context);
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
    @Path("/type/{type}/status/{statusDesc}")
    // Possible statusDesc values: red, green, yellow, notred, notgreen
    public DtoWrapper<FacilityListDto> getFacilitiesByModelType(@Context HttpServletRequest request, @PathParam("type") String type, @PathParam("statusDesc") String statusDesc) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<FacilityListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getFacilitiesByModelTypeAndStatus(type, statusDesc), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}