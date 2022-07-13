package com.disney.xband.xbrc.parkentrymodel.scheduler;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerMetadataList;
import com.disney.xband.common.scheduler.SchedulerMetadataLoader;
import com.disney.xband.common.scheduler.XconnectScheduler;

public class ParkEntrySchedulerHelper {
	
	private static Logger logger = Logger.getLogger(ParkEntrySchedulerHelper.class);
	
	public static void registerClasses() throws JAXBException
	{
		SchedulerMetadataList metadata = SchedulerMetadataLoader.loadFromXmlResource("ParkEntrySchedulerItems.xml");
		for (SchedulerItemMetadata md : metadata.getMetadata())
			XconnectScheduler.getInstance().registerItemMetadata(md);
	}
}
