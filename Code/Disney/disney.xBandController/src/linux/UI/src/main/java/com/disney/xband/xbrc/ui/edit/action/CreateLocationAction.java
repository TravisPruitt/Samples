package com.disney.xband.xbrc.ui.edit.action;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.db.EventsLocationConfigService;
import com.disney.xband.xbrc.lib.entity.AnnotationHelper;
import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;

public class CreateLocationAction extends BaseAction {

	private static Logger logger = Logger.getLogger(CreateLocationAction.class);
	private LocationService locationService;
	private Location location;
	private Map<Integer, String> locationTypes; 
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		locationService = ServiceLocator.getInstance().createService(LocationService.class);
		locationTypes = locationService.getLocationTypes(true);
	}

	@Override
	public String execute() throws Exception {
		
		location = new Location();		
		
		return SUCCESS;
	}
	
	public String saveNewLocation() throws Exception {
		
		String name = LocationListEditAjaxAction.sanitizeLocationName(location.getName(), 
				AnnotationHelper.getColumnLength(location, "name"));
		
		if (name.isEmpty()) {
			this.addActionError("The location name is not valid");
			return INPUT;
		}
		
		Collection<Location> existing = locationService.findAll(true);
		
		for (Location loc : existing) {
			if (loc.getName().equalsIgnoreCase(location.getName())) {
				this.addActionError("Location with name: " + location.getName() + " already exists. Please enter a different name.");
				return INPUT;
			}
			
			if (location.getLocationId() != null && !location.getLocationId().isEmpty() && location.getLocationId().equalsIgnoreCase(loc.getLocationId()))
			{
				this.addActionError("The location ID " + location.getLocationId() + " is already used by another location " + loc.getName() + ". Please enter a unique value for location ID.");
				return INPUT;
			}
		}
		
        locationService.save(location);

        // We must create the events location config with default values
        if (isAttractionModel()) {
	        Connection conn = null;
	    	try {
	    		conn = UIConnectionPool.getInstance().getConnection();
	        	EventsLocationConfig config = new EventsLocationConfig();
	        	
	        	if (LocationType.getByOrdinal(location.getLocationTypeId()) == LocationType.Waypoint)
	            {
	            	config.setSendConfToJMS(true);
	            	config.setSendTapToHTTP(true);
	            	config.setSendTapToJMS(true);
	            }
	        	 
	        	config.setLocationId(location.getId());
				EventsLocationConfigService.save(conn, config);
			} catch (Throwable t) {
				logger.error("Failed to save EventsLocationConfig record", t);
			} finally {
				UIConnectionPool.getInstance().releaseConnection(conn);
			}
        }
			
		return SUCCESS;
	}

	public Map<Integer, String> getLocationTypes(){
		return locationTypes;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
}
