package com.disney.xband.jms.lib.entity.gff;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 <businessEvent>
    <location>GXP.BLUELANE</location>
    <eventType>Entitlement</eventType>
    <subType>BlueLane</subType>
    <referenceId>0776810827978534</referenceId>
    <guestIdentifier>0776810827978534</guestIdentifier>
    <timeStamp>2011-11-18T15:19:11Z</timeStamp>
    <payLoad><![CDATA[
        <blueLane>
            <xbandId>0776810827978534</xbandId>
            <gxpEntertainmentId>80010110</gxpEntertainmentId>
            <reason>{No Valid Park Admission || No Xpass || Early || Late || Different Entertainment}</reason>
            <tapTime>Fri Nov 18 15:19:11 EST 2011</tapTime>
            <facilityId>80007944</facilityId>
        </blueLane>
    ]]></payLoad>
    <correlationId>074321ff-5af5-44a5-85dd-fb8bf2b8980c</correlationId>
</businessEvent>


 */

@XmlRootElement(name="businessEvent")
public class BusinessEvent implements Serializable 
{

	private static final long serialVersionUID = 1L;
	
	private String location;

	private String eventType;
	
	private String subType;
	
	private String referenceId;
	
	private String guestIdentifier;


    private String facilityId;
	
	private String timeStamp;
	
	private String correlationId;
	
	private String payload;
    private String facilityOneSourceId;

    @XmlElement(name="location")
	public String getLocation()
	{
		return this.location;
	}
	
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	@XmlElement(name="event_type")
	public String getEventType()
	{
		return this.eventType;
	}
	
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}
	
	@XmlElement(name="subtype")
	public String getSubType()
	{
		return this.subType;
	}
	
	public void setSubType(String subType)
	{
		this.subType = subType;
	}
	
	@XmlElement(name="referenceid")
	public String getReferenceId()
	{
		return this.referenceId;
	}
	
	public void setReferenceId(String referenceId)
	{
		this.referenceId = referenceId;
	}
	
	@XmlElement(name="guest_identifier")
	public String getGuestIdentifier()
	{
		return this.guestIdentifier;
	}
	
	public void setGuestIdentifier(String guestIdentifier)
	{
		this.guestIdentifier = guestIdentifier;
	}
	
	@XmlElement(name="timestamp")
	public String getTimeStamp()
	{
		return this.timeStamp;
	}
	
	public void setTimeStamp(String timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	
	@XmlElement(name="correlationId")
	public String getCorrelationId()
	{
		return this.correlationId;
	}
	
	public void setCorrelationId(String correlationId)
	{
		this.correlationId = correlationId;
	}
	
	@XmlElement(name="payLoad")
	public String getPayLoad()
	{
		return this.payload;
	}
	
	public void setPayLoad(String payload)
	{
		this.payload = payload;
	}

    @XmlElement(name = "facilityid")
    public String getFacilityId() {
        if(facilityId == null) {
            return facilityOneSourceId;
        }
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @XmlElement(name = "facilityonesourceid")
    public String getFacilityOneSourceId() {
        return facilityOneSourceId;
    }


    public void setFacilityOneSourceId(String value) {
        this.facilityOneSourceId = value;
    }


}
