package com.disney.xband.xbrc.attractionmodel;

public class VehicleIndex
{
	private Vehicle vehicle;
	private int eventIndex = 0;
	
	public VehicleIndex(Vehicle vehicle)
	{
		this.vehicle = vehicle;
	}
	
	public int getEventIndex()
	{
		return eventIndex;
	}

	public void setEventIndex(int eventIndex)
	{
		this.eventIndex = eventIndex;
	}

	public Vehicle getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle)
	{
		this.vehicle = vehicle;
	}
}
