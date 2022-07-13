package com.disney.xband.jmslistener;

import java.util.Date;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.gff.GffService;
import com.disney.xband.jmslistener.idms.IdmsService;
import com.disney.xband.jmslistener.xi.XiService;

public class ListenerTask extends Thread implements IStoppable
{
	private static Logger logger = Logger.getLogger(ListenerTask.class);
    private volatile boolean isStopped;
    private long repeatMs;

    public ListenerTask(long repeatMs) {
        this.repeatMs = repeatMs;
    }

    @Override
    public void shutdown() {
        this.isStopped = true;
    }

    @Override
    public void run() {
        try {
            while (!isStopped) {
                try {
                    //Call restart.
                    //Will ignore if listener already started.
                    XiService.INSTANCE.Restart();
                    IdmsService.INSTANCE.Restart();
                    GffService.INSTANCE.Restart();

                    Date dtNow = new Date();
                    Date dtStart = StatusInfo.INSTANCE.getListenerStatus().getStartPerfDate();
                    long sec = (dtNow.getTime() - dtStart.getTime()) / 1000;

                    if (sec > StatusInfo.INSTANCE.getListenerStatus().getPerfMetricsPeriod()) {
                        StatusInfo.INSTANCE.getListenerStatus().clearPerfValues();
                    }

                }
                catch (Exception ex) {
                    logger.error(ExceptionFormatter.format("Unexpected exception in Listener Task", ex));
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

}
