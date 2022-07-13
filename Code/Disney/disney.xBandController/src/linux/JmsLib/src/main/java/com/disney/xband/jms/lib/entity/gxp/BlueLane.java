package com.disney.xband.jms.lib.entity.gxp;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*        <blueLane>
            <xbandId>0776810827978534</xbandId>
            <gxpEntertainmentId>80010110</gxpEntertainmentId>
            <reason>{No Valid Park Admission || No Xpass || Early || Late || Different Entertainment}</reason>
            <tapTime>Fri Nov 18 15:19:11 EST 2011</tapTime>
            <facilityId>80007944</facilityId>
        </blueLane>
*/

@XmlRootElement(name="blueLane")
public class BlueLane implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	private String xbandId;
	
	private String gxpEntertainmentId;
	
	private String reason;
	
	private String tapTime;
	
	private String facilityId;
	
	@XmlElement(name="xbandId")
	public String getXbandId()
	{
		return this.xbandId;
	}
	
	public void setXbandId(String xbandId)
	{
		this.xbandId = xbandId;
	}
	
	@XmlElement(name="gxpEntertainmentId")
	public String getGxpEntertainmentId()
	{
		return this.gxpEntertainmentId;
	}
	
	public void setGxpEntertainmentId(String gxpEntertainmentId)
	{
		this.gxpEntertainmentId = gxpEntertainmentId;
	}
	
	@XmlElement(name="reason")
	public String getReason()
	{
		return this.reason;
	}
	
	public void setReason(String reason)
	{
		this.reason = reason;
	}
	
	@XmlElement(name="tapTime")
	public String getTapTime()
	{
		return this.tapTime;
	}
	
	public void setTapTime(String tapTime)
	{
		this.tapTime = tapTime;
	}

	@XmlElement(name="facilityId")
	public String getFacilityId()
	{
		return this.facilityId;
	}
	
	public void setFacilityId(String facilityId)
	{
		this.facilityId = facilityId;
	}	
}
