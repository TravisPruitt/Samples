package com.disney.xband.xbrms.server.scheduler;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.XconnectSchedulerJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;
import com.disney.xband.xbrms.server.managed.XbrcPerfService;

@DisallowConcurrentExecution
public class PerfMetricsTableCleanupJob extends XconnectSchedulerJob {

	private static Logger logger = Logger.getLogger(PerfMetricsTableCleanupJob.class);
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception {
		
		SchedulerItemParameter param = context.getParameters().get("keep.days");
		if (param == null || param.getValue() == null || param.getValue().isEmpty())
			throw new Exception("The required job parameter keep.days ws not provided.");
		
		int keepDays = Integer.parseInt(param.getValue());
		
		logger.info("Deleting all performance metric data older than " + keepDays + " days");
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        cal.add(Calendar.DATE, keepDays * -1);
		
        XbrcPerfService perfService = new XbrcPerfService();
        perfService.deleteOlderThan(cal.getTime(), cal);
		if (isAborted())
			return;
	}

	@Override
	public void abort(XconnectSchedulerJobContext context) throws Exception {
		// nothing to do
	}

}
