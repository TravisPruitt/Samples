package com.disney.xband.jmslistener;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.cache.CacheTask;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.gff.GffService;
import com.disney.xband.jmslistener.idms.IdmsService;
import com.disney.xband.jmslistener.xi.XiService;

public class JmsListener 
{
	private static Logger logger = Logger.getLogger(JmsListener.class);

	private static DiscoveryTask discoveryTask = null;
	private static CacheTask cacheTask = null;

	// termination flag
	private static boolean bTerminate = false;
	
	private JmsListener()
	{
	}

	/* Run the listener client */
   public static void main(String [] args)
    {
  		try
        {
            Process();
        }
        catch (Exception e)
        { 
	    	logger.error(ExceptionFormatter.format("Unexpected error - ", e));
        }
    }
   
    public static void Process()
	{
    	try
    	{
            GffService.INSTANCE.Start();
    		XiService.INSTANCE.Start();
	    	IdmsService.INSTANCE.Start();
	    	WebServer.INSTANCE.Start();
	
            discoveryTask = new DiscoveryTask(ConfigurationProperties.INSTANCE.getDiscoveryRetryPeriod());
            discoveryTask.setDaemon(false);
            discoveryTask.start();

            cacheTask = new CacheTask(ConfigurationProperties.INSTANCE.getCacheExpirationMinutes());
            cacheTask.setDaemon(false);
            cacheTask.start();

	    	// run the listener forever
			for (;;)
			{
				// check the termination flag
				if (bTerminate)
					break;
	
				// outer try/catch in case anything fails here
				try
				{
					GffService.INSTANCE.Restart();
	            	XiService.INSTANCE.Restart();
	            	IdmsService.INSTANCE.Restart();
	
	                Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					logger.error(ExceptionFormatter.format("Unexpected exception in Process loop", e));
				}
				catch (Exception ex)
				{
					logger.error(ExceptionFormatter.format("Unexpected exception in Process loop", ex));
				}
			}
    	}
		catch (Exception ex)
		{
			logger.error(ExceptionFormatter.format("Unexpected exception starting listeners.", ex));
		}
	}

    public static void Stop()
	{
    	bTerminate = true;
    	
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
    	catch(Exception ex)
    	{
			logger.error(ExceptionFormatter.format("Errors stoppping GFF Listeners.", ex));
    	}

        try
        {
            XiService.INSTANCE.Stop();
        }
        catch(Exception ex)
        {
            logger.error(ExceptionFormatter.format("Errors stoppping Xi Listeners.", ex));
        }

        try
        {
            IdmsService.INSTANCE.Stop();
        }
        catch(Exception ex)
        {
            logger.error(ExceptionFormatter.format("Errors stoppping IDMS Listeners.", ex));
        }

        try
        {
            WebServer.INSTANCE.Stop();
        }
        catch(Exception ex)
        {
            logger.error(ExceptionFormatter.format("Errors stoppping WebServer.", ex));
        }
	}
}
