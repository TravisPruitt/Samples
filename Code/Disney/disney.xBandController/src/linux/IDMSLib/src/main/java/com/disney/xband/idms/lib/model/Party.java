package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Party 
{
	private long partyId;
    private long primaryGuestId;
	private String partyName;
	private int count;
	private List<PartyMember> members;
	private LinkCollection links;
	
	@XmlElement(name="partyId")
	public long getPartyId()
	{
		return this.partyId;
	}
	
	public void setPartyId(long partyId)
	{
		this.partyId = partyId;
	}
	
	@XmlElement(name="primaryGuestId")
	public long getPrimaryGuestId() 
	{
		return primaryGuestId;
	}
	
	public void setPrimaryGuestId(long primaryGuestId) 
	{
		this.primaryGuestId = primaryGuestId;
	}
	
	@XmlElement(name="count")
	public int getCount() 
	{
		return count;
	}
	
	public void setCount(int count) 
	{
		this.count = count;
	}
	
	@XmlElement(name="members")
	public List<PartyMember> getMembers() 
	{
		return members;
	}
	
	public void setMembers(List<PartyMember> members)
	{
		this.members = members;
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks() 
	{
		return links;
	}
	
	public void setLinks(LinkCollection links) 
	{
		this.links = links;
	}
	
	@XmlElement(name="partyName")
	public String getPartyName()
	{
		return this.partyName;
	}
	
	public void setPartyName(String partyName)
	{
		this.partyName = partyName;
	}
}
