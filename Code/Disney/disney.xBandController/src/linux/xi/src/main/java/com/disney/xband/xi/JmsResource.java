package com.disney.xband.xi;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.disney.xband.xi.model.jms.JmsDAO;
import com.disney.xband.xi.model.jms.JmsDAO.HistogramGrouping;
import com.disney.xband.xi.model.jms.JmsGuestStates;
import com.disney.xband.xi.model.jms.JmsMessageCount;
import com.disney.xband.xi.model.jms.JmsMessageHistogram;
import com.disney.xband.xi.model.jms.JmsMessages;
import com.disney.xband.xi.model.jms.JmsMessageRange;

@Path("/jms")
public class JmsResource
{
	private JmsDAO dao = null;
	
	public JmsResource()
	{
		dao = new JmsDAO();
	}
	
	@Path("/venue/{venuename}/messages")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public JmsMessages jmsMessagesForAttraction(@PathParam("venuename") String sVenue, @QueryParam("from") String sFrom, @QueryParam("to") String sTo, @QueryParam("afterid") String sFromId)
	{
		// weed out degenerate cases
		boolean bHaveFromOrTo = (sFrom!=null) || (sTo!=null);
		if (bHaveFromOrTo && sFromId!=null)
			throw new WebApplicationException(500);
		if (!bHaveFromOrTo && sFromId==null)
			throw new WebApplicationException(500);
		
		if (bHaveFromOrTo)
		{
			Date dtFrom = null;
			Date dtTo = null;
			
			try
			{
				if (sFrom!=null)
					dtFrom = parseSqlDate(sFrom);
				else
					dtFrom = new Date(0);
				
				if (sTo!=null)
					dtTo = parseSqlDate(sTo);
				else
					dtTo = new Date();
			}
			catch(ParseException pe)
			{
				throw new WebApplicationException(500);
			}
			
			try
			{
				return dao.getJmsMessagesForAttractionByDate(sVenue, dtFrom, dtTo);
			}
			catch (Exception e)
			{
				throw new WebApplicationException(500);
			}
		}
		else
		{
			try
			{
				return dao.getJmsMessagesForAttractionById(sVenue, Long.parseLong(sFromId));
			}
			catch (Exception e)
			{
				throw new WebApplicationException(500);
			}
		}
		
	}
		
	@Path("/venue/{venuename}/gueststate")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public JmsGuestStates jmsGuestStateForAttraction(@PathParam("venuename") String sVenue, @QueryParam("from") String sFrom, @QueryParam("to") String sTo)
	{
		if (sFrom==null || sTo==null)
			throw new WebApplicationException(500);
		
		Date dtFrom = null;
		Date dtTo = null;
		try
		{
			dtFrom = parseSqlDate(sFrom);
			dtTo = parseSqlDate(sTo);
		}
		catch(ParseException pe)
		{
			throw new WebApplicationException(500);
		}
		try
		{
			return dao.getJmsGuestStatesForAttractionAtTime(sVenue, dtFrom, dtTo);
		}
		catch (Exception e)
		{
			throw new WebApplicationException(500);
		}
	}
	
	@Path("/venue/{venuename}/messagehistogram")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public JmsMessageHistogram jmsMessageHistogramForAttraction(@PathParam("venuename") String sVenue, 
																@QueryParam("from") String sFrom, 
																@QueryParam("to") String sTo, 
																@QueryParam("groupby") String sGroupBy)
	{
		// if no from date specified, start at the earliest time
		Date dtFrom = new Date(0);
		if (sFrom!=null)
		{
			try
			{
				dtFrom = parseSqlDate(sFrom);
			}
			catch(ParseException pe)
			{
				throw new WebApplicationException(500);
			}
		}
		
		// if no to date is specified, end at "now"
		Date dtTo = new Date();
		if (sTo!=null)
		{
			try
			{
				dtTo = parseSqlDate(sTo);
			}
			catch(ParseException pe)
			{
				throw new WebApplicationException(500);
			}
		}
		
		// if no grouping specified, assume weekly
		JmsDAO.HistogramGrouping grouping = HistogramGrouping.WEEKLY;
		if (sGroupBy!=null)
		{
			try
			{
				grouping = JmsDAO.HistogramGrouping.valueOf(sGroupBy.toUpperCase());
			}
			catch(IllegalArgumentException iae)
			{
				throw new WebApplicationException(500);
			}
		}
		
		try
		{
			return dao.getJmsMessageHistogram(sVenue, dtFrom, dtTo, grouping);
		}
		catch (Exception e)
		{
			throw new WebApplicationException(500);
		}
	}
	
	@Path("/venue/{venuename}/messagerange")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public JmsMessageRange jmsMessageRangeForAttraction(@PathParam("venuename") String sVenue)
	{
		try
		{
			return dao.getJmsMessageRangeForAttraction(sVenue);
		}
		catch (Exception e)
		{
			throw new WebApplicationException(500);
		}
	}
	
	@Path("/venue/{venuename}/messagecount")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public JmsMessageCount jmsMessageCountForAttraction(@PathParam("venuename") String sVenue, 
														@QueryParam("from") String sFrom, 
														@QueryParam("to") String sTo, 
														@QueryParam("afterid") String sFromId)
	{
		// weed out degenerate cases
		boolean bHaveFromOrTo = (sFrom!=null) || (sTo!=null);
		if (bHaveFromOrTo && sFromId!=null)
			throw new WebApplicationException(500);
		if (!bHaveFromOrTo && sFromId==null)
			throw new WebApplicationException(500);
		
		if (bHaveFromOrTo)
		{
			Date dtFrom = null;
			Date dtTo = null;
			
			try
			{
				if (sFrom!=null)
					dtFrom = parseSqlDate(sFrom);
				else
					dtFrom = new Date(0);
				
				if (sTo!=null)
					dtTo = parseSqlDate(sTo);
				else
					dtTo = new Date();
			}
			catch(ParseException pe)
			{
				throw new WebApplicationException(500);
			}
			
			try
			{
				return dao.getJmsMessageCountForAttractionByDate(sVenue, dtFrom, dtTo);
			}
			catch (Exception e)
			{
				throw new WebApplicationException(500);
			}
		}
		else
		{
			try
			{
				return dao.getJmsMessageCountForAttractionById(sVenue, Long.parseLong(sFromId));
			}
			catch (Exception e)
			{
				throw new WebApplicationException(500);
			}
		}
		
	}
	
	public static Date parseSqlDate(String sFrom) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));		
		return sdf.parse(sFrom);
	}
	
	public static String formatDate(Date dt, boolean bSQL)
	{
		SimpleDateFormat sdf;
		
		if (bSQL)
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		else
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(dt);
	}

	public static String formatDate(Date dt)
	{
		return formatDate(dt, true);
	}


}
