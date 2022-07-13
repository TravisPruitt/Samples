package com.disney.xband.xbrc.scheduler;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.XconnectSchedulerJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;
import com.disney.xband.xbrc.lib.db.SchedulerLogService;
import com.disney.xband.xbrc.lib.model.XBRCController;

@DisallowConcurrentExecution
public class SchedulerLogCleanup extends XconnectSchedulerJob {

	private static Logger logger = Logger.getLogger(SchedulerLogCleanup.class);
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception {
		
		SchedulerItemParameter param = context.getParameters().get("keep.days");
		if (param == null || param.getValue() == null || param.getValue().isEmpty())
			throw new Exception("The required job parameter keep.days ws not provided.");
		
		int keepDays = Integer.parseInt(param.getValue());
		
		// For safety reasons we will not allow deleting of records that are more recent than a day.
		
		if (keepDays < 1)
			throw new Exception("Cannot delete scheduler logs data because the keep.days parameter is set to " + keepDays +
								" The minimum value required for keep.days is 1 day");
		
		logger.info("Deleting scheduler logs data older than " + keepDays + " days");
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, keepDays * -1);
        Date olderThan = cal.getTime();
		
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			
			SchedulerLogService.delete(conn, olderThan);
		}
		finally
		{
			XBRCController.getInstance().releasePooledConnection(conn);
		}
	}

	@Override
	public void abort(XconnectSchedulerJobContext context) throws Exception {
		// nothing to do
	}

}
