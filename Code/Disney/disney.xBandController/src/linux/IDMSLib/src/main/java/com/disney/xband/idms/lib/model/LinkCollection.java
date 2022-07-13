package com.disney.xband.idms.lib.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class LinkCollection
{
	
	private Link self;
	private Link ownerProfile;
	private Link xbandRequest;
	private Link productIdReference;
	private Link nextActions;
	private Link history;
	private Link reservation;
	
	public LinkCollection()
	{
		
	}
	
	@XmlElement(name="self")
	public Link getSelf()
	{
		return this.self;
	}
	
	public void setSelf(Link self)
	{
		this.self = self;
	}
	
	@XmlElement(name="ownerProfile")
	public Link getOwnerProfile()
	{
		return this.ownerProfile;
	}
	
	public void setOwnerProfile(Link ownerProfile)
	{
		this.ownerProfile = ownerProfile;
	}
	
	@XmlElement(name="xbandRequest")
	public Link getXbandRequest()
	{
		return this.xbandRequest;
	}
	
	public void setXbandRequest(Link xbandRequest)
	{
		this.xbandRequest = xbandRequest;
	}
	
	@XmlElement(name="productIdReference")
	public Link getProductIdReference()
	{
		return this.productIdReference;
	}
	
	public void setProductIdReference(Link productIdReference)
	{
		this.productIdReference = productIdReference;
	}
	
	@XmlElement(name="nextAction")
	public Link getNextAction()
	{
		return this.nextActions;
	}
	
	public void setNextAction(Link nextAction)
	{
		this.nextActions = nextAction;
	}
	
	@XmlElement(name="history")
	public Link getHistory()
	{
		return this.history;
	}
	
	public void setHistory(Link history)
	{
		this.history = history;
	}

	@XmlElement(name="reservation")
	public Link getReservation()
	{
		return this.reservation;
	}
	
	public void setReservation(Link reservation)
	{
		this.reservation = reservation;
	}

}
