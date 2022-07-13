using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Net.NetworkInformation;
using System.Configuration;
using System.IO;
using System.Reflection;
using Microsoft.Win32;

namespace xTRC
{
    [Guid("7DCC1ECA-ACA7-4268-8340-AEE5FBAC4E70"),
         ClassInterface(ClassInterfaceType.None),
      ComSourceInterfaces(typeof(ITapEvents))]
    public class xBRC : ITapInterface
    {
#if IMPL
        private const string UID_TAG = "\"shortRangeTag\":\"";

        private string macAddress;

        private int eventNumber;

        private bool sentHello;

        private int secondsSinceLastHello;

        private System.Timers.Timer helloTimer;

        private System.Timers.Timer eventTimer;

        private String xbrcUrl;

        private int locationId;
#endif

        public xBRC()
        {
            // Write output to the event log.
            Trace.WriteLine("xBRC class initialize.");

#if IMPL
            try
            {

                var key = Registry.LocalMachine.OpenSubKey(@"SOFTWARE\Disney\xTRC", true);
                int helloInterval = 1000;
                int eventInterval = 1000;

                if (key.GetValue("HelloInterval") != null)
                {
                    helloInterval = int.Parse(key.GetValue("HelloInterval").ToString());
                }

                if (key.GetValue("EventInterval") != null)
                {
                    eventInterval = int.Parse(key.GetValue("EventInterval").ToString());
                }

                Trace.TraceInformation("Hello Interval: ", helloInterval);

                macAddress = String.Empty;
                this.eventNumber = 0;

                this.sentHello = false;

                this.helloTimer = new System.Timers.Timer(helloInterval);
                this.helloTimer.Elapsed += new System.Timers.ElapsedEventHandler(HelloTimerElapsed);
                this.helloTimer.AutoReset = true;
                this.helloTimer.Start();

                this.eventTimer = new System.Timers.Timer(eventInterval);
                this.eventTimer.Elapsed += new System.Timers.ElapsedEventHandler(EventTimerElapsed);
                this.eventTimer.AutoReset = true;
                this.eventTimer.Start();

                this.secondsSinceLastHello = 0;
            }
            catch (Exception ex)
            {
                Trace.TraceError("Unexpected error,", ex);
            }
#endif
        }

#if IMPL
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
                Trace.TraceError("Unexpected error,", ex);
            }
        }

        private void EventTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            TapMessage eventMessage = null;
            try
            {
                //send a hello message if we haven't sent one before
                SendHello();

                while (EventMessageQueue.Instance.HasEvents)
                {
                    eventMessage = EventMessageQueue.Instance.Dequeue();
                    HttpChannel chan = new HttpChannel(this.xbrcUrl);

                    chan.put("stream", eventMessage.ToString());
                }
            }
            catch (Exception ex)
            {
                if (eventMessage != null)
                {
                    EventMessageQueue.Instance.Enqueue(eventMessage);
                }
                Trace.TraceError("Unexpected error,", ex);
            }
        }
#endif

        public void SendTap(string secureId, string uid)
        {
            Trace.TraceInformation("Called SendTap with secureID {0} and uid {1}", secureId, uid);

#if IMPL
            Stopwatch sw = new Stopwatch();
            sw.Start();
            try
            {
                if (String.IsNullOrEmpty(this.xbrcUrl))
                {
                    throw new ApplicationException("The xBRC location has not been included in the configuration file.");
                }

                if (this.locationId == 0)
                {
                    throw new ApplicationException("No location information has been provided in the configuration file.");
                }

                TapMessage tapMessage = new TapMessage();
                tapMessage.MacAddress = this.macAddress;
                tapMessage.EventNumber = this.eventNumber++;
                tapMessage.Time = DateTime.UtcNow;
                tapMessage.SecureID = secureId;
                tapMessage.UID = uid;

                EventMessageQueue.Instance.Enqueue(tapMessage);
            }
            catch (Exception ex)
            {
                Trace.TraceError("Unexpected error,", ex);
            }
#endif
 }
#if IMPL

        private void SendHello()
        {
            lock (this)
            {
                if (!this.sentHello)
                {
                    try
                    {
                        HttpChannel chan = new HttpChannel(ConfigurationManager.AppSettings["xbrcUrl"]);

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
                                                    Convert.ToInt32(ConfigurationManager.AppSettings["locationId"]));


                        chan.put("hello", json);

                        this.sentHello = true;
                    }
                    catch (Exception ex)
                    {
                        Trace.TraceError("Unexpected error,", ex);
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
#endif
    }
 }
