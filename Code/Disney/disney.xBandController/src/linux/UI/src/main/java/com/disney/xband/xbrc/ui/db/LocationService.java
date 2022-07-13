package com.disney.xband.xbrc.ui.db;

import java.util.Collection;
import java.util.Map;

import com.disney.xband.xbrc.lib.entity.Location;

public interface LocationService {
	
	public Location find(long id) throws Exception;
	
	public Collection<Location> findAll(boolean excludeUnknown) throws Exception;
	
	public void save(Location location) throws Exception;
	
	public void delete(Long locationId, Collection<String> xbrcUrl) throws Exception;
	
	public Map<Integer, String> getLocationTypes(boolean excludeUnknown) throws Exception;
}
