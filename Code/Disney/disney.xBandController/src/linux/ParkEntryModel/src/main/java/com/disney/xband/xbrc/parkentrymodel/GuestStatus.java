package com.disney.xband.xbrc.parkentrymodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.lib.castapp.Message;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;

@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({GuestStatusState.class})
@XmlRootElement(name="guest")
public class GuestStatus<State>
{
	private static Date timeMin = new Date(Long.MIN_VALUE);

	private State state;
	private Date timeLastEvent;
	private String sLastReader;
	private String lastLrrReader;
	private Integer fpScanCount = 0;
	private Date timeEarliestAtReader;
	private Date timeLatestAtReader;
	private Date timeLatestAtLrrReader;
	private Date timeArrived;
	private Date timeDeparted;
	private boolean bHasXpass = false;
	private String sGuestID;
	private String sPidDecimal;
	private String sLinkID;
	private String sLinkIDType;
	private String sid;
	private Guest guest;
	private List<Celebration> celebrations;
	private String bandRfid;
	private Integer omniTransactionId;
	private PETransactionState peTransactionState;
	private Long temlateId;
	private Message entilementMsg;
	private AnswerCommand omniEntitlementResponse;
	private AnswerCommand omniBioResponse;
    private String bandType = "Unknown";
    private boolean sentArrivedMessage = false;

    public String getBandRfid() {
		return bandRfid;
	}

	public void setBandRfid(String bandRfid) {
		this.bandRfid = bandRfid;
	}

	private List<String> liSeen = new ArrayList<String>();
	
	public GuestStatus(State initialState)
	{
		this.state = initialState;
		this.sLastReader = "";
		this.timeEarliestAtReader =
				this.timeArrived = 
				this.timeDeparted =
				this.timeLastEvent =
				this.timeLatestAtReader = timeMin;
		this.sGuestID = null;
		this.sLinkID = null;
		this.sLinkIDType = null;
		this.guest = null;
		this.omniTransactionId = 0;
		this.peTransactionState = null;
	}
	
	// for XML serialization
	public GuestStatus()
	{		
	}

	@XmlElement(name="state")
	public State getState()
	{
		return state;
	}
	
	public void setState(State state)
	{
		this.state = state; 
	}
	
	@XmlElement(name="timeLastEvent")
	public Date getTimeLastEvent()
	{
		return timeLastEvent;
	}
	
	public void setTimeLastEvent(Date timeLastEvent)
	{
		this.timeLastEvent = timeLastEvent;
	}
	
	@XmlElement(name="lastReader")
	public String getLastReader()
	{
		return sLastReader;
	}
	
	public void setLastReader(String sLastReader)
	{
		this.sLastReader = sLastReader;
	}
	
	@XmlElement(name="timeEarliestAtReader")
	public Date getTimeEarliestAtReader()
	{
		return timeEarliestAtReader;
	}
	
	public void setTimeEarliestAtReader(Date timeEarliestAtReader)
	{
		this.timeEarliestAtReader = timeEarliestAtReader;
	}
	
	@XmlElement(name="timeLatestAtReader")
	public Date getTimeLatestAtReader()
	{
		return timeLatestAtReader;
	}
	
	public void setTimeLatestAtReader(Date timeLatestAtReader)
	{
		this.timeLatestAtReader = timeLatestAtReader;
	}

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
		if (IsSet(this.timeArrived) && IsSet(this.timeDeparted))
			return (int)(this.timeDeparted.getTime() - this.timeArrived.getTime()) / 1000;
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
	
	@XmlElement(name="guestId")
	public String getGuestID()
	{
		return this.sGuestID;
	}
	
	public void setGuestID(String sGuestID)
	{
		this.sGuestID = sGuestID;
	}
	
	@XmlElement(name="pidDecimal")
	public String getPidDecimal()
	{
		return this.sPidDecimal;
	}
	
	public void setPidDecimal(String sPidDecimal)
	{
		this.sPidDecimal = sPidDecimal;
	}
	
	@XmlElement(name="linkId")
	public String getLinkID()
	{
		return this.sLinkID;
	}
	
	public void setLinkID(String sLinkID)
	{
		this.sLinkID = sLinkID;
	}
	
	@XmlElement(name="linkIdType")
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

	@XmlElement(name="fpScanCount")
	public Integer getFpScanCount() {
		return fpScanCount;
	}

	public void setFpScanCount(Integer fpScanCount) {
		this.fpScanCount = fpScanCount;
	}

	@XmlElement(name="timeArrived")
	public Date getTimeArrived() {
		return timeArrived;
	}

	public void setTimeArrived(Date timeArrived) {
		this.timeArrived = timeArrived;
	}

	public Date getTimeDeparted() {
		return timeDeparted;
	}

	public void setTimeDeparted(Date timeDeparted) {
		this.timeDeparted = timeDeparted;
	}
	
	@XmlElement(name="omniTransactionId")
	public Integer getOmniTransactionId() {
		return omniTransactionId;
	}

	public void setOmniTransactionId(Integer omniTransactionId) {
		this.omniTransactionId = omniTransactionId;
	}
	
	public Integer nextOmniTransactionId() {
		return ++omniTransactionId;
	}

	public PETransactionState getPeTransactionState() {
		return peTransactionState;
	}
	
	public void setPeTransactionState(PETransactionState peTransactionState) {
		this.peTransactionState = peTransactionState;
	}

	@XmlElement(name="templateId")
	public Long getTemlateId() {
		return temlateId;
	}

	public void setTemlateId(Long temlateId) {
		this.temlateId = temlateId;
	}

	@XmlElement(name="entitlementMsg")
	public Message getEntilementMsg() {
		return entilementMsg;
	}

	public void setEntilementMsg(Message entilementMsg) {
		this.entilementMsg = entilementMsg;
	}
	
	public AnswerCommand getOmniEntitlementResponse() {
		return omniEntitlementResponse;
	}

	public void setOmniEntitlementResponse(AnswerCommand omniEntitlementResponse) {
		this.omniEntitlementResponse = omniEntitlementResponse;
	}
	
	public AnswerCommand getLastOmniEntitlementResponse() {
		if (omniBioResponse != null)
			return omniBioResponse;
		return omniEntitlementResponse;
	}

	public AnswerCommand getOmniBioResponse() {
		return omniBioResponse;
	}

	public void setOmniBioResponse(AnswerCommand omniBioResponse) {
		this.omniBioResponse = omniBioResponse;
	}

	@XmlElement(name="sid")
	public String getSecureIdEncrypted()
	{
		return XBRCController.getInstance().encrypt(this.sid);
	}
	
	public void setSecureIdEncrypted(String sData)
	{
		this.sid = XBRCController.getInstance().decrypt(sData);
	}
	
	public String getSid()
	{
		return sid;
	}

	public void setSid(String sid)
	{
		this.sid = sid;
	}

	@XmlElement(name="bandType")
    public String getBandType() {
        return bandType;
    }

    public void setBandType(String bandType) {
        this.bandType = bandType;
    }

	public List<Celebration> getCelebrations()
	{
		return celebrations;
	}

	public void setCelebrations(List<Celebration> celebrations)
	{
		this.celebrations = celebrations;
	}
	
	@Override
	public String toString() {
    	if (guest != null)
    		return guest.getFirstName() + " " + guest.getLastName() + " " + sGuestID;
    	return sGuestID;
    }

	public Date getTimeLatestAtLrrReader() {
		return timeLatestAtLrrReader;
	}

	public void setTimeLatestAtLrrReader(Date timeLatestAtLrrReader) {
		this.timeLatestAtLrrReader = timeLatestAtLrrReader;
	}
	
	@XmlElement(name="lastLrrReader")
	public String getLastLrrReader() {
		return lastLrrReader;
	}

	public void setLastLrrReader(String lastLrrReader) {
		this.lastLrrReader = lastLrrReader;
	}

	public boolean isSentArrivedMessage() {
		return sentArrivedMessage;
	}

	public void setSentArrivedMessage(boolean sentArrivedMessage) {
		this.sentArrivedMessage = sentArrivedMessage;
	}
}
