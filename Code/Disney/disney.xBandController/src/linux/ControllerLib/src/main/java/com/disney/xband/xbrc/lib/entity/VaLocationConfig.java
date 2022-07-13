package com.disney.xband.xbrc.lib.entity;

import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;

public class VaLocationConfig {
	
	/*
	 * Vehicle association algorithms.
	 * nearevents - Calculate score based on band events immediately surrounding the GPIO/VEHICLE event. Highest score wins. 
	 * closestpeak - Calculate the peak time of the guest band pings. GPIO/VEHICLE event closest to the peak time wins.
	 * closestpeakfallback - Use only if nearevents algorithm produced no associations.
	 */
	public enum VaAlgorithm { nearevents, closestpeakfallback, closestpeak };
	
	@PersistName("locationid")
	@MetaData(name="locationid")
	private Long locationId;
	@PersistName("onridetimeoutsec")
	@MetaData(name="onridetimeoutsec", min = "0", defaultValue = "2")
	private Integer onrideTimeoutSec = 2;
	@PersistName("maxAnalyzeGuestEvents")
	@MetaData(name="maxAnalyzeGuestEvents", min="500", defaultValue="10000", max="100000")
	private Integer maxAnalyzeGuestEvents = 10000;
	@PersistName("maxAnalyzeGuestEventsPerVehicle")
	@MetaData(name="maxAnalyzeGuestEventsPerVehicle", min="2", defaultValue="2", max="100")
	private Integer maxAnalyzeGuestEventsPerVehicle = 2;
	@PersistName("useVehicleEventTime")
	@MetaData(name="useVehicleEventTime", defaultValue="false", description="Whether to use the timestamp from the Vehicle ID event or event receive time at xBRC.")
	private Boolean useVehicleEventTime = false;
	@PersistName("requireVehicleLaserEvent")
	@MetaData(name="requireVehicleLaserEvent", defaultValue="false", description="Whether to require GPIO events.")
	private Boolean requireVehicleLaserEvent = true;
	@PersistName("vehicleTimeOffsetMs")
	@MetaData(name="vehicleTimeOffsetMs", min = "-30000", defaultValue = "0", max="30000", description="Allows to shift the Vehicle ID time in case LRR read times are consistently off.")
	private Long vehicleTimeOffsetMs = 0l;	
	@PersistName("minReadsToAssociate")
	@MetaData(name="minReadsToAssociate", min="2", defaultValue="2", max="1000", description="Minimum number of Long range reads collected at location to attempt associating a guest to a vehicle.")
	private Integer minReadsToAssociate = 2;		
	@PersistName("trainTimeoutSec")
	@MetaData(name="trainTimeoutSec", min = "1", defaultValue = "20", max="3600", description="Time to associate vehicle laser breaks to a VEHICLE event before the next train arrives.")
	private Integer trainTimeoutSec = 20;
	@PersistName("laserbreaksbeforevehicle")
	@MetaData(name="laserbreaksbeforevehicle", min="0", defaultValue="0", max="1000", description="Number of laser break (GPIO) events preceeding each VEHICLE event. Set to 0 for variable number of laser breaks and to use trainTimeoutSec instead.")
	private Integer laserBreaksBeforeVehicle = 0;
	@PersistName("laserbreaksaftervehicle")
	@MetaData(name="laserbreaksaftervehicle", min="0", defaultValue="0", max="1000", description="Number of laser break (GPIO) events following each VEHICLE event. Set to 0 for variable number of laser breaks and to use trainTimeoutSec instead.")
	private Integer laserBreaksAfterVehicle = 0;		
    @PersistName("vehicleHoldTimeMs")
	@MetaData(name="vehicleHoldTimeMs", min = "0", defaultValue = "1000", max="60000", description="Allows to shift the VAMS time in case LRR read times are consistently off.")
	private Long vehicleHoldTimeMs = 1000l;
    @PersistName("vaAlgorithm")
	@MetaData(name="vaAlgorithm", defaultValue = "closestpeakfallback", choices="nearevents, closestpeakfallback, closestpeak", description="The vehicle association algorithm to use. Choices: default, closestpeakfallback, closestpeak")
    private String vaAlgorithm = VaAlgorithm.closestpeakfallback.name();
    @PersistName("carsPerTrain")
	@MetaData(name="carspertrain", min="0", defaultValue="0", max="1000", description="In attractions equipped with trains, the number of cars in a train.")
	private Integer carsPerTrain = 0;
    @PersistName("vaTimeoutSec")
	@MetaData(name="vaTimeoutSec", min = "0", defaultValue = "20", max="1200", description="After how many seconds a guest can be associated to another vehicle. This should essencially be the time it takes to go around the ride again.")
	private Integer vaTimeoutSec = 20;
    
	public Integer getOnrideTimeoutSec() {
		return onrideTimeoutSec;
	}
	public void setOnrideTimeoutSec(Integer onrideTimeoutSec) {
		this.onrideTimeoutSec = onrideTimeoutSec;
	}
	public Integer getMaxAnalyzeGuestEvents() {
		return maxAnalyzeGuestEvents;
	}
	public void setMaxAnalyzeGuestEvents(Integer maxAnalyzeGuestEvents) {
		this.maxAnalyzeGuestEvents = maxAnalyzeGuestEvents;
	}
	public Integer getMaxAnalyzeGuestEventsPerVehicle() {
		return maxAnalyzeGuestEventsPerVehicle;
	}
	public void setMaxAnalyzeGuestEventsPerVehicle(
			Integer maxAnalyzeGuestEventsPerVehicle) {
		this.maxAnalyzeGuestEventsPerVehicle = maxAnalyzeGuestEventsPerVehicle;
	}
	public Boolean getUseVehicleEventTime() {
		return useVehicleEventTime;
	}
	public void setUseVehicleEventTime(Boolean useVehicleEventTime) {
		this.useVehicleEventTime = useVehicleEventTime;
	}
	public Long getVehicleTimeOffsetMs() {
		return vehicleTimeOffsetMs;
	}
	public void setVehicleTimeOffsetMs(Long vehicleTimeOffsetMs) {
		this.vehicleTimeOffsetMs = vehicleTimeOffsetMs;
	}
	public Integer getMinReadsToAssociate() {
		return minReadsToAssociate;
	}
	public void setMinReadsToAssociate(Integer minReadsToAssociate) {
		this.minReadsToAssociate = minReadsToAssociate;
	}
	public Integer getTrainTimeoutSec() {
		return trainTimeoutSec;
	}
	public void setTrainTimeoutSec(Integer trainTimeoutSec) {
		this.trainTimeoutSec = trainTimeoutSec;
	}
	public Integer getLaserBreaksBeforeVehicle() {
		return laserBreaksBeforeVehicle;
	}
	public void setLaserBreaksBeforeVehicle(Integer laserBreaksBeforeVehicle) {
		this.laserBreaksBeforeVehicle = laserBreaksBeforeVehicle;
	}
	public Integer getLaserBreaksAfterVehicle() {
		return laserBreaksAfterVehicle;
	}
	public void setLaserBreaksAfterVehicle(Integer laserBreaksAfterVehicle) {
		this.laserBreaksAfterVehicle = laserBreaksAfterVehicle;
	}
	public Long getVehicleHoldTimeMs() {
		return vehicleHoldTimeMs;
	}
	public void setVehicleHoldTimeMs(Long vehicleHoldTimeMs) {
		this.vehicleHoldTimeMs = vehicleHoldTimeMs;
	}
	public String getVaAlgorithm() {
		return vaAlgorithm;
	}
	public void setVaAlgorithm(String vaAlgorithm) {
		this.vaAlgorithm = vaAlgorithm;
	}
	public VaAlgorithm getVaAlgorithmEnum()
	{
		try {
			return VaAlgorithm.valueOf(vaAlgorithm);
		}
		catch(Exception e)
		{
			return VaAlgorithm.closestpeakfallback;
		}
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	public Boolean getRequireVehicleLaserEvent() {
		return requireVehicleLaserEvent;
	}
	public void setRequireVehicleLaserEvent(Boolean requireVehicleLaserEvent) {
		this.requireVehicleLaserEvent = requireVehicleLaserEvent;
	}
	public Integer getCarsPerTrain() {
		return carsPerTrain;
	}
	public void setCarsPerTrain(Integer carsPerTrain) {
		this.carsPerTrain = carsPerTrain;
	}
	public Integer getVaTimeoutSec() {
		return vaTimeoutSec;
	}
	public void setVaTimeoutSec(Integer vaTimeoutSec) {
		this.vaTimeoutSec = vaTimeoutSec;
	}
}
