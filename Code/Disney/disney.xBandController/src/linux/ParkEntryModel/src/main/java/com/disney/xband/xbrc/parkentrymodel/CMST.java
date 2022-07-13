package com.disney.xband.xbrc.parkentrymodel;

import java.util.Date;

/*
 * Cast member state table.
 */
public class CMST {
	private String bandId;
	private String portalId;
	private CastMemberState state;
	private String locationName;
	private Date stateChangeTime = new Date();
	private String omniNumericId;
	
	public String getBandId() {
		return bandId;
	}
	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	public CastMemberState getState() {
		return state;
	}
	public void setState(CastMemberState state) {
		this.state = state;
		this.stateChangeTime = new Date();
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public Date getStateChangeTime() {
		return stateChangeTime;
	}
	public void setStateChangeTime(Date stateChangeTime) {
		this.stateChangeTime = stateChangeTime;
	}
	public String getOmniNumericId() {
		return omniNumericId;
	}
	public void setOmniNumericId(String omniNumericId) {
		this.omniNumericId = omniNumericId;
	}
	public String getPortalId()
	{
		return portalId;
	}
	public void setPortalId(String portalId)
	{
		this.portalId = portalId;
	}
}
