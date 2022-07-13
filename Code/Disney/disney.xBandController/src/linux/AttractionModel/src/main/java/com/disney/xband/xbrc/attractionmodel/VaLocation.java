package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.VaLocationConfig;
import com.disney.xband.xbrc.lib.model.LocationInfo;

public class VaLocation {
	
	private static Logger logger = Logger.getLogger(VaLocation.class);
	
	private VaLocationConfig config;
	private LocationInfo location;
	// Next vehicle sequence in a series of vehicles
	private int vehicleSequence = 0;
	// Need this for resetting the car ids
	private Date lastAvmsLaserEventTime = null;
	// The vehicle event identifying the current train.
	Vehicle currentTrain = null;	
	// GPIO events that need a VEHICLE event
	LinkedList<Vehicle> gpioWaitingForVehicle = null;	
	// GPIO/VID events that happened before the guest singulated to VA array.
	// This should rarely be needed, but if the GPIO/VID event is shifted in time
	// significantly due to physical installation, then we may need this functionality.
	LinkedList<Vehicle> vehiclesWaitingForGuests = null;
	// Keep track of missed events as best we can
	private int missedGPIOEvents = 0;
	private int missedVehicleEvents = 0;
	
	public VaLocation()
	{
		gpioWaitingForVehicle = new LinkedList<Vehicle>();
		vehiclesWaitingForGuests = new LinkedList<Vehicle>();
	}
	
	public boolean isExpireTrain(Date dtNow)
	{
		return  !countLaserBreaks() && currentTrain != null &&
				(dtNow.getTime() - currentTrain.getAdjustedEventTime().getTime()) > (config.getTrainTimeoutSec()*1000);
	}
	
	public void processTimerEvents(Date dtNow)
	{
		if (isExpireTrain(dtNow))
		{
			if (currentTrain != null)
				logger.trace("Expiring the current VEHICLE event for train " + currentTrain.getVehicleid());
			
			currentTrain = null;
			vehicleSequence = 0;
		}
		
		// Expire vehicles waiting for guest to show up
		Iterator<Vehicle> it = vehiclesWaitingForGuests.iterator();
		while (it.hasNext())
		{
			Vehicle v = it.next();
			if (dtNow.getTime() - v.getTimestamp().getTime() > config.getVehicleHoldTimeMs())
			{
				it.remove();
			}
		}
	}
	
	/*
	 * Guest needs to wait for the vehicle ID to be received from Vehicle ID system even after no band events were received for onridetimeout time. 
	 */
	public boolean gotVehicleId()
	{
		return !config.getRequireVehicleLaserEvent() || (currentTrain != null || countLaserBreaks());
	}
	
	public boolean countLaserBreaks() 
	{
		return config.getLaserBreaksBeforeVehicle() > 0 || config.getLaserBreaksAfterVehicle() > 0;
	}
	
	public int expectedLaserBreaks()
	{
		return config.getLaserBreaksBeforeVehicle() + config.getLaserBreaksAfterVehicle();
	}

	public VaLocationConfig getConfig() {
		return config;
	}

	public void setConfig(VaLocationConfig config) {
		this.config = config;
	}

	public LocationInfo getLocation() {
		return location;
	}

	public void setLocation(LocationInfo location) {
		this.location = location;
	}

	public int getVehicleSequence() {
		return vehicleSequence;
	}

	public void setVehicleSequence(int vehicleSequence) {
		this.vehicleSequence = vehicleSequence;
	}

	public Date getLastAvmsLaserEventTime() {
		return lastAvmsLaserEventTime;
	}

	public void setLastAvmsLaserEventTime(Date lastAvmsLaserEventTime) {
		this.lastAvmsLaserEventTime = lastAvmsLaserEventTime;
	}

	public Vehicle getCurrentTrain() {
		return currentTrain;
	}

	public void setCurrentTrain(Vehicle currentTrain) {
		this.currentTrain = currentTrain;
	}

	public LinkedList<Vehicle> getGpioWaitingForVehicle() {
		return gpioWaitingForVehicle;
	}

	public void setGpioWaitingForVehicle(LinkedList<Vehicle> gpioWaitingForVehicle) {
		this.gpioWaitingForVehicle = gpioWaitingForVehicle;
	}

	public LinkedList<Vehicle> getVehiclesWaitingForGuests() {
		return vehiclesWaitingForGuests;
	}

	public void setVehiclesWaitingForGuests(
			LinkedList<Vehicle> vehiclesWaitingForGuests) {
		this.vehiclesWaitingForGuests = vehiclesWaitingForGuests;
	}

	public int getMissedGPIOEvents() {
		return missedGPIOEvents;
	}

	public void setMissedGPIOEvents(int missedGPIOEvents) {
		this.missedGPIOEvents = missedGPIOEvents;
	}

	public int getMissedVehicleEvents() {
		return missedVehicleEvents;
	}

	public void setMissedVehicleEvents(int missedVehicleEvents) {
		this.missedVehicleEvents = missedVehicleEvents;
	}
}
