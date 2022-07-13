package com.disney.xband.idms.web;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.PathSegment;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;

public class IdentifierParameter 
{
	private static Logger logger = Logger.getLogger(IdentifierParameter.class);
	private String identifierType;
	private String identifierValue; 

	public String getIdentifierType()
	{
		return this.identifierType;
	}
	
	public String getIdentifierValue()
	{
		return this.identifierValue;
	}
	
	public IdentifierParameter(String id)
	{
		Parse(id);
	}
	
	public IdentifierParameter(PathSegment pathSegment)
	{
		Set<Entry<String, List<String>>> ids = pathSegment.getMatrixParameters().entrySet();

		if (ids.isEmpty())
		{
			//get the id value.
			String id = pathSegment.getPath();
			
			Parse(id);
		}
		else
		{
			try 
			{
				for (Entry<String, List<String>> s : ids)
				{
					this.identifierType = s.getKey();
					this.identifierValue = URLDecoder.decode(s.getValue().get(0), "UTF-8");
				}
			} 
			catch (Exception ex) 
			{
				logger.error("Unexpected error.", ex);
			}
		}
	}
	
	private void Parse(String id)
	{
		try
		{
			// it isn't a list of id types, so try to parse to a guestId.
			Long.parseLong(id);
			this.identifierType = "guestId";
			this.identifierValue = id;
			return;
		}
		catch(NumberFormatException ex)
		{
			//Ignore
		}
		catch(Exception ex)
		{
			logger.info(
					ExceptionFormatter.format("Unexpected error.",ex));
		}
		
		//If an long can't be parsed then assume it's an xid.
		this.identifierType = "xid";
		this.identifierValue= id;
		
	}
}
