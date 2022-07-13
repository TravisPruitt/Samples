package com.disney.xband.xbrc.ui.bean;

import java.util.List;

public class LocationCounts {
	Long locationId;
	Integer totalGuestCount;
	List<ReaderCounts> readerCounts;

	public LocationCounts(Long locationId, Integer totalGuestCount,
			List<ReaderCounts> readerCounts) {
		super();
		this.locationId = locationId;
		this.totalGuestCount = totalGuestCount;
		this.readerCounts = readerCounts;
	}
	public Integer getTotalGuestCount() {
		return totalGuestCount;
	}
	public void setTotalGuestCount(Integer totalGuestCount) {
		this.totalGuestCount = totalGuestCount;
	}
	public List<ReaderCounts> getReaderCounts() {
		return readerCounts;
	}
	public void setReaderCounts(List<ReaderCounts> readerCounts) {
		this.readerCounts = readerCounts;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
}
