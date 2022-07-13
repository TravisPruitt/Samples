package com.disney.xband.common.lib.junit.scheduler;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.XconnectSchedulerJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;

/*
 * This is a test job that expects one parameter: run.time.ms.
 * The job will run for the ammount of time specified.
 * This allows for testing long running jobs etc..
 */
@DisallowConcurrentExecution
public class TestSchedulerJob extends XconnectSchedulerJob {

	private static Logger logger = Logger.getLogger(TestSchedulerJob.class);	
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception {	
	
		SchedulerItemParameter param = context.getParameters().get("run.time.ms");
		if (param == null || param.getValue() == null || param.getValue().isEmpty())
			throw new Exception("The required job parameter run.time.ms ws not provided.");
		
		long runtime = Long.parseLong(param.getValue());
		
		logger.info("TestSchedulerJob: sleeping for " + runtime + " milliseconds");		
		
		// Sleep in 1 second increments 
		long sleepTime = runtime > 1000 ? 1000 : runtime;
		
		while (sleepTime > 0 && !isAborted()) {
			Thread.sleep(sleepTime);
			runtime -= sleepTime;
			sleepTime = runtime > 1000 ? 1000 : runtime;
		}
		
		if (isAborted())
			logger.info("TestSchedulerJob: job was aborted.");
		else
			logger.info("TestSchedulerJob: finished sleeping for " + runtime + " milliseconds");
	}

	@Override
	public void abort(XconnectSchedulerJobContext context) throws Exception {
		// nothing to do here
	}
}
