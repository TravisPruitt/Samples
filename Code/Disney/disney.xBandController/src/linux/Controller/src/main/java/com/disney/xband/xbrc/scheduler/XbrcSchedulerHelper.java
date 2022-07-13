package com.disney.xband.xbrc.scheduler;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerMetadataList;
import com.disney.xband.common.scheduler.SchedulerMetadataLoader;
import com.disney.xband.common.scheduler.XconnectScheduler;

public class XbrcSchedulerHelper {
	
	private static Logger logger = Logger.getLogger(XbrcSchedulerHelper.class);
	
	public static void registerClasses(XconnectScheduler scheduler) throws JAXBException
	{
		SchedulerMetadataList metadata = SchedulerMetadataLoader.loadFromXmlResource("ControllerSchedulerItems.xml");
		for (SchedulerItemMetadata md : metadata.getMetadata())
			scheduler.registerItemMetadata(md);
	}
}
