package com.disney.xband.jms.lib.entity.gxp;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;


/*<Response xmlns="http://gxpdapconfig/gxp-web/services/booking/xpass/678">
   <activities>
      <e>
         <activityId>500657</activityId>
         <activityStatus>Booked</activityStatus>
         <reason>
            <activityReason>Standard</activityReason>
            <activityReasonTime>2012-03-09T22:08:57Z</activityReasonTime>
         </reason>
      </e>
   </activities>
   <bookingGxpLinkId>500302</bookingGxpLinkId>
   <entertainments>
      <e>
         <entertainmentId>80010208</entertainmentId>
         <experienceType>Continuous</experienceType>
         <locationId>80010208</locationId>
      </e>
   </entertainments>
   <gxpLinkId>500302</gxpLinkId>
   <redemptionTimes>
      <e/>
   </redemptionTimes>
   <returnWindow>
      <endTime>2012-04-01T05:10:00Z</endTime>
      <startTime>2012-04-01T04:10:00Z</startTime>
   </returnWindow>
   <status>Booked</status>
   <totalRedemptionsAllowed>1</totalRedemptionsAllowed>
   <totalRedemptionsRemaining>1</totalRedemptionsRemaining>
   <xpassId>678</xpassId>
   <xpassType>STD</xpassType>
</Response>*/

@XmlRootElement
public class BookingResponse
{
	private int bookingGxpLinkId;
	private int gxpLinkId;
	
	private ReturnWindow returnWindow;

	private String status;
	private int totalRedemptionsAllowed;
	private int totalRedemptionsRemaining;
	private int xpassId;
	private String xpassType;
	
	private List<Entertainment> entertainments;
	
	@XmlTransient()
	public Object getActivities()
	{
		return null;
	}

	public void setActivities(Object activities)
	{
	}

	@XmlElement(name="bookingGxpLinkId")
	public int getBookingGxpLinkId()
	{
		return this.bookingGxpLinkId;
	}
	
	public void setBookingGxpLinkId(int bookingGxpLinkId)
	{
		this.bookingGxpLinkId = bookingGxpLinkId;
	}
	
	@XmlElementWrapper(name = "entertainments") 
	@XmlElements(@XmlElement(name="",type=Entertainment.class))
	public List<Entertainment> getEntertainments()
	{
		return this.entertainments;
	}
	
	public void setEntertainments(List<Entertainment> entertainments)
	{
		this.entertainments = entertainments;
	}
	
	@XmlElement(name="gxpLinkId")
	public int getGxpLinkId()
	{
		return this.gxpLinkId;
	}
	
	public void setGxpLinkId(int gxpLinkId)
	{
		this.gxpLinkId = gxpLinkId;
	}
	
	@XmlTransient()
	public Object getRedemptionTimes()
	{
		return null;
	}
	
	public void setRedemptionTimes(Object redepemptionTimes)
	{
	}
	
	@XmlElement(name="returnWindow")
	public ReturnWindow getReturnWindow()
	{
		return this.returnWindow;
	}
	
	public void setReturnWindow(ReturnWindow returnWindow)
	{
		this.returnWindow = returnWindow;
	}
	
	@XmlElement(name="status")
	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	@XmlElement(name="totalRedemptionsAllowed")
	public int getTotalRedemptionsAllowed()
	{
		return this.totalRedemptionsAllowed;
	}

	public void setTotalRedemptionsAllowed(int totalRedemptionsAllowed)
	{
		this.totalRedemptionsAllowed = totalRedemptionsAllowed;
	}

	@XmlElement(name="totalRedemptionsRemaining")
	public int getTotalRedemptionsRemaining()
	{
		return this.totalRedemptionsRemaining;
	}

	public void setTotalRedemptionsRemaining(int totalRedemptionsRemaining)
	{
		this.totalRedemptionsRemaining = totalRedemptionsRemaining;
	}

	@XmlElement(name="xpassId")
	public int getXpassId()
	{
		return this.xpassId;
	}

	public void setXpassId(int xpassId)
	{
		this.xpassId = xpassId;
	}

	//@XmlElement(name="xpassType")
	@XmlTransient
	public String getXpassType()
	{
		return this.xpassType;
	}
	
	public void setXpassType(String xpassType)
	{
		this.xpassType = xpassType;
	}
}
