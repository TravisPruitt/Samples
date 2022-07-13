package com.disney.xband.xbrms.server.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.SchedulerItemParameterMetadata;
import com.disney.xband.common.scheduler.SchedulerMetadataList;
import com.disney.xband.common.scheduler.SchedulerMetadataLoader;
import com.disney.xband.common.scheduler.XconnectScheduler;
import com.disney.xband.xbrms.server.SSConnectionPool;

public class XbrmsSchedulerHelper {
	
	private static Logger logger = Logger.getLogger(XbrmsSchedulerHelper.class);
	
	public static void registerClasses(XconnectScheduler scheduler) throws JAXBException
	{
		SchedulerMetadataList metadata = SchedulerMetadataLoader.loadFromXmlResource("XbrmsSchedulerItems.xml");
		for (SchedulerItemMetadata md : metadata.getMetadata())
		{
			scheduler.registerItemMetadata(md);
			
			if (md.isSystemOnly())
				initSystemItem(md);
		}
	}
	
	private static void initSystemItem(SchedulerItemMetadata md)
	{
		if (md == null)
			return;
		if (md.getKey() == null)
		{
			logger.error("Error in XbrmsSchdulerItem.xml configuration file. Item " 
					+ md.getName() + " is marked as 'system only' but it doesn't define a key/guid. Item not created.");
			return;
		}
		
		Connection conn = null;
		try
		{
			conn = SSConnectionPool.getInstance().getConnection();
			
			if (SchedulerItemService.itemExists(conn, md.getKey()))
				return;
		
			logger.info("Creating system only scheduler job " + md.getName());
			
			SchedulerItem item = new SchedulerItem();
			item.setItemKey(md.getKey());
			item.setDescription(md.getName());
			item.setJobClassName(md.getJobClassName());
			item.setMetadata(md);
			item.setEnabled(true);
			item.setSchedulingExpression(md.getDefaultSchedulingExpression());
			
			item.setParameters(new LinkedList<SchedulerItemParameter>());
			
			if (md.getParameters() != null)
			{
				for (SchedulerItemParameterMetadata pmd : md.getParameters()) {
					SchedulerItemParameter param = new SchedulerItemParameter();
					param.setName(pmd.getName());
					param.setMetadata(pmd);
					param.setItemKey(item.getItemKey());
					param.setSequence(pmd.getSequence());
					param.setValue(pmd.getDefaultValue());
					item.getParameters().add(param);
				}
			}
			
			try
			{
				SchedulerItemService.insert(conn, item, "xBRMS Server");
			}
			catch (Exception e)
			{
				logger.error("Failed to create system only scheduler job " + md.getName(), e);
			}
		}
		catch (SQLException e)
		{
			SSConnectionPool.handleSqlException(e, "looking up a system only scheduler item on system init.");
		}
		finally
		{
			SSConnectionPool.getInstance().releaseConnection(conn);
            conn = null;
		}
	}
}
