package com.disney.xband.xbrms.server.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.EnvPropertiesMapDto;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:49 PM
 */

@Path("/config")
public class XbrmsConfigResource extends ResourceBase {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/xbrms")
    public DtoWrapper<XbrmsConfigDto> getXbrmsConfig(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<XbrmsConfigDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getXbrmsConfig(), logger, context);
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
    @Path("/xbrms")
    public void setXbrmsConfig(@Context HttpServletRequest request, XbrmsConfigDto config) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().setXbrmsConfig(config);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/properties")
    public DtoWrapper<EnvPropertiesMapDto> getServerProperties(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<EnvPropertiesMapDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getServerProps(), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
