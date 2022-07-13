package com.swedishchef.esm.stas.latest.artifacts;

import com.enterprisehorizons.magma.designtime.artifact.GeoArtifact;

public class ParkArtifact extends GeoArtifact{
	private static final long serialVersionUID = -4805752221040598128L;
	private String parkName;
	private int guestCount;
	private int forecastedGuestCount;
	public ParkArtifact(){
		
	}
	public String getParkName() {
		return parkName;
	}
	public void setParkName(String parkName) {
		this.parkName = parkName;
	}
	public int getGuestCount() {
		return guestCount;
	}
	public void setGuestCount(int guestCount) {
		this.guestCount = guestCount;
	}
	public int getForecastedGuestCount() {
		return forecastedGuestCount;
	}
	public void setForecastedGuestCount(int forecastedGuestCount) {
		this.forecastedGuestCount = forecastedGuestCount;
	}
}
