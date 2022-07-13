package com.disney.xband.jmslistener.xi;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import com.disney.xband.jmslistener.Listener;
import org.apache.log4j.Logger;
import com.disney.xband.jmslistener.ConnectionPool;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.cache.GxpLinkIdCache;
import com.disney.xband.jmslistener.cache.SecureIdCache;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.configuration.XiListenerConfiguration;

public class XiService extends JmsService {
    public static XiService INSTANCE = new XiService();

    private static Logger xiServiceLogger = Logger.getLogger(XiService.class);
    private ConnectionPool connectionPool;
    private GxpLinkIdCache gxpLinkIdCache;
    private SecureIdCache secureIdCache;

    private XiService() {
        super(ConfigurationProperties.INSTANCE.getXiListenerConfiguration(), "XiService");

        this.gxpLinkIdCache = new GxpLinkIdCache(getXiListenerConfiguration().getIdmsRootUrl(),
                ConfigurationProperties.INSTANCE.getCacheExpirationMinutes(),
                ConfigurationProperties.INSTANCE.getCacheSize());

        this.secureIdCache = new SecureIdCache(getXiListenerConfiguration().getIdmsRootUrl(),
                ConfigurationProperties.INSTANCE.getCacheExpirationMinutes(),
                ConfigurationProperties.INSTANCE.getCacheSize());

        this.noTrafficToleranceSecs = this.getXiListenerConfiguration().getNoTrafficToleranceSecs();

        if (ConnectionNeeded()) {
            this.connectionPool = new ConnectionPool(ConfigurationProperties.INSTANCE.getXiListenerConfiguration());
            this.connectionPool.Connect();
        }
    }

    public XiListenerConfiguration getXiListenerConfiguration() {
        return (XiListenerConfiguration) this.getConfiguration();
    }

    public GxpLinkIdCache getGxpLinkIdCache() {
        return this.gxpLinkIdCache;
    }

    public SecureIdCache getSecureIdCache() {
        return this.secureIdCache;
    }

    @Override
    protected boolean ConnectionNeeded() {
        if (this.getXiListenerConfiguration().getXbrcTopic().startsWith("#") &&
                this.getXiListenerConfiguration().getXpassTopic().startsWith("#") &&
                this.getXiListenerConfiguration().getBlueLaneTopic().startsWith("#") &&
                this.getXiListenerConfiguration().getXpassRedeemTopic().startsWith("#"))
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean OpenSubscriptions() {
        final int sharedConfigurationsCount = this.getConfiguration().getSharedConfigurations();

        if (!this.getXiListenerConfiguration().getXbrcTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getXiListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getXiListenerConfiguration().getXbrcTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new XbrcListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (xiServiceLogger.isInfoEnabled()) {
                    xiServiceLogger.info("Created " + num + " shared subscriptions to " +
                            this.getXiListenerConfiguration().getXbrcTopic());
                }
            }
            catch (JMSException ex) {
                xiServiceLogger.error("Error creating " + this.getXiListenerConfiguration().getXbrcTopic(), ex);
                return false;
            }
        }

        if (!this.getXiListenerConfiguration().getXpassTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getXiListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getXiListenerConfiguration().getXpassTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new BookingEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (xiServiceLogger.isInfoEnabled()) {
                    xiServiceLogger.info("Created " + num + " shared subscriptions to " +
                            this.getXiListenerConfiguration().getXpassTopic());
                }
            }
            catch (JMSException ex) {
                xiServiceLogger.error("Error creating " + this.getXiListenerConfiguration().getXpassTopic(), ex);
                return false;
            }
        }

        if (!this.getXiListenerConfiguration().getBlueLaneTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getXiListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getXiListenerConfiguration().getBlueLaneTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new BlueLaneEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (xiServiceLogger.isInfoEnabled()) {
                    xiServiceLogger.info("Created " + num + " shared subscriptions to " +
                            this.getXiListenerConfiguration().getBlueLaneTopic());
                }
            }
            catch (JMSException ex) {
                xiServiceLogger.error("Error creating " + this.getXiListenerConfiguration().getBlueLaneTopic(), ex);
                return false;
            }
        }

        if (!this.getXiListenerConfiguration().getXpassRedeemTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getXiListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getXiListenerConfiguration().getXpassRedeemTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new RedemptionEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                if (xiServiceLogger.isInfoEnabled()) {
                    xiServiceLogger.info("Created " + num + " shared subscriptions to " +
                            this.getXiListenerConfiguration().getXpassRedeemTopic());
                }
            }
            catch (JMSException ex) {
                xiServiceLogger.error("Error creating " + this.getXiListenerConfiguration().getXpassRedeemTopic(), ex);
                return false;
            }
        }

        return true;
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    @Override
    public int getCacheCapcity() {
        return this.gxpLinkIdCache.getCacheCapacity() + this.secureIdCache.getCacheCapacity();
    }

    @Override
    public int getCacheSize() {
        return this.gxpLinkIdCache.getCacheSize() + this.secureIdCache.getCacheSize();
    }

    @Override
    public void clearCache() {
        this.gxpLinkIdCache.clear();
        this.secureIdCache.clear();
    }
}
