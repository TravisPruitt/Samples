package com.disney.xband.xbrc.parkentrymodel;

public class CastMemberCreds
{
	private String username = null;
	private String password = null;
	private String portalId = null;
	
	public CastMemberCreds(String username, String password, String portalId)
	{
		super();
		this.username = username;
		this.password = password;
		this.portalId = portalId;
	}
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getPortalId()
	{
		return portalId;
	}
	public void setPortalId(String portalId)
	{
		this.portalId = portalId;
	}
}
