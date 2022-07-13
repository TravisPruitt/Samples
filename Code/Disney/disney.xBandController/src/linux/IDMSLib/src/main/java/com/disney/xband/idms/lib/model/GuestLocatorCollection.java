package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class GuestLocatorCollection
{
	private List<String> guestLocators;

	@XmlElement(name = "guestLocators")
	public List<String> getGuestLocators()
	{
		return this.guestLocators;
	}

	public void setGuestLocators(List<String> guestLocators)
	{
		this.guestLocators = guestLocators;
	}
}
