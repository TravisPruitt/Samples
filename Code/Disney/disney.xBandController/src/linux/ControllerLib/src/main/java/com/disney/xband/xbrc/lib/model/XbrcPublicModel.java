package com.disney.xband.xbrc.lib.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.AttractionType;
import com.disney.xband.xbrc.lib.entity.LocationType;

/*
 * The XbrcPublicModel class is a container that holds the following items (or will hold):
 * 1) Relationships between XBRC entity objects.
 * 2) Processing (logic) shared between multiple projects.
 * 
 * The information made available by this class should not be specific to a single project.
 * For example, any logical processing that can be used in the Controller and in the UI should go here. 
 */
public class XbrcPublicModel {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(XbrcPublicModel.class.getName());
	
	private static class SingletonHolder { 
		public static final XbrcPublicModel instance = new XbrcPublicModel();
	}
	
	public static XbrcPublicModel getInstance() {
		return SingletonHolder.instance;
	}	

	/*
	 * @return Allowed location types and how many of each.
	 */
	public Map<LocationType,Integer> GetAllowedLocationTypes(AttractionType attractionType) {
		
		HashMap<LocationType, Integer> map = new HashMap<LocationType, Integer>();
		
		switch(attractionType) 
		{
		case Restaurant:
		case Ride:
			map.put(LocationType.Entry, 1);
			map.put(LocationType.xPassEntry, 1);
			map.put(LocationType.Merge, 1);
			map.put(LocationType.Queue, 2);
			map.put(LocationType.Load, 2);
			map.put(LocationType.Exit, 1);
			break;
		}		
		return map;
	}
}
