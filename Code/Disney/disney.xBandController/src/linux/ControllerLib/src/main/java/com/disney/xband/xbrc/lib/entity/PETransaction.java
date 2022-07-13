package com.disney.xband.xbrc.lib.entity;

import java.util.Date;

public class PETransaction {
	private Long id;
	private String bandId;
	private String readerName;
	private String locationName;
	private Date startTime;
	private Long omniEntitlementDuration = 0L;
	private Integer omniEntitlementCount = 0;
	private Long omniEntitlementErrorCode = 0L;
	private String omniEntitlementErrorDesc;
	private Long scanDuration = 0L;
	private Integer scanCount = 0;
	private Long omniBioMatchDuration = 0L;
	private Integer omniBioMatchCount = 0;
	private Long omniBioMatchErrorCode = 0L;
	private String omniBioMatchErrorDesc;
	private Long totalDuration = 0L;
	private Integer scanErrorCount = 0;
	private String scanErrorReasons;
	private Boolean blue = false;
	private Boolean abandoned = false;
	private Date finishTime;
	private String tdssnSite;
	private String tdssnStation;
	private String tdssnDate;
	private String tdssnTicketId;
	private Boolean decremented = false;
	private Boolean newEnrollment = false;
	
	public String getBandId() {
		return bandId;
	}
	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Long getOmniEntitlementDuration() {
		return omniEntitlementDuration;
	}
	public void setOmniEntitlementDuration(Long omniEntitlementDuration) {
		this.omniEntitlementDuration = omniEntitlementDuration;
	}
	public Long getScanDuration() {
		return scanDuration;
	}
	public void setScanDuration(Long scanDuration) {
		this.scanDuration = scanDuration;
	}
	public Long getOmniBioMatchDuration() {
		return omniBioMatchDuration;
	}
	public void setOmniBioMatchDuration(Long omniBioMatchDuration) {
		this.omniBioMatchDuration = omniBioMatchDuration;
	}
	public Long getTotalDuration() {
		return totalDuration;
	}
	public void setTotalDuration(Long totalDuration) {
		this.totalDuration = totalDuration;
	}
	public Boolean getBlue() {
		return blue;
	}
	public void setBlue(Boolean blue) {
		this.blue = blue;
	}
	public Integer getScanErrorCount() {
		return scanErrorCount;
	}
	public void setScanErrorCount(Integer scanErrorCount) {
		this.scanErrorCount = scanErrorCount;
	}
	public String getScanErrorReasons() {
		return scanErrorReasons;
	}
	public void setScanErrorReasons(String scanErrorReasons) {
		this.scanErrorReasons = scanErrorReasons;
	}
	public Boolean getAbandoned() {
		return abandoned;
	}
	public void setAbandoned(Boolean abandoned) {
		this.abandoned = abandoned;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	public Integer getOmniEntitlementCount() {
		return omniEntitlementCount;
	}
	public void setOmniEntitlementCount(Integer omniEntitlementCount) {
		this.omniEntitlementCount = omniEntitlementCount;
	}
	public Integer getScanCount() {
		return scanCount;
	}
	public void setScanCount(Integer scanCount) {
		this.scanCount = scanCount;
	}
	public Integer getOmniBioMatchCount() {
		return omniBioMatchCount;
	}
	public void setOmniBioMatchCount(Integer omniBioMatchCount) {
		this.omniBioMatchCount = omniBioMatchCount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReaderName() {
		return readerName;
	}
	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getTdssnSite() {
		return tdssnSite;
	}
	public void setTdssnSite(String tdssnSite) {
		this.tdssnSite = tdssnSite;
	}
	public String getTdssnStation() {
		return tdssnStation;
	}
	public void setTdssnStation(String tdssnStation) {
		this.tdssnStation = tdssnStation;
	}
	public String getTdssnDate() {
		return tdssnDate;
	}
	public void setTdssnDate(String tdssnDate) {
		this.tdssnDate = tdssnDate;
	}
	public String getTdssnTicketId() {
		return tdssnTicketId;
	}
	public void setTdssnTicketId(String tdssnTicketId) {
		this.tdssnTicketId = tdssnTicketId;
	}
	public Long getOmniEntitlementErrorCode() {
		return omniEntitlementErrorCode;
	}
	public void setOmniEntitlementErrorCode(Long omniEntitlementErrorCode) {
		this.omniEntitlementErrorCode = omniEntitlementErrorCode;
	}
	public String getOmniEntitlementErrorDesc() {
		return omniEntitlementErrorDesc;
	}
	public void setOmniEntitlementErrorDesc(String omniEntitlementErrorDesc) {
		this.omniEntitlementErrorDesc = omniEntitlementErrorDesc;
	}
	public Long getOmniBioMatchErrorCode() {
		return omniBioMatchErrorCode;
	}
	public void setOmniBioMatchErrorCode(Long omniBioMatchErrorCode) {
		this.omniBioMatchErrorCode = omniBioMatchErrorCode;
	}
	public String getOmniBioMatchErrorDesc() {
		return omniBioMatchErrorDesc;
	}
	public void setOmniBioMatchErrorDesc(String omniBioMatchErrorDesc) {
		this.omniBioMatchErrorDesc = omniBioMatchErrorDesc;
	}
	public Boolean getDecremented() {
		return decremented;
	}
	public void setDecremented(Boolean decremented) {
		this.decremented = decremented;
	}
	public Boolean getNewEnrollment() {
		return newEnrollment;
	}
	public void setNewEnrollment(Boolean newenrollment) {
		this.newEnrollment = newenrollment;
	}
}
