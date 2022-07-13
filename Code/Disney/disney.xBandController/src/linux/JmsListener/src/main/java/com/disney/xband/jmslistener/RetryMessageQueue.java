package com.disney.xband.jmslistener;

import java.util.concurrent.LinkedBlockingQueue;
import javax.jms.JMSException;
import org.apache.log4j.Logger;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class RetryMessageQueue {
    private static Logger logger = Logger.getLogger(MessageQueue.class);
    private LinkedBlockingQueue<RetryMessage> queue;
    private String name;

    public String getName() {
        return this.name;
    }

    public RetryMessageQueue(String name) {
        this.queue = new LinkedBlockingQueue<RetryMessage>(32);
        this.name = name;
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void add(RetryMessage retryMessage) {
        boolean isAdded = false;

        while (!isAdded) {
            try {
                queue.put(retryMessage);
                isAdded = true;
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void requeueAfterAttempt(RetryMessage retryMessage) {
        if (retryMessage.getAttempts() <= ConfigurationProperties.INSTANCE.getRetryAttempts()) {
            retryMessage.incrementAttempts();
            requeue(retryMessage);
        }
        else {
            try {
                logger.error("Unable to process message: " + retryMessage.getMessage().getText());
            }
            catch (JMSException ex) {
                logger.error(ExceptionFormatter.format("Error when retrieving message text.", ex));
            }
        }
    }

    public void requeue(RetryMessage retryMessage) {
        //logger.trace("Adding message");
        try {
            queue.add(retryMessage);
        }
        catch (IllegalStateException e) {
            logger.error(name + " queue is full. Failed to requeue a message for reprocessing.");
        }
    }

    public RetryMessage get() {
        //logger.trace("Getting message");
        while (true) {
            try {
                return queue.take();
            }
            catch (InterruptedException e) {
            }
        }
    }
}
