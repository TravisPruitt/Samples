package com.disney.xband.xbrms.server.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpHeaders;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.ReaderInfoListDto;
import com.disney.xband.xbrms.server.LocalCall;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/30/13
 * Time: 10:26 PM
 */
@Path("/readers")
public class ReadersResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/xbrc/{xbrcId}/location/{locationId}")
    public DtoWrapper<ReaderInfoListDto> getReadersForLocation(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("locationId") String locationId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getReadersForLocation(xbrcId, locationId), logger, context);
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
    @Path("/unassigned")
    public DtoWrapper<ReaderInfoListDto> getUnassignedReaders(@Context HttpServletRequest request) {
        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getUnassignedReaders(), logger, context);
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
    @Path("/unassigned/status/{status}")
    public DtoWrapper<ReaderInfoListDto> getUnassignedReadersByReaderStatus(@Context HttpServletRequest request, @PathParam("status") String status) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getUnassignedReadersByReaderStatus(status), logger, context);
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
    @Path("/xbrc/{xbrcId}/unlinked")
    public DtoWrapper<ReaderInfoListDto> getUnlinkedReaders(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId) {
        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getUnlinkedReadersByReaderStatus(xbrcId, null), logger, context);
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
    @Path("/xbrc/{xbrcId}/unlinked/status/{status}")
    public DtoWrapper<ReaderInfoListDto> getUnlinkedReadersByStatus(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("status") String status) {
        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getUnlinkedReadersByReaderStatus(xbrcId, status), logger, context);
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
    public String refreshReadersHealthStatus(@Context HttpServletRequest request) {
        this.setAuditParams(request);

        try {
            XbrmsUtils.getLocalCaller().refreshReadersHealthStatus();
            this.auditSuccess(request);

            return "";
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }

    @PUT
    // @Consumes({MediaType.APPLICATION_XML})
    @Path("/replace/xbrc/id/{id}/reader/mac/{oldMac}/name/{oldName}")
    public Response replaceReaders(@Context HttpServletRequest request, @PathParam("id") String xbrcId, @PathParam("oldMac") String oldMac, @PathParam("oldName") String oldName, String readerInfo) {
    	int statusCode = 500;
    	try {
            statusCode = ((LocalCall) XbrmsUtils.getLocalCaller()).replaceReader(xbrcId, oldMac, oldName, readerInfo);
            this.auditSuccess(request);
            return Response.status(statusCode).build();
        }
        catch (RuntimeException e) {
            this.auditFailure(request);
            throw new WebApplicationException(
            		Response.status(statusCode).entity(e.getMessage()).build());
        }
    }
    
    @DELETE
    @Path("/unassigned/mac/{mac}")
    public void deleteUnassignedReaderFromCache(@Context HttpServletRequest request, @PathParam("mac") String mac)
    {
    	try
    	{
    		XbrmsUtils.getLocalCaller().deleteUnassignedReaderFromCache(mac);
    		this.auditSuccess(request);
    	}
    	catch (RuntimeException e)
    	{
    		this.auditFailure(request);
    		throw e;
    	}
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/xbrc/{xbrcId}/linked/status/{status}")
    public DtoWrapper<ReaderInfoListDto> getLinkedReadersByReaderStatus(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId, @PathParam("status") String status) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getLinkedReadersByReaderStatus(xbrcId, status), logger, context);
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
    @Path("/xbrc/{xbrcId}/linked")
    public DtoWrapper<ReaderInfoListDto> getLinkedReadersByReaderStatus(@Context HttpServletRequest request, @PathParam("xbrcId") String xbrcId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<ReaderInfoListDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getLinkedReadersByReaderStatus(xbrcId, ""), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
