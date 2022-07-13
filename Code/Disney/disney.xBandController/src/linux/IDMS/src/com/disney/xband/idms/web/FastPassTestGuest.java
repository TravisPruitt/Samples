package com.disney.xband.idms.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.idms.lib.data.GuestData;
import com.disney.xband.idms.lib.data.GuestDataIdentifier;

@XmlRootElement
public class FastPassTestGuest
{
	private FastPassTestGuestProfile profile;
	
	public FastPassTestGuest(GuestData guestData)
	{
		
		this.profile = new FastPassTestGuestProfile();
		
		this.profile.setMiddleName(guestData.getMiddleName());
		this.profile.setLastName(guestData.getLastName());
		this.profile.setFirstName(guestData.getFirstName());
		this.profile.setTitle(guestData.getTitle());
		this.profile.setSuffix(guestData.getSuffix());
		
		if (guestData.getIdentifierList() != null)
		{
			
			List<FastPassTestGuestIdentifier> identifiers = new ArrayList<FastPassTestGuestIdentifier>();
			 
			for (GuestDataIdentifier identifier : guestData.getIdentifierList())
			{
				FastPassTestGuestIdentifier guestIdentifier = new FastPassTestGuestIdentifier();
				guestIdentifier.setType(identifier.getType());
				guestIdentifier.setValue(identifier.getValue());
				identifiers.add(guestIdentifier);
			}
			
			this.profile.setIdentifiers(identifiers);
		}
	}

	@XmlElement(name="profile")
	public FastPassTestGuestProfile getProfile() 
	{
		return this.profile;
	}
	
	public void setProfile(FastPassTestGuestProfile profile) 
	{
		this.profile = profile;
	}

}
