package com.disney.xband.jms.lib.entity.xbms;

import javax.xml.bind.annotation.XmlElement;

/*
"arrivalDate" : "2010-09-26T04:00:00Z",
"departureDate" : "2010-10-09T04:00:00Z",
"facilityId" : 280010388,
"travelSegmentId": 1234578,
"travelComponentId": 87654321
 */

public class ResortReservation 
{
	private String arrivalDate;
	private String departureDate;
	private long facilityId;
	private long travelSegmentId;
	private long travelComponentId;

	@XmlElement(name="arrivalDate")
	public String getArrivalDate()
	{
		return this.arrivalDate;
	}

	public void setArrivalDate(String arrivalDate)
	{
		this.arrivalDate = arrivalDate;
	}

	@XmlElement(name="departureDate")
	public String getDepartureDate()
	{
		return this.departureDate;
	}

	public void setDepartureDate(String departureDate)
	{
		this.departureDate = departureDate;
	}

	@XmlElement(name="facilityId")
	public long getFacilityId()
	{
		return this.facilityId;
	}

	public void setFacilityId(long facilityId)
	{
		this.facilityId = facilityId;
	}

	@XmlElement(name="travelSegmentId")
	public long getTravelSegmentId()
	{
		return this.travelSegmentId;
	}

	public void setTravelSegmentId(long travelSegmentId)
	{
		this.travelSegmentId = travelSegmentId;
	}

	@XmlElement(name="travelComponentId")
	public long getTravelComponentId()
	{
		return this.travelComponentId;
	}

	public void setTravelComponentId(long travelComponentId)
	{
		this.travelComponentId = travelComponentId;
	}

}
