package com.disney.xband.jmslistener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import progress.message.jclient.ConnectionStateChangeListener;
import progress.message.jclient.Constants;
import progress.message.jclient.Connection;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.jms.lib.connection.ConnectionManager;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;
import com.disney.xband.jmslistener.configuration.JmsServiceConfiguration;

/**
 * Base class to be used for services processing messages received from the
 * JMS Broker.
 */
public abstract class JmsService implements ExceptionListener, ConnectionStateChangeListener {
    public static long DEFAULT_NO_TRAFFIC_TOLERANCE_SECS = 600; // 10 minutes

    private static Logger logger = Logger.getLogger(JmsService.class);
    private Connection connection;
    private ListenerState state;
    private JmsServiceConfiguration configuration;
    private String serviceName;
    private String hostName;

    protected List<Listener> listeners = new ArrayList<Listener>();
    protected List<IStoppable> stoppableItems = new ArrayList<IStoppable>(128);
    protected long noTrafficToleranceSecs = DEFAULT_NO_TRAFFIC_TOLERANCE_SECS * 1000;

    /**
     * Initializes a new instance of the JmsService.
     *
     * @param configuration configuration to use for connecting to the broker.
     * @param serviceName   name of the service to be displayed when logging messages.
     */
    public JmsService(JmsServiceConfiguration configuration, String serviceName) {
        this.state = ListenerState.NotStarted;
        this.configuration = configuration;
        this.serviceName = serviceName;

        try {
            //Set the client id to the be the server host name. This client ID needs
            //to be unique across shared subscriptions.
            InetAddress addr = InetAddress.getLocalHost();
            this.hostName = addr.getHostName();
        }
        catch (UnknownHostException e) {
            logger.error(ExceptionFormatter.format("Can't retrieve hostname to use as client id.", e));
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets the connection pool used to persist data from the JMS messages to the database.
     */
    public abstract ConnectionPool getConnectionPool();

    /**
     * Gets the service configuration.
     */
    public JmsServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    public Session createSession(String connectionId) throws JMSException {
        if(this.connection == null) {
            try {
                this.connection = createConnection(
                    ConfigurationProperties.INSTANCE.getConnectionFactoryJndiName(),
                    this.configuration.getJmsBrokerDomain(),
                    this.configuration.getJmsBrokerUrl(),
                    this.configuration.getJmsUser(),
                    this.configuration.getJmsPassword(),
                    connectionId);

                if(logger.isDebugEnabled()) {
                    logger.debug(this.serviceName + " successfully opened connection to " + this.configuration.getJmsBrokerUrl());
                }
            }
            catch (JMSException ex) {
                logger.error(ExceptionFormatter.formatMessage("Service: " + this.serviceName + " failed to create connection: ", ex));
                this.state = ListenerState.NotStarted;
                throw ex;
            }
        }

        Session session = null;

        try {
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            if(logger.isDebugEnabled()) {
                logger.debug(this.serviceName + " successfully started a new session with " + this.configuration.getJmsBrokerUrl());
            }
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage("Service: " + this.serviceName + " failed to create session: ", ex));
            this.state = ListenerState.NotStarted;
            throw ex;
        }

        return session;
    }

    public abstract int getCacheCapcity();

    public abstract int getCacheSize();

    public abstract void clearCache();

    public void Restart() {
        if (this.state != ListenerState.Running) {
            Start();
        }
        else {
            if (this.listeners.size() > 0) {
                boolean isStalled = true;

                for (Listener listener : this.listeners) {
                    if (System.currentTimeMillis() - listener.getLastMessageReceivedTime() < noTrafficToleranceSecs) {
                        isStalled = false;
                        break;
                    }
                }

                if (isStalled) {
                    for (Listener listener : this.listeners) {
                        StatusInfo.INSTANCE.incHealthItemCounters(listener);
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info(
                                "Service " + this.serviceName + " has not received any messages on some of its topics for " +
                                        (noTrafficToleranceSecs / 1000) + " seconds. Restarting the service ..."
                        );
                    }

                    this.Stop();
                }
            }
        }
    }

    public synchronized void Start() {
        if (this.state != ListenerState.Running) {

            try {
                if (this.ConnectionNeeded()) {
                    if(!OpenSubscriptions()) {
                        logger.error(this.serviceName + " failed to start. Restarting in 5 seconds.");
                        doStop();

                        try {
                            Thread.sleep(5000);
                        }
                        catch(InterruptedException ignore) {
                        }
                    }
                    else {
                        this.state = ListenerState.Running;
                    }
                }
                else {
                    logger.info("Service: " + this.serviceName + " no connections required.");
                }
            }
            catch (Exception e) {
                logger.error(this.serviceName + " failed to start: ", e);
                doStop();

                try {
                    Thread.sleep(5000);
                }
                catch(InterruptedException ignore) {
                }
            }
        }
    }

    /* Close the JMS connection */
    public synchronized void Stop() {
        if (this.state != ListenerState.Stopped) {
            doStop();
        }
    }

    public void onException(JMSException ex) {
        logger.error("Service " + this.serviceName + " got JMS exception and will be stopped: ", ex);
        Stop();
    }

    @Override
    public void connectionStateChanged(int state) {
        if (state == Constants.ACTIVE) {
            if (logger.isInfoEnabled()) {
                logger.info("Service " + this.serviceName + ": connection state changed to ACTIVE.");
            }
        }
        else {
            if (state == Constants.RECONNECTING) {
                if (logger.isInfoEnabled()) {
                    logger.info("Service " + this.serviceName + ": connection state changed to RECONNECTING.");
                }
            }
            else {
                if (state == Constants.FAILED) {
                    logger.error("Service " + this.serviceName + ": connection state changed to FAILED.");
                }
                else {
                    if (state == Constants.CLOSED) {
                        logger.warn("Service " + this.serviceName + ": connection state changed to CLOSED.");
                    }
                }
            }
        }
    }

    public abstract boolean OpenSubscriptions();

    protected abstract boolean ConnectionNeeded();


    private progress.message.jclient.Connection createConnection(
            String connectionFactoryJndiName, String brokerDomain,
            String brokerUrl, String user, String password, String connectionId) throws JMSException {

        progress.message.jclient.ConnectionFactory factory = null;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sonicsw.jndi.mfcontext.MFContextFactory");
        env.put("com.sonicsw.jndi.mfcontext.domain", brokerDomain);
        env.put(Context.PROVIDER_URL, brokerUrl);
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            InitialContext context = new InitialContext(env);
            // create the connection factory
            factory = (progress.message.jclient.ConnectionFactory) context.lookup(connectionFactoryJndiName);
            factory.setSocketConnectTimeout(ConfigurationProperties.INSTANCE.getSocketTimeout());
            final String id = (this.hostName + "-jmslistener-" + connectionId).replace(".", "_");
            ((progress.message.jclient.ConnectionFactory)factory).setConnectID(id);
            //System.out.println(id);
        }
        catch (NamingException e) {
            logger.error(ExceptionFormatter.formatMessage(
                    serviceName + " Cannot connect to JMS Broker - " + brokerUrl +
                    " using Domain - " + brokerDomain + " and User - " + user, e));

            this.state = ListenerState.NotStarted;
            throw new JMSException(e.getMessage());
        }

        progress.message.jclient.Connection connection = null;

        try {
            // now use the factory to create our connection
            connection = (progress.message.jclient.Connection) factory.createConnection(user, password);
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Cannot connect to JMS Broker - " + brokerUrl +
                    " using Domain - " + brokerDomain + " and User - " + user, ex));

            this.state = ListenerState.NotStarted;
            throw new JMSException(ex.getMessage());
        }

        connection.setConnectionStateChangeListener(this);
        connection.setPingInterval(ConfigurationProperties.INSTANCE.getPingInterval());

        try {
            //Set the client id to the be the server host name. This client ID needs
            //to be unique across shared subscriptions.
            connection.setClientID(this.hostName);
            connection.setExceptionListener(this);
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage("Service: " + this.serviceName + " set exception listener: ", ex));

            this.state = ListenerState.NotStarted;
            throw new JMSException(ex.getMessage());
        }

        try {
            connection.start();
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage("Service: " + this.serviceName + " could not start connection: ", ex));

            this.state = ListenerState.NotStarted;
            throw new JMSException(ex.getMessage());
        }

        if(logger.isInfoEnabled()) {
            logger.info("Service " + this.serviceName + " opened a new connection to " + this.configuration.getJmsBrokerUrl());
        }

        return connection;
    }

    private void closeConnections() {
        ConnectionManager.closeConnection(connection);

        // We do not want to get restarted too fast.
        try {
            Thread.sleep(10000);
        }
        catch(InterruptedException ignore) {
        }

        if(this.stoppableItems != null) {
            for(IStoppable consumer : this.stoppableItems) {
                try {
                    consumer.shutdown();
                }
                catch (Exception e) {
                    logger.error("Failed while trying to shutdown a consumer: ", e);
                }
            }
        }

        this.connection = null;
    }

    /* Close the JMS connection */
    private void doStop() {
        this.listeners.clear();
        this.closeConnections();
        this.stoppableItems.clear();
        this.state = ListenerState.Stopped;

        if (logger.isInfoEnabled()) {
            logger.info("Service: " + this.serviceName +
                    " closed connection to broker: " + this.configuration.getJmsBrokerUrl());
        }
    }
}
