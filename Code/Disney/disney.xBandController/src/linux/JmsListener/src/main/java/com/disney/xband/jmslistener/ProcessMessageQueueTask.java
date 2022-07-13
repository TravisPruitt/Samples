package com.disney.xband.jmslistener;

import java.util.Date;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class ProcessMessageQueueTask extends Thread implements IStoppable {
    private static Logger logger = Logger.getLogger(ProcessMessageQueueTask.class);
    private Listener listener;
    private RetryMessageQueue messageQueue;
    private volatile boolean isStopped;
    private long repeatMs;
    private long retryInterval;

    public ProcessMessageQueueTask(Listener listener, RetryMessageQueue messageQueue, long repeatMs) {
        this.listener = listener;
        this.messageQueue = messageQueue;
        this.repeatMs = repeatMs;
        this.retryInterval = ConfigurationProperties.INSTANCE.getRetryPeriodMsecs();
    }

    @Override
    public void shutdown() {
        this.isStopped = true;
    }

    @Override
    public void run() {
        try {
            while (!this.isStopped) {
                this.processQueue();

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

    private void processQueue() {
        while (!messageQueue.isEmpty()) {
            RetryMessage retryMessage = null;

            try {
                retryMessage = messageQueue.get();
            }
            catch (NoSuchElementException e) {
                // another thread must have removed the last message
            }

            if (retryMessage == null) {
                continue;
            }

            try {
                if ((retryMessage.getAttempts() == 0) ||
                    (System.currentTimeMillis() - retryMessage.getLastRetry() > retryInterval)) {

                    final Date startTime = new Date();
                    // this.listener.processMessage(new SavedMessage(retryMessage.getMessage()));
                    this.listener.processMessage(retryMessage.getMessage());
                    final Date endTime = new Date();
                    long msec = endTime.getTime() - startTime.getTime();

                    StatusInfo.INSTANCE.getListenerStatus().getPerfMessageMsec().processValue(msec);

                    if(logger.isDebugEnabled()) {
                        logger.debug("Messages in progress: " + this.messageQueue.size() +
                            " for queue " + this.messageQueue.getName());
                    }
                }
                else {
                    //
                    // Commenting out the log statement because we are spinning to fast and there can be
                    // too many log messages.
                    //
                    // logger.trace("Requeing message. Messages in progress: " + this.messageQueue.size() +
                    //		" for queue " + this.messageQueue.getName());

                    if (messageQueue.isEmpty()) {
                        // Without this delay we are spinning too fast in the loop taking a message off the
                        // queue and putting it back. This take a toll on CPU usage and makes the log file
                        // grow extremely fast.
                        try {
                            Thread.sleep(100);
                        }
                        catch (Exception ignore) {
                        }
                    }

                    messageQueue.requeue(retryMessage);
                }
            }
            catch (Exception ex) {
                final String err = ex.getMessage();

                if((err != null) && (err.indexOf("Rerun the transaction") >= 0) &&
                    (retryMessage.getAttempts() <= ConfigurationProperties.INSTANCE.getRetryAttempts())) {

                    if(logger.isInfoEnabled()) {
                        logger.info("Data access problems detected. Will retry the transaction in " + retryInterval + " msecs");
                    }

                    messageQueue.requeueAfterAttempt(retryMessage);
                }
                else {
                    logger.error(ExceptionFormatter.format("Error in message processing.", ex));
                }
            }
        }
    }
}
