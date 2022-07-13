package com.disney.xband.jmslistener;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.log4j.Logger;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public abstract class Listener implements javax.jms.MessageListener, IStoppable {
    private static Logger logger = Logger.getLogger(Listener.class);
    protected static Logger msgLogger = Logger.getLogger("JmsMessageLogger");

    private static AtomicInteger liveMessageCount = new AtomicInteger(0);
    public static long lastMetrics;
    private long lastMessageReceivedTime;
    private AtomicInteger savedCount = new AtomicInteger(0);
    private AtomicInteger restoredCount = new AtomicInteger(0);
    protected JmsService service;
    private ProcessMessageQueueTask processMessageQueueTask;
    private RetryMessageQueue messageQueue;

    protected Listener(JmsService service) {
        this(service, null);
    }

    public int getLiveMessageCount() {
        return liveMessageCount.get();
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            this.onTextMessage(textMessage);
        }
        catch (Exception ex) {
            logger.error(ExceptionFormatter.format(
                    "Unexpected error - ", ex));
        }
    }

    //public abstract void processMessage(SavedMessage message);
    public abstract void processMessage(TextMessage message);

    public long getLastMessageReceivedTime() {
        return lastMessageReceivedTime;
    }

    public void setLastMessageReceivedTime(long lastMessageReceivedTime) {
        this.lastMessageReceivedTime = lastMessageReceivedTime;
    }

    @Override
    public void shutdown() {
        if (this.processMessageQueueTask != null) {
            this.processMessageQueueTask.shutdown();
        }
    }

    protected Listener(JmsService service, RetryMessageQueue messageQueue) {
        StatusInfo.INSTANCE.registerHealthItem(this);
        this.lastMetrics = System.currentTimeMillis();
        this.lastMessageReceivedTime = System.currentTimeMillis();
        this.service = service;

        if (messageQueue != null) {
            this.messageQueue = messageQueue;
            this.processMessageQueueTask =
                    new ProcessMessageQueueTask(this, messageQueue, ConfigurationProperties.INSTANCE.getMessageProcessingInterval());
            this.processMessageQueueTask.start();
        }
    }

    protected void onTextMessage(TextMessage textMessage) {
        boolean incremented = false;

        try {
            lastMessageReceivedTime = System.currentTimeMillis();
            StatusInfo.INSTANCE.resetHealthItemCounters(this);

            if (this.messageQueue != null) {
                this.messageQueue.add(new RetryMessage(textMessage));

                if (logger.isDebugEnabled()) {
                    logger.debug("Messages in progress: " + this.messageQueue.size() + " for queue " + this.messageQueue.getName());
                }
            }
            else {
                incMessageCount();
                incremented = true;

                Date startTime = new Date();

                //processMessage(new SavedMessage(textMessage));
                processMessage(textMessage);
                Date endTime = new Date();
                long msec = endTime.getTime() - startTime.getTime();

                StatusInfo.INSTANCE.getListenerStatus().getPerfMessageMsec().processValue(msec);

                if (logger.isDebugEnabled()) {
                    logger.debug("Messages in progress: " + this.getLiveMessageCount());
                }
            }

        }
        catch (Exception ex) {
            /*
            if ((ex instanceof DAOException) && !this.testSqlConnectivity()) {
                saveMessage(this.getClass().getName(), textMessage);
                // stopListener
            }
            */

            logger.error(ExceptionFormatter.format("Unexpected error - ", ex));
        }
        catch (Throwable ex) {
            logger.error(ExceptionFormatter.format("Exiting due to the critical error: ", ex));
            System.exit(123);
        }
        finally {
            if (incremented) {
                decMessageCount();
            }

            //Publish every ten minutes.
            if (System.currentTimeMillis() - lastMetrics > 600000) {
                logger.debug("Metrics Period Start: " +
                        StatusInfo.INSTANCE.getListenerStatus().getStartPerfTime());

                StatusInfo.INSTANCE.getListenerStatus().clearPerfValues();

                logger.debug("Message performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfMessageMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfMessageMsec().getMean()));

                logger.debug("IDMS query performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSQueryMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSQueryMsec().getMean()));

                logger.debug("IDMS Update performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfIDMSUpdateMsec().getMean()));

                logger.debug("Gxp Callback performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfGxpCallbackMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfGxpCallbackMsec().getMean()));

                logger.debug("Xband Request Callback performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfXbandRequestCallbackMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfXbandRequestCallbackMsec().getMean()));

                logger.debug("Xband Callback performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfXbandCallbackMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfXbandCallbackMsec().getMean()));

                logger.debug("Pxc Callback performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfPxcCallbackMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfPxcCallbackMsec().getMean()));

                logger.debug("Guest Profile Callback performance - Count: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfGuestProfileCallbackMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfGuestProfileCallbackMsec().getMean()));

                logger.debug("Database call performance: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().getCount()) +
                        " Mean: " +
                        String.valueOf(StatusInfo.INSTANCE.getListenerStatus().getPerfDatabaseMsec().getMean()));

                lastMetrics = System.currentTimeMillis();
            }
        }
    }

    private void saveMessage(String listenerClass, TextMessage message) {
        int num = this.savedCount.incrementAndGet();
    }

    private SavedMessage getSavedMessage() {
        return null;
    }

    private boolean testSqlConnectivity() {
        return DAOFactory.getDAOFactory().testConnectivity();
    }

    private void incMessageCount() {
        liveMessageCount.incrementAndGet();
    }

    private void decMessageCount() {
        liveMessageCount.decrementAndGet();
    }
}
