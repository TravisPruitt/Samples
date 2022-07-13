using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Net;

namespace FPT2LoadTestConsoleTest
{
    public class Session
    {
        private Thread th;
        private string sName;
        private AutoResetEvent halt = new AutoResetEvent(false);
        public double Duration { get; set; }

        private Random rand = new Random();

        public Session(string sName)
        {
            this.sName = sName;
            Duration = double.NaN;
        }

        public void Open()
        {
            th = new Thread(new ThreadStart(ThreadFunc));
            th.Start();
        }

        public void Close()
        {
            halt.Set();
            th.Join(1000);
        }

        private void ThreadFunc()
        {
            bool bQuit = false;
            try
            {
                while (!bQuit)
                {
                    // send the start message
                    HttpWebRequest req =
                      (HttpWebRequest)WebRequest.Create(string.Format("http://localhost:8008/log/{0}/start", sName));

                    req.Method = "GET";
                    req.KeepAlive = true;
                    req.Proxy = null;
                    req.Timeout = 1000;

                    DateTime dtStart = DateTime.Now;
                    WebResponse res = req.GetResponse();
                    Duration = (DateTime.Now - dtStart).TotalSeconds;

                    // calculate random split times
                    double s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18;
                    double total;
                    s0 = rand.NextDouble() * 2.0;
                    s1 = rand.NextDouble() * 2.0;
                    s2 = rand.NextDouble() * 2.0;
                    s3 = rand.NextDouble() * 2.0;
                    s4 = rand.NextDouble() * 2.0;
                    s5 = rand.NextDouble() * 2.0;
                    s6 = rand.NextDouble() * 2.0;
                    s7 = rand.NextDouble() * 2.0;
                    s8 = rand.NextDouble() * 2.0;
                    s9 = rand.NextDouble() * 2.0;
                    s10 = rand.NextDouble() * 2.0;
                    s11 = rand.NextDouble() * 2.0;
                    s12 = rand.NextDouble() * 2.0;
                    s13 = rand.NextDouble() * 2.0;
                    s14 = rand.NextDouble() * 2.0;
                    s15 = rand.NextDouble() * 2.0;
                    s16 = rand.NextDouble() * 2.0;
                    s17 = rand.NextDouble() * 2.0;
                    s18 = rand.NextDouble() * 2.0;
                    total = s0 + s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10 + s11 + s12 + s13 + s4 + s15 + s16 + s17 + s18;

                    // wait until the total time has expired (while polling the quit flag)
                    while (true)
                    {
                        if (halt.WaitOne(1000))
                        {
                            bQuit = true;
                            break;
                        }

                        if ((DateTime.Now - dtStart).TotalSeconds > total)
                            break;
                    }

                    req = (HttpWebRequest)WebRequest.Create(string.Format("http://localhost:8008/log/{20}/end?split0={0}&split1={1}&split2={2}&split3={3}&split4={4}&split5={5}&split6={6}&split7={7}&split8={8}&split9={9}&split10={10}&split11={11}&split12={12}&split13={13}&split14={14}&split15={15}&split16={16}&split17={17}&split18={18}&split19={19}",
                                                                          s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, total, sName));

                    req.Method = "GET";
                    req.KeepAlive = true;
                    req.Proxy = null;
                    req.Timeout = 1000;

                    res = req.GetResponse();
                }
            }
            catch (Exception )
            {
            }

        }
    }
}
