package com.disney.xband.xbrc.lib.entity;

import java.util.Date;

@Deprecated // Use GST instead
public class GuestStatus
{	
	private String guestId;
	private boolean hasXPass;
	private GuestStatusState state;
	private String lastReader;
	private Date timeEarliestAtReader;
	private Date timeLatestAtReader;
	private Date timeEntered;
	private Date timeMerged;
	private Date timeLoaded;
	private Date timeExited;
	private String CarID;
	private boolean hasDeferredEntry;
	
	public GuestStatus(String guestId, boolean hasXPass,
			GuestStatusState state, String lastReader,
			Date timeEarliestAtReader, Date timeLatestAtReader,
			Date timeEntered, Date timeMerged, Date timeLoaded,
			Date timeExited, String carID, boolean hasDeferredEntry) {
		super();
		this.guestId = guestId;
		this.hasXPass = hasXPass;
		this.state = state;
		this.lastReader = lastReader;
		this.timeEarliestAtReader = timeEarliestAtReader;
		this.timeLatestAtReader = timeLatestAtReader;
		this.timeEntered = timeEntered;
		this.timeMerged = timeMerged;
		this.timeLoaded = timeLoaded;
		this.timeExited = timeExited;
		CarID = carID;
		this.hasDeferredEntry = hasDeferredEntry;
	}
	
	public String getGuestId() {
		return guestId;
	}
	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}
	public boolean isHasXPass() {
		return hasXPass;
	}
	public void setHasXPass(boolean hasXPass) {
		this.hasXPass = hasXPass;
	}
	public GuestStatusState getState() {
		return state;
	}
	public void setState(GuestStatusState state) {
		this.state = state;
	}
	public String getLastReader() {
		return lastReader;
	}
	public void setLastReader(String lastReader) {
		this.lastReader = lastReader;
	}
	public Date getTimeEarliestAtReader() {
		return timeEarliestAtReader;
	}
	public void setTimeEarliestAtReader(Date timeEarliestAtReader) {
		this.timeEarliestAtReader = timeEarliestAtReader;
	}
	public Date getTimeLatestAtReader() {
		return timeLatestAtReader;
	}
	public void setTimeLatestAtReader(Date timeLatestAtReader) {
		this.timeLatestAtReader = timeLatestAtReader;
	}
	public Date getTimeEntered() {
		return timeEntered;
	}
	public void setTimeEntered(Date timeEntered) {
		this.timeEntered = timeEntered;
	}
	public Date getTimeMerged() {
		return timeMerged;
	}
	public void setTimeMerged(Date timeMerged) {
		this.timeMerged = timeMerged;
	}
	public Date getTimeLoaded() {
		return timeLoaded;
	}
	public void setTimeLoaded(Date timeLoaded) {
		this.timeLoaded = timeLoaded;
	}
	public Date getTimeExited() {
		return timeExited;
	}
	public void setTimeExited(Date timeExited) {
		this.timeExited = timeExited;
	}
	public String getCarID() {
		return CarID;
	}
	public void setCarID(String carID) {
		CarID = carID;
	}
	public boolean isHasDeferredEntry() {
		return hasDeferredEntry;
	}
	public void setHasDeferredEntry(boolean hasDeferredEntry) {
		this.hasDeferredEntry = hasDeferredEntry;
	}
}
