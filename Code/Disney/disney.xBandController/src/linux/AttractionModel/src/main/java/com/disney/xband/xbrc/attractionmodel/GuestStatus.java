package com.disney.xband.xbrc.attractionmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.model.BandMediaType;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;

@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({GuestStatusState.class})
@XmlRootElement(name="guest")
public class GuestStatus<State>
{
    private static Logger logger = Logger.getLogger(GuestStatus.class);
	private static Date timeMin = new Date(Long.MIN_VALUE);

	private State state;
	private String sLastReader;
	private Double avgSsAtReader;
	private Long maxSsAtReader;
	private Date timeEarliestAtReader;
	private Date timeLatestAtReader;
	private Date timeEntered;
	private Date timeMerged;
	private Date timeLoaded;
	private Date timeExited;
	private boolean bHasXpass;
	private String sCarId;
	private Date carIdTime;
	private String sLRID;
	private EventAggregate deferredEntryEvent;
	private String sGuestID;
	private String sPidDecimal;
	private String sLinkID;
	private String sLinkIDType;
	private String sSecureId;
	private Guest guest;
	private BandMediaType bandMediaType = BandMediaType.Unknown;
    private String bandType = "Unknown";
    private GSTLocationInfo gli = null;
    private boolean bandTransmitting = false;
    private ConfidenceEventAggregate lastConfidenceState;
    private Integer lastSentConfidenceValue = null;

    // Keep track of vehicles that guest can possibly be associated with
	private List<VehicleIndex> vehicles = new LinkedList<VehicleIndex>();
	
	// Keep track of singulated events at a location.
	private ArrayList<LRREventAggregate> eventsAtLocation = new ArrayList<LRREventAggregate>();
    private List<EventAggregate> confidenceEventsAtLocation = new ArrayList<EventAggregate>();

	private List<String> liSeen = new ArrayList<String>();
	
	public GuestStatus()
	{
	}
	
	public GuestStatus(State initialState)
	{
		this.state = initialState;
		this.sCarId = "";
		this.sLastReader = "";
		this.timeEarliestAtReader =
		this.timeEntered = 
		this.timeExited =		
		this.timeLatestAtReader =
		this.timeLoaded = 
		this.timeMerged = timeMin;
		this.deferredEntryEvent = null;
		this.sGuestID = null;
		this.sPidDecimal = null;
		this.sLinkID = null;
		this.sLinkIDType = null;
		this.sSecureId = null;
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
	
	public String getCarId()
	{
		return sCarId;
	}
	
	public void setCarId(String sCarId)
	{
		this.sCarId = sCarId;
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
	
	@XmlElement(name="pid")
	public String getPidDecimal()
	{
		return this.sPidDecimal;
	}
	
	public void setPidDecimal(String sPidDecimal)
	{
		this.sPidDecimal = sPidDecimal;
	}

	@XmlElement(name="sid")
	public String getSecureIdEncrypted()
	{
		return XBRCController.getInstance().encrypt(this.sSecureId);
	}
	
	public void setSecureIdEncrypted(String sData)
	{
		this.sSecureId = XBRCController.getInstance().decrypt(sData);
	}
	
	public String getSecureId()
	{
		return this.sSecureId;
	}
	
	public void setSecureId(String sSecureId)
	{
		this.sSecureId = sSecureId;
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

	public Double getAvgSsAtReader()
	{
		return avgSsAtReader;
	}

	public void setAvgSsAtReader(Double avgSsAtReader)
	{
		this.avgSsAtReader = avgSsAtReader;
	}

	public Long getMaxSsAtReader()
	{
		return maxSsAtReader;
	}

	public void setMaxSsAtReader(Long maxSsAtReader)
	{
		this.maxSsAtReader = maxSsAtReader;
	}

	public List<VehicleIndex> getVehicles()
	{
		return vehicles;
	}

	public void setVehicles(List<VehicleIndex> vehicles)
	{
		this.vehicles = vehicles;
	}

	public ArrayList<LRREventAggregate> getEventsAtLocation()
	{
		return eventsAtLocation;
	}

	public void setEventsAtLocation(ArrayList<LRREventAggregate> eventsAtLocation)
	{
		this.eventsAtLocation = eventsAtLocation;
	}

	public BandMediaType getBandMediaType()
	{
		return bandMediaType;
	}

	public void setBandMediaType(BandMediaType bandMediaType)
	{
		this.bandMediaType = bandMediaType;
	}

	public String getLRID()
	{
		return sLRID;
	}

	public void setLRID(String sLRID)
	{
		this.sLRID = sLRID;
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

    public EventAggregate getOldestConfidenceEvent()
    {
        EventAggregate ev = null;

        if ( confidenceEventsAtLocation.size() > 0 )
        {
            ev = confidenceEventsAtLocation.get(0);
        }

        return ev;
    }
    
    public EventAggregate getNewestConfidenceEvent()
    {
        EventAggregate ev = null;

        if ( confidenceEventsAtLocation.size() > 0 )
        {
            ev = confidenceEventsAtLocation.get(confidenceEventsAtLocation.size()-1);
        }

        return ev;
    }

    public List<EventAggregate> getConfidenceEvents()
    {
        return confidenceEventsAtLocation;
    }

    // Add events through this interface as this is a FILO queue.
    public void addConfidenceEvent(EventAggregate ea)
    {
        logger.trace("Adding user event for: Guest=" + getGuestID() + ";time=" +
                ea.getTimestamp().getTime());
        confidenceEventsAtLocation.add(ea);
    }

    public void clearConfidenceEvents()
    {
        logger.trace("Clearing all user events.");
        confidenceEventsAtLocation.clear();
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

    public long getRoleDetectDelay( EventsLocationConfig locationConfig )
    {
        long delay = 0;

        if ( bandType != null )
        {
            if ( bandType.equals("Guest"))
                delay = locationConfig.getGuestDetectDelay();
            else if ( bandType.equals("Cast Member"))
                delay = locationConfig.getCastMemberDetectDelay();
            else if (bandType.equals("Puck"))
                delay = locationConfig.getPuckDetectDelay();
        }

        logger.trace("Role Detect Delay for: Guest=" + getGuestID() + ";roleDetectDelay=" + delay);
        return delay;
    }
    
    @Override
    public String toString() {
    	if (guest != null)
    		return guest.getFirstName() + " " + guest.getLastName() + " " + sGuestID;
    	return sGuestID;
    }

	@XmlElement(name="bandtransmitting")
	public boolean isBandTransmitting() {
		return bandTransmitting;
	}

	public void setBandTransmitting(boolean bandTransmitting) {
		this.bandTransmitting = bandTransmitting;
	}

	@XmlTransient
	public ConfidenceEventAggregate getLastConfidenceState() {
		return lastConfidenceState;
	}

	public void setLastConfidenceState(ConfidenceEventAggregate lastConfidenceState) {
		this.lastConfidenceState = lastConfidenceState;
	}

	@XmlTransient
	public Integer getLastSentConfidenceValue() {
		return lastSentConfidenceValue;
	}

	public void setLastSentConfidenceValue(Integer lastSentConfidenceValue) {
		this.lastSentConfidenceValue = lastSentConfidenceValue;
	}

	public Date getCarIdTime() {
		return carIdTime;
	}

	public void setCarIdTime(Date carIdTime) {
		this.carIdTime = carIdTime;
	}
}
