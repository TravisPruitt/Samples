package com.disney.xband.xbrc.lib.entity;

public class OmniServer
{
	private Long id;
	private String hostname;
	private Integer port;
	private String description;
	private Boolean active = Boolean.TRUE;	// active by default
	private String notActiveReason;
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getHostname()
	{
		return hostname;
	}
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	public Integer getPort()
	{
		return port;
	}
	public void setPort(Integer port)
	{
		this.port = port;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public Boolean getActive()
	{
		return active;
	}
	public void setActive(Boolean active)
	{
		this.active = active;
	}
	public String getNotActiveReason()
	{
		return notActiveReason;
	}
	public void setNotActiveReason(String notActiveReason)
	{
		this.notActiveReason = notActiveReason;
	}
}
