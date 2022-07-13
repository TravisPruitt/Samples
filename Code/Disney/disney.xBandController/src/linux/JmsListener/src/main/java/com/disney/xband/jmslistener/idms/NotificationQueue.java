package com.disney.xband.jmslistener.idms;

import javax.jms.*;

import com.disney.xband.jmslistener.IStoppable;
import org.apache.log4j.Logger;
import progress.message.jclient.ConnectionStateChangeListener;
import progress.message.jclient.Constants;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.MessageQueue;

public class NotificationQueue extends Thread implements ExceptionListener, ConnectionStateChangeListener, IStoppable {
    private static Logger logger = Logger.getLogger(NotificationQueue.class);

    private static int MAX_QUEUE_SIZE = 256 * 1024;

    private JmsService service;
    private MessageQueue messageQueue;
    private String queueName;
    private Session session;
    private MessageProducer messageProducer;
    private volatile boolean isStopped;
    private long repeatMs;

    public NotificationQueue(JmsService service, MessageQueue businessEventQueue, String queueName, long repeatMs, int connNum)
    throws JMSException {

        this.service = service;
        this.messageQueue = businessEventQueue;
        this.queueName = queueName;
        this.repeatMs = repeatMs;

        if (!this.queueName.startsWith("#")) {
            try {
                this.session = service.createSession(this.queueName);
                final Queue queue = this.session.createQueue(this.queueName);
                this.messageProducer = session.createProducer(queue);

                if (logger.isInfoEnabled()) {
                    logger.info(
                        "Connected to queue: " + this.queueName + " on broker: " + service.getConfiguration().getJmsBrokerUrl() +
                        " using Domain - " + this.service.getConfiguration().getJmsBrokerDomain() +
                        " and User - " + this.service.getConfiguration().getJmsUser());
                }
            }
            catch (JMSException ex) {
                logger.error(ExceptionFormatter.format(
                    "Cannot connect to JMS Broker - " + this.service.getConfiguration().getJmsBrokerUrl() +
                    " using Domain - " + this.service.getConfiguration().getJmsBrokerUrl() +
                    " and User - " + this.service.getConfiguration().getJmsUser() + " and Queue - " + this.queueName, ex));

                throw ex;
            }
        }
    }

    @Override
    public void run() {
        if ((this.messageQueue != null) && (this.messageProducer != null)) {
            try {
                int count = 0;

                while (!isStopped) {
                    try {
                        send();
                    }
                    catch (Exception ex) {
                        logger.error(ExceptionFormatter.format("Unexpected exception in NotificationQueue: ", ex));
                    }

                    try {
                        Thread.sleep(this.repeatMs);
                    }
                    catch (InterruptedException ignore) {
                    }

                    if(count > 100) {
                        count = 0;

                        if(logger.isDebugEnabled()) {
                            logger.debug(this.queueName + " current queue size " + this.messageQueue.size());
                        }
                    }
                }
            }
            catch (Throwable ex) {
                logger.error(ExceptionFormatter.format("Exiting due to the critical error: ", ex));
                System.exit(1);
            }
        }
    }

    public void onException(JMSException ex) {
        logger.error("JMS session error occurred. Stopping publishing of notifications: ", ex);
        this.service.Stop();
    }

    @Override
    public void connectionStateChanged(int state) {
        if (state == Constants.ACTIVE) {
            if (logger.isInfoEnabled()) {
                logger.info("Discovery connection state changed to ACTIVE.");
            }
        }
        else if (state == Constants.RECONNECTING) {
            if (logger.isInfoEnabled()) {
                logger.info("Discovery connection state changed to RECONNECTING.");
            }
        }
        else if (state == Constants.FAILED) {
            logger.error("Discovery connection state changed to FAILED.");
        }
        else if (state == Constants.CLOSED) {
            logger.warn("Discovery connection state changed to CLOSED.");
        }
    }

    @Override
    public void shutdown() {
        this.isStopped = true;

        if (this.session != null) {
            try {
                this.session.close();
            }
            catch (Exception e) {
            }
        }

        this.messageProducer = null;

        if (logger.isInfoEnabled()) {
            logger.info("DISCONNECTED from queue: " + this.queueName + " on broker: " +
                this.service.getConfiguration().getJmsBrokerUrl() + " using domain - " +
                this.service.getConfiguration().getJmsBrokerDomain() + " and user - " +
                this.service.getConfiguration().getJmsUser());
        }
    }

    private void send() {
        if ((this.messageProducer != null) && (this.session != null)) {
            TextMessage textMessage = null;

            try {
                while (!this.messageQueue.isEmpty()) {
                    textMessage = this.messageQueue.get();

                    if(textMessage != null) {
                        this.messageProducer.send(textMessage);
                    }
                }
            }
            catch (Exception ex) {
                logger.error("Failed to send a message to " + this.queueName);

                if (textMessage != null) {
                    this.messageQueue.add(textMessage);
                }
            }
        }
    }
}
