package com.disney.xband.xbms.web;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.xbms.dao.DAOFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.JResponse;

@Path("/messages")
public class MessagesView
{
	private static Logger logger = Logger.getLogger(MessagesView.class);

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public JResponse<MessageBatch> GetMessageBatch(@PathParam("id") int messageBatchId)
	{
		MessageBatch result = null;

		try
		{
			result = DAOFactory.getDAOFactory().getMessageDAO().Retreive(messageBatchId);

		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Get Message Batch failed.",ex));
			throw new WebApplicationException(500);
		}
	
		if (result == null) 
		{
			throw new WebApplicationException(404);
		}
		
		return JResponse.ok(result).build();
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON + ";charset=utf-8"})
	public Response SendMessages(String messagesPostString)
	{
		URI location = null;
		int messageBatchId = 0;

		try
		{
			ObjectMapper om = new ObjectMapper();
			MessagesPost messagesPost = om.readValue(messagesPostString, MessagesPost.class);
			
			messageBatchId = DAOFactory.getDAOFactory().getMessageDAO().Start(messagesPost.getCount(), 
					messagesPost.getPuckCount(), messagesPost.getCastMemberCount());
			
			JMSAgent.INSTANCE.AddBatch(messageBatchId);

			location = new URI("/messages/" + messageBatchId);
		}
		catch(Exception ex)
		{
			logger.error(ExceptionFormatter.format("Send Messages failed.",ex));
			throw new WebApplicationException(500);
		}
	
		if (messageBatchId == 0)
		{
			throw new WebApplicationException(400);
		}
		
		return Response.created(location).build();
	}
}
