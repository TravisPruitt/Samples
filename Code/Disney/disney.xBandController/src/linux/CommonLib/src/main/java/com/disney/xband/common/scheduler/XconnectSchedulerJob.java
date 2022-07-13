package com.disney.xband.common.scheduler;

import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public abstract class XconnectSchedulerJob implements Job, InterruptableJob {
	
	private static Logger logger = Logger.getLogger(XconnectSchedulerJob.class);
	private XconnectSchedulerJobContext context;
	private boolean abort = false;
	
	// Implement run() in the derived class to do the work.
	public abstract void run(XconnectSchedulerJobContext context) throws Exception;
	
	// Implement abort() in the derived class to abort execution if possible. Otherwise throw Exception if cannot abort 
	// and the thread will be killed by the scheduler.
	public abstract void abort(XconnectSchedulerJobContext context) throws Exception;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerItem item = (SchedulerItem) context.getMergedJobDataMap().get("schedulerItem");
		XconnectScheduler scheduler = (XconnectScheduler) context.getMergedJobDataMap().get("scheduler");
		XconnectSchedulerJobContext jobContext = new XconnectSchedulerJobContext(item);
		
		try {
			this.context = jobContext;
			scheduler.getSerializer().insertLog(jobContext.getLog());
		} catch (Exception e) {
			logger.error("Failed to insert scheduler log into database " + jobContext.getLog(), e);
		}
		
		item.setLastLog(jobContext.getLog());
		
		try {
			
			// Substitute any environment property values now
			Properties envProperties = XconnectScheduler.getInstance().getEnvProperties();
			for (SchedulerItemParameter param : jobContext.getParameters().values()) {
				if (!param.isEnvPropValue())
					continue;
				String value = envProperties != null ? envProperties.getProperty(param.getValue()) : null;
				if (value == null || value.isEmpty())
					throw new Exception("enviroment.properties value is not set: " + param.getValue() + " for parameter " + param.getName());
				param.setValue(value);
			}
			
			run(jobContext);
		}
		catch(Exception e) {
			jobContext.getLog().setSuccess(false);
			jobContext.getLog().setStatusReport("Job failed with exception: " + 
					(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.toString()));
			logger.warn("Job " + item + " failed.", e);
		}
		
		jobContext.getLog().setFinishDate(new Date());
		
		try {
			scheduler.getSerializer().updateLog(jobContext.getLog());
		} catch (Exception e) {
			logger.error("Failed to update the scheduler log record in the database " + jobContext.getLog(), e);
		}
	}
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		 try {
			 logger.warn("Scheduler job " + context.getDescription() + " ItemKey: " + context.getKey() + " is being aborted");
			 abort = true;
			 
			 // allow the subclass to do any other abort processing
			 abort(context);
		 } catch( Exception e) {
			 logger.warn("Scheduler job " + context.getDescription() + " ItemKey: " + context.getKey() + " could not be aborted gracefully. (" +
					 	 e.getLocalizedMessage() + "). The job thread will be killed forcefully.");
			 throw new UnableToInterruptJobException(e);
		 }
	}
	
	public boolean isAborted() {
		return abort;
	}
}
