package com.disney.xband.xbrc.lib.entity;

import com.disney.xband.common.lib.PersistName;

public class GST {
	@PersistName("GuestId")
	public String id;
	@PersistName("HasXPass")
	public Boolean xPass;
	@PersistName("State")
	public String state;
	@PersistName("LastReader")
	public String lastReader;
	@PersistName("TimeEarliestAtReader")
	public Long timeEarliestAtReader;
	@PersistName("TimeLatestAtReader")
	public Long timeLatestAtReader;
	@PersistName("TimeEntered")
	public Long timeEntered;
	@PersistName("TimeMerged")
	public Long timeMerged;
	@PersistName("TimeLoaded")
	public Long timeLoaded;
	@PersistName("TimeExited")
	public Long timeExited;
	@PersistName("CarId")
	public String carId;
	@PersistName("HasDeferredEntry")
	public Boolean deferredEntry;
	
	/**
	 * @return the guestId
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param guestId the guestId to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the hasXPass
	 */
	public Boolean isXPass() {
		return xPass;
	}
	/**
	 * @param hasXPass the hasXPass to set
	 */
	public void setXPass(Boolean xPass) {
		this.xPass = xPass;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the lastReader
	 */
	public String getLastReader() {
		return lastReader;
	}
	/**
	 * @param lastReader the lastReader to set
	 */
	public void setLastReader(String lastReader) {
		this.lastReader = lastReader;
	}
	/**
	 * @return the timeEarliestAtReader
	 */
	public Long getTimeEarliestAtReader() {
		return timeEarliestAtReader;
	}
	/**
	 * @param timeEarliestAtReader the timeEarliestAtReader to set
	 */
	public void setTimeEarliestAtReader(Long timeEarliestAtReader) {
		this.timeEarliestAtReader = timeEarliestAtReader;
	}
	/**
	 * @return the timeLatestAtReader
	 */
	public Long getTimeLatestAtReader() {
		return timeLatestAtReader;
	}
	/**
	 * @param timeLatestAtReader the timeLatestAtReader to set
	 */
	public void setTimeLatestAtReader(Long timeLatestAtReader) {
		this.timeLatestAtReader = timeLatestAtReader;
	}
	/**
	 * @return the timeEntered
	 */
	public Long getTimeEntered() {
		return timeEntered;
	}
	/**
	 * @param timeEntered the timeEntered to set
	 */
	public void setTimeEntered(Long timeEntered) {
		this.timeEntered = timeEntered;
	}
	/**
	 * @return the timeMerged
	 */
	public Long getTimeMerged() {
		return timeMerged;
	}
	/**
	 * @param timeMerged the timeMerged to set
	 */
	public void setTimeMerged(Long timeMerged) {
		this.timeMerged = timeMerged;
	}
	/**
	 * @return the timeLoaded
	 */
	public Long getTimeLoaded() {
		return timeLoaded;
	}
	/**
	 * @param timeLoaded the timeLoaded to set
	 */
	public void setTimeLoaded(Long timeLoaded) {
		this.timeLoaded = timeLoaded;
	}
	/**
	 * @return the timeExited
	 */
	public Long getTimeExited() {
		return timeExited;
	}
	/**
	 * @param timeExited the timeExited to set
	 */
	public void setTimeExited(Long timeExited) {
		this.timeExited = timeExited;
	}
	/**
	 * @return the carId
	 */
	public String getCarId() {
		return carId;
	}
	/**
	 * @param carId the carId to set
	 */
	public void setCarId(String carId) {
		this.carId = carId;
	}
	/**
	 * @return the deferredEntry
	 */
	public Boolean isDeferredEntry() {
		return deferredEntry;
	}
	/**
	 * @param deferredEntry the deferredEntry to set
	 */
	public void setDeferredEntry(Boolean deferredEntry) {
		this.deferredEntry = deferredEntry;
	}

}
