package com.disney.xband.idms.lib.model.oneview;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CelebrationGuestPut 
{
	private int celebrationId;
	private String xid;
	private String role;
	
	@XmlElement(name="celebrationId")
	public int getCelebrationId() 
	{
		return this.celebrationId;
	}
	
	public void setCelebrationId(int celebrationId) 
	{
		this.celebrationId = celebrationId;
	}

	@XmlElement(name="xid")
	public String getXID() 
	{
		return this.xid;
	}
	
	public void setXID(String xid) 
	{
		this.xid = xid;
	}

	@XmlElement(name="role")
	public String getRole() 
	{
		return this.role;
	}
	
	public void setRole(String role) 
	{
		this.role = role;
	}
}
