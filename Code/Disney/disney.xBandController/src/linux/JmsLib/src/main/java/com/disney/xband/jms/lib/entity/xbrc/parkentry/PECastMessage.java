package com.disney.xband.jms.lib.entity.xbrc.parkentry;

import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.xbrc.XbrcMessage;

@XmlRootElement(name="message")
public class PECastMessage extends XbrcMessage
{
	private static final long serialVersionUID = 1L;
	
	private String portalId;
	private String locationName;
	
	public String getPortalId()
	{
		return portalId;
	}
	public void setPortalId(String portalId)
	{
		this.portalId = portalId;
	}
	public String getLocationName()
	{
		return locationName;
	}
	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}
}
