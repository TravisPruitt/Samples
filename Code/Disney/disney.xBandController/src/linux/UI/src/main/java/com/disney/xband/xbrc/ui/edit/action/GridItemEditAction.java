package com.disney.xband.xbrc.ui.edit.action;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;
import com.disney.xband.xbrc.ui.db.GridItemService;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class GridItemEditAction extends BaseAction {

	private static Logger logger = Logger.getLogger(GridItemEditAction.class);
	private GridItemService gridItemService;
	private LocationService locationService;
	private Collection<Location> locationsList;
	private Map<Long,String> locationsMap;
	private GridItem gridItem;
	private String gridItemId;
	// Whether to use location or state when looking for guests for this GridItem.
	private String selector;
	private LinkedHashMap<String,String> selectorMap; 
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		gridItemService = ServiceLocator.getInstance().createService(GridItemService.class);
		locationService = ServiceLocator.getInstance().createService(LocationService.class);		
		locationsMap = new LinkedHashMap<Long,String>();
		selectorMap = new LinkedHashMap<String,String>();
		selectorMap.put("location", "Location");
		selectorMap.put("state", "Singulation State");
		gridItem = new GridItem();
	}
	
	@Override
	public String execute() throws Exception {
		gridItem = gridItemService.find(Long.parseLong(gridItemId));
		selector = gridItem.getState() == GuestStatusState.INDETERMINATE ? "location" : "state"; 
		locationsList = locationService.findAll(true);
		for (Location l : locationsList) {
			locationsMap.put(l.getId(), l.getName());
		}
		return super.execute();
	}
	
	public String saveGridItem() throws Exception {
		GridItem oldGridItem = gridItemService.find(Long.parseLong(gridItemId));
		
		if (selector.equals("location"))
			gridItem.setState(GuestStatusState.INDETERMINATE);
		else
			gridItem.setLocationId(null);
		
		oldGridItem.setLabel(gridItem.getLabel());
		oldGridItem.setDescription(gridItem.getDescription());
		oldGridItem.setLocationId(gridItem.getLocationId());
		oldGridItem.setState(gridItem.getState());
		oldGridItem.setSequence(gridItem.getSequence());
		oldGridItem.setxPassOnly(gridItem.getxPassOnly());
		
		gridItemService.update(oldGridItem);
		
		return SUCCESS;
	}

	public String getGridItemId() {
		return gridItemId;
	}

	public void setGridItemId(String gridItemId) {
		this.gridItemId = gridItemId;
	}

	public GridItem getGridItem() {
		return gridItem;
	}

	public void setGi(GridItem gridItem) {
		this.gridItem = gridItem;
	}

	public Map<Long, String> getLocationsMap() {
		return locationsMap;
	}

	public void setLocationsList(Collection<Location> locationsList) {
		this.locationsList = locationsList;
	}
	
	public GuestStatusState[] getGuestStatusStates() {
		return GuestStatusState.values();
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	public Map<String,String> getSelectorMap() {		
		return selectorMap;
	}
	
	public XpassOnlyState[] getXpassOnlyStates() {
		return GridItem.XpassOnlyState.values();
	}
}
