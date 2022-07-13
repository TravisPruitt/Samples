package com.disney.xband.jms.lib.entity.xbrc;

import javax.xml.bind.annotation.XmlElement;

public class InVehicleMessage extends EventMessage 
{	
	private static final long serialVersionUID = 1L;
	
	private String vehicle;
	
	private String car;
	
	private String row;
	
	private String seat;

	private String attractionId;
	
	private String locationId;
	
	private String confidence;
	
	private String sequence;

	@XmlElement(name="vehicle")
	public String getVehicle()
	{
		return this.vehicle;
	}
	
	public void setVehicle(String vehicle)
	{
		this.vehicle = vehicle;
	}
	
	@XmlElement(name="car")
	public String getCar()
	{
		return this.car;
	}
	
	public void setCar(String car)
	{
		this.car = car;
	}
	
	@XmlElement(name="row")
	public String getRow()
	{
		return this.row;
	}
	
	public void setRow(String row)
	{
		this.row = row;
	}
	
	@XmlElement(name="seat")
	public String getSeat()
	{
		return this.seat;
	}
	
	public void setSeat(String seat)
	{
		this.seat = seat;
	}
	
	@XmlElement(name="attractionid")
	public String getAttractionId()
	{
		return this.attractionId;
	}
	
	public void setAttractionId(String attractionId)
	{
		this.attractionId = attractionId;
	}
	
	@XmlElement(name="locationid")
	public String getLocationId()
	{
		return this.locationId;
	}
	
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}
	
	@XmlElement(name="confidence")
	public String getConfidence()
	{
		return this.confidence;
	}
	
	public void setConfidence(String confidence)
	{
		this.confidence = confidence;
	}
	
	@XmlElement(name="sequence")
	public String getSequence()
	{
		return this.sequence;
	}
	
	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}
}
