using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using Disney.xBand.Messages.JMS;
using Disney.xBand.Messages;
using System.Net;
using System.IO;
using System.Configuration;

namespace Disney.xBand.TestHarness
{
    class Program
    {
         private static System.Diagnostics.EventLog eventLog;

        static void Main(string[] args)
        {
            if (!System.Diagnostics.EventLog.SourceExists("xBRMS Message Service"))
            {
                System.Diagnostics.EventLog.CreateEventSource("xBRMS Message Service", "Application");
            }

            eventLog = new System.Diagnostics.EventLog();
            eventLog.Log = "Application";
            eventLog.Source = "xBRMS Message Service";

            string topicsSetting = ConfigurationManager.AppSettings["ESB_Topics"];

            string[] topics = topicsSetting.Split(';');

            List<Listener> listeners = new List<Listener>();

            foreach (string topic in topics)
            {
                string[] topicInfo = topic.Split(',');

                Listener listener = new Listener(eventLog,topicInfo[0],topicInfo[1]);

                listener.Start();

                listeners.Add(listener);
 
            }
        
            Console.WriteLine("Press any key to end application");

            while (true)
            {
                Thread.Sleep(100);
            }
        }

    }
}
