using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Configuration;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Runtime.Serialization;
using System.Xml.Serialization;
using System.Xml;
using Sonic.Jms;

namespace Disney.xBand.xBMS.Simulator.JMS
{
    public abstract class Publisher : IDisposable
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private Sonic.Jms.Connection connection;
        protected Sonic.Jms.Session PublisherSession { get; private set; }
        private Sonic.Jms.Topic topic;
        protected Sonic.Jms.MessageProducer MessageProducer { get; private set; }

        private String topicName;

        protected string IdmsRootUrl { get; private set; }

        protected System.Timers.Timer PublishTimer { get; private set; }

        protected System.Timers.Timer ValidationTimer { get; private set; }

        protected MessageRepository MessageRepository { get; private set; }

        protected bool IsConnected { get; private set; }

        private SimulatorExceptionListener exceptionListener;

        public Publisher(string topicName, MessageRepository messageRepository)
        {
            this.topicName = topicName;
            this.MessageRepository = messageRepository;

            this.PublishTimer = new System.Timers.Timer(int.Parse(ConfigurationManager.AppSettings["MessageInterval"]));
            this.PublishTimer.Elapsed += new System.Timers.ElapsedEventHandler(PublishTimerElapsed);
            this.PublishTimer.AutoReset = true;

            this.ValidationTimer = new System.Timers.Timer(int.Parse(ConfigurationManager.AppSettings["MessageInterval"]));
            this.ValidationTimer.Elapsed += new System.Timers.ElapsedEventHandler(ValidationTimerElapsed);
            this.ValidationTimer.AutoReset = true;
            
            this.IsConnected = false;

            this.exceptionListener = new SimulatorExceptionListener(this);

            this.IdmsRootUrl = ConfigurationManager.AppSettings["IdmsRootUrl"];
        }

        public void Start()
        {
            if (!this.IsConnected)
            {
                try
                {
                    Sonic.Jms.Cf.Impl.ConnectionFactory factory = new Sonic.Jms.Cf.Impl.ConnectionFactory();

                    factory.setConnectionURLs(ConfigurationManager.AppSettings["ConnectionUrl"]);
                    factory.setInitialConnectTimeout(30);

                    this.connection = factory.createConnection(ConfigurationManager.AppSettings["ESB_Username"], ConfigurationManager.AppSettings["ESB_Password"]);
                    this.connection.start();

                    this.connection.setExceptionListener(this.exceptionListener);

                    this.PublisherSession = connection.createSession(false, Sonic.Jms.SessionMode.AUTO_ACKNOWLEDGE);

                    this.topic = PublisherSession.createTopic(this.topicName);
                    this.MessageProducer = PublisherSession.createProducer(topic);

                    this.PublishTimer.Start();

                    this.ValidationTimer.Start();

                    this.IsConnected = true;

                    log.InfoFormat("Connection to broker: {0} and topic {1} succesful", ConfigurationManager.AppSettings["ConnectionUrl"], this.topicName);
                }
                catch (Sonic.Jms.JMSException ex)
                {
                    log.Error("Error connecting to broker.", ex);

                    this.IsConnected = false;
                }
                catch (Exception ex)
                {
                    log.Error("Unexpected error.", ex);

                    this.IsConnected = false;
                }
            }
        }

        public void Stop()
        {
            this.IsConnected = false;

            if (this.MessageProducer != null)
            {
                try
                {
                    this.MessageProducer.close();
                }
                catch (Sonic.Jms.JMSException)
                {
                }
            }

            if (this.PublisherSession != null)
            {
                try
                {
                    this.PublisherSession.close();
                }
                catch (Sonic.Jms.JMSException)
                {
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (Sonic.Jms.JMSException)
                {
                }
            }

            this.PublishTimer.Stop();
            this.ValidationTimer.Stop();
        }

        protected abstract void PublishTimerElapsed(object sender, System.Timers.ElapsedEventArgs e);

        protected abstract void ValidationTimerElapsed(object sender, System.Timers.ElapsedEventArgs e);

        public void Dispose()
        {
            Stop();
        }
    }
}
