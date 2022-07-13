package com.disney.xband.xbrms.server.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 1:15 PM
 */
@Provider
public class OperationFailedException implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return Response.status(500).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}
