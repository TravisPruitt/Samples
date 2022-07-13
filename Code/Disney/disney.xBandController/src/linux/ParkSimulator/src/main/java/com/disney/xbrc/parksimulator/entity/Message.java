package com.disney.xbrc.parksimulator.entity;

public class Message
{
	private static long idNext = 0;
	
	private long id;
	private String sFacility;
	private String sType;
	private String sPayload;
	
	public static Message create(String sFacility, String sType, String sPayload)
	{
		Message msg = new Message();
		msg.setId(idNext++);
		msg.setFacility(sFacility);
		msg.setType(sType);
		msg.setPayload(sPayload);
		
		return msg;
	}
	
	private Message()
	{
	}
	
	public long getId()
	{
		return id;
	}
	
	private void setId(long id)
	{
		this.id = id;
	}
	
	public String getFacility()
	{
		return sFacility;
	}
	
	private void setFacility(String sFacility)
	{
		this.sFacility = sFacility;
	}
	
	public String getType()
	{
		return sType;
	}
	
	private void setType(String sType)
	{
		this.sType = sType;
	}
	
	public String getPayload()
	{
		return sPayload;
	}
	
	private void setPayload(String sPayload)
	{
		this.sPayload = sPayload;
	}
	

}
