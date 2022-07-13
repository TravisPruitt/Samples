package com.disney.xband.xbrc.ui;

import com.disney.xband.common.lib.MetaData;
import com.disney.xband.common.lib.PersistName;

/**
 * This is a shell of @see com.disney.xband.xbrc.Controller$ControllerInfo. Used by UI for Config table lookup.
 * @author iwona
 */
@PersistName("ControllerInfo")
public class ControllerInfo {
	private String venue;
	private String model;
	private String name;
	private String xviewurl;
    private String vipAddress;
	
	public ControllerInfo() {}
	
	public ControllerInfo(String venue, String model, String name, String vipAddress) {
		super();
		this.venue = venue;
		this.model = model;
		this.name = name;
        this.vipAddress = vipAddress;
	}

    public String getVipAddress()
    {
        return vipAddress;
    }

    public void setVipAddress(String vipAddress)
    {
        this.vipAddress = vipAddress;
    }

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getXviewurl()
	{
		return xviewurl;
	}

	public void setXviewurl(String xviewurl)
	{
		this.xviewurl = xviewurl;
	}

	@Override
	protected ControllerInfo clone() {
		return new ControllerInfo(venue, model, name, vipAddress);
	}
}
