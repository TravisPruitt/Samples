package com.disney.xband.idms.web;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ReadOnlyException extends WebApplicationException {
     public ReadOnlyException() {
	     super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	         .entity("Current configuration only supports read-only operations.\r\n").type(MediaType.TEXT_PLAIN).build());
     }
}
