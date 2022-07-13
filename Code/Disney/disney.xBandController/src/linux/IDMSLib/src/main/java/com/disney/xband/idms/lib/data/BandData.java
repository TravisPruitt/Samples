package com.disney.xband.idms.lib.data;

import java.util.ArrayList;
import java.util.Date;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.LinkCollection;
import com.disney.xband.idms.lib.model.XBand;
import com.disney.xband.idms.lib.model.xview.Xband;

public class BandData
{

	private long xBandId;
	private String secureId;
	private String tapId;
	private String longRangeId; // lRId
	private String printedName;
	// private String productId;
	private String publicId;
	private String bandId;
	// private Date assignmentDateTime;
	private String bandType;
	private boolean active;
	private String bandFriendlyName;
	private String createdBy;
	private Date createdDate;
	private String updatedBy;
	private Date updatedDate;

	private GuestData guest;

	public BandData()
	{
	}

	public long getXbandId()
	{
		return xBandId;
	}

	public void setXbandId(long xBandId)
	{
		this.xBandId = xBandId;
	}

	public String getSecureId()
	{
		return secureId;
	}

	public void setSecureId(String secureId)
	{
		this.secureId = secureId;
	}

	public String getTapId()
	{
		return tapId;
	}

	public void setTapId(String shortRangeId)
	{
		this.tapId = shortRangeId;
	}

	public String getLongRangeId()
	{
		return longRangeId;
	}

	public void setLongRangeId(String longRangeId)
	{
		this.longRangeId = longRangeId;
	}

	public String getPrintedName()
	{
		return printedName;
	}

	public void setPrintedName(String printedName)
	{
		this.printedName = printedName;
	}

	public boolean getActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getPublicId()
	{
		return this.publicId;
	}

	public void setPublicId(String publicId)
	{
		this.publicId = publicId;
	}

	public String getBandId()
	{
		return this.bandId;
	}

	public void setBandId(String bandId)
	{
		this.bandId = bandId;
	}

	public String getBandType()
	{
		return this.bandType;
	}

	public void setBandType(String bandType)
	{
		this.bandType = bandType;
	}

	public String getBandFriendlyName()
	{
		return this.bandFriendlyName;
	}

	public void setBandFriendlyName(String bandFriendlyName)
	{
		this.bandFriendlyName = bandFriendlyName;
	}

	public GuestData getGuest()
	{
		return guest;
	}

	public void setGuest(GuestData guest)
	{
		this.guest = guest;
	}

	public XBand getXBand()
	{
		ArrayList<com.disney.xband.idms.lib.model.Guest> modelGuests = new ArrayList<com.disney.xband.idms.lib.model.Guest>(
				0);
		new ArrayList<com.disney.xband.idms.lib.model.Guest>(0);
		XBand x = new XBand();

		x.setPrintedName(printedName);
		x.setLongRangeTag(longRangeId);
		x.setShortRangeTag(tapId);
		x.setXbandId(xBandId);
		x.setProductId(bandId);
		x.setXbandRequestId(bandId);
		x.setBandId(bandId);
		x.setBandType(bandType);
		x.setPublicId(publicId);

		if (active)
		{
			x.setState("Active");
			x.setSecondaryState("Active");

		}
		else
		{
			x.setState("Inactive");
			x.setSecondaryState("Inactive");
		}

		if (guest != null)
		{
			modelGuests.add(guest.getGuest());
		}
		x.setGuests(modelGuests);

		// Setup the Links.
		LinkCollection links = new LinkCollection();
		links.setHistory(null);
		links.setNextAction(null);
		links.setProductIdReference(null);

		if (guest != null)
		{
			Link ownerLink = new Link();
			ownerLink.setHref("/guest/" + guest.getGuestId() + "/profile");
			links.setOwnerProfile(ownerLink);
		}

		Link self = new Link();
		self.setHref("/xband/" + xBandId);
		links.setSelf(self);

		return x;
	}

	public Xband getxband()
	{
		ArrayList<com.disney.xband.idms.lib.model.xview.Guest> xviewGuests = new ArrayList<com.disney.xband.idms.lib.model.xview.Guest>(
				0);
		Xband xband = new Xband();

		xband.setActive(active);
		xband.setBandFriendlyName(bandFriendlyName);
		xband.setBandId(bandId);
		xband.setLRId(longRangeId);
		xband.setTapId(tapId);
		xband.setPrintedName(printedName);
		xband.setXBandId(Long.toString(xBandId));
		xband.setBandType(bandType);
		xband.setPublicId(publicId);

		if (guest != null)
		{
			xviewGuests.add(guest.getXviewGuest());
		}
		xband.setGuests(xviewGuests);

		return xband;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public Date getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate()
	{
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}
}
