package com.disney.xband.jmslistener.idms;

import java.util.ArrayList;
import java.util.List;
import javax.jms.*;

import com.disney.xband.jmslistener.*;
import org.apache.log4j.Logger;

import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.idms.dao.DAOException;
import com.disney.xband.idms.dao.DAOFactory;
import com.disney.xband.idms.lib.model.GuestLocatorCollection;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.configuration.IdmsListenerConfiguration;

public class IdmsService extends JmsService {
    public static IdmsService INSTANCE = new IdmsService();

    private static Logger idmsServiceLogger = Logger.getLogger(IdmsService.class);
    private static DAOFactory daoFactory = DAOFactory.getDAOFactory();
    private List<String> guestLocators;

    public IdmsListenerConfiguration getIdmsListenerConfiguration() {
        return (IdmsListenerConfiguration) this.getConfiguration();
    }

    public List<String> getGuestLocators() {
        return this.guestLocators;
    }

    public IdmsService() {
        super(ConfigurationProperties.INSTANCE.getIdmsListenerConfiguration(), "IdmsService");

        this.noTrafficToleranceSecs = this.getIdmsListenerConfiguration().getNoTrafficToleranceSecs();
    }

    @Override
    protected boolean ConnectionNeeded() {
        if (this.getIdmsListenerConfiguration().getNotificationQueue().startsWith("#") &&
                this.getIdmsListenerConfiguration().getPxcCallbackQueue().startsWith("#") &&
                this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic().startsWith("#") &&
                this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic().startsWith("#") &&
                this.getIdmsListenerConfiguration().getXbmsXbandTopic().startsWith("#") &&
                this.getIdmsListenerConfiguration().getIdmsXbandTopic().startsWith("#") &&
                this.getIdmsListenerConfiguration().getPxcReceiveQueue().startsWith("#"))
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean OpenSubscriptions() {
        final int sharedConfigurationsCount = this.getIdmsListenerConfiguration().getSharedConfigurations();
        final String baseName = this.getIdmsListenerConfiguration().getSharedConfigurationGroup().replace("[", "").replace("]", "");

        if (!this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getIdmsListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic());

                    final String subscriberName = baseName + i;

                    final MessageConsumer consumer = session.createDurableSubscriber(
                            topic, "XBMSXBANDREQUEST" + subscriberName);

                    final RetryMessageQueue xbmsXbandRequestMessageQueue =
                            new RetryMessageQueue(this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic());

                    final Listener listener = new XbandRequestListener(this, xbmsXbandRequestMessageQueue);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (idmsServiceLogger.isInfoEnabled()) {
                    idmsServiceLogger.info("Created " + num + " shared subscriptions  to " +
                            this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic());
                }
            }
            catch (JMSException ex) {
                idmsServiceLogger.error("Error creating " + this.getIdmsListenerConfiguration().getXbmsXbandRequestTopic(), ex);
                return false;
            }
        }

        if (!this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getIdmsListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic());

                    final String subscriberName = baseName + i;

                    final MessageConsumer consumer = session.createDurableSubscriber(
                            topic, "IDMSXBANDREQUEST" + subscriberName);

                    final RetryMessageQueue idmsXbandRequestMessageQueue =
                            new RetryMessageQueue(this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic());

                    final Listener listener = new XbandRequestListener(this, idmsXbandRequestMessageQueue);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (idmsServiceLogger.isInfoEnabled()) {
                    idmsServiceLogger.info("Created " + num + " shared subscriptions  to " +
                            this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic());
                }
            }
            catch (JMSException ex) {
                idmsServiceLogger.error("Error creating " + this.getIdmsListenerConfiguration().getIdmsXbandRequestTopic(), ex);
                return false;
            }
        }

        if (!this.getIdmsListenerConfiguration().getXbmsXbandTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());
                    final Topic topic = session.createTopic(
                            this.getIdmsListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getIdmsListenerConfiguration().getXbmsXbandTopic());

                    final String subscriberName = baseName + i;

                    final MessageConsumer consumer = session.createDurableSubscriber(
                            topic, "XBMSXBAND" + subscriberName);

                    final RetryMessageQueue xbmsXbandMessageQueue =
                            new RetryMessageQueue(this.getIdmsListenerConfiguration().getXbmsXbandTopic());

                    final Listener listener = new XbandListener(this, xbmsXbandMessageQueue, null, session);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (idmsServiceLogger.isInfoEnabled()) {
                    idmsServiceLogger.info("Created " + num + " shared subscriptions  to " +
                            this.getIdmsListenerConfiguration().getXbmsXbandTopic());
                }
            }
            catch (JMSException ex) {
                idmsServiceLogger.error("Error creating " + this.getIdmsListenerConfiguration().getXbmsXbandTopic(), ex);
                return false;
            }
        }

        if (!this.getIdmsListenerConfiguration().getIdmsXbandTopic().startsWith("#")) {
            try {
                int num = 0;
                int nfNum = 0;
                final MessageQueue notificationMessageQueue = new MessageQueue();

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getIdmsListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getIdmsListenerConfiguration().getIdmsXbandTopic());

                    final String subscriberName = baseName + i;

                    final MessageConsumer consumer = session.createDurableSubscriber(
                            topic, "IDMSXBAND" + subscriberName);

                    final RetryMessageQueue idmsXbandMessageQueue =
                            new RetryMessageQueue(this.getIdmsListenerConfiguration().getIdmsXbandTopic());

                    final Listener listener =
                            new XbandListener(this, idmsXbandMessageQueue, notificationMessageQueue, session);

                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;

                    if (!this.getIdmsListenerConfiguration().getNotificationQueue().startsWith("#")) {
                        final NotificationQueue notificationQueue = new NotificationQueue(
                                this, notificationMessageQueue,
                                this.getIdmsListenerConfiguration().getNotificationQueue(),
                                this.getIdmsListenerConfiguration().getJmsRetryPeriod(), i);

                        if (idmsServiceLogger.isInfoEnabled()) {
                            idmsServiceLogger.info("Created publisher for " +
                                    this.getIdmsListenerConfiguration().getNotificationQueue());
                        }

                        stoppableItems.add(notificationQueue);
                        ++nfNum;
                        notificationQueue.start();
                    }
                }

                if (idmsServiceLogger.isInfoEnabled()) {
                    idmsServiceLogger.info("Created " + num + " shared subscriptions  to " +
                            this.getIdmsListenerConfiguration().getIdmsXbandTopic());
                    idmsServiceLogger.info("Created " + nfNum + " notification queues for " +
                            this.getIdmsListenerConfiguration().getNotificationQueue());
                }
            }
            catch (JMSException ex) {
                idmsServiceLogger.error("Error creating " + this.getIdmsListenerConfiguration().getIdmsXbandTopic(), ex);
                return false;
            }
        }

        if (!this.getIdmsListenerConfiguration().getPxcReceiveQueue().startsWith("#")) {
            try {
                int num = 0;
                int nfNum = 0;
                final MessageQueue callbackMessageQueue = new MessageQueue();

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Queue pxcReceiveQueue =
                            session.createQueue(this.getIdmsListenerConfiguration().getPxcReceiveQueue());

                    final MessageConsumer consumer = session.createConsumer(pxcReceiveQueue);

                    final Listener listener = new PxcListener(this, callbackMessageQueue, session);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;

                    if (!this.getIdmsListenerConfiguration().getPxcCallbackQueue().startsWith("#")) {
                        final NotificationQueue callbackQueue = new NotificationQueue(this,
                                callbackMessageQueue, this.getIdmsListenerConfiguration().getPxcCallbackQueue(),
                                this.getIdmsListenerConfiguration().getJmsRetryPeriod(), i);

                        if (idmsServiceLogger.isInfoEnabled()) {
                            idmsServiceLogger.info("Created publisher for " +
                                    this.getIdmsListenerConfiguration().getPxcReceiveQueue());
                        }

                        stoppableItems.add(callbackQueue);
                        ++nfNum;
                        callbackQueue.start();
                    }
                }

                if (idmsServiceLogger.isInfoEnabled()) {
                    idmsServiceLogger.info("Created " + num + " shared subscriptions  to " +
                            this.getIdmsListenerConfiguration().getPxcReceiveQueue());
                    idmsServiceLogger.info("Created " + nfNum + " notification queues for " +
                            this.getIdmsListenerConfiguration().getPxcReceiveQueue());
                }
            }
            catch (JMSException ex) {
                idmsServiceLogger.error("Error creating " + this.getIdmsListenerConfiguration().getPxcReceiveQueue(), ex);
                return false;
            }
        }

        initGuestLocators(1);

        return true;
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return null;
    }

    @Override
    public int getCacheCapcity() {
        return 0;
    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public void clearCache() {
    }

    private void initGuestLocators(int attempt) {
        try {
            final GuestLocatorCollection guestLocatorCollection = daoFactory.getGuestDAO().GetGuestLocators();

            if (guestLocatorCollection != null) {
                this.guestLocators = guestLocatorCollection.getGuestLocators();
            }

            if (this.guestLocators == null) {
                this.guestLocators = new ArrayList<String>();

                this.guestLocators.add("gxp-link-id");
                this.guestLocators.add("dme-link-id");
                this.guestLocators.add("xbms-link-id");
                this.guestLocators.add("xid");
                this.guestLocators.add("FBID");
                this.guestLocators.add("swid");
                this.guestLocators.add("transactional-guest-id");
                this.guestLocators.add("gff-bog-link-id");
                this.guestLocators.add("admission-link-id");
                this.guestLocators.add("payment-link-id");
                this.guestLocators.add("media-link-id");
                this.guestLocators.add("leveln-link-id");
            }
        }
        catch (DAOException ex) {
            if (attempt > 0) {
                initGuestLocators(--attempt);
            }
            else {
                idmsServiceLogger.error(ExceptionFormatter.format("Can't get guest locators: ", ex));
            }
        }
    }
}
