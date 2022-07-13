package com.disney.xband.xbrc.spacemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;
import org.apache.log4j.Logger;

@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({GuestStatusState.class})
@XmlRootElement(name="guest")
public class GuestStatus<State>
{
    private static Logger logger = Logger.getLogger(GuestStatus.class);
    private static Date timeMin = new Date(Long.MIN_VALUE);

	private State state;
	private Date timeLastEvent;
	private String sLastReader;
	private Date timeEarliestAtReader;
	private Date timeLatestAtReader;
	private Date timeEntered;
	private Date timeMerged;
	private Date timeLoaded;
	private Date timeExited;
	private boolean bHasXpass;
	private EventAggregate deferredEntryEvent;
	private String sGuestID;
	private String sPidDecimal;
	private String sLinkID;
	private String sLinkIDType;
	private Guest guest;
    private String bandType = "Unknown";
    private GSTLocationInfo gli = null;
    private boolean bandTransmitting = false;
    
    // Set to a negative number initial confidence could be zero.
    private Integer confidenceLastEvent = null;
	
	private List<String> liSeen = new ArrayList<String>();
    private List<EventAggregate> liEAEvents = new ArrayList<EventAggregate>();

	public GuestStatus()
	{
	}
	
    public GuestStatus(State initialState)
	{
		this.state = initialState;
		this.sLastReader = "";
		this.timeEarliestAtReader =
				this.timeEntered = 
				this.timeExited =
				this.timeLastEvent =
				this.timeLatestAtReader =
				this.timeLoaded = 
				this.timeMerged = timeMin;
		this.deferredEntryEvent = null;
		this.sGuestID = null;
		this.sPidDecimal = null;
		this.sLinkID = null;
		this.sLinkIDType = null;
		this.guest = null;
	}
    
	@XmlElement(namespace="", type=GuestStatusState.class)
	public State getState()
	{
		return state;
	}
	
	public void setState(State state)
	{
		this.state = state; 
	}

	@XmlTransient	
	public Date getTimeLastEvent()
	{
		return timeLastEvent;
	}
	
	public void setTimeLastEvent(Date timeLastEvent)
	{
		this.timeLastEvent = timeLastEvent;
	}
	
	public String getLastReader()
	{
		return sLastReader;
	}
	
	public void setLastReader(String sLastReader)
	{
		this.sLastReader = sLastReader;
	}
	
	public Date getTimeEarliestAtReader()
	{
		return timeEarliestAtReader;
	}
	
	public void setTimeEarliestAtReader(Date timeEarliestAtReader)
	{
		this.timeEarliestAtReader = timeEarliestAtReader;
	}
	
	public Date getTimeLatestAtReader()
	{
		return timeLatestAtReader;
	}
	
	public void setTimeLatestAtReader(Date timeLatestAtReader)
	{
		this.timeLatestAtReader = timeLatestAtReader;
	}
	
	public Date getTimeEntered()
	{
		return timeEntered;
	}
	
	public void setTimeEntered(Date timeEntered)
	{
		this.timeEntered = timeEntered;
	}
	
	public Date getTimeMerged()
	{
		return timeMerged;
	}
	
	public void setTimeMerged(Date timeMerged)
	{
		this.timeMerged = timeMerged;
	}
	
	public Date getTimeLoaded()
	{
		return timeLoaded;
	}
	
	public void setTimeLoaded(Date timeLoaded)
	{
		this.timeLoaded = timeLoaded;
	}
	
	public Date getTimeExited()
	{
		return timeExited;
	}
	
	public void setTimeExited(Date timeExited)
	{
		this.timeExited = timeExited;
	}

	@XmlElement(name="xpass")	
	public boolean getHasXpass()
	{
		return bHasXpass;
	}

	public void setHasXpass(boolean bHasXpass)
	{
		this.bHasXpass = bHasXpass;
	}
	
	public int getSecondsInAttraction()
	{
		if (IsSet(this.timeExited) && IsSet(this.timeEntered))
			return (int)(this.timeExited.getTime() - this.timeEntered.getTime()) / 1000;
		else
			return 0;
	}
	
	public int getSecondsWaitingToLoad()
	{
		if (IsSet(this.timeLoaded) && IsSet(this.timeEntered))
			return (int) (this.timeLoaded.getTime() - this.timeEntered.getTime()) / 1000;
		else
			return 0;
	}
	
	public int getSecondsWaitingToMerge()
	{
		if (IsSet(this.timeMerged) && IsSet(this.timeEntered))
			return (int) (this.timeMerged.getTime() - this.timeEntered.getTime()) / 1000;
		else
			return 0;
	}
	
	public void AddVisitedLocation(String sReaderName)
	{
		// get the location for the reader
		String sLocation = XBRCController.getInstance().getReader(sReaderName).getLocation().getName();
		if (!liSeen.contains(sLocation))
			liSeen.add(sLocation);
	}
	
	public boolean BeenThere(String sReaderName)
	{
		String sLocation = XBRCController.getInstance().getReader(sReaderName).getLocation().getName();
		return liSeen.contains(sLocation);
	}
	
	public void setDeferredEntryEvent(EventAggregate ev)
	{
		this.deferredEntryEvent = ev;
	}
	
	public EventAggregate getDeferredEntryEvent()
	{
		return this.deferredEntryEvent;
	}

	@XmlElement(name="id")	
	public String getGuestID()
	{
		return this.sGuestID;
	}
	
	public void setGuestID(String sGuestID)
	{
		this.sGuestID = sGuestID;
	}

	@XmlElement(name="publicid")	
	public String getPidDecimal()
	{
		return this.sPidDecimal;
	}
	
	public void setPidDecimal(String sPidDecimal)
	{
		this.sPidDecimal = sPidDecimal;
	}
	
	@XmlElement(name="linkid")
    public String getLinkID()
    {
            return this.sLinkID;
    }

    public void setLinkID(String sLinkID)
    {
            this.sLinkID = sLinkID;
    }

	@XmlElement(name="linkidtype")
    public String getLinkIDType()
    {
            return this.sLinkIDType;
    }

    public void setLinkIDType(String sLinkIDType)
    {
            this.sLinkIDType = sLinkIDType;
    }

	public Guest getGuest()
	{
		return this.guest;
	}
	
	public void setGuest(Guest guest)
	{
		this.guest = guest;
	}

	private boolean
	NotSet(Date date)
	{
		return date.compareTo(timeMin)==0;
		//return date==timeMin;
	}
	
	private boolean
	IsSet(Date date)
	{
		return !NotSet(date);
	}

    // TLP: I currently assume that the order of the events are from
    // oldest to newest.
    public EventAggregate getOldestEvent() 
    {
        EventAggregate ev = null;

        if ( liEAEvents.size() > 0 )
        {
            ev = liEAEvents.get(0);
        }

        return ev;
    }

    public List<EventAggregate> getEAEvents() 
    {
        return liEAEvents;
    }

    // Add events through this interface as this is a FILO queue.
    public void addEAEvent(EventAggregate ea)
    {
        logger.trace("Adding user event for: Guest=" + getGuestID() + ";time=" +
                ea.getTimestamp().getTime());
        liEAEvents.add(ea);
    }

    public void clearEAEvents()
    {
        logger.trace("Clearing all user events.");
        liEAEvents.clear();
    }

	@XmlElement(name="bandtype")    
    public String getBandType() 
	{
        return bandType;
    }

    public void setBandType(String bandType) 
    {
        this.bandType = bandType;
    }

    public Integer getConfidenceLastEvent() 
    {
        return confidenceLastEvent;
    }

    public void setConfidenceLastEvent(Integer confidenceLastEvent) 
    {
        this.confidenceLastEvent = confidenceLastEvent;
    }

    public long getRoleDetectDelay()
    {
        long delay = 0;

        if ( bandType != null )
        {
	        if ( bandType.equals("Guest"))
	            delay = ConfigOptions.INSTANCE.getControllerInfo().getGuestDetectDelay();
	        else if ( bandType.equals("Cast Member"))
	            delay = ConfigOptions.INSTANCE.getControllerInfo().getCastMemberDetectDelay();
	        else if (bandType.equals("Puck"))
	            delay = ConfigOptions.INSTANCE.getControllerInfo().getPuckDetectDelay();
        }

        logger.trace("Role Detect Delay for: Guest=" + getGuestID() + ";roleDetectDelay=" + delay);
        return delay;
    }

    public void ToQueue(int maximumEvents) 
    {
        liEAEvents = new LimitedQueue<EventAggregate>(maximumEvents);
    }
    
    @XmlElement(name="location")
    public GSTLocationInfo getGSTLocationInfo()
    {
    	return gli;
    }
    
    public void setGSTLocationInfo(GSTLocationInfo gli)
    {
    	this.gli = gli;
    }

	public boolean isBandTransmitting() {
		return bandTransmitting;
	}

	public void setBandTransmitting(boolean bandTransmitting) {
		this.bandTransmitting = bandTransmitting;
	}
	
	 @Override
    public String toString() {
    	if (guest != null)
    		return guest.getFirstName() + " " + guest.getLastName() + " " + sGuestID;
    	return sGuestID;
    }
}
