using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Diagnostics;

namespace Disney.xBand.Simulator.Listeners
{
    public class EventsListener : ReaderHttpListener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public EventsListener(Dto.Reader reader, Dto.Repositories.IReaderRepository readerRepository)
            : base(reader, readerRepository, "events")
        {
        }

        protected override string ProcessRequest(HttpListenerRequest httpListenerRequest)
        {
            string json = String.Empty;

            try
            {
                string bandID = httpListenerRequest.QueryString["XLRID"];
                string since = httpListenerRequest.QueryString["since"];
                string max = httpListenerRequest.QueryString["max"];

                int sinceEventNumber = 0;
                int maximumEvents = 0;


                if (String.Compare(this.Reader.ReaderType.ReaderTypeName, "Tap", true) == 0)
                {
                    Dto.TapReaderEvents events = this.Repository.GetTapReaderEvents(this.Reader.ReaderID, bandID, sinceEventNumber, maximumEvents);

                    json = ToJson(typeof(Dto.TapReaderEvents), events);
                }
                else
                {
                    Dto.LongRangeReaderEvents events = this.Repository.GetLongRangeReaderEvents(this.Reader.ReaderID, bandID, sinceEventNumber, maximumEvents);

                    json = ToJson(typeof(Dto.LongRangeReaderEvents), events);
                }

            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                log.Error(String.Format("Error sending events for reader for reader {0}", this.Reader.ReaderName), ex);
            }

            return json;
        }
    }
}
