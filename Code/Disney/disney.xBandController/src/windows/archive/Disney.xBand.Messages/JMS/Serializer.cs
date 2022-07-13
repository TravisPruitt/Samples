using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.Xml;
using System.IO;

namespace Disney.xBand.Messages.JMS
{
    public class Serializer
    {
        public static SimpleMessage DeserializeSimple(string message)
        {
            SimpleMessage result = null;

            XmlSerializer serializer = new XmlSerializer(typeof(SimpleMessage));

            using (XmlReader reader = XmlReader.Create(new StringReader(message)))
            {
                result = (SimpleMessage)serializer.Deserialize(reader);
            }

            return result;

        }

        public static ExitMessage DeserializeExit(string message)
        {
            ExitMessage result = null;

            XmlSerializer serializer = new XmlSerializer(typeof(ExitMessage));

            using (XmlReader reader = XmlReader.Create(new StringReader(message)))
            {
                result = (ExitMessage)serializer.Deserialize(reader);
            }

            return result;

        }

        public static LoadMessage DeserializeLoad(string message)
        {
            LoadMessage result = null;

            XmlSerializer serializer = new XmlSerializer(typeof(LoadMessage));

            using (XmlReader reader = XmlReader.Create(new StringReader(message)))
            {
                result = (LoadMessage)serializer.Deserialize(reader);
            }

            return result;

        }

        public static AbandonMessage DeserializeAbandon(string message)
        {
            AbandonMessage result = null;

            XmlSerializer serializer = new XmlSerializer(typeof(AbandonMessage));

            using (XmlReader reader = XmlReader.Create(new StringReader(message)))
            {
                result = (AbandonMessage)serializer.Deserialize(reader);
            }

            return result;

        }
        
        public static MetricsMessage DeserializeMetrics(string message)
        {
            MetricsMessage result = null;

            XmlSerializer serializer = new XmlSerializer(typeof(MetricsMessage));

            using (XmlReader reader = XmlReader.Create(new StringReader(message)))
            {
                result = (MetricsMessage)serializer.Deserialize(reader);
            }

            return result;

        }
    }
}
