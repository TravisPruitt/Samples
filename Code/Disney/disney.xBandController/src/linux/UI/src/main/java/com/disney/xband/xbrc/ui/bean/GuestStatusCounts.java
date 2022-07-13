package com.disney.xband.xbrc.ui.bean;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.ui.bean.GridItem.ItemType;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;

public class GuestStatusCounts {
	/*id, XGrid, YGrid, ItemType, Label, State, Sequence, count(*)*/
	private Integer id;	
	private Integer xGrid;
	private Integer yGrid;
	private ItemType itemType;
	private String label;
	private GuestStatusState state;	
	private Integer sequence;
	private Integer count;
	private Boolean hasXPass;
	private Long locationId;
	private String locationName;
	private XpassOnlyState xPassOnly;
	
	public GuestStatusCounts(Integer id, Integer xGrid, Integer yGrid,
			ItemType itemType, XpassOnlyState xPassOnly, String label, GuestStatusState state,
			Integer sequence, Integer count, Boolean hasXPass, Long locationId,
			String locationName) {
		super();
		this.id = id;
		this.xGrid = xGrid;
		this.yGrid = yGrid;
		this.itemType = itemType;
		this.xPassOnly = xPassOnly;
		this.label = label;
		this.state = state;
		this.sequence = sequence;
		this.count = count;
		this.hasXPass = hasXPass;
		this.locationId = locationId;
		this.locationName = locationName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public ItemType getItemType() {
		return itemType;
	}
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public GuestStatusState getState() {
		return state;
	}
	public void setState(GuestStatusState state) {
		this.state = state;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean isHasXPass() {
		return hasXPass;
	}

	public void setHasXPass(Boolean hasXPass) {
		this.hasXPass = hasXPass;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	public XpassOnlyState getXpassonly() {
		return xPassOnly;
	}

	public void setXpassonly(XpassOnlyState xPassOnly) {
		this.xPassOnly = xPassOnly;
	}
}
