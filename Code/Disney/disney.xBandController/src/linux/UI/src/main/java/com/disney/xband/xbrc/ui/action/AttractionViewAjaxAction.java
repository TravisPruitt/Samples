package com.disney.xband.xbrc.ui.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.lib.entity.XbrcStatus;
import com.disney.xband.xbrc.ui.AttractionViewConfig;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.XbrcDataCache;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;
import com.disney.xband.xbrc.ui.bean.GuestStatusCounts;
import com.disney.xband.xbrc.ui.db.Data;
import com.opensymphony.xwork2.ActionSupport;

public class AttractionViewAjaxAction extends BaseAction {
	
	private final AttractionViewConfig avConfig = UIProperties.getInstance().getAttractionViewConfig();
	
	private List<GuestStatusDisplay> guestStatusIcons;
	// This map is keyed either by locationId or GuestStatusState
	private HashMap<String, GuestCountDisplay> guestStatusCounts;
	private Integer totalGuestCount = 0;
	private final int nGuestIconYOffset = avConfig.getGuestIconHeight() / 2;
	private final int nGuestCountXOffest = avConfig.getGridSize() / 2 - 10;
	private final int nGuestCountYOffset = avConfig.getGridSize() / 2 - 10;
	private XbrcStatus xbrcStatus;
	private Integer xpassGuestCount = 0;
	
	@Override
	public String execute() throws Exception {
		
		guestStatusIcons = new LinkedList<GuestStatusDisplay>();
		guestStatusCounts = new HashMap<String, GuestCountDisplay>();
		 
		/*
		 * First, we get the total number of guests per state, paired with x,y coordinates for each item in the grid 
		 * matching the particular state.
		 */
		List<GuestStatusCounts> guestCounts = XbrcDataCache.getInstance().getGuestCounts();
		xbrcStatus = XbrcDataCache.getInstance().getXbrcStatus();
		
		/*
		 * Now we distribute the total number of guests per state over the total number of grid items matching the
		 * given state. However, we stack them up in groups of <max guests per icon> * <max icons per grid item>.
		 * For each icon that represents up to <max guests per icon> we create a GuestStatusDisplay object.
		 */
		
		AddToGuestStatusCounts(guestCounts);
		
		return super.execute();
	}

	private void AddToGuestStatusCounts(List<GuestStatusCounts> guestCounts) {
			
		/* 
		 * The list of guestCounts is ordered in reverse so that icons are added from the end towards the front.
		 * So the first item per each state is actually last in the list.
		 */
		GuestStatusCounts firstItemInPreviousGroup = null;
		String currentGrouping = "";
		
		int remainingGuestsToShow = 0;
		Boolean currentHaveXPass = false;
	
		for (GuestStatusCounts gsc : guestCounts) {
			
			// Guests are grouped either by locationId (if not null) or by the state.
			String gscGrouping = gsc.getLocationId() == null ? gsc.getState().toString() : gsc.getLocationId().toString();
			
			if (!gscGrouping.equals(currentGrouping) || currentHaveXPass != gsc.isHasXPass()) {
				if (firstItemInPreviousGroup != null) {				
					addGuestStatusCountDisplay(new GuestCountDisplay(firstItemInPreviousGroup));
				}
				currentGrouping = gscGrouping;
				currentHaveXPass = gsc.isHasXPass();
				remainingGuestsToShow = gsc.getCount();
			}
			
			int iIcon = 0;
			while (remainingGuestsToShow > 0 && iIcon < avConfig.getMaxIconsPerGridItem()) {
				GuestStatusDisplay gsd = new GuestStatusDisplay(gsc,iIcon);
				guestStatusIcons.add(gsd);
				iIcon++;
				remainingGuestsToShow -= avConfig.getMaxGuestsPerIcon();
			}
			
			firstItemInPreviousGroup = gsc;
		}
		
		if (firstItemInPreviousGroup != null)
			addGuestStatusCountDisplay(new GuestCountDisplay(firstItemInPreviousGroup));
	}
	
	private void addGuestStatusCountDisplay(GuestCountDisplay gscd) {
		totalGuestCount += gscd.getCount();
		
		if (gscd.getGsc().isHasXPass())
			xpassGuestCount += gscd.getCount();
		
		String key = gscd.gsc.getLocationId() == null ? gscd.gsc.getState().toString() : gscd.gsc.getLocationId().toString();
		
		// If the location or state splits xpass and non-xpass guests then group the accordingly. 
		if (gscd.gsc.getXpassonly() != XpassOnlyState.AllGuests)
			key += gscd.gsc.isHasXPass();
		
		GuestCountDisplay currentGscd = guestStatusCounts.get(key);
		
		/* If there is already existing counts of guests for this state, we just add to it */
		if (currentGscd != null) {
			currentGscd.setCount(currentGscd.getCount() + gscd.getCount());
			return;
		}
		
		guestStatusCounts.put(key, gscd);
	}
	
	public List<GuestStatusDisplay> getGuestStatusIcons() {
		return guestStatusIcons;
	}
	
	public Collection<GuestCountDisplay> getGuestStatusCounts() {
		return guestStatusCounts.values();
	}
	
	public class GuestCountDisplay {
		Integer topPos;
		Integer leftPos;
		GuestStatusCounts gsc;
		Integer count;
		
		public GuestCountDisplay(GuestStatusCounts gsc) {
			this.gsc = gsc;
			this.topPos = gsc.getyGrid() * avConfig.getGridSize() + nGuestCountYOffset;
			this.leftPos = gsc.getxGrid() * avConfig.getGridSize() + nGuestCountXOffest;
			this.count = gsc.getCount();
		}
		
		public Integer getTopPos() {
			return topPos;
		}		

		public Integer getLeftPos() {
			return leftPos;
		}
		
		public GuestStatusCounts getGsc() {
			return gsc;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}
	
	public class GuestStatusDisplay {
		Integer topPos;
		Integer leftPos;
		String image;
		Long locationId;
		String state;
		GridItem.XpassOnlyState xPassOnly;
		
		public GuestStatusDisplay(GuestStatusCounts gsc, int iIcon) {
			
			// This value is passed to a javascript function so it cannot be null
			locationId = gsc.getLocationId() == null ? -1 : gsc.getLocationId();
			state = gsc.getState() == GuestStatusState.INDETERMINATE ? "" : gsc.getState().toString();
			
			/* Icons are alternated up/down or left/right based on even or odd iIcon */
			int nStagger = (iIcon % 2) == 0 ? 1 : -1;	
			
			if (gsc.getItemType().equals("VPath")) {
				topPos = gsc.getyGrid() * avConfig.getGridSize() - iIcon * avConfig.getGuestIconSpacing() + nGuestIconYOffset;
				leftPos = gsc.getxGrid() * avConfig.getGridSize() + (iIcon * avConfig.getGuestIconStagger() * nStagger);				
			}
			else {
				topPos = gsc.getyGrid() * avConfig.getGridSize() + (iIcon * avConfig.getGuestIconStagger() * nStagger) + nGuestIconYOffset;
				leftPos = gsc.getxGrid() * avConfig.getGridSize() - iIcon * avConfig.getGuestIconSpacing() + avConfig.getGridSize() - avConfig.getGuestIconWidth();
			}
			
			if (gsc.isHasXPass())
			{
				this.image = "xguest.png";
				// Move the XPass icon so that it does not overlap the non-xpass icon.
				topPos -= 2;
				leftPos += 2;
			}
			else
				this.image = "guest.png";
			
			xPassOnly = gsc.getXpassonly();
		}

		public Integer getTopPos() {
			return topPos;
		}		

		public Integer getLeftPos() {
			return leftPos;
		}
		
		public String getImage() {
			return image;
		}

		public Long getLocationId() {
			return locationId;
		}

		public String getState() {
			return state;
		}

		public GridItem.XpassOnlyState getXpassonly() {
			return xPassOnly;
		}
	}

	public Integer getTotalGuestCount() {
		return totalGuestCount;
	}

	public void setTotalGuestCount(Integer totalGuestCount) {
		this.totalGuestCount = totalGuestCount;
	}

	public XbrcStatus getXbrcStatus()
	{
		return xbrcStatus;
	}

	public Integer getXpassGuestCount()
	{
		return xpassGuestCount;
	}	
}
