package com.disney.xband.xbrms.server.rest;

import com.disney.xband.lib.controllerapi.XbrcConfig;
import com.disney.xband.xbrc.lib.entity.XbrcConfiguration;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.XbrcConfigListMapDto;
import com.disney.xband.xbrms.common.model.XbrcConfigurationListDto;
import com.disney.xband.xbrms.common.model.XbrcIdListDto;
import com.disney.xband.xbrms.server.LocalCall;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:03 PM
 */
@Path("/xbrc")
public class XbrcConfigResource extends ResourceBase {
    // Configuration
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/config/stored/{configId}")
    public DtoWrapper<XbrcConfiguration> getXbrcStoredConfig(@Context HttpServletRequest request, @PathParam("configId") String configId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfiguration> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getXbrcStoredConfig(configId), logger, context);
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
    @Path("/config/fetch/id/{xbrcId}/{name}")
    public DtoWrapper<XbrcConfiguration> getXbrcConfig(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("name") String name) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfiguration> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getXbrcConfig(xbrcId, name), logger, context);
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
    @Path("/config/fetch/addr/{xbrcAddr}/{xbrcPort}/{name}")
    public DtoWrapper<XbrcConfiguration> getXbrcConfig(@Context HttpServletRequest request, @PathParam("xbrcAddr") String xbrcAddr, @PathParam("xbrcPort") String xbrcPort, @PathParam("name") String name) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfiguration> ret = RestUtils.wrap(((LocalCall) XbrmsUtils.getLocalCaller()).getXbrcConfig(xbrcAddr, xbrcPort, name), logger, context);
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
    public DtoWrapper<XbrcConfigurationListDto> getStoredXbrcConfigs(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfigurationListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getStoredXbrcConfigs(), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    // @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @Path("/config/parse")
    public DtoWrapper<XbrcConfiguration> parseXbrcConfigXml(@Context HttpServletRequest request, String xml) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfiguration> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().parseXbrcConfigXml(xml), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @Path("/config/props")
    public DtoWrapper<XbrcConfigListMapDto> getXbrcProperties(@Context HttpServletRequest request, XbrcIdListDto list) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrcConfigListMapDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getXbrcProperties(list), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @POST
    @Path("/config/add/{xbrcId}")
    public void addXbrcConfig(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, XbrcConfiguration conf) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().addXbrcConfig(xbrcId, conf);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @DELETE
    @Path("/config/delete/{configId}")
    public void deleteXbrcConfig(@Context HttpServletRequest request, @PathParam("configId") String configId) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deleteXbrcConfig(configId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/config/deploy/{configId}/{xbrcId}")
    public void deployXbrcConfig(@Context HttpServletRequest request, @PathParam("configId") String configId, @PathParam("xbrcId") String xbrcId) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().deployXbrcConfig(configId, xbrcId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @POST
    @Path("/config/update/{xbrcId}")
    public void updateXbrcConfig(@Context HttpServletRequest request, XbrcConfig conf, @PathParam("xbrcId") String xbrcId) {
        this.setAuditParams(request);

        try {
            // RestUtils.saveObject(conf);
            XbrmsUtils.getLocalCaller().updateXbrcConfig(conf, xbrcId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
    
    @PUT
    @Path("/schedule/override/{xbrcId}/{hours}")
    public void overrideSchedule(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("hours") String hours) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().overrideSchedule(xbrcId, hours);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
