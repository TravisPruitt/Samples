package com.disney.xband.xbrc.parkentrymodel.scheduler;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.XconnectSchedulerJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;
import com.disney.xband.xbrc.lib.db.PETransactionService;
import com.disney.xband.xbrc.lib.db.XbioImageService;
import com.disney.xband.xbrc.lib.db.XbioTemplateService;
import com.disney.xband.xbrc.lib.model.XBRCController;

@DisallowConcurrentExecution
public class DatabaseCleanupTask extends XconnectSchedulerJob {

	private static Logger logger = Logger.getLogger(DatabaseCleanupTask.class);
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception {
		
		SchedulerItemParameter param = context.getParameters().get("keep.days");
		if (param == null || param.getValue() == null || param.getValue().isEmpty())
			throw new Exception("The required job parameter keep.days ws not provided.");
		
		int keepDays = Integer.parseInt(param.getValue());
		
		// For safety reasons we will not allow deleting of records that are more recent than a day.
		// This is just in case someone enters a wrong value for the keep.days parameter by a mistake.
		// The assumption is that a daily data export task runs so data for the previous day should be archived.
		
		if (keepDays < 1)
			throw new Exception("Cannot delete park entry guest data because the keep.days parameter is set to " + keepDays +
								" The minimum value required for keep.days is 1 day");
		
		logger.info("Deleting all park entry guest data older than " + keepDays + " days");
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, keepDays * -1);
        Date olderThan = cal.getTime();
		
		Connection conn = null;
		try
		{
			conn = XBRCController.getInstance().getPooledConnection();
			
			XbioImageService.deleteOlderThan(conn, olderThan);
			if (isAborted())
				return;
			XbioTemplateService.deleteOlderThan(conn, olderThan);
			if (isAborted())
				return;
			PETransactionService.deleteOlderThan(conn, olderThan);
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
