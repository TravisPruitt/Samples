package com.disney.xband.xbrms.server.rest;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.common.model.PerfMetricMetadataMapDto;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:37 PM
 */
@Path("/performance")
public class PerfMetricsResource extends ResourceBase {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/meta/{hiId}")
    public DtoWrapper<PerfMetricMetadataMapDto> getMetricMetaById(@Context HttpServletRequest request, @PathParam("hiId") String hiId) {
        this.setAuditParams(request);

        try {
            final DtoWrapper<PerfMetricMetadataMapDto> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getMetricMetaById(hiId), logger, context);
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
    @Path("/metrics/{hiId}/{metaName}/{metaVersion}/{startDate}/{endDate}")
    public DtoWrapper<String> getMetrics(@Context HttpServletRequest request, @PathParam("hiId") String hiId, @PathParam("metaName") String metaName,
                                         @PathParam("metaVersion") String metaVersion, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {

        this.setAuditParams(request);

        if("null".equals(metaName)) {
            metaName = null;
        }

        if("null".equals(metaVersion)) {
            metaVersion = null;
        }

        try {
            final DtoWrapper<String> ret = RestUtils.wrap(XbrmsUtils.getLocalCaller().getMetrics(hiId, metaName, metaVersion, startDate, endDate), logger, context);
            this.auditSuccess(request);

            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
