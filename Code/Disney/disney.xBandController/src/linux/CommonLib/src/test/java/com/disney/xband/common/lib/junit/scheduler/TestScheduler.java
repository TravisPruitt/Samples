package com.disney.xband.common.lib.junit.scheduler;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerItemMetadata;
import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.SchedulerMetadataList;
import com.disney.xband.common.scheduler.SchedulerMetadataLoader;
import com.disney.xband.common.scheduler.XconnectScheduler;

public class TestScheduler {

	@BeforeClass
	public static void setup() throws Exception {
		XconnectScheduler.getInstance().initialize(new Properties(), new TestSchedulerSerializer());
		
		SchedulerMetadataList metadata = SchedulerMetadataLoader.loadFromXmlResource("TestSchedulerItems.xml");
		for (SchedulerItemMetadata md : metadata.getMetadata())
			XconnectScheduler.getInstance().registerItemMetadata(md);
		
		XconnectScheduler.getInstance().loadItems();
		XconnectScheduler.getInstance().start();
	}
	
	@AfterClass
	public static void destroy() throws Exception {
		XconnectScheduler.getInstance().stop();
	}
	
	private SchedulerItem makeTestItem(String runtimeMs) {
		SchedulerItemMetadata itemMetadata = XconnectScheduler.getInstance().getItemMetadata("com.disney.xband.common.lib.junit.scheduler.TestSchedulerJob");
		assertNotNull(itemMetadata);
		
		SchedulerItem item = new SchedulerItem();
		item.setDescription(itemMetadata.getName());
		item.setEnabled(true);
		item.setItemKey(UUID.randomUUID().toString());
		item.setJobClassName(itemMetadata.getJobClassName());
		item.setSchedulingExpression(itemMetadata.getDefaultSchedulingExpression());
		item.setUpdatedBy("testuser");
		item.setUpdatedDate(new Date());
		
		item.setParameters(new LinkedList<SchedulerItemParameter>());
		
		SchedulerItemParameter runtime = new SchedulerItemParameter();
		runtime.setName("run.time.ms");
		runtime.setValue(runtimeMs);
		runtime.setSequence(1);
		runtime.setItemKey(item.getItemKey());		
		
		item.getParameters().add(runtime);
		
		return item;
	}
	
	@Test
	public void testAddAndRemoveItem() throws Exception {
		SchedulerItem item = makeTestItem("1");
		
		// For this test we don't want this item to start running so we change the scheduling expression
		// to run once at 3 AM
		item.setSchedulingExpression("0 0 3 * * ?");
		
		XconnectScheduler.getInstance().addItem(item);
		
		// now lest remove it
		XconnectScheduler.getInstance().deleteItem(item.getItemKey(), "testuser");
	}
	
	@Test
	public void testRemoveRunningItem() throws Exception {
		SchedulerItem item = makeTestItem("5000");
		
		// For this test we don't want this item to start running immediately so we schedule to run it every second.
		// The metadata for this item does not allow for two of them to run at the same time so hopefully only one job will start.
		item.setSchedulingExpression("0/1 * * * * ?");
		
		XconnectScheduler.getInstance().addItem(item);
		
		// We wait for 2 seconds to let the job start and then try to remove it.
		Thread.sleep(2000);
		
		// now lest remove it
		XconnectScheduler.getInstance().deleteItem(item.getItemKey(), "testuser");
	}
	
	@Test
	public void testStopWhileItemIsRunning() throws Exception {
		SchedulerItem item = makeTestItem("5000");
		
		// For this test we don't want this item to start running immediately so we schedule to run it every second.
		// The metadata for this item does not allow for two of them to run at the same time so hopefully only one job will start.
		item.setSchedulingExpression("0/1 * * * * ?");
		
		XconnectScheduler.getInstance().addItem(item);
		
		// We wait for 2 seconds to let the job start and then try to remove it.
		Thread.sleep(2000);
		
		// Stop the scheduler now.
		XconnectScheduler.getInstance().stop();
	}
}
