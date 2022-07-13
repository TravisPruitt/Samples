package com.disney.xband.xbrms.server.rest;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.*;
import com.disney.xband.xbrms.server.LocalCall;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/8/12
 * Time: 12:55 PM
 */
@Path("/health")
public class HealthItemResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DtoWrapper<HealthItemListMapDto> getHealthItemsInventory(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<HealthItemListMapDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getHealthItemsInventory(), logger, context);
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
    @Path("/addr/{addr}/{port}")
    public DtoWrapper<HealthItemDto> getHealthItemByAddr(@Context HttpServletRequest request, @PathParam("addr") String addr, @PathParam("port") String port) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<HealthItemDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getHealthItemByAddr(addr, port), logger, context);
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
    @Path("/ip/{addr}/{port}")
    public DtoWrapper<HealthItemDto> getHealthItemByIp(@Context HttpServletRequest request, @PathParam("addr") String addr, @PathParam("port") String port) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<HealthItemDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getHealthItemByAddr(addr, port), logger, context);
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
    public DtoWrapper<HealthItemDto> getHealthItemById(@Context HttpServletRequest request, @PathParam("id") String id) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<HealthItemDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getHealthItemById(id), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @POST
    @Path("/add/{addr}/{port}/{className}")
    public void addHealthItem(@Context HttpServletRequest request, @PathParam("addr") String addr, @PathParam("port") String port, @PathParam("className") String className) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().addHealthItem(addr, port, className);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @DELETE
    @Path("/delete/{id}")
    public void deleteHealthItem(@Context HttpServletRequest request, @PathParam("id") String id) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deleteHealthItem(id);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @POST
    @Path("/deactivate/{id}")
    public void deactivateHealthItem(@Context HttpServletRequest request, @PathParam("id") String id) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deactivateHealthItem(id);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/type/xbrc")
    public DtoWrapper<XbrcListDto> getXbrcList(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final XbrcListDto list = new XbrcListDto();
            final Collection<XbrcDto> dtos = ((LocalCall) XbrmsUtils.getLocalCaller()).getFacilitiesForSystemHealth().getFacility();
            list.setHealthItem(new ArrayList<XbrcDto>(dtos));
            DtoWrapper<XbrcListDto> ret = RestUtils.wrap(list, logger, context);
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
    @Path("/type/idms")
    public DtoWrapper<IdmsListDto> getIdmsList(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<IdmsListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getIdmsListDto(), logger, context);
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
    @Path("/type/jmslistener")
    public DtoWrapper<JmsListenerListDto> getJmsListenerList(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<JmsListenerListDto> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getJmsListenerListDto(), logger, context);
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
    @Path("/refresh")
    public String refreshHealthItemsStatus(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().refreshHealthItemsStatus();
            this.auditSuccess(request);

            return "";
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}