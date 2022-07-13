package com.disney.xband.idms.lib.model.oneview;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement()
@XmlType(propOrder={"guest","xBands","entitlements","links"})
public class GuestData 
{
	private GuestDataGuest guest;
	//TODO: Change this to a property
	@XmlElement(name="xBands")
	public List<GuestDataXband> xBands;
	private List<GuestDataEntitlement> entitlements;
	private LinkCollection links;
	
	public GuestData()
	{
		this.links = new LinkCollection();
		this.xBands = new ArrayList<GuestDataXband>();
		this.entitlements = new ArrayList<GuestDataEntitlement>();
	}
	
	@XmlElement(name="guest")
	public GuestDataGuest getGuest()
	{
		return this.guest;
	}

	public void setGuest(GuestDataGuest guest)
	{
		this.guest = guest;
	}

/*	public List<GuestDataXband> getXBands()
	{
		return this.xBands;
	}

	public void setXBands(List<GuestDataXband> xBands)
	{
		this.xBands = xBands;
	}
*/
	@XmlElement(name="entitlements")
	public List<GuestDataEntitlement> getEntitlements()
	{
		return this.entitlements;
	}

	public void setEntitlements(List<GuestDataEntitlement> entitlements)
	{
		this.entitlements = entitlements;
	}

	@XmlElement(name="links")
	public LinkCollection getLinks() 
	{
		return this.links;
	}
	
	public void setLinks(LinkCollection links) 
	{
		this.links = links;
	}
}
