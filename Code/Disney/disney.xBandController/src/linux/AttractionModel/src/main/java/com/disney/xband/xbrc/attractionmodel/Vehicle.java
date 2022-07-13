package com.disney.xband.xbrc.attractionmodel;

import java.util.Date;

import com.disney.xband.xbrc.lib.model.AVMSEventAggregate;
import com.disney.xband.xbrc.lib.model.EventAggregate;

public class Vehicle
{
	public static final String unknownVehicleid = "00000000"; 
	
	private int guestCount = 0;
	private String vehicleid;
	private Integer vehicleSequence;
	private Date adjustedEventTime;
	private EventAggregate eventAggregate;
	private int gpioEventCountBeforeVehicle = 0;
	private int gpioEventCountAfterVehicle = 0;
	
	private String attractionid;
	private String sceneid;
	private String locationid;
	private Integer confidence;	
	private Date readerEventTime;
	private Date timestamp;
	private Date trainReaderEventTime;
	private Date trainReaderTimestamp;
	
	public Vehicle(AVMSEventAggregate avmsEvent)
	{
		setAvmsEvent(avmsEvent);
	}
	
	public Vehicle(AvmsLaserEvent laserEvent)
	{
		this.readerEventTime = laserEvent.getReaderEventTime();
		this.timestamp = laserEvent.getTime();
		this.eventAggregate = laserEvent.getEventAggregate();
	}	
	
	public int getGuestCount()
	{
		return guestCount;
	}

	public void setGuestCount(int guestCount)
	{
		this.guestCount = guestCount;
	}

	public void setAvmsEvent(AVMSEventAggregate avmsEvent)
	{
		this.vehicleid = avmsEvent.getID();
		this.attractionid = avmsEvent.getAvmsEvent().getAttractionid();
		this.sceneid = avmsEvent.getAvmsEvent().getSceneid();
		this.locationid = avmsEvent.getAvmsEvent().getLocationid();
		this.confidence = avmsEvent.getAvmsEvent().getConfidence();
		this.timestamp = avmsEvent.getTimestamp();
		this.readerEventTime = avmsEvent.getAvmsEvent().getReaderEventTime();
		this.eventAggregate = avmsEvent; 
	}
	
	public void setTrainInfo(Vehicle train)
	{
		this.vehicleid = train.getVehicleid();
		this.attractionid = train.getAttractionid();
		this.sceneid = train.getSceneid();
		this.locationid = train.getLocationid();
		this.confidence = train.getConfidence();
		this.trainReaderEventTime = train.getReaderEventTime();
		this.trainReaderTimestamp = train.getTimestamp();
	}

	public Date getAdjustedEventTime()
	{
		return adjustedEventTime;
	}

	public void setAdjustedEventTime(Date adjustedEventTime)
	{
		this.adjustedEventTime = adjustedEventTime;
	}

	public Integer getVehicleSequence()
	{
		return vehicleSequence;
	}

	public void setVehicleSequence(int vehicleSequence)
	{
		this.vehicleSequence = vehicleSequence;
	}
	
	public String getVehicleid()
	{
		return vehicleid;
	}

	public void setVehicleid(String vehicleid)
	{
		this.vehicleid = vehicleid;
	}
	
	public String getCompositeID()
	{
		if (vehicleid != null)
		{
			if (vehicleSequence != null)
				return vehicleid + "-" + vehicleSequence;
			
			return vehicleid;
		}
			
		if (vehicleSequence != null)
			return unknownVehicleid + "-" + vehicleSequence;
		
		return unknownVehicleid;
	}

	public String getAttractionid()
	{
		return attractionid;
	}

	public void setAttractionid(String attractionid)
	{
		this.attractionid = attractionid;
	}

	public String getSceneid()
	{
		return sceneid;
	}

	public void setSceneid(String sceneid)
	{
		this.sceneid = sceneid;
	}

	public String getLocationid()
	{
		return locationid;
	}

	public void setLocationid(String locationid)
	{
		this.locationid = locationid;
	}

	public Integer getConfidence()
	{
		return confidence;
	}

	public void setConfidence(Integer confidence)
	{
		this.confidence = confidence;
	}

	public Date getReaderEventTime()
	{
		return readerEventTime;
	}

	public void setReaderEventTime(Date readerEventTime)
	{
		this.readerEventTime = readerEventTime;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setVehicleSequence(Integer vehicleSequence)
	{
		this.vehicleSequence = vehicleSequence;
	}

	public Date getTrainReaderEventTime()
	{
		return trainReaderEventTime;
	}

	public void setTrainReaderEventTime(Date trainReaderEventTime)
	{
		this.trainReaderEventTime = trainReaderEventTime;
	}

	public Date getTrainReaderTimestamp()
	{
		return trainReaderTimestamp;
	}

	public void setTrainReaderTimestamp(Date trainReaderTimestamp)
	{
		this.trainReaderTimestamp = trainReaderTimestamp;
	}

	public EventAggregate getEventAggregate()
	{
		return eventAggregate;
	}

	public void setEventAggregate(EventAggregate eventAggregate)
	{
		this.eventAggregate = eventAggregate;
	}

	public int getGpioEventCountBeforeVehicle()
	{
		return gpioEventCountBeforeVehicle;
	}

	public void setGpioEventCountBeforeVehicle(int gpioEventCountBeforeVehicle)
	{
		this.gpioEventCountBeforeVehicle = gpioEventCountBeforeVehicle;
	}

	public int getGpioEventCountAfterVehicle()
	{
		return gpioEventCountAfterVehicle;
	}

	public void setGpioEventCountAfterVehicle(int gpioEventCountAfterVehicle)
	{
		this.gpioEventCountAfterVehicle = gpioEventCountAfterVehicle;
	}
	
	public int getGpioEventCount()
	{
		return gpioEventCountBeforeVehicle + gpioEventCountAfterVehicle; 
	}
}
