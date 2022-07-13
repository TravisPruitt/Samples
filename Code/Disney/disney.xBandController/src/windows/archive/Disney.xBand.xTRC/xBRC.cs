using System;
using System.Collections.Generic;
using System.Text;
using System.Net.NetworkInformation;
using System.IO;
using System.Xml.Serialization;
using log4net.Config;
using System.Net;
using System.Diagnostics;

namespace Disney.xBand.xTRC
{
    public class xBRC
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private const string UID_TAG = "\"shortRangeTag\":\"";

        private string macAddress;

        private xTRCConfiguration configuration;

        private int eventNumber;

        private bool sentHello;

        private int secondsSinceLastHello;

        private System.Timers.Timer helloTimer;

        private System.Timers.Timer eventTimer;

        private Metrics metrics;

        public xBRC() : this(null)
        {
        }

        //So I can test entry and merge from my test program.
        public xBRC(string configurationPath)
        {
            this.metrics = new Metrics("Send Tap Metrics");

            macAddress = String.Empty;
            this.eventNumber = 0;

            if(String.IsNullOrEmpty(configurationPath))
            {
                configurationPath = @"c:\xtrc\";
            }

            XmlConfigurator.Configure(new System.IO.FileInfo(Path.Combine(configurationPath,@"log4net.xml")));

            using (FileStream fs = new FileStream(Path.Combine(configurationPath, "xTRC-Configuration.xml"), FileMode.Open))
            {
                XmlSerializer xs = new XmlSerializer(typeof(xTRCConfiguration));
                this.configuration = (xTRCConfiguration)xs.Deserialize(fs);
            }

            if (!this.configuration.xbrcUrl.EndsWith("/"))
            {
                this.configuration.xbrcUrl += "/";
            }

            this.sentHello = false;

            this.helloTimer = new System.Timers.Timer(this.configuration.helloInterval);
            this.helloTimer.Elapsed += new System.Timers.ElapsedEventHandler(HelloTimerElapsed);
            this.helloTimer.AutoReset = true;
            this.helloTimer.Start();

            this.eventTimer = new System.Timers.Timer(this.configuration.eventInterval);
            this.eventTimer.Elapsed += new System.Timers.ElapsedEventHandler(EventTimerElapsed);
            this.eventTimer.AutoReset = true;
            this.eventTimer.Start();

            this.secondsSinceLastHello = 0;
        }

        private void HelloTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            try
            {
                if (secondsSinceLastHello >= 60)
                {
                    this.sentHello = false;
                    secondsSinceLastHello = 0;
                }
                else
                {
                    secondsSinceLastHello += (int)(Math.Round(this.helloTimer.Interval / 1000.0, MidpointRounding.AwayFromZero));
                }
            }
            catch (Exception ex)
            {
                log.Error("Unexpected error,", ex);
            }
        }

        private void EventTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            TapEvent eventMessage = null;
            try
            {
                // send a hello message if we haven't sent one before
                SendHello();

                while (EventMessageQueue.Instance.HasEvents)
                {
                    eventMessage = EventMessageQueue.Instance.Dequeue();
                    HttpChannel chan = new HttpChannel(this.configuration.xbrcUrl);

                    chan.put("stream", eventMessage.ToString());
                }
            }
            catch (Exception ex)
            {
                if (eventMessage != null)
                {
                    EventMessageQueue.Instance.Enqueue(eventMessage);
                }
                log.Error("Unexpected error,", ex);
            }
        }

        /// <summary>
        ///     Sends a tap message to the xBRC for the specified ID.
        /// </summary>
        /// <param name="secureid">The secure ID of a card or band.</param>
        /// <param name="secureid">The UID of a card or band, depending whether the xBRC is set to accept secure ID or Tap ID.</param>
        public void SendTap(string secureId, string uid)
        {
            log.Info(String.Format("Called SendTap with secureID {0} and uid {1}",secureId, uid));

            Stopwatch sw = new Stopwatch();
            sw.Start();
            try
            {
                if (String.IsNullOrEmpty(this.configuration.xbrcUrl))
                {
                    throw new ApplicationException("The xBRC location has not been included in the configuration file.");
                }

                if (this.configuration.locationId == -1)
                {
                    throw new ApplicationException("No location information has been provided in the configuration file.");
                }

                TapEvent tapEvent = new TapEvent();
                tapEvent.MacAddress = this.macAddress;
                tapEvent.EventNumber = this.eventNumber++;
                tapEvent.Time = DateTime.UtcNow;
                tapEvent.SecureID = secureId;
                tapEvent.UID = uid;

                EventMessageQueue.Instance.Enqueue(tapEvent);
                metrics.Update(sw);

                if (metrics.Count % 10 == 0)
                {
                    log.Info(metrics.ToString());
                }
            }
            catch (Exception ex)
            {
                log.Error("Unexpected error,", ex);
            }
        }

        private void SendHello()
        {
            lock (this)
            {
                if (!this.sentHello)
                {
                    try
                    {
                        HttpChannel chan = new HttpChannel(this.configuration.xbrcUrl);

                        string json = string.Format(@"{{
	                                        ""mac"" : ""{0}"",
	                                        ""port"" : 0,
	                                        ""next eno"" : {1},
	                                        ""reader name"" : ""{0}"",
	                                        ""reader type"" : ""Mobile Gxp"",
	                                        ""location id"" : {2}
                                        }}",
                                                    this.macAddress,
                                                    this.eventNumber,
                                                    this.configuration.locationId);


                        chan.put("hello", json);

                        this.sentHello = true;
                    }
                    catch (Exception ex)
                    {
                        log.Error("Unexpected error,", ex);
                    }
                }
            }
        }


        private void GetMacAddress()
        {
            IPGlobalProperties computerProperties = IPGlobalProperties.GetIPGlobalProperties();
            NetworkInterface[] nics = NetworkInterface.GetAllNetworkInterfaces();

            foreach (NetworkInterface adapter in nics)
            {
                // ignore loopback
                if (adapter.NetworkInterfaceType == NetworkInterfaceType.Loopback)
                    continue;

                // return first non loopback
                macAddress = adapter.GetPhysicalAddress().ToString();
            }
        }
    }
}
