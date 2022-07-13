package com.disney.xband.xbrc.attractionmodel;

import com.disney.xband.xbrc.lib.model.MessageArgs;

public class InVehicleMessageArgs extends MessageArgs
{
	private Vehicle vehicle;
	
	public Vehicle getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle)
	{
		this.vehicle = vehicle;
	}

	public InVehicleMessageArgs(Vehicle vehicle, boolean bxPass, String GuestID, String PublicID, String LinkID, String LinkIDType, String BandType)
	{
		super(vehicle.getEventAggregate(), bxPass, GuestID, PublicID, LinkID, LinkIDType, BandType);
		this.vehicle = vehicle;
	}
}
