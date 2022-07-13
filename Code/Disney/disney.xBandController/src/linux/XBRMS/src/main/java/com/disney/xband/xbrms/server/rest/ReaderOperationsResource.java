package com.disney.xband.xbrms.server.rest;

import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:39 PM
 */
@Path("/reader")
public class ReaderOperationsResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/info/{xbrcId}/{readerMac}")
    public DtoWrapper<ReaderInfo> getReaderByMac(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("readerMac") String readerMac) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfo> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getReaderByMac(xbrcId, readerMac), logger, context);
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
    @Path("/info/mac/{mac}")
    public DtoWrapper<ReaderInfo> getReaderByMac(@Context HttpServletRequest request, @PathParam("mac") String mac) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfo> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getUnassignedReaderByMac(mac), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/assign/{xbrcAddress}/{readerMac}")
    public void assignReader(@Context HttpServletRequest request, @PathParam("xbrcAddress") String xbrcAddress, @PathParam("readerMac") String readerMac) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().assignReader(xbrcAddress, readerMac);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/identify/{xbrcId}/{readerMac}")
    public Response identifyReaderComp(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("readerMac") String readerMac) {
        this.setAuditParams(request);

        try {
            int statusCode = XbrmsUtils.getLocalCaller().identifyReaderComp(xbrcId, readerMac);
            this.auditSuccess(request);
           	return Response.status(statusCode).build();
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/reboot/{xbrcId}/{readerId}")
    public void rebootReaderComp(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("readerId") String readerId) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().rebootReaderComp(xbrcId, readerId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    @Path("/restart/{xbrcId}/{readerId}")
    public void restartReaderComp(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("readerId") String readerId) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().restartReaderComp(xbrcId, readerId);
            this.auditSuccess(request);
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}