package com.disney.xband.jmslistener.cache;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.IStoppable;
import org.apache.log4j.Logger;

public class CacheTask extends Thread implements IStoppable
{
    private static Logger logger = Logger.getLogger(CacheTask.class);

    private volatile boolean isStopped;
    private long repeatMs;

    public CacheTask(long repeatMs) {
        this.repeatMs = repeatMs;
    }

    @Override
    public void shutdown() {
        this.isStopped = true;
    }

	@Override
	public void run() 
	{
        if (false) {
            try {
                while (!isStopped) {
                    try {
                    }
                    catch (Exception ex) {
                        logger.error(ExceptionFormatter.format("Unexpected exception in CacheTask", ex));
                    }

                    try {
                        Thread.sleep(repeatMs);
                    }
                    catch (InterruptedException ignore) {
                    }
                }
            }
            catch (Throwable ex) {
                logger.error(ExceptionFormatter.format("Exiting due to the critical error: ", ex));
                System.exit(123);
            }
        }
        else {
            logger.debug("JMS caching disabled.");
            return;
        }
	}
}
