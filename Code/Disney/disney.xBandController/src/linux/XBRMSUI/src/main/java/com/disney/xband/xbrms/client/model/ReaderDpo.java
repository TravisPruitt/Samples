package com.disney.xband.xbrms.client.model;

public class ReaderDpo
{
	private String ip;
	private String mac;
	private String id;
	private String name;
	private String type;
	private String status;
	private String statusMessage;
	
	public String getIp()
	{
		return ip;
	}
	public String getMac()
	{
		return mac;
	}
	public String getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public String getType()
	{
		return type;
	}
	public String getStatus()
	{
		return status;
	}
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public void setMac(String mac)
	{
		this.mac = mac;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatusMessage()
	{
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
	
}
