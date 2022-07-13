using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;

namespace GFFSimulator
{
    public class Publisher
    {
        private Session session;
        private Sonic.Jms.Topic topic;
        private Sonic.Jms.MessageProducer publisher;

        public Publisher(Session session, string topicName)
        {
            try
            {
                this.session = session;
                this.topic = session.CreateTopic(topicName);
                this.publisher = session.CreateProducer(topic);
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                // Rethrow so so listenter won't attempt to start.
                throw;
            }
        }

        public void Publish(string sMessage)
        {
            try
            {
                Sonic.Jms.TextMessage msg = session.CreateTextMessage();
                msg.setText(sMessage);

                // here's where you can set properties on the message envelope, e.g.
                // msg.setStringProperty("xbrc_facility", "8001024");

                publisher.send(msg);
                session.Commit();
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                // rethrow
                throw ex;
            }
        }

    }
}
