using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Xml.Serialization;
using System.IO;
using System.Xml;
using System.Globalization;

namespace xBRCMessageUtil
{
    public class Listener
    {
        public enum HistogramGrouping
        {
            Weekly,
            Daily,
            Hourly
        }

        private const int msecProcessPeriod = 1000;

        public interface Callback
        {
            string Get(string sPath);
            void LogError(string sError);
        }

        private class WorkerArgs
        {
            public string sVenue;
            public DateTime dtSOD;
            public DateTime dtStart;
            public DateTime dtEnd;
            public double dTimeFactor;

            public WorkerArgs(string sVenue, DateTime dtSOD, DateTime dtStart, DateTime dtEnd, double dTimeFactor)
            {
                this.sVenue = sVenue;
                this.dtSOD = dtSOD;
                this.dtStart = dtStart;
                this.dtEnd = dtEnd;
                this.dTimeFactor = dTimeFactor;
            }
        }

        private Callback callback = null;
        private Thread threadMessageProcessor = null;
        private Thread threadDataFetcher = null;
        private AutoResetEvent[] areDataFetcher = new AutoResetEvent[] { new AutoResetEvent(false), new AutoResetEvent(false), new AutoResetEvent(false) };
        private bool bStopWorkers = false;
        private double dTimeFactor = 1.0;

        private List<GuestAction> liInitial = new List<GuestAction>();
        private List<GuestAction> liReceived = new List<GuestAction>();
        private List<GuestAction> liActions = new List<GuestAction>();

        // to communicate with the data fetcher
        private string sDataFetcherUrl = null;
        private string sDataFetcherData = null;

        public Listener(Callback cb)
        {
            this.callback = cb;
        }

        public void Start(string sVenue, DateTime dtSOD, DateTime dtStart, DateTime dtEnd, double dTimeFactor)
        {
            if (dtSOD == null || dtStart == DateTime.MinValue || dtEnd == DateTime.MinValue)
                throw new ApplicationException("Must specify 'start of day', start and end time");

            WorkerArgs wa = new WorkerArgs(sVenue, dtSOD, dtStart, dtEnd, dTimeFactor);
            StartMessageProcessorWorker(wa);
            StartMessageFetcher();
        }

        public void Start(string sVenue)
        {
            StartMessageProcessorWorker(sVenue);
        }

        public void Terminate()
        {
            // stop the threads
            bStopWorkers = true;
            if (threadMessageProcessor != null)
            {
                if (threadMessageProcessor.IsAlive)
                {
                    if (!threadMessageProcessor.Join(msecProcessPeriod * 2))
                        threadMessageProcessor.Abort();
                }
                threadMessageProcessor = null;
            }
            if (threadDataFetcher!= null)
            {
                if (threadDataFetcher.IsAlive)
                {
                    if (!threadDataFetcher.Join(msecProcessPeriod * 2))
                        threadDataFetcher.Abort();
                }
                threadDataFetcher = null;
            }
        }

        public GuestAction[] GetInitialState()
        {
            if (threadMessageProcessor == null)
            {
                callback.LogError("Must call Start() before calling GetInitialState");
                return null;
            }

            // see if "done" has shown up
            lock (liActions)
            {
                if (liInitial.Count > 0 && liInitial[liInitial.Count - 1].Action == GuestAction.ActionType.Done)
                {
                    GuestAction[] ag = liInitial.ToArray();
                    liInitial.Clear();
                    return ag;
                }
                else
                    return null;
            }

        }

        public GuestAction[] GetGuestActions()
        {
            if (threadMessageProcessor == null)
            {
                callback.LogError("Must call Start() before calling GetGuestActions");
                return null;
            }

            lock (liActions)
            {
                GuestAction[] ag = liActions.ToArray();
                liActions.Clear();
                return ag;
            }
        }

        public double TimeFactor
        {
            get
            {
                return dTimeFactor;
            }
            set
            {
                dTimeFactor = value;
            }
        }

        public MessageRange GetMessageRange(string sVenue)
        {
            string sURL = string.Format("/xi/dashboard/jms/venue/{0}/messagerange", sVenue);
            string sXML = callback.Get(sURL);
            XmlSerializer xs = new XmlSerializer(typeof(MessageRange));
            StringReader sr = new StringReader(sXML);
            MessageRange mr = xs.Deserialize(sr) as MessageRange;
            return mr;
        }

        public MessageHistogram GetMessageHistogram(string sVenue, HistogramGrouping grouping)
        {
            return GetMessageHistogram(sVenue, DateTime.MinValue, DateTime.MaxValue, grouping);
        }

        public MessageHistogram GetMessageHistogram(string sVenue, DateTime dtStart, HistogramGrouping grouping)
        {
            return GetMessageHistogram(sVenue, dtStart, DateTime.MaxValue, grouping);
        }

        public MessageHistogram GetMessageHistogram(string sVenue, DateTime dtStart, DateTime dtEnd, HistogramGrouping grouping)
        {
            string sURL = string.Format("/xi/dashboard/jms/venue/{0}/messagehistogram?groupby={1}", sVenue, Enum.GetName(typeof(HistogramGrouping), grouping));
            if (dtStart != DateTime.MinValue)
            {
                sURL += "&from=" + formatDate(dtStart);
                if (dtEnd != DateTime.MaxValue)
                    sURL += "&to=" + formatDate(dtEnd);
            }
            string sXML = callback.Get(sURL);
            XmlSerializer xs = new XmlSerializer(typeof(MessageHistogram));
            StringReader sr = new StringReader(sXML);
            MessageHistogram mh = xs.Deserialize(sr) as MessageHistogram;
            return mh;
        }

        /*
         * Thread functions
         */

        private void StartMessageFetcher()
        {
            if (threadDataFetcher == null || (threadDataFetcher!=null && !threadDataFetcher.IsAlive) )
            {
                threadDataFetcher = new Thread(new ThreadStart(DataFetcherWorkerThread));
                threadDataFetcher.Start();
            }
        }

        private void StartMessageProcessorWorker(WorkerArgs wa)
        {
            if (threadMessageProcessor == null || (threadMessageProcessor!=null && !threadMessageProcessor.IsAlive) )
            {
                threadMessageProcessor = new Thread(new ParameterizedThreadStart(MessageProcessorWorkerThread));
                threadMessageProcessor.Start(wa);
            }
        }

        private void StartMessageProcessorWorker(string sVenue)
        {
            if (threadMessageProcessor == null || (threadMessageProcessor != null && !threadMessageProcessor.IsAlive))
            {
                threadMessageProcessor = new Thread(new ParameterizedThreadStart(MessageProcessorWorkerThread));
                threadMessageProcessor.Start(sVenue);
            }
        }

        private void StartDataFetch(string sURL)
        {
            sDataFetcherUrl = sURL;
            SignalDataFetch();
            WaitForGotUrl();
        }

        /*
         * Semaphore functions for data fetcher thread
         */

        private string WaitForData()
        {
            WaitForGotData();
            return sDataFetcherData;
        }

        private void SignalDataFetch()
        {
            areDataFetcher[0].Set();
        }

        private bool WaitForDataFetchSignal()
        {
            return areDataFetcher[0].WaitOne();
        }

        private void SignalGotUrl()
        {
            areDataFetcher[1].Set();
        }

        private bool WaitForGotUrl()
        {
            return areDataFetcher[1].WaitOne();
        }

        private void SignalGotData()
        {
            areDataFetcher[2].Set();
        }

        private bool WaitForGotData()
        {
            return areDataFetcher[2].WaitOne();
        }

        /*
         * Data fetcher thread
         */ 
        private void DataFetcherWorkerThread()
        {
            while (true)
            {
                // wait for signal
                if (!WaitForDataFetchSignal())
                    return;

                // are we being told to quit?
                if (bStopWorkers)
                    return;

                // grab the url
                string sURL = sDataFetcherUrl;
                SignalGotUrl();

                // fetch the data
                try
                {
                    sDataFetcherData = callback.Get(sURL);
                }
                catch (Exception)
                {
                    sDataFetcherData = null;
                }

                // set signal
                SignalGotData();
            }
        }

        /*
         * Message processor thread
         */

        private void MessageProcessorWorkerThread(object args)
        {
            WorkerArgs wa = args as WorkerArgs;
            DateTime dtBegin = DateTime.Now;

            // iterate from start to end time polling data periodically
            // if wa is null, we're polling in real-time and want to poll frequently, for small amounts of data, otherwise, we want to
            // poll less frequently, for bigger chunks

            string sVenue;
            DateTime dtStart;
            DateTime dtSOD;
            DateTime dtEnd;
            int cmsecPeriod;
            bool bRealTime;
            bool bEnterRealTime = false;

            // If worker args specified, use those
            if (wa != null && wa.dtStart < DateTime.UtcNow)
            {
                sVenue = wa.sVenue;
                dtSOD = wa.dtSOD;
                dtStart = wa.dtStart;
                dtEnd = wa.dtEnd;
                cmsecPeriod = 3600 * 1000;      // poll data an hour at a time
                dTimeFactor = wa.dTimeFactor;
                bRealTime = false;
            }
            else
            {
                // else, start at "Now" and use reasonable defaults
                sVenue = args as string;
                dtStart = DateTime.Now;

                // set the start of day to about 6am EDT (10am UTC)
                dtSOD = new DateTime(dtStart.Year, dtStart.Month, dtStart.Day, 10, 0, 0);

                // set the end of day to SOD + 18 hours
                dtEnd = dtSOD + TimeSpan.FromHours(18.0);

                // poll frequently and in real time
                cmsecPeriod = 1000;
                dTimeFactor = 1.0;
                bRealTime = true;
            }

            // get the current state of the venue
            string sXML = null;
            if (dtSOD < dtStart)
                FetchInitialState(sVenue, dtSOD, dtStart, dtEnd);

            // start the first data fetch
            DateTime dtCurrent = dtStart;
            DateTime dtStop = dtCurrent + TimeSpan.FromMilliseconds(cmsecPeriod);
            if (dtStop > DateTime.UtcNow)
            {
                // switch to real time
                dTimeFactor = 1.0;
                dtStop = DateTime.UtcNow;
                cmsecPeriod = 1000;
                bRealTime = true;
            }

            sXML = null;
            try
            {
                string sCurrent = formatDate(dtCurrent);
                string sStop = formatDate(dtStop);
                string sURL = string.Format("/xi/dashboard/jms/venue/{0}/messages?from={1}&to={2}", wa.sVenue, sCurrent, sStop);
                StartDataFetch(sURL);
            }
            catch (Exception ex)
            {
                callback.LogError("HTTP error: " + ex.Message);
                return;
            }


            // set request time cursor to iterate from start to end
            while (true)
            {
                // are we being told to quit?
                if (bStopWorkers)
                    return;

                // wait for our data to arrive
                sXML = WaitForData();
                if (bEnterRealTime)
                    bRealTime = true;

                // process the data
                if (sXML != null)
                {
                    try
                    {
                        StringReader sr = new StringReader(sXML);
                        XmlDocument xd = new XmlDocument();
                        xd.Load(sr);

                        XmlNode xn = xd.DocumentElement;
                        if (xn.NodeType != XmlNodeType.Element || xn.Name != "venue")
                            throw new Exception("Missing <venue> tag");

                        // get the time attribute out of the envelope
                        string sTimeAttr = xn.Attributes["time"].Value;

                        // start the next data fetch

                        // advance cursor. Note that for real-time fetches, sTimeAttr always contains
                        // the timestamp of the last successfully fetched event.
                        if (bRealTime)
                            dtCurrent = parseDate(sTimeAttr);
                        else
                            dtCurrent = dtStop;

                        if (dtCurrent < dtEnd)
                        {
                            dtStop = dtCurrent + TimeSpan.FromMilliseconds(cmsecPeriod);

                            // switch to real-time if necessary
                            if ( !bRealTime && (dtStop>DateTime.UtcNow) )
                            {
                                // switch to real time
                                dTimeFactor = 1.0;
                                cmsecPeriod = 1000;
                                dtStop = DateTime.UtcNow;

                                // set flag to switch to real time when the next data arrive
                                bEnterRealTime = true;
                            }

                            // start the fetch
                            try
                            {
                                string sCurrent = formatDate(dtCurrent);
                                string sStop = formatDate(dtStop);
                                string sURL = string.Format("/xi/dashboard/jms/venue/{0}/messages?from={1}&to={2}", wa.sVenue, sCurrent, sStop);
                                StartDataFetch(sURL);
                            }
                            catch (Exception ex)
                            {
                                callback.LogError("HTTP error: " + ex.Message);
                            }
                        }

                        // now, process the children
                        foreach (XmlNode xnc in xn.ChildNodes)
                            processMessage(xnc);
                    }
                    catch (Exception ex)
                    {
                        callback.LogError("Error during message processing: " + ex.Message);
                    }

                    // now, in a "timely" fashion, add the incoming messages to the outgoing list
                    foreach (GuestAction ga in liReceived)
                    {
                        try
                        {
                            // see if it's time to send out the message
                            if (!bRealTime)
                            {
                                TimeSpan tsClock = DateTime.Now - dtBegin;
                                TimeSpan tsMessage = ga.TimeStamp - dtStart;
                                // Console.WriteLine("Comparing a clock delta of " + tsClock + " a message delta of " + tsMessage + " with a time factor of " + dTimeFactor);
                                if (tsMessage.TotalMilliseconds > tsClock.TotalMilliseconds * dTimeFactor)
                                {
                                    // no, wait 
                                    int cmsecWait = (int)((tsMessage.TotalMilliseconds / dTimeFactor) - tsClock.TotalMilliseconds);

                                    // Console.WriteLine("Waiting for " + cmsecWait / 1000.0 + " seconds");
                                    Thread.Sleep(cmsecWait);
                                }
                            }

                            //  add the message
                            lock (liActions)
                            {
                                liActions.Add(ga);
                            }
                        }
                        catch (Exception ex)
                        {
                            callback.LogError("Error processing message: " + ga.TimeStamp + ": " + ex.Message);
                        }
                    }

                    // clear the receive list
                    liReceived.Clear();
                }
                else
                {
                    callback.LogError("Error processing request: " + sDataFetcherUrl);
                }

                // is it time to quit
                if (dtCurrent >= dtEnd)
                {
                    // yes - queue up a "done" event and then quit
                    lock(liActions)
                    {
                        GuestAction ga = new GuestAction();
                        ga.SetTimeStamp(formatDate(dtCurrent));
                        ga.Action = GuestAction.ActionType.Done;
                        liActions.Add(ga);
                    }

                    // set the quit flag, signal the data fetcher and let threads die naturally
                    bStopWorkers = true;
                    SignalDataFetch();
                }
            }
        }

        private void FetchInitialState(string sVenue, DateTime dtSOD, DateTime dtStart, DateTime dtEnd)
        {
            string sXML = null;
            try
            {
                // compose state url
                string sSOD = formatDate(dtSOD);
                string sStart = formatDate(dtStart);
                string sEnd = formatDate(dtEnd);

                string sURL = string.Format("/xi/dashboard/jms/venue/{0}/gueststate?from={1}&to={2}", sVenue, sSOD, sStart);
                StartDataFetch(sURL);
                sXML = WaitForData();

                if (sXML == null)
                {
                    callback.LogError("No results when getting venue state");
                    return;
                }

                // deserialize
                StringReader sr = new StringReader(sXML);
                XmlDocument xd = new XmlDocument();
                xd.Load(sr);

                XmlNode xn = xd.DocumentElement;
                if (xn.NodeType != XmlNodeType.Element || xn.Name != "venue")
                    throw new Exception("Missing <venue> tag");

                // now, process the children
                List<string> liSeen = new List<string>();
                foreach (XmlNode xnc in xn.ChildNodes)
                {
                    GuestState gs = processGuestState(xnc);

                    // add but only if we haven't seen the guest before
                    if (!liSeen.Contains(gs.GuestId))
                    {
                        GuestAction ga = new GuestAction();
                        ga.Action = GuestAction.ActionType.Add;
                        ga.GuestId = gs.GuestId;
                        ga.SetTimeStamp(gs.Location.Arrived);
                        ga.xPass = gs.XPass;

                        switch (gs.State)
                        {
                            case "HASENTERED":
                                ga.Location = "Entry";
                                break;

                            case "HASMERGED":
                                ga.Location = "Merge";
                                break;

                            case "EXITED":
                                ga.Location = "Exit";
                                break;

                            case "LOADING":
                            case "RIDING":
                            default:
                                ga.Location = "Load";
                                break;

                        }
                        lock (liInitial)
                        {
                            liInitial.Add(ga);
                        }

                        // add to the seen list
                        liSeen.Add(gs.GuestId);
                    }

                }
            }
            catch (Exception ex)
            {
                callback.LogError("HTTP error: " + ex.Message);
            }
            finally
            {
                lock (liInitial)
                {
                    GuestAction ga = new GuestAction();
                    ga.SetTimeStamp(formatDate(DateTime.UtcNow));
                    ga.Action = GuestAction.ActionType.Done;
                    liInitial.Add(ga);
                }
            }

        }

        /*
         * Message processing helper functions
         */

        private void processMessage(XmlNode xn)
        {
            // inspect the "type" attribute and process as needed
            string sMsgType = xn.Attributes["type"].InnerText;
            switch (sMsgType)
            {
                case "ENTRY":
                    {
                        processEntryMessage(xn);
                        break;
                    }

                case "MERGE":
                    {
                        processMergeMessage(xn);
                        break;
                    }

                case "LOAD":
                    {
                        processLoadMessage(xn);
                        break;
                    }

                case "EXIT":
                    {
                        processExitMessage(xn);
                        break;
                    }

                case "ABANDON":
                    {
                        processAbandonMessage(xn);
                        break;
                    }

                default:
                    {
                        break;
                    }
            }
        }

        private void processEntryMessage(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(SimpleMessageItem));
            StringReader sr = new StringReader(xn.OuterXml);
            SimpleMessageItem sim = xs.Deserialize(sr) as SimpleMessageItem;
            GuestAction ga = new GuestAction();
            ga.Action = GuestAction.ActionType.Add;
            ga.GuestId = sim.GuestID;
            ga.SetTimeStamp(sim.Timestamp);
            ga.Location = "Entry";
            ga.xPass = sim.xPass;
            liReceived.Add(ga);
        }

        private void processMergeMessage(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(SimpleMessageItem));
            StringReader sr = new StringReader(xn.OuterXml);
            SimpleMessageItem sim = xs.Deserialize(sr) as SimpleMessageItem;
            GuestAction ga = new GuestAction();
            ga.Action = GuestAction.ActionType.Move;
            ga.GuestId = sim.GuestID;
            ga.SetTimeStamp(sim.Timestamp);
            ga.Location = "Merge";
            ga.xPass = sim.xPass;
            liReceived.Add(ga);
        }

        private void processLoadMessage(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(LoadMessageItem));
            StringReader sr = new StringReader(xn.OuterXml);
            LoadMessageItem sim = xs.Deserialize(sr) as LoadMessageItem;
            GuestAction ga = new GuestAction();
            ga.Action = GuestAction.ActionType.Move;
            ga.GuestId = sim.GuestID;
            ga.SetTimeStamp(sim.Timestamp);
            ga.Location = "Load";
            ga.xPass = sim.xPass;
            liReceived.Add(ga);
        }

        private void processExitMessage(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(ExitMessageItem));
            StringReader sr = new StringReader(xn.OuterXml);
            ExitMessageItem sim = xs.Deserialize(sr) as ExitMessageItem;
            GuestAction ga = new GuestAction();
            ga.Action = GuestAction.ActionType.Move;
            ga.GuestId = sim.GuestID;
            ga.SetTimeStamp(sim.Timestamp);
            ga.Location = "Exit";
            ga.xPass = sim.xPass;
            liReceived.Add(ga);
        }

        private void processAbandonMessage(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(AbandonMessageItem));
            StringReader sr = new StringReader(xn.OuterXml);
            AbandonMessageItem sim = xs.Deserialize(sr) as AbandonMessageItem;
            GuestAction ga = new GuestAction();
            ga.Action = GuestAction.ActionType.Abandon;
            ga.GuestId = sim.GuestID;
            ga.SetTimeStamp(sim.Timestamp);
            ga.Location = "";
            ga.xPass = sim.xPass;
            liReceived.Add(ga);
        }

        private GuestState processGuestState(XmlNode xn)
        {
            XmlSerializer xs = new XmlSerializer(typeof(GuestState));
            StringReader sr = new StringReader(xn.OuterXml);
            return xs.Deserialize(sr) as GuestState;
        }

        private string formatDate(DateTime dateTime)
        {
            return dateTime.ToString("yyyy-MM-dd HH:mm:ss.fff");
        }

        private DateTime parseDate(string sDate)
        {
            return DateTime.ParseExact(sDate, "yyyy-MM-dd'T'HH:mm:ss.fff", CultureInfo.CurrentCulture);
        }

    }
}
