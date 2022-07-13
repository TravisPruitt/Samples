using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Net;
using System.Diagnostics;
using Disney.xBand.Simulator.Listeners;
using Disney.xBand.Simulator.Dto.Repositories;

namespace Disney.xBand.Simulator.Publishers
{
    public class EventPublisher : Publisher
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private static readonly object mutex = new object();

        public EventPublisher(Dto.Reader reader, string controllerUrl, IReaderRepository readerRepository)
            : base(reader, controllerUrl, readerRepository, 1000)
        {
        }

        protected override void TimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            SendEventData();
        }

        public void StreamInfoUpdated(object sender, StreamInfoUpdatedEventArgs e)
        {
            this.Timer.Interval = e.EventsInterval;
            this.Reader.UpstreamUrl = e.Url;
            this.Reader.EventsInterval = e.EventsInterval;
            this.Reader.MaximumEvents = e.MaximumEvents;
        }

        private void SendEventData()
        {
            lock (mutex)
            {
                try
                {
                    string bandID = String.Empty;
                    DataContractJsonSerializer s = null;
                    MemoryStream ms = new MemoryStream();

                    int eventCount = 0;

                    long lastEventNumber = ReaderRepository.GetLastUpstreamEvent(this.Reader.ReaderID);

                    if (String.Compare(this.Reader.ReaderType.ReaderTypeName, "Tap", true) == 0)
                    {
                        Dto.TapReaderEvents events = this.ReaderRepository.GetTapReaderEvents(this.Reader.ReaderID, null, lastEventNumber, -1);

                        eventCount = events.ReaderEvents.Length;

                        if (eventCount > 0)
                        {
                            s = new DataContractJsonSerializer(typeof(Dto.TapReaderEvents));
                            s.WriteObject(ms, events);

                            lastEventNumber = (from re in events.ReaderEvents select re.EventNumber).Max();
                        }

                    }
                    else
                    {
                        Dto.LongRangeReaderEvents events = this.ReaderRepository.GetLongRangeReaderEvents(this.Reader.ReaderID, null, lastEventNumber, -1);

                        eventCount = events.ReaderEvents.Length;

                        if (eventCount > 0)
                        {
                            s = new DataContractJsonSerializer(typeof(Dto.LongRangeReaderEvents));
                            s.WriteObject(ms, events);

                            lastEventNumber = (from re in events.ReaderEvents select re.EventNumber).Max();
                        }
                    }

                    if (eventCount > 0)
                    {

                        string json = Encoding.Default.GetString(ms.ToArray());
                        ms.Close();

                        Uri uri = new Uri(this.Reader.UpstreamUrl);

                        byte[] content = Encoding.UTF8.GetBytes(json);

                        HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(uri);
                        request.Method = "PUT";

                        request.ContentType = "application/json";
                        request.ContentLength = content.Length;
                        request.Timeout = 1000;

                        try
                        {
                            Stream requestStream = request.GetRequestStream();
                            requestStream.Write(content, 0, content.Length);
                            requestStream.Close();

                            HttpWebResponse resp = (HttpWebResponse)request.GetResponse();

                            if (resp.StatusCode == HttpStatusCode.OK)
                            {
                                ReaderRepository.SetLastUpstreamEvent(this.Reader.ReaderID, lastEventNumber);
                            }

                            resp.Close();
                        }
                        catch (WebException e)
                        {
                            log.Error(String.Format("Send events message to {0} from {1} failed with status code: {2}.",
                                this.ControllerUrl,
                                String.Format("Reader {0} Port {1}", this.Reader.ReaderName, this.Reader.WebPort),
                                e.Status));
                        }
                    }
                }

                catch (Exception ex)
                {
                    Debug.WriteLine(ex.Message);
                    Debug.WriteLine(ex.StackTrace);
                    log.Error("Unexpected Error sending to controller", ex);
                }
            }
        }

    }
}
