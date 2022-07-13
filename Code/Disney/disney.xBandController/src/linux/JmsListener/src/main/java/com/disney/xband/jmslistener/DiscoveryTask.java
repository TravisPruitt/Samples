package com.disney.xband.jmslistener;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import progress.message.jclient.ConnectionStateChangeListener;
import progress.message.jclient.Constants;
import progress.message.jclient.Connection;
import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.ExceptionFormatter;
import com.disney.xband.common.lib.health.DiscoveryInfo;
import com.disney.xband.jms.lib.connection.ConnectionManager;
import com.disney.xband.jmslistener.configuration.ConfigurationProperties;

public class DiscoveryTask extends Thread implements ExceptionListener, ConnectionStateChangeListener, IStoppable {
    private static Logger logger = Logger.getLogger(DiscoveryTask.class);

    private Connection connection = null;
    private Session session = null;
    private MessageProducer messageProducer = null;
    private Topic topic;
    private volatile boolean isStopped;
    private long repeatMs;

    public DiscoveryTask(long repeatMs) {
        this.repeatMs = repeatMs;
    }

    @Override
    public void shutdown() {
        this.isStopped = true;
    }

    private void createConnection(
            String connectionFactoryJndiName, String brokerDomain,
            String brokerUrl, String user, String password) {
        progress.message.jclient.ConnectionFactory factory = null;

        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sonicsw.jndi.mfcontext.MFContextFactory");
        env.put("com.sonicsw.jndi.mfcontext.domain", brokerDomain);
        env.put(Context.PROVIDER_URL, brokerUrl);
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            InitialContext context = new InitialContext(env);

            // create the connection factory
            factory = (progress.message.jclient.ConnectionFactory) context
                    .lookup(connectionFactoryJndiName);

            factory.setSocketConnectTimeout(5000);
        }
        catch (NamingException e) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Service: DiscoveryService Cannot connect to JMS Broker - " + brokerUrl
                            + " using Domain - " + brokerDomain
                            + " and User - " + user, e));
            this.connection = null;
        }

        ConnectionManager.closeConnection(connection);
        connection = null;

        if (factory == null) {
            return;
        }

        try {
            // now use the factory to create our connection
            connection = (progress.message.jclient.Connection) factory.createConnection(user, password);
        }
        catch (javax.jms.JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Cannot connect to JMS Broker - " + brokerUrl
                            + " using Domain - " + brokerDomain
                            + " and User - " + user, ex));

            connection = null;
            return;
        }

        this.connection.setConnectionStateChangeListener(this);
        this.connection.setPingInterval(ConfigurationProperties.INSTANCE.getPingInterval());

        try {
            this.connection.setExceptionListener(this);
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Service: DiscoveryService set exception listener: ", ex));

            this.connection = null;
            return;
        }

        try {
            // This session is not transacted, and it uses automatic message
            // acknowledgment
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Service: DiscoveryService could not create session: ", ex));

            this.connection = null;
            this.session = null;
            return;
        }

        try {
            // create a pub/sub on our topic
            this.topic = this.session
                    .createTopic(ConfigurationProperties.INSTANCE
                            .getDiscoveryJmsTopic());
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Service: DiscoveryService could not create topic for topic name "
                            + ConfigurationProperties.INSTANCE.getDiscoveryJmsTopic() + " : ", ex));

            this.connection = null;
            this.session = null;
            this.topic = null;
            return;
        }

        try {
            // create a publisher for that topic
            this.messageProducer = this.session.createProducer(topic);
        }
        catch (JMSException ex) {
            logger.error(ExceptionFormatter.formatMessage(
                    "Service: DiscoveryService could not create message producer for topic name "
                            + ConfigurationProperties.INSTANCE.getDiscoveryJmsTopic() + " : ", ex));

            this.connection = null;
            this.session = null;
            this.topic = null;
            this.messageProducer = null;
            return;
        }
    }

    @Override
    public void run() {
        if (!ConfigurationProperties.INSTANCE.getDiscoveryJmsTopic().startsWith("#")) {
            try {
                while (!isStopped) {
                    try {
                        if (this.connection == null) {
                            // now use the factory to create our connection
                            createConnection(
                                    ConfigurationProperties.INSTANCE.getConnectionFactoryJndiName(),
                                    ConfigurationProperties.INSTANCE
                                            .getDiscoveryJmsBrokerDomain(),
                                    ConfigurationProperties.INSTANCE
                                            .getDiscoveryJmsBrokerUrl(),
                                    ConfigurationProperties.INSTANCE.getDiscoveryJmsUser(),
                                    ConfigurationProperties.INSTANCE
                                            .getDiscoveryJmsPassword());

                            logger.info("Connected to topic: "
                                    + ConfigurationProperties.INSTANCE
                                    .getDiscoveryJmsTopic()
                                    + " on broker: "
                                    + ConfigurationProperties.INSTANCE
                                    .getDiscoveryJmsBrokerUrl());
                        }

                        if (this.connection != null) {
                            if (this.connection.getConnectionState() == Constants.ACTIVE) {
                                String ourIp = "";
                                try {
                                    Collection<String> iplist = getOwnIpAddress();
                                    if (iplist.size() > 0) {
                                        ourIp = iplist.iterator().next();
                                    }
                                }
                                catch (SocketException e) {
                                    logger.error(
                                            "Failed to get our own IP address in order to send out a discovery JMS message.",
                                            e);
                                }

                                DiscoveryInfo discoveryInfo = new DiscoveryInfo(ourIp,
                                        ConfigurationProperties.INSTANCE.getHttpPort(),
                                        getHostname(), "", "", "", "", // HA Status
                                        DiscoveryInfo.getSerialversionuid(), 10000,
                                        DateUtils.format(new Date().getTime()));

                                try {
                                    ObjectMapper om = new ObjectMapper();
                                    String text;
                                    text = om.writeValueAsString(discoveryInfo);
                                    javax.jms.TextMessage msg = this.session.createTextMessage();
                                    msg.setText(text);
                                    msg.setStringProperty("xbrc.type", "DISCOVERY");
                                    msg.setStringProperty("xbrc_type", "DISCOVERY");

                                    this.messageProducer.send(msg);

                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Sent JMS discovery message. " + text);
                                    }
                                }
                                catch (JMSException e) {
                                    logger.error("Failed to publish discovery message.", e);
                                }
                            }
                        }
                    }
                    catch (Exception ex) {
                        logger.error(ExceptionFormatter.format("Unexpected exception in Discovery Task", ex));
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
            logger.debug("JMS disabled. Not sending discovery messages.");
            return;
        }
    }

    public void Stop() {
        //No need to stop a producer.

        ConnectionManager.closeConnection(connection);

        this.connection = null;
        this.messageProducer = null;
        this.session = null;

        logger.info("DISCONNECTED from topic: "
                + ConfigurationProperties.INSTANCE.getDiscoveryJmsTopic()
                + " on broker: "
                + ConfigurationProperties.INSTANCE.getDiscoveryJmsBrokerUrl()
                + " using Domain - "
                + ConfigurationProperties.INSTANCE
                .getDiscoveryJmsBrokerDomain()
                + " and User - "
                + ConfigurationProperties.INSTANCE.getDiscoveryJmsUser());
    }

    @Override
    public void onException(JMSException ex) {
        Stop();
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

    private Collection<String> getOwnIpAddress() throws SocketException {
        LinkedList<String> result = new LinkedList<String>();

        // Build a list of all interfaces and sub-interfaces.
        LinkedList<NetworkInterface> allInterfaces = new LinkedList<NetworkInterface>();
        for (NetworkInterface iface : Collections.list(NetworkInterface
                .getNetworkInterfaces()))
        {
            allInterfaces.add(iface);
            for (NetworkInterface siface : Collections.list(iface
                    .getSubInterfaces()))
            {
                allInterfaces.add(siface);
            }
        }

        for (NetworkInterface iface : allInterfaces) {
            // loop through all the interfaces in each adapter
            Enumeration<InetAddress> raddrs = iface.getInetAddresses();
            for (InetAddress raddr : Collections.list(raddrs)) {
                // skip any loopback or ipv6 addresses
                if (raddr.isLoopbackAddress() || raddr instanceof Inet6Address) {
                    continue;
                }

                String sAddress = raddr.toString().substring(1);

                result.add(sAddress);
            }
        }

        return result;
    }

    private String getHostname() {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            return addr.getHostName();
        }
        catch (UnknownHostException e) {
            logger.warn("Failed to get hostname", e);
        }

        return "";
    }
}
