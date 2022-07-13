package com.disney.xband.xbrms.client.action;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.common.lib.health.StatusType;
import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.client.XbrmsAccessHelper;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.model.ReaderDpo;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.FacilityListDto;
import com.disney.xband.xbrms.common.model.ReaderInfoListDto;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.opensymphony.xwork2.Preparable;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

public class ReplaceReaderAction extends BaseAction implements Preparable
{
	private static Logger logger = Logger.getLogger(ReplaceReaderAction.class);
	
	// map of <xbrms id, xbrms name>
	private Map<String, String> parks;
	
	private String parkId;
	private String xbrcId;
	private String existingReaderMac;
	private String replacementReaderMac;
	private boolean unassignedReplacement;
	
	// map of <xbrms id, xbrc>
	private Map<String, Collection<XbrcDto>> venues;
	private Collection<ReaderDpo> existingReaders;
	private Collection<ReaderDpo> replacementUnlinkedReaders;
	private Collection<ReaderDpo> replacementUnassignedReaders;
	private ReaderDpo existingReader;
	
	// when filter is set only parks and venues that have readers in Red state will be shown
	private boolean filter = true;
	
	@Override
    public void prepare() throws Exception
    {
		// get all parks the UI knows about
		Collection<XbrmsDto> xbrmss = getConfiguredXbrmss();
		
		/*
		 *  Get applicable venues for these parks. If 'filtered' is true, these will only be
		 *  venues that have a reader in 'Red' health status. Otherwise, all parks are included.
		 */
		venues = new LinkedHashMap<String, Collection<XbrcDto>>();
		for (XbrmsDto xbrms : xbrmss)
		{
			try
			{
				IRestCall caller = XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest()) 
						? XbrmsUtils.getRestCaller(xbrms.getUrl()) : XbrmsUtils.getRestCaller();
						
				FacilityListDto facilities = null;
				try
				{
					if (filter)
						facilities = caller.getFacilitiesByReaderStatus("red");
					else
						facilities = caller.getFacilities();
				}
				catch (HTTPException e)
				{
					this.errMsg = e.getCause() != null ? "HTTP status code: " + e.getStatusCode() : "unknown";
					String errMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : "unknown";
					
					if (logger.isDebugEnabled())
						logger.error("Failed to retrieve a list of venues for park: " + parkId + " and venue: " + xbrcId
								+ ". Cause: " + errMessage, e);
					else
						logger.error("Failed to retrieve a list of venues for park: " + parkId + " and venue: " + xbrcId
								+ ". Cause: " + errMessage);
				}
				
				if (facilities != null && facilities.getFacility() != null)
				{
					Collection<XbrcDto> filteredXbrcs = new ArrayList<XbrcDto>();
					for (XbrcDto xbrc : facilities.getFacility())
					{
						// exclude all invalid xbrcs
	                    if (!xbrc.isValidHaStatus() || HAStatusEnum.slave.name().equals(xbrc.getHaStatus()))
	                        continue;
	                    if (HAStatusEnum.unknown.name().equals(xbrc.getHaStatus()))
	                    {
	                    	if (XbrmsUtils.isEmpty(xbrc.getVip()) || xbrc.getVip().startsWith("#"))
	                    		continue;

	                    	final int CONNECTION_TIMEOUT = 2000;	// this needs to be short, otherwise user's experience will suffer
	                    	try
	                    	{
	                    		Socket socket = new Socket();
	                    		InetAddress addr = InetAddress.getByName(xbrc.getVip());
	                    		SocketAddress sAddrs = new InetSocketAddress(addr, xbrc.getPort());
	                    		socket.connect(sAddrs, CONNECTION_TIMEOUT);
	                    	}
	                    	catch(Exception e)
	                    	{
	                    		logger.info("Detected xBRC " + xbrc.getIp() + "/" + xbrc.getHostname() + " configured with a VIP that is currently not reachable: " + xbrc.getVip());

	                    		continue;
	                    	}
	                    }
	
	                    // valid xbrc -- include
	                    filteredXbrcs.add(xbrc);
					}
					
					if (filteredXbrcs.size() > 0)
						venues.put(xbrms.getUrl(), filteredXbrcs);
				}
			}
			catch (Throwable t)
			{
				if (t.getCause() != null || t.getCause() instanceof UnknownHostException)
					logger.warn("Failed to communicate with xBRMS server at " + xbrms.getUrl() + " while trying to get its list of venues.");
				else
					logger.error("Failed to retreive a list of venues from xBRMS server " + xbrms.getUrl(), t);
			}
		}
		
		// only list parks that have the applicable venues
		parks = new HashMap<String, String>(8);
		if (xbrmss != null)
		{
			for (XbrmsDto xbrms : xbrmss)
			{
				if (!venues.containsKey(xbrms.getUrl()))
					continue;
				
				parks.put(xbrms.getUrl(), xbrms.getDesc());
			}
		}
		
		existingReaders = new LinkedList<ReaderDpo>();
		replacementUnlinkedReaders = new LinkedList<ReaderDpo>();
		replacementUnassignedReaders = new LinkedList<ReaderDpo>();
    }
		
	@Override
	public String execute() throws Exception 
	{
        return SUCCESS;
	}
	
	public String initParksAndVenues() throws Exception
	{
		return SUCCESS;
	}
	
	public String initExistingReadersByVenue() throws Exception
	{
		if (xbrcId == null || xbrcId.isEmpty() || parkId == null || parkId.isEmpty())
		{
			this.errMsg = "invalid request.";
			return INPUT;
		}
		
		try
		{
			IRestCall caller = XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest()) 
					? XbrmsUtils.getRestCaller(parkId) : XbrmsUtils.getRestCaller();
			
			ReaderInfoListDto readers = caller.getLinkedReadersByReaderStatus(xbrcId, (filter ? "red" : ""));
			if (readers != null && readers.getReaderInfoList() != null && readers.getReaderInfoList().size() > 0)
			{
				for (ReaderInfo ri : readers.getReaderInfoList())
				{
					existingReaders.add(initReader(ri));
				}
			}

		} 
		catch (HTTPException e)
		{
			this.errMsg = e.getCause() != null ? "HTTP status code: " + e.getStatusCode() : "unknown";
			String errMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : "unknown";
			
			if (logger.isDebugEnabled())
				logger.error("Failed to retrieve a list of venues for park: " + parkId + " and venue: " + xbrcId
						+ ". Cause: " + errMessage, e);
			else
				logger.error("Failed to retrieve a list of venues for park: " + parkId + " and venue: " + xbrcId
						+ ". Cause: " + errMessage);
			
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String initReplacementReaders() throws Exception
	{
		if (parkId == null || parkId.isEmpty() 
				|| xbrcId == null || xbrcId.isEmpty()
				|| existingReaderMac == null || existingReaderMac.isEmpty())
		{
			this.errMsg = "invalid request.";
			return INPUT;
		}
		
		try
		{
			IRestCall caller = XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest()) 
					? XbrmsUtils.getRestCaller(parkId) : XbrmsUtils.getRestCaller();
			
			if (existingReaderMac != null)
			{
				ReaderInfo reader = caller.getReaderByMac(xbrcId, existingReaderMac);
				
				if (reader == null)
					return SUCCESS;
				
				existingReader = new ReaderDpo();
				existingReader.setMac(reader.getMacAddress());
				existingReader.setId("" + reader.getId());
				existingReader.setIp(reader.getIpAddress());
				existingReader.setName(reader.getName());
				existingReader.setType(reader.getType().getDescription());
				existingReader.setStatus(reader.getStatus().name());
				existingReader.setStatusMessage(reader.getStatusMessage());
			}
			
			// list of unlinked readers with the same reader type as the one we are replacing
			ReaderInfoListDto unlinkedList = caller.getUnlinkedReadersByReaderStatus(xbrcId, "notred");
			if (unlinkedList != null && unlinkedList.getReaderInfoList() != null)
				for (ReaderInfo readerInfo : unlinkedList.getReaderInfoList())
					if (readerInfo.getType().getDescription().equals(existingReader.getType()))
						replacementUnlinkedReaders.add(initReader(readerInfo));
			
			if (XbrmsAccessHelper.getInstance().canAccessAsset("Deny maintenance role", ServletActionContext.getRequest()))
			{
				// list of unasigned/lost/found readers with the same reader type as the one we are replacing
				caller = XbrmsUtils.getRestCaller();
				ReaderInfoListDto unassignedList = caller.getUnassignedReaders();
				if (unassignedList != null && unassignedList.getReaderInfoList() != null)
					for (ReaderInfo readerInfo : unassignedList.getReaderInfoList())
						if (readerInfo.getType().getDescription().equals(existingReader.getType()) 
								&& readerInfo.getStatus() != null && readerInfo.getStatus() != StatusType.Red)
							replacementUnassignedReaders.add(initReader(readerInfo));
			}
		}
		catch (HTTPException e)
		{
			this.errMsg = e.getCause() != null ? "HTTP status code: " + e.getStatusCode() : "unknown";
			String errMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : "unknown";
			
			if (logger.isDebugEnabled())
				logger.error("Failed to retrieve a list of replacement readers of type " + existingReader.getType() + " from xBRMS " + parkId
						+ ". Cause: " + errMessage, e);
			else
				logger.error("Failed to retrieve a list of replacement readers of type " + existingReader.getType() + " from xBRMS " + parkId
						+ ". Cause: " + errMessage);
			
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String runReplaceReader()
	{
		if (existingReaderMac == null || existingReaderMac.isEmpty() ||
			replacementReaderMac == null || replacementReaderMac.isEmpty() ||
			parkId == null || parkId.isEmpty() ||
			xbrcId == null || xbrcId.isEmpty())
		{
			return INPUT;
		}

        try
		{
			IRestCall caller = XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest()) 
					? XbrmsUtils.getRestCaller(parkId) : XbrmsUtils.getRestCaller();
			
			ReaderInfo existingReader = caller.getReaderByMac(xbrcId, existingReaderMac);
			ReaderInfo replacementReader = null;
			
			if (this.unassignedReplacement) // get it from the global server
				replacementReader = XbrmsUtils.getRestCaller().getUnassignedReaderByMac(replacementReaderMac);
			else // get it from the park server
				replacementReader = caller.getReaderByMac(xbrcId, replacementReaderMac);
			
            if(!existingReader.getName().equalsIgnoreCase(replacementReader.getName())) 
            {
            	// invoke the replacement on the selected xBRC
                int statusCode = caller.replaceReader(xbrcId, existingReader, replacementReader);
                
                if (statusCode <= 200)
                {
	                // remove the reader from the global xBRMSs unassigned readers cache
	                if (this.unassignedReplacement)
	                	XbrmsUtils.getRestCaller().deleteUnassignedReaderFromCache(replacementReaderMac);
                }
            }
            else
            {
            	this.errMsg = "Replacement cannot be performed for readers with identical names. Please, rename one of the readers first.";
            	return INPUT;
            }
		}
		catch (UniformInterfaceException e)
		{
			ClientResponse response = e.getResponse();
			String message = response.getEntity(String.class);
			int statusCode = response.getStatus();
			
			this.errMsg = message == null ? "HTTP status code: " + statusCode: message;
			
			if (logger.isDebugEnabled())
				logger.error("Failed to replace reader " + existingReaderMac + " with reader " + replacementReaderMac
						+ ". Cause: " + this.errMsg, e);
			else
				logger.error("Failed to replace reader " + existingReaderMac + " with reader " + replacementReaderMac
						+ ". Cause: " + this.errMsg);
			
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String lightUpReader()
	{
		if (replacementReaderMac == null || replacementReaderMac.isEmpty() ||
			parkId == null || parkId.isEmpty() ||
			xbrcId == null || xbrcId.isEmpty())
		{
			this.errMsg = "invalid request.";
			return INPUT;
		}
		
		try 
		{
			IRestCall caller = XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest()) 
					? XbrmsUtils.getRestCaller(parkId) : XbrmsUtils.getRestCaller();
					
			int requestStatusCode = 500;
			if (this.unassignedReplacement){ // get it from the global service
				requestStatusCode = XbrmsUtils.getRestCaller().identifyReaderComp(PkConstants.XBRMS_KEY, replacementReaderMac);
			} else { // get it from the park server
				ReaderInfo replacementReader = caller.getReaderByMac(xbrcId, replacementReaderMac);
				requestStatusCode = caller.identifyReaderComp(xbrcId, "" + replacementReader.getId());
			}
		
			if (requestStatusCode < 0)
			{
				this.errMsg = "unable to encode sequence name.";
				return ERROR;
			}
        }
		catch (HTTPException e)
		{
			String excMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : null;
			
			switch (e.getStatusCode())
			{
				case 404: this.errMsg = "reader not found."; break;
				case 501: this.errMsg = "reader type with no light to light up."; break;
				default: this.errMsg = "HTTP status code: " + e.getStatusCode();
			}
			
			if (logger.isDebugEnabled())
				logger.error("Failed attempt to light up a replacement reader " + replacementReaderMac
						+ ". Cause: " + excMessage == null ? " unknown" : " " + excMessage, e);
			else
				logger.error("Failed attempt to light up a replacement reader " + replacementReaderMac
						+ ". Cause: " + excMessage == null ? " unknown" : " " + excMessage);
			
			return ERROR;
		}
        catch (Exception e) 
        {
            this.errMsg = e.toString();
            return ERROR;
        }
		
		return SUCCESS;
	}
	
	private ReaderDpo initReader(ReaderInfo ri)
	{
		if (ri == null)
			return null;
		
		ReaderDpo reader = new ReaderDpo();
		reader.setId("" + ri.getId());
		reader.setMac(ri.getMacAddress());
		reader.setIp(ri.getIpAddress());
		reader.setName(ri.getName());
		reader.setType(ri.getType().getDescription());
		reader.setStatus(ri.getStatus().name());
		reader.setStatusMessage(ri.getStatusMessage());
		
		return reader;
	}

	public Map<String, String> getParks()
	{
		return parks;
	}

	public Map<String, Collection<XbrcDto>> getVenues()
	{
		return venues;
	}

	public Collection<ReaderDpo> getExistingReaders()
	{
		return existingReaders;
	}

	public Collection<ReaderDpo> getReplacementUnlinkedReaders()
	{
		return replacementUnlinkedReaders;
	}

	public String getParkId()
	{
		return parkId;
	}

	public void setParkId(String parkId)
	{
		this.parkId = parkId;
	}

	public String getXbrcId()
	{
		return xbrcId;
	}

	public void setXbrcId(String xbrcId)
	{
		this.xbrcId = xbrcId;
	}

	public String getExistingReaderMac()
	{
		return existingReaderMac;
	}

	public String getReplacementReaderMac()
	{
		return replacementReaderMac;
	}

	public void setExistingReaderMac(String existingReaderMac)
	{
		this.existingReaderMac = existingReaderMac;
	}

	public void setReplacementReaderMac(String replacementReaderMac)
	{
		this.replacementReaderMac = replacementReaderMac;
	}

	public ReaderDpo getExistingReader()
	{
		return existingReader;
	}

	public void setExistingReader(ReaderDpo existingReader)
	{
		this.existingReader = existingReader;
	}

	public Collection<ReaderDpo> getReplacementUnassignedReaders()
	{
		return replacementUnassignedReaders;
	}

	public boolean isUnassignedReplacement()
	{
		return unassignedReplacement;
	}

	public void setUnassignedReplacement(boolean unassignedReplacement)
	{
		this.unassignedReplacement = unassignedReplacement;
	}
	
	public void setFilter(boolean filter)
	{
		this.filter = filter;
	}
	
	public boolean getFilter()
	{
		return this.filter;
	}
}
