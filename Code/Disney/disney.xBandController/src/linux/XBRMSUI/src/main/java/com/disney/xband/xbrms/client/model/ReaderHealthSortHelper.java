package com.disney.xband.xbrms.client.model;

import java.util.Collections;
import java.util.List;

import com.disney.xband.lib.controllerapi.ReaderLocationInfo;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.common.model.Comparators;
import com.disney.xband.xbrms.common.model.LocationInfoDto;

/**
 * Define all sorts to be used in the UI here.
 * @author Iwona Glabek
 *
 */
public class ReaderHealthSortHelper implements IPresentationObject
{	
	public enum ReaderHealthPageSort
	{
		VENUE_LOCATION_READER_HEALTH,
		READER_HEALTH_LOCATION,
		VENUE_LOCATION_READER_HEALTH_NAME,
		READER_HEALTH_NAME_LOCATION
	}
	
	public static void sort(List<LocationInfoDto> collection, ReaderHealthPageSort sortType)
	{
		if (sortType == null)
			return;
		
		if (collection == null || collection.size() == 0)
			return;
		
		switch (sortType)
		{
			case VENUE_LOCATION_READER_HEALTH: venueLocationHealth(collection); break;
			case VENUE_LOCATION_READER_HEALTH_NAME: venueLocationReaderHealthName(collection); break;
		}
	}
	
	public static void sort(ReaderLocationInfo readerLocationInfo, ReaderHealthPageSort sortType)
	{
		if (sortType == null)
			return;
		
		if (readerLocationInfo == null)
			return;
		
		switch (sortType)
		{
			case READER_HEALTH_LOCATION: readerHealthAndLocation(readerLocationInfo); break;
			case READER_HEALTH_NAME_LOCATION: readerHealthNameAndLocation(readerLocationInfo); break;
		}
	}
	
	private static void venueLocationHealth(List<LocationInfoDto> collection)
	{
		LocationInfoDto sampleXbrcReaderLocationInfo = null;
		LocationInfo sampleLocationInfo = null;
		ReaderInfo sampleReaderInfo = null;
		
		for (LocationInfoDto xrli : collection)
		{
			if (sampleXbrcReaderLocationInfo == null)
				sampleXbrcReaderLocationInfo = xrli;
			
			if (xrli.getLoc() == null)
				continue;
			
			for (LocationInfo li : xrli.getLoc().getReaderlocationinfo())
			{
				if (sampleLocationInfo == null)
					sampleLocationInfo = li;
				
				if (li.getReaders() == null || li.getReaders().size() == 0)
					continue;
				
				sampleReaderInfo = li.getReaders().iterator().next();
				
				// sort by reader's health status
				Collections.sort(li.getReaders(), sampleReaderInfo.new HealthStatusComparator());
			}
			
			// sort by location's name
			Collections.sort(xrli.getLoc().getReaderlocationinfo(), sampleLocationInfo.new ByReaderHealth());
		}
		
		// sort by xbrc's venue name (same as facility id)
		Collections.sort(collection, new Comparators.ByVenueComparator());
	}
	
	private static void venueLocationReaderHealthName(List<LocationInfoDto> collection)
	{
		LocationInfoDto sampleXbrcReaderLocationInfo = null;
		LocationInfo sampleLocationInfo = null;
		ReaderInfo sampleReaderInfo = null;
		
		for (LocationInfoDto xrli : collection)
		{
			if (sampleXbrcReaderLocationInfo == null)
				sampleXbrcReaderLocationInfo = xrli;
			
			if (xrli.getLoc() == null)
				continue;
			
			for (LocationInfo li : xrli.getLoc().getReaderlocationinfo())
			{
				if (sampleLocationInfo == null)
					sampleLocationInfo = li;
				
				if (li.getReaders() == null || li.getReaders().size() == 0)
					continue;
				
				sampleReaderInfo = li.getReaders().iterator().next();
				
				// sort by reader's health status
				Collections.sort(li.getReaders(), sampleReaderInfo.new HealthStatusAndNameComparator());
			}
			
			// sort by location's name
			Collections.sort(xrli.getLoc().getReaderlocationinfo(), sampleLocationInfo.new ByReaderHealth());
		}
		
		// sort by xbrc's venue name (same as facility id)
		Collections.sort(collection, new Comparators.ByVenueComparator());
	}
	
	private static void readerHealthAndLocation(ReaderLocationInfo rlInfo)
	{
		if (rlInfo == null || rlInfo.getReaderlocationinfo() == null || rlInfo.getReaderlocationinfo().size() == 0)
			return;

		LocationInfo sampleLocationInfo = null;
		ReaderInfo sampleReaderInfo = null;
		
		for (LocationInfo li : rlInfo.getReaderlocationinfo())
		{
			if (sampleLocationInfo == null)
				sampleLocationInfo = li;
			
			if (li.getReaders() == null || li.getReaders().size() == 0)
				continue;
			
			sampleReaderInfo = li.getReaders().iterator().next();
			
			// sort these readers by their health status
			Collections.sort(li.getReaders(), sampleReaderInfo.new HealthStatusComparator());
		}
		
		// sort these locations by worst reader's status first and then location's name
		Collections.sort(rlInfo.getReaderlocationinfo(), sampleLocationInfo.new ByReaderHealth());
	}
	
	private static void readerHealthNameAndLocation(ReaderLocationInfo rlInfo)
	{
		if (rlInfo == null || rlInfo.getReaderlocationinfo() == null || rlInfo.getReaderlocationinfo().size() == 0)
			return;

		LocationInfo sampleLocationInfo = null;
		ReaderInfo sampleReaderInfo = null;
		
		for (LocationInfo li : rlInfo.getReaderlocationinfo())
		{
			if (sampleLocationInfo == null)
				sampleLocationInfo = li;
			
			if (li.getReaders() == null || li.getReaders().size() == 0)
				continue;
			
			sampleReaderInfo = li.getReaders().iterator().next();
			
			// sort these readers by their health status
			Collections.sort(li.getReaders(), sampleReaderInfo.new HealthStatusAndNameComparator());
		}
		
		// sort these locations by worst reader's status first and then location's name
		Collections.sort(rlInfo.getReaderlocationinfo(), sampleLocationInfo.new ByReaderHealth());
	}
}
