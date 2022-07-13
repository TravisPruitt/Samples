package com.disney.xband.common.lib.junit.scheduler;

import java.util.Date;
import java.util.List;

import com.disney.xband.common.scheduler.SchedulerItem;
import com.disney.xband.common.scheduler.SchedulerLog;
import com.disney.xband.common.scheduler.XconnectSchedulerSerializer;

public class TestSchedulerSerializer implements XconnectSchedulerSerializer {

	@Override
	public void insertItem(SchedulerItem item, String updatedBy)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateItem(SchedulerItem item, String updatedBy)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteItem(String key) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SchedulerItem> findItems() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertLog(SchedulerLog log) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLog(SchedulerLog log) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteLogs(Date olderThan) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteLogs(Date olderThan, String jobClassName)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SchedulerLog> findLogs(Date startDate, String jobClassName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SchedulerLog> findItemLogs(Date startDate, String itemKey)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
