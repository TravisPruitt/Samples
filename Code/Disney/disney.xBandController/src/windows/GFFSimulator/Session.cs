using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;
using Sonic.Jms;

namespace GFFSimulator
{
    public class Session
    {
        private Sonic.Jms.Cf.Impl.ConnectionFactory factory;
        private Sonic.Jms.Connection connection;
        private Sonic.Jms.Session publisherSession;

        public Session(string URL, string userName, string userPassword, string connectionId)
        {
            try
            {
                this.factory = new Sonic.Jms.Cf.Impl.ConnectionFactory(URL);

                this.factory.setConnectionURLs(URL);
                if (connectionId != null)
                    this.factory.setConnectID(connectionId);

                this.connection = factory.createConnection(userName, userPassword);

                this.publisherSession = connection.createSession(true, Sonic.Jms.SessionMode.AUTO_ACKNOWLEDGE);

            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                // Rethrow so so listenter won't attempt to start.
                throw;
            }
        }

        public void Start()
        {
            this.connection.start();
        }

        public Topic CreateTopic(string sTopic)
        {
            return publisherSession.createTopic(sTopic);
        }

        public MessageProducer CreateProducer(Topic topic)
        {
            return publisherSession.createProducer(topic);
        }

        public TextMessage CreateTextMessage()
        {
            return publisherSession.createTextMessage();
        }

        public void Commit()
        {
            publisherSession.commit();
        }
    }
}
