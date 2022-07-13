package com.disney.xband.xbrc.ui;

import com.disney.xband.common.lib.PersistName;

@PersistName("UIConfig")
public class UIConfig {
	// how long to keep the cached guest info objects obtained from Xview
	private int guestXviewCacheTimeSec = 1800;
	private String controllerURL = "http://localhost:8080";
	private String attractionViewImageFilename = "";
	private boolean showSubwayMap = true;
	private String productTitle = "Walt Disney World"; 

	public int getGuestXviewCacheTimeSec() {
		return guestXviewCacheTimeSec;
	}

	public void setGuestXviewCacheTimeSec(int guestXviewCacheTimeSec) {
		this.guestXviewCacheTimeSec = guestXviewCacheTimeSec;
	}

	public String getControllerURL() {
		return controllerURL;
	}

	public void setControllerURL(String controllerURL) {
		this.controllerURL = controllerURL;
	}

	public boolean isShowSubwayMap()
	{
		return showSubwayMap;
	}

	public void setShowSubwayMap(boolean showSubwayMap)
	{
		this.showSubwayMap = showSubwayMap;
	}

	public String getAttractionViewImageFilename()
	{
		return attractionViewImageFilename;
	}

	public void setAttractionViewImageFilename(String attractionViewImageFilename)
	{
		this.attractionViewImageFilename = attractionViewImageFilename;
	}

	public String getProductTitle()
	{
		return productTitle;
	}

	public void setProductTitle(String productTitle)
	{
		this.productTitle = productTitle;
	}
}
