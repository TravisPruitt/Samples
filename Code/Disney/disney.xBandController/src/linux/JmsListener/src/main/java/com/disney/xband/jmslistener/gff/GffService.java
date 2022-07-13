package com.disney.xband.jmslistener.gff;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import com.disney.xband.jmslistener.Listener;
import org.apache.log4j.Logger;
import com.disney.xband.jmslistener.ConnectionPool;
import com.disney.xband.jmslistener.JmsService;
import com.disney.xband.jmslistener.cache.XbandIdCache;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.configuration.GffListenerConfiguration;

public class GffService extends JmsService {
    public static GffService INSTANCE = new GffService();

    private static Logger gffServiceLogger = Logger.getLogger(GffService.class);
    private XbandIdCache xbandIdCache;

    private ConnectionPool connectionPool;

    private GffService() {
        super(ConfigurationProperties.INSTANCE.getGffListenerConfiguration(), "GffService");

        this.xbandIdCache = new XbandIdCache(getGffListenerConfiguration().getIdmsRootUrl(),
                ConfigurationProperties.INSTANCE.getCacheExpirationMinutes(),
                ConfigurationProperties.INSTANCE.getCacheSize());

        this.noTrafficToleranceSecs = this.getGffListenerConfiguration().getNoTrafficToleranceSecs();

        if (ConnectionNeeded() && this.connectionPool == null) {
            this.connectionPool = new ConnectionPool(ConfigurationProperties.INSTANCE.getGffListenerConfiguration());
            this.getConnectionPool().Connect();
        }
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return this.connectionPool;
    }

    public GffListenerConfiguration getGffListenerConfiguration() {
        return (GffListenerConfiguration) this.getConfiguration();
    }

    public XbandIdCache getXbandIdCache() {
        return this.xbandIdCache;
    }

    @Override
    protected boolean ConnectionNeeded() {
        if (this.getGffListenerConfiguration().getPointOfSaleTopic().startsWith("#") &&
                this.getGffListenerConfiguration().getTableManagementTopic().startsWith("#") &&
                this.getGffListenerConfiguration().getKitchenManagementTopic().startsWith("#") &&
                this.getGffListenerConfiguration().getXpassVenueTopic().startsWith("#"))
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean OpenSubscriptions() {
        int sharedConfigurationsCount = getGffListenerConfiguration().getSharedConfigurations();

        if (!this.getGffListenerConfiguration().getPointOfSaleTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getGffListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getGffListenerConfiguration().getPointOfSaleTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new PointOfSaleEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                gffServiceLogger.info("Created " + num + " shared subscriptions to " +
                        this.getGffListenerConfiguration().getPointOfSaleTopic());
            }
            catch (JMSException ex) {
                gffServiceLogger.error("Error creating " + this.getGffListenerConfiguration().getPointOfSaleTopic(), ex);
                return false;
            }
        }

        if (!this.getGffListenerConfiguration().getTableManagementTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getGffListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getGffListenerConfiguration().getTableManagementTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new TableManagementEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                gffServiceLogger.info("Created " + num + " shared subscriptions to " +
                        this.getGffListenerConfiguration().getTableManagementTopic());
            }
            catch (JMSException ex) {
                gffServiceLogger.error("Error creating " + this.getGffListenerConfiguration().getTableManagementTopic(), ex);
                return false;
            }
        }

        if (!this.getGffListenerConfiguration().getKitchenManagementTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getGffListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getGffListenerConfiguration().getKitchenManagementTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new KitchenManagementEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                gffServiceLogger.info("Created " + num + " shared subscriptions to " +
                        this.getGffListenerConfiguration().getKitchenManagementTopic());
            }
            catch (JMSException ex) {
                gffServiceLogger.error("Error creating " + this.getGffListenerConfiguration().getKitchenManagementTopic(), ex);
                return false;
            }
        }

        if (!this.getGffListenerConfiguration().getXpassVenueTopic().startsWith("#")) {
            try {
                int num = 0;

                for (int i = 0; i < sharedConfigurationsCount; i++) {
                    final Session session = this.createSession(this.getClass().getName());

                    final Topic topic = session.createTopic(
                            this.getGffListenerConfiguration().getSharedConfigurationGroup() +
                                    this.getGffListenerConfiguration().getXpassVenueTopic());

                    final MessageConsumer consumer = session.createConsumer(topic);
                    final Listener listener = new RestaurantEventListener(this);
                    consumer.setMessageListener(listener);

                    if (i == 0) {
                        listeners.add(listener);
                    }

                    stoppableItems.add(listener);
                    ++num;
                }

                gffServiceLogger.info("Created " + num + " shared subscriptions to " +
                        this.getGffListenerConfiguration().getXpassVenueTopic());
            }
            catch (JMSException ex) {
                gffServiceLogger.error("Error creating " + this.getGffListenerConfiguration().getXpassVenueTopic(), ex);
                return false;
            }
        }

        return true;
    }

    @Override
    public int getCacheCapcity() {
        return this.xbandIdCache.getCacheCapacity();
    }

    @Override
    public int getCacheSize() {
        return this.xbandIdCache.getCacheSize();
    }

    @Override
    public void clearCache() {
        this.xbandIdCache.clear();
    }

}
