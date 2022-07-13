using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Xml;

namespace GFFSimulator
{
    class Sender
    {
        Session session;
        Publisher pubTap;
        Publisher pubOrder;
        Publisher pubTable;

        public Sender(CLOptions clo)
        {
            string sURL = string.Format("{0}", clo.Broker);
            session = new Session(sURL, clo.User, clo.Password, null);
            pubTap = new Publisher(session, "GFF.XI.POINTOFSALE");
            pubOrder = new Publisher(session, "GFF.XI.KICHENMANAGEMENT");
            pubTable = new Publisher(session, "GFF.XI.TABLEMANAGEMENT");
            session.Start();
        }

        public void Send(List<businessEvent> liEvents, CLOptions clo)
        {
            if (liEvents.Count == 0)
                return;

            // peg the start time
            DateTime dtStart = DateTime.Now;
            DateTime dtMsgStart = Simulator.ParseTime(liEvents[0].timestamp);

            Console.WriteLine("Writing " + liEvents.Count + " messages to JMS");
            foreach (businessEvent be in liEvents)
            {
                // do we need to wait?
                DateTime dtMsg = Simulator.ParseTime(be.timestamp);
                if (!clo.FastMode)
                {
                    double secMsgSpan = (dtMsg - dtMsgStart).TotalSeconds;
                    double secClockSpan = (DateTime.Now - dtStart).TotalSeconds;
                    if (secMsgSpan > secClockSpan)
                        System.Threading.Thread.Sleep(1000 * (int)(secMsgSpan - secClockSpan));
                }

                // serialize the message
                string s = Serialize(be);
                switch (be.location)
                {
                    case "GFF.XI.POINTOFSALE":
                        pubTap.Publish(s);
                        break;

                    case "GFF.XI.KITCHENMANAGEMENT":
                        pubOrder.Publish(s);
                        break;

                    case "GFF.XI.TABLEMANAGEMENT":
                        pubTable.Publish(s);
                        break;

                    default:
                        Console.WriteLine("Error: invalid message " + s);
                        break;
                }
                Console.Write("Message time: " + be.timestamp + "\r");
            }
            Console.WriteLine();
            Console.WriteLine("Done");
        }

        private string Serialize(businessEvent be)
        {
            XmlSerializer ser = new XmlSerializer(typeof(businessEvent));
            XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
            ns.Add("", "");
            StringWriter sw = new StringWriter();
            XmlWriter xw = XmlWriter.Create(sw, new XmlWriterSettings { OmitXmlDeclaration=true, Indent = true });
            ser.Serialize(xw, be, ns);
            return sw.ToString();
        }
    }
}
