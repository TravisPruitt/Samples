package com.disney.xband.jmslistener;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.cache.CacheTask;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.gff.GffService;
import com.disney.xband.jmslistener.idms.IdmsService;
import com.disney.xband.jmslistener.xi.XiService;


public class Main implements Daemon 
{
	private static Logger logger = Logger
			.getLogger(Main.class);

	private static ListenerTask restartTask = null;
	private static DiscoveryTask discoveryTask = null;
	private static CacheTask cacheTask = null;

    public static void main(String[] args) 
    {
    }

    @Override
	public void destroy()
	{
	}

	@Override
	public void init(DaemonContext arg0) 
			throws DaemonInitException, Exception 
	{
	}

	@Override
	public void start() 
			throws Exception 
	{
    	try
    	{
            GffService.INSTANCE.Start();
    		XiService.INSTANCE.Start();
	    	IdmsService.INSTANCE.Start();
	    	WebServer.INSTANCE.Start();

	    	StatusInfo.INSTANCE.getListenerStatus().setPerfMetricsPeriod(300);
	    	
	    	if (restartTask != null) {
                restartTask.shutdown();
                restartTask = null;
            }

            restartTask = new ListenerTask(3000);
            restartTask.setDaemon(false);
            restartTask.start();

            if (discoveryTask != null) {
                discoveryTask.shutdown();
                discoveryTask = null;
            }

            discoveryTask = new DiscoveryTask(ConfigurationProperties.INSTANCE.getDiscoveryRetryPeriod());
            discoveryTask.setDaemon(false);
            discoveryTask.start();

            if (cacheTask != null) {
                cacheTask.shutdown();
                cacheTask = null;
            }

            cacheTask = new CacheTask(ConfigurationProperties.INSTANCE.getCacheExpirationMinutes());
            cacheTask.setDaemon(false);
            cacheTask.start();
    	}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Unexpected exception starting listeners.", ex));
		}
	}

	@Override
	public void stop() 
			throws Exception 
	{
    	if (restartTask != null)
    	{
            try {
                restartTask.shutdown();
            }
            catch(Exception ignore) {
            }
            finally {
            	restartTask = null;
            }
        }

    	if (discoveryTask != null)
    	{
            try {
                discoveryTask.shutdown();
            }
            catch(Exception ignore) {
            }
            finally {
            	discoveryTask = null;
            }
        }
    	
    	if (cacheTask != null)
    	{
            try {
                cacheTask.shutdown();
            }
            catch(Exception ignore) {
            }
            finally {
            	cacheTask = null;
            }
        }
    	
    	try
    	{
    		GffService.INSTANCE.Stop();
    	}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format(
					"Unexpected exception stopping GFF listeners.", ex));
		}

        try
        {
            XiService.INSTANCE.Stop();
        }
        catch (Exception ex)
        {
            logger.error(ExceptionFormatter.format(
                    "Unexpected exception stopping Xi listeners.", ex));
        }

        try
        {
            IdmsService.INSTANCE.Stop();
        }
        catch (Exception ex)
        {
            logger.error(ExceptionFormatter.format(
                    "Unexpected exception stopping IDMS listeners.", ex));
        }

        try
        {
            WebServer.INSTANCE.Stop();
        }
        catch (Exception ex)
        {
            logger.error(ExceptionFormatter.format(
                    "Unexpected exception stopping WebServer.", ex));
        }
	}

}
