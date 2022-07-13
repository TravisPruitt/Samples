package com.disney.xband.xbrms.server.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.server.LocalCall;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 11/19/12
 * Time: 5:41 PM
 */
@Path("/scheduler")
public class SchedulerResource extends ResourceBase 
{
	@PUT
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON + ";charset=utf-8"})
    @Path("/message")
    public DtoWrapper<SchedulerResponse> onSchedulerMessage(@Context HttpServletRequest request, SchedulerRequest schedulerRequest) 
	{
        this.setAuditParams(request);

        try {
        	final DtoWrapper<SchedulerResponse> ret = RestUtils.wrap(((LocalCall)XbrmsUtils.getLocalCaller()).onSchedulerMessage(schedulerRequest), logger, context);
            this.auditSuccess(request);
            
            return ret;
        }
        catch (RuntimeException e) {
            this.auditFailure(request);

            throw e;
        }
    }
}
