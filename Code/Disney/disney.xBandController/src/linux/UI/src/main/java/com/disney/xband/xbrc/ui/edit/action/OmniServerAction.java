package com.disney.xband.xbrc.ui.edit.action;

import java.sql.Connection;
import java.util.Map;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.db.OmniServerService;
import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;

public class OmniServerAction extends BaseAction {

	private Map<OmniServer, Integer> omniServers;
	private OmniServer omniServer;
	private long omniServerId;
	private String omniServerHostname;
	private Map<Reader, Integer> readers;
	private Integer defaultOmniServerPort;
	
	@Override
	public String execute() throws Exception {		
		
		Connection conn = null;
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			
			omniServers = OmniServerService.getReaderCount(conn);
		}
		catch (Exception e)
		{
			this.addActionError("Failed to retrieve a list of configured omni servers.");
			LOG.error("Failed to retrieve a list of configured omni servers.", e);
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		try {
			defaultOmniServerPort = UIProperties.getInstance().getParkEntryConfig().getOmniTicketPort();
			omniServer = new OmniServer();
			omniServer.setPort(defaultOmniServerPort);
		} catch (Exception e){
			// missing default omni server port is not a problem
		}
		
		return SUCCESS;
	}
	
	public String delete() throws Exception {		
		
		Connection conn = null;
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			
			try
			{
				OmniServerService.delete(conn, omniServer.getId(), true);
			}
			catch (Exception e)
			{
				this.addActionError("Failed to add omni server " + (omniServer.getHostname() != null ? omniServer.getHostname() : ""));
				LOG.error("Failed to add omni server " + (omniServer.getHostname() != null ? omniServer.getHostname() : ""), e);
			}
			
			try
			{
				omniServers = OmniServerService.getReaderCount(conn);
			}
			catch (Exception e1)
			{
				this.addActionError("Failed to retrieve a list of omni servers.");
			}
		}
		catch (Exception e)
		{
			this.addActionError("Failed to delete omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)));
			LOG.error("Failed to delete omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)), e);
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String add() throws Exception {		
		
		Connection conn = null;
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			
			try
			{
				omniServer.setId(null);
				OmniServerService.save(conn, omniServer);
			}
			catch (Exception e)
			{
				this.addActionError("Failed to add omni server " + (omniServerHostname != null ? omniServerHostname : ""));
				LOG.error("Failed to add omni server " + (omniServerHostname != null ? omniServerHostname : ""), e);
			}
			
			try
			{
				omniServers = OmniServerService.getReaderCount(conn);
			}
			catch (Exception e1)
			{
				this.addActionError("Failed to retrieve a list of omni servers.");
				LOG.error("Failed to retrieve a list of omni servers.", e1);
			}
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String update() throws Exception {		
		
		Connection conn = null;
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			
			OmniServerService.update(conn, omniServer);
			
			omniServers = OmniServerService.getReaderCount(conn);
		}
		catch (Exception e)
		{
			this.addActionError("Failed to update omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)));
			LOG.error("Failed to update omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)), e);
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}
	
	public String edit() throws Exception {		
		
		Connection conn = null;
		try
		{
			conn = UIConnectionPool.getInstance().getConnection();
			
			omniServer = OmniServerService.find(conn, omniServerId);
			
			readers = OmniServerService.findReaders(conn, omniServerId);
		}
		catch (Exception e)
		{
			this.addActionError("Failed to update omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)));
			LOG.error("Failed to update omni server " + (omniServerHostname != null ? omniServerHostname : ("ID " + omniServerId)), e);
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		return SUCCESS;
	}

	public Map<OmniServer, Integer> getOmniServers()
	{
		return omniServers;
	}

	public OmniServer getOmniServer()
	{
		return omniServer;
	}

	public void setOmniServer(OmniServer omniServer)
	{
		this.omniServer = omniServer;
	}

	public long getOmniServerId()
	{
		return omniServerId;
	}

	public void setOmniServerId(long omniServerId)
	{
		this.omniServerId = omniServerId;
	}
	
	public String getOmniServerHostname()
	{
		return this.omniServerHostname;
	}
	public void setOmniServerHostname(String omniServerHostname)
	{
		this.omniServerHostname = omniServerHostname;
	}

	public Map<Reader, Integer> getReaders()
	{
		return readers;
	}

	public Integer getDefaultOmniServerPort()
	{
		return defaultOmniServerPort;
	}

	public void setDefaultOmniServerPort(Integer defaultOmniServerPort)
	{
		this.defaultOmniServerPort = defaultOmniServerPort;
	}
}
