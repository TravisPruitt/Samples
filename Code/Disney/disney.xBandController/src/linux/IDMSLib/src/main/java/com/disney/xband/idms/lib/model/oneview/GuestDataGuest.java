package com.disney.xband.idms.lib.model.oneview;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.LinkCollection;

@XmlRootElement
@XmlType(propOrder={"links","guestIdentifiers"})
public class GuestDataGuest 
{
	private LinkCollection links;
	private List<GuestDataGuestIdentifier> guestIdentifiers;
	
	public GuestDataGuest()
	{
		this.links = new LinkCollection();
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

	@XmlElement(name="guestIdentifiers")
	public List<GuestDataGuestIdentifier> getGuestIdentifiers() 
	{
		return guestIdentifiers;
	}
	
	public void setGuestIdentifiers(List<GuestDataGuestIdentifier> guestIdentifiers)
	{
		this.guestIdentifiers = guestIdentifiers;
		
		for(GuestDataGuestIdentifier guestIdentifier : this.guestIdentifiers)
		{
			if(guestIdentifier.getType().compareToIgnoreCase("xid") == 0)
			{
				Link self = new Link();
				self.setHref("/guest/" + guestIdentifier.getValue() + "/profile");
				
				this.links.setSelf(self);
			}
		}
	}
}
