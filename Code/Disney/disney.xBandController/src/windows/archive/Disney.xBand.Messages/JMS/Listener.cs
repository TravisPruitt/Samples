using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Collections;
using System.Xml.Serialization;
using EM.SonicMQ.Messages;
using System.IO;
using System.Configuration;
using System.Diagnostics;
using System.Collections.Concurrent;

namespace Disney.xBand.Messages.JMS
{
    public class Listener : Sonic.Jms.MessageListener, Sonic.Jms.ExceptionListener
    {
        private Sonic.Jms.Cf.Impl.ConnectionFactory factory;
        private Sonic.Jms.Connection connection;
        private Sonic.Jms.Session subscriberSession;
        private Sonic.Jms.Topic topic;
        private Sonic.Jms.MessageConsumer subscriber;
        private EventLog eventLog;
        private long processingMilliseconds;
        private long messagesProcessed;
        private DateTime startTime;

        // Construct a ConcurrentQueue.
        ConcurrentQueue<int> cq;

        public Listener()
        {
            cq = new ConcurrentQueue<int>();
        }

        public Listener(EventLog eventLog, string topicName, string connectID)
        {
            if (eventLog == null)
            {
                throw new ArgumentException("eventlog cannot be NULL", "eventLog");
            }
            this.eventLog = eventLog;

            try
            {
                this.factory = new Sonic.Jms.Cf.Impl.ConnectionFactory();

                this.factory.setConnectionURLs(ConfigurationManager.AppSettings["ConnectionUrl"]);
                this.factory.setConnectID(connectID);

                this.connection = factory.createConnection(ConfigurationManager.AppSettings["ESB_Username"], ConfigurationManager.AppSettings["ESB_Password"]);

                this.subscriberSession = connection.createSession(false, Sonic.Jms.SessionMode.AUTO_ACKNOWLEDGE);

                this.topic = subscriberSession.createTopic(topicName);
                this.subscriber = subscriberSession.createConsumer(topic);

                this.subscriber.setMessageListener(this);
            }
            catch (Sonic.Jms.JMSException ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                this.eventLog.WriteEntry(String.Format("Message: {0}{2}Stack Trace: {1}", ex.Message, ex.StackTrace, Environment.NewLine), EventLogEntryType.Error);

                //Rethrow so so listenter won't attempt to start.
                throw;
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                this.eventLog.WriteEntry(String.Format("Message: {0}{2}Stack Trace: {1}", ex.Message, ex.StackTrace, Environment.NewLine), EventLogEntryType.Error);

                //Rethrow so so listenter won't attempt to start.
                throw;
            }

        }

        public void Start()
        {
            this.startTime = DateTime.Now;
            connection.start();
        }

        public void Stop()
        {
            connection.stop();
        }

        public virtual void onMessage(Sonic.Jms.Message message)
        {
            try
            {
                if (message is Sonic.Client.Jms.Impl.TextMessage)
                {
                    Stopwatch sw = new Stopwatch();
                    sw.Start();

                    Sonic.Client.Jms.Impl.TextMessage xmlMessage = message as Sonic.Client.Jms.Impl.TextMessage;
                    string facility = String.Empty;
                    string messageType = String.Empty;
                    string facilityTypeName = String.Empty;

                    Hashtable properties = xmlMessage.getProperties();

                    if (xmlMessage.propertyExists("xbrc_facility"))
                    {
                        facility = properties["xbrc_facility"].ToString();
                    }

                    if (xmlMessage.propertyExists("xbrc_message_type"))
                    {
                        messageType = properties["xbrc_message_type"].ToString().ToUpper();
                    }

                    if (xmlMessage.propertyExists("xbrc_facility_type"))
                    {
                        facilityTypeName = properties["xbrc_facility_type"].ToString().ToUpper();
                    }

                    switch (messageType)
                    {
                        case "ENTRY":
                        case "INQUEUE":
                        case "MERGE":
                            {
                                SimpleMessage simpleMessage = Serializer.DeserializeSimple(xmlMessage.getText());
                                simpleMessage.FacilityTypeName = facilityTypeName;
                                simpleMessage.Save();
                                UpdateStatistics(sw.ElapsedMilliseconds);
                                break;
                            }
                        case "LOAD":
                            {
                                LoadMessage loadMessage = Serializer.DeserializeLoad(xmlMessage.getText());
                                loadMessage.FacilityTypeName = facilityTypeName;
                                loadMessage.Save();
                                UpdateStatistics(sw.ElapsedMilliseconds);
                                break;
                            }
                        case "EXIT":
                            {
                                ExitMessage exitMessage = Serializer.DeserializeExit(xmlMessage.getText());
                                exitMessage.FacilityTypeName = facilityTypeName;
                                exitMessage.Save();
                                UpdateStatistics(sw.ElapsedMilliseconds);
                                break;
                            }
                        case "ABANDON":
                            {
                                AbandonMessage abandonMessage = Serializer.DeserializeAbandon(xmlMessage.getText());
                                abandonMessage.FacilityTypeName = facilityTypeName;
                                abandonMessage.Save();
                                UpdateStatistics(sw.ElapsedMilliseconds);
                                break;
                            }
                        case "METRICS":
                            {
                                MetricsMessage metricsMessage = Serializer.DeserializeMetrics(xmlMessage.getText());
                                metricsMessage.FacilityTypeName = facilityTypeName;
                                metricsMessage.Save();
                                UpdateStatistics(sw.ElapsedMilliseconds);
                                break;
                            }

                        case "DISCOVERY":
                            {
                                //Ignore this message type.
                                break;
                            }

                        default:
                            {
                                string eventLogMessage =  String.Format("Message Type not found: {0}", messageType);
                                Debug.WriteLine(eventLogMessage);
                                this.eventLog.WriteEntry(eventLogMessage, EventLogEntryType.Warning);
                                break;
                            }
                    }
                }

            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);
                
                this.eventLog.WriteEntry(String.Format("Message: {0}{2}Stack Trace: {1}",ex.Message,ex.StackTrace,Environment.NewLine), EventLogEntryType.Error);

            }
        }


        public void onException(Sonic.Jms.JMSException exception)
        {
            // See if the error is a dropped connection. If so, try to reconnect.
            // NOTE: the test is against SonicMQ error codes.
            int dropCode = Sonic.Jms.Ext.ErrorCodes.ERR_CONNECTION_DROPPED;

            if (Sonic.Jms.Ext.ErrorCodes.testException(exception, dropCode))
            {
            }
            
            this.eventLog.WriteEntry(String.Format("Message: {0}{2}Stack Trace: {1}", exception.Message, exception.StackTrace, Environment.NewLine), EventLogEntryType.Error);
        }

        private void UpdateStatistics(long elapsedMilliseconds)
        {
            this.messagesProcessed++;
            this.processingMilliseconds += elapsedMilliseconds;

            if (messagesProcessed % 1000 == 0)
            {
                TimeSpan elapsedTime = DateTime.Now.Subtract(this.startTime);
                this.eventLog.WriteEntry(String.Format("Messages Processed: {0} in {1} milliseconds. Elapsed Time: {2}", this.messagesProcessed, this.processingMilliseconds, elapsedTime));
            }
        }
    }
}
