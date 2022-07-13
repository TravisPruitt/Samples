package com.disney.xband.idms.lib.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.data.GuestDataIdentifier;

@XmlRootElement
public class GuestIdentifierCollection {
	
	private List<GuestIdentifier> identifiers;
	private LinkCollection links;
	
	public GuestIdentifierCollection()
	{
	}

	public GuestIdentifierCollection(GuestData guest)
	{
		ArrayList<com.disney.xband.idms.lib.model.GuestIdentifier> identifiers = new ArrayList<com.disney.xband.idms.lib.model.GuestIdentifier>(0);
		List<GuestDataIdentifier> identifierList = guest.getIdentifierList(); 

		if (identifierList != null)
		{
			identifiers.ensureCapacity(identifierList.size());
			if (identifierList.size() > 0)
			{
				for (GuestDataIdentifier identifier : identifierList)
				{
					com.disney.xband.idms.lib.model.GuestIdentifier guestIdentifier = new com.disney.xband.idms.lib.model.GuestIdentifier();
					guestIdentifier.setGuestId(guest.getGuestId());
					guestIdentifier.setType(identifier.getType());
					guestIdentifier.setValue(identifier.getValue());
					identifiers.add(guestIdentifier);
				}
			}
			this.identifiers = identifiers;
		
			// Set the links object.
			LinkCollection links = new LinkCollection();
			Link self = new Link();
			self.setHref("/guest/" + guest.getGuestId()+ "/identifiers");
			links.setSelf(self);

			Link ownerProfile = new Link();
			ownerProfile.setHref("/guest/" + guest.getGuestId() + "/profile");

			links.setOwnerProfile(ownerProfile);
			this.links = links;
		}
	}
	
	@XmlElement(name="identifiers")
	public List<GuestIdentifier> getIdentifiers() {
		return identifiers;
	}
	
	@XmlElement(name="identifiers")
	public void setIdentifiers(List<GuestIdentifier> identifiers) {
		this.identifiers = identifiers;
	}
	
	@XmlElement(name="links")
	public LinkCollection getLinks() {
		return links;
	}
	
	@XmlElement(name="links")
	public void setLinks(LinkCollection links) {
		this.links = links;
	}
	
	

}
