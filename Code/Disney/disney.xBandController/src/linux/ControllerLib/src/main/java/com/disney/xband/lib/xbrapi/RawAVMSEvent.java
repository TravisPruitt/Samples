package com.disney.xband.lib.xbrapi;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * AMVS (Disneys' Automated Vehicle Maintenance System) vehicle reader event.
 */
@XmlRootElement(name="message")
public class RawAVMSEvent
{
	public static final String TYPE_VEHICLEHELLO = "VEHICLEHELLO";
	public static final String TYPE_SYSTEMHELLO = "SYSTEMHELLO";
	
	private String type;
	private Date time;
	private String vehicleid;
	private String attractionid;
	private String sceneid;
	private String locationid; 
	private Integer confidence;
	private String status;
	private String statusmessage;
	
	@XmlAttribute
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	@XmlAttribute
	public Date getTime()
	{
		return time;
	}
	public void setTime(Date time)
	{
		this.time = time;
	}
		
	public String getVehicleid()
	{
		return vehicleid;
	}
	public void setVehicleid(String vehicleid)
	{
		this.vehicleid = vehicleid;
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

	public String getStatus() 
	{
		return status;
	}

	public String getStatusmessage() 
	{
		return statusmessage;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

	public void setStatusmessage(String statusmessage) 
	{
		this.statusmessage = statusmessage;
	}
}
