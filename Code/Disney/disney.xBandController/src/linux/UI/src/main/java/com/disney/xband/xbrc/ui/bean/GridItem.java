package com.disney.xband.xbrc.ui.bean;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;

/*
 * GridItem represents a UI element that occupies a single grid location.
 * A collection of GirdItem elements make up a plan of an attraction.  
 */
public class GridItem {
	
	public enum ItemType 
	{ 	Gate,	// 0 - Gate or a location such as entry, exit, merge etc.
		HPath,	// 1 - Horizontal path element.
		VPath,	// 2 - Vertical path element.
		TNorth,	// 3 - T intersection entering from the North.
		TWest,	// 4 - T intersection entering from the West.
		TSouth,	// 5 - T intersection entering from the South.
		TEast,	// 6 - T intersection entering from the East.
		Cross,	// 8 - Cross intersection. 
		ESTurn,	// 9 - East South turn
		WSTurn,	// 10 - West South turn
		ENTurn,	// 11 - East North turn
		WNTurn,	// 12 - West North turn
	};
	
	public enum XpassOnlyState {
		AllGuests,		//	0 - Allow all guests to be shown at this GridItem.
		XpassGuests,	// 	1 - Allow only XPass guests to be shown at this GridItem.
		NonXpassGuests;	//	2 - Allow only non-XPass guests to be shown at this GridItem.
		
		public static XpassOnlyState fromOrdinal(int ordinal) {
			for (XpassOnlyState s : XpassOnlyState.values()) {
				if (s.ordinal() == ordinal)
					return s;
			}			
			return XpassOnlyState.AllGuests;
		}
	};
	
	private Integer id;
	private ItemType itemType;
	private Integer xGrid;
	private Integer yGrid;
	private GuestStatusState state;
	private String label;
	private String description;
	private String image;
	private Integer sequence;
	private XpassOnlyState xPassOnly;
	private Long locationId;
	
	public GridItem() {		
	}
	
	public GridItem(Integer id, ItemType type, Integer xGrid, Integer yGrid,
			GuestStatusState state, String label, String description, String image, Integer sequence, XpassOnlyState xPassOnly, Long locationId) {
		super();
		this.id = id;
		this.itemType = type;
		this.xGrid = xGrid;
		this.yGrid = yGrid;
		this.state = state;
		this.label = label;
		this.description = description;
		this.image = image;
		this.sequence = sequence;
		this.xPassOnly = xPassOnly;
		this.locationId = locationId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ItemType getItemType() {
		return itemType;
	}
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	public Integer getxGrid() {
		return xGrid;
	}
	public void setxGrid(Integer xGrid) {
		this.xGrid = xGrid;
	}
	public Integer getyGrid() {
		return yGrid;
	}
	public void setyGrid(Integer yGrid) {
		this.yGrid = yGrid;
	}
	public GuestStatusState getState() {
		return state;
	}
	public void setState(GuestStatusState state) {
		this.state = state;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	public XpassOnlyState getxPassOnly() {
		return xPassOnly;
	}

	public void setxPassOnly(XpassOnlyState xPassOnly) {
		this.xPassOnly = xPassOnly;
	}
	
	public int getXpassOnlyOrdinal() {
		return xPassOnly.ordinal();
	}

	public void setXpassOnlyOrdinal(int xPassOnly) {
		this.xPassOnly = XpassOnlyState.fromOrdinal(xPassOnly);
	}
	
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		GridItem c = (GridItem)super.clone();
		
		c.id = new Integer(this.id);
		c.itemType = this.itemType;
		c.xGrid = new Integer(this.xGrid);
		c.yGrid = new Integer(this.yGrid);
		c.state = state;
		c.label = new String(label);
		c.description = new String(description);
		c.image = new String(image);
		c.sequence = new Integer(sequence);
		c.xPassOnly = xPassOnly;
		c.locationId = new Long(locationId);
		
		return c;
	}
}
