package com.disney.xband.xfpe.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;

public interface LocationService {
	
	public Location find(long id) throws Exception;
	
	public Collection<Location> findAll() throws Exception;
	
	public void save(Location location) throws Exception;
	
	public void delete(Location location) throws Exception;
	
	public Map<Integer, String> getLocationTypes(boolean excludeUnknown) throws Exception;
}
